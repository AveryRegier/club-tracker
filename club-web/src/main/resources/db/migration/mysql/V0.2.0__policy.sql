CREATE TABLE ${schema}.policy (
    club_id ${uuid},
    policy_name VARCHAR(20) NOT NULL,
    PRIMARY KEY (club_id, policy_name),
    FOREIGN KEY (club_id) REFERENCES ${schema}.club(id)
);
