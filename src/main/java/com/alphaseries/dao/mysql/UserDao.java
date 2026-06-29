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

    public long socketByUserId(long userId) throws SQLException {
        return database.queryOne(
            "SELECT id_socket FROM users WHERE id=? LIMIT 1",
            resultSet -> resultSet.getLong(1),
            userId)
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

    public int addCredits(long userId, long credits) throws SQLException {
        return database.execute("UPDATE users SET credits=credits+" + credits + " WHERE id=?", userId);
    }

    public long respectAmount(long userId) throws SQLException {
        return database.queryOne(
            "SELECT respect_amount FROM users WHERE id=? LIMIT 1",
            resultSet -> resultSet.getLong(1),
            userId)
            .orElse(0L);
    }

    public long respectReceived(long userId) throws SQLException {
        return database.queryOne(
            "SELECT respect_received FROM users WHERE id=? LIMIT 1",
            resultSet -> resultSet.getLong(1),
            userId)
            .orElse(0L);
    }

    public int spendRespect(long userId) throws SQLException {
        return database.execute(
            "UPDATE users SET respect_amount=respect_amount-1,respect_given=respect_given+1 WHERE id=?",
            userId);
    }

    public int receiveRespect(long userId) throws SQLException {
        return database.execute(
            "UPDATE users SET respect_received=respect_received+1 WHERE id=?",
            userId);
    }

    public long scratchAmount(long userId) throws SQLException {
        return database.queryOne(
            "SELECT scratch_amount FROM users WHERE id=? LIMIT 1",
            resultSet -> resultSet.getLong(1),
            userId)
            .orElse(0L);
    }

    public int spendScratch(long userId) throws SQLException {
        return database.execute(
            "UPDATE users SET scratch_amount=scratch_amount-1,scratch_given=scratch_given+1 WHERE id=?",
            userId);
    }

    public long activityPoints(long userId, long pointType) throws SQLException {
        return database.queryOne(
            "SELECT activitypoints_" + pointType + " FROM users WHERE id=? LIMIT 1",
            resultSet -> resultSet.getLong(1),
            userId)
            .orElse(0L);
    }

    public Optional<ActivityPointBalance> activityPointBalance(long userId) throws SQLException {
        return database.queryOne(
            "SELECT activitypoints_1,activitypoints_2,activitypoints_3,activitypoints_4 FROM users WHERE id=? LIMIT 1",
            resultSet -> new ActivityPointBalance(
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getLong(3),
                resultSet.getLong(4)),
            userId);
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

    public String wardrobeRows(long userId) throws SQLException {
        return String.join("\r", database.query(
            "SELECT id_slot,figure,gender FROM users_wardrobe WHERE id_user=? ORDER BY id_slot",
            resultSet -> resultSet.getString(1) + "\t" + resultSet.getString(2) + "\t" + resultSet.getString(3),
            userId));
    }

    public int deleteWardrobeSlot(long userId, long slotId) throws SQLException {
        return database.execute("DELETE FROM users_wardrobe WHERE id_user=? AND id_slot=? LIMIT 1", userId, slotId);
    }

    public int insertWardrobeSlot(long userId, long slotId, String figure, String gender) throws SQLException {
        return database.execute(
            "INSERT INTO users_wardrobe(id_user,id_slot,figure,gender) VALUES(?,?,?,?)",
            userId,
            slotId,
            figure,
            gender);
    }

    public int updateTutorialClothes(long userId, String gender, String figure) throws SQLException {
        return database.execute(
            "UPDATE users SET tutorial_clothes=?,gender=?,figure=? WHERE id=?",
            1L,
            gender,
            figure,
            userId);
    }

    public String motto(long userId) throws SQLException {
        return database.queryOne(
            "SELECT motto FROM users WHERE id=? LIMIT 1",
            resultSet -> resultSet.getString(1),
            userId)
            .orElse("");
    }

    public String name(long userId) throws SQLException {
        return database.queryOne(
            "SELECT name FROM users WHERE id=? LIMIT 1",
            resultSet -> resultSet.getString(1),
            userId)
            .orElse("");
    }

    public long userIdByName(String name) throws SQLException {
        return database.queryOne(
            "SELECT id FROM users WHERE name=? LIMIT 1",
            resultSet -> resultSet.getLong(1),
            name)
            .orElse(0L);
    }

    public String sessionId(long userId) throws SQLException {
        return database.queryOne(
            "SELECT id_session FROM users WHERE id=? LIMIT 1",
            resultSet -> resultSet.getString(1),
            userId)
            .orElse("");
    }

    public String loginSession(long userId) throws SQLException {
        return database.queryOne(
            "SELECT login_session FROM users WHERE id=? LIMIT 1",
            resultSet -> resultSet.getString(1),
            userId)
            .orElse("");
    }

    public long socketByName(String name) throws SQLException {
        return database.queryOne(
            "SELECT id_socket FROM users WHERE name=? AND id_socket IS NOT NULL LIMIT 1",
            resultSet -> resultSet.getLong(1),
            name)
            .orElse(0L);
    }

    public Optional<ActiveUserLocation> activeLocationByName(String name) throws SQLException {
        return database.queryOne(
            "SELECT users.id,users.id_socket,logs_visitedrooms.id_room "
                + "FROM users,logs_visitedrooms WHERE users.name=? "
                + "AND users.id=logs_visitedrooms.id_user AND logs_visitedrooms.timestamp_left IS NULL LIMIT 1",
            resultSet -> new ActiveUserLocation(
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getLong(3)),
            name);
    }

    public String gender(long userId) throws SQLException {
        return database.queryOne(
            "SELECT gender FROM users WHERE id=? LIMIT 1",
            resultSet -> resultSet.getString(1),
            userId)
            .orElse("");
    }

    public long countByName(String name) throws SQLException {
        return database.queryOne(
            "SELECT COUNT(*) FROM users WHERE name=?",
            resultSet -> resultSet.getLong(1),
            name)
            .orElse(0L);
    }

    public int updateName(long userId, String name) throws SQLException {
        return database.execute(
            "UPDATE users SET name=?,tutorial_name=?,merge_name=? WHERE id=?",
            name,
            1L,
            0L,
            userId);
    }

    public int insertIdentityLog(String previousIdentity, String newIdentity, long sessionId) throws SQLException {
        return database.execute(
            "INSERT INTO logs_identity(previous_identity,new_identity,timestamp,id_session) VALUES(?,?,UNIX_TIMESTAMP(),?)",
            previousIdentity,
            newIdentity,
            sessionId);
    }

    public int updateHomeRoom(long userId, long roomId) throws SQLException {
        return database.execute("UPDATE users SET homeroom=? WHERE id=?", roomId, userId);
    }

    public record UserIdentity(long userId, long socketIndex, String motto, String figure, String gender) {
    }

    public record ActiveUserLocation(long userId, long socketIndex, long roomId) {
    }

    public record ActivityPointBalance(long pointTypeOne, long pointTypeTwo, long pointTypeThree, long pointTypeFour) {
        public long valueFor(long pointType) {
            if (pointType == 1L) {
                return pointTypeOne;
            }
            if (pointType == 2L) {
                return pointTypeTwo;
            }
            if (pointType == 3L) {
                return pointTypeThree;
            }
            if (pointType == 4L) {
                return pointTypeFour;
            }
            return 0L;
        }
    }
}
