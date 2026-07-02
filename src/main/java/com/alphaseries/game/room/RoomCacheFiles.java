package com.alphaseries.game.room;

import com.alphaseries.config.AppPaths;
import com.alphaseries.util.FileUtils;

import java.nio.file.Path;
import java.util.List;

public final class RoomCacheFiles {
    private RoomCacheFiles() {
    }

    public static List<String> roomAndPathfinderPaths(long roomId) {
        if (roomId <= 0L) {
            return List.of();
        }
        return List.of(
            roomPath(roomId),
            pathfinderPath(roomId));
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

    public static void deleteAll(List<String> paths) {
        if (paths == null) {
            return;
        }
        for (String path : paths) {
            FileUtils.deleteFile(path);
        }
    }
}
