-- =====================================================================
-- V7 (task T4E796A): danh mục kỹ năng
--  - Thêm cột is_hidden: kỹ năng bị ẩn không cho chọn mới, sinh viên đang dùng vẫn giữ.
--  - Chuẩn hoá nhóm kỹ năng về 5 nhóm (lưu mã, hiển thị tiếng Việt trong code):
--    LANGUAGE = Ngôn ngữ, FRAMEWORK = Framework, DATABASE = CSDL,
--    TOOL = Công cụ, SOFT_SKILL = Kỹ năng mềm
-- =====================================================================
ALTER TABLE skills ADD COLUMN is_hidden BOOLEAN NOT NULL DEFAULT FALSE;

-- Một số kỹ năng mẫu cần xếp theo tên
UPDATE skills SET category = 'LANGUAGE'  WHERE name IN ('HTML/CSS');
UPDATE skills SET category = 'FRAMEWORK' WHERE name IN ('ReactJS', 'Vue.js', 'Angular', 'Node.js');

-- Còn lại xếp theo nhóm cũ
UPDATE skills SET category = CASE category
        WHEN 'Lập trình'      THEN 'LANGUAGE'
        WHEN 'Framework'      THEN 'FRAMEWORK'
        WHEN 'Cơ sở dữ liệu'  THEN 'DATABASE'
        WHEN 'Kỹ năng mềm'    THEN 'SOFT_SKILL'
        WHEN 'Ngoại ngữ'      THEN 'SOFT_SKILL'
        WHEN 'Nghiệp vụ'      THEN 'SOFT_SKILL'
        ELSE 'TOOL'
    END
WHERE category NOT IN ('LANGUAGE', 'FRAMEWORK', 'DATABASE', 'TOOL', 'SOFT_SKILL') OR category IS NULL;

ALTER TABLE skills
    MODIFY category VARCHAR(20) NOT NULL,
    ADD CONSTRAINT ck_skills_category CHECK (category IN ('LANGUAGE', 'FRAMEWORK', 'DATABASE', 'TOOL', 'SOFT_SKILL'));
