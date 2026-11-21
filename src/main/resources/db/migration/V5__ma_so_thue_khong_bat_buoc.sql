-- =====================================================================
-- V5 (task T4E7967): admin tạo tài khoản đối tác chỉ cần tên công ty,
-- nên mã số thuế không còn bắt buộc. Ràng buộc UNIQUE vẫn giữ
-- (MySQL cho phép nhiều dòng NULL trong cột UNIQUE).
-- =====================================================================
ALTER TABLE companies MODIFY tax_code VARCHAR(20) NULL;
