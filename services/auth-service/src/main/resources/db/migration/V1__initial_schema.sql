CREATE TYPE user_status AS ENUM ('ACTIVE', 'INACTIVE', 'SUSPENDED', 'PENDING');
CREATE TYPE auth_provider AS ENUM ('LOCAL', 'GOOGLE', 'FACEBOOK', 'APPLE');

CREATE TABLE auth_users
(
    id                    BIGSERIAL PRIMARY KEY,
    phone_number          VARCHAR(15)              NOT NULL,
    email                 VARCHAR(255),
    name                  VARCHAR(100)             NOT NULL,
    normalized_name       VARCHAR(100)             NOT NULL,
    profile_picture_url   TEXT,
    password_hash         VARCHAR(255)             NOT NULL,
    public_key            TEXT,
    status                user_status              NOT NULL DEFAULT 'PENDING',
    provider              auth_provider            NOT NULL DEFAULT 'LOCAL',
    provider_id           VARCHAR(255),
    failed_login_attempts INTEGER                  NOT NULL DEFAULT 0,
    last_failed_login     TIMESTAMP WITH TIME ZONE,
    mfa_enabled           BOOLEAN                  NOT NULL DEFAULT false,
    mfa_secret            VARCHAR(255),
    recovery_codes        TEXT[],
    timezone              VARCHAR(50)                       DEFAULT 'UTC',
    locale                VARCHAR(10)                       DEFAULT 'en-US',
    created_at            TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login            TIMESTAMP WITH TIME ZONE,
    email_verified_at     TIMESTAMP WITH TIME ZONE,
    phone_verified_at     TIMESTAMP WITH TIME ZONE,
    CONSTRAINT chk_phone_number_format CHECK (phone_number ~ '^\+[1-9]\d{1,14}$'),
    CONSTRAINT chk_email_format CHECK (email IS NULL OR email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
);

CREATE TABLE auth_user_sessions
(
    id            BIGSERIAL PRIMARY KEY,
    user_id       BIGINT                   NOT NULL REFERENCES auth_users (id) ON DELETE CASCADE,
    session_token VARCHAR(512)             NOT NULL,
    refresh_token VARCHAR(512),
    device_id     VARCHAR(255),
    device_name   VARCHAR(100),
    device_type   VARCHAR(50),
    ip_address    INET,
    user_agent    TEXT,
    expires_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    revoked       BOOLEAN                  NOT NULL DEFAULT false,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create password_reset_tokens table
CREATE TABLE auth_password_reset_tokens
(
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT                   NOT NULL REFERENCES auth_users (id) ON DELETE CASCADE,
    token_hash VARCHAR(255)             NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    used       BOOLEAN                  NOT NULL DEFAULT false,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE auth_email_verification_tokens
(
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT                   NOT NULL REFERENCES auth_users (id) ON DELETE CASCADE,
    token_hash VARCHAR(255)             NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE auth_audit_log
(
    id            BIGSERIAL PRIMARY KEY,
    user_id       BIGINT                   REFERENCES auth_users (id) ON DELETE SET NULL,
    action_type   VARCHAR(50)              NOT NULL,
    resource_type VARCHAR(50)              NOT NULL,
    resource_id   VARCHAR(100),
    details       JSONB,
    ip_address    INET,
    user_agent    TEXT,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);