package com.alphaseries.game.room;

import com.alphaseries.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class RoomEventLocales {
    private final Map<String, LocaleEntry> entriesByKey;

    private RoomEventLocales(Map<String, LocaleEntry> entriesByKey) {
        this.entriesByKey = copyEntries(entriesByKey);
    }

    public static RoomEventLocales fromEntries(List<LocaleEntry> entries) {
        Map<String, LocaleEntry> locales = new LinkedHashMap<>();
        appendEntries(locales, entries);
        return new RoomEventLocales(locales);
    }

    public static RoomEventLocales empty() {
        return new RoomEventLocales(Map.of());
    }

    public RoomEventLocales withEntries(List<LocaleEntry> entries) {
        Map<String, LocaleEntry> locales = copyEntries(entriesByKey);
        appendEntries(locales, entries);
        return new RoomEventLocales(locales);
    }

    public String categoryName(long categoryId) {
        LocaleEntry entry = entriesByKey.get(String.valueOf(categoryId));
        return entry == null ? "" : entry.categoryName();
    }

    public List<LocaleEntry> entries() {
        return List.copyOf(entriesByKey.values());
    }

    private static void appendEntries(Map<String, LocaleEntry> entriesByKey, List<LocaleEntry> entries) {
        if (entries == null) {
            return;
        }
        for (LocaleEntry entry : entries) {
            if (entry != null && !StringUtils.text(entry.key()).isEmpty()) {
                entriesByKey.put(StringUtils.text(entry.key()), entry);
            }
        }
    }

    private static Map<String, LocaleEntry> copyEntries(Map<String, LocaleEntry> source) {
        Map<String, LocaleEntry> entries = new LinkedHashMap<>();
        if (source != null) {
            for (Map.Entry<String, LocaleEntry> entry : source.entrySet()) {
                if (entry.getValue() != null && !StringUtils.text(entry.getKey()).isEmpty()) {
                    entries.put(StringUtils.text(entry.getKey()), entry.getValue());
                }
            }
        }
        return entries;
    }

    public record LocaleEntry(String key, String categoryName, String reservedText) {
        public LocaleEntry {
            key = StringUtils.text(key);
            categoryName = StringUtils.text(categoryName);
            reservedText = StringUtils.text(reservedText);
        }
    }
}
