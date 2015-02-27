CREATE TABLE ${schema}.club (
    id uuid ${generate_uuid} PRIMARY KEY,
    parent_club_id uuid REFERENCES ${schema}.club,
    curriculum VARCHAR(20)
);
