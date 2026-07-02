package com.alphaseries.game.moderation;

import com.alphaseries.config.AppConfigState;
import com.alphaseries.dao.mysql.StaffModerationDao;
import com.alphaseries.dao.mysql.UserDao;
import com.alphaseries.db.Database;
import com.alphaseries.db.MySQL;
import com.alphaseries.server.mus.MusConnectionManager;
import com.alphaseries.util.NumberUtils;

import java.sql.SQLException;
import java.util.List;

public final class StaffModerationPacketHandlers {
    private StaffModerationPacketHandlers() {
    }

    public static void sendCallForHelpChatLog(int socketIndex, StaffWire.CallForHelpChatLogRequest request) {
        try {
            String userId = userIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)
                || !userHasPermission(userId, "fuse_mod")
                || !userHasPermission(userId, "fuse_receive_calls_for_help")) {
                return;
            }
            long callForHelpId = request.callForHelpId();
            if (callForHelpId <= 0L) {
                return;
            }
            StaffModerationDao moderationDao = staffModerationDao();
            StaffModerationDao.CallForHelpRoom room = moderationDao.callForHelpRoom(callForHelpId).orElse(null);
            if (room == null) {
                return;
            }
            List<StaffRoomChatRow> chatRows = moderationDao.recentChatRowsBefore(room.roomId(), room.timestampSent());
            MusConnectionManager.instance().sendData(
                socketIndex,
                StaffPayloads.callForHelpChatLogResponse(callForHelpId, room, chatRows));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void sendRoomChatLog(int socketIndex, StaffWire.RoomChatLogRequest request) {
        try {
            String userId = userIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)
                || !userHasPermission(userId, "fuse_mod")
                || !userHasPermission(userId, "fuse_chatlog")) {
                return;
            }
            long roomId = request.roomId();
            if (roomId <= 0L) {
                return;
            }
            StaffModerationDao moderationDao = staffModerationDao();
            StaffModerationDao.RoomChatHeader room = moderationDao.roomChatHeader(roomId).orElse(null);
            if (room == null) {
                return;
            }
            List<StaffRoomChatRow> chatRows = moderationDao.recentChatRows(roomId);
            MusConnectionManager.instance().sendData(socketIndex, StaffPayloads.roomChatLogResponse(room, chatRows));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void sendRoomInfo(int socketIndex, StaffWire.RoomInfoRequest request) {
        try {
            String userId = userIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId) || !userHasPermission(userId, "fuse_mod")) {
                return;
            }
            long roomId = request.roomId();
            if (roomId <= 0L) {
                return;
            }
            StaffModerationDao moderationDao = staffModerationDao();
            StaffModerationDao.RoomInfo room = moderationDao.roomInfo(roomId).orElse(null);
            if (room == null) {
                return;
            }
            StaffModerationDao.RoomEvent event = moderationDao.roomEvent(roomId).orElse(null);
            MusConnectionManager.instance().sendData(socketIndex, StaffPayloads.roomInfoResponse(room, event));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    private static StaffModerationDao staffModerationDao() {
        return new StaffModerationDao(MySQL.configuredDatabase());
    }

    private static UserDao userDao() {
        return new UserDao(MySQL.configuredDatabase());
    }

    private static String userIdFromSocket(int socketIndex) {
        Database database = MySQL.configuredDatabase();
        if (socketIndex <= 0 || database == null) {
            return "";
        }
        try {
            long userId = userDao().userIdBySocket(socketIndex);
            return userId <= 0L ? "0" : String.valueOf(userId);
        } catch (SQLException ex) {
            return "";
        }
    }

    private static boolean userHasPermission(String userId, String permissionName) {
        if (MySQL.configuredDatabase() == null) {
            return false;
        }
        long numericUserId = NumberUtils.parseLong(userId);
        try {
            UserDao users = userDao();
            long rankIndex = users.rankLevel(numericUserId);
            long hcLevel = users.hcLevel(numericUserId);
            return AppConfigState.instance().permissionMatrix().allows(rankIndex, "", permissionName, hcLevel);
        } catch (SQLException ex) {
            return false;
        }
    }
}
