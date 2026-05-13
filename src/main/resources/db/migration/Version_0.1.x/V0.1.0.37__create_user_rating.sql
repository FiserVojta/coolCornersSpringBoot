CREATE TABLE IF NOT EXISTS user_rating (
    id        SERIAL PRIMARY KEY,
    author    VARCHAR(256) NOT NULL,
    user_id   INTEGER NOT NULL REFERENCES corneruser (id),
    rating    INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
