CREATE TABLE IF NOT EXISTS trip_has_corner_file (
                                                     file_id INTEGER NOT NULL,
    trip_id  INTEGER NOT NULL,


    CONSTRAINT pk_trip_has_corner_file PRIMARY KEY (file_id, trip_id))