
CREATE TABLE ${schema}.family_registration (
    family_id ${uuid},
    input_field_id ${uuid},
    the_value VARCHAR(5000),
    PRIMARY KEY(family_id, input_field_id),
    FOREIGN KEY (family_id) REFERENCES ${schema}.family(id),
    FOREIGN KEY (input_field_id) REFERENCES ${schema}.input_field(id)
);

