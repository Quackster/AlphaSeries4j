package com.alphaseries.dao.mysql;

import com.alphaseries.db.Database;
import com.alphaseries.game.moderation.StaffRoomChatRow;
import com.alphaseries.game.moderation.StaffRoomChatVisitRow;
import com.alphaseries.game.moderation.StaffRoomVisitRow;
import com.alphaseries.game.moderation.StaffUserLookup;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public final class StaffModerationDao {
    private final Database database;

    public StaffModerationDao(Database database) {
        this.database = database;
    }

    public List<String> staffMessages(long type) throws SQLException {
        return database.query(
            "SELECT message FROM staff_messages WHERE type=? ORDER BY id ASC",
            resultSet -> resultSet.getString(1),
            type);
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

    public int insertDirectModerationLog(
        long moderationType,
        long moderatorUserId,
        long targetUserId,
        long roomId,
        String message,
        long sessionId
    ) throws SQLException {
        return database.execute(
            "INSERT INTO logs_moderation(id_type,id_user,id_target,id_target_2,timestamp,message,id_session) "
                + "VALUES(?,?,?,?,UNIX_TIMESTAMP(),?,?)",
            moderationType,
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

    public int insertFurniturePickupLog(
        long userId,
        long roomId,
        long furnitureId,
        String sessionId
    ) throws SQLException {
        return database.execute(
            "INSERT INTO logs_moderation(id_type,id_user,id_target,id_target_2,timestamp,message,id_session) "
                + "VALUES(?,?,?,?,UNIX_TIMESTAMP(),?,?)",
            8L,
            userId,
            roomId,
            furnitureId,
            "",
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

    public Optional<OpenCallForHelpReviewRow> openCallForHelpReview(long callForHelpId) throws SQLException {
        return database.queryOne(
            "SELECT staff_cfh.id,users.id,users.name,staff_cfh.id_partner,staff_cfh.id_room,"
                + "staff_cfh.id_category,staff_cfh.description,rooms.id,rooms.name FROM staff_cfh,users,rooms "
                + "WHERE staff_cfh.id=? AND staff_cfh.id_closed='0' AND users.id=staff_cfh.id_user "
                + "AND rooms.id=staff_cfh.id_room LIMIT 1",
            resultSet -> new OpenCallForHelpReviewRow(
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getString(3),
                resultSet.getLong(4),
                resultSet.getLong(5),
                resultSet.getLong(6),
                resultSet.getString(7),
                resultSet.getLong(8),
                resultSet.getString(9)),
            callForHelpId);
    }

    public String openCallForHelpReviewRow(long callForHelpId) throws SQLException {
        return openCallForHelpReview(callForHelpId)
            .map(OpenCallForHelpReviewRow::legacyRow)
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

    public int moveCallForHelpToTab(List<Long> callForHelpIds, long tabId, long pickerUserId) throws SQLException {
        if (callForHelpIds == null || callForHelpIds.isEmpty()) {
            return 0;
        }
        String placeholders = String.join(",", java.util.Collections.nCopies(callForHelpIds.size(), "?"));
        if (tabId == 2L) {
            Object[] parameters = new Object[2 + callForHelpIds.size()];
            parameters[0] = tabId;
            parameters[1] = pickerUserId;
            for (int index = 0; index < callForHelpIds.size(); index++) {
                parameters[index + 2] = callForHelpIds.get(index);
            }
            return database.execute(
                "UPDATE staff_cfh SET id_tab=?,id_picker=?,timestamp_picked=UNIX_TIMESTAMP() WHERE id IN ("
                    + placeholders + ")",
                parameters);
        }
        Object[] parameters = new Object[1 + callForHelpIds.size()];
        parameters[0] = tabId;
        for (int index = 0; index < callForHelpIds.size(); index++) {
            parameters[index + 1] = callForHelpIds.get(index);
        }
        return database.execute(
            "UPDATE staff_cfh SET id_tab=?,id_picker=0,timestamp_picked=NULL WHERE id IN (" + placeholders + ")",
            parameters);
    }

    public Optional<StaffUserLookup> staffUserLookup(long userId) throws SQLException {
        return database.queryOne(
            "SELECT users.id,users.name FROM users WHERE users.id=? LIMIT 1",
            resultSet -> new StaffUserLookup(resultSet.getLong(1), resultSet.getString(2)),
            userId);
    }

    public List<StaffRoomChatVisitRow> recentChatHistoryVisits(long userId) throws SQLException {
        return database.query(
            "SELECT models.type,rooms.id,rooms.name,logs_visitedrooms.timestamp_enter,"
                + "logs_visitedrooms.timestamp_left FROM rooms,logs_visitedrooms,models WHERE logs_visitedrooms.id_user=? "
                + "AND rooms.id=logs_visitedrooms.id_room AND logs_visitedrooms.timestamp_enter > UNIX_TIMESTAMP()-21600 "
                + "AND models.id=rooms.id_model GROUP BY logs_visitedrooms.id ORDER BY logs_visitedrooms.id DESC LIMIT 10",
            resultSet -> new StaffRoomChatVisitRow(
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getString(3),
                resultSet.getLong(4),
                resultSet.getLong(5)),
            userId);
    }

    public List<StaffRoomVisitRow> recentRoomVisits(long userId) throws SQLException {
        return database.query(
            "SELECT models.type,rooms.id,rooms.name,DATE_FORMAT(FROM_UNIXTIME(logs_visitedrooms.timestamp_enter), '%H'),"
                + "DATE_FORMAT(FROM_UNIXTIME(logs_visitedrooms.timestamp_enter), '%i') FROM rooms,logs_visitedrooms,models "
                + "WHERE logs_visitedrooms.timestamp_enter > UNIX_TIMESTAMP()-21600 AND logs_visitedrooms.id_user=? "
                + "AND rooms.id=logs_visitedrooms.id_room AND models.id=rooms.id_model GROUP BY logs_visitedrooms.id "
                + "ORDER BY logs_visitedrooms.id DESC LIMIT 50",
            resultSet -> new StaffRoomVisitRow(
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getString(3),
                resultSet.getLong(4),
                resultSet.getLong(5)),
            userId);
    }

    public List<StaffRoomChatRow> chatRowsForVisit(
        long roomId,
        long userId,
        long timestampEnter,
        long timestampLeft
    ) throws SQLException {
        String timestampLeftSql = timestampLeft > 0L ? " AND logs_chat.timestamp <= ?" : "";
        if (timestampLeft > 0L) {
            return database.query(
                "SELECT DATE_FORMAT(FROM_UNIXTIME(logs_chat.timestamp), '%H'),"
                    + "DATE_FORMAT(FROM_UNIXTIME(logs_chat.timestamp), '%i'),users.id,users.name,logs_chat.description "
                    + "FROM logs_chat,users WHERE logs_chat.id_room=? AND logs_chat.id_user=? "
                    + "AND logs_chat.timestamp >= ?" + timestampLeftSql
                    + " AND users.id=logs_chat.id_user ORDER BY logs_chat.id ASC",
                resultSet -> new StaffRoomChatRow(
                    resultSet.getLong(1),
                    resultSet.getLong(2),
                    resultSet.getLong(3),
                    resultSet.getString(4),
                    resultSet.getString(5)),
                roomId,
                userId,
                timestampEnter,
                timestampLeft);
        }
        return database.query(
            "SELECT DATE_FORMAT(FROM_UNIXTIME(logs_chat.timestamp), '%H'),"
                + "DATE_FORMAT(FROM_UNIXTIME(logs_chat.timestamp), '%i'),users.id,users.name,logs_chat.description "
                + "FROM logs_chat,users WHERE logs_chat.id_room=? AND logs_chat.id_user=? "
                + "AND logs_chat.timestamp >= ?" + timestampLeftSql
                + " AND users.id=logs_chat.id_user ORDER BY logs_chat.id ASC",
            resultSet -> new StaffRoomChatRow(
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getLong(3),
                resultSet.getString(4),
                resultSet.getString(5)),
            roomId,
            userId,
            timestampEnter);
    }

    public int lockRoomForModeration(long roomId) throws SQLException {
        return database.execute(
            "UPDATE rooms SET status_door=?, name=? WHERE id=?",
            1L,
            "Inappropriate to hotel management",
            roomId);
    }

    public long latestOpenCallForHelpId(long userId) throws SQLException {
        return database.queryOne(
            "SELECT id FROM staff_cfh WHERE id_user=? AND id_closed='0' "
                + "AND timestamp_sent > UNIX_TIMESTAMP()-600 ORDER BY id DESC LIMIT 1",
            resultSet -> resultSet.getLong(1),
            userId)
            .orElse(0L);
    }

    public int deleteCallForHelp(long callForHelpId) throws SQLException {
        return database.execute("DELETE FROM staff_cfh WHERE id=?", callForHelpId);
    }

    public String openStaffCallRows() throws SQLException {
        List<String> rows = database.query(
            "SELECT staff_cfh.id,staff_cfh.id_tab,users.id,users.name,"
                + "staff_cfh.id_partner,staff_cfh.id_room,staff_cfh.id_category,staff_cfh.description,rooms.id,rooms.name,"
                + "staff_cfh.id_picker FROM staff_cfh,users,rooms WHERE staff_cfh.id_closed!='3' "
                + "AND staff_cfh.timestamp_sent > UNIX_TIMESTAMP()-43200 AND users.id=staff_cfh.id_user "
                + "AND users.id_socket IS NOT NULL AND rooms.id=staff_cfh.id_room LIMIT 1000",
            resultSet -> resultSet.getString(1) + "\t" + resultSet.getString(2) + "\t" + resultSet.getString(3)
                + "\t" + resultSet.getString(4) + "\t" + resultSet.getString(5) + "\t" + resultSet.getString(6)
                + "\t" + resultSet.getString(7) + "\t" + resultSet.getString(8) + "\t" + resultSet.getString(9)
                + "\t" + resultSet.getString(10) + "\t" + resultSet.getString(11));
        return String.join("\r", rows);
    }

    public Optional<Long> recentCallForHelpClosedState(long userId) throws SQLException {
        return database.queryOne(
            "SELECT id_closed FROM staff_cfh WHERE id_user=? AND timestamp_sent > UNIX_TIMESTAMP()-600 ORDER BY id DESC LIMIT 1",
            resultSet -> resultSet.getLong(1),
            userId);
    }

    public int insertCallForHelp(
        long userId,
        long roomId,
        long categoryId,
        long partnerUserId,
        String description
    ) throws SQLException {
        return database.execute(
            "INSERT INTO staff_cfh(id_user,id_room,id_category,id_partner,description,timestamp_sent) "
                + "VALUES(?,?,?,?,?,UNIX_TIMESTAMP())",
            userId,
            roomId,
            categoryId,
            partnerUserId,
            description);
    }

    public long newestCallForHelpId() throws SQLException {
        return database.queryOne(
            "SELECT MAX(id) FROM staff_cfh",
            resultSet -> resultSet.getLong(1))
            .orElse(0L);
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

    public record OpenCallForHelpReviewRow(
        long callForHelpId,
        long callerUserId,
        String callerName,
        long partnerUserId,
        long roomId,
        long categoryId,
        String description,
        long duplicateRoomId,
        String roomName
    ) {
        public String legacyRow() {
            return callForHelpId + "\t" + callerUserId + "\t" + text(callerName) + "\t" + partnerUserId
                + "\t" + roomId + "\t" + categoryId + "\t" + text(description) + "\t" + duplicateRoomId
                + "\t" + text(roomName);
        }

        public String reviewPayloadRow() {
            return callForHelpId + "\t\t" + callerUserId + "\t" + text(callerName) + "\t" + partnerUserId
                + "\t" + roomId + "\t" + categoryId + "\t" + text(description) + "\t" + duplicateRoomId
                + "\t" + text(roomName) + "\t";
        }

        private static String text(String value) {
            return value == null ? "" : value;
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
