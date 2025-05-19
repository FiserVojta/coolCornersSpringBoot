CREATE TABLE corneruser (
                       id SERIAL PRIMARY KEY,
                       keycloak_id TEXT UNIQUE NOT NULL,
                       email TEXT NOT NULL,
                       name TEXT,
                       display_name TEXT,
                       created_at TIMESTAMPTZ DEFAULT now()
);