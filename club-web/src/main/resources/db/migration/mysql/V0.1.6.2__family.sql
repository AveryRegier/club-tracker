CREATE TABLE ${schema}.family (
    id ${uuid} ${generate_uuid} PRIMARY KEY,
    address_id ${uuid},
    FOREIGN KEY (address_id) REFERENCES ${schema}.address(id)
);
