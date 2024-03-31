create table if not exists country (
                       id SERIAL PRIMARY KEY,
                       name  VARCHAR(50) NOT NULL,
                       code  VARCHAR(50) NOT NULL
);

INSERT INTO country (name, code) VALUES ('Czech Republic', 'CZ');
INSERT INTO country (name, code) VALUES ('Germany', 'DE');
INSERT INTO country (name, code) VALUES ('Slovakia', 'SK');