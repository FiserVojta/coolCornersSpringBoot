-- Safe additive change: nullable aggregate column + new table, no rewrites of existing data.
ALTER TABLE travel
    ADD COLUMN IF NOT EXISTS rating DOUBLE PRECISION;

CREATE TABLE IF NOT EXISTS travel_rating
(
    id         BIGSERIAL PRIMARY KEY,
    travel_id  BIGINT       NOT NULL,
    author     VARCHAR(256) NOT NULL,
    rating     INTEGER      NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE,

    -- Ratings die with their travel; the app deletes travels via entityManager.remove only.
    CONSTRAINT fk_travel_rating_travel
        FOREIGN KEY (travel_id) REFERENCES travel (id) ON DELETE CASCADE,
    -- One rating per user and travel; re-rating updates the existing row.
    CONSTRAINT uq_travel_rating_travel_author UNIQUE (travel_id, author)
);

CREATE INDEX IF NOT EXISTS idx_travel_rating_travel ON travel_rating (travel_id);
