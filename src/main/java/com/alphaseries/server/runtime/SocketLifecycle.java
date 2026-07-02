package com.alphaseries.server.runtime;

import com.alphaseries.dao.mysql.DaoProvider;
import com.alphaseries.dao.mysql.UserDao;
import com.alphaseries.game.session.GameServerSessionState;
import com.alphaseries.game.session.SessionState;
import com.alphaseries.game.session.SocketMarkerSet;
import com.alphaseries.util.NumberUtils;

public final class SocketLifecycle {
    private SocketLifecycle() {
    }

    /**
     * Original function: Proc_6_243_7FFEB0.
     */
    public static void disconnectSocket(long socketIndex) {
        if (socketIndex <= 0) {
            return;
        }
        int socketIndexValue = (int) socketIndex;
        clearSocketUser(socketIndexValue);
        Guardian.setSocketConnected(socketIndexValue, false);
        Guardian.removeSocketMarker(socketIndexValue);
        SocketMarkerSet socketMarkers = SessionState.instance().socketMarkers();
        socketMarkers.remove(socketIndexValue);
        SessionState.instance().setSocketMarkers(socketMarkers);
        GameServerSessionState sessionState = SessionState.instance().gameServerSession();
        sessionState.removeSocket(socketIndexValue);
        SessionState.instance().setGameServerSession(sessionState);
    }

    /**
     * Original function: Proc_6_242_7FF0D0.
     */
    private static String clearSocketUser(int socketIndex) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (!userId.isEmpty() && !"0".equals(userId)) {
                UserDao users = userDao();
                if (users != null) {
                    users.clearSocket(NumberUtils.parseLong(userId));
                }
            }
            return "";
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    private static UserDao userDao() {
        return DaoProvider.userDao();
    }
}
