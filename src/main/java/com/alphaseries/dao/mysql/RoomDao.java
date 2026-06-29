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

    public long currentRoomIdByUser(long userId) throws SQLException {
        return database.queryOne(
            "SELECT id_room FROM logs_visitedrooms WHERE id_user=? AND timestamp_left IS NULL "
                + "ORDER BY timestamp_enter DESC LIMIT 1",
            resultSet -> resultSet.getLong(1),
            userId)
            .orElse(0L);
    }

    public long furnitureIdAtExcluding(long roomId, long excludedFurnitureId, long positionX, long positionY) throws SQLException {
        return database.queryOne(
            "SELECT id FROM furnitures WHERE id_room=? AND position_x=? AND position_y=? AND id<>? "
                + "ORDER BY position_z DESC,id DESC LIMIT 1",
            resultSet -> resultSet.getLong(1),
            roomId,
            positionX,
            positionY,
            excludedFurnitureId)
            .orElse(0L);
    }

    public String topFurnitureHeightAt(long roomId, long positionX, long positionY) throws SQLException {
        return database.queryOne(
            "SELECT position_z FROM furnitures WHERE id_room=? AND position_x=? AND position_y=? "
                + "ORDER BY position_z DESC,id DESC LIMIT 1",
            resultSet -> resultSet.getString(1),
            roomId,
            positionX,
            positionY)
            .orElse("");
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

    public String settingsRow(long roomId) throws SQLException {
        return database.queryOne(
            "SELECT rooms.id,rooms.name,rooms.description,rooms.status_door,"
                + "rooms.id_category,rooms.visitors_max,models.visitors_max,rooms.tag_1,rooms.tag_2,NULL,"
                + "rooms.allow_otherspets,rooms.allow_feedpets,rooms.allow_walkthrough,rooms.disable_walls "
                + "FROM rooms,models WHERE rooms.id=? AND models.id=rooms.id_model LIMIT 1",
            resultSet -> resultSet.getString(1) + "\t" + resultSet.getString(2) + "\t" + resultSet.getString(3)
                + "\t" + resultSet.getString(4) + "\t" + resultSet.getString(5) + "\t" + resultSet.getString(6)
                + "\t" + resultSet.getString(7) + "\t" + resultSet.getString(8) + "\t" + resultSet.getString(9)
                + "\t" + resultSet.getString(10) + "\t" + resultSet.getString(11) + "\t" + resultSet.getString(12)
                + "\t" + resultSet.getString(13) + "\t" + resultSet.getString(14),
            roomId)
            .orElse("");
    }

    public String rightsRows(long roomId) throws SQLException {
        List<String> rows = database.query(
            "SELECT users.id,users.name FROM rooms_rights,users WHERE rooms_rights.id_room=? "
                + "AND users.id=rooms_rights.id_user LIMIT 250",
            resultSet -> resultSet.getString(1) + "\t" + resultSet.getString(2),
            roomId);
        return String.join("\r", rows);
    }

    public int updateIcon(long roomId, String iconPayload) throws SQLException {
        return database.execute("UPDATE rooms SET icon=? WHERE id=?", iconPayload, roomId);
    }

    public int deleteRoomEvents(long roomId) throws SQLException {
        return database.execute("DELETE FROM rooms_events WHERE id_room=?", roomId);
    }

    public long doorStatus(long roomId) throws SQLException {
        return database.queryOne(
            "SELECT status_door FROM rooms WHERE id=? LIMIT 1",
            resultSet -> resultSet.getLong(1),
            roomId)
            .orElse(0L);
    }

    public int insertRoomEvent(
        long roomId,
        long userId,
        String eventName,
        String eventDescription,
        long categoryId,
        String tagOne,
        String tagTwo,
        String categoryName
    ) throws SQLException {
        return database.execute(
            "INSERT INTO rooms_events(id_room,id_user,name,description,id_category,tag_1,tag_2,timestamp,name_category) "
                + "VALUES(?,?,?,?,?,?,?,UNIX_TIMESTAMP(),?)",
            roomId,
            userId,
            eventName,
            eventDescription,
            categoryId,
            nullableText(tagOne),
            nullableText(tagTwo),
            categoryName);
    }

    public int updateRoomEvent(
        long roomId,
        long userId,
        String eventName,
        String eventDescription,
        String tagOne,
        String tagTwo
    ) throws SQLException {
        return database.execute(
            "UPDATE rooms_events SET id_user=?,name=?,description=?,tag_1=?,tag_2=? WHERE id_room=?",
            userId,
            eventName,
            eventDescription,
            nullableText(tagOne),
            nullableText(tagTwo),
            roomId);
    }

    private static String nullableText(String value) {
        return value == null || value.isEmpty() ? null : value;
    }
}
