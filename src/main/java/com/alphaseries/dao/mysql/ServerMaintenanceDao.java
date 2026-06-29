package com.alphaseries.dao.mysql;

import com.alphaseries.db.Database;

import java.sql.SQLException;

public final class ServerMaintenanceDao {
    private final Database database;

    public ServerMaintenanceDao(Database database) {
        this.database = database;
    }

    public void resetConnectedUsers() throws SQLException {
        database.execute("UPDATE users SET id_socket=null,lastonline_time=UNIX_TIMESTAMP() WHERE id_socket IS NOT NULL");
    }

    public int resetOccupiedRoomSlots() throws SQLException {
        return database.execute("UPDATE rooms SET id_slot=null,visitors_now=? WHERE id_slot IS NOT NULL OR visitors_now!=?", 0L, 0L);
    }

    public int resetVisitedRoomSlots() throws SQLException {
        return database.execute("UPDATE rooms SET id_slot=null,visitors_now=? WHERE visitors_now != ?", 0L, 0L);
    }

    public void clearActiveVisitedRooms() throws SQLException {
        database.execute("DELETE FROM logs_visitedrooms WHERE timestamp_left IS NULL");
    }

    public void clearRoomEvents() throws SQLException {
        database.execute("DELETE FROM rooms_events");
    }
}
