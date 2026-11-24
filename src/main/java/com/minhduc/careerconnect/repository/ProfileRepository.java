package com.minhduc.careerconnect.repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ProfileRepository {

    /** Thông tin hiển thị ở trang "Hồ sơ của tôi". */
    public record MyProfile(long userId, long profileId, String email, String fullName, LocalDate dateOfBirth,
                            String phone, String summary, String desiredPosition, String desiredWorkLocation,
                            String status, boolean isPublic, String slug) {
    }

    private final JdbcTemplate jdbcTemplate;

    public ProfileRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /** Tạo hồ sơ trống: trạng thái nháp, riêng tư (không công khai). */
    public void insertEmptyProfile(long userId, String slug) {
        jdbcTemplate.update(
                "INSERT INTO profiles (user_id, slug, status, is_public) VALUES (?, ?, 'DRAFT', FALSE)",
                userId, slug);
    }

    /** Tìm hồ sơ theo email chủ hồ sơ (người đang đăng nhập). */
    public Optional<MyProfile> findByOwnerEmail(String email) {
        List<MyProfile> rows = jdbcTemplate.query(
                "SELECT u.id AS user_id, p.id AS profile_id, u.email, u.full_name, p.date_of_birth, p.phone, "
                        + "p.summary, p.desired_position, p.desired_work_location, p.status, p.is_public, p.slug "
                        + "FROM profiles p JOIN users u ON u.id = p.user_id WHERE u.email = ?",
                (rs, i) -> {
                    Date dob = rs.getDate("date_of_birth");
                    return new MyProfile(rs.getLong("user_id"), rs.getLong("profile_id"), rs.getString("email"),
                            rs.getString("full_name"), dob == null ? null : dob.toLocalDate(), rs.getString("phone"),
                            rs.getString("summary"), rs.getString("desired_position"),
                            rs.getString("desired_work_location"), rs.getString("status"),
                            rs.getBoolean("is_public"), rs.getString("slug"));
                },
                email);
        return rows.stream().findFirst();
    }

    /** Cập nhật thông tin cơ bản của hồ sơ. */
    public void updateBasicInfo(long profileId, LocalDate dateOfBirth, String phone, String summary,
                                String desiredPosition, String desiredWorkLocation) {
        jdbcTemplate.update(
                "UPDATE profiles SET date_of_birth = ?, phone = ?, summary = ?, desired_position = ?, "
                        + "desired_work_location = ? WHERE id = ?",
                dateOfBirth == null ? null : Date.valueOf(dateOfBirth), phone, summary, desiredPosition,
                desiredWorkLocation, profileId);
    }

    /** Họ tên nằm ở bảng users. */
    public void updateOwnerFullName(long userId, String fullName) {
        jdbcTemplate.update("UPDATE users SET full_name = ? WHERE id = ?", fullName, userId);
    }
}
