CREATE TABLE ${schema}.login (
    id ${uuid} REFERENCES ${schema}.person NOT NULL,
    provider_id VARCHAR(20) REFERENCES ${schema}.provider NOT NULL,
    unique_id VARCHAR(500) NOT NULL,
    auth INTEGER,
    PRIMARY KEY(id, provider_id, unique_id)
);


