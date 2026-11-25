-- =====================================================================
-- V10 (task T4E796D): lưu thông tin file minh chứng của chứng chỉ.
-- File nằm trong thư mục upload (cấu hình app.upload.dir), DB chỉ lưu tên file
-- sinh ngẫu nhiên + tên gốc + loại file + dung lượng.
-- =====================================================================
ALTER TABLE certificates
    ADD COLUMN file_stored_name   VARCHAR(100) NULL,
    ADD COLUMN file_original_name VARCHAR(255) NULL,
    ADD COLUMN file_content_type  VARCHAR(100) NULL,
    ADD COLUMN file_size          BIGINT       NULL;
