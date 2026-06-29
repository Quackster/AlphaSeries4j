package com.alphaseries.dao.mysql;

import com.alphaseries.db.Database;

import java.sql.SQLException;
import java.util.List;

public final class MessengerDao {
    private final Database database;

    public MessengerDao(Database database) {
        this.database = database;
    }

    public List<Long> acceptedFriendSocketIndexes(long userId) throws SQLException {
        return database.query(
            "SELECT users.id_socket FROM friendships,users WHERE friendships.has_accept=? AND friendships.id_user=? "
                + "AND users.id=friendships.id_friend AND users.id_socket>?",
            resultSet -> resultSet.getLong(1),
            1L,
            userId,
            0L);
    }

    public int deletePendingRequests(long userId) throws SQLException {
        return database.execute(
            "DELETE FROM friendships WHERE id_user=? AND has_accept=? LIMIT 75",
            userId,
            0L);
    }

    public int deletePendingRequests(long userId, String targetIdList) throws SQLException {
        if (targetIdList == null || targetIdList.isEmpty() || !targetIdList.matches("\\d+(,\\d+)*")) {
            return 0;
        }
        return database.execute(
            "DELETE FROM friendships WHERE id_user=? AND has_accept=? AND id_friend IN (" + targetIdList + ") LIMIT 75",
            userId,
            0L);
    }
}
