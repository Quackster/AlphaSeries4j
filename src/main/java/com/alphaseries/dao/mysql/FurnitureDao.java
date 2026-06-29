package com.alphaseries.dao.mysql;

import com.alphaseries.db.Database;
import com.alphaseries.game.inventory.InventoryItemRow;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public final class FurnitureDao {
    private final Database database;

    public FurnitureDao(Database database) {
        this.database = database;
    }

    public Optional<RoomFurniture> roomFurniture(long furnitureId, long roomId) throws SQLException {
        return database.queryOne(
            "SELECT id,id_product,sign,caption FROM furnitures WHERE id=? AND id_room=? LIMIT 1",
            resultSet -> new RoomFurniture(
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getString(3),
                resultSet.getString(4)),
            furnitureId,
            roomId);
    }

    public Optional<RoomFurnitureWithWall> roomFurnitureWithWall(long furnitureId, long roomId) throws SQLException {
        return database.queryOne(
            "SELECT id,id_product,sign,caption,position_wall FROM furnitures WHERE id=? AND id_room=? LIMIT 1",
            resultSet -> new RoomFurnitureWithWall(
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getString(3),
                resultSet.getString(4),
                resultSet.getString(5)),
            furnitureId,
            roomId);
    }

    public Optional<GiftBoxFurniture> giftBox(long furnitureId, long roomId) throws SQLException {
        return database.queryOne(
            "SELECT id,id_product,id_destination,sign_extra FROM furnitures WHERE id=? AND id_room=? LIMIT 1",
            resultSet -> new GiftBoxFurniture(
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getLong(3),
                resultSet.getString(4)),
            furnitureId,
            roomId);
    }

    public Optional<WallStateFurniture> wallState(long furnitureId, long roomId) throws SQLException {
        return database.queryOne(
            "SELECT id,id_product,sign,position_wall FROM furnitures WHERE id=? AND id_room=? "
                + "AND position_wall IS NOT NULL LIMIT 1",
            resultSet -> new WallStateFurniture(
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getString(3),
                resultSet.getString(4)),
            furnitureId,
            roomId);
    }

    public Optional<RoomFurnitureProduct> roomFurnitureProduct(long furnitureId, long roomId) throws SQLException {
        return database.queryOne(
            "SELECT id_product FROM furnitures WHERE id_room=? AND id=? LIMIT 1",
            resultSet -> new RoomFurnitureProduct(resultSet.getLong(1)),
            roomId,
            furnitureId);
    }

    public Optional<RoomFurnitureProduct> roomFurnitureProductById(long furnitureId, long roomId) throws SQLException {
        return database.queryOne(
            "SELECT id_product FROM furnitures WHERE id=? AND id_room=? LIMIT 1",
            resultSet -> new RoomFurnitureProduct(resultSet.getLong(1)),
            furnitureId,
            roomId);
    }

    public Optional<RoomFurnitureOwnerProduct> roomFurnitureOwnerProduct(long furnitureId, long roomId) throws SQLException {
        return database.queryOne(
            "SELECT id_product,id_owner FROM furnitures WHERE id=? AND id_room=? LIMIT 1",
            resultSet -> new RoomFurnitureOwnerProduct(
                resultSet.getLong(1),
                resultSet.getLong(2)),
            furnitureId,
            roomId);
    }

    public Optional<FloorStateFurniture> floorStateFurniture(long furnitureId, long roomId) throws SQLException {
        return database.queryOne(
            "SELECT id_product,sign FROM furnitures WHERE id=? AND id_room=? AND position_wall IS NULL LIMIT 1",
            resultSet -> new FloorStateFurniture(
                resultSet.getLong(1),
                resultSet.getString(2)),
            furnitureId,
            roomId);
    }

    public Optional<SimpleFloorFurniture> simpleFloorFurniture(long furnitureId, long roomId) throws SQLException {
        return database.queryOne(
            "SELECT id,position_x,position_y,id_product FROM furnitures WHERE id=? AND id_room=? LIMIT 1",
            resultSet -> new SimpleFloorFurniture(
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getLong(3),
                resultSet.getLong(4)),
            furnitureId,
            roomId);
    }

    public List<FloorPositionFurniture> floorFurnitureAt(long roomId, long positionX, long positionY) throws SQLException {
        return database.query(
            "SELECT id,id_room,id_product,sign FROM furnitures WHERE id_room=? AND position_x=? AND position_y=? "
                + "AND position_wall IS NULL LIMIT 250",
            resultSet -> new FloorPositionFurniture(
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getLong(3),
                resultSet.getString(4)),
            roomId,
            positionX,
            positionY);
    }

    public List<FloorPositionFurniture> floorFurnitureAt(long positionX, long positionY) throws SQLException {
        return database.query(
            "SELECT id,id_room,id_product,sign FROM furnitures WHERE position_x=? AND position_y=? "
                + "AND position_wall IS NULL LIMIT 250",
            resultSet -> new FloorPositionFurniture(
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getLong(3),
                resultSet.getString(4)),
            positionX,
            positionY);
    }

    public Optional<RoomFurnitureState> roomFurnitureState(long furnitureId) throws SQLException {
        return database.queryOne(
            "SELECT id_room,id_product,sign FROM furnitures WHERE id=? LIMIT 1",
            resultSet -> new RoomFurnitureState(
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getString(3)),
            furnitureId);
    }

    public Optional<LocatedFurnitureState> locatedFurnitureState(long furnitureId) throws SQLException {
        return database.queryOne(
            "SELECT id_room,id_product,sign FROM furnitures WHERE id=? LIMIT 1",
            resultSet -> new LocatedFurnitureState(
                furnitureId,
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getString(3)),
            furnitureId);
    }

    public Optional<LocatedFurnitureState> newestLocatedFurnitureStateByProduct(long productId) throws SQLException {
        return database.queryOne(
            "SELECT id,id_room,id_product,sign FROM furnitures WHERE id_product=? ORDER BY id DESC LIMIT 1",
            resultSet -> new LocatedFurnitureState(
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getLong(3),
                resultSet.getString(4)),
            productId);
    }

    public long newestFurnitureIdByRoomAndProduct(long roomId, long productId) throws SQLException {
        return database.queryOne(
            "SELECT id FROM furnitures WHERE id_room=? AND id_product=? ORDER BY id DESC LIMIT 1",
            resultSet -> resultSet.getLong(1),
            roomId,
            productId)
            .orElse(0L);
    }

    public boolean existsInRoom(long furnitureId, long roomId) throws SQLException {
        return database.queryOne(
            "SELECT COUNT(*) FROM furnitures WHERE id=? AND id_room=? LIMIT 1",
            resultSet -> resultSet.getLong(1),
            furnitureId,
            roomId)
            .orElse(0L) > 0L;
    }

    public Optional<InventoryFurniture> inventoryFurniture(long furnitureId) throws SQLException {
        return database.queryOne(
            "SELECT id_product,id_owner,sign,id_secondary FROM furnitures WHERE id =? LIMIT 1",
            resultSet -> new InventoryFurniture(
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getString(3),
                resultSet.getLong(4)),
            furnitureId);
    }

    public Optional<InventoryPlacementFurniture> inventoryPlacementFurniture(long furnitureId, long ownerId) throws SQLException {
        return database.queryOne(
            "SELECT id_product,id,sign,id_secondary,id_destination FROM furnitures WHERE id=? AND id_owner=? "
                + "AND id_room IS NULL LIMIT 1",
            resultSet -> new InventoryPlacementFurniture(
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getString(3),
                resultSet.getLong(4),
                resultSet.getLong(5)),
            furnitureId,
            ownerId);
    }

    public Optional<InventoryPlacementFurniture> roomPlacementFurniture(long furnitureId, long roomId) throws SQLException {
        return database.queryOne(
            "SELECT id_product,id,sign,id_secondary,id_destination FROM furnitures WHERE id=? AND id_room=? LIMIT 1",
            resultSet -> new InventoryPlacementFurniture(
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getString(3),
                resultSet.getLong(4),
                resultSet.getLong(5)),
            furnitureId,
            roomId);
    }

    public List<InventoryItemRow> inventoryFurnitureForOwner(long ownerId) throws SQLException {
        return database.query(
            "SELECT id,id_product,sign,id_secondary FROM furnitures WHERE id_owner=? AND id_room IS NULL LIMIT 1000",
            resultSet -> new InventoryItemRow(
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getString(3),
                resultSet.getLong(4)),
            ownerId);
    }

    public Optional<TradeFurniture> tradeFurniture(long furnitureId, long ownerId) throws SQLException {
        return database.queryOne(
            "SELECT id,id_product,sign,id_secondary FROM furnitures WHERE id=? AND id_owner=? AND id_room IS NULL LIMIT 1",
            resultSet -> new TradeFurniture(
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getString(3),
                resultSet.getLong(4)),
            furnitureId,
            ownerId);
    }

    public Optional<TradeFurniture> tradeFurnitureForRemoval(long furnitureId, long ownerId) throws SQLException {
        return database.queryOne(
            "SELECT id,id_product,sign FROM furnitures WHERE id=? AND id_owner=? AND id_room IS NULL LIMIT 1",
            resultSet -> new TradeFurniture(
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getString(3),
                0L),
            furnitureId,
            ownerId);
    }

    public Optional<DecorationFurniture> decorationFurniture(long furnitureId, long ownerId) throws SQLException {
        return database.queryOne(
            "SELECT id,id_product,sign FROM furnitures WHERE id_owner=? AND id=? AND id_room IS NULL LIMIT 1",
            resultSet -> new DecorationFurniture(
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getString(3)),
            ownerId,
            furnitureId);
    }

    public Optional<PendingFurnitureState> pendingFurnitureState(long furnitureId) throws SQLException {
        return database.queryOne(
            "SELECT id_room,sign FROM furnitures WHERE id=? LIMIT 1",
            resultSet -> new PendingFurnitureState(
                resultSet.getLong(1),
                resultSet.getLong(2)),
            furnitureId);
    }

    public List<RollerFurniture> rollerFurnitureInRoom(long roomId) throws SQLException {
        return database.query(
            "SELECT furnitures.id,furnitures.position_x,furnitures.position_y,"
                + "furnitures.position_z,furnitures.position_r FROM furnitures,products WHERE furnitures.id_room=? "
                + "AND furnitures.id_product=products.id AND (products.action LIKE '%roller%' OR "
                + "products.name LIKE '%roller%' OR products.sprite LIKE '%roller%') ORDER BY furnitures.id",
            resultSet -> new RollerFurniture(
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getLong(3),
                resultSet.getString(4),
                resultSet.getLong(5)),
            roomId);
    }

    public List<WallFurniture> wallFurnitureInRoom(long roomId) throws SQLException {
        return database.query(
            "SELECT id,id_product,position_wall,sign,id_secondary FROM furnitures WHERE id_room=? "
                + "AND id_owner IS NULL AND position_wall IS NOT NULL LIMIT 100",
            resultSet -> new WallFurniture(
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getString(3),
                resultSet.getString(4),
                resultSet.getLong(5)),
            roomId);
    }

    public List<DimmerPreset> dimmerPresets(long furnitureId) throws SQLException {
        return database.query(
            "SELECT id_light,id_preset,id_background,colour,id_state FROM furnitures_dimmerpresets "
                + "WHERE id_furni=? LIMIT 3",
            resultSet -> new DimmerPreset(
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getLong(3),
                resultSet.getString(4),
                resultSet.getLong(5)),
            furnitureId);
    }

    public Optional<ActiveDimmerState> activeDimmerState(long furnitureId) throws SQLException {
        return database.queryOne(
            "SELECT furnitures_dimmerpresets.id_light,furnitures_dimmerpresets.id_preset,"
                + "furnitures_dimmerpresets.id_background,furnitures_dimmerpresets.colour,"
                + "furnitures.id_product,furnitures.position_wall,furnitures.sign "
                + "FROM furnitures_dimmerpresets,furnitures WHERE furnitures_dimmerpresets.id_furni=? "
                + "AND furnitures_dimmerpresets.id_state=? AND furnitures.id=furnitures_dimmerpresets.id_furni LIMIT 1",
            resultSet -> new ActiveDimmerState(
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getLong(3),
                resultSet.getString(4),
                resultSet.getLong(5),
                resultSet.getString(6),
                resultSet.getString(7)),
            furnitureId,
            2L);
    }

    public Optional<WallProductPosition> wallProductPosition(long furnitureId) throws SQLException {
        return database.queryOne(
            "SELECT id_product,position_wall FROM furnitures WHERE id=? LIMIT 1",
            resultSet -> new WallProductPosition(
                resultSet.getLong(1),
                resultSet.getString(2)),
            furnitureId);
    }

    public long roomIdByFurniture(long furnitureId) throws SQLException {
        return database.queryOne(
            "SELECT id_room FROM furnitures WHERE id=? LIMIT 1",
            resultSet -> resultSet.getLong(1),
            furnitureId)
            .orElse(0L);
    }

    public long floorFurnitureCountAtExcluding(long roomId, long excludedFurnitureId, long positionX, long positionY)
        throws SQLException {

        return database.queryOne(
            "SELECT COUNT(*) FROM furnitures WHERE id_room=? AND position_wall IS NULL AND position_x=? "
                + "AND position_y=? AND id<>? LIMIT 1",
            resultSet -> resultSet.getLong(1),
            roomId,
            positionX,
            positionY,
            excludedFurnitureId)
            .orElse(0L);
    }

    public int updatePostIt(long furnitureId, String sign, String caption) throws SQLException {
        return database.execute(
            "UPDATE furnitures SET sign=?,caption=? WHERE id=?",
            sign,
            caption,
            furnitureId);
    }

    public int updateSign(long furnitureId, long sign) throws SQLException {
        return database.execute("UPDATE furnitures SET sign=? WHERE id=?", sign, furnitureId);
    }

    public int updateSignLimited(long furnitureId, long sign) throws SQLException {
        return database.execute("UPDATE furnitures SET sign=? WHERE id=? LIMIT 1", sign, furnitureId);
    }

    public int updateSignText(long furnitureId, String sign) throws SQLException {
        return database.execute("UPDATE furnitures SET sign=? WHERE id=?", sign, furnitureId);
    }

    public int updateGiftMetadata(
        long furnitureId,
        String giftMessage,
        String sign,
        long ownerId,
        long catalogProductId,
        long secondaryId
    ) throws SQLException {
        return database.execute(
            "UPDATE furnitures SET sign_extra=?,sign=?,id_owner=?,id_destination=?,id_secondary=? WHERE id=?",
            giftMessage,
            sign,
            ownerId,
            catalogProductId,
            secondaryId,
            furnitureId);
    }

    public int updateRecyclerRewardBox(long ownerId, long boxProductId, String sign, long destinationId) throws SQLException {
        return database.execute(
            "UPDATE furnitures SET sign=?,id_owner=?,id_destination=? "
                + "WHERE id_owner=? AND id_product=? ORDER BY id DESC LIMIT 1",
            sign,
            ownerId,
            destinationId,
            ownerId,
            boxProductId);
    }

    public long recyclableInventoryCount(long ownerId, String selectedFurnitureIds) throws SQLException {
        String whereClause = recyclableInventoryWhereClause(ownerId, selectedFurnitureIds);
        if (whereClause.isEmpty()) {
            return 0L;
        }
        return database.queryOne(
            "SELECT COUNT(*) FROM furnitures,products WHERE " + whereClause,
            resultSet -> resultSet.getLong(1))
            .orElse(0L);
    }

    public int clearRecyclerItems(long ownerId, String selectedFurnitureIds) throws SQLException {
        if (!isNumericIdList(selectedFurnitureIds)) {
            return 0;
        }
        return database.execute(
            "UPDATE furnitures SET id_owner=NULL WHERE id_owner=? AND id_room IS NULL AND id IN ("
                + selectedFurnitureIds + ")",
            ownerId);
    }

    public int insertRecyclerLog(long userId, String selectedFurnitureIds, long rewardProductId) throws SQLException {
        if (!isNumericIdList(selectedFurnitureIds)) {
            return 0;
        }
        return database.execute(
            "INSERT INTO logs_recycler(id_user,timestamp,items,id_reward,id_session) VALUES(?,UNIX_TIMESTAMP(),?,?,?)",
            userId,
            selectedFurnitureIds,
            rewardProductId,
            0L);
    }

    private static String recyclableInventoryWhereClause(long ownerId, String selectedFurnitureIds) {
        if (!isNumericIdList(selectedFurnitureIds)) {
            return "";
        }
        StringBuilder whereClause = new StringBuilder();
        for (String selectedFurnitureId : selectedFurnitureIds.split(",", -1)) {
            if (whereClause.length() > 0) {
                whereClause.append(" OR ");
            }
            whereClause.append("furnitures.id_owner='").append(ownerId).append("' AND furnitures.id_room IS NULL")
                .append(" AND furnitures.id='").append(selectedFurnitureId).append("' AND products.id=furnitures.id_product")
                .append(" AND products.is_recycleable='1'");
        }
        return whereClause.toString();
    }

    private static boolean isNumericIdList(String selectedFurnitureIds) {
        return selectedFurnitureIds != null && selectedFurnitureIds.matches("\\d+(,\\d+)*");
    }

    public int resetDimmerPresetStates(long furnitureId) throws SQLException {
        return database.execute("UPDATE furnitures_dimmerpresets SET id_state=? WHERE id_furni=?", 1L, furnitureId);
    }

    public int updateDimmerPreset(long furnitureId, long presetId, long lightLevel, long backgroundId, String colour) throws SQLException {
        return database.execute(
            "UPDATE furnitures_dimmerpresets SET id_state=?,id_light=?,id_background=?,colour=? "
                + "WHERE id_furni=? AND id_preset=?",
            2L,
            lightLevel,
            backgroundId,
            colour,
            furnitureId,
            presetId);
    }

    public int updateRoomFurnitureState(long furnitureId, long roomId, long ownerId, long sign) throws SQLException {
        return database.execute(
            "UPDATE furnitures SET sign=?,task_owner=?,task_time=UNIX_TIMESTAMP() WHERE id=? AND id_room=? LIMIT 1",
            sign,
            ownerId,
            furnitureId,
            roomId);
    }

    public int updateRoomPosition(long furnitureId, long roomId, long positionX, long positionY, String positionZ)
        throws SQLException {

        return database.execute(
            "UPDATE furnitures SET position_x=?,position_y=?,position_z=? WHERE id=? AND id_room=? LIMIT 1",
            positionX,
            positionY,
            positionZ,
            furnitureId,
            roomId);
    }

    public int moveRoomFurnitureToInventory(long furnitureId, long ownerId) throws SQLException {
        return database.execute(
            "UPDATE furnitures SET id_room=NULL,position_x=NULL,position_y=NULL,position_z=NULL,"
                + "position_r='0',position_wall=NULL,id_owner=?,task_owner=?,task_time=UNIX_TIMESTAMP() "
                + "WHERE id=? LIMIT 1",
            ownerId,
            ownerId,
            furnitureId);
    }

    public int moveRoomFurnitureToInventory(long furnitureId, long roomId, long ownerId) throws SQLException {
        return database.execute(
            "UPDATE furnitures SET id_room=NULL,position_x=NULL,position_y=NULL,position_z=NULL,"
                + "position_r='0',position_wall=NULL,id_owner=?,task_owner=?,task_time=UNIX_TIMESTAMP() "
                + "WHERE id=? AND id_room=? LIMIT 1",
            ownerId,
            ownerId,
            furnitureId,
            roomId);
    }

    public int placeWallFurniture(long furnitureId, long ownerId, long roomId, String wallPosition) throws SQLException {
        return database.execute(
            "UPDATE furnitures SET position_wall=?,id_room=?,id_owner=NULL,task_owner=?,task_time=UNIX_TIMESTAMP() "
                + "WHERE id=? AND id_owner=? AND id_room IS NULL LIMIT 1",
            wallPosition,
            roomId,
            ownerId,
            furnitureId,
            ownerId);
    }

    public int placeFloorFurniture(
        long furnitureId,
        long ownerId,
        long roomId,
        long positionX,
        long positionY,
        String positionZ,
        long rotation
    ) throws SQLException {
        return database.execute(
            "UPDATE furnitures SET id_owner=NULL,id_room=?,position_x=?,position_y=?,position_z=?,position_r=?,"
                + "position_wall=NULL,task_owner=?,task_time=UNIX_TIMESTAMP() "
                + "WHERE id=? AND id_owner=? AND id_room IS NULL LIMIT 1",
            roomId,
            positionX,
            positionY,
            positionZ,
            rotation,
            ownerId,
            furnitureId,
            ownerId);
    }

    public int moveFloorFurniture(
        long furnitureId,
        long roomId,
        long ownerId,
        long positionX,
        long positionY,
        String positionZ,
        long rotation
    ) throws SQLException {
        return database.execute(
            "UPDATE furnitures SET position_x=?,position_y=?,position_z=?,position_r=?,position_wall=NULL,"
                + "task_owner=?,task_time=UNIX_TIMESTAMP() WHERE id=? AND id_room=? LIMIT 1",
            positionX,
            positionY,
            positionZ,
            rotation,
            ownerId,
            furnitureId,
            roomId);
    }

    public int deleteFurniture(long furnitureId) throws SQLException {
        return database.execute("DELETE FROM furnitures WHERE id=? LIMIT 1", furnitureId);
    }

    public int insertInventoryFurniture(long productId, long ownerId, String sign) throws SQLException {
        return database.execute(
            "INSERT INTO furnitures(id_product,id_owner,sign,task_owner,task_time) VALUES(?,?,?,?,UNIX_TIMESTAMP())",
            productId,
            ownerId,
            sign,
            ownerId);
    }

    public int insertCatalogFurniture(long productId, long ownerId, String sign, long catalogProductId) throws SQLException {
        return database.execute(
            "INSERT INTO furnitures(id_product,id_owner,sign,task_owner,task_time,id_ctlgproduct) "
                + "VALUES(?,?,?,?,UNIX_TIMESTAMP(),?)",
            productId,
            ownerId,
            sign,
            ownerId,
            catalogProductId);
    }

    public int insertClubGiftFurniture(long productId, long catalogProductId, long ownerId, String sign) throws SQLException {
        return database.execute(
            "INSERT INTO furnitures(id_product,id_ctlgproduct,id_owner,task_owner,task_time,position_r,sign) "
                + "VALUES(?,?,?,?,UNIX_TIMESTAMP(),?,?)",
            productId,
            catalogProductId,
            ownerId,
            ownerId,
            0L,
            sign);
    }

    public long newestFurnitureIdByOwnerAndProduct(long ownerId, long productId) throws SQLException {
        return database.queryOne(
            "SELECT id FROM furnitures WHERE id_owner=? AND id_product=? ORDER BY id DESC LIMIT 1",
            resultSet -> resultSet.getLong(1),
            ownerId,
            productId)
            .orElse(0L);
    }

    public List<Long> newestFurnitureIdsByOwner(long ownerId, long limit) throws SQLException {
        long effectiveLimit = Math.max(0L, limit);
        if (effectiveLimit <= 0L) {
            return List.of();
        }
        return database.query(
            "SELECT id FROM furnitures WHERE id_owner=? ORDER BY id DESC LIMIT " + effectiveLimit,
            resultSet -> resultSet.getLong(1),
            ownerId);
    }

    public int insertDefaultDimmerPresets(long furnitureId) throws SQLException {
        return database.execute(
            "INSERT INTO furnitures_dimmerpresets(id_furni,id_preset,id_state) VALUES(?,?,?),(?,?,?),(?,?,?)",
            furnitureId,
            1L,
            2L,
            furnitureId,
            2L,
            1L,
            furnitureId,
            3L,
            1L);
    }

    public int updateDefaultDimmerSign(long furnitureId) throws SQLException {
        return database.execute("UPDATE furnitures SET sign=? WHERE id=?", "1,1,1,#000000,166", furnitureId);
    }

    public record RoomFurniture(long furnitureId, long productId, String sign, String caption) {
    }

    public record RoomFurnitureWithWall(long furnitureId, long productId, String sign, String caption, String wallPosition) {
    }

    public record GiftBoxFurniture(long furnitureId, long boxProductId, long openedProductId, String openedSign) {
    }

    public record WallStateFurniture(long furnitureId, long productId, String sign, String wallPosition) {
    }

    public record RoomFurnitureProduct(long productId) {
    }

    public record RoomFurnitureOwnerProduct(long productId, long ownerId) {
    }

    public record FloorStateFurniture(long productId, String sign) {
    }

    public record SimpleFloorFurniture(long furnitureId, long positionX, long positionY, long productId) {
    }

    public record FloorPositionFurniture(long furnitureId, long roomId, long productId, String sign) {
    }

    public record RoomFurnitureState(long roomId, long productId, String sign) {
    }

    public record LocatedFurnitureState(long furnitureId, long roomId, long productId, String sign) {
    }

    public record InventoryFurniture(long productId, long ownerId, String itemData, long secondaryValue) {
    }

    public record InventoryPlacementFurniture(
        long productId,
        long furnitureId,
        String sign,
        long secondaryValue,
        long destinationId
    ) {
    }

    public record TradeFurniture(long furnitureId, long productId, String sign, long secondaryValue) {
    }

    public record DecorationFurniture(long furnitureId, long productId, String sign) {
    }

    public record PendingFurnitureState(long roomId, long sign) {
    }

    public record RollerFurniture(long furnitureId, long positionX, long positionY, String positionZ, long rotation) {
    }

    public record WallFurniture(long furnitureId, long productId, String wallPosition, String sign, long secondaryValue) {
    }

    public record DimmerPreset(long lightLevel, long presetId, long backgroundId, String colour, long stateId) {
    }

    public record ActiveDimmerState(
        long lightLevel,
        long presetId,
        long backgroundId,
        String colour,
        long productId,
        String wallPosition,
        String sign
    ) {
    }

    public record WallProductPosition(long productId, String wallPosition) {
    }
}
