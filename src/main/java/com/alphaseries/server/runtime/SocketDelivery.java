package com.alphaseries.server.runtime;

import com.alphaseries.dao.mysql.DaoProvider;
import com.alphaseries.dao.mysql.RoomDao;
import com.alphaseries.game.session.SessionState;
import com.alphaseries.server.mus.MusConnectionManager;
import com.alphaseries.util.StringUtils;

import java.util.LinkedHashSet;
import java.util.Set;

public final class SocketDelivery {
    private SocketDelivery() {
    }

    /**
     * Original function: Proc_6_244_801E80.
     */
    public static void sendToSocket(int socketIndex, String payload) {
        if (socketIndex <= 0 || !Guardian.isSocketConnected(socketIndex)
            || SessionState.instance().representedSockets().isBusy(socketIndex)) {
            return;
        }
        MusConnectionManager.instance().sendData(socketIndex, StringUtils.text(payload) + '\1');
    }

    public static void sendToSocket(int socketIndex, Iterable<String> payloads) {
        if (payloads == null) {
            return;
        }
        for (String payload : payloads) {
            sendToSocket(socketIndex, payload);
        }
    }

    /**
     * Original function: Proc_6_245_801FA0.
     * Original function: Proc_6_247_8027E0.
     */
    public static long broadcastToCurrentRoom(int socketIndex, String payload) {
        String userId = SessionLookups.userIdTextFromSocket(socketIndex);
        long roomId = SessionLookups.currentRoomId(socketIndex, userId);
        return broadcastToRoomUsers(roomId, payload);
    }

    /**
     * Original function: Proc_6_246_8024C0.
     * Original function: Proc_6_248_802B80.
     */
    public static long broadcastToRoomUsers(long roomId, String payload) {
        if (roomId <= 0L || StringUtils.text(payload).isEmpty()) {
            return 0L;
        }
        try {
            RoomDao rooms = roomDao();
            if (rooms == null) {
                return 0L;
            }
            Set<Integer> sentSockets = new LinkedHashSet<>();
            long sentCount = 0L;
            for (Long activeSocketIndex : rooms.activeSocketIndexesByRoomWithFallback(roomId)) {
                int socketIndex = activeSocketIndex == null ? 0 : activeSocketIndex.intValue();
                if (socketIndex > 0 && sentSockets.add(socketIndex)) {
                    sendToSocket(socketIndex, payload);
                    sentCount++;
                }
            }
            return sentCount;
        } catch (Exception ignored) {
            return 0L;
        }
    }

    private static RoomDao roomDao() {
        return DaoProvider.roomDao();
    }
}
