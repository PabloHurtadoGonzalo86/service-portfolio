-- V1: Initial schema - analysis_results, portfolios, oauth2_authorized_client

CREATE TABLE IF NOT EXISTS analysis_results (
    id              BIGSERIAL       PRIMARY KEY,
    repo_url        VARCHAR(255)    NOT NULL,
    project_name    VARCHAR(255)    NOT NULL,
    short_description TEXT          NOT NULL,
    tech_stack      JSONB           NOT NULL DEFAULT '[]',
    detected_features JSONB         NOT NULL DEFAULT '[]',
    readme_content  TEXT            NOT NULL,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS portfolios (
    id                  BIGSERIAL       PRIMARY KEY,
    github_username     VARCHAR(255)    NOT NULL,
    portfolio_data      TEXT            NOT NULL DEFAULT '{}',
    total_public_repos  INTEGER         NOT NULL DEFAULT 0,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS oauth2_authorized_client (
    client_registration_id  VARCHAR(100)    NOT NULL,
    principal_name          VARCHAR(200)    NOT NULL,
    access_token_type       VARCHAR(100)    NOT NULL,
    access_token_value      BYTEA           NOT NULL,
    access_token_issued_at  TIMESTAMP       NOT NULL,
    access_token_expires_at TIMESTAMP       NOT NULL,
    access_token_scopes     VARCHAR(1000),
    refresh_token_value     BYTEA,
    refresh_token_issued_at TIMESTAMP,
    created_at              TIMESTAMP       DEFAULT CURRENT_TIMESTAMP NOT NULL,
    PRIMARY KEY (client_registration_id, principal_name)
);
