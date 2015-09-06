CREATE TABLE ${schema}.family (
    id ${uuid} ${generate_uuid} PRIMARY KEY,
    address_id ${uuid} REFERENCES ${schema}.address
);
