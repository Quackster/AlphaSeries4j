package com.alphaseries.dao.mysql;

import com.alphaseries.db.Database;
import com.alphaseries.game.social.BadgeRow;
import com.alphaseries.game.user.ExpiredUserEffectRow;
import com.alphaseries.game.user.UserEffectActivationRow;
import com.alphaseries.game.user.UserEffectSummaryRow;
import com.alphaseries.game.user.UserGroupRow;

import java.sql.SQLException;
import java.util.List;
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

    public int assignLoginSocket(long userId, long socketIndex) throws SQLException {
        return database.execute("UPDATE users SET login_ticket=null,id_socket = ? WHERE id = ?", socketIndex, userId);
    }

    public int resetDailyInteractionCounters(long userId) throws SQLException {
        return database.execute(
            "UPDATE users SET respect_amount=?,scratch_amount=?,update_time=UNIX_TIMESTAMP() WHERE id=? LIMIT 1",
            5L,
            5L,
            userId);
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

    public long tutorialGuide(long userId) throws SQLException {
        return database.queryOne(
            "SELECT tutorial_guide FROM users WHERE id=? LIMIT 1",
            resultSet -> resultSet.getLong(1),
            userId)
            .orElse(0L);
    }

    public int markTutorialGuide(long userId) throws SQLException {
        return database.execute("UPDATE users SET tutorial_guide=? WHERE id=?", 1L, userId);
    }

    public List<BadgeRow> unequippedBadges(long userId) throws SQLException {
        return database.query(
            "SELECT id_badge,id_slot,id FROM users_badges WHERE id_user=? AND id_slot=? LIMIT 1000",
            resultSet -> new BadgeRow(
                resultSet.getString(1),
                resultSet.getLong(2),
                resultSet.getLong(3)),
            userId,
            0L);
    }

    public List<BadgeRow> equippedBadges(long userId) throws SQLException {
        return database.query(
            "SELECT id_badge,id_slot,id FROM users_badges WHERE id_slot != ? AND id_user=? LIMIT 5",
            resultSet -> new BadgeRow(
                resultSet.getString(1),
                resultSet.getLong(2),
                resultSet.getLong(3)),
            0L,
            userId);
    }

    public List<String> tagNames(long userId) throws SQLException {
        return database.query(
            "SELECT name FROM users_tags WHERE id_user=? LIMIT 30",
            resultSet -> resultSet.getString(1),
            userId);
    }

    public int clearEquippedBadges(long userId) throws SQLException {
        return database.execute("UPDATE users_badges SET id_slot=? WHERE id_user=?", 0L, userId);
    }

    public int equipBadge(long userId, String badgeId, long slot) throws SQLException {
        return database.execute(
            "UPDATE users_badges SET id_slot=? WHERE id_badge=? AND id_user=?",
            slot,
            badgeId,
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

    public int updateMotto(long userId, String motto) throws SQLException {
        return database.execute("UPDATE users SET motto=? WHERE id=?", motto, userId);
    }

    public int updateSoundSetting(long userId, long soundSetting) throws SQLException {
        return database.execute("UPDATE users SET settings_sound=? WHERE id=? LIMIT 1", soundSetting, userId);
    }

    public int clearSocket(long userId) throws SQLException {
        return database.execute("UPDATE users SET id_socket=null WHERE id = ?", userId);
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

    public int incrementStaffPickedCount(long userId) throws SQLException {
        return database.execute("UPDATE users SET amount_staffpicked=amount_staffpicked+1 WHERE id=?", userId);
    }

    public Optional<UserGroupRow> userGroup(long groupId) throws SQLException {
        return database.queryOne(
            "SELECT group_name,group_description,id_badge,id_room FROM users_groups WHERE id=? LIMIT 1",
            resultSet -> new UserGroupRow(
                resultSet.getString(1),
                resultSet.getString(2),
                resultSet.getString(3),
                resultSet.getLong(4)),
            groupId);
    }

    public List<UserEffectSummaryRow> userEffectSummaries(long userId) throws SQLException {
        return database.query(
            "SELECT id_effect,time_rent,COUNT(id_effect),timestamp_expire,UNIX_TIMESTAMP() "
                + "FROM users_effects WHERE id_user=? GROUP BY users_effects.id_effect LIMIT 50",
            resultSet -> new UserEffectSummaryRow(
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getLong(3),
                resultSet.getLong(4),
                resultSet.getLong(5)),
            userId);
    }

    public Optional<UserEffectActivationRow> userEffectActivation(long userId, long effectId) throws SQLException {
        return database.queryOne(
            "SELECT id,time_rent,timestamp_expire FROM users_effects WHERE id_user=? AND id_effect=? "
                + "ORDER BY timestamp_expire DESC LIMIT 1",
            resultSet -> new UserEffectActivationRow(
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getLong(3)),
            userId,
            effectId);
    }

    public int activateUserEffect(long rowId) throws SQLException {
        return database.execute(
            "UPDATE users_effects SET timestamp_expire=UNIX_TIMESTAMP()+time_rent WHERE id=? LIMIT 1",
            rowId);
    }

    public List<ExpiredUserEffectRow> expiredUserEffects() throws SQLException {
        return database.query(
            "SELECT users_effects.id_effect,users.id_socket,users_effects.id "
                + "FROM users_effects,users WHERE users_effects.timestamp_expire IS NOT NULL "
                + "AND users_effects.timestamp_expire<UNIX_TIMESTAMP() AND users.id=users_effects.id_user "
                + "AND users.id_socket IS NOT NULL LIMIT 500",
            resultSet -> new ExpiredUserEffectRow(
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getLong(3)));
    }

    public void deleteExpiredUserEffects() throws SQLException {
        database.execute(
            "DELETE FROM users_effects WHERE users_effects.timestamp_expire IS NOT NULL "
                + "AND users_effects.timestamp_expire<UNIX_TIMESTAMP() LIMIT 500");
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
