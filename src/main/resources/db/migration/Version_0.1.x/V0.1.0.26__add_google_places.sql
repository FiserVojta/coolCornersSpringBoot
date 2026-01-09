create table if not exists google_place (
                                    place_id VARCHAR(100) PRIMARY KEY,
                                     name VARCHAR(100) NOT NULL
    );

CREATE TABLE IF NOT EXISTS trip_has_google_place (
                                              place_id VARCHAR(100) NOT NULL,
                                              trip_id  INTEGER NOT NULL,

    -- Make (place_id, trip_id) the composite primary key
                                              CONSTRAINT pk_trip_has_google_place PRIMARY KEY (place_id, trip_id),

    -- Foreign key references
    CONSTRAINT fk_trip_has_google_place_place
    FOREIGN KEY (place_id)
    REFERENCES google_place (place_id),
    CONSTRAINT fk_trip_has_google_place_trip
    FOREIGN KEY (trip_id)
    REFERENCES trip (id)
    );