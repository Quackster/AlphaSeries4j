package com.alphaseries.dao.mysql;

import com.alphaseries.db.Database;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public final class StaffModerationDao {
    private final Database database;

    public StaffModerationDao(Database database) {
        this.database = database;
    }

    public Optional<CallForHelpRoom> callForHelpRoom(long callForHelpId) throws SQLException {
        return database.queryOne(
            "SELECT rooms.id,rooms.name,models.type,staff_cfh.id_user,staff_cfh.id_partner,staff_cfh.timestamp_sent "
                + "FROM rooms,models,staff_cfh WHERE staff_cfh.id=? "
                + "AND rooms.id=staff_cfh.id_room AND models.id=rooms.id_model LIMIT 1",
            resultSet -> new CallForHelpRoom(
                resultSet.getLong(1),
                resultSet.getString(2),
                resultSet.getLong(3),
                resultSet.getLong(4),
                resultSet.getLong(5),
                resultSet.getLong(6)),
            callForHelpId);
    }

    public String recentChatRowsBefore(long roomId, long timestampSent) throws SQLException {
        List<String> rows = database.query(
            "SELECT DATE_FORMAT(FROM_UNIXTIME(logs_chat.timestamp), '%H'),"
                + "DATE_FORMAT(FROM_UNIXTIME(logs_chat.timestamp), '%i'),users.id,users.name,logs_chat.description "
                + "FROM logs_chat,rooms,users WHERE logs_chat.id_room=? AND logs_chat.timestamp < ? "
                + "AND logs_chat.timestamp > ? AND users.id=logs_chat.id_user "
                + "GROUP BY logs_chat.id ORDER BY logs_chat.id DESC LIMIT 100",
            resultSet -> resultSet.getString(1) + "\t" + resultSet.getString(2) + "\t" + resultSet.getString(3)
                + "\t" + resultSet.getString(4) + "\t" + resultSet.getString(5),
            roomId,
            timestampSent,
            timestampSent - 600L);
        return String.join("\r", rows);
    }

    public Optional<RoomChatHeader> roomChatHeader(long roomId) throws SQLException {
        return database.queryOne(
            "SELECT rooms.id,rooms.name,models.type FROM rooms,models WHERE rooms.id=? "
                + "AND models.id=rooms.id_model LIMIT 1",
            resultSet -> new RoomChatHeader(resultSet.getLong(1), resultSet.getString(2), resultSet.getLong(3)),
            roomId);
    }

    public String recentChatRows(long roomId) throws SQLException {
        List<String> rows = database.query(
            "SELECT DATE_FORMAT(FROM_UNIXTIME(logs_chat.timestamp), '%H'),"
                + "DATE_FORMAT(FROM_UNIXTIME(logs_chat.timestamp), '%i'),users.id,users.name,logs_chat.description "
                + "FROM logs_chat,rooms,users WHERE logs_chat.id_room=? AND logs_chat.timestamp > UNIX_TIMESTAMP()-600 "
                + "AND users.id=logs_chat.id_user GROUP BY logs_chat.id ORDER BY logs_chat.id DESC LIMIT 100",
            resultSet -> resultSet.getString(1) + "\t" + resultSet.getString(2) + "\t" + resultSet.getString(3)
                + "\t" + resultSet.getString(4) + "\t" + resultSet.getString(5),
            roomId);
        return String.join("\r", rows);
    }

    public Optional<RoomInfo> roomInfo(long roomId) throws SQLException {
        return database.queryOne(
            "SELECT rooms.id,rooms.visitors_now,users.id,users.name,rooms.name,rooms.description,rooms.tag_1,rooms.tag_2 "
                + "FROM rooms,users WHERE rooms.id=? AND users.id=rooms.id_owner LIMIT 1",
            resultSet -> new RoomInfo(
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getLong(3),
                resultSet.getString(4),
                resultSet.getString(5),
                resultSet.getString(6),
                resultSet.getString(7),
                resultSet.getString(8)),
            roomId);
    }

    public Optional<RoomEvent> roomEvent(long roomId) throws SQLException {
        return database.queryOne(
            "SELECT name,description,tag_1,tag_2 FROM rooms_events WHERE id_room=? LIMIT 1",
            resultSet -> new RoomEvent(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4)),
            roomId);
    }

    public record CallForHelpRoom(long roomId, String roomName, long modelType, long userId, long partnerId, long timestampSent) {
        public String[] toFields() {
            return new String[]{
                String.valueOf(roomId),
                roomName,
                String.valueOf(modelType),
                String.valueOf(userId),
                String.valueOf(partnerId),
                String.valueOf(timestampSent)
            };
        }
    }

    public record RoomChatHeader(long roomId, String roomName, long modelType) {
        public String[] toFields() {
            return new String[]{String.valueOf(roomId), roomName, String.valueOf(modelType)};
        }
    }

    public record RoomInfo(long roomId, long visitorsNow, long ownerId, String ownerName, String roomName,
                           String description, String tag1, String tag2) {
        public String[] toFields() {
            return new String[]{
                String.valueOf(roomId),
                String.valueOf(visitorsNow),
                String.valueOf(ownerId),
                ownerName,
                roomName,
                description,
                tag1,
                tag2
            };
        }
    }

    public record RoomEvent(String name, String description, String tag1, String tag2) {
        public String[] toFields() {
            return new String[]{name, description, tag1, tag2};
        }
    }
}
