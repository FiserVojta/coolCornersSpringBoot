create table if not exists place (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(100) NOT NULL,
                       description TEXT NOT NULL,
                       phone_number VARCHAR(20),
                       price DOUBLE PRECISION,
                       opening_hours TEXT,
                       category_id INTEGER NOT NULL REFERENCES category (id),
                       image VARCHAR(255),
                        latitude DOUBLE PRECISION NOT NULL,
                        longitude DOUBLE PRECISION NOT NULL
);

