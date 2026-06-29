package com.alphaseries;

import com.alphaseries.db.Database;
import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.vb.Vb;

import java.sql.SQLException;
import java.util.List;

public final class MySQL {
    private static Database databaseConnection;

    private MySQL() {
    }

    public static void configureDatabaseConnection(Database database) {
        databaseConnection = database;
    }

    public static Database configuredDatabase() {
        return databaseConnection;
    }

    public static void Proc_5_0_6D3CD0(Object... args) {
        executeSql(buildSqlFromArgs(args));
    }

    public static void Proc_5_1_6D4110(Object... args) {
        executeSql(buildSqlFromArgs(args));
    }

    public static String Proc_5_2_6D4690(Object... args) {
        return readSqlRows(buildSqlFromArgs(args));
    }

    public static String Proc_5_3_6D4CF0(Object... args) {
        return readSqlRows(buildSqlFromArgs(args));
    }

    public static void Proc_5_4_6D55E0(Object... args) {
        try {
            int socketIndex = mySqlSocketIndex(args);
            String requestPayload = mySqlRequestPayload(mySqlPacketPayload(args), "GI");
            String userId = mySqlUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)
                || !mySqlUserHasPermission(userId, "fuse_mod")
                || !mySqlUserHasPermission(userId, "fuse_receive_calls_for_help")) {
                return;
            }
            long cfhId = Vb.val(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
            if (cfhId <= 0L) {
                return;
            }
            String cfhRow = Proc_5_2_6D4690("SELECT rooms.id,rooms.name,models.type,staff_cfh.id_user,staff_cfh.id_partner,staff_cfh.timestamp_sent FROM rooms,models,staff_cfh WHERE staff_cfh.id='"
                + cfhId + "' AND rooms.id=staff_cfh.id_room AND models.id=rooms.id_model LIMIT 1", 0, 0);
            if (cfhRow.isEmpty()) {
                return;
            }
            String[] cfhFields = cfhRow.split("\t", -1);
            if (cfhFields.length < 6) {
                return;
            }
            long roomId = Vb.val(cfhFields[0]);
            long timestampSent = Vb.val(cfhFields[5]);
            String chatRows = Proc_5_2_6D4690("SELECT DATE_FORMAT(FROM_UNIXTIME(logs_chat.timestamp), '%H'),DATE_FORMAT(FROM_UNIXTIME(logs_chat.timestamp), '%i'),users.id,users.name,logs_chat.description FROM logs_chat,rooms,users WHERE logs_chat.id_room='"
                + roomId + "' AND logs_chat.timestamp < " + timestampSent + " AND logs_chat.timestamp > "
                + (timestampSent - 600L) + " AND users.id=logs_chat.id_user GROUP BY logs_chat.id ORDER BY logs_chat.id DESC LIMIT 100", 0, 0);
            HandlingMUS.Proc_12_1_821AA0(socketIndex, "HV" + mySqlCallForHelpChatLogPayload(cfhId, cfhFields, chatRows), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_5_5_6D64D0(Object... args) {
        try {
            int socketIndex = mySqlSocketIndex(args);
            String requestPayload = mySqlRequestPayload(mySqlPacketPayload(args), "GH");
            String userId = mySqlUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)
                || !mySqlUserHasPermission(userId, "fuse_mod")
                || !mySqlUserHasPermission(userId, "fuse_chatlog")) {
                return;
            }
            long roomId = Vb.val(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
            if (roomId <= 0L) {
                return;
            }
            String roomRow = Proc_5_2_6D4690("SELECT rooms.id,rooms.name,models.type FROM rooms,models WHERE rooms.id='"
                + roomId + "' AND models.id=rooms.id_model LIMIT 1", 0, 0);
            if (roomRow.isEmpty()) {
                return;
            }
            String chatRows = Proc_5_2_6D4690("SELECT DATE_FORMAT(FROM_UNIXTIME(logs_chat.timestamp), '%H'),DATE_FORMAT(FROM_UNIXTIME(logs_chat.timestamp), '%i'),users.id,users.name,logs_chat.description FROM logs_chat,rooms,users WHERE logs_chat.id_room='"
                + roomId + "' AND logs_chat.timestamp > UNIX_TIMESTAMP()-600 AND users.id=logs_chat.id_user GROUP BY logs_chat.id ORDER BY logs_chat.id DESC LIMIT 100", 0, 0);
            HandlingMUS.Proc_12_1_821AA0(socketIndex,
                "HW" + mySqlRoomChatLogHeader(roomRow.split("\t", -1)) + mySqlRoomChatLogRows(chatRows), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_5_6_6D7090(Object... args) {
        try {
            int socketIndex = mySqlSocketIndex(args);
            String requestPayload = mySqlRequestPayload(mySqlPacketPayload(args), "GK");
            String userId = mySqlUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId) || !mySqlUserHasPermission(userId, "fuse_mod")) {
                return;
            }
            long roomId = Vb.val(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
            if (roomId <= 0L) {
                return;
            }
            String roomRow = Proc_5_2_6D4690("SELECT rooms.id,rooms.visitors_now,users.id,users.name,rooms.name,rooms.description,rooms.tag_1,rooms.tag_2 FROM rooms,users WHERE rooms.id='"
                + roomId + "' AND users.id=rooms.id_owner LIMIT 1", 0, 0);
            if (roomRow.isEmpty()) {
                return;
            }
            String eventRow = Proc_5_2_6D4690("SELECT name,description,tag_1,tag_2 FROM rooms_events WHERE id_room='"
                + roomId + "' LIMIT 1", 0, 0);
            String[] eventFields = eventRow.isEmpty() ? null : eventRow.split("\t", -1);
            HandlingMUS.Proc_12_1_821AA0(socketIndex,
                "HZ" + mySqlRoomInfoPayload(roomRow.split("\t", -1), eventFields), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static String buildSqlFromArgs(Object... args) {
        if (args == null) {
            return "";
        }
        StringBuilder sql = new StringBuilder();
        for (Object arg : args) {
            if (arg != null) {
                String part = Vb.cStr(arg);
                if (!isIgnorableSqlArg(part)) {
                    sql.append(part);
                }
            }
        }
        return sql.toString().replace("\\'", "/'");
    }

    public static int mySqlSocketIndex(Object... args) {
        if (args == null || args.length == 0) {
            return 0;
        }
        return (int) Vb.val(args[0]);
    }

    public static String mySqlPacketPayload(Object... args) {
        if (args == null) {
            return "";
        }
        String payload = args.length >= 3 ? Vb.cStr(args[2]) : "";
        if (payload.isEmpty() && args.length >= 2) {
            payload = Vb.cStr(args[1]);
        }
        return payload;
    }

    public static String mySqlRequestPayload(String packetPayload, String expectedPrefix) {
        String payload = Vb.cStr(packetPayload);
        String prefix = Vb.cStr(expectedPrefix);
        if (!prefix.isEmpty() && payload.startsWith(prefix)) {
            return payload.substring(prefix.length());
        }
        return payload;
    }

    public static String mySqlUserIdFromSocket(int socketIndex) {
        if (socketIndex <= 0) {
            return "";
        }
        return String.valueOf(Vb.val(Proc_5_2_6D4690("SELECT id FROM users WHERE id_socket='" + socketIndex + "' LIMIT 1", 0, 0)));
    }

    public static boolean mySqlUserHasPermission(String userId, String permissionName) {
        String escapedUserId = Functions.Proc_10_11_80A9C0(userId, 0, 0);
        long rankIndex = Vb.val(Proc_5_2_6D4690("SELECT level FROM users WHERE id='" + escapedUserId + "' LIMIT 1", 0, 0));
        long hcLevel = Vb.val(Proc_5_2_6D4690("SELECT level_hc FROM users WHERE id='" + escapedUserId + "' LIMIT 1", 0, 0));
        return Functions.Proc_10_1_809790(rankIndex, "", permissionName, hcLevel);
    }

    public static String readSqlRows(String sqlText) {
        if (sqlText == null || sqlText.isEmpty() || databaseConnection == null) {
            return "";
        }
        try {
            return formatSqlRows(databaseConnection.query(sqlText));
        } catch (SQLException ex) {
            return "";
        }
    }

    public static void executeSql(String sqlText) {
        if (sqlText == null || sqlText.isEmpty() || databaseConnection == null) {
            return;
        }
        try {
            databaseConnection.execute(sqlText);
        } catch (SQLException ignored) {
            // The original VB6 call sites suppress most SQL failures.
        }
    }

    public static String formatSqlRows(List<List<Object>> rows) {
        StringBuilder rowText = new StringBuilder();
        for (List<Object> row : rows) {
            if (rowText.length() > 0) {
                rowText.append('\r');
            }
            for (int fieldIndex = 0; fieldIndex < row.size(); fieldIndex++) {
                if (fieldIndex > 0) {
                    rowText.append('\t');
                }
                Object value = row.get(fieldIndex);
                if (value != null) {
                    rowText.append(value);
                }
            }
        }
        return rowText.toString().replace("\\n", "\n");
    }

    public static String mySqlRoomChatLogHeader(String[] roomFields) {
        if (roomFields == null || roomFields.length < 3) {
            return "";
        }
        long roomId = Vb.val(roomFields[0]);
        String roomName = roomFields[1];
        long modelType = Vb.val(roomFields[2]);
        return PacketBuilder.create()
            .appendInt(roomId)
            .appendInt(modelType)
            .appendString(roomName)
            .build();
    }

    public static String mySqlRoomChatLogRows(String chatRows) {
        if (chatRows == null || chatRows.isEmpty()) {
            return "";
        }
        PacketBuilder payload = PacketBuilder.create();
        for (String row : chatRows.split("\r", -1)) {
            if (!row.isEmpty()) {
                String[] fields = row.split("\t", -1);
                if (fields.length >= 5) {
                    payload.appendInt(Vb.val(fields[0]))
                        .appendInt(Vb.val(fields[1]))
                        .appendInt(Vb.val(fields[2]))
                        .appendString(fields[3])
                        .appendString(fields[4]);
                }
            }
        }
        return payload.build();
    }

    public static String mySqlRoomInfoPayload(String[] roomFields, String[] eventFields) {
        if (roomFields == null || roomFields.length < 8) {
            return "";
        }
        PacketBuilder payload = PacketBuilder.create()
            .appendInt(Vb.val(roomFields[0]))
            .appendInt(Vb.val(roomFields[1]))
            .appendInt(Vb.val(roomFields[2]));
        for (int fieldIndex = 3; fieldIndex <= 7; fieldIndex++) {
            payload.appendString(roomFields[fieldIndex]);
        }

        boolean hasEvent = eventFields != null && eventFields.length >= 4;
        payload.appendBoolean(hasEvent);
        if (hasEvent) {
            for (int fieldIndex = 0; fieldIndex <= 3; fieldIndex++) {
                payload.appendString(eventFields[fieldIndex]);
            }
        }
        return payload.build();
    }

    public static String mySqlCallForHelpChatLogPayload(long cfhId, String[] cfhFields, String chatRows) {
        if (cfhFields == null || cfhFields.length < 6) {
            return "";
        }
        return PacketBuilder.create()
            .appendInt(cfhId)
            .appendInt(Vb.val(cfhFields[0]))
            .appendInt(Vb.val(cfhFields[2]))
            .appendInt(Vb.val(cfhFields[3]))
            .appendInt(Vb.val(cfhFields[4]))
            .appendString(cfhFields[1])
            .appendRaw(mySqlRoomChatLogRows(chatRows))
            .build();
    }

    public static boolean isIgnorableSqlArg(String value) {
        return value == null || value.isEmpty() || "0".equals(value) || "-1".equals(value);
    }
}
