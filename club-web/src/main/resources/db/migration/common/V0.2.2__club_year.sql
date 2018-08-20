CREATE TABLE ${schema}.club_year (
    club_id ${uuid} NOT NULL REFERENCES ${schema}.club,
    id ${uuid} NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);
