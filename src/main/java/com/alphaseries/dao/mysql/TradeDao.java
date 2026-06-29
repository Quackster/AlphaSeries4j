package com.alphaseries.dao.mysql;

import com.alphaseries.db.Database;

import java.sql.SQLException;

public final class TradeDao {
    private final Database database;

    public TradeDao(Database database) {
        this.database = database;
    }

    public int transferInventoryFurniture(String furnitureIds, long fromUserId, long toUserId) throws SQLException {
        if (furnitureIds == null || furnitureIds.isEmpty()) {
            return 0;
        }
        return database.execute(
            "UPDATE furnitures SET id_owner=? WHERE id IN (" + furnitureIds + ") AND id_owner=? AND id_room IS NULL",
            toUserId,
            fromUserId);
    }

    public int insertTradeLog(
        long userId,
        long partnerId,
        String userItems,
        String partnerItems,
        long roomId,
        String sessionId
    ) throws SQLException {
        return database.execute(
            "INSERT INTO logs_trading(id_user,id_partner,items_user,items_partner,id_room,timestamp,id_session) "
                + "VALUES(?,?,?,?,?,UNIX_TIMESTAMP(),?)",
            userId,
            partnerId,
            userItems,
            partnerItems,
            roomId,
            sessionId);
    }
}
