package com.alphaseries;

import com.alphaseries.game.session.SocketMarkerSet;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Guardian {
    public static final int WINSOCK_CONNECTED_STATE = 7;
    public static String global_008291A0 = "";
    public static long global_0082919C = 0L;

    private static final Set<Integer> connectedSockets = new HashSet<Integer>();
    private static boolean gameServerConnected = false;

    private Guardian() {
    }

    public static final class SocketMarkerState {
        public String markers = "";
        public long highestIndex;
        public boolean accepted;
        public boolean added;
    }

    public static void Proc_11_0_821190(Object... args) {
        if (args == null || args.length == 0) {
            return;
        }
        try {
            Files.createDirectory(Path.of(StringUtils.text(args[0])));
        } catch (IOException ignored) {
            // VB6 source suppresses MkDir failures.
        }
    }

    public static void Proc_11_1_821240(Object... args) {
        if (args == null || args.length == 0) {
            return;
        }
        deleteDirectory(Path.of(StringUtils.text(args[0])));
    }

    /**
     * Original function: Proc_11_1_821240.
     */
    public static void deleteDirectory(Path path) {
        deleteRecursively(path);
    }

    public static int Proc_11_2_821390(Object... args) {
        if (args == null || args.length == 0) {
            return isSocketConnected(0L) ? 1 : 0;
        }
        return isSocketConnected(NumberUtils.parseLong(args[0])) ? 1 : 0;
    }

    /**
     * Original function: Proc_11_2_821390.
     */
    public static boolean isSocketConnected(long socketIndex) {
        if (gameServerConnected) {
            return true;
        }
        return connectedSockets.contains((int) socketIndex);
    }

    public static void Proc_11_3_821440(Object... args) {
        if (args == null || args.length == 0) {
            return;
        }
        toggleSocketMarker(NumberUtils.parseLong(args[0]));
    }

    /**
     * Original function: Proc_11_3_821440.
     */
    public static void toggleSocketMarker(long socketIndex) {
        SocketMarkerState state = toggleSocketMarkerState(global_008291A0, global_0082919C, socketIndex);
        if (!state.accepted) {
            return;
        }
        global_008291A0 = state.markers;
        global_0082919C = state.highestIndex;
        connectedSockets.remove((int) socketIndex);
    }

    public static SocketMarkerState toggleSocketMarkerState(String existingMarkers, long highestIndex, long socketIndexLong) {
        SocketMarkerState state = new SocketMarkerState();
        SocketMarkerSet socketMarkers = SocketMarkerSet.fromLegacy(existingMarkers);
        state.highestIndex = highestIndex;
        if (socketIndexLong < 0L || socketIndexLong == 2500L) {
            state.markers = socketMarkers.toLegacyMarkers();
            return state;
        }
        state.accepted = true;
        int socketIndex = (int) socketIndexLong;
        if (!socketMarkers.contains(socketIndex)) {
            socketMarkers.add(socketIndex);
            state.added = true;
            if (socketIndex > state.highestIndex) {
                state.highestIndex = socketIndex;
            }
        } else {
            socketMarkers.remove(socketIndex);
        }
        state.markers = socketMarkers.toLegacyMarkers();
        return state;
    }

    public static List<Long> markedSocketIndexes() {
        List<Long> socketIndexes = new ArrayList<>();
        SocketMarkerSet socketMarkers = SocketMarkerSet.fromLegacy(global_008291A0);
        for (long socketIndex = 1L; socketIndex <= global_0082919C; socketIndex++) {
            if (socketMarkers.contains(socketIndex)) {
                socketIndexes.add(socketIndex);
            }
        }
        return socketIndexes;
    }

    public static void removeSocketMarker(long socketIndex) {
        SocketMarkerSet socketMarkers = SocketMarkerSet.fromLegacy(global_008291A0);
        socketMarkers.remove(socketIndex);
        global_008291A0 = socketMarkers.toLegacyMarkers();
    }

    public static void addSocketMarker(long socketIndex) {
        SocketMarkerSet socketMarkers = SocketMarkerSet.fromLegacy(global_008291A0);
        socketMarkers.add(socketIndex);
        global_008291A0 = socketMarkers.toLegacyMarkers();
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
