package com.minhduc.careerconnect.repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PasswordResetCodeRepository {

    public record ResetCode(long id, String codeHash, LocalDateTime expiresAt, int attempts) {
    }

    private final JdbcTemplate jdbcTemplate;

    public PasswordResetCodeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Huỷ mọi mã còn hiệu lực của người dùng (khi xin mã mới).
     */
    public void cancelActiveCodes(long userId) {
        jdbcTemplate.update(
                "UPDATE password_reset_codes SET status = 'CANCELLED' WHERE user_id = ? AND status = 'ACTIVE'",
                userId);
    }

    public void insert(long userId, String codeHash, LocalDateTime expiresAt) {
        jdbcTemplate.update(
                "INSERT INTO password_reset_codes (user_id, code_hash, expires_at) VALUES (?, ?, ?)",
                userId, codeHash, Timestamp.valueOf(expiresAt));
    }

    /**
     * Mã mới nhất còn trạng thái ACTIVE.
     */
    public Optional<ResetCode> findLatestActive(long userId) {
        List<ResetCode> rows = jdbcTemplate.query(
                "SELECT id, code_hash, expires_at, attempts FROM password_reset_codes "
                        + "WHERE user_id = ? AND status = 'ACTIVE' ORDER BY id DESC LIMIT 1",
                (rs, i) -> new ResetCode(rs.getLong("id"), rs.getString("code_hash"),
                        rs.getTimestamp("expires_at").toLocalDateTime(), rs.getInt("attempts")),
                userId);
        return rows.stream().findFirst();
    }

    public void updateAttempts(long codeId, int attempts) {
        jdbcTemplate.update("UPDATE password_reset_codes SET attempts = ? WHERE id = ?", attempts, codeId);
    }

    public void markCancelled(long codeId) {
        jdbcTemplate.update("UPDATE password_reset_codes SET status = 'CANCELLED' WHERE id = ?", codeId);
    }

    public void markUsed(long codeId) {
        jdbcTemplate.update(
                "UPDATE password_reset_codes SET status = 'USED', used_at = ? WHERE id = ?",
                Timestamp.valueOf(LocalDateTime.now()), codeId);
    }
}
