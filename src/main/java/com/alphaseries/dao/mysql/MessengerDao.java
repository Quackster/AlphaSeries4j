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

    public boolean acceptedFriendshipExists(long userId, long targetUserId) throws SQLException {
        return database.queryOne(
            "SELECT id_user FROM friendships WHERE has_accept=? AND ((id_friend=? AND id_user=?) "
                + "OR (id_user=? AND id_friend=?)) LIMIT 1",
            resultSet -> resultSet.getLong(1),
            1L,
            targetUserId,
            userId,
            targetUserId,
            userId)
            .orElse(0L) > 0L;
    }

    public int deleteAcceptedFriendships(long userId, String targetIdList) throws SQLException {
        if (targetIdList == null || targetIdList.isEmpty() || !targetIdList.matches("\\d+(,\\d+)*")) {
            return 0;
        }
        return database.execute(
            "DELETE FROM friendships WHERE has_accept=? AND ((id_user=? AND id_friend IN (" + targetIdList
                + ")) OR (id_friend=? AND id_user IN (" + targetIdList + "))) LIMIT 150",
            1L,
            userId,
            userId);
    }

    public String pendingRequestRows(long userId) throws SQLException {
        List<String> rows = database.query(
            "SELECT users.id,users.name FROM users,friendships WHERE friendships.has_accept=? AND friendships.id_user=? "
                + "AND users.id=friendships.id_friend LIMIT 50",
            resultSet -> resultSet.getString(1) + "\t" + resultSet.getString(2),
            0L,
            userId);
        return String.join("\r", rows);
    }

    public String acceptedFriendRows(long userId, String dateTimeFormat, long limit) throws SQLException {
        long queryLimit = limit > 0L ? limit : 200L;
        List<String> rows = database.query(
            "SELECT users.id,users.name,users.id_socket,users.figure,users.motto,users.level,"
                + "DATE_FORMAT(FROM_UNIXTIME(users.lastonline_time), ?) FROM friendships,users "
                + "WHERE friendships.has_accept=? AND friendships.id_user=? AND users.id=friendships.id_friend "
                + "LIMIT " + queryLimit,
            resultSet -> resultSet.getString(1) + "\t" + resultSet.getString(2) + "\t" + resultSet.getString(3)
                + "\t" + resultSet.getString(4) + "\t" + resultSet.getString(5) + "\t" + resultSet.getString(6)
                + "\t" + resultSet.getString(7),
            dateTimeFormat,
            1L,
            userId);
        return String.join("\r", rows);
    }
}
