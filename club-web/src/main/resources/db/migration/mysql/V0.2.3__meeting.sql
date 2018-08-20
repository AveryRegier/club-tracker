CREATE TABLE ${schema}.meeting (
    club_year_id ${uuid} NOT NULL,
    id ${uuid} NOT NULL,
    meeting_date DATE NOT NULL,
    PRIMARY KEY(id),
    FOREIGN KEY(club_year_id) REFERENCES ${schema}.club_year(id)
);
