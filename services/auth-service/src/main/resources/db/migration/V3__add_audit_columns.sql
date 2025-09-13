ALTER TABLE auth_users
    ADD COLUMN version INTEGER NOT NULL DEFAULT 0;

CREATE TABLE auth_login_history
(
    id             BIGSERIAL PRIMARY KEY,
    user_id        BIGINT                   NOT NULL REFERENCES auth_users (id) ON DELETE CASCADE,
    attempt_type   VARCHAR(20)              NOT NULL CHECK (attempt_type IN ('SUCCESS', 'FAILURE')),
    ip_address     INET,
    user_agent     TEXT,
    device_id      VARCHAR(255),
    failure_reason VARCHAR(100),

    created_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_login_history_user_id ON auth_login_history (user_id);
CREATE INDEX idx_login_history_created_at ON auth_login_history (created_at);
CREATE INDEX idx_login_history_attempt_type ON auth_login_history (attempt_type);

CREATE
OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at
= CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$
language 'plpgsql';

CREATE TRIGGER trigger_users_updated_at
    BEFORE UPDATE
    ON auth_users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_sessions_updated_at
    BEFORE UPDATE
    ON auth_user_sessions
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();