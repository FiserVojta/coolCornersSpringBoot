create table if not exists cornerfile (
                                       id SERIAL PRIMARY KEY,
                                       name  VARCHAR(50),
                                       url  VARCHAR(50) NOT NULL,
                                       created_by  VARCHAR(50) NOT NULL,
                                       entity_status  varchar(32) NOT NULL,
                                    created_at TIMESTAMPTZ
    );
