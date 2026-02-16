-- V2: Users table for SaaS multi-tenant support

CREATE TABLE users (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    github_id               BIGINT UNIQUE NOT NULL,
    github_username         VARCHAR(255) NOT NULL,
    email                   VARCHAR(255),
    name                    VARCHAR(255),
    avatar_url              VARCHAR(500),
    github_access_token     TEXT,
    github_refresh_token    TEXT,
    github_token_expires_at TIMESTAMP WITH TIME ZONE,
    plan                    VARCHAR(20) NOT NULL DEFAULT 'FREE',
    analyses_used           INTEGER NOT NULL DEFAULT 0,
    portfolios_used         INTEGER NOT NULL DEFAULT 0,
    usage_reset_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT (date_trunc('month', NOW()) + INTERVAL '1 month'),
    created_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    last_login_at           TIMESTAMP WITH TIME ZONE
);

-- FK en tablas existentes (nullable para backwards-compat con datos existentes)
ALTER TABLE analysis_results ADD COLUMN user_id UUID REFERENCES users(id) ON DELETE SET NULL;
ALTER TABLE portfolios ADD COLUMN user_id UUID REFERENCES users(id) ON DELETE SET NULL;

-- Indices
CREATE INDEX idx_users_github_id ON users(github_id);
CREATE INDEX idx_analysis_results_user_id ON analysis_results(user_id);
CREATE INDEX idx_portfolios_user_id ON portfolios(user_id);
