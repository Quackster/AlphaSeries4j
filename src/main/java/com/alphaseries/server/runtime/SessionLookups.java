package com.alphaseries.server.runtime;

import com.alphaseries.dao.mysql.DaoProvider;
import com.alphaseries.dao.mysql.RoomDao;
import com.alphaseries.dao.mysql.UserDao;
import com.alphaseries.game.session.SessionState;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

public final class SessionLookups {
    private SessionLookups() {
    }

    public static String userIdTextFromSocket(int socketIndex) {
        if (socketIndex <= 0) {
            return "";
        }
        long sessionUserId = SessionState.instance().sessionUserIdBySocket(socketIndex);
        if (sessionUserId > 0L) {
            return String.valueOf(sessionUserId);
        }
        UserDao users = userDao();
        if (users == null) {
            return "";
        }
        try {
            return String.valueOf(users.userIdBySocket(socketIndex));
        } catch (Exception ignored) {
            return "";
        }
    }

    public static int socketFromUserIdText(String userId) {
        String idText = String.valueOf(NumberUtils.parseLong(userId));
        if (idText.isEmpty() || "0".equals(idText)) {
            return 0;
        }
        long socketIndex = SessionState.instance().linkedUserSocketIndex(idText);
        if (socketIndex <= 0L) {
            UserDao users = userDao();
            if (users != null) {
                try {
                    socketIndex = users.socketByUserId(NumberUtils.parseLong(idText));
                } catch (Exception ignored) {
                    socketIndex = 0L;
                }
            }
        }
        return (int) socketIndex;
    }

    public static long currentRoomId(int socketIndex, String userId) {
        long roomId = SessionState.instance().sessionCacheLong(socketIndex, 1);
        if (roomId > 0L) {
            return roomId;
        }
        if (!StringUtils.text(userId).isEmpty() && !"0".equals(StringUtils.text(userId))) {
            RoomDao rooms = roomDao();
            if (rooms != null) {
                try {
                    roomId = rooms.currentRoomIdByUser(NumberUtils.parseLong(userId));
                } catch (Exception ignored) {
                    roomId = 0L;
                }
            }
        }
        if (roomId <= 0L) {
            RoomDao rooms = roomDao();
            if (rooms != null) {
                try {
                    roomId = rooms.roomIdBySlot(socketIndex);
                } catch (Exception ignored) {
                    roomId = 0L;
                }
            }
        }
        return roomId;
    }

    public static long representedRoomUserIndex(int socketIndex, String userId) {
        long roomUserIndex = 0L;
        RoomDao rooms = roomDao();
        if (rooms != null) {
            try {
                roomUserIndex = rooms.activeVisitIdByUser(NumberUtils.parseLong(userId));
            } catch (Exception ignored) {
                roomUserIndex = 0L;
            }
        }
        return roomUserIndex > 0L ? roomUserIndex : socketIndex;
    }

    private static UserDao userDao() {
        return DaoProvider.userDao();
    }

    private static RoomDao roomDao() {
        return DaoProvider.roomDao();
    }
}
