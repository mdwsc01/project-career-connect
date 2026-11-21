-- =====================================================================
-- V1: Tạo toàn bộ bảng theo ERD (docs/database-design.md) - task T4E7961
-- MySQL 8, InnoDB, utf8mb4
-- =====================================================================

CREATE TABLE companies
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(200) NOT NULL,
    tax_code    VARCHAR(20)  NOT NULL,
    website     VARCHAR(255),
    industry    VARCHAR(100),
    size_range  VARCHAR(50),
    address     VARCHAR(255),
    description TEXT,
    logo_url    VARCHAR(500),
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uk_companies_tax_code UNIQUE (tax_code)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE users
(
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    email         VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name     VARCHAR(100) NOT NULL,
    role          VARCHAR(20)  NOT NULL,
    is_locked     BOOLEAN      NOT NULL DEFAULT FALSE,
    locked_reason VARCHAR(255),
    company_id    BIGINT,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT fk_users_company FOREIGN KEY (company_id) REFERENCES companies (id) ON DELETE SET NULL,
    CONSTRAINT ck_users_role CHECK (role IN ('CANDIDATE', 'EMPLOYER', 'MENTOR', 'ADMIN'))
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE INDEX idx_users_role_locked ON users (role, is_locked);

CREATE TABLE profiles
(
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    user_id          BIGINT       NOT NULL,
    slug             VARCHAR(120) NOT NULL,
    headline         VARCHAR(200),
    summary          TEXT,
    phone            VARCHAR(20),
    location         VARCHAR(100),
    avatar_url       VARCHAR(500),
    desired_position VARCHAR(150),
    years_experience INT          NOT NULL DEFAULT 0,
    status           VARCHAR(20)  NOT NULL DEFAULT 'DRAFT',
    is_public        BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uk_profiles_user UNIQUE (user_id),
    CONSTRAINT uk_profiles_slug UNIQUE (slug),
    CONSTRAINT fk_profiles_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT ck_profiles_status CHECK (status IN ('DRAFT', 'PENDING', 'APPROVED', 'REJECTED'))
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE INDEX idx_profiles_status_public ON profiles (status, is_public);

CREATE TABLE educations
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    profile_id  BIGINT       NOT NULL,
    school      VARCHAR(200) NOT NULL,
    degree      VARCHAR(100),
    major       VARCHAR(150),
    start_date  DATE,
    end_date    DATE,
    gpa         DECIMAL(3, 2),
    description TEXT,
    PRIMARY KEY (id),
    CONSTRAINT fk_educations_profile FOREIGN KEY (profile_id) REFERENCES profiles (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE skills
(
    id       BIGINT       NOT NULL AUTO_INCREMENT,
    name     VARCHAR(100) NOT NULL,
    category VARCHAR(50),
    PRIMARY KEY (id),
    CONSTRAINT uk_skills_name UNIQUE (name)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE profile_skills
(
    profile_id BIGINT  NOT NULL,
    skill_id   BIGINT  NOT NULL,
    level      TINYINT NOT NULL,
    PRIMARY KEY (profile_id, skill_id),
    CONSTRAINT fk_profile_skills_profile FOREIGN KEY (profile_id) REFERENCES profiles (id) ON DELETE CASCADE,
    CONSTRAINT fk_profile_skills_skill FOREIGN KEY (skill_id) REFERENCES skills (id) ON DELETE RESTRICT,
    CONSTRAINT ck_profile_skills_level CHECK (level BETWEEN 1 AND 5)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE INDEX idx_profile_skills_skill_level ON profile_skills (skill_id, level);

CREATE TABLE projects
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    profile_id  BIGINT       NOT NULL,
    name        VARCHAR(200) NOT NULL,
    role        VARCHAR(100),
    description TEXT,
    tech_stack  VARCHAR(255),
    url         VARCHAR(500),
    start_date  DATE,
    end_date    DATE,
    PRIMARY KEY (id),
    CONSTRAINT fk_projects_profile FOREIGN KEY (profile_id) REFERENCES profiles (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE certificates
(
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    profile_id     BIGINT       NOT NULL,
    name           VARCHAR(200) NOT NULL,
    issuer         VARCHAR(150),
    issued_date    DATE,
    expiry_date    DATE,
    credential_url VARCHAR(500),
    PRIMARY KEY (id),
    CONSTRAINT fk_certificates_profile FOREIGN KEY (profile_id) REFERENCES profiles (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE shortlists
(
    id         BIGINT   NOT NULL AUTO_INCREMENT,
    company_id BIGINT   NOT NULL,
    profile_id BIGINT   NOT NULL,
    created_by BIGINT   NOT NULL,
    note       VARCHAR(500),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uk_shortlists_company_profile UNIQUE (company_id, profile_id),
    CONSTRAINT fk_shortlists_company FOREIGN KEY (company_id) REFERENCES companies (id) ON DELETE CASCADE,
    CONSTRAINT fk_shortlists_profile FOREIGN KEY (profile_id) REFERENCES profiles (id) ON DELETE CASCADE,
    CONSTRAINT fk_shortlists_created_by FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE interview_requests
(
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    company_id       BIGINT       NOT NULL,
    profile_id       BIGINT       NOT NULL,
    requested_by     BIGINT       NOT NULL,
    position_title   VARCHAR(150) NOT NULL,
    message          TEXT,
    proposed_time    DATETIME     NOT NULL,
    mode             VARCHAR(20)  NOT NULL,
    location_or_link VARCHAR(255),
    status           VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    responded_at     DATETIME,
    created_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_interviews_company FOREIGN KEY (company_id) REFERENCES companies (id) ON DELETE RESTRICT,
    CONSTRAINT fk_interviews_profile FOREIGN KEY (profile_id) REFERENCES profiles (id) ON DELETE RESTRICT,
    CONSTRAINT fk_interviews_requested_by FOREIGN KEY (requested_by) REFERENCES users (id) ON DELETE RESTRICT,
    CONSTRAINT ck_interviews_mode CHECK (mode IN ('ONLINE', 'OFFLINE')),
    CONSTRAINT ck_interviews_status CHECK (status IN ('PENDING', 'ACCEPTED', 'DECLINED', 'CANCELLED'))
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE INDEX idx_interviews_profile_status ON interview_requests (profile_id, status);
CREATE INDEX idx_interviews_company_status ON interview_requests (company_id, status);

CREATE TABLE profile_reviews
(
    id          BIGINT      NOT NULL AUTO_INCREMENT,
    profile_id  BIGINT      NOT NULL,
    reviewer_id BIGINT      NOT NULL,
    result      VARCHAR(20) NOT NULL,
    comment     TEXT,
    created_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_reviews_profile FOREIGN KEY (profile_id) REFERENCES profiles (id) ON DELETE CASCADE,
    CONSTRAINT fk_reviews_reviewer FOREIGN KEY (reviewer_id) REFERENCES users (id) ON DELETE RESTRICT,
    CONSTRAINT ck_reviews_result CHECK (result IN ('APPROVED', 'NEEDS_CHANGES', 'REJECTED'))
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE INDEX idx_reviews_profile_created ON profile_reviews (profile_id, created_at);

CREATE TABLE notifications
(
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    user_id    BIGINT       NOT NULL,
    type       VARCHAR(50)  NOT NULL,
    title      VARCHAR(200) NOT NULL,
    content    VARCHAR(1000),
    link_url   VARCHAR(500),
    is_read    BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE INDEX idx_notifications_user_read_created ON notifications (user_id, is_read, created_at);

CREATE TABLE profile_views
(
    id             BIGINT   NOT NULL AUTO_INCREMENT,
    profile_id     BIGINT   NOT NULL,
    viewer_user_id BIGINT,
    viewed_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_views_profile FOREIGN KEY (profile_id) REFERENCES profiles (id) ON DELETE CASCADE,
    CONSTRAINT fk_views_viewer FOREIGN KEY (viewer_user_id) REFERENCES users (id) ON DELETE SET NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE INDEX idx_views_profile_viewed ON profile_views (profile_id, viewed_at);