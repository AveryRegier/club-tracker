CREATE TABLE ${schema}.parent (
    id ${uuid} NOT NULL PRIMARY KEY,
    family_id ${uuid} NOT NULL,
    FOREIGN KEY (id) REFERENCES ${schema}.person(id),
    FOREIGN KEY (family_id) REFERENCES ${schema}.family(id)
);
