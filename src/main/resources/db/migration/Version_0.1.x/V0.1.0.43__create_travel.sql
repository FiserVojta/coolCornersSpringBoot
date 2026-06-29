CREATE TABLE IF NOT EXISTS travel
(
    id             BIGSERIAL PRIMARY KEY,
    title          VARCHAR(255) NOT NULL,
    description    TEXT,
    location       VARCHAR(255),
    start_date     DATE,
    end_date       DATE,
    visibility     VARCHAR(20)  NOT NULL DEFAULT 'PRIVATE',
    share_token    VARCHAR(36)  NOT NULL,
    owner_id       BIGINT       NOT NULL,
    cover_image_id BIGINT,
    entity_status  VARCHAR(20),
    created_at     TIMESTAMP WITH TIME ZONE,

    CONSTRAINT uq_travel_share_token UNIQUE (share_token),
    CONSTRAINT fk_travel_owner
        FOREIGN KEY (owner_id) REFERENCES corneruser (id),
    CONSTRAINT fk_travel_cover_image
        FOREIGN KEY (cover_image_id) REFERENCES cornerfile (id)
);

CREATE INDEX IF NOT EXISTS idx_travel_owner ON travel (owner_id);
CREATE INDEX IF NOT EXISTS idx_travel_visibility ON travel (visibility);

CREATE TABLE IF NOT EXISTS travel_has_corner_file
(
    travel_id BIGINT NOT NULL,
    file_id   BIGINT NOT NULL,

    CONSTRAINT pk_travel_has_corner_file PRIMARY KEY (travel_id, file_id),
    CONSTRAINT fk_travel_has_corner_file_travel
        FOREIGN KEY (travel_id) REFERENCES travel (id),
    CONSTRAINT fk_travel_has_corner_file_file
        FOREIGN KEY (file_id) REFERENCES cornerfile (id)
);
