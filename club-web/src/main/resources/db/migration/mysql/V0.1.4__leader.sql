CREATE TABLE ${schema}.leader (
    id ${uuid} NOT NULL,
    club_id ${uuid},
    role VARCHAR(10) NOT NULL,
    PRIMARY KEY(id, club_id),
    FOREIGN KEY (id) REFERENCES ${schema}.person(id),
    FOREIGN KEY (club_id) REFERENCES ${schema}.club(id)
);
