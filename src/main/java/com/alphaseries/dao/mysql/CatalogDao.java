package com.alphaseries.dao.mysql;

import com.alphaseries.db.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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

    public List<Long> giftWrapProductIds() throws SQLException {
        return database.query(
            "SELECT id FROM products WHERE sprite LIKE ?",
            resultSet -> resultSet.getLong(1),
            "present_wrap*%");
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

    public long maxProductId() throws SQLException {
        return database.queryOne(
            "SELECT MAX(id) FROM products",
            resultSet -> resultSet.getLong(1))
            .orElse(0L);
    }

    public List<ProductCacheRow> productCacheRows() throws SQLException {
        return database.query(
            "SELECT id,id_type,action,NULL,NULL,default_sign,status_max,handitems,distance_allowed,"
                + "is_tradeable,is_recycleable,is_signable,default_sign,min_roomrights,name,description,NULL,NULL,sprite,"
                + "is_iconstack,id_deco,time_rent,square_x,square_y,square_z,NULL,effect,receive_badge,wire,id_counter,"
                + "square_rotation,status_walkon,status_walkoff,NULL,has_charge,charge_price_credits,"
                + "charge_price_activitypoints,charge_price_activitypoints_type,charge_size,NULL,is_marketofferable,is_badgeshop "
                + "FROM products ORDER BY id ASC",
            resultSet -> new ProductCacheRow(rowValues(resultSet, 42)));
    }

    public long maxCatalogProductId() throws SQLException {
        return database.queryOne(
            "SELECT MAX(id) FROM catalog_products",
            resultSet -> resultSet.getLong(1))
            .orElse(0L);
    }

    public List<CatalogProductCacheRow> catalogProductCacheRows() throws SQLException {
        return database.query(
            "SELECT id,sprite,id_product,ctlg_pageid,type_secondary,amount,receive_badge,price_credits,"
                + "price_activitypoints,type_activitypoints,allow_gifts,min_hc_level_required,replace_defaultsign "
                + "FROM catalog_products ORDER BY id ASC",
            resultSet -> new CatalogProductCacheRow(rowValues(resultSet, 13)));
    }

    public List<ProductDealRow> productDealRows() throws SQLException {
        return database.query(
            "SELECT id,items FROM products_deals ORDER BY id ASC",
            resultSet -> new ProductDealRow(resultSet.getLong(1), resultSet.getString(2)));
    }

    public record ProductCacheRow(List<String> values) {
        public String legacyRow() {
            return String.join("\t", values);
        }
    }

    public record CatalogProductCacheRow(List<String> values) {
        public String legacyRow() {
            return String.join("\t", values);
        }
    }

    public record ProductDealRow(long dealId, String items) {
        public String legacyRow() {
            return dealId + "\t" + (items == null ? "" : items);
        }
    }

    private static List<String> rowValues(ResultSet resultSet, int columnCount) throws SQLException {
        List<String> values = new ArrayList<>();
        for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
            String value = resultSet.getString(columnIndex);
            values.add(value == null ? "" : value);
        }
        return values;
    }
}
