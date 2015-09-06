CREATE TABLE ${schema}.input_field_value (
    parent_input_field_id ${uuid} REFERENCES ${schema}.input_field,
    the_order integer NOT NULL,
    name VARCHAR(25) NOT NULL,
    the_value VARCHAR(10) NOT NULL,
    is_default CHAR(1) NOT NULL,
    PRIMARY KEY(parent_input_field_id, the_order)
);
