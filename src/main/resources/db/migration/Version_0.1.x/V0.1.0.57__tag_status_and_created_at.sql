-- Phase 4 of user-created shared tags (PostgreSQL 15): moderation support.
-- Purely additive. Both columns use constant defaults, so PostgreSQL 11+ adds them as a
-- metadata-only change (no table rewrite); the tag table is small regardless.

-- Moderation status. HIDDEN tags are excluded from listings/suggestions and cannot be newly
-- assigned, but existing links keep working.
ALTER TABLE tag
    ADD COLUMN IF NOT EXISTS status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE';

ALTER TABLE tag
    DROP CONSTRAINT IF EXISTS ck_tag_status;
ALTER TABLE tag
    ADD CONSTRAINT ck_tag_status CHECK (status IN ('ACTIVE', 'HIDDEN'));

-- Creation timestamp, used for the per-creator daily creation limit and the nightly cleanup of
-- unused user tags. With the fast default, every pre-existing row gets the migration's time,
-- which delays cleanup of pre-existing unused tags by 30 days (the cleanup minimum age).
ALTER TABLE tag
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMPTZ NOT NULL DEFAULT now();
