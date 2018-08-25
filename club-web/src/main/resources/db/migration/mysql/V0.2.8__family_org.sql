
ALTER TABLE ${schema}.family
    MODIFY COLUMN organization_id ${uuid} NOT NULL;
