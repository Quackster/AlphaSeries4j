package com.alphaseries.game.room;

import com.alphaseries.dao.mysql.SettingsDao;
import com.alphaseries.db.Database;
import com.alphaseries.db.MySQL;
import com.alphaseries.game.GameDataCaches;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class RoomEventBootCache {
    private RoomEventBootCache() {
    }

    /**
     * Original function: Proc_1_8_6C6850.
     */
    public static void loadRoomEventLocalesCache() {
        SettingsDao settings = settingsDao();
        RoomEventLocales locales = GameDataCaches.roomEventLocales();
        if (settings != null) {
            try {
                locales = buildRoomEventLocales(settings.roomEventLocaleRows(), locales);
            } catch (Exception ignored) {
                // Legacy startup cache loading tolerated missing tables or SQL failures.
            }
        }
        GameDataCaches.setRoomEventLocales(locales);
    }

    /**
     * Original function: Proc_1_8_6C6850.
     */
    public static RoomEventLocales buildRoomEventLocales(List<SettingsDao.LocaleRow> localeRows,
            RoomEventLocales existingLocales) {
        return (existingLocales == null ? RoomEventLocales.empty() : existingLocales)
            .withEntries(roomEventLocaleEntries(localeRows));
    }

    /**
     * Original function: Proc_1_8_6C6850.
     */
    public static List<RoomEventLocales.LocaleEntry> roomEventLocaleEntries(List<SettingsDao.LocaleRow> localeRows) {
        List<RoomEventLocales.LocaleEntry> entries = new ArrayList<>();
        if (localeRows != null) {
            for (SettingsDao.LocaleRow row : localeRows) {
                if (row != null) {
                    String cacheKey = StringUtils.text(row.variableName()).replaceFirst("roomevent_type_", "");
                    if (!cacheKey.isEmpty()) {
                        entries.add(new RoomEventLocales.LocaleEntry(
                            String.valueOf(NumberUtils.parseLong(cacheKey)),
                            List.of(StringUtils.text(row.value()), "")));
                    }
                }
            }
        }
        return entries;
    }

    private static SettingsDao settingsDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new SettingsDao(database);
    }
}
