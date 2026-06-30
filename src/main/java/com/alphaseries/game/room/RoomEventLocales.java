package com.alphaseries.game.room;

import com.alphaseries.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class RoomEventLocales {
    private final Map<String, List<String>> fieldsByKey;

    private RoomEventLocales(String cacheText) {
        this.fieldsByKey = parseFields(cacheText);
    }

    public static RoomEventLocales fromLegacy(String cacheText) {
        return new RoomEventLocales(cacheText);
    }

    public String cacheText() {
        StringBuilder cache = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : fieldsByKey.entrySet()) {
            cache.append('\0').append(entry.getKey()).append('\1')
                .append(String.join("\2", entry.getValue()));
        }
        return cache.toString();
    }

    public String field(String keyName, long columnIndex) {
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

    private static Map<String, List<String>> parseFields(String cacheText) {
        Map<String, List<String>> fieldsByKey = new LinkedHashMap<>();
        for (String record : StringUtils.text(cacheText).split("\0", -1)) {
            int separatorAt = record.indexOf('\1');
            if (separatorAt <= 0) {
                continue;
            }
            String key = record.substring(0, separatorAt);
            String fields = record.substring(separatorAt + 1);
            fieldsByKey.put(key, List.of(fields.split("\2", -1)));
        }
        return fieldsByKey;
    }
}
