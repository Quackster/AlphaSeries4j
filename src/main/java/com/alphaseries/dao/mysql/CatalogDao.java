package com.alphaseries.dao.mysql;

import com.alphaseries.db.Database;
import com.alphaseries.util.StringUtils;

import java.sql.ResultSet;
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
            CatalogDao::productCacheRow);
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
            CatalogDao::catalogProductCacheRow);
    }

    public List<ProductDealRow> productDealRows() throws SQLException {
        return database.query(
            "SELECT id,items FROM products_deals ORDER BY id ASC",
            resultSet -> productDealRow(resultSet.getLong(1), resultSet.getString(2)));
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

    public static final class ProductCacheRow {
        private final long productId;
        private final int fieldCount;
        private final long type;
        private final String defaultSign;
        private final String fallbackDefaultSign;
        private final String interactionAction;
        private final long stateCount;
        private final long maxState;
        private final String tradeName;
        private final String displayName;
        private final String description;
        private final String primarySprite;
        private final String alternateSprite;
        private final String defaultDecoration;
        private final long dimensionMapId;
        private final long squareZ;
        private final String itemData;
        private final String badgeId;
        private final String fallbackBadgeId;
        private final long wiredCode;
        private final boolean hasCharges;
        private final long chargeSize;
        private final long chargePriceCredits;
        private final long chargePriceActivityPoints;
        private final long chargePriceActivityPointsType;

        public ProductCacheRow(
            long productId,
            int fieldCount,
            long type,
            String defaultSign,
            String fallbackDefaultSign,
            String interactionAction,
            long stateCount,
            long maxState,
            String tradeName,
            String displayName,
            String description,
            String primarySprite,
            String alternateSprite,
            String defaultDecoration,
            long dimensionMapId,
            long squareZ,
            String itemData,
            String badgeId,
            String fallbackBadgeId,
            long wiredCode,
            boolean hasCharges,
            long chargeSize,
            long chargePriceCredits,
            long chargePriceActivityPoints,
            long chargePriceActivityPointsType
        ) {
            this.productId = productId;
            this.fieldCount = Math.max(0, fieldCount);
            this.type = type;
            this.defaultSign = StringUtils.text(defaultSign);
            this.fallbackDefaultSign = StringUtils.text(fallbackDefaultSign);
            this.interactionAction = StringUtils.text(interactionAction);
            this.stateCount = stateCount;
            this.maxState = maxState;
            this.tradeName = StringUtils.text(tradeName);
            this.displayName = StringUtils.text(displayName);
            this.description = StringUtils.text(description);
            this.primarySprite = StringUtils.text(primarySprite);
            this.alternateSprite = StringUtils.text(alternateSprite);
            this.defaultDecoration = StringUtils.text(defaultDecoration);
            this.dimensionMapId = dimensionMapId;
            this.squareZ = squareZ;
            this.itemData = StringUtils.text(itemData);
            this.badgeId = StringUtils.text(badgeId);
            this.fallbackBadgeId = StringUtils.text(fallbackBadgeId);
            this.wiredCode = wiredCode;
            this.hasCharges = hasCharges;
            this.chargeSize = chargeSize;
            this.chargePriceCredits = chargePriceCredits;
            this.chargePriceActivityPoints = chargePriceActivityPoints;
            this.chargePriceActivityPointsType = chargePriceActivityPointsType;
        }

        public long productId() {
            return productId;
        }

        public int fieldCount() {
            return fieldCount;
        }

        public long type() {
            return type;
        }

        public String defaultSign() {
            return defaultSign;
        }

        public String fallbackDefaultSign() {
            return fallbackDefaultSign;
        }

        public String interactionAction() {
            return interactionAction;
        }

        public long stateCount() {
            return stateCount;
        }

        public long maxState() {
            return maxState;
        }

        public String tradeName() {
            return tradeName;
        }

        public String displayName() {
            return displayName;
        }

        public String description() {
            return description;
        }

        public String primarySprite() {
            return primarySprite;
        }

        public String alternateSprite() {
            return alternateSprite;
        }

        public String defaultDecoration() {
            return defaultDecoration;
        }

        public long dimensionMapId() {
            return dimensionMapId;
        }

        public long squareZ() {
            return squareZ;
        }

        public String itemData() {
            return itemData;
        }

        public String badgeId() {
            return badgeId;
        }

        public String fallbackBadgeId() {
            return fallbackBadgeId;
        }

        public long wiredCode() {
            return wiredCode;
        }

        public boolean hasCharges() {
            return hasCharges;
        }

        public long chargeSize() {
            return chargeSize;
        }

        public long chargePriceCredits() {
            return chargePriceCredits;
        }

        public long chargePriceActivityPoints() {
            return chargePriceActivityPoints;
        }

        public long chargePriceActivityPointsType() {
            return chargePriceActivityPointsType;
        }

    }

    public static final class CatalogProductCacheRow {
        private final long rowId;
        private final int fieldCount;
        private final String sprite;
        private final long productId;
        private final long pageId;
        private final String typeSecondary;
        private final long amount;
        private final String receiveBadge;
        private final long creditPrice;
        private final long activityPointPrice;
        private final long activityPointType;
        private final long allowGifts;
        private final long minimumHcRank;
        private final long replaceDefaultSign;

        public CatalogProductCacheRow(
            long rowId,
            int fieldCount,
            String sprite,
            long productId,
            long pageId,
            String typeSecondary,
            long amount,
            String receiveBadge,
            long creditPrice,
            long activityPointPrice,
            long activityPointType,
            long allowGifts,
            long minimumHcRank,
            long replaceDefaultSign
        ) {
            this.rowId = rowId;
            this.fieldCount = Math.max(0, fieldCount);
            this.sprite = StringUtils.text(sprite);
            this.productId = productId;
            this.pageId = pageId;
            this.typeSecondary = StringUtils.text(typeSecondary);
            this.amount = amount;
            this.receiveBadge = StringUtils.text(receiveBadge);
            this.creditPrice = creditPrice;
            this.activityPointPrice = activityPointPrice;
            this.activityPointType = activityPointType;
            this.allowGifts = allowGifts;
            this.minimumHcRank = minimumHcRank;
            this.replaceDefaultSign = replaceDefaultSign;
        }

        public long rowId() {
            return rowId;
        }

        public int fieldCount() {
            return fieldCount;
        }

        public String sprite() {
            return sprite;
        }

        public long productId() {
            return productId;
        }

        public long pageId() {
            return pageId;
        }

        public String typeSecondary() {
            return typeSecondary;
        }

        public long amount() {
            return amount;
        }

        public String receiveBadge() {
            return receiveBadge;
        }

        public long creditPrice() {
            return creditPrice;
        }

        public long activityPointPrice() {
            return activityPointPrice;
        }

        public long activityPointType() {
            return activityPointType;
        }

        public long allowGifts() {
            return allowGifts;
        }

        public long minimumHcRank() {
            return minimumHcRank;
        }

        public long replaceDefaultSign() {
            return replaceDefaultSign;
        }

    }

    public record ProductDealRow(long dealId, List<Long> itemProductIds) {
        public ProductDealRow {
            itemProductIds = itemProductIds == null ? List.of() : List.copyOf(itemProductIds);
        }
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
    }

    private static ProductCacheRow productCacheRow(ResultSet resultSet) throws SQLException {
        return new ProductCacheRow(
            resultSet.getLong(1),
            42,
            resultSet.getLong(2),
            resultSet.getString(6),
            resultSet.getString(7),
            resultSet.getString(9),
            resultSet.getLong(12),
            resultSet.getLong(14),
            resultSet.getString(15),
            resultSet.getString(16),
            resultSet.getString(17),
            resultSet.getString(19),
            resultSet.getString(20),
            resultSet.getString(21),
            resultSet.getLong(22),
            resultSet.getLong(25),
            resultSet.getString(26),
            resultSet.getString(28),
            resultSet.getString(29),
            resultSet.getLong(29),
            resultSet.getLong(36) != 0L,
            resultSet.getLong(35),
            resultSet.getLong(36),
            resultSet.getLong(37),
            resultSet.getLong(38));
    }

    private static CatalogProductCacheRow catalogProductCacheRow(ResultSet resultSet) throws SQLException {
        return new CatalogProductCacheRow(
            resultSet.getLong(1),
            13,
            resultSet.getString(2),
            resultSet.getLong(3),
            resultSet.getLong(4),
            resultSet.getString(5),
            resultSet.getLong(6),
            resultSet.getString(7),
            resultSet.getLong(8),
            resultSet.getLong(9),
            resultSet.getLong(10),
            resultSet.getLong(11),
            resultSet.getLong(12),
            resultSet.getLong(13));
    }

    private static ProductDealRow productDealRow(long dealId, String items) {
        return new ProductDealRow(dealId, productIds(items));
    }

    private static List<Long> productIds(String items) {
        if (items == null || items.isEmpty()) {
            return List.of();
        }
        return StringUtils.positiveLongFields(items.replace(',', ';'), ';');
    }
}
