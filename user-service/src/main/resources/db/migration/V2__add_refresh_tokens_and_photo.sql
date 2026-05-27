ALTER TABLE users
    ADD COLUMN IF NOT EXISTS profile_photo VARCHAR(500) NOT NULL DEFAULT '';

CREATE TABLE IF NOT EXISTS refresh_tokens
(
    id         BIGSERIAL PRIMARY KEY,
    token      VARCHAR(255)        NOT NULL,
    user_email VARCHAR(255)        NOT NULL,
    expires_at TIMESTAMP           NOT NULL,
    created_at TIMESTAMP           NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_refresh_tokens_token UNIQUE (token)
);

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_email ON refresh_tokens (user_email);
