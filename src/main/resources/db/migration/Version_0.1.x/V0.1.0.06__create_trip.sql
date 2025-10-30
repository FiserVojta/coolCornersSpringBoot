create table if not exists trip (
                      id SERIAL PRIMARY KEY,
                      name VARCHAR(100) NOT NULL,
                      description TEXT NOT NULL,
                      rating DOUBLE PRECISION,
                      duration INTEGER,
                      category_id INTEGER NOT NULL,
                      image VARCHAR(255),
                      creator VARCHAR(255),
                      FOREIGN KEY (category_id) REFERENCES category (id)
);
create table if not exists trip_has_tag (
                              tag_id INTEGER NOT NULL REFERENCES tag (id),
                              trip_id INTEGER NOT NULL REFERENCES trip (id),
    CONSTRAINT pk_trip_has_tag PRIMARY KEY (tag_id, trip_id)
);

CREATE TABLE IF NOT EXISTS trip_has_place (
                                              place_id INTEGER NOT NULL,
                                              trip_id  INTEGER NOT NULL,

    -- Make (place_id, trip_id) the composite primary key
                                              CONSTRAINT pk_trip_has_place PRIMARY KEY (place_id, trip_id),

    -- Foreign key references
    CONSTRAINT fk_trip_has_place_place
        FOREIGN KEY (place_id)
        REFERENCES place (id),
    CONSTRAINT fk_trip_has_place_trip
        FOREIGN KEY (trip_id)
        REFERENCES trip (id)
    );

CREATE SEQUENCE if not exists trip_seq;