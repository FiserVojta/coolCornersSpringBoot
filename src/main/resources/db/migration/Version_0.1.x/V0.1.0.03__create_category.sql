create table if not exists category (
                          id SERIAL PRIMARY KEY,
                          name VARCHAR(50) NOT NULL,
                          main BOOLEAN NOT NULL,
                          title VARCHAR(100) NOT null
);