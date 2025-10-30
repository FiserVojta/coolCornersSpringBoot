
ALTER TABLE place
    ADD COLUMN if not exists created_by varchar(128)  default 'admin@corners.cz' not null;

ALTER TABLE trip
    ADD COLUMN if not exists created_by varchar(128)  default 'admin@corners.cz' not null;
