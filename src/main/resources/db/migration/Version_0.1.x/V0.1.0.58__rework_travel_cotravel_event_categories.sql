-- Rework travel, cotravel and event categories (PostgreSQL).
--
-- Travel and cotravel share one "type of trip" list. Road trip is dropped because travels carry a
-- transport mode, and Group trip is dropped because every cotravel plan is a group trip. Events are
-- categorised by format, and Travel opportunity is dropped because that is what cotravel is for.
--
-- Safety: only display titles change on kept rows (map icons key off the internal name). Rows using
-- a removed category are remapped first, and a category is deleted only when nothing references it.
-- event.category_id has no foreign key, so remapping events first is what keeps them valid.
-- Re-runnable: inserts are guarded by NOT EXISTS and updates match on (name, category_type).

-- 1) New categories.
INSERT INTO category (name, main, title, category_type)
SELECT v.name, TRUE, v.title, v.category_type
FROM (VALUES
    ('Mountains', 'Mountains & snow', 'TRAVEL'),
    ('Beach', 'Beach & islands', 'COTRAVEL'),
    ('Mountains', 'Mountains & snow', 'COTRAVEL'),
    ('Workshop', 'Workshop', 'EVENT'),
    ('Outdoor', 'Outdoor activity', 'EVENT'),
    ('Festival', 'Culture & festival', 'EVENT')
) AS v(name, title, category_type)
WHERE NOT EXISTS (
    SELECT 1 FROM category c WHERE c.name = v.name AND c.category_type = v.category_type
);

-- 2) New display titles for kept categories.
UPDATE category c
SET title = v.title
FROM (VALUES
    ('Citybreak', 'City break'),
    ('Hiking', 'Nature & hiking'),
    ('Beach', 'Beach & islands'),
    ('Culture', 'Culture & history'),
    ('Expedition', 'Adventure & expedition')
) AS v(name, title)
WHERE c.name = v.name
  AND c.category_type IN ('TRAVEL', 'COTRAVEL')
  AND c.title <> v.title;

UPDATE category c
SET title = v.title
FROM (VALUES
    ('Presentation', 'Talk & presentation'),
    ('Social', 'Meetup & social'),
    ('Volunteering', 'Volunteering')
) AS v(name, title)
WHERE c.name = v.name
  AND c.category_type = 'EVENT'
  AND c.title <> v.title;

-- 3) Remap rows that use a removed category.
-- Road trip travels keep that meaning as a DRIVING transport mode unless a mode is already set.
UPDATE travel t
SET transport_mode = 'DRIVING'
FROM category c
WHERE t.category_id = c.id
  AND c.category_type = 'TRAVEL'
  AND c.name = 'Roadtrip'
  AND t.transport_mode IS NULL;

UPDATE travel t
SET category_id = NULL
FROM category c
WHERE t.category_id = c.id
  AND c.category_type = 'TRAVEL'
  AND c.name = 'Roadtrip';

-- Cotravel plans in Road trip or Group trip have no clear new category, so they become uncategorised
-- and their owners can pick one when editing.
UPDATE wander w
SET category_id = NULL
FROM category c
WHERE w.category_id = c.id
  AND c.category_type = 'COTRAVEL'
  AND c.name IN ('Roadtrip', 'Group trip');

-- Travel opportunity events move to Meetup & social.
UPDATE event e
SET category_id = social.id
FROM category legacy,
     (SELECT id FROM category WHERE name = 'Social' AND category_type = 'EVENT' ORDER BY id LIMIT 1) social
WHERE e.category_id = legacy.id
  AND legacy.category_type = 'EVENT'
  AND legacy.name = 'Travel';

-- 4) Delete removed categories plus untyped leftovers from V0.1.0.08, but only rows nothing
-- references (places and trips may still point at the untyped ones).
DELETE FROM category c
WHERE ((c.category_type = 'TRAVEL' AND c.name = 'Roadtrip')
    OR (c.category_type = 'COTRAVEL' AND c.name IN ('Roadtrip', 'Group trip'))
    OR (c.category_type = 'EVENT' AND c.name = 'Travel')
    OR c.category_type IS NULL)
  AND NOT EXISTS (SELECT 1 FROM place p WHERE p.category_id = c.id)
  AND NOT EXISTS (SELECT 1 FROM trip t WHERE t.category_id = c.id)
  AND NOT EXISTS (SELECT 1 FROM wander w WHERE w.category_id = c.id)
  AND NOT EXISTS (SELECT 1 FROM event e WHERE e.category_id = c.id)
  AND NOT EXISTS (SELECT 1 FROM travel tr WHERE tr.category_id = c.id)
  AND NOT EXISTS (SELECT 1 FROM google_place g WHERE g.category_id = c.id);
