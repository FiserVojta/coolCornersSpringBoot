-- Phase 1 of user-created shared tags (PostgreSQL 15).
-- 1) add + backfill tag.normalized_name, 2) merge tags that collide on it,
-- 3) enforce NOT NULL + uniqueness, 4) add join-table lookup indexes.
-- The tag tables are small, so everything runs in Flyway's single transaction
-- (non-concurrent index builds; the brief locks are acceptable at this size).

-- 1) Expand: nullable column, then backfill.
-- Must match TagNames.normalize(): NFKC, lowercase, keep only letters/digits.
ALTER TABLE tag
    ADD COLUMN IF NOT EXISTS normalized_name VARCHAR(50);

UPDATE tag
SET normalized_name = left(lower(regexp_replace(normalize(name, NFKC), '[^[:alnum:]]', '', 'g')), 50)
WHERE normalized_name IS NULL;

-- Names without any letter/digit cannot be normalized; give them a unique fallback key.
UPDATE tag
SET normalized_name = 'tag' || id
WHERE normalized_name IS NULL OR normalized_name = '';

-- 2) Merge duplicates: per normalized_name the lowest id is canonical.
DROP TABLE IF EXISTS tmp_tag_merge;
CREATE TEMP TABLE tmp_tag_merge ON COMMIT DROP AS
SELECT t.id AS duplicate_id, c.canonical_id
FROM tag t
JOIN (SELECT normalized_name, min(id) AS canonical_id
      FROM tag
      GROUP BY normalized_name
      HAVING count(*) > 1) c ON c.normalized_name = t.normalized_name
WHERE t.id <> c.canonical_id;

-- Re-point join rows to the canonical tag (skip rows that already exist), then drop the old ones.
INSERT INTO place_has_tag (place_id, tag_id)
SELECT pht.place_id, m.canonical_id
FROM place_has_tag pht
JOIN tmp_tag_merge m ON m.duplicate_id = pht.tag_id
ON CONFLICT DO NOTHING;
DELETE FROM place_has_tag WHERE tag_id IN (SELECT duplicate_id FROM tmp_tag_merge);

INSERT INTO trip_has_tag (trip_id, tag_id)
SELECT tht.trip_id, m.canonical_id
FROM trip_has_tag tht
JOIN tmp_tag_merge m ON m.duplicate_id = tht.tag_id
ON CONFLICT DO NOTHING;
DELETE FROM trip_has_tag WHERE tag_id IN (SELECT duplicate_id FROM tmp_tag_merge);

INSERT INTO wander_has_tag (wander_id, tag_id)
SELECT wht.wander_id, m.canonical_id
FROM wander_has_tag wht
JOIN tmp_tag_merge m ON m.duplicate_id = wht.tag_id
ON CONFLICT DO NOTHING;
DELETE FROM wander_has_tag WHERE tag_id IN (SELECT duplicate_id FROM tmp_tag_merge);

INSERT INTO travel_has_tag (travel_id, tag_id)
SELECT tvt.travel_id, m.canonical_id
FROM travel_has_tag tvt
JOIN tmp_tag_merge m ON m.duplicate_id = tvt.tag_id
ON CONFLICT DO NOTHING;
DELETE FROM travel_has_tag WHERE tag_id IN (SELECT duplicate_id FROM tmp_tag_merge);

-- No join rows reference the duplicates any more; remove them.
DELETE FROM tag WHERE id IN (SELECT duplicate_id FROM tmp_tag_merge);

-- 3) Contract: enforce the invariant the application relies on.
ALTER TABLE tag
    ALTER COLUMN normalized_name SET NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS ux_tag_normalized_name ON tag (normalized_name);

-- 4) Lookup indexes. Join-table PKs lead with the owner id, so "which rows use tag X"
-- needs a tag_id-leading index (trip_has_tag's PK already leads with tag_id, so it
-- instead gets a trip_id index for loading a trip's tags).
CREATE INDEX IF NOT EXISTS ix_place_has_tag_tag_id ON place_has_tag (tag_id);
CREATE INDEX IF NOT EXISTS ix_wander_has_tag_tag_id ON wander_has_tag (tag_id);
CREATE INDEX IF NOT EXISTS ix_travel_has_tag_tag_id ON travel_has_tag (tag_id);
CREATE INDEX IF NOT EXISTS ix_trip_has_tag_trip_id ON trip_has_tag (trip_id);
