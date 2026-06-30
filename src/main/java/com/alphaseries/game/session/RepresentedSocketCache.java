package com.alphaseries.game.session;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

public final class RepresentedSocketCache {
    private final String cacheText;

    private RepresentedSocketCache(Object cacheText) {
        this.cacheText = StringUtils.text(cacheText);
    }

    public static RepresentedSocketCache fromLegacy(Object cacheText) {
        if (cacheText instanceof RepresentedSocketCache representedSocketCache) {
            return representedSocketCache;
        }
        return new RepresentedSocketCache(cacheText);
    }

    public static RepresentedSocketCache empty() {
        return new RepresentedSocketCache("");
    }

    public String record(long socketIndex) {
        if (socketIndex <= 0L || cacheText.isEmpty()) {
            return "";
        }
        String markerText = "[" + socketIndex + "]";
        int startAt = cacheText.indexOf(markerText);
        if (startAt < 0) {
            return "";
        }
        startAt += markerText.length();
        int endAt = cacheText.indexOf('[', startAt);
        if (endAt < 0) {
            endAt = cacheText.length();
        }
        return cacheText.substring(startAt, endAt);
    }

    public long roomSlot(long socketIndex) {
        String[] fields = record(socketIndex).split("\2", -1);
        return fields.length >= 2 ? NumberUtils.parseLong(fields[1]) : 0L;
    }

    public boolean isBusy(long socketIndex) {
        String[] fields = record(socketIndex).split("\2", -1);
        return fields.length >= 6 && NumberUtils.parseLong(fields[5]) != 0L;
    }
}
