package com.alphaseries;

import com.alphaseries.dao.mysql.StaffModerationDao;
import com.alphaseries.dao.mysql.UserDao;
import com.alphaseries.db.Database;
import com.alphaseries.game.moderation.StaffModerationPacketHandlers;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

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
        StaffModerationPacketHandlers.sendCallForHelpChatLog(args);
    }

    public static void Proc_5_5_6D64D0(Object... args) {
        StaffModerationPacketHandlers.sendRoomChatLog(args);
    }

    public static void Proc_5_6_6D7090(Object... args) {
        StaffModerationPacketHandlers.sendRoomInfo(args);
    }

    public static String buildSqlFromArgs(Object... args) {
        if (args == null) {
            return "";
        }
        StringBuilder sql = new StringBuilder();
        for (Object arg : args) {
            if (arg != null) {
                String part = StringUtils.text(arg);
                if (!isIgnorableSqlArg(part)) {
                    sql.append(part);
                }
            }
        }
        return sql.toString().replace("\\'", "/'");
    }

    private static StaffModerationDao staffModerationDao() {
        return new StaffModerationDao(databaseConnection);
    }

    private static UserDao userDao() {
        return new UserDao(databaseConnection);
    }

    public static int mySqlSocketIndex(Object... args) {
        if (args == null || args.length == 0) {
            return 0;
        }
        return NumberUtils.parseInt(args[0]);
    }

    public static String mySqlPacketPayload(Object... args) {
        if (args == null) {
            return "";
        }
        String payload = args.length >= 3 ? StringUtils.text(args[2]) : "";
        if (payload.isEmpty() && args.length >= 2) {
            payload = StringUtils.text(args[1]);
        }
        return payload;
    }

    public static String mySqlRequestPayload(String packetPayload, String expectedPrefix) {
        String payload = StringUtils.text(packetPayload);
        String prefix = StringUtils.text(expectedPrefix);
        if (!prefix.isEmpty() && payload.startsWith(prefix)) {
            return payload.substring(prefix.length());
        }
        return payload;
    }

    public static String mySqlUserIdFromSocket(int socketIndex) {
        if (socketIndex <= 0 || databaseConnection == null) {
            return "";
        }
        try {
            long userId = userDao().userIdBySocket(socketIndex);
            return userId <= 0L ? "0" : String.valueOf(userId);
        } catch (SQLException ex) {
            return "";
        }
    }

    public static boolean mySqlUserHasPermission(String userId, String permissionName) {
        if (databaseConnection == null) {
            return false;
        }
        long numericUserId = NumberUtils.parseLong(userId);
        try {
            UserDao users = userDao();
            long rankIndex = users.rankLevel(numericUserId);
            long hcLevel = users.hcLevel(numericUserId);
            return Functions.permissionMatrix().allows(rankIndex, "", permissionName, hcLevel);
        } catch (SQLException ex) {
            return false;
        }
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

    public static boolean isIgnorableSqlArg(String value) {
        return value == null || value.isEmpty() || "0".equals(value) || "-1".equals(value);
    }
}
