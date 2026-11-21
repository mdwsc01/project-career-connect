-- =====================================================================
-- V4 (task T4E7966): bảng lưu mã OTP đặt lại mật khẩu
-- Mã được lưu dạng BCrypt (không lưu mã gốc), hết hạn sau 10 phút,
-- dùng một lần, nhập sai 5 lần thì bị huỷ.
-- =====================================================================
CREATE TABLE password_reset_codes
(
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    user_id    BIGINT       NOT NULL,
    code_hash  VARCHAR(100) NOT NULL,
    expires_at DATETIME     NOT NULL,
    attempts   INT          NOT NULL DEFAULT 0,
    status     VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    used_at    DATETIME,
    PRIMARY KEY (id),
    CONSTRAINT fk_reset_codes_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT ck_reset_codes_status CHECK (status IN ('ACTIVE', 'USED', 'CANCELLED'))
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE INDEX idx_reset_codes_user_status ON password_reset_codes (user_id, status);
