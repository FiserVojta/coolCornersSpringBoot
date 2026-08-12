-- Seed CoTravel (wander) categories. The CategoryType.COTRAVEL enum and the
-- /public/categories?type=COTRAVEL filter already existed, but no category rows were ever
-- inserted for it, so the co-travel create form had an empty category list. Safe additive:
-- new rows only, guarded so re-running (or running against a partially seeded DB) is a no-op.
INSERT INTO category (name, main, title, category_type)
SELECT v.name, TRUE, v.title, 'COTRAVEL'
FROM (VALUES
    ('Group trip', 'Group trip'),
    ('Roadtrip', 'Road trip'),
    ('Hiking', 'Hiking & nature'),
    ('Citybreak', 'City break'),
    ('Culture', 'Culture & food'),
    ('Expedition', 'Expedition')
) AS v(name, title)
WHERE NOT EXISTS (
    SELECT 1 FROM category c WHERE c.name = v.name AND c.category_type = 'COTRAVEL'
);
