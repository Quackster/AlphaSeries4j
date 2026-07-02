package com.alphaseries.db;

import com.alphaseries.config.AppConfigState;
import com.alphaseries.dao.mysql.UserDao;
import com.alphaseries.util.NumberUtils;

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

    private static UserDao userDao() {
        return new UserDao(databaseConnection);
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
            return AppConfigState.instance().permissionMatrix().allows(rankIndex, "", permissionName, hcLevel);
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
}
