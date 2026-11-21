package com.minhduc.careerconnect.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.minhduc.careerconnect.dto.UserSearchFilter;

@Repository
public class UserRepository {

    /** Thông tin tối thiểu để kiểm tra mật khẩu. */
    public record UserCredential(long id, String email, String passwordHash) {
    }

    /** Một dòng trong bảng quản trị tài khoản. */
    public record UserRow(long id, String fullName, String email, String role, String companyName,
                          boolean locked, String lockedReason, LocalDateTime createdAt) {
    }

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean existsByEmail(String email) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE email = ?", Integer.class, email);
        return count != null && count > 0;
    }

    public Optional<UserCredential> findCredentialByEmail(String email) {
        List<UserCredential> rows = jdbcTemplate.query(
                "SELECT id, email, password_hash FROM users WHERE email = ?",
                (rs, i) -> new UserCredential(rs.getLong("id"), rs.getString("email"), rs.getString("password_hash")),
                email);
        return rows.stream().findFirst();
    }

    /** Thêm tài khoản mới, trả về id vừa tạo. companyId có thể null. */
    public long insert(String email, String passwordHash, String fullName, String role, Long companyId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO users (email, password_hash, full_name, role, company_id) VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, email);
            ps.setString(2, passwordHash);
            ps.setString(3, fullName);
            ps.setString(4, role);
            ps.setObject(5, companyId);
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    /** Giữ cho code đăng ký sinh viên cũ chạy như trước. */
    public long insert(String email, String passwordHash, String fullName, String role) {
        return insert(email, passwordHash, fullName, role, null);
    }

    public void updatePassword(long userId, String passwordHash) {
        jdbcTemplate.update("UPDATE users SET password_hash = ? WHERE id = ?", passwordHash, userId);
    }

    // ------------------------------------------------------------------ quản trị tài khoản

    public Optional<UserRow> findRowById(long id) {
        List<UserRow> rows = jdbcTemplate.query(SELECT_ROW + " WHERE u.id = ?", this::mapRow, id);
        return rows.stream().findFirst();
    }

    public List<UserRow> search(UserSearchFilter filter, int offset, int limit) {
        List<Object> params = new ArrayList<>();
        String where = buildWhere(filter, params);
        params.add(limit);
        params.add(offset);
        return jdbcTemplate.query(SELECT_ROW + where + " ORDER BY u.created_at DESC, u.id DESC LIMIT ? OFFSET ?",
                this::mapRow, params.toArray());
    }

    public long count(UserSearchFilter filter) {
        List<Object> params = new ArrayList<>();
        String where = buildWhere(filter, params);
        Long total = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users u" + where, Long.class, params.toArray());
        return total == null ? 0 : total;
    }

    public void lock(long userId, String reason) {
        jdbcTemplate.update("UPDATE users SET is_locked = TRUE, locked_reason = ? WHERE id = ?", reason, userId);
    }

    public void unlock(long userId) {
        jdbcTemplate.update("UPDATE users SET is_locked = FALSE, locked_reason = NULL WHERE id = ?", userId);
    }

    private static final String SELECT_ROW =
            "SELECT u.id, u.full_name, u.email, u.role, c.name AS company_name, u.is_locked, u.locked_reason, u.created_at "
                    + "FROM users u LEFT JOIN companies c ON c.id = u.company_id";

    /** Tạo mệnh đề WHERE theo bộ lọc; giá trị đưa vào params (không nối chuỗi trực tiếp, tránh SQL injection). */
    private String buildWhere(UserSearchFilter filter, List<Object> params) {
        List<String> conditions = new ArrayList<>();
        if (filter.getQ() != null && !filter.getQ().isBlank()) {
            conditions.add("(u.full_name LIKE ? OR u.email LIKE ?)");
            String like = "%" + filter.getQ().trim() + "%";
            params.add(like);
            params.add(like);
        }
        if (filter.getRole() != null && !filter.getRole().isBlank()) {
            conditions.add("u.role = ?");
            params.add(filter.getRole());
        }
        if ("ACTIVE".equals(filter.getStatus())) {
            conditions.add("u.is_locked = FALSE");
        } else if ("LOCKED".equals(filter.getStatus())) {
            conditions.add("u.is_locked = TRUE");
        }
        return conditions.isEmpty() ? "" : " WHERE " + String.join(" AND ", conditions);
    }

    private UserRow mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        return new UserRow(rs.getLong("id"), rs.getString("full_name"), rs.getString("email"),
                rs.getString("role"), rs.getString("company_name"), rs.getBoolean("is_locked"),
                rs.getString("locked_reason"), rs.getTimestamp("created_at").toLocalDateTime());
    }
}
