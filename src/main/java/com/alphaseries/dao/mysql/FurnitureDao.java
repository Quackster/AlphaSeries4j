package com.alphaseries.dao.mysql;

import com.alphaseries.db.Database;

import java.sql.SQLException;
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

    public Optional<PendingFurnitureState> pendingFurnitureState(long furnitureId) throws SQLException {
        return database.queryOne(
            "SELECT id_room,sign FROM furnitures WHERE id=? LIMIT 1",
            resultSet -> new PendingFurnitureState(
                resultSet.getLong(1),
                resultSet.getLong(2)),
            furnitureId);
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

    public record PendingFurnitureState(long roomId, long sign) {
    }
}
