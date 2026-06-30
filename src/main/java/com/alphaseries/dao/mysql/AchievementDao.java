package com.alphaseries.dao.mysql;

import com.alphaseries.db.Database;

import java.sql.SQLException;
import java.util.List;

public final class AchievementDao {
    private final Database database;

    public AchievementDao(Database database) {
        this.database = database;
    }

    public List<AchievementSettingsRow> enabledSettingsRows() throws SQLException {
        return database.query(
            "SELECT id_quest,id_badge,progress,reward_increase,level_total,score_increase,type_reward "
                + "FROM settings_achievements WHERE is_enabled=? LIMIT 100",
            resultSet -> new AchievementSettingsRow(
                resultSet.getLong(1),
                resultSet.getString(2),
                resultSet.getLong(3),
                resultSet.getLong(4),
                resultSet.getLong(5),
                resultSet.getLong(6),
                resultSet.getLong(7)),
            1L);
    }

    public record AchievementSettingsRow(
        long questId,
        String badgeId,
        long progress,
        long rewardIncrease,
        long levelTotal,
        long scoreIncrease,
        long rewardType
    ) {
    }
}
