package com.github.averyregier.club.broker;

import com.github.averyregier.club.domain.utility.Setting;
import com.github.averyregier.club.domain.utility.Settings;
import com.github.averyregier.club.domain.utility.adapter.SettingAdapter;
import org.jooq.DSLContext;
import org.jooq.Record2;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static com.github.averyregier.club.db.tables.ClubSetting.CLUB_SETTING;
import static com.github.averyregier.club.domain.utility.UtilityMethods.equalsAny;

public class SettingBroker extends PersistenceBroker<Settings> {
    public SettingBroker(Connector connector) {
        super(connector);
    }

    @Override
    protected void persist(Settings thing, DSLContext create) {
        create.delete(CLUB_SETTING)
                .where(CLUB_SETTING.REFERENCE_ID.eq(thing.getId().getBytes()))
                .execute();
        Map<String, String> settings = thing.marshall();
        for (Map.Entry<String, String> entry : settings.entrySet()) {
            if (!equalsAny(create.insertInto(CLUB_SETTING)
                    .set(CLUB_SETTING.REFERENCE_ID, thing.getId().getBytes())
                    .set(CLUB_SETTING.SETTING_KEY, entry.getKey())
                    .set(CLUB_SETTING.SETTING_VALUE, entry.getValue())
                    .execute(), 1, 2)) {
                fail("Setting persistence failed: " + thing.getId() + " " + entry.getKey());
            }
        }
    }

    public Stream<Setting<?>> find(String id, Map<String, Setting.Type<?>> definitions) {
        return query(create -> create
                .select(CLUB_SETTING.SETTING_KEY, CLUB_SETTING.SETTING_VALUE)
                .from(CLUB_SETTING)
                .where(CLUB_SETTING.REFERENCE_ID.eq(id.getBytes()))
                .fetch().stream()
                .map(record -> map(definitions, record))
                .filter(Optional::isPresent)
                .map(Optional::get)
        );
    }

    private Optional<Setting<?>> map(Map<String, Setting.Type<?>> definition, Record2<String, String> record) {
        String key = record.value1();
        return createSetting(record, key, definition.get(key));
    }

    private <T> Optional<Setting<?>> createSetting(Record2<String, String> record, String key, Setting.Type<T> type) {
        return type
                .unmarshall(record.value2())
                .map(v -> new SettingAdapter<>(type, key, v));
    }

}
