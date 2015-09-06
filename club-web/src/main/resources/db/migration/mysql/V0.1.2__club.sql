CREATE TABLE ${schema}.club (
    id ${uuid} ${generate_uuid} PRIMARY KEY,
    parent_club_id ${uuid},
    curriculum VARCHAR(20),
    FOREIGN KEY (parent_club_id) REFERENCES ${schema}.club(id)
);
