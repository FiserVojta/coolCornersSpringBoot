create table if not exists trip (
                      id SERIAL PRIMARY KEY,
                      name VARCHAR(100) NOT NULL,
                      description TEXT NOT NULL,
                      rating DOUBLE PRECISION NOT NULL,
                      duration INTEGER,
                      category_id INTEGER NOT NULL,
                      image VARCHAR(255),
                      creator VARCHAR(255),
                      FOREIGN KEY (category_id) REFERENCES category (id)
);
create table if not exists trip_has_tag (
                              id uuid PRIMARY KEY,
                              tag_id INTEGER NOT NULL REFERENCES tag (id),
                              trip_id INTEGER NOT NULL REFERENCES trip (id)
);
