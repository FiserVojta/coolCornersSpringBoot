CREATE TABLE IF NOT EXISTS travel_place
(
    id        BIGSERIAL PRIMARY KEY,
    travel_id BIGINT           NOT NULL,
    name      VARCHAR(255),
    latitude  DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,

    CONSTRAINT fk_travel_place_travel
        FOREIGN KEY (travel_id) REFERENCES travel (id)
);

CREATE INDEX IF NOT EXISTS idx_travel_place_travel ON travel_place (travel_id);
