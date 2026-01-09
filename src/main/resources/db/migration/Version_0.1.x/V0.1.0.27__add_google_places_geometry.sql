DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'google_place' AND column_name = 'geometry'
    ) THEN
ALTER TABLE google_place ADD COLUMN geometry GEOMETRY(Point, 4326);
END IF;
END $$;