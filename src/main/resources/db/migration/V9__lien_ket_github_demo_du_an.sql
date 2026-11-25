-- =====================================================================
-- V9 (task T4E796C): dự án có link GitHub và link demo riêng
-- =====================================================================
ALTER TABLE projects RENAME COLUMN url TO github_url;

ALTER TABLE projects
    ADD COLUMN demo_url VARCHAR(500) NULL AFTER github_url;
