package com.minhduc.careerconnect.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ProfileRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProfileRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Tạo hồ sơ trống: trạng thái nháp, riêng tư (không công khai).
     */
    public void insertEmptyProfile(long userId, String slug) {
        jdbcTemplate.update(
                "INSERT INTO profiles (user_id, slug, status, is_public) VALUES (?, ?, 'DRAFT', FALSE)",
                userId, slug);
    }
}
