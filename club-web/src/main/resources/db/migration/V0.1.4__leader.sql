CREATE TABLE ${schema}.leader (
    id uuid REFERENCES ${schema}.person NOT NULL,
    club_id uuid REFERENCES ${schema}.club,
    role VARCHAR(10) NOT NULL,
    PRIMARY KEY(id, club_id)
);
