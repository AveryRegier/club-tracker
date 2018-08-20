CREATE TABLE ${schema}.schedule (
    meeting_id ${uuid} NOT NULL REFERENCES ${schema}.meeting,
    club_id ${uuid} NOT NULL REFERENCES ${schema}.club,
    curriculum VARCHAR(20) NOT NULL,
    section_id VARCHAR(100) NOT NULL,
    PRIMARY KEY(meeting_id, club_id, curriculum)
);
