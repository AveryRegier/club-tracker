CREATE TABLE ${schema}.clubber (
    id ${uuid} NOT NULL PRIMARY KEY,
    family_id ${uuid},
    club_id ${uuid},
    age_group VARCHAR(20) NOT NULL,
    FOREIGN KEY (id) REFERENCES ${schema}.person(id),
    FOREIGN KEY (family_id) REFERENCES ${schema}.family(id),
    FOREIGN KEY (club_id) REFERENCES ${schema}.club(id)
);
