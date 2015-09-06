CREATE TABLE ${schema}.address (
    id ${uuid} ${generate_uuid} PRIMARY KEY,
    line1 VARCHAR(200),
    line2 VARCHAR(200),
    city VARCHAR(100),
    territory VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(3)
);


