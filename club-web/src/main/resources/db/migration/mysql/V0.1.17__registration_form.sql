CREATE TABLE ${schema}.registration_form (
    organization_id ${uuid},
    type VARCHAR(10),
    input_group_id ${uuid},
    PRIMARY KEY(organization_id, type),
    FOREIGN KEY (organization_id) REFERENCES ${schema}.organization(id),
    FOREIGN KEY (input_group_id) REFERENCES ${schema}.input_group(id)
);
