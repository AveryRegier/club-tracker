UPDATE ${schema}.family f
SET f.organization_id = (SELECT id from ${schema}.organization LIMIT 1);
