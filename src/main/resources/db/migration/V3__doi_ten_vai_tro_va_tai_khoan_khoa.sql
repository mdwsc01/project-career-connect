-- =====================================================================
-- V3 (task T4E7964): đổi tên vai trò theo yêu cầu phân quyền
--   CANDIDATE -> STUDENT, EMPLOYER -> PARTNER, MENTOR -> ADMIN
-- và thêm 1 tài khoản bị khoá để kiểm thử đăng nhập.
-- =====================================================================

ALTER TABLE users DROP CHECK ck_users_role;

UPDATE users SET role = 'STUDENT' WHERE role = 'CANDIDATE';
UPDATE users SET role = 'PARTNER' WHERE role = 'EMPLOYER';
UPDATE users SET role = 'ADMIN'   WHERE role = 'MENTOR';

ALTER TABLE users
    ADD CONSTRAINT ck_users_role CHECK (role IN ('STUDENT', 'PARTNER', 'ADMIN'));

-- Tài khoản giả bị khoá (mật khẩu Demo@1234)
INSERT INTO users (email, password_hash, full_name, role, is_locked, locked_reason)
VALUES ('sv.bikhoa@example.com', '$2a$10$YP1YQNXK.BfbByuH1h/ydeNClI1gHr0RmsQD2dH6rodoMwNTYbZUa',
        'Tài khoản Bị Khoá (demo)', 'STUDENT', TRUE,
        'Vi phạm quy định về nội dung hồ sơ (tài khoản thử nghiệm)');
