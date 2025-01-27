DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'trip' AND column_name = 'geometry'
    ) THEN
ALTER TABLE trip ADD COLUMN geometry GEOMETRY(Point, 4326);
END IF;
END $$;