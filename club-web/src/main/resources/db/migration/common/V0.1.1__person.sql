
CREATE TABLE ${schema}.person (
    id ${uuid} ${generate_uuid} PRIMARY KEY,
    title VARCHAR(10),
    given VARCHAR(25),
    surname VARCHAR(50),
    honorific VARCHAR(10),
    friendly VARCHAR(25),
    gender CHAR(1),
    email VARCHAR(150)
);

