CREATE TABLE ${schema}.input_group (
    id ${uuid} ${generate_uuid} PRIMARY KEY,
    parent_input_group_id ${uuid},
    name VARCHAR(25),
    the_order integer NOT NULL,
    FOREIGN KEY (parent_input_group_id) REFERENCES ${schema}.input_group(id)
);
