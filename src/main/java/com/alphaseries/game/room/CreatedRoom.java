package com.alphaseries.game.room;

import com.alphaseries.util.StringUtils;

import java.util.Iterator;
import java.util.List;

public record CreatedRoom(long roomId, String roomName, String payload, CacheInvalidation cacheInvalidation) {
    public CreatedRoom {
        roomName = StringUtils.text(roomName);
        payload = StringUtils.text(payload);
        cacheInvalidation = cacheInvalidation == null ? CacheInvalidation.empty() : cacheInvalidation;
    }

    public CreatedRoom(long roomId, String roomName, String payload) {
        this(roomId, roomName, payload, CacheInvalidation.forRoom(roomId));
    }

    public static CreatedRoom empty() {
        return new CreatedRoom(0L, "", "", CacheInvalidation.empty());
    }

    public boolean valid() {
        return roomId > 0L && !payload.isEmpty();
    }

    public void invalidateCaches() {
        RoomCacheFiles.deleteAll(cacheInvalidation);
    }

    public record CacheInvalidation(String roomPath, String pathfinderPath) implements Iterable<String> {
        public CacheInvalidation {
            roomPath = StringUtils.text(roomPath);
            pathfinderPath = StringUtils.text(pathfinderPath);
        }

        public static CacheInvalidation empty() {
            return new CacheInvalidation("", "");
        }

        public static CacheInvalidation forRoom(long roomId) {
            RoomCacheFiles.CachePaths paths = RoomCacheFiles.roomAndPathfinderPaths(roomId);
            return new CacheInvalidation(paths.roomPath(), paths.pathfinderPath());
        }

        public int size() {
            int count = 0;
            if (!roomPath.isEmpty()) {
                count++;
            }
            if (!pathfinderPath.isEmpty()) {
                count++;
            }
            return count;
        }

        @Override
        public Iterator<String> iterator() {
            if (roomPath.isEmpty() && pathfinderPath.isEmpty()) {
                return List.<String>of().iterator();
            }
            if (roomPath.isEmpty()) {
                return List.of(pathfinderPath).iterator();
            }
            if (pathfinderPath.isEmpty()) {
                return List.of(roomPath).iterator();
            }
            return List.of(roomPath, pathfinderPath).iterator();
        }
    }
}
