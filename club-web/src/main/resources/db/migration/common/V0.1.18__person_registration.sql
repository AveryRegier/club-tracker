
CREATE TABLE ${schema}.person_registration (
    person_id ${uuid} REFERENCES ${schema}.person,
    input_field_id ${uuid} REFERENCES input_field,
    the_value VARCHAR(5000),
    PRIMARY KEY(person_id, input_field_id)
);

