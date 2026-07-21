-- Safe additive change: nullable category column, new join table, new category rows.
ALTER TABLE travel
    ADD COLUMN IF NOT EXISTS category_id INTEGER REFERENCES category (id);

CREATE TABLE IF NOT EXISTS travel_has_tag
(
    travel_id BIGINT  NOT NULL REFERENCES travel (id) ON DELETE CASCADE,
    tag_id    INTEGER NOT NULL REFERENCES tag (id),

    CONSTRAINT pk_travel_has_tag PRIMARY KEY (travel_id, tag_id)
);

-- Travel gets its own category assortment (CategoryType.TRAVEL).
INSERT INTO category (name, main, title, category_type)
VALUES ('Roadtrip', TRUE, 'Road trip', 'TRAVEL'),
       ('Hiking', TRUE, 'Hiking & nature', 'TRAVEL'),
       ('Citybreak', TRUE, 'City break', 'TRAVEL'),
       ('Beach', TRUE, 'Beach & relax', 'TRAVEL'),
       ('Culture', TRUE, 'Culture & food', 'TRAVEL'),
       ('Expedition', TRUE, 'Expedition', 'TRAVEL');
