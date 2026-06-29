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

    public record InventoryFurniture(long productId, long ownerId, String itemData, long secondaryValue) {
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
}
