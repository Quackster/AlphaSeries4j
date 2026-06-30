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

    public long maxCatalogPageId() throws SQLException {
        return database.queryOne(
            "SELECT MAX(id) FROM catalog_pages",
            resultSet -> resultSet.getLong(1))
            .orElse(0L);
    }

    public List<CatalogPageRow> catalogPageRows() throws SQLException {
        return database.query(
            "SELECT id,name,level_minrequired,hclevel_minrequired,is_clickable,ctlg_template,"
                + "ctlg_header_img,ctlg_special_img,ctlg_special_template,ctlg_txt1,ctlg_txt2,ctlg_txt3,ctlg_txt4,"
                + "ctlg_txt5,ctlg_txt6,ctlg_txt7,ctlg_txt8,ctlg_txt9,ctlg_txt10,ctlg_txt11,ctlg_link,is_develop "
                + "FROM catalog_pages ORDER BY id_order ASC",
            resultSet -> new CatalogPageRow(
                resultSet.getLong(1),
                resultSet.getString(2),
                resultSet.getLong(3),
                resultSet.getLong(4),
                resultSet.getLong(5),
                resultSet.getString(6),
                resultSet.getString(7),
                resultSet.getString(8),
                resultSet.getString(9),
                resultSet.getString(10),
                resultSet.getString(11),
                resultSet.getString(12),
                resultSet.getString(13),
                resultSet.getString(14),
                resultSet.getString(15),
                resultSet.getString(16),
                resultSet.getString(17),
                resultSet.getString(18),
                resultSet.getString(19),
                resultSet.getString(20),
                resultSet.getString(21),
                resultSet.getLong(22)));
    }

    public List<CatalogPageProductRow> catalogPageProductRows(long pageId) throws SQLException {
        return database.query(
            "SELECT id,id_product,price_credits,price_activitypoints,sprite,type_activitypoints,"
                + "amount,type_secondary,replace_defaultsign,min_hc_level_required "
                + "FROM catalog_products WHERE ctlg_pageid=? ORDER BY id_order,sprite ASC",
            resultSet -> new CatalogPageProductRow(
                resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getLong(3),
                resultSet.getLong(4),
                resultSet.getString(5),
                resultSet.getLong(6),
                resultSet.getLong(7),
                resultSet.getString(8),
                resultSet.getLong(9),
                resultSet.getLong(10)),
            pageId);
    }

    public List<CatalogPageTreeRow> catalogPageTreeRows(long parentId, long rank, long hcLevel) throws SQLException {
        return database.query(
            "SELECT id,name,ctlg_color,ctlg_icon,is_develop,is_visible FROM catalog_pages "
                + "WHERE id_parent=? AND level_minrequired <= ? AND hclevel_minrequired <= ? ORDER BY id_order ASC",
            resultSet -> new CatalogPageTreeRow(
                resultSet.getLong(1),
                resultSet.getString(2),
                resultSet.getLong(3),
                resultSet.getLong(4),
                resultSet.getLong(5),
                resultSet.getLong(6)),
            parentId,
            rank,
            hcLevel);
    }

    public long catalogPageChildCount(long parentId, long rank, long hcLevel) throws SQLException {
        return database.queryOne(
            "SELECT COUNT(id) FROM catalog_pages WHERE id_parent=? AND level_minrequired <= ? AND hclevel_minrequired <= ?",
            resultSet -> resultSet.getLong(1),
            parentId,
            rank,
            hcLevel)
            .orElse(0L);
    }

    public record ProductCacheRow(List<String> values) {
    }

    public record CatalogProductCacheRow(List<String> values) {
    }

    public record ProductDealRow(long dealId, String items) {
    }

    public record CatalogPageRow(
        long pageId,
        String name,
        long minimumRank,
        long minimumHcRank,
        long clickable,
        String template,
        String headerImage,
        String specialImage,
        String specialTemplate,
        String textOne,
        String textTwo,
        String textThree,
        String textFour,
        String textFive,
        String textSix,
        String textSeven,
        String textEight,
        String textNine,
        String textTen,
        String textEleven,
        String link,
        long develop
    ) {
        public String[] legacyFields() {
            return new String[] {
                String.valueOf(pageId),
                text(name),
                String.valueOf(minimumRank),
                String.valueOf(minimumHcRank),
                String.valueOf(clickable),
                text(template),
                text(headerImage),
                text(specialImage),
                text(specialTemplate),
                text(textOne),
                text(textTwo),
                text(textThree),
                text(textFour),
                text(textFive),
                text(textSix),
                text(textSeven),
                text(textEight),
                text(textNine),
                text(textTen),
                text(textEleven),
                text(link),
                String.valueOf(develop)
            };
        }
    }

    public record CatalogPageProductRow(
        long catalogProductId,
        long productId,
        long creditPrice,
        long activityPointPrice,
        String sprite,
        long activityPointType,
        long amount,
        String secondaryType,
        long replaceDefaultSign,
        long minimumHcRank
    ) {
    }

    public record CatalogPageTreeRow(long pageId, String name, long color, long icon, long develop, long visible) {
        public String[] legacyFields() {
            return new String[] {
                String.valueOf(pageId),
                text(name),
                String.valueOf(color),
                String.valueOf(icon),
                String.valueOf(develop),
                String.valueOf(visible)
            };
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

    private static String text(String value) {
        return value == null ? "" : value;
    }
}
