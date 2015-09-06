CREATE TABLE ${schema}.clubber (
    id ${uuid} REFERENCES ${schema}.person NOT NULL PRIMARY KEY,
    family_id ${uuid} REFERENCES ${schema}.family,
    club_id ${uuid} REFERENCES ${schema}.club,
    age_group VARCHAR(20) NOT NULL
);
