package com.alphaseries.dao.mysql;

import com.alphaseries.db.Database;
import com.alphaseries.util.NumberUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class TradeDao {
    private final Database database;

    public TradeDao(Database database) {
        this.database = database;
    }

    public int transferInventoryFurniture(String furnitureIds, long fromUserId, long toUserId) throws SQLException {
        return transferInventoryFurniture(parseSqlIdList(furnitureIds), fromUserId, toUserId);
    }

    public int transferInventoryFurniture(List<Long> furnitureIds, long fromUserId, long toUserId) throws SQLException {
        if (furnitureIds == null || furnitureIds.isEmpty()) {
            return 0;
        }
        String placeholders = String.join(",", Collections.nCopies(furnitureIds.size(), "?"));
        List<Object> parameters = new ArrayList<>();
        parameters.add(toUserId);
        parameters.addAll(furnitureIds);
        parameters.add(fromUserId);
        return database.execute(
            "UPDATE furnitures SET id_owner=? WHERE id IN (" + placeholders + ") AND id_owner=? AND id_room IS NULL",
            parameters.toArray());
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

    private static List<Long> parseSqlIdList(String furnitureIds) {
        if (furnitureIds == null || furnitureIds.isEmpty()) {
            return List.of();
        }
        List<Long> ids = new ArrayList<>();
        for (String idText : furnitureIds.split(",", -1)) {
            String normalized = idText.trim();
            if (normalized.startsWith("'") && normalized.endsWith("'") && normalized.length() >= 2) {
                normalized = normalized.substring(1, normalized.length() - 1);
            }
            long id = NumberUtils.parseLong(normalized);
            if (id <= 0L) {
                return List.of();
            }
            ids.add(id);
        }
        return List.copyOf(ids);
    }
}
