CREATE TABLE ${schema}.club_setting (
    reference_id ${uuid},
    setting_key VARCHAR(50) NOT NULL,
    setting_value VARCHAR(500) NOT NULL,
    PRIMARY KEY(reference_id, setting_key)
);
