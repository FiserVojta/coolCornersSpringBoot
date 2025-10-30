create table if not exists place_rating (
                                        id SERIAL PRIMARY KEY,
                                        author VARCHAR(256) NOT NULL,
                                        place_id INTEGER NOT NULL REFERENCES place (id),
                                        rating INTEGER NOT NULL,
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );