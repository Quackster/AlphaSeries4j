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
}
