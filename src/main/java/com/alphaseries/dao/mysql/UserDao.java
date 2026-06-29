package com.alphaseries.dao.mysql;

import com.alphaseries.db.Database;

import java.sql.SQLException;
import java.util.Optional;

public final class UserDao {
    private final Database database;

    public UserDao(Database database) {
        this.database = database;
    }

    public int markEmailValidated(long userId) throws SQLException {
        return database.execute("UPDATE users SET email_validated=? WHERE id=? LIMIT 1", 1L, userId);
    }

    public long emailValidated(long userId) throws SQLException {
        return database.queryOne(
            "SELECT email_validated FROM users WHERE id=? LIMIT 1",
            resultSet -> resultSet.getLong(1),
            userId)
            .orElse(0L);
    }

    public long userIdBySocket(long socketIndex) throws SQLException {
        return database.queryOne(
            "SELECT id FROM users WHERE id_socket=? LIMIT 1",
            resultSet -> resultSet.getLong(1),
            socketIndex)
            .orElse(0L);
    }

    public long rankLevel(long userId) throws SQLException {
        return database.queryOne(
            "SELECT level FROM users WHERE id=? LIMIT 1",
            resultSet -> resultSet.getLong(1),
            userId)
            .orElse(0L);
    }

    public long hcLevel(long userId) throws SQLException {
        return database.queryOne(
            "SELECT level_hc FROM users WHERE id=? LIMIT 1",
            resultSet -> resultSet.getLong(1),
            userId)
            .orElse(0L);
    }

    public long credits(long userId) throws SQLException {
        return database.queryOne(
            "SELECT credits FROM users WHERE id=? LIMIT 1",
            resultSet -> resultSet.getLong(1),
            userId)
            .orElse(0L);
    }

    public long activityPoints(long userId, long pointType) throws SQLException {
        return database.queryOne(
            "SELECT activitypoints_" + pointType + " FROM users WHERE id=? LIMIT 1",
            resultSet -> resultSet.getLong(1),
            userId)
            .orElse(0L);
    }

    public Optional<UserIdentity> findIdentity(long userId) throws SQLException {
        return database.queryOne(
            "SELECT id,id_socket,motto,figure,gender FROM users WHERE id=? LIMIT 1",
            resultSet -> new UserIdentity(
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getString(3),
                resultSet.getString(4),
                resultSet.getString(5)),
            userId);
    }

    public record UserIdentity(long userId, long socketIndex, String motto, String figure, String gender) {
    }
}
