create table if not exists comment (
                         id uuid PRIMARY KEY,
                         name VARCHAR(50) NOT NULL,
                         value VARCHAR(255) NOT NULL,
                         title VARCHAR(100) NOT NULL,
                         author VARCHAR(50) NOT NULL,
                         place_id INTEGER REFERENCES place (id),
                         trip_id INTEGER REFERENCES trip (id),
                         created TIMESTAMP NOT NULL,
                         rating DOUBLE PRECISION NOT NULL
);
