CREATE TABLE ${schema}.record (
    clubber_id ${uuid} NOT NULL,
    club_id ${uuid},
    section_id VARCHAR(100) NOT NULL,
    signed_by ${uuid},
    sign_date DATE,
    note VARCHAR(1000),
    PRIMARY KEY(clubber_id, section_id),
    FOREIGN KEY(signed_by, club_id)  REFERENCES ${schema}.listener (id, club_id),
    FOREIGN KEY (clubber_id) REFERENCES ${schema}.person(id),
    FOREIGN KEY (club_id) REFERENCES ${schema}.club(id)
);
