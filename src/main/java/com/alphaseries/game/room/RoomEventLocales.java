package com.alphaseries.game.room;

import com.alphaseries.util.StringUtils;

public final class RoomEventLocales {
    private final String cacheText;

    private RoomEventLocales(String cacheText) {
        this.cacheText = StringUtils.text(cacheText);
    }

    public static RoomEventLocales fromLegacy(String cacheText) {
        return new RoomEventLocales(cacheText);
    }

    public String cacheText() {
        return cacheText;
    }

    public String field(String keyName, long columnIndex) {
        String key = StringUtils.text(keyName);
        if (cacheText.isEmpty() || key.isEmpty()) {
            return "";
        }
        String marker = "\0" + key + "\1";
        int markerAt = cacheText.indexOf(marker);
        if (markerAt < 0) {
            return "";
        }
        String row = cacheText.substring(markerAt + marker.length());
        int recordEnd = row.indexOf('\0');
        if (recordEnd >= 0) {
            row = row.substring(0, recordEnd);
        }
        String[] fields = row.split("\2", -1);
        return columnIndex >= 0 && columnIndex < fields.length ? fields[(int) columnIndex] : "";
    }
}
