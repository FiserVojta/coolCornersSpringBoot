-- Category-driven map pins: let each category own its map glyph (and optional
-- colour). Both columns are nullable; a NULL icon falls back to the 'default'
-- glyph in the frontend renderer, so existing rows stay safe.
alter table category add column if not exists icon  varchar(32);
alter table category add column if not exists color varchar(7);

-- Backfill seeded categories to glyph keys defined in corners-react mapyIcons.ts.
-- Idempotent: matches by name, only fills rows that have no icon yet.
update category set icon = 'viewpoint'  where name = 'View'        and icon is null;
update category set icon = 'bar'         where name in ('Drinks', 'Drink') and icon is null;
update category set icon = 'restaurant'  where name = 'Food'        and icon is null;
update category set icon = 'museum'      where name = 'Sightseeing' and icon is null;
update category set icon = 'park'        where name = 'Nature'      and icon is null;
update category set icon = 'mix'          where name = 'Mix'          and icon is null;
update category set icon = 'travel'       where name = 'Travel'       and icon is null;
update category set icon = 'presentation' where name = 'Presentation' and icon is null;
update category set icon = 'social'       where name = 'Social'       and icon is null;
update category set icon = 'volunteering' where name = 'Volunteering' and icon is null;
