CREATE TABLE ${schema}.parent (
    id uuid REFERENCES ${schema}.person NOT NULL PRIMARY KEY,
    family_id uuid REFERENCES ${schema}.family (id) NOT NULL
);
