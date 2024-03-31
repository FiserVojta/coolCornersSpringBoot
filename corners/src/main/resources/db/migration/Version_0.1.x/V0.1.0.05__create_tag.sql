create table if not exists tag (
                     id SERIAL PRIMARY KEY,
                     name VARCHAR(50) NOT NULL,
                     creator VARCHAR(50) NOT null
);

create table if not exists  place_has_tag (
                               id uuid PRIMARY KEY,
                               tag_id INTEGER NOT NULL REFERENCES tag (id),
                               place_id INTEGER NOT NULL REFERENCES place (id)
);
