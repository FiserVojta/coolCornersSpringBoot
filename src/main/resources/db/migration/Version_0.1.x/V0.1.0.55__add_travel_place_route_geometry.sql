-- Safe additive change: nullable column, no default, no rewrite. Caches the routed line of the
-- leg arriving at this place as a JSON array of [lat, lng] pairs, computed by the client when the
-- travel is saved. NULL means "not cached" and the map routes the leg itself, as before.
ALTER TABLE travel_place
    ADD COLUMN IF NOT EXISTS route_geometry JSONB;
