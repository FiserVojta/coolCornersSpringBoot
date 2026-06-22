-- Per-stop category: let a saved Mapy stop (google_place) link to one of our
-- own categories, so its map pin can carry a category glyph. The value is
-- guessed from the Mapy label at save time and editable by the trip creator.
-- Nullable + additive; a NULL category falls back to the default pin.
alter table google_place add column if not exists category_id integer;

do $$
begin
    if not exists (
        select 1 from information_schema.table_constraints
        where constraint_name = 'fk_google_place_category'
    ) then
        alter table google_place
            add constraint fk_google_place_category
            foreign key (category_id) references category (id);
    end if;
end $$;
