-- Per-photo note/caption (nullable, safe additive; ADD COLUMN without default is metadata-only in PostgreSQL).
ALTER TABLE travel_photo
    ADD COLUMN IF NOT EXISTS note TEXT;

-- Per-day notes for a travel: one optional note per calendar day, unique per (travel, day).
CREATE TABLE IF NOT EXISTS travel_day_note
(
    id        BIGSERIAL PRIMARY KEY,
    travel_id BIGINT NOT NULL,
    day       DATE   NOT NULL,
    note      TEXT,

    CONSTRAINT fk_travel_day_note_travel
        FOREIGN KEY (travel_id) REFERENCES travel (id),
    CONSTRAINT uq_travel_day_note_travel_day
        UNIQUE (travel_id, day)
);

CREATE INDEX IF NOT EXISTS idx_travel_day_note_travel ON travel_day_note (travel_id);
