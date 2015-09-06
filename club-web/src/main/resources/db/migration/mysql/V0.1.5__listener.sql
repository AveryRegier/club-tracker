CREATE TABLE ${schema}.listener (
    id ${uuid} NOT NULL,
    club_id ${uuid} NOT NULL,
    PRIMARY KEY(id, club_id),
    FOREIGN KEY (id) REFERENCES ${schema}.person(id),
    FOREIGN KEY (club_id) REFERENCES ${schema}.club(id)
);
