package com.alphaseries.server.lifecycle;

import com.alphaseries.dao.mysql.ServerMaintenanceDao;
import com.alphaseries.db.Database;
import com.alphaseries.db.MySQL;

public final class ServerStartupMaintenance {
    private ServerStartupMaintenance() {
    }

    /**
     * Original function: Proc_1_3_6BEBA0.
     */
    public static void resetActiveServerData() {
        ServerMaintenanceDao maintenanceDao = serverMaintenanceDao();
        if (maintenanceDao == null) {
            return;
        }
        try {
            maintenanceDao.resetConnectedUsers();
            maintenanceDao.resetVisitedRoomSlots();
            maintenanceDao.clearActiveVisitedRooms();
            maintenanceDao.clearRoomEvents();
        } catch (Exception ignored) {
            // VB6 source suppresses boot-time maintenance failures.
        }
    }

    private static ServerMaintenanceDao serverMaintenanceDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new ServerMaintenanceDao(database);
    }
}
