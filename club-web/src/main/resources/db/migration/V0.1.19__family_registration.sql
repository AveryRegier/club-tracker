
CREATE TABLE ${schema}.family_registration (
    family_id uuid REFERENCES ${schema}.family,
    input_field_id uuid REFERENCES input_field,
    the_value VARCHAR(5000),
    PRIMARY KEY(family_id, input_field_id)
);

