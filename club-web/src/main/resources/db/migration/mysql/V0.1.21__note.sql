
CREATE TABLE ${schema}.note (
    id ${uuid} NOT NULL,
    written_by ${uuid} NOT NULL,
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reply_to ${uuid},
    PRIMARY KEY(id),
    FOREIGN KEY(written_by) REFERENCES ${schema}.person(id),
    FOREIGN KEY(reply_to) REFERENCES ${schema}.note(id)
);

CREATE TABLE ${schema}.note_text (
    id ${uuid} NOT NULL,
    sequence INTEGER NOT NULL DEFAULT 0,
    note VARCHAR(10000) NOT NULL,
    PRIMARY KEY(id, sequence),
    FOREIGN KEY(id) REFERENCES ${schema}.note(id)
);

