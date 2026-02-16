-- V4: Add indexes for common query patterns

CREATE INDEX IF NOT EXISTS idx_analysis_results_repo_url ON analysis_results(repo_url);
CREATE INDEX IF NOT EXISTS idx_analysis_results_created_at ON analysis_results(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_portfolios_github_username ON portfolios(github_username);
CREATE INDEX IF NOT EXISTS idx_portfolios_created_at ON portfolios(created_at DESC);
