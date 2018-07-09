package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.club.PolicyHolder;
import com.github.averyregier.club.domain.utility.HasId;
import com.github.averyregier.club.domain.utility.Setting;
import com.github.averyregier.club.domain.utility.Settings;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SettingsAdapter implements Settings {
    private HasId idHolder;
    private Map<String, Setting.Type<?>> definitions;
    private List<Setting<?>> settings;

    public SettingsAdapter(PolicyHolder idHolder) {
        this(idHolder, idHolder.createSettingDefinitions());
    }

    public SettingsAdapter(HasId idHolder, Map<String, Setting.Type<?>> definitions) {
        this(idHolder, definitions, Collections.emptyList());
    }

    public SettingsAdapter(HasId idHolder, Map<String, Setting.Type<?>> definitions, List<Setting<?>> settings) {
        this.idHolder = idHolder;
        this.definitions = definitions;
        this.settings = settings;
    }

    @Override
    public Map<String, Setting.Type<?>> getSettingDefinitions() {
        return definitions;
    }

    @Override
    public Collection<Setting<?>> getSettings() {
        return settings;
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
