-- =====================================================================
-- V6 (task T4E7968): thêm ngày sinh và nơi làm việc mong muốn cho hồ sơ
-- =====================================================================
ALTER TABLE profiles
    ADD COLUMN date_of_birth DATE NULL AFTER phone,
    ADD COLUMN desired_work_location VARCHAR(100) NULL AFTER desired_position;
