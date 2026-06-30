package com.alphaseries.dao.mysql;

import com.alphaseries.db.Database;

import java.sql.SQLException;
import java.util.List;

public final class CatalogDao {
    private final Database database;

    public CatalogDao(Database database) {
        this.database = database;
    }

    public long destinationIdByProduct(long productId) throws SQLException {
        return database.queryOne(
            "SELECT id_destination FROM catalog_products WHERE id_product=? ORDER BY id DESC LIMIT 1",
            resultSet -> resultSet.getLong(1),
            productId)
            .orElse(0L);
    }

    public long idBySprite(String sprite) throws SQLException {
        return database.queryOne(
            "SELECT id FROM catalog_products WHERE sprite=? LIMIT 1",
            resultSet -> resultSet.getLong(1),
            sprite)
            .orElse(0L);
    }

    public long firstGiftWrapProductId() throws SQLException {
        return database.queryOne(
            "SELECT id FROM products WHERE sprite LIKE ? ORDER BY id ASC LIMIT 1",
            resultSet -> resultSet.getLong(1),
            "present_wrap%")
            .orElse(0L);
    }

    public long productIdBySprite(String sprite) throws SQLException {
        return database.queryOne(
            "SELECT id FROM products WHERE sprite=? LIMIT 1",
            resultSet -> resultSet.getLong(1),
            sprite)
            .orElse(0L);
    }

    public long firstProductIdByType(long productType) throws SQLException {
        return database.queryOne(
            "SELECT id FROM products WHERE id_type=? LIMIT 1",
            resultSet -> resultSet.getLong(1),
            productType)
            .orElse(0L);
    }

    public List<Long> counterProductIds() throws SQLException {
        return database.query(
            "SELECT id FROM products WHERE id_counter IS NOT NULL",
            resultSet -> resultSet.getLong(1));
    }
}
