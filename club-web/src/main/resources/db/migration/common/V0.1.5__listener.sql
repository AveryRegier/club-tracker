CREATE TABLE ${schema}.listener (
    id ${uuid} REFERENCES ${schema}.person NOT NULL,
    club_id ${uuid} REFERENCES ${schema}.club NOT NULL,
    PRIMARY KEY(id, club_id)
);
