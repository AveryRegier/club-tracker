UPDATE ${schema}.family f
SET f.organization_id = (SELECT id from organization LIMIT 1);
