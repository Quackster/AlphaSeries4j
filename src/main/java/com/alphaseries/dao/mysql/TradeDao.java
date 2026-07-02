package com.alphaseries.dao.mysql;

import com.alphaseries.db.Database;

import java.sql.SQLException;
import java.util.List;

public final class TradeDao {
    private final Database database;

    public TradeDao(Database database) {
        this.database = database;
    }

    public int transferInventoryFurniture(List<Long> furnitureIds, long fromUserId, long toUserId) throws SQLException {
        if (furnitureIds == null || furnitureIds.isEmpty()) {
            return 0;
        }
        String placeholders = SqlFragments.placeholders(furnitureIds.size());
        return database.execute(
            "UPDATE furnitures SET id_owner=? WHERE id IN (" + placeholders + ") AND id_owner=? AND id_room IS NULL",
            SqlFragments.parametersWithIds(List.of(toUserId), furnitureIds, List.of(fromUserId)));
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
