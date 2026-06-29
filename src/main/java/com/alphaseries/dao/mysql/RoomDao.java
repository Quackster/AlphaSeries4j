package com.alphaseries.dao.mysql;

import com.alphaseries.db.Database;

import java.sql.SQLException;
import java.util.List;

public final class RoomDao {
    private final Database database;

    public RoomDao(Database database) {
        this.database = database;
    }

    public long furnitureCountAt(long roomId, long positionX, long positionY) throws SQLException {
        return database.queryOne(
            "SELECT COUNT(*) FROM furnitures WHERE id_room=? AND position_x=? AND position_y=?",
            resultSet -> resultSet.getLong(1),
            roomId,
            positionX,
            positionY)
            .orElse(0L);
    }

    public long botCountAt(long roomId, long positionX, long positionY) throws SQLException {
        return database.queryOne(
            "SELECT COUNT(*) FROM bots WHERE id_room=? AND position_x=? AND position_y=?",
            resultSet -> resultSet.getLong(1),
            roomId,
            positionX,
            positionY)
            .orElse(0L);
    }

    public long roomIdBySlot(long roomSlot) throws SQLException {
        return database.queryOne(
            "SELECT id FROM rooms WHERE id_slot=? LIMIT 1",
            resultSet -> resultSet.getLong(1),
            roomSlot)
            .orElse(0L);
    }

    public long roomIdByBot(long botId) throws SQLException {
        return database.queryOne(
            "SELECT id_room FROM bots WHERE id=? LIMIT 1",
            resultSet -> resultSet.getLong(1),
            botId)
            .orElse(0L);
    }

    public List<Long> activeSocketIndexesByRoom(long roomId) throws SQLException {
        return database.query(
            "SELECT users.id_socket FROM logs_visitedrooms,users WHERE logs_visitedrooms.id_room=? "
                + "AND logs_visitedrooms.timestamp_left IS NULL AND users.id=logs_visitedrooms.id_user "
                + "AND users.id_socket IS NOT NULL",
            resultSet -> resultSet.getLong(1),
            roomId);
    }
}
