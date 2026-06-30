package com.alphaseries.game.moderation;

import com.alphaseries.Functions;
import com.alphaseries.HandlingMUS;
import com.alphaseries.MySQL;
import com.alphaseries.dao.mysql.StaffModerationDao;
import com.alphaseries.dao.mysql.UserDao;
import com.alphaseries.db.Database;
import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.sql.SQLException;

public final class StaffModerationPacketHandlers {
    private StaffModerationPacketHandlers() {
    }

    public static void sendCallForHelpChatLog(Object... args) {
        try {
            int socketIndex = socketIndex(args);
            String requestPayload = requestPayload(packetPayload(args), "GI");
            String userId = userIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)
                || !userHasPermission(userId, "fuse_mod")
                || !userHasPermission(userId, "fuse_receive_calls_for_help")) {
                return;
            }
            long callForHelpId = NumberUtils.parseLong(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
            if (callForHelpId <= 0L) {
                return;
            }
            StaffModerationDao moderationDao = staffModerationDao();
            StaffModerationDao.CallForHelpRoom room = moderationDao.callForHelpRoom(callForHelpId).orElse(null);
            if (room == null) {
                return;
            }
            String chatRows = moderationDao.recentChatRowsBefore(room.roomId(), room.timestampSent());
            HandlingMUS.Proc_12_1_821AA0(
                socketIndex,
                "HV" + callForHelpChatLogPayload(callForHelpId, room, chatRows),
                0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void sendRoomChatLog(Object... args) {
        try {
            int socketIndex = socketIndex(args);
            String requestPayload = requestPayload(packetPayload(args), "GH");
            String userId = userIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)
                || !userHasPermission(userId, "fuse_mod")
                || !userHasPermission(userId, "fuse_chatlog")) {
                return;
            }
            long roomId = NumberUtils.parseLong(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
            if (roomId <= 0L) {
                return;
            }
            StaffModerationDao moderationDao = staffModerationDao();
            StaffModerationDao.RoomChatHeader room = moderationDao.roomChatHeader(roomId).orElse(null);
            if (room == null) {
                return;
            }
            String chatRows = moderationDao.recentChatRows(roomId);
            HandlingMUS.Proc_12_1_821AA0(socketIndex, "HW" + roomChatLogHeader(room) + roomChatLogRows(chatRows), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void sendRoomInfo(Object... args) {
        try {
            int socketIndex = socketIndex(args);
            String requestPayload = requestPayload(packetPayload(args), "GK");
            String userId = userIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId) || !userHasPermission(userId, "fuse_mod")) {
                return;
            }
            long roomId = NumberUtils.parseLong(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
            if (roomId <= 0L) {
                return;
            }
            StaffModerationDao moderationDao = staffModerationDao();
            StaffModerationDao.RoomInfo room = moderationDao.roomInfo(roomId).orElse(null);
            if (room == null) {
                return;
            }
            StaffModerationDao.RoomEvent event = moderationDao.roomEvent(roomId).orElse(null);
            HandlingMUS.Proc_12_1_821AA0(socketIndex, "HZ" + roomInfoPayload(room, event), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    private static String callForHelpChatLogPayload(
        long callForHelpId,
        StaffModerationDao.CallForHelpRoom room,
        String chatRows
    ) {
        return PacketBuilder.create()
            .appendInt(callForHelpId)
            .appendInt(room.roomId())
            .appendInt(room.modelType())
            .appendInt(room.userId())
            .appendInt(room.partnerId())
            .appendString(room.roomName())
            .appendRaw(roomChatLogRows(chatRows))
            .build();
    }

    private static String roomChatLogHeader(StaffModerationDao.RoomChatHeader room) {
        return PacketBuilder.create()
            .appendInt(room.roomId())
            .appendInt(room.modelType())
            .appendString(room.roomName())
            .build();
    }

    private static String roomChatLogRows(String chatRows) {
        if (chatRows == null || chatRows.isEmpty()) {
            return "";
        }
        PacketBuilder payload = PacketBuilder.create();
        for (String row : chatRows.split("\r", -1)) {
            if (!row.isEmpty()) {
                String[] fields = row.split("\t", -1);
                if (fields.length >= 5) {
                    payload.appendInt(NumberUtils.parseLong(fields[0]))
                        .appendInt(NumberUtils.parseLong(fields[1]))
                        .appendInt(NumberUtils.parseLong(fields[2]))
                        .appendString(fields[3])
                        .appendString(fields[4]);
                }
            }
        }
        return payload.build();
    }

    private static String roomInfoPayload(StaffModerationDao.RoomInfo room, StaffModerationDao.RoomEvent event) {
        PacketBuilder payload = PacketBuilder.create()
            .appendInt(room.roomId())
            .appendInt(room.visitorsNow())
            .appendInt(room.ownerId())
            .appendString(room.ownerName())
            .appendString(room.roomName())
            .appendString(room.description())
            .appendString(room.tag1())
            .appendString(room.tag2());

        boolean hasEvent = event != null;
        payload.appendBoolean(hasEvent);
        if (hasEvent) {
            payload.appendString(event.name())
                .appendString(event.description())
                .appendString(event.tag1())
                .appendString(event.tag2());
        }
        return payload.build();
    }

    private static StaffModerationDao staffModerationDao() {
        return new StaffModerationDao(MySQL.configuredDatabase());
    }

    private static UserDao userDao() {
        return new UserDao(MySQL.configuredDatabase());
    }

    private static int socketIndex(Object... args) {
        if (args == null || args.length == 0) {
            return 0;
        }
        return NumberUtils.parseInt(args[0]);
    }

    private static String packetPayload(Object... args) {
        if (args == null) {
            return "";
        }
        String payload = args.length >= 3 ? StringUtils.text(args[2]) : "";
        if (payload.isEmpty() && args.length >= 2) {
            payload = StringUtils.text(args[1]);
        }
        return payload;
    }

    private static String requestPayload(String packetPayload, String expectedPrefix) {
        String payload = StringUtils.text(packetPayload);
        String prefix = StringUtils.text(expectedPrefix);
        if (!prefix.isEmpty() && payload.startsWith(prefix)) {
            return payload.substring(prefix.length());
        }
        return payload;
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
            return Functions.Proc_10_1_809790(rankIndex, "", permissionName, hcLevel);
        } catch (SQLException ex) {
            return false;
        }
    }
}
