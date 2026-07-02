package com.alphaseries.dao.mysql;

import com.alphaseries.db.Database;
import com.alphaseries.game.messenger.MessengerFriend;
import com.alphaseries.game.messenger.PendingFriendRequest;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public final class MessengerDao {
    private static final long DEFAULT_ACCEPTED_FRIEND_LIMIT = 200L;

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

    public int deletePendingRequests(long userId, List<Long> targetIds) throws SQLException {
        if (targetIds == null || targetIds.isEmpty()) {
            return 0;
        }
        String placeholders = SqlFragments.placeholders(targetIds.size());
        return database.execute(
            "DELETE FROM friendships WHERE id_user=? AND has_accept=? AND id_friend IN (" + placeholders + ") LIMIT 75",
            SqlFragments.parametersWithIds(List.of(userId, 0L), targetIds));
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

    public int deleteAcceptedFriendships(long userId, List<Long> targetIds) throws SQLException {
        if (targetIds == null || targetIds.isEmpty()) {
            return 0;
        }
        String placeholders = SqlFragments.placeholders(targetIds.size());
        return database.execute(
            "DELETE FROM friendships WHERE has_accept=? AND ((id_user=? AND id_friend IN (" + placeholders
                + ")) OR (id_friend=? AND id_user IN (" + placeholders + "))) LIMIT 150",
            SqlFragments.parametersWithRepeatedIds(List.of(1L, userId), targetIds, List.of(userId)));
    }

    public List<PendingFriendRequest> pendingRequests(long userId) throws SQLException {
        return database.query(
            "SELECT users.id,users.name FROM users,friendships WHERE friendships.has_accept=? AND friendships.id_user=? "
                + "AND users.id=friendships.id_friend LIMIT 50",
            resultSet -> new PendingFriendRequest(
                resultSet.getLong(1),
                String.valueOf(resultSet.getString(2))),
            0L,
            userId);
    }

    public List<MessengerFriend> acceptedFriends(long userId, String dateTimeFormat, long limit) throws SQLException {
        long queryLimit = acceptedFriendLimit(limit);
        return database.query(
            "SELECT users.id,users.name,users.id_socket,users.figure,users.motto,users.level,"
                + "DATE_FORMAT(FROM_UNIXTIME(users.lastonline_time), ?) FROM friendships,users "
                + "WHERE friendships.has_accept=? AND friendships.id_user=? AND users.id=friendships.id_friend "
                + "LIMIT " + queryLimit,
            resultSet -> new MessengerFriend(
                resultSet.getLong(1),
                String.valueOf(resultSet.getString(2)),
                String.valueOf(resultSet.getString(5)),
                String.valueOf(resultSet.getString(4)),
                resultSet.getLong(6),
                resultSet.getLong(3),
                String.valueOf(resultSet.getString(7))),
            dateTimeFormat,
            1L,
            userId);
    }

    private static long acceptedFriendLimit(long limit) {
        return limit > 0L ? limit : DEFAULT_ACCEPTED_FRIEND_LIMIT;
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

    public boolean pendingRequestExists(long userId, long targetUserId, String dateTimeFormat) throws SQLException {
        return database.queryOne(
            "SELECT users.id,users.name,users.motto,users.figure,users.level,users.id_socket,"
                + "DATE_FORMAT(FROM_UNIXTIME(users.lastonline_time), ?) FROM users,friendships "
                + "WHERE friendships.has_accept=? AND friendships.id_user=? AND friendships.id_friend=? "
                + "AND users.id=friendships.id_friend LIMIT 1",
            resultSet -> resultSet.getLong(1),
            dateTimeFormat,
            0L,
            userId,
            targetUserId)
            .orElse(0L) > 0L;
    }

    public Optional<MessengerFriend> messengerFriend(long userId, String dateTimeFormat) throws SQLException {
        return database.queryOne(
            "SELECT id,name,motto,figure,level,id_socket,DATE_FORMAT(FROM_UNIXTIME(lastonline_time), ?) "
                + "FROM users WHERE id=? LIMIT 1",
            resultSet -> new MessengerFriend(
                resultSet.getLong(1),
                resultSet.getString(2),
                resultSet.getString(3),
                resultSet.getString(4),
                resultSet.getLong(5),
                resultSet.getLong(6),
                resultSet.getString(7)),
            dateTimeFormat,
            userId);
    }

    public int insertReversePendingFriendship(long targetUserId, long userId) throws SQLException {
        return database.execute(
            "INSERT IGNORE INTO friendships(id_user,id_friend,has_accept) VALUES(?,?,?)",
            targetUserId,
            userId,
            0L);
    }

    public int acceptFriendshipPair(long userId, long targetUserId) throws SQLException {
        return database.execute(
            "UPDATE friendships SET has_accept=? WHERE ((id_user=? AND id_friend=?) OR (id_user=? AND id_friend=?)) "
                + "AND has_accept=? LIMIT 2",
            1L,
            userId,
            targetUserId,
            targetUserId,
            userId,
            0L);
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

    public int insertInviteChatLog(long userId, long roomId, String description, long sessionId) throws SQLException {
        return database.execute(
            "INSERT INTO logs_chat(id_user,id_room,timestamp,description,id_type,id_session) "
                + "VALUES(?,?,UNIX_TIMESTAMP(),?,?,?)",
            userId,
            roomId,
            description,
            4L,
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
