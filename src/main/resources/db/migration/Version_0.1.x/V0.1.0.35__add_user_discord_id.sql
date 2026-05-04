ALTER TABLE corneruser ADD COLUMN discord_id TEXT;
ALTER TABLE corneruser ADD CONSTRAINT corneruser_discord_id_unique UNIQUE (discord_id);
