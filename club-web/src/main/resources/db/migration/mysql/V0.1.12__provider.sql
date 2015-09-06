CREATE TABLE ${schema}.provider (
    provider_id VARCHAR(20) PRIMARY KEY,
    site VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    image VARCHAR(250) NOT NULL,
    client_key VARCHAR(500) NOT NULL,
    secret VARCHAR(500) NOT NULL
);

