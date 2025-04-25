--- Norair (az3l1t) file
--- Last change - 25.04.2025 (14:19 AM)
CREATE TABLE tasks (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    user_id BIGINT NOT NULL
);
--- Drop -> DROP TABLE tasks;