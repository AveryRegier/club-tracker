package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.utility.HasId;
import com.github.averyregier.club.domain.utility.Setting;
import com.github.averyregier.club.domain.utility.Settings;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class SettingsAdapter implements Settings {
    private HasId idHolder;

    public SettingsAdapter(HasId idHolder) {
        this.idHolder = idHolder;
    }

    @Override
    public Map<String, Setting.Type<?>> getSettingDefinitions() {
        return Collections.emptyMap();
    }

    @Override
    public Collection<Setting<?>> getSettings() {
        return Collections.emptyList();
    }

    @Override
    public String getId() {
        return idHolder.getId();
    }

    @Override
    public String getShortCode() {
        return idHolder.getShortCode();
    }
}
