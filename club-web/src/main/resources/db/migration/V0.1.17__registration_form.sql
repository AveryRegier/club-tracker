CREATE TABLE ${schema}.registration_form (
    organization_id uuid REFERENCES ${schema}.organization,
    type VARCHAR(10),
    input_group_id uuid REFERENCES ${schema}.input_group,
    PRIMARY KEY(organization_id, type)
);
