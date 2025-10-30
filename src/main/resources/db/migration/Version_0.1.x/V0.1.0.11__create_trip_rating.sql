create table if not exists trip_rating (
                                        id SERIAL PRIMARY KEY,
                                        author VARCHAR(256) NOT NULL,
                                        trip_id INTEGER NOT NULL REFERENCES trip (id),
                                        rating INTEGER NOT NULL,
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );