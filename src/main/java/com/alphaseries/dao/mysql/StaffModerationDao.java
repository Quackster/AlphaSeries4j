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

    public Optional<UserModerationSummary> userModerationSummary(long userId) throws SQLException {
        Optional<String> userRow = database.queryOne(
            "SELECT users.id,users.name,ROUND((UNIX_TIMESTAMP()-users.create_time)/60,0),"
                + "ROUND((UNIX_TIMESTAMP()-users.lastonline_time)/60,0),users.id_socket FROM users WHERE users.id=? LIMIT 1",
            resultSet -> resultSet.getString(1) + "\t" + resultSet.getString(2) + "\t" + resultSet.getString(3)
                + "\t" + resultSet.getString(4) + "\t" + resultSet.getString(5),
            userId);
        if (userRow.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new UserModerationSummary(
            userRow.get(),
            countCallForHelpByUser(userId),
            countPickedCallForHelpByUser(userId),
            countCautionsByUser(userId),
            countBansByUser(userId)));
    }

    public String userLastIpAddress(long userId) throws SQLException {
        return database.queryOne(
            "SELECT ip_last FROM users WHERE id=? LIMIT 1",
            resultSet -> resultSet.getString(1),
            userId)
            .orElse("");
    }

    public int insertModerationBanLog(
        long moderatorUserId,
        long targetUserId,
        long roomId,
        String message,
        long sessionId
    ) throws SQLException {
        return database.execute(
            "INSERT INTO logs_moderation(id_type,id_user,id_target,id_target_2,timestamp,message,id_session) "
                + "VALUES('6',?,?,?,UNIX_TIMESTAMP(),?,?)",
            moderatorUserId,
            targetUserId,
            roomId,
            message,
            sessionId);
    }

    public int insertUserBan(
        long targetUserId,
        long moderatorUserId,
        String message,
        long banSeconds,
        String ipAddress
    ) throws SQLException {
        if (ipAddress == null || ipAddress.isEmpty()) {
            return database.execute(
                "INSERT INTO users_bans(id_user,id_partner,message,timestamp_expire,timestamp_submit) "
                    + "VALUES(?,?,?,UNIX_TIMESTAMP()+" + banSeconds + ",UNIX_TIMESTAMP())",
                targetUserId,
                moderatorUserId,
                message);
        }
        return database.execute(
            "INSERT INTO users_bans(id_user,id_partner,message,timestamp_expire,timestamp_submit,ipaddress) "
                + "VALUES(?,?,?,UNIX_TIMESTAMP()+" + banSeconds + ",UNIX_TIMESTAMP(),?)",
            targetUserId,
            moderatorUserId,
            message,
            ipAddress);
    }

    public int clearUserLoginSession(long userId) throws SQLException {
        return database.execute("UPDATE users SET login_session=NULL WHERE id=?", userId);
    }

    public Optional<RoomModerationTarget> roomModerationTarget(long roomId) throws SQLException {
        return database.queryOne(
            "SELECT id_slot,id_owner FROM rooms WHERE id=? LIMIT 1",
            resultSet -> new RoomModerationTarget(resultSet.getLong(1), resultSet.getLong(2)),
            roomId);
    }

    public int insertRoomModerationLog(
        long moderationType,
        long moderatorUserId,
        long roomId,
        String message,
        long sessionId
    ) throws SQLException {
        return database.execute(
            "INSERT INTO logs_moderation(id_type,id_user,id_target,timestamp,message,id_session) "
                + "VALUES(?,?,?,UNIX_TIMESTAMP(),?,?)",
            moderationType,
            moderatorUserId,
            roomId,
            message,
            sessionId);
    }

    public int deleteRoomEvent(long roomId) throws SQLException {
        return database.execute("DELETE FROM rooms_events WHERE id_room=? LIMIT 1", roomId);
    }

    public int insertUserCaution(long userId, long moderatorUserId, String message) throws SQLException {
        return database.execute(
            "INSERT INTO users_cautions(id_user,id_partner,message,timestamp_submit) VALUES(?,?,?,UNIX_TIMESTAMP())",
            userId,
            moderatorUserId,
            message);
    }

    public String openCallForHelpReviewRow(long callForHelpId) throws SQLException {
        return database.queryOne(
            "SELECT staff_cfh.id,users.id,users.name,staff_cfh.id_partner,staff_cfh.id_room,"
                + "staff_cfh.id_category,staff_cfh.description,rooms.id,rooms.name FROM staff_cfh,users,rooms "
                + "WHERE staff_cfh.id=? AND staff_cfh.id_closed='0' AND users.id=staff_cfh.id_user "
                + "AND rooms.id=staff_cfh.id_room LIMIT 1",
            resultSet -> resultSet.getString(1) + "\t" + resultSet.getString(2) + "\t" + resultSet.getString(3)
                + "\t" + resultSet.getString(4) + "\t" + resultSet.getString(5) + "\t" + resultSet.getString(6)
                + "\t" + resultSet.getString(7) + "\t" + resultSet.getString(8) + "\t" + resultSet.getString(9),
            callForHelpId)
            .orElse("");
    }

    public long callForHelpReporterUserId(long callForHelpId) throws SQLException {
        return database.queryOne(
            "SELECT id_user FROM staff_cfh WHERE id=? LIMIT 1",
            resultSet -> resultSet.getLong(1),
            callForHelpId)
            .orElse(0L);
    }

    public int closeCallForHelp(long callForHelpId, long closeState) throws SQLException {
        return database.execute("UPDATE staff_cfh SET id_closed=?,id_tab=? WHERE id=?", closeState, 0L, callForHelpId);
    }

    public int lockRoomForModeration(long roomId) throws SQLException {
        return database.execute(
            "UPDATE rooms SET status_door=?, name=? WHERE id=?",
            1L,
            "Inappropriate to hotel management",
            roomId);
    }

    private long countCallForHelpByUser(long userId) throws SQLException {
        return database.queryOne(
            "SELECT COUNT(id) FROM staff_cfh WHERE id_user=?",
            resultSet -> resultSet.getLong(1),
            userId)
            .orElse(0L);
    }

    private long countPickedCallForHelpByUser(long userId) throws SQLException {
        return database.queryOne(
            "SELECT COUNT(id) FROM staff_cfh WHERE id_user=? AND id_closed=?",
            resultSet -> resultSet.getLong(1),
            userId,
            2L)
            .orElse(0L);
    }

    private long countCautionsByUser(long userId) throws SQLException {
        return database.queryOne(
            "SELECT COUNT(id) FROM users_cautions WHERE id_user=?",
            resultSet -> resultSet.getLong(1),
            userId)
            .orElse(0L);
    }

    private long countBansByUser(long userId) throws SQLException {
        return database.queryOne(
            "SELECT COUNT(id) FROM users_bans WHERE id_user=?",
            resultSet -> resultSet.getLong(1),
            userId)
            .orElse(0L);
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

    public record UserModerationSummary(String userRow, long callForHelpCount, long pickedCallForHelpCount,
                                        long cautionCount, long banCount) {
    }

    public record RoomModerationTarget(long roomSlot, long ownerUserId) {
    }
}
