package com.alphaseries.dao.mysql;

import com.alphaseries.db.Database;

import java.sql.SQLException;
import java.util.Optional;

public final class PackageDao {
    private final Database database;

    public PackageDao(Database database) {
        this.database = database;
    }

    public Optional<PackageRow> packageByProduct(long productId) throws SQLException {
        return database.queryOne(
            "SELECT id_product,type_secondary,id_contain,type_check FROM packages WHERE id_product=? LIMIT 1",
            resultSet -> new PackageRow(
                resultSet.getLong(1),
                resultSet.getString(2),
                resultSet.getLong(3),
                resultSet.getString(4)),
            productId);
    }

    public Optional<PetPackage> petPackage(long packageId) throws SQLException {
        return database.queryOne(
            "SELECT id_pet,id_race,color FROM packages_pets WHERE id=? LIMIT 1",
            resultSet -> new PetPackage(
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getString(3)),
            packageId);
    }

    public record PackageRow(long productId, String secondaryType, long containedId, String checkType) {
    }

    public record PetPackage(long petType, long race, String color) {
    }
}
