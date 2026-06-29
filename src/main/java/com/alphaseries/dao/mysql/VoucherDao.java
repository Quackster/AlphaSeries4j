package com.alphaseries.dao.mysql;

import com.alphaseries.db.Database;

import java.sql.SQLException;
import java.util.Optional;

public final class VoucherDao {
    private final Database database;

    public VoucherDao(Database database) {
        this.database = database;
    }

    public Optional<VoucherReward> reward(String voucherName) throws SQLException {
        return database.queryOne(
            "SELECT contain_product,contain_credits,contain_shells FROM vouchers WHERE name=? LIMIT 1",
            resultSet -> new VoucherReward(
                resultSet.getString(1),
                resultSet.getLong(2),
                resultSet.getLong(3)),
            voucherName);
    }

    public long catalogProductProductIdBySprite(String sprite) throws SQLException {
        return database.queryOne(
            "SELECT id_product FROM catalog_products WHERE sprite=? LIMIT 1",
            resultSet -> resultSet.getLong(1),
            sprite)
            .orElse(0L);
    }

    public int deleteVoucher(String voucherName) throws SQLException {
        return database.execute("DELETE FROM vouchers WHERE name=? LIMIT 1", voucherName);
    }

    public record VoucherReward(String productSprite, long credits, long shells) {
    }
}
