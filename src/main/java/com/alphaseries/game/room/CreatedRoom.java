package com.alphaseries.game.room;

import com.alphaseries.util.StringUtils;

import java.util.List;

public record CreatedRoom(long roomId, String roomName, String payload, List<String> cacheInvalidationPaths) {
    public CreatedRoom {
        roomName = StringUtils.text(roomName);
        payload = StringUtils.text(payload);
        cacheInvalidationPaths = List.copyOf(cacheInvalidationPaths == null ? List.of() : cacheInvalidationPaths);
    }

    public CreatedRoom(long roomId, String roomName, String payload) {
        this(roomId, roomName, payload, RoomCacheFiles.roomAndPathfinderPaths(roomId));
    }

    public static CreatedRoom empty() {
        return new CreatedRoom(0L, "", "", List.of());
    }

    public boolean valid() {
        return roomId > 0L && !payload.isEmpty();
    }

    public void invalidateCaches() {
        RoomCacheFiles.deleteAll(cacheInvalidationPaths);
    }
}
