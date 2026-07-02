package com.alphaseries.game.room;

import com.alphaseries.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class RoomEventLocales {
    private final Map<String, List<String>> fieldsByKey;

    private RoomEventLocales(Map<String, List<String>> fieldsByKey) {
        this.fieldsByKey = copyFields(fieldsByKey);
    }

    public static RoomEventLocales fromEntries(List<LocaleEntry> entries) {
        Map<String, List<String>> fields = new LinkedHashMap<>();
        appendEntries(fields, entries);
        return new RoomEventLocales(fields);
    }

    public static RoomEventLocales empty() {
        return new RoomEventLocales(Map.of());
    }

    public RoomEventLocales withEntries(List<LocaleEntry> entries) {
        Map<String, List<String>> fields = copyFields(fieldsByKey);
        appendEntries(fields, entries);
        return new RoomEventLocales(fields);
    }

    public String categoryName(long categoryId) {
        return field(String.valueOf(categoryId), 0);
    }

    private String field(String keyName, long columnIndex) {
        String key = StringUtils.text(keyName);
        if (key.isEmpty()) {
            return "";
        }
        List<String> fields = fieldsByKey.get(key);
        if (fields == null || columnIndex < 0 || columnIndex >= fields.size()) {
            return "";
        }
        return fields.get((int) columnIndex);
    }

    public List<LocaleEntry> entries() {
        return fieldsByKey.entrySet().stream()
            .map(entry -> new LocaleEntry(entry.getKey(), entry.getValue()))
            .toList();
    }

    private static void appendEntries(Map<String, List<String>> fieldsByKey, List<LocaleEntry> entries) {
        if (entries == null) {
            return;
        }
        for (LocaleEntry entry : entries) {
            if (entry != null && !StringUtils.text(entry.key()).isEmpty()) {
                fieldsByKey.put(StringUtils.text(entry.key()), List.copyOf(entry.fields()));
            }
        }
    }

    private static Map<String, List<String>> copyFields(Map<String, List<String>> source) {
        Map<String, List<String>> fields = new LinkedHashMap<>();
        if (source != null) {
            for (Map.Entry<String, List<String>> entry : source.entrySet()) {
                if (entry.getKey() != null) {
                    fields.put(entry.getKey(), entry.getValue() == null ? List.of() : List.copyOf(entry.getValue()));
                }
            }
        }
        return fields;
    }

    public record LocaleEntry(String key, List<String> fields) {
        public LocaleEntry {
            key = StringUtils.text(key);
            fields = fields == null ? List.of() : List.copyOf(fields);
        }
    }
}
