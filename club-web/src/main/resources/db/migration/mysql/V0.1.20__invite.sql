
CREATE TABLE ${schema}.invite (
    id ${uuid} NOT NULL,
    auth INTEGER NOT NULL,
    sent TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    invited_by ${uuid} NOT NULL,
    completed TIMESTAMP,
    PRIMARY KEY(id, auth),
    FOREIGN KEY (id) REFERENCES ${schema}.person(id),
    FOREIGN KEY (invited_by) REFERENCES ${schema}.person(id),
);

