CREATE TABLE ${schema}.award (
    id uuid ${generate_uuid} PRIMARY KEY,
    clubber_id uuid NOT NULL,
    section_id VARCHAR(100) NOT NULL,
    accomplishment VARCHAR(100),
    token VARCHAR(100),
    presented_at uuid REFERENCES ${schema}.ceremony,
    FOREIGN KEY (clubber_id, section_id) REFERENCES ${schema}.record (clubber_id, section_id)
);
