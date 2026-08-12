-- Safe additive change: nullable self-reference so a travel can be "my own version" of another
-- travel (the same trip done again by someone else, with their own photos). Existing rows keep
-- origin_travel_id NULL, which means "this is an original".
ALTER TABLE travel
    ADD COLUMN IF NOT EXISTS origin_travel_id BIGINT;

-- ON DELETE SET NULL: removing an original must not delete other people's versions of it;
-- they simply become originals again.
DO
$$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_travel_origin') THEN
            ALTER TABLE travel
                ADD CONSTRAINT fk_travel_origin
                    FOREIGN KEY (origin_travel_id) REFERENCES travel (id) ON DELETE SET NULL;
        END IF;
    END
$$;

CREATE INDEX IF NOT EXISTS idx_travel_origin ON travel (origin_travel_id);
