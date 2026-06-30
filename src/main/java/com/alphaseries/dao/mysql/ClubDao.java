package com.alphaseries.dao.mysql;

import com.alphaseries.db.Database;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public final class ClubDao {
    private final Database database;

    public ClubDao(Database database) {
        this.database = database;
    }

    public String clubProductRows() throws SQLException {
        List<String> rows = database.query(
            "SELECT id,sprite_name,months,level,price_credits FROM products_club ORDER BY id ASC",
            resultSet -> resultSet.getString(1) + "\t" + resultSet.getString(2) + "\t" + resultSet.getString(3)
                + "\t" + resultSet.getString(4) + "\t" + resultSet.getString(5));
        return String.join("\r", rows);
    }

    public String userClubStatusRow(long userId) throws SQLException {
        return database.queryOne(
            "SELECT level_hc,hc_days,hc2_days,hc_periods,hc2_periods,hc_presents,"
                + "ROUND((UNIX_TIMESTAMP()-hc_startperiod)/60/60/24,0) FROM users WHERE id=? LIMIT 1",
            resultSet -> resultSet.getString(1) + "\t" + resultSet.getString(2) + "\t" + resultSet.getString(3)
                + "\t" + resultSet.getString(4) + "\t" + resultSet.getString(5) + "\t" + resultSet.getString(6)
                + "\t" + resultSet.getString(7),
            userId)
            .orElse("");
    }

    public Optional<ClubGiftStatus> clubGiftStatus(long userId) throws SQLException {
        return database.queryOne(
            "SELECT level_hc,hc_days,hc2_days,hc_presents,"
                + "ROUND((UNIX_TIMESTAMP()-hc_startperiod)/60/60/24,0) FROM users WHERE id=? LIMIT 1",
            resultSet -> new ClubGiftStatus(
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getLong(3),
                resultSet.getLong(4),
                resultSet.getLong(5)),
            userId);
    }

    public Optional<ContainedClubProduct> containedClubProduct(long productId) throws SQLException {
        return database.queryOne(
            "SELECT months,level FROM products_containshc WHERE id_product=? LIMIT 1",
            resultSet -> new ContainedClubProduct(resultSet.getLong(1), resultSet.getLong(2)),
            productId);
    }

    public List<ContainedClubProductRow> containedClubProductRows() throws SQLException {
        return database.query(
            "SELECT id_product,months,level FROM products_containshc",
            resultSet -> new ContainedClubProductRow(
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getLong(3)));
    }

    public int applyClubPeriod(long userId, long hcRank, long currentPeriods, long paidDays, long giftIncrementDefault)
        throws SQLException {

        long periodIncrement = 1L;
        long giftIncrement = giftIncrementDefault;
        if (paidDays > 0L) {
            periodIncrement = Math.round((paidDays + currentPeriods) / 31.0d);
            giftIncrement = 0L;
        }
        if (periodIncrement < 1L) {
            periodIncrement = 1L;
            giftIncrement = giftIncrementDefault;
        }
        String periodColumn = hcRank > 1L ? "hc2" : "hc";
        return database.execute(
            "UPDATE users SET hc_startperiod=UNIX_TIMESTAMP(),"
                + periodColumn + "_periods=" + periodColumn + "_periods+" + periodIncrement
                + ",hc_presents=hc_presents+" + giftIncrement
                + " WHERE id=?",
            userId);
    }

    public int decrementPresents(long userId) throws SQLException {
        return database.execute("UPDATE users SET hc_presents=hc_presents-1 WHERE id=?", userId);
    }

    public record ContainedClubProduct(long months, long level) {
    }

    public record ContainedClubProductRow(long productId, long months, long level) {
        public String legacyRow() {
            return productId + "\t" + months + "\t" + level;
        }
    }

    public record ClubGiftStatus(long hcLevel, long hcDays, long vipDays, long presentsAvailable, long daysSinceStart) {
        public long activeDays() {
            long activeDays = (hcLevel > 1L ? vipDays : hcDays) - daysSinceStart;
            return Math.max(0L, activeDays);
        }
    }
}
