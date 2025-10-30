

ALTER TABLE event
    ADD COLUMN if not exists start_time TIMESTAMPTZ,
ADD COLUMN if not exists capacity INTEGER,
ADD COLUMN if not exists duration INTEGER,
ADD COLUMN if not exists price NUMERIC(10, 2);