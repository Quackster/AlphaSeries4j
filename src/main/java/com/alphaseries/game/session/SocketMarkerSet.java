package com.alphaseries.game.session;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.LinkedHashSet;
import java.util.Set;

public final class SocketMarkerSet {
    private final Set<Long> socketIndexes = new LinkedHashSet<>();

    private SocketMarkerSet(String markers) {
        parse(StringUtils.text(markers));
    }

    public static SocketMarkerSet fromLegacy(String markers) {
        return new SocketMarkerSet(markers);
    }

    public void remove(long socketIndex) {
        socketIndexes.remove(socketIndex);
    }

    public String toLegacyMarkers() {
        StringBuilder markers = new StringBuilder();
        for (Long socketIndex : socketIndexes) {
            markers.append('[').append(socketIndex).append(']');
        }
        return markers.toString();
    }

    private void parse(String markers) {
        if (markers.isEmpty()) {
            return;
        }
        for (String part : markers.split("\\]", -1)) {
            long socketIndex = NumberUtils.parseLong(part.replace("[", ""));
            if (socketIndex > 0L) {
                socketIndexes.add(socketIndex);
            }
        }
    }
}
