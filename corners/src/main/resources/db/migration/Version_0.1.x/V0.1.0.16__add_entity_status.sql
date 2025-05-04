
ALTER TABLE event
    ADD COLUMN if not exists entity_status varchar(32) default 'ACTIVE' not null;

ALTER TABLE place
    ADD COLUMN if not exists entity_status varchar(32) default 'ACTIVE' not null;

ALTER TABLE trip
    ADD COLUMN if not exists entity_status varchar(32) default 'ACTIVE' not null;
