package com.github.averyregier.club.domain.utility;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public interface Settings extends HasId {

    default Map<String, String> marshall() {
        return getSettings().stream()
                .collect(Collectors.toMap(
                        Setting::getKey,
                        Setting::marshall));
    }


    Map<String, Setting.Type<?>> getSettingDefinitions();
    Collection<Setting<?>> getSettings();
}
