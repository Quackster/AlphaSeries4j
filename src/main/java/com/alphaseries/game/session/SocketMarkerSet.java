package com.alphaseries.game.session;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public final class SocketMarkerSet {
    private final Set<Long> socketIndexes = new LinkedHashSet<>();

    private SocketMarkerSet(Object markers) {
        parse(StringUtils.text(markers));
    }

    private SocketMarkerSet(Collection<Long> socketIndexes) {
        if (socketIndexes != null) {
            for (Long socketIndex : socketIndexes) {
                if (socketIndex != null && socketIndex > 0L) {
                    this.socketIndexes.add(socketIndex);
                }
            }
        }
    }

    public static SocketMarkerSet fromLegacy(Object markers) {
        if (markers instanceof SocketMarkerSet socketMarkers) {
            return socketMarkers;
        }
        if (markers instanceof Iterable<?> socketIndexes) {
            Set<Long> parsedSocketIndexes = new LinkedHashSet<>();
            for (Object socketIndex : socketIndexes) {
                parsedSocketIndexes.add(NumberUtils.parseLong(socketIndex));
            }
            return fromSocketIndexes(parsedSocketIndexes);
        }
        return new SocketMarkerSet(markers);
    }

    public static SocketMarkerSet fromSocketIndexes(Collection<Long> socketIndexes) {
        return new SocketMarkerSet(socketIndexes);
    }

    public static SocketMarkerSet empty() {
        return new SocketMarkerSet("");
    }

    public Set<Long> socketIndexes() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(socketIndexes));
    }

    public void add(long socketIndex) {
        if (socketIndex > 0L) {
            socketIndexes.add(socketIndex);
        }
    }

    public void remove(long socketIndex) {
        socketIndexes.remove(socketIndex);
    }

    public boolean contains(long socketIndex) {
        return socketIndexes.contains(socketIndex);
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
