package com.alphaseries;

import com.alphaseries.vb.Vb;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
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
            Files.createDirectory(Path.of(Vb.cStr(args[0])));
        } catch (IOException ignored) {
            // VB6 source suppresses MkDir failures.
        }
    }

    public static void Proc_11_1_821240(Object... args) {
        if (args == null || args.length == 0) {
            return;
        }
        deleteRecursively(Path.of(Vb.cStr(args[0])));
    }

    public static int Proc_11_2_821390(Object... args) {
        if (gameServerConnected) {
            return 1;
        }
        if (args != null && args.length >= 1) {
            int socketIndex = (int) Vb.val(args[0]);
            return connectedSockets.contains(socketIndex) ? 1 : 0;
        }
        return 0;
    }

    public static void Proc_11_3_821440(Object... args) {
        if (args == null || args.length == 0) {
            return;
        }
        long socketIndexLong = Vb.val(args[0]);
        SocketMarkerState state = toggleSocketMarkerState(global_008291A0, global_0082919C, socketIndexLong);
        if (!state.accepted) {
            return;
        }
        int socketIndex = (int) socketIndexLong;
        global_008291A0 = state.markers;
        global_0082919C = state.highestIndex;
        connectedSockets.remove(socketIndex);
    }

    public static SocketMarkerState toggleSocketMarkerState(String existingMarkers, long highestIndex, long socketIndexLong) {
        SocketMarkerState state = new SocketMarkerState();
        state.markers = Vb.cStr(existingMarkers);
        state.highestIndex = highestIndex;
        if (socketIndexLong < 0L || socketIndexLong == 2500L) {
            return state;
        }
        state.accepted = true;
        int socketIndex = (int) socketIndexLong;
        String marker = "[" + socketIndex + "]";
        if (!state.markers.contains(marker)) {
            state.markers += marker;
            state.added = true;
            if (socketIndex > state.highestIndex) {
                state.highestIndex = socketIndex;
            }
        } else {
            state.markers = state.markers.replace(marker, "");
        }
        return state;
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
