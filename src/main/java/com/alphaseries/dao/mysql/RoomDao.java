package com.alphaseries.dao.mysql;

import com.alphaseries.db.Database;
import com.alphaseries.game.navigator.NavigatorRoom;
import com.alphaseries.game.navigator.NavigatorTagPopularity;
import com.alphaseries.game.navigator.NewFriendRooms;
import com.alphaseries.game.navigator.OfficialNavigatorItem;
import com.alphaseries.game.room.RoomModelFurnitureRow;
import com.alphaseries.game.room.RoomOccupantRow;
import com.alphaseries.game.room.RoomUserEntryRow;
import com.alphaseries.game.room.RoomUserProfileRow;
import com.alphaseries.game.room.RoomUserTargetRow;

import java.sql.ResultSet;
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

    public long botCountAtLimited(long roomId, long positionX, long positionY) throws SQLException {
        return database.queryOne(
            "SELECT COUNT(*) FROM bots WHERE id_room=? AND position_x=? AND position_y=? LIMIT 1",
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

    public List<NewFriendRooms.RoomPick> newFriendRoomPicks() throws SQLException {
        return database.query(
            "SELECT rooms.id,models.type FROM rooms_categories,rooms,models "
                + "WHERE rooms_categories.is_newfriends=? AND rooms.id_category=rooms_categories.id "
                + "AND models.id=rooms.id_model ORDER BY rooms.visitors_now DESC LIMIT 15",
            resultSet -> new NewFriendRooms.RoomPick(
                resultSet.getLong(1),
                resultSet.getLong(2)),
            1L);
    }

    public Optional<NavigatorRoom> navigatorRoom(long roomId) throws SQLException {
        return database.queryOne(
            "SELECT rooms.id,rooms.name,users.name,rooms.status_door,"
                + "rooms.visitors_now,rooms.visitors_max,rooms.description,rooms_categories.has_trading,NULL,"
                + "rooms.rate,rooms.id_category,rooms.icon,rooms.tag_1,rooms.tag_2,rooms.allow_otherspets,"
                + "rooms.is_staff_picked FROM users,rooms,rooms_categories WHERE rooms.id=? "
                + "AND users.id=rooms.id_owner AND rooms_categories.id=rooms.id_category LIMIT 1",
            resultSet -> new NavigatorRoom(
                resultSet.getLong(1),
                resultSet.getString(2),
                resultSet.getString(3),
                resultSet.getLong(4),
                resultSet.getLong(5),
                resultSet.getLong(6),
                resultSet.getString(7),
                resultSet.getLong(8),
                resultSet.getLong(10),
                resultSet.getLong(11),
                resultSet.getString(12),
                resultSet.getString(13),
                resultSet.getString(14),
                resultSet.getLong(15),
                resultSet.getLong(16)),
            roomId);
    }

    public List<NavigatorTagPopularity> navigatorTagPopularities(long limit) throws SQLException {
        return database.query(
            "SELECT SUM(get_one) as get_one,get_two FROM (SELECT SUM(rooms.visitors_now) as get_one,"
                + "rooms.tag_1 as get_two FROM rooms,users WHERE rooms.tag_1 != '' AND rooms.visitors_max > 0 "
                + "AND users.id=rooms.id_owner GROUP BY 2 UNION ALL SELECT SUM(rooms.visitors_now) as get_one,"
                + "rooms.tag_2 as get_two FROM rooms,users WHERE rooms.tag_2 != '' AND rooms.visitors_max > 0 "
                + "AND users.id=rooms.id_owner GROUP BY 2) as a GROUP BY get_two ORDER BY 1 DESC LIMIT ?",
            resultSet -> new NavigatorTagPopularity(
                resultSet.getLong(1),
                resultSet.getString(2)),
            limit);
    }

    public List<OfficialNavigatorItem> officialNavigatorItems() throws SQLException {
        return database.query(officialNavigatorQuery(), resultSet -> new OfficialNavigatorItem(
            resultSet.getLong(1),
            resultSet.getLong(2),
            resultSet.getLong(3),
            resultSet.getString(4),
            resultSet.getString(5),
            resultSet.getString(6),
            resultSet.getString(7),
            resultSet.getString(8),
            resultSet.getString(9),
            resultSet.getString(10),
            resultSet.getString(11),
            resultSet.getString(12),
            resultSet.getString(13),
            resultSet.getString(14),
            resultSet.getString(15),
            resultSet.getString(16),
            resultSet.getString(17),
            resultSet.getString(18),
            resultSet.getString(19),
            resultSet.getString(20),
            resultSet.getString(21),
            resultSet.getString(22),
            resultSet.getString(23),
            resultSet.getString(24),
            resultSet.getString(25),
            resultSet.getLong(26),
            resultSet.getLong(27),
            resultSet.getLong(28)));
    }

    public long ownedRoomCount(long ownerId) throws SQLException {
        return database.queryOne(
            "SELECT COUNT(id) FROM rooms WHERE id_owner=?",
            resultSet -> resultSet.getLong(1),
            ownerId)
            .orElse(0L);
    }

    public Optional<CreatableRoomModel> creatableRoomModel(long hcLevel, String modelName) throws SQLException {
        return database.queryOne(
            "SELECT id,visitors_max FROM models WHERE create_min_level_hc <= ? AND type=? AND name=? LIMIT 1",
            resultSet -> new CreatableRoomModel(
                resultSet.getLong(1),
                resultSet.getLong(2)),
            hcLevel,
            0L,
            modelName);
    }

    public int insertRoom(long ownerId, String roomName, long visitorsMax, long modelId) throws SQLException {
        return database.execute(
            "INSERT INTO rooms(id_owner,name,visitors_max,id_model,timestamp_created) VALUES(?,?,?,?,UNIX_TIMESTAMP())",
            ownerId,
            roomName,
            visitorsMax,
            modelId);
    }

    public long newestRoomId() throws SQLException {
        return database.queryOne(
            "SELECT MAX(id) FROM rooms",
            resultSet -> resultSet.getLong(1))
            .orElse(0L);
    }

    public long roomSlot(long roomId) throws SQLException {
        return database.queryOne(
            "SELECT id_slot FROM rooms WHERE id=? LIMIT 1",
            resultSet -> resultSet.getLong(1),
            roomId)
            .orElse(0L);
    }

    public long roomOwnerId(long roomId) throws SQLException {
        return database.queryOne(
            "SELECT id_owner FROM rooms WHERE id=? LIMIT 1",
            resultSet -> resultSet.getLong(1),
            roomId)
            .orElse(0L);
    }

    public long staffPickedState(long roomId) throws SQLException {
        return database.queryOne(
            "SELECT is_staff_picked FROM rooms WHERE id=? LIMIT 1",
            resultSet -> resultSet.getLong(1),
            roomId)
            .orElse(0L);
    }

    public int deleteStaffPickedOfficialRoom(long categoryId, long roomId) throws SQLException {
        return database.execute(
            "DELETE FROM rooms_official WHERE id_parent=? AND id_room=? LIMIT 1",
            categoryId,
            roomId);
    }

    public int insertStaffPickedOfficialRoom(long categoryId, long roomId, long styleId, long iconId) throws SQLException {
        return database.execute(
            "INSERT INTO rooms_official(id_parent,id_room,id_style,id_type,icon) VALUES(?,?,?,?,?)",
            categoryId,
            roomId,
            styleId,
            2L,
            iconId);
    }

    public int updateStaffPickedState(long roomId, long state) throws SQLException {
        return database.execute("UPDATE rooms SET is_staff_picked=? WHERE id=?", state, roomId);
    }

    public String modelHeightmap(long roomId) throws SQLException {
        return database.queryOne(
            "SELECT heightmap FROM models,rooms WHERE rooms.id=? AND models.id=rooms.id_model LIMIT 1",
            resultSet -> resultSet.getString(1),
            roomId)
            .orElse("");
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

    public int insertRoomChatLog(
        long userId,
        long roomId,
        String description,
        long chatType,
        String sessionId
    ) throws SQLException {
        return database.execute(
            "INSERT INTO logs_chat(id_user,id_room,timestamp,description,id_type,id_session) "
                + "VALUES(?,?,UNIX_TIMESTAMP(),?,?,?)",
            userId,
            roomId,
            description,
            chatType,
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

    public boolean hasRatedRoom(long userId, long roomId) throws SQLException {
        return database.queryOne(
            "SELECT id_user FROM rooms_rates WHERE id_user=? AND id_room=? LIMIT 1",
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

    public Optional<RoomUserEntryRow> roomUserEntry(long userId, long roomId) throws SQLException {
        return database.queryOne(
            "SELECT users.id,users.name,users.figure,users.motto,users.gender,"
                + "models.position_x,models.position_y,rooms.id_slot FROM users,rooms,models "
                + "WHERE users.id=? AND rooms.id=? AND models.id=rooms.id_model LIMIT 1",
            resultSet -> new RoomUserEntryRow(
                resultSet.getLong(1),
                resultSet.getString(2),
                resultSet.getString(3),
                resultSet.getString(4),
                resultSet.getString(5),
                resultSet.getLong(6),
                resultSet.getLong(7),
                resultSet.getLong(8)),
            userId,
            roomId);
    }

    public List<RoomOccupantRow> activeRoomOccupants(long roomId) throws SQLException {
        return database.query(
            "SELECT logs_visitedrooms.id,users.id,users.name,users.figure,users.motto,"
                + "users.gender,models.position_x,models.position_y,users.id_socket "
                + "FROM logs_visitedrooms,users,rooms,models WHERE logs_visitedrooms.id_room=? "
                + "AND logs_visitedrooms.timestamp_left IS NULL AND users.id=logs_visitedrooms.id_user "
                + "AND rooms.id=logs_visitedrooms.id_room AND models.id=rooms.id_model "
                + "ORDER BY logs_visitedrooms.timestamp_enter ASC LIMIT 250",
            resultSet -> new RoomOccupantRow(
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getString(3),
                resultSet.getString(4),
                resultSet.getString(5),
                resultSet.getString(6),
                resultSet.getLong(7),
                resultSet.getLong(8),
                resultSet.getLong(9)),
            roomId);
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

    public long modelIdByRoom(long roomId) throws SQLException {
        return database.queryOne(
            "SELECT id_model FROM rooms WHERE id=? LIMIT 1",
            resultSet -> resultSet.getLong(1),
            roomId)
            .orElse(0L);
    }

    public List<RoomModelFurnitureRow> modelFurnitureRows(long modelId) throws SQLException {
        return database.query(
            "SELECT id_type,id_source,id_sprite,position_x,position_y,position_z,"
                + "action,action_rotation,action_height FROM models_furnitures WHERE id_model=? LIMIT 500",
            resultSet -> new RoomModelFurnitureRow(
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getString(3),
                resultSet.getLong(4),
                resultSet.getLong(5),
                resultSet.getLong(6),
                resultSet.getString(7),
                resultSet.getLong(8),
                resultSet.getLong(9)),
            modelId);
    }

    public List<Long> activeSocketIndexesByRoom(long roomId) throws SQLException {
        return database.query(
            "SELECT users.id_socket FROM logs_visitedrooms,users WHERE logs_visitedrooms.id_room=? "
                + "AND logs_visitedrooms.timestamp_left IS NULL AND users.id=logs_visitedrooms.id_user "
                + "AND users.id_socket IS NOT NULL",
            resultSet -> resultSet.getLong(1),
            roomId);
    }

    public List<Long> activeSocketIndexesByRoomWithFallback(long roomId) throws SQLException {
        List<Long> socketIndexes = activeSocketIndexesByRoom(roomId);
        if (!socketIndexes.isEmpty()) {
            return socketIndexes;
        }
        return database.query(
            "SELECT id_socket FROM users WHERE id_socket IS NOT NULL AND id IN "
                + "(SELECT id_user FROM logs_visitedrooms WHERE id_room=? AND timestamp_left IS NULL)",
            resultSet -> resultSet.getLong(1),
            roomId);
    }

    public List<Long> activeVisitIdsByRoom(long roomId) throws SQLException {
        return database.query(
            "SELECT id FROM logs_visitedrooms WHERE id_room=? AND timestamp_left IS NULL LIMIT 250",
            resultSet -> resultSet.getLong(1),
            roomId);
    }

    public Optional<RoomUserProfileRow> activeRoomUserProfileByVisitId(long roomId, long visitId) throws SQLException {
        return database.queryOne(
            "SELECT logs_visitedrooms.id,users.name,users.motto,users.achievement_score,users.figure "
                + "FROM logs_visitedrooms,users WHERE logs_visitedrooms.id=? AND logs_visitedrooms.id_room=? "
                + "AND logs_visitedrooms.timestamp_left IS NULL AND users.id=logs_visitedrooms.id_user LIMIT 1",
            RoomDao::roomUserProfileRow,
            visitId,
            roomId);
    }

    public Optional<RoomUserProfileRow> activeRoomUserProfileByUserId(long roomId, long userId) throws SQLException {
        return database.queryOne(
            "SELECT logs_visitedrooms.id,users.name,users.motto,users.achievement_score,users.figure "
                + "FROM logs_visitedrooms,users WHERE logs_visitedrooms.id_user=? AND logs_visitedrooms.id_room=? "
                + "AND logs_visitedrooms.timestamp_left IS NULL AND users.id=logs_visitedrooms.id_user LIMIT 1",
            RoomDao::roomUserProfileRow,
            userId,
            roomId);
    }

    public Optional<RoomUserTargetRow> activeRoomUserTargetByVisitId(long roomId, long visitId) throws SQLException {
        return database.queryOne(
            "SELECT logs_visitedrooms.id,logs_visitedrooms.id_user,users.id_socket "
                + "FROM logs_visitedrooms,users WHERE logs_visitedrooms.id=? AND logs_visitedrooms.id_room=? "
                + "AND logs_visitedrooms.timestamp_left IS NULL AND users.id=logs_visitedrooms.id_user LIMIT 1",
            RoomDao::roomUserTargetRow,
            visitId,
            roomId);
    }

    public Optional<RoomUserTargetRow> activeRoomUserTargetByUserId(long roomId, long userId) throws SQLException {
        return database.queryOne(
            "SELECT logs_visitedrooms.id,logs_visitedrooms.id_user,users.id_socket "
                + "FROM logs_visitedrooms,users WHERE logs_visitedrooms.id_user=? AND logs_visitedrooms.id_room=? "
                + "AND logs_visitedrooms.timestamp_left IS NULL AND users.id=logs_visitedrooms.id_user LIMIT 1",
            RoomDao::roomUserTargetRow,
            userId,
            roomId);
    }

    public List<Long> activeRightHolderSocketIndexes(long roomId) throws SQLException {
        return database.query(
            "SELECT users.id_socket FROM rooms_rights,users WHERE rooms_rights.id_room=? "
                + "AND users.id=rooms_rights.id_user AND users.id_socket IS NOT NULL",
            resultSet -> resultSet.getLong(1),
            roomId);
    }

    private static RoomUserProfileRow roomUserProfileRow(ResultSet resultSet) throws SQLException {
        return new RoomUserProfileRow(
            resultSet.getLong(1),
            resultSet.getString(2),
            resultSet.getString(3),
            resultSet.getLong(4),
            resultSet.getString(5));
    }

    private static RoomUserTargetRow roomUserTargetRow(ResultSet resultSet) throws SQLException {
        return new RoomUserTargetRow(
            resultSet.getLong(1),
            resultSet.getLong(2),
            resultSet.getLong(3));
    }

    public List<ActiveRoomEffect> activeRoomEffects(long roomId) throws SQLException {
        return database.query(
            "SELECT logs_visitedrooms.id,users_effects.id_effect "
                + "FROM logs_visitedrooms,users_effects WHERE logs_visitedrooms.id_room=? "
                + "AND logs_visitedrooms.timestamp_left IS NULL AND users_effects.id_user=logs_visitedrooms.id_user "
                + "AND users_effects.timestamp_expire IS NOT NULL AND users_effects.timestamp_expire>UNIX_TIMESTAMP() "
                + "ORDER BY logs_visitedrooms.timestamp_enter ASC LIMIT 250",
            resultSet -> new ActiveRoomEffect(
                resultSet.getLong(1),
                resultSet.getLong(2)),
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

    public int updateDecoration(long roomId, RoomDecoration decoration, String value) throws SQLException {
        if (decoration == null) {
            return 0;
        }
        return database.execute("UPDATE rooms SET " + decoration.columnName() + "=? WHERE id=?", value, roomId);
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

    public List<Long> favouriteRoomIds(long userId, long limit) throws SQLException {
        long rowLimit = Math.max(1L, limit);
        return database.query(
            "SELECT id_room FROM rooms_favourites WHERE id_user=? LIMIT " + rowLimit,
            resultSet -> resultSet.getLong(1),
            userId);
    }

    public int deleteFavouriteRoom(long userId, long roomId) throws SQLException {
        return database.execute(
            "DELETE FROM rooms_favourites WHERE id_room=? AND id_user=?",
            roomId,
            userId);
    }

    public int insertFavouriteRoom(long userId, long roomId) throws SQLException {
        return database.execute(
            "INSERT INTO rooms_favourites(id_user,id_room,timestamp) VALUES(?,?,UNIX_TIMESTAMP())",
            userId,
            roomId);
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

    public Optional<RoomModelEntry> roomModelEntry(long roomId) throws SQLException {
        return database.queryOne(
            "SELECT rooms.id,rooms.id_slot,NULL,models.name,models.id,rooms.id_floor,"
                + "rooms.id_wallpaper,rooms.id_landscape,rooms.rate,models.map,models.position_x,models.position_y,NULL,"
                + "rooms.name,rooms.disable_walls,rooms.allow_otherspets,rooms.allow_walkthrough,rooms.allow_feedpets,models.type,"
                + "rooms.visitors_primaryid FROM rooms,models WHERE rooms.id=? AND models.id=rooms.id_model LIMIT 1",
            resultSet -> new RoomModelEntry(
                resultSet.getLong(5),
                resultSet.getString(10)),
            roomId);
    }

    public Optional<RoomPresentationState> roomPresentationState(long roomId) throws SQLException {
        return database.queryOne(
            "SELECT rooms.id,rooms.id_slot,users.id,models.name,models.id,rooms.id_floor,"
                + "rooms.id_wallpaper,rooms.id_landscape,rooms.rate,models.map,models.position_x,models.position_y,NULL,"
                + "rooms.name,rooms.disable_walls,rooms.allow_otherspets,rooms.allow_walkthrough,rooms.allow_feedpets,models.type,"
                + "rooms.visitors_primaryid,rooms.is_staff_picked,thickness_floor,thickness_wallpaper FROM rooms,models,users "
                + "WHERE rooms.id=? AND users.id=rooms.id_owner AND models.id=rooms.id_model LIMIT 1",
            resultSet -> new RoomPresentationState(
                resultSet.getLong(3),
                resultSet.getLong(5),
                resultSet.getString(6),
                resultSet.getString(7),
                resultSet.getString(8),
                resultSet.getLong(9),
                resultSet.getString(10),
                resultSet.getLong(15),
                resultSet.getLong(22),
                resultSet.getLong(23)),
            roomId);
    }

    public Optional<RoomPlacementState> roomPlacementState(long roomId) throws SQLException {
        return database.queryOne(
            "SELECT models.map,rooms.allow_walkthrough,rooms.id_slot FROM rooms,models WHERE rooms.id=? "
                + "AND models.id=rooms.id_model LIMIT 1",
            resultSet -> new RoomPlacementState(
                resultSet.getString(1),
                resultSet.getLong(2),
                resultSet.getLong(3)),
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

    public record ActiveRoomEffect(long roomUserIndex, long effectId) {
    }

    public record CreatableRoomModel(long modelId, long visitorsMax) {
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

    public record RoomModelEntry(long modelId, String modelMap) {
    }

    public record RoomPresentationState(
        long ownerUserId,
        long modelId,
        String floorPattern,
        String wallpaperPattern,
        String landscapePattern,
        long roomRate,
        String modelMap,
        long disableWalls,
        long thicknessFloor,
        long thicknessWallpaper
    ) {
    }

    public record RoomPlacementState(String modelMap, long allowWalkthrough, long roomSlot) {
    }

    public enum RoomDecoration {
        WALLPAPER("wallpaper", "id_wallpaper"),
        FLOOR("floor", "id_floor"),
        LANDSCAPE("landscape", "id_landscape");

        private final String wireName;
        private final String columnName;

        RoomDecoration(String wireName, String columnName) {
            this.wireName = wireName;
            this.columnName = columnName;
        }

        public String wireName() {
            return wireName;
        }

        public String columnName() {
            return columnName;
        }

        public static RoomDecoration fromProductType(long productType) {
            if (productType == 2L) {
                return WALLPAPER;
            }
            if (productType == 3L) {
                return FLOOR;
            }
            if (productType == 4L) {
                return LANDSCAPE;
            }
            return null;
        }
    }

    private static String nullableText(String value) {
        return value == null || value.isEmpty() ? null : value;
    }

    private static String officialNavigatorQuery() {
        String separator = " UNION ALL ";
        StringBuilder queryText = new StringBuilder();
        queryText.append("SELECT rooms_official.id_type,rooms_official.id_style,rooms_official.icon,");
        queryText.append("rooms_official.caption,rooms_official.caption_2,rooms_official.caption_3,");
        queryText.append("NULL,rooms.id,rooms.name,users.name,rooms.status_door,rooms.visitors_now,");
        queryText.append("rooms.visitors_max,rooms.description,rooms_categories.has_trading,NULL,");
        queryText.append("rooms.rate,rooms.id_category,rooms.icon,rooms.tag_1,rooms.tag_2,");
        queryText.append("rooms.allow_otherspets,NULL,NULL,NULL,rooms_official.id_parent,");
        queryText.append("rooms_official.id,rooms_official.requires_level_in FROM users,rooms,");
        queryText.append("rooms_categories,rooms_official WHERE rooms_official.id_type='2' ");
        queryText.append("AND rooms_official.id_room IS NOT NULL AND rooms.id=rooms_official.id_room ");
        queryText.append("AND users.id=rooms.id_owner AND rooms_categories.id=rooms.id_category ");
        queryText.append("GROUP BY rooms_official.id");

        queryText.append(separator).append("SELECT rooms_official.id_type,rooms_official.id_style,");
        queryText.append("rooms_official.icon,rooms_official.caption,rooms_official.caption_2,");
        queryText.append("rooms_official.caption_3,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,");
        queryText.append("NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,rooms_official.id_parent,");
        queryText.append("rooms_official.id,rooms_official.requires_level_in FROM rooms_official ");
        queryText.append("WHERE rooms_official.id_type='1' GROUP BY rooms_official.id");

        queryText.append(separator).append("SELECT rooms_official.id_type,rooms_official.id_style,");
        queryText.append("rooms_official.icon,rooms_official.caption,rooms_official.caption_2,");
        queryText.append("rooms_official.caption_3,NULL,rooms.id,rooms.name,NULL,rooms.status_door,");
        queryText.append("rooms.visitors_now,rooms.visitors_max,rooms.description,");
        queryText.append("rooms_categories.has_trading,NULL,rooms.rate,rooms.id_category,rooms.icon,");
        queryText.append("rooms.tag_1,rooms.tag_2,rooms.allow_otherspets,models.name,");
        queryText.append("models.required_files,models.visitors_max,rooms_official.id_parent,");
        queryText.append("rooms_official.id,rooms_official.requires_level_in FROM models,rooms,");
        queryText.append("rooms_categories,rooms_official WHERE rooms_official.id_type='3' ");
        queryText.append("AND rooms_official.id_room IS NOT NULL AND rooms.id=rooms_official.id_room ");
        queryText.append("AND models.id=rooms.id_model AND rooms_categories.id=rooms.id_category ");
        queryText.append("GROUP BY rooms_official.id");

        queryText.append(separator).append("SELECT rooms_official.id_type,rooms_official.id_style,");
        queryText.append("rooms_official.icon,rooms_official.caption,rooms_official.caption_2,");
        queryText.append("rooms_official.caption_3,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,");
        queryText.append("NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,rooms_official.id_parent,");
        queryText.append("rooms_official.id,rooms_official.requires_level_in FROM rooms_official ");
        queryText.append("WHERE rooms_official.id_type='4' GROUP BY rooms_official.id ");
        queryText.append("ORDER BY 27 ASC LIMIT 255");
        return queryText.toString();
    }
}
