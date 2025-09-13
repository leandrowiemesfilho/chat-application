CREATE EXTENSION IF NOT EXISTS pg_trgm;  -- For text pattern matching indexes
CREATE EXTENSION IF NOT EXISTS btree_gin; -- For GIN indexes on standard data types
CREATE EXTENSION IF NOT EXISTS btree_gist; -- For GiST indexes on standard data types

CREATE UNIQUE INDEX idx_users_phone_number ON auth_users(phone_number)
    WHERE status != 'INACTIVE';

CREATE UNIQUE INDEX idx_users_email ON auth_users(email)
    WHERE email IS NOT NULL AND status != 'INACTIVE';

CREATE INDEX idx_users_status ON auth_users(status);
CREATE INDEX idx_users_provider ON auth_users(provider, provider_id);
CREATE INDEX idx_users_created_at ON auth_users(created_at);
CREATE INDEX idx_users_last_login ON auth_users(last_login);

CREATE INDEX idx_users_active ON auth_users(id)
    WHERE status = 'ACTIVE';

CREATE INDEX idx_users_name_lower ON auth_users(LOWER(name));

CREATE UNIQUE INDEX idx_sessions_token ON auth_user_sessions(session_token);
CREATE INDEX idx_sessions_user_id ON auth_user_sessions(user_id);
CREATE INDEX idx_sessions_expires_at ON auth_user_sessions(expires_at);
CREATE INDEX idx_sessions_revoked ON auth_user_sessions(revoked);
CREATE INDEX idx_sessions_device ON auth_user_sessions(user_id, device_id);

CREATE INDEX idx_sessions_not_revoked ON auth_user_sessions(id)
    WHERE revoked = false;

CREATE INDEX idx_pwd_reset_tokens_user_id ON auth_password_reset_tokens(user_id);
CREATE INDEX idx_pwd_reset_tokens_expires ON auth_password_reset_tokens(expires_at);
CREATE INDEX idx_pwd_reset_tokens_used ON auth_password_reset_tokens(used);

CREATE INDEX idx_pwd_reset_tokens_unused ON auth_password_reset_tokens(token_hash)
    WHERE used = false;

CREATE INDEX idx_email_verify_tokens_user ON auth_email_verification_tokens(user_id);
CREATE INDEX idx_email_verify_tokens_expires ON auth_email_verification_tokens(expires_at);

CREATE INDEX idx_audit_log_user_id ON auth_audit_log(user_id);
CREATE INDEX idx_audit_log_action_type ON auth_audit_log(action_type);
CREATE INDEX idx_audit_log_resource ON auth_audit_log(resource_type, resource_id);
CREATE INDEX idx_audit_log_created_at ON auth_audit_log(created_at);

CREATE INDEX idx_audit_log_details ON auth_audit_log USING gin (details);