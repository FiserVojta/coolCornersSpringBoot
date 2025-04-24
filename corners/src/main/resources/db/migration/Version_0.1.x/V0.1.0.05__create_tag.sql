create table if not exists tag (
                     id SERIAL PRIMARY KEY,
                     name VARCHAR(50) NOT NULL,
                     creator VARCHAR(50) NOT null
);

create table if not exists  place_has_tag (
                               tag_id INTEGER NOT NULL REFERENCES tag (id),
                               place_id INTEGER NOT NULL REFERENCES place (id),
    CONSTRAINT pk_place_has_tag PRIMARY KEY (place_id, tag_id)
);
