-- V3: Drop oauth2_authorized_client table (tokens now stored encrypted in users table)
DROP TABLE IF EXISTS oauth2_authorized_client;
