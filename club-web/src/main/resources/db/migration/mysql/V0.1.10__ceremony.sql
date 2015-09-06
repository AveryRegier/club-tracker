CREATE TABLE ${schema}.ceremony (
    id ${uuid} ${generate_uuid} PRIMARY KEY,
    name VARCHAR(100),
    presentation_date DATE
);
