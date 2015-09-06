CREATE TABLE ${schema}.login (
    id ${uuid} NOT NULL,
    provider_id VARCHAR(20) NOT NULL,
    unique_id VARCHAR(500) NOT NULL,
    auth INTEGER,
    PRIMARY KEY(id, provider_id, unique_id),
    FOREIGN KEY (id) REFERENCES ${schema}.person(id),
    FOREIGN KEY (provider_id) REFERENCES ${schema}.provider(provider_id)
);


