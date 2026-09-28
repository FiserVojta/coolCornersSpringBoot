-- Safe additive change: nullable column, no default, no rewrite. Existing rows keep NULL, which
-- means "unspecified" and the map keeps drawing straight lines between places.
ALTER TABLE travel
    ADD COLUMN IF NOT EXISTS transport_mode VARCHAR(32);
