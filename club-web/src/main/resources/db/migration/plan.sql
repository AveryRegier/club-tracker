
${setup}
${create_schema}

CREATE TABLE ${schema}.person (
    id uuid ${generate_uuid} PRIMARY KEY,
    title VARCHAR(10),
    given VARCHAR(25),
    surname VARCHAR(50),
    honorific VARCHAR(10),
    friendly VARCHAR(25),
    gender CHAR(1),
    email VARCHAR(150)
);

CREATE TABLE ${schema}.club (
    id uuid ${generate_uuid} PRIMARY KEY,
    parent_club_id uuid REFERENCES ${schema}.club,
    curriculum VARCHAR(20)
);

CREATE TABLE ${schema}.organization (
    id uuid ${generate_uuid} PRIMARY KEY,
    organizationName VARCHAR(50) NOT NULL,
    locale VARCHAR(10),
    club_id uuid REFERENCES ${schema}.club
);

CREATE TABLE ${schema}.leader (
    id uuid REFERENCES ${schema}.person NOT NULL,
    club_id uuid REFERENCES ${schema}.club,
    role VARCHAR(10) NOT NULL,
    PRIMARY KEY(id, club_id)
);

CREATE TABLE ${schema}.family (
    id uuid ${generate_uuid} PRIMARY KEY
);

CREATE TABLE ${schema}.clubber (
    id uuid REFERENCES ${schema}.person NOT NULL PRIMARY KEY,
    family_id uuid REFERENCES ${schema}.family,
    club_id uuid REFERENCES ${schema}.club,
    age_group VARCHAR(20) NOT NULL
);

CREATE TABLE ${schema}.listener (
    id uuid REFERENCES ${schema}.person NOT NULL,
    club_id uuid REFERENCES ${schema}.club NOT NULL,
    PRIMARY KEY(id, club_id)
);

CREATE TABLE ${schema}.parent (
    id uuid REFERENCES ${schema}.person NOT NULL PRIMARY KEY,
    family_id uuid REFERENCES ${schema}.family (id) NOT NULL
);

CREATE TABLE ${schema}.record (
    clubber_id uuid REFERENCES ${schema}.person NOT NULL,
    club_id uuid REFERENCES ${schema}.club,
    section_id VARCHAR(100) NOT NULL,
    signed_by uuid,
    sign_date DATE,
    note VARCHAR(1000),
    PRIMARY KEY(clubber_id, section_id),
    FOREIGN KEY(signed_by, club_id)  REFERENCES ${schema}.listener (id, club_id)
);

CREATE TABLE ${schema}.ceremony (
    id uuid ${generate_uuid} PRIMARY KEY,
    name VARCHAR(100),
    presentation_date DATE
);

CREATE TABLE ${schema}.award (
    id uuid ${generate_uuid} PRIMARY KEY,
    clubber_id uuid NOT NULL,
    section_id VARCHAR(100) NOT NULL,
    accomplishment VARCHAR(100),
    token VARCHAR(100),
    presented_at uuid REFERENCES ${schema}.ceremony,
    FOREIGN KEY (clubber_id, section_id) REFERENCES ${schema}.record (clubber_id, section_id)
);

CREATE TABLE ${schema}.provider (
    provider_id VARCHAR(20) PRIMARY KEY,
    site VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    image VARCHAR(250) NOT NULL,
    client_key VARCHAR(500) NOT NULL,
    secret VARCHAR(500) NOT NULL
);