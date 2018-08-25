ALTER TABLE ${schema}.family
    ADD COLUMN organization_id ${uuid};

ALTER TABLE ${schema}.family
    ADD FOREIGN KEY (organization_id) REFERENCES ${schema}.organization(id);
