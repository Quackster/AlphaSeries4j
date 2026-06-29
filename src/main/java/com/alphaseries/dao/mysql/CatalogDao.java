package com.alphaseries.dao.mysql;

import com.alphaseries.db.Database;

import java.sql.SQLException;

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
}
