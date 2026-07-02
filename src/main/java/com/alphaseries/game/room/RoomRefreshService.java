package com.alphaseries.game.room;

import com.alphaseries.dao.mysql.RoomDao;
import com.alphaseries.db.Database;
import com.alphaseries.db.MySQL;
import com.alphaseries.game.session.SessionState;
import com.alphaseries.messages.outgoing.UserPayloads;
import com.alphaseries.server.mus.MusConnectionManager;
import com.alphaseries.server.packet.Filesystems;

import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;

public final class RoomRefreshService {
    private RoomRefreshService() {
    }

    /**
     * Original function: Proc_10_12_80ADB0.
     */
    public static long sendRoomAlert(String messageText, String linkText) {
        return Filesystems.broadcastToActiveSessions(UserPayloads.roomAlert(messageText, linkText), "");
    }

    /**
     * Original function: Proc_10_18_80C9E0.
     */
    public static void sendRoomReady(int socketIndex) {
        if (socketIndex <= 0) {
            return;
        }
        MusConnectionManager.instance().sendData(socketIndex, "@R");
    }

    public static long sendRoomReadyRefreshes(long roomId) {
        try {
            if (roomId <= 0L) {
                return 0L;
            }
            Set<Integer> sentSockets = new LinkedHashSet<>();
            long readyCount = 0L;
            for (Long activeSocketIndex : roomDao().activeSocketIndexesByRoom(roomId)) {
                int socketIndex = activeSocketIndex == null ? 0 : activeSocketIndex.intValue();
                if (socketIndex > 0 && sentSockets.add(socketIndex)) {
                    sendRoomReady(socketIndex);
                    readyCount++;
                }
            }
            return readyCount;
        } catch (Exception ex) {
            return 0L;
        }
    }

    public static String roomAlertPayload(String alertType, String alertText) {
        return UserPayloads.roomAlert(alertType, alertText);
    }

    /**
     * Original function: Proc_10_20_80CF60.
     */
    public static long sendUserRoomAlert(long userId, String alertType, String alertText) {
        try {
            if (userId <= 0L) {
                return 0L;
            }
            long socketIndex = SessionState.instance().linkedSocketIndex(userId);
            if (socketIndex <= 0L) {
                return 0L;
            }
            MusConnectionManager.instance().sendData((int) socketIndex,
                roomAlertPayload(alertType, alertText));
            return 1L;
        } catch (Exception ex) {
            return 0L;
        }
    }

    /**
     * Original function: Proc_10_21_80D0A0.
     */
    public static long sendRoomAlertToRoom(long roomId, String alertType, String alertText) {
        try {
            if (roomId <= 0L) {
                return 0L;
            }
            String payload = roomAlertPayload(alertType, alertText);
            Set<Integer> sentSockets = new LinkedHashSet<>();
            long sentCount = 0L;
            for (Long activeSocketIndex : roomDao().activeSocketIndexesByRoom(roomId)) {
                int socketIndex = activeSocketIndex == null ? 0 : activeSocketIndex.intValue();
                if (socketIndex > 0 && sentSockets.add(socketIndex)) {
                    MusConnectionManager.instance().sendData(socketIndex, payload);
                    sentCount++;
                }
            }
            return sentCount;
        } catch (Exception ex) {
            return 0L;
        }
    }

    private static RoomDao roomDao() throws SQLException {
        return new RoomDao(configuredDatabase());
    }

    private static Database configuredDatabase() throws SQLException {
        if (MySQL.configuredDatabase() == null) {
            throw new SQLException("Database is not configured.");
        }
        return MySQL.configuredDatabase();
    }
}
