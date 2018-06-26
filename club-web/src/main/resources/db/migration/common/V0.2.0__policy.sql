CREATE TABLE ${schema}.policy (
    club_id ${uuid} REFERENCES ${schema}.club,
    policy_name VARCHAR(20) NOT NULL,
    PRIMARY KEY(club_id, policy_name)
);
