-- =====================================================================
-- V8 (task T4E796A): sửa V7 - nhóm "Framework" (chữ thường) chưa được đổi
-- thành "FRAMEWORK" vì cột dùng collation không phân biệt hoa thường.
-- Đưa mọi mã nhóm về chữ in hoa.
-- =====================================================================
UPDATE skills SET category = UPPER(category);