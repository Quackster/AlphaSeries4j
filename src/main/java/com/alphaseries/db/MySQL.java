package com.alphaseries.db;

import com.alphaseries.config.AppConfigState;
import com.alphaseries.dao.mysql.UserDao;
import java.sql.SQLException;

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

    public static long mySqlUserIdFromSocket(int socketIndex) {
        if (socketIndex <= 0 || databaseConnection == null) {
            return 0L;
        }
        try {
            long userId = userDao().userIdBySocket(socketIndex);
            return userId <= 0L ? 0L : userId;
        } catch (SQLException ex) {
            return 0L;
        }
    }

    public static boolean mySqlUserHasPermission(long userId, String permissionName) {
        if (databaseConnection == null) {
            return false;
        }
        try {
            UserDao users = userDao();
            long rankIndex = users.rankLevel(userId);
            long hcLevel = users.hcLevel(userId);
            return AppConfigState.instance().permissionMatrix().allows(rankIndex, "", permissionName, hcLevel);
        } catch (SQLException ex) {
            return false;
        }
    }

}
