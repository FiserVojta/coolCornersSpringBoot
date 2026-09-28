-- Safe additive change: nullable column, no default, no rewrite. Stores how the traveller got to
-- this place from the previous one. NULL means "use the travel's transport_mode", so existing
-- rows keep rendering exactly as before.
ALTER TABLE travel_place
    ADD COLUMN IF NOT EXISTS transport_mode VARCHAR(32);
