CREATE TABLE ${schema}.policy (
    club_id ${uuid} REFERENCES ${schema}.club,
    policy_name VARCHAR(50) NOT NULL,
    PRIMARY KEY(club_id, policy_name)
);
