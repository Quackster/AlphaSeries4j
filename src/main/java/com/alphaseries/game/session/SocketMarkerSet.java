package com.alphaseries.game.session;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public final class SocketMarkerSet {
    private final Set<Long> socketIndexes = new LinkedHashSet<>();

    private SocketMarkerSet(Collection<Long> socketIndexes) {
        if (socketIndexes != null) {
            for (Long socketIndex : socketIndexes) {
                if (socketIndex != null && socketIndex > 0L) {
                    this.socketIndexes.add(socketIndex);
                }
            }
        }
    }

    public static SocketMarkerSet fromSocketIndexes(Collection<Long> socketIndexes) {
        return new SocketMarkerSet(socketIndexes);
    }

    public static SocketMarkerSet empty() {
        return new SocketMarkerSet(Set.of());
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

}
