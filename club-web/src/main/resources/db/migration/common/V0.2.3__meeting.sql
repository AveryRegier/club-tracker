CREATE TABLE ${schema}.meeting (
    club_year_id ${uuid} NOT NULL REFERENCES ${schema}.club_year,
    id ${uuid} PRIMARY KEY,
    meeting_date DATE NOT NULL
);
