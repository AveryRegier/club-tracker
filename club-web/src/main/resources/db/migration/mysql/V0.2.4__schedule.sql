CREATE TABLE ${schema}.schedule (
    meeting_id ${uuid} NOT NULL,
    club_id ${uuid} NOT NULL,
    curriculum VARCHAR(20) NOT NULL,
    section_id VARCHAR(100) NOT NULL,
    PRIMARY KEY(meeting_id, club_id, curriculum),
    FOREIGN KEY(club_id) REFERENCES ${schema}.club(id),
    FOREIGN KEY(meeting_id) REFERENCES ${schema}.meeting(id)
);
