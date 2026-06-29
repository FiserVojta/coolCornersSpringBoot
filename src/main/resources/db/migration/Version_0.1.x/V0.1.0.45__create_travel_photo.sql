CREATE TABLE IF NOT EXISTS travel_photo
(
    id        BIGSERIAL PRIMARY KEY,
    travel_id BIGINT NOT NULL,
    file_id   BIGINT NOT NULL,
    latitude  DOUBLE PRECISION,
    longitude DOUBLE PRECISION,

    CONSTRAINT fk_travel_photo_travel
        FOREIGN KEY (travel_id) REFERENCES travel (id),
    CONSTRAINT fk_travel_photo_file
        FOREIGN KEY (file_id) REFERENCES cornerfile (id)
);

CREATE INDEX IF NOT EXISTS idx_travel_photo_travel ON travel_photo (travel_id);

-- Migrate existing gallery links (no coordinates yet) into the new per-photo table.
INSERT INTO travel_photo (travel_id, file_id)
SELECT travel_id, file_id
FROM travel_has_corner_file;

DROP TABLE IF EXISTS travel_has_corner_file;
