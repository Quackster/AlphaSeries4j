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

    public long userIdByName(String userName) throws SQLException {
        return database.queryOne(
            "SELECT id FROM users WHERE name=? LIMIT 1",
            resultSet -> resultSet.getLong(1),
            userName)
            .orElse(0L);
    }

    public boolean friendshipExists(long userId, long targetUserId) throws SQLException {
        return database.queryOne(
            "SELECT id_user FROM friendships WHERE (id_user=? AND id_friend=?) OR (id_user=? AND id_friend=?) LIMIT 1",
            resultSet -> resultSet.getLong(1),
            userId,
            targetUserId,
            targetUserId,
            userId)
            .orElse(0L) > 0L;
    }

    public long acceptFriends(long userId) throws SQLException {
        return database.queryOne(
            "SELECT accept_friends FROM users WHERE id=? LIMIT 1",
            resultSet -> resultSet.getLong(1),
            userId)
            .orElse(0L);
    }

    public int insertFriendRequest(long targetUserId, long userId) throws SQLException {
        return database.execute(
            "INSERT IGNORE INTO friendships(id_user,id_friend) VALUES(?,?)",
            targetUserId,
            userId);
    }

    public int insertPrivateChatLog(long userId, long roomId, String description, long sessionId) throws SQLException {
        return database.execute(
            "INSERT INTO logs_chat(id_user,id_room,timestamp,description,id_type,id_session) "
                + "VALUES(?,?,UNIX_TIMESTAMP(),?,?,?)",
            userId,
            roomId,
            description,
            3L,
            sessionId);
    }

    public List<SearchUser> searchUsers(String searchText, String dateTimeFormat) throws SQLException {
        String normalizedSearch = searchText == null ? "" : searchText.trim().toLowerCase();
        if (normalizedSearch.isEmpty()) {
            return List.of();
        }
        if (normalizedSearch.length() > 3) {
            return database.query(
                "SELECT id,name,id_socket,figure,motto,nickname,DATE_FORMAT(FROM_UNIXTIME(lastonline_time), ?) "
                    + "FROM users WHERE LOWER(name) LIKE ? LIMIT 50",
                resultSet -> new SearchUser(
                    resultSet.getLong(1),
                    resultSet.getString(2),
                    resultSet.getLong(3),
                    resultSet.getString(4),
                    resultSet.getString(5),
                    resultSet.getString(6),
                    resultSet.getString(7)),
                dateTimeFormat,
                normalizedSearch + "%");
        }
        return database.query(
            "SELECT id,name,id_socket,figure,motto,nickname,DATE_FORMAT(FROM_UNIXTIME(lastonline_time), ?) "
                + "FROM users WHERE LOWER(name)=? LIMIT 50",
            resultSet -> new SearchUser(
                resultSet.getLong(1),
                resultSet.getString(2),
                resultSet.getLong(3),
                resultSet.getString(4),
                resultSet.getString(5),
                resultSet.getString(6),
                resultSet.getString(7)),
            dateTimeFormat,
            normalizedSearch);
    }

    public record SearchUser(
        long userId,
        String userName,
        long socketIndex,
        String figure,
        String motto,
        String nickname,
        String lastOnline
    ) {
    }
}
