package com.alphaseries.server.runtime;

import com.alphaseries.game.session.SocketMarkerSet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Guardian {
    public static final int WINSOCK_CONNECTED_STATE = 7;

    private static final Set<Integer> connectedSockets = new HashSet<Integer>();
    private static SocketMarkerSet socketMarkers = SocketMarkerSet.empty();
    private static long highestMarkedSocketIndex = 0L;
    private static boolean gameServerConnected = false;

    private Guardian() {
    }

    public record SocketMarkerState(SocketMarkerSet markers, long highestIndex, boolean accepted, boolean added) {
        public static SocketMarkerState rejected(SocketMarkerSet markers, long highestIndex) {
            return new SocketMarkerState(markers == null ? SocketMarkerSet.empty() : markers, highestIndex, false, false);
        }
    }

    public static void createDirectory(Path path) {
        try {
            Files.createDirectory(path);
        } catch (IOException ignored) {
            // VB6 source suppresses MkDir failures.
        }
    }

    public static void deleteDirectory(Path path) {
        deleteRecursively(path);
    }

    public static boolean isSocketConnected(long socketIndex) {
        if (gameServerConnected) {
            return true;
        }
        return connectedSockets.contains((int) socketIndex);
    }

    public static void toggleSocketMarker(long socketIndex) {
        SocketMarkerState state = toggleSocketMarkerState(socketMarkers, highestMarkedSocketIndex, socketIndex);
        if (!state.accepted()) {
            return;
        }
        socketMarkers = state.markers();
        highestMarkedSocketIndex = state.highestIndex();
        connectedSockets.remove((int) socketIndex);
    }

    public static SocketMarkerState toggleSocketMarkerState(SocketMarkerSet existingMarkers, long highestIndex, long socketIndexLong) {
        SocketMarkerSet socketMarkers = existingMarkers == null ? SocketMarkerSet.empty() : existingMarkers;
        if (socketIndexLong < 0L || socketIndexLong == 2500L) {
            return SocketMarkerState.rejected(socketMarkers, highestIndex);
        }
        int socketIndex = (int) socketIndexLong;
        boolean added = false;
        if (!socketMarkers.contains(socketIndex)) {
            socketMarkers.add(socketIndex);
            added = true;
            highestIndex = Math.max(highestIndex, socketIndex);
        } else {
            socketMarkers.remove(socketIndex);
        }
        return new SocketMarkerState(socketMarkers, highestIndex, true, added);
    }

    public static List<Long> markedSocketIndexes() {
        List<Long> socketIndexes = new ArrayList<>();
        SocketMarkerSet socketMarkers = socketMarkers();
        for (long socketIndex = 1L; socketIndex <= highestMarkedSocketIndex(); socketIndex++) {
            if (socketMarkers.contains(socketIndex)) {
                socketIndexes.add(socketIndex);
            }
        }
        return socketIndexes;
    }

    public static void removeSocketMarker(long socketIndex) {
        Guardian.socketMarkers.remove(socketIndex);
    }

    public static void addSocketMarker(long socketIndex) {
        socketMarkers.add(socketIndex);
        if (socketIndex > highestMarkedSocketIndex) {
            highestMarkedSocketIndex = socketIndex;
        }
    }

    public static SocketMarkerSet socketMarkers() {
        return socketMarkers;
    }

    public static void setSocketMarkers(SocketMarkerSet socketMarkers) {
        Guardian.socketMarkers = socketMarkers == null ? SocketMarkerSet.empty() : socketMarkers;
        highestMarkedSocketIndex = highestSocketIndex(socketMarkers);
    }

    public static long highestMarkedSocketIndex() {
        return highestMarkedSocketIndex;
    }

    public static void clearSocketMarkers() {
        setSocketMarkers(SocketMarkerSet.empty());
    }

    private static long highestSocketIndex(SocketMarkerSet socketMarkers) {
        long highestIndex = 0L;
        if (socketMarkers != null) {
            for (long socketIndex : socketMarkers.socketIndexes()) {
                if (socketIndex > highestIndex) {
                    highestIndex = socketIndex;
                }
            }
        }
        return highestIndex;
    }

    public static void setGameServerConnected(boolean connected) {
        gameServerConnected = connected;
    }

    public static void setSocketConnected(int socketIndex, boolean connected) {
        if (connected) {
            connectedSockets.add(socketIndex);
        } else {
            connectedSockets.remove(socketIndex);
        }
    }

    private static void deleteRecursively(Path path) {
        if (!Files.exists(path)) {
            return;
        }
        try {
            if (Files.isDirectory(path)) {
                java.util.List<Path> children = Files.list(path).collect(java.util.stream.Collectors.toList());
                for (Path child : children) {
                    deleteRecursively(child);
                }
            }
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
            // VB6 source suppresses delete failures.
        }
    }
}
