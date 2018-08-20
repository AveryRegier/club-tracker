CREATE TABLE ${schema}.club_year (
    club_id ${uuid} NOT NULL,
    id ${uuid} NOT NULL,
    name VARCHAR(100) NOT NULL,
    PRIMARY KEY(id),
    FOREIGN KEY(club_id) REFERENCES ${schema}.club(id)
);
