package com.alphaseries.dao.mysql;

import com.alphaseries.db.Database;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public final class QuestDao {
    private final Database database;

    public QuestDao(Database database) {
        this.database = database;
    }

    public int clearAcceptedQuest(long userId) throws SQLException {
        return database.execute(
            "UPDATE users_quests SET timestamp_accepted=NULL WHERE id_user=? "
                + "AND timestamp_accepted IS NOT NULL AND timestamp_done IS NULL LIMIT 1",
            userId);
    }

    public long existingLevel(long userId, long questId) throws SQLException {
        return database.queryOne(
            "SELECT id_level FROM users_quests WHERE id_user=? AND id_quest=? LIMIT 1",
            resultSet -> resultSet.getLong(1),
            userId,
            questId)
            .orElse(Long.MIN_VALUE);
    }

    public int reactivateQuest(long userId, long questId, long numericQuestId) throws SQLException {
        return database.execute(
            "UPDATE users_quests SET timestamp_done=NULL,timestamp_accepted=UNIX_TIMESTAMP(),"
                + "id_numericquest=?,time_next=NULL WHERE id_user=? AND id_quest=? LIMIT 1",
            numericQuestId,
            userId,
            questId);
    }

    public int insertQuest(long userId, long questId, long numericQuestId) throws SQLException {
        return database.execute(
            "INSERT INTO users_quests(id_user,id_quest,id_level,id_numericquest,timestamp_accepted) "
                + "VALUES(?,?,?,?,UNIX_TIMESTAMP())",
            userId,
            questId,
            0L,
            numericQuestId);
    }

    public long progress(long userId, long questId) throws SQLException {
        return database.queryOne(
            "SELECT progress FROM users_quests WHERE id_user=? AND id_quest=? LIMIT 1",
            resultSet -> resultSet.getLong(1),
            userId,
            questId)
            .orElse(0L);
    }

    public String timeNext(long userId, long questId) throws SQLException {
        return database.queryOne(
            "SELECT time_next FROM users_quests WHERE id_user=? AND id_quest=? LIMIT 1",
            resultSet -> resultSet.getString(1),
            userId,
            questId)
            .orElse("");
    }

    public int scheduleNextTime(long userId, long questId, long waitSeconds) throws SQLException {
        return database.execute(
            "UPDATE users_quests SET time_next=DATE_ADD(NOW(),INTERVAL " + nonNegativeSeconds(waitSeconds)
                + " SECOND) WHERE id_user=? AND id_quest=? LIMIT 1",
            userId,
            questId);
    }

    public Optional<UserQuestLevelRow> activeLevelRow(long userId) throws SQLException {
        return database.queryOne(
            "SELECT id_quest,id_level FROM users_quests WHERE id_user=? "
                + "AND timestamp_accepted IS NOT NULL AND timestamp_done IS NULL LIMIT 1",
            resultSet -> new UserQuestLevelRow(resultSet.getLong(1), resultSet.getLong(2)),
            userId);
    }

    public Optional<UserQuestLevelRow> latestLevelRow(long userId) throws SQLException {
        return database.queryOne(
            "SELECT id_quest,id_level FROM users_quests WHERE id_user=? "
                + "ORDER BY timestamp_done DESC,timestamp_accepted DESC,id_level DESC LIMIT 1",
            resultSet -> new UserQuestLevelRow(resultSet.getLong(1), resultSet.getLong(2)),
            userId);
    }

    public int resetUserQuests(long userId) throws SQLException {
        return database.execute(
            "UPDATE users_quests SET timestamp_done=NULL,timestamp_accepted=NULL WHERE id_user=? LIMIT 50",
            userId);
    }

    public Optional<UserQuestProgressRow> activeProgressRow(long userId) throws SQLException {
        return database.queryOne(
            "SELECT id_quest,id_numericquest,progress,id_level,time_next FROM users_quests WHERE id_user=? "
                + "AND timestamp_accepted IS NOT NULL AND timestamp_done IS NULL LIMIT 1",
            resultSet -> new UserQuestProgressRow(
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getLong(3),
                resultSet.getLong(4),
                resultSet.getString(5)),
            userId);
    }

    public Optional<UserQuestCompletionRow> completionRow(long userId, long questId) throws SQLException {
        if (questId <= 0L) {
            return database.queryOne(
                "SELECT id_quest,id_numericquest,progress,id_level FROM users_quests WHERE id_user=? "
                    + "AND timestamp_accepted IS NOT NULL AND timestamp_done IS NULL LIMIT 1",
                resultSet -> new UserQuestCompletionRow(
                    resultSet.getLong(1),
                    resultSet.getLong(2),
                    resultSet.getLong(3),
                    resultSet.getLong(4)),
                userId);
        }
        return database.queryOne(
            "SELECT id_quest,id_numericquest,progress,id_level FROM users_quests WHERE id_user=? AND id_quest=? LIMIT 1",
            resultSet -> new UserQuestCompletionRow(
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getLong(3),
                resultSet.getLong(4)),
            userId,
            questId);
    }

    public int completeQuest(long userId, long questId) throws SQLException {
        return database.execute(
            "UPDATE users_quests SET id_level=id_level+1,progress='0',id_numericquest='0',timestamp_done=UNIX_TIMESTAMP() "
                + "WHERE id_user=? AND id_quest=? LIMIT 1",
            userId,
            questId);
    }

    public List<UserQuestListRow> listRows(long userId) throws SQLException {
        return database.query(
            "SELECT id_quest,id_level,timestamp_done,timestamp_accepted,time_next,progress "
                + "FROM users_quests WHERE id_user=? LIMIT 250",
            resultSet -> new UserQuestListRow(
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getString(3),
                resultSet.getString(4),
                resultSet.getString(5),
                resultSet.getLong(6)),
            userId);
    }

    public long remainingWait(String timeNextText) throws SQLException {
        return database.queryOne(
            "SELECT GREATEST(0,UNIX_TIMESTAMP(?)-UNIX_TIMESTAMP())",
            resultSet -> resultSet.getLong(1),
            timeNextText)
            .orElse(0L);
    }

    public List<QuestDefinition> questDefinitions() throws SQLException {
        return database.query(
            "SELECT id,level,name,NULL,reward,reward_type,require_action,id_additional,id_campaign,amount_activities,waitamount "
                + "FROM quests ORDER BY id_campaign DESC,level ASC",
            resultSet -> new QuestDefinition(
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getString(3),
                resultSet.getString(4),
                resultSet.getLong(5),
                resultSet.getLong(6),
                resultSet.getString(7),
                resultSet.getLong(8),
                resultSet.getLong(9),
                resultSet.getLong(10),
                resultSet.getLong(11)));
    }

    private static long nonNegativeSeconds(long seconds) {
        return Math.max(0L, seconds);
    }

    public record UserQuestLevelRow(long questId, long level) {
    }

    public record UserQuestProgressRow(long questId, long numericQuestId, long progress, long level, String timeNext) {
    }

    public record UserQuestCompletionRow(long questId, long numericQuestId, long progress, long level) {
    }

    public record UserQuestListRow(
        long questId,
        long level,
        String timestampDone,
        String timestampAccepted,
        String timeNext,
        long progress
    ) {
    }

    public record QuestDefinition(
        long questId,
        long level,
        String name,
        String reservedSlot,
        long reward,
        long rewardType,
        String requiredAction,
        long additionalId,
        long campaignId,
        long activityAmount,
        long waitAmount
    ) {
    }
}
