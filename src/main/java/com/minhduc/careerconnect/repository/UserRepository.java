package com.minhduc.careerconnect.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    /** Thông tin tối thiểu để kiểm tra mật khẩu. */
    public record UserCredential(long id, String email, String passwordHash) {
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

    /** Thêm tài khoản mới, trả về id vừa tạo. */
    public long insert(String email, String passwordHash, String fullName, String role) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO users (email, password_hash, full_name, role) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, email);
            ps.setString(2, passwordHash);
            ps.setString(3, fullName);
            ps.setString(4, role);
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public void updatePassword(long userId, String passwordHash) {
        jdbcTemplate.update("UPDATE users SET password_hash = ? WHERE id = ?", passwordHash, userId);
    }
}
