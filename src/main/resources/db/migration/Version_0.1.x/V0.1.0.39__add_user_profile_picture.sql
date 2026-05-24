ALTER TABLE corneruser
    ADD COLUMN IF NOT EXISTS profile_picture_file_id INTEGER REFERENCES cornerfile(id);
