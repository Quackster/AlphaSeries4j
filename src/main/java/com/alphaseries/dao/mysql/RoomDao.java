package com.alphaseries.dao.mysql;

import com.alphaseries.db.Database;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

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

    public long activeVisitIdByUser(long userId) throws SQLException {
        return database.queryOne(
            "SELECT id FROM logs_visitedrooms WHERE id_user=? AND timestamp_left IS NULL "
                + "ORDER BY timestamp_enter DESC LIMIT 1",
            resultSet -> resultSet.getLong(1),
            userId)
            .orElse(0L);
    }

    public int insertVisit(long userId, long roomId, String sessionId) throws SQLException {
        return database.execute(
            "INSERT INTO logs_visitedrooms(id_user,id_room,timestamp_enter,id_session) VALUES(?,?,UNIX_TIMESTAMP(),?)",
            userId,
            roomId,
            sessionId);
    }

    public int markRoomEntered(long roomId, long slotId) throws SQLException {
        return database.execute(
            "UPDATE rooms SET id_slot=?,visitors_now=visitors_now+1 WHERE id=?",
            slotId,
            roomId);
    }

    public Optional<RoomEntryState> roomEntryState(long roomId) throws SQLException {
        return database.queryOne(
            "SELECT visitors_now,visitors_max,status_door,password,id_slot,id_owner "
                + "FROM rooms WHERE rooms.id=? LIMIT 1",
            resultSet -> new RoomEntryState(
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getLong(3),
                resultSet.getString(4),
                resultSet.getLong(5),
                resultSet.getLong(6)),
            roomId);
    }

    public boolean userBannedFromRoom(long userId, long roomId) throws SQLException {
        return database.queryOne(
            "SELECT id_user FROM rooms_bans WHERE id_user=? AND id_room=? LIMIT 1",
            resultSet -> resultSet.getLong(1),
            userId,
            roomId)
            .orElse(0L) > 0L;
    }

    public Optional<ActiveRoomVisit> activeVisitWithRoomSlot(long userId) throws SQLException {
        return database.queryOne(
            "SELECT logs_visitedrooms.id,logs_visitedrooms.id_room,rooms.id_slot "
                + "FROM logs_visitedrooms,rooms WHERE logs_visitedrooms.id_user=? "
                + "AND logs_visitedrooms.timestamp_left IS NULL AND rooms.id=logs_visitedrooms.id_room "
                + "ORDER BY logs_visitedrooms.timestamp_enter DESC LIMIT 1",
            resultSet -> new ActiveRoomVisit(
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getLong(3)),
            userId);
    }

    public int closeVisitById(long visitId) throws SQLException {
        return database.execute(
            "UPDATE logs_visitedrooms SET timestamp_left=UNIX_TIMESTAMP() WHERE id=? AND timestamp_left IS NULL",
            visitId);
    }

    public int closeVisitsByUserRoom(long userId, long roomId) throws SQLException {
        return database.execute(
            "UPDATE logs_visitedrooms SET timestamp_left=UNIX_TIMESTAMP() WHERE id_user=? AND id_room=? AND timestamp_left IS NULL",
            userId,
            roomId);
    }

    public int decrementVisitors(long roomId) throws SQLException {
        return database.execute(
            "UPDATE rooms SET visitors_now=IF(visitors_now>0,visitors_now-1,0) WHERE id=?",
            roomId);
    }

    public int clearRoomSlot(long roomId, long slotId) throws SQLException {
        return database.execute(
            "UPDATE rooms SET id_slot=null WHERE id=? AND id_slot=?",
            roomId,
            slotId);
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

    public List<Long> activeRightHolderSocketIndexes(long roomId) throws SQLException {
        return database.query(
            "SELECT users.id_socket FROM rooms_rights,users WHERE rooms_rights.id_room=? "
                + "AND users.id=rooms_rights.id_user AND users.id_socket IS NOT NULL",
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

    public int updateSettings(
        long roomId,
        long thicknessFloor,
        long thicknessWallpaper,
        String roomName,
        String roomPassword,
        String roomDescription,
        long doorStatus,
        long categoryId,
        String tagOne,
        String tagTwo,
        long allowOthersPets,
        long allowFeedPets,
        long allowWalkthrough,
        long visitorsMax,
        long disableWalls
    ) throws SQLException {
        return database.execute(
            "UPDATE rooms SET thickness_floor=?,thickness_wallpaper=?,name=?,password=?,description=?,"
                + "status_door=?,id_category=?,tag_1=?,tag_2=?,allow_otherspets=?,allow_feedpets=?,"
                + "allow_walkthrough=?,visitors_max=?,disable_walls=? WHERE id=?",
            thicknessFloor,
            thicknessWallpaper,
            roomName,
            roomPassword,
            roomDescription,
            doorStatus,
            categoryId,
            nullableText(tagOne),
            nullableText(tagTwo),
            allowOthersPets,
            allowFeedPets,
            allowWalkthrough,
            visitorsMax,
            disableWalls,
            roomId);
    }

    public int deleteRoomEvents(long roomId) throws SQLException {
        return database.execute("DELETE FROM rooms_events WHERE id_room=?", roomId);
    }

    public boolean userOwnsRoom(long userId, long roomId) throws SQLException {
        return database.queryOne(
            "SELECT id FROM rooms WHERE id=? AND id_owner=? LIMIT 1",
            resultSet -> resultSet.getLong(1),
            roomId,
            userId)
            .orElse(0L) > 0L;
    }

    public boolean userHasRoomRight(long userId, long roomId) throws SQLException {
        if (database.queryOne(
            "SELECT id_owner FROM rooms WHERE id=? AND id_owner=? LIMIT 1",
            resultSet -> resultSet.getLong(1),
            roomId,
            userId)
            .orElse(0L) > 0L) {
            return true;
        }
        return database.queryOne(
            "SELECT id_user FROM rooms_rights WHERE id_user=? AND id_room=? LIMIT 1",
            resultSet -> resultSet.getLong(1),
            userId,
            roomId)
            .orElse(0L) > 0L;
    }

    public boolean userRatedRoom(long userId, long roomId) throws SQLException {
        return database.queryOne(
            "SELECT id_user FROM rooms_rates WHERE id_user=? AND id_room=? LIMIT 1",
            resultSet -> resultSet.getLong(1),
            userId,
            roomId)
            .orElse(0L) > 0L;
    }

    public int insertRoomRate(long userId, long roomId) throws SQLException {
        return database.execute(
            "INSERT INTO rooms_rates(id_user,id_room,timestamp) VALUES(?,?,UNIX_TIMESTAMP())",
            userId,
            roomId);
    }

    public long roomRate(long roomId) throws SQLException {
        return database.queryOne(
            "SELECT rate FROM rooms WHERE id=? LIMIT 1",
            resultSet -> resultSet.getLong(1),
            roomId)
            .orElse(0L);
    }

    public int updateRoomRate(long roomId, long roomRate) throws SQLException {
        return database.execute("UPDATE rooms SET rate=? WHERE id=?", roomRate, roomId);
    }

    public int deleteRoomRight(long userId, long roomId) throws SQLException {
        return database.execute(
            "DELETE FROM rooms_rights WHERE id_user=? AND id_room=?",
            userId,
            roomId);
    }

    public int deleteRoomRights(long roomId) throws SQLException {
        return database.execute("DELETE FROM rooms_rights WHERE id_room=?", roomId);
    }

    public int deleteRoom(long roomId) throws SQLException {
        return database.execute("DELETE FROM rooms WHERE id=? LIMIT 1", roomId);
    }

    public int insertRoomRight(long userId, long roomId) throws SQLException {
        return database.execute(
            "INSERT IGNORE INTO rooms_rights(id_user,id_room) VALUES(?,?)",
            userId,
            roomId);
    }

    public long doorStatus(long roomId) throws SQLException {
        return database.queryOne(
            "SELECT status_door FROM rooms WHERE id=? LIMIT 1",
            resultSet -> resultSet.getLong(1),
            roomId)
            .orElse(0L);
    }

    public long visibleCategoryId(long categoryId, long rankIndex, long hcLevel) throws SQLException {
        return database.queryOne(
            "SELECT id FROM rooms_categories WHERE id=? AND level_minrequired <= ? "
                + "AND hclevel_minrequired <= ? LIMIT 1",
            resultSet -> resultSet.getLong(1),
            categoryId,
            rankIndex,
            hcLevel)
            .orElse(0L);
    }

    public int insertRoomBan(long roomId, long userId) throws SQLException {
        return database.execute(
            "INSERT IGNORE INTO rooms_bans(id_room,id_user,timestamp_expire) VALUES(?,?,UNIX_TIMESTAMP()+900)",
            roomId,
            userId);
    }

    public Optional<OfficialRoomModel> officialRoomModel(long roomId) throws SQLException {
        return database.queryOne(
            "SELECT rooms.id,rooms_official.id,models.required_files,rooms_official.caption "
                + "FROM rooms_official,rooms,models WHERE rooms.id=? "
                + "AND rooms_official.id_room=rooms.id AND models.id=rooms.id_model AND models.type='1' LIMIT 1",
            resultSet -> new OfficialRoomModel(
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getString(3),
                resultSet.getString(4)),
            roomId);
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

    public String eventRow(long roomId, String timeFormat) throws SQLException {
        return database.queryOne(
            "SELECT users.id,users.name,rooms_events.id_room,rooms_events.id_category,"
                + "rooms_events.name,rooms_events.description,DATE_FORMAT(FROM_UNIXTIME(rooms_events.timestamp), ?),"
                + "rooms_events.tag_1,rooms_events.tag_2 FROM rooms_events,users WHERE rooms_events.id_room=? "
                + "AND users.id=rooms_events.id_user LIMIT 1",
            resultSet -> resultSet.getString(1) + "\t" + resultSet.getString(2) + "\t" + resultSet.getString(3)
                + "\t" + resultSet.getString(4) + "\t" + resultSet.getString(5) + "\t" + resultSet.getString(6)
                + "\t" + resultSet.getString(7) + "\t" + resultSet.getString(8) + "\t" + resultSet.getString(9),
            timeFormat,
            roomId)
            .orElse("");
    }

    public record ActiveRoomVisit(long visitId, long roomId, long slotId) {
    }

    public record RoomEntryState(
        long visitorsNow,
        long visitorsMax,
        long doorStatus,
        String password,
        long roomSlot,
        long ownerUserId
    ) {
    }

    public record OfficialRoomModel(long roomId, long officialId, String requiredFiles, String caption) {
    }

    private static String nullableText(String value) {
        return value == null || value.isEmpty() ? null : value;
    }
}
