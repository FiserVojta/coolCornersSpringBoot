create table if not exists city (
                      id SERIAL PRIMARY KEY,
                      name  VARCHAR(50) NOT NULL,
                      country_id INTEGER NOT NULL REFERENCES country (id)
);

INSERT INTO city (name, country_id) VALUES ('Jičín', 1);
INSERT INTO city (name, country_id) VALUES ('Prague', 1);
INSERT INTO city (name, country_id) VALUES ('Brno', 1);
INSERT INTO city (name, country_id) VALUES ('Ostrava', 1);
INSERT INTO city (name, country_id) VALUES ('Plzeň', 1);
INSERT INTO city (name, country_id) VALUES ('Liberec', 1);
INSERT INTO city (name, country_id) VALUES ('Olomouc', 1);
INSERT INTO city (name, country_id) VALUES ('České Budějovice', 1);
INSERT INTO city (name, country_id) VALUES ('Hradec Králové', 1);
INSERT INTO city (name, country_id) VALUES ('Pardubice', 1);