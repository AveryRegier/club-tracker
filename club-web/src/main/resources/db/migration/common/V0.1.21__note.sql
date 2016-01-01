
CREATE TABLE ${schema}.note (
    id ${uuid} NOT NULL PRIMARY KEY,
    written_by ${uuid} NOT NULL REFERENCES ${schema}.person(id),
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reply_to ${uuid} REFERENCES ${schema}.note(id)
);

CREATE TABLE ${schema}.note_text (
    id ${uuid} NOT NULL REFERENCES ${schema}.note(id),
    sequence INTEGER DEFAULT 0 NOT NULL,
    note VARCHAR(10000) NOT NULL,
    PRIMARY KEY(id, sequence)
);

