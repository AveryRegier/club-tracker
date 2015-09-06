CREATE TABLE ${schema}.organization (
    id ${uuid} ${generate_uuid} PRIMARY KEY,
    organizationName VARCHAR(50) NOT NULL,
    locale VARCHAR(10),
    club_id ${uuid} REFERENCES ${schema}.club
);
