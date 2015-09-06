CREATE TABLE ${schema}.input_field (
    id ${uuid} ${generate_uuid} PRIMARY KEY,
    parent_input_group_id ${uuid},
    name VARCHAR(100) NOT NULL,
    type VARCHAR(10) NOT NULL,
    required CHAR(1) NOT NULL,
    the_order integer NOT NULL,
    FOREIGN KEY (parent_input_group_id) REFERENCES ${schema}.input_group(id)
);
