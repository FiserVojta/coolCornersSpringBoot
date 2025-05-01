CREATE TABLE event (
                                 id SERIAL PRIMARY KEY,
                                 name TEXT,
                                 description TEXT,
                                 created_by TEXT,
                                 created_at TIMESTAMPTZ,
                                 venue TEXT
);