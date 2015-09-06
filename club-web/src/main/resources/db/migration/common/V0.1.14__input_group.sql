CREATE TABLE ${schema}.input_group (
    id ${uuid} ${generate_uuid} PRIMARY KEY,
    parent_input_group_id ${uuid} REFERENCES ${schema}.input_group,
    name VARCHAR(25),
    the_order integer NOT NULL
);
