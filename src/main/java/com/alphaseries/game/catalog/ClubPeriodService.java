package com.alphaseries.game.catalog;

import com.alphaseries.config.AppConfigState;
import com.alphaseries.dao.mysql.ClubDao;
import com.alphaseries.db.Database;
import com.alphaseries.db.MySQL;
import com.alphaseries.util.NumberUtils;

import java.sql.SQLException;

public final class ClubPeriodService {
    private ClubPeriodService() {
    }

    /**
     * Original function: Proc_10_23_80E110.
     */
    public static long applyClubPeriod(long userId, long hcRank, long currentPeriods, long paidDays) {
        try {
            if (userId <= 0L) {
                return 0L;
            }
            long giftIncrementDefault = AppConfigState.instance().settingsCache().longValueOrDefault(
                "com.server.socket.game.club.gifts.hcrank" + hcRank + ".amount", 0);
            clubDao().applyClubPeriod(userId, hcRank, currentPeriods, paidDays, giftIncrementDefault);
            return 1L;
        } catch (Exception ex) {
            return 0L;
        }
    }

    private static ClubDao clubDao() throws SQLException {
        return new ClubDao(configuredDatabase());
    }

    private static Database configuredDatabase() throws SQLException {
        if (MySQL.configuredDatabase() == null) {
            throw new SQLException("Database is not configured.");
        }
        return MySQL.configuredDatabase();
    }
}
