
CREATE TABLE ${schema}.person_registration (
    person_id ${uuid},
    input_field_id ${uuid},
    the_value VARCHAR(5000),
    PRIMARY KEY(person_id, input_field_id),
    FOREIGN KEY (person_id) REFERENCES ${schema}.person(id),
    FOREIGN KEY (input_field_id) REFERENCES ${schema}.input_field(id)
);

