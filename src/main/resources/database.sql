CREATE DATABASE db_portal
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    CONNECTION LIMIT = -1;

CREATE SCHEMA portal_schema;

CREATE TABLE portal_schema.users
(
    user_id   BIGSERIAL PRIMARY KEY,
    username  VARCHAR(32)  NOT NULL UNIQUE,
    email     VARCHAR(64)  NOT NULL UNIQUE,
    password  VARCHAR(256) NOT NULL,
    status    VARCHAR(20)  NOT NULL,
    role      VARCHAR(20)  NOT NULL,
    create_at TIMESTAMPTZ  NOT NULL,
    update_at TIMESTAMPTZ  NOT NULL
);

CREATE TABLE portal_schema.attempts_login
(
    attempt_id      BIGSERIAL PRIMARY KEY,
    number_attempts INTEGER CHECK ( number_attempts >= 0 AND number_attempts <= 5) DEFAULT 0 NOT NULL,
    lock_time       BIGINT                                                         DEFAULT 0 NOT NULL,
    create_at       TIMESTAMPTZ                                                              NOT NULL,
    update_at       TIMESTAMPTZ                                                              NOT NULL,
    user_id         BIGINT REFERENCES portal_schema.users (user_id) UNIQUE
);

CREATE TABLE portal_schema.refresh_tokens
(
    token_id  BIGSERIAL PRIMARY KEY,
    token     VARCHAR(80) NOT NULL UNIQUE,
    lifetime  BIGINT      NOT NULL,
    user_id   BIGINT REFERENCES portal_schema.users (user_id),
    create_at TIMESTAMPTZ NOT NULL,
    update_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX refresh_tokens_user_id_index ON portal_schema.refresh_tokens (user_id);

CREATE TABLE portal_schema.confirmation_tokens
(
    confirmation_id BIGSERIAL PRIMARY KEY,
    token           VARCHAR(60) NOT NULL UNIQUE,
    lifetime        BIGINT      NOT NULL,
    user_id         BIGINT REFERENCES portal_schema.users (user_id) UNIQUE,
    create_at       TIMESTAMPTZ NOT NULL,
    update_at       TIMESTAMPTZ NOT NULL
);

CREATE USER developer WITH PASSWORD 'super_secret_password';
GRANT CONNECT ON DATABASE "db_portal" TO developer;
GRANT USAGE ON SCHEMA portal_schema TO developer;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA portal_schema TO developer;
