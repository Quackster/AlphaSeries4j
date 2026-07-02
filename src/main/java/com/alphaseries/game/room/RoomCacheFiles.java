package com.alphaseries.game.room;

import com.alphaseries.config.AppPaths;
import com.alphaseries.util.FileUtils;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

public final class RoomCacheFiles {
    private RoomCacheFiles() {
    }

    public static CachePaths roomAndPathfinderPaths(long roomId) {
        if (roomId <= 0L) {
            return CachePaths.empty();
        }
        return new CachePaths(
            roomPath(roomId),
            pathfinderPath(roomId));
    }

    public record CachePaths(String roomPath, String pathfinderPath) implements Iterable<String> {
        public CachePaths {
            roomPath = roomPath == null ? "" : roomPath;
            pathfinderPath = pathfinderPath == null ? "" : pathfinderPath;
        }

        public static CachePaths empty() {
            return new CachePaths("", "");
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

    private static String roomPath(long roomId) {
        return roomId <= 0L ? "" : Path.of(AppPaths.applicationPath(), "CACHE", "ROOMS", roomId + ".cache").toString();
    }

    private static String pathfinderPath(long roomId) {
        return roomId <= 0L ? "" : Path.of(AppPaths.applicationPath(), "CACHE", "PATHFINDER", roomId + ".cache").toString();
    }

    public static void invalidateRoom(long roomId) {
        deleteAll(roomAndPathfinderPaths(roomId));
    }

    public static void invalidateRoomPayload(long roomId) {
        String path = roomPath(roomId);
        if (!path.isEmpty()) {
            FileUtils.deleteFile(path);
        }
    }

    public static void deleteAll(Iterable<String> paths) {
        if (paths == null) {
            return;
        }
        for (String path : paths) {
            FileUtils.deleteFile(path);
        }
    }
}
