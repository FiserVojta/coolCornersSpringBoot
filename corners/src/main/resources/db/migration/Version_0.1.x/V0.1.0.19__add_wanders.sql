CREATE TABLE wander (
                        id BIGSERIAL PRIMARY KEY,
                        created_by_user_id BIGINT,
                        description TEXT,
                        capacity INTEGER,
                        start_time TIMESTAMPTZ,
                        category_id BIGINT,

                        CONSTRAINT fk_wander_user FOREIGN KEY (created_by_user_id) REFERENCES corneruser(id),
                        CONSTRAINT fk_wander_category FOREIGN KEY (category_id) REFERENCES category(id)
);

CREATE TABLE wanderpart (
                            id BIGSERIAL PRIMARY KEY,
                            "order" INTEGER,
                            wander_part_id BIGINT,
                            CONSTRAINT fk_wander FOREIGN KEY (wander_part_id) REFERENCES wander(id)
);

CREATE TABLE wanderpart_place (
                                  wanderpart_id BIGINT NOT NULL,
                                  place_id BIGINT NOT NULL,
                                  PRIMARY KEY (wanderpart_id, place_id),
                                  CONSTRAINT fk_wanderpart_place_wanderpart FOREIGN KEY (wanderpart_id) REFERENCES wanderpart(id),
                                  CONSTRAINT fk_wanderpart_place_place FOREIGN KEY (place_id) REFERENCES place(id)
);

CREATE TABLE wanderpart_trip (
                                 wanderpart_id BIGINT NOT NULL,
                                 trip_id BIGINT NOT NULL,
                                 PRIMARY KEY (wanderpart_id, trip_id),
                                 CONSTRAINT fk_wanderpart_trip_wanderpart FOREIGN KEY (wanderpart_id) REFERENCES wanderpart(id),
                                 CONSTRAINT fk_wanderpart_trip_trip FOREIGN KEY (trip_id) REFERENCES trip(id)
);



CREATE TABLE wander_has_wanderers (
                                      wander_id BIGINT NOT NULL,
                                      user_id BIGINT NOT NULL,
                                      PRIMARY KEY (wander_id, user_id),
                                      CONSTRAINT fk_wander_has_wanderers_wander FOREIGN KEY (wander_id) REFERENCES wander(id),
                                      CONSTRAINT fk_wander_has_wanderers_user FOREIGN KEY (user_id) REFERENCES corneruser(id)
);

CREATE TABLE wander_has_tag (
                                wander_id BIGINT NOT NULL,
                                tag_id BIGINT NOT NULL,
                                PRIMARY KEY (wander_id, tag_id),
                                CONSTRAINT fk_wander_has_tag_wander FOREIGN KEY (wander_id) REFERENCES wander(id),
                                CONSTRAINT fk_wander_has_tag_tag FOREIGN KEY (tag_id) REFERENCES tag(id)
);

CREATE TABLE wander_has_parts (
                                  wander_id BIGINT NOT NULL,
                                  wander_part_id BIGINT NOT NULL,
                                  PRIMARY KEY (wander_id, wander_part_id),
                                  CONSTRAINT fk_wander_has_parts_wander FOREIGN KEY (wander_id) REFERENCES wander(id),
                                  CONSTRAINT fk_wander_has_parts_part FOREIGN KEY (wander_part_id) REFERENCES wanderpart(id)
);