ALTER TABLE cornerfile
    ADD COLUMN IF NOT EXISTS thumbnail_url  TEXT,
    ADD COLUMN IF NOT EXISTS thumbnail_name TEXT;
