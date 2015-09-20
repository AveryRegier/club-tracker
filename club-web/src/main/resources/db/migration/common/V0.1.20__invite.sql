
CREATE TABLE ${schema}.invite (
    id ${uuid} REFERENCES ${schema}.person NOT NULL,
    auth INTEGER NOT NULL,
    sent TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    invited_by ${uuid} REFERENCES ${schema}.person NOT NULL,
    completed TIMESTAMP,
    PRIMARY KEY(id, auth)
);

