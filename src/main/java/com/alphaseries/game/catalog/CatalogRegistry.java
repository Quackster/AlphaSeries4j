package com.alphaseries.game.catalog;

import com.alphaseries.dao.mysql.CatalogDao;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class CatalogRegistry {
    private final Map<Long, CatalogRow> products;
    private final Map<Long, CatalogRow> catalogProducts;
    private final Map<Long, CatalogRow> deals;

    private CatalogRegistry(
        Map<Long, CatalogRow> products,
        Map<Long, CatalogRow> catalogProducts,
        Map<Long, CatalogRow> deals
    ) {
        this.products = new LinkedHashMap<>(products);
        this.catalogProducts = new LinkedHashMap<>(catalogProducts);
        this.deals = new LinkedHashMap<>(deals);
    }

    public static CatalogRegistry fromRowMaps(
        Map<Long, CatalogRow> products,
        Map<Long, CatalogRow> catalogProducts,
        Map<Long, CatalogRow> deals
    ) {
        return new CatalogRegistry(
            products == null ? Map.of() : products,
            catalogProducts == null ? Map.of() : catalogProducts,
            deals == null ? Map.of() : deals);
    }

    public static CatalogRegistry fromRows(
        Iterable<CatalogDao.ProductCacheRow> products,
        Iterable<CatalogDao.CatalogProductCacheRow> catalogProducts,
        Iterable<CatalogDao.ProductDealRow> deals
    ) {
        return new CatalogRegistry(
            productRowsById(products),
            catalogProductRowsById(catalogProducts),
            dealRowsById(deals));
    }

    public static CatalogRegistry empty() {
        return new CatalogRegistry(Map.of(), Map.of(), Map.of());
    }

    public List<CatalogRow> productRows() {
        return List.copyOf(products.values());
    }

    public Map<Long, CatalogRow> productRowMap() {
        return Map.copyOf(products);
    }

    public Optional<Product> product(long productId) {
        CatalogRow row = products.get(productId);
        if (row == null || row.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new Product(
            row.rowId(),
            row.productType(),
            row.productName(),
            row.productDescription(),
            row.productSprite(),
            row.defaultDecoration(),
            row.productStateCount(),
            row.squareZ(),
            row.chargeSize(),
            row.chargePriceCredits(),
            row.chargePriceActivityPoints(),
            row.chargePriceActivityPointsType()));
    }

    public List<CatalogRow> catalogProductRows() {
        return List.copyOf(catalogProducts.values());
    }

    public Map<Long, CatalogRow> catalogProductRowMap() {
        return Map.copyOf(catalogProducts);
    }

    public Optional<CatalogProduct> catalogProduct(long catalogProductId) {
        CatalogRow row = catalogProducts.get(catalogProductId);
        if (row == null || row.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new CatalogProduct(
            row.rowId(),
            row.catalogSprite(),
            row.catalogProductId(),
            row.catalogPageId(),
            row.catalogTypeSecondary(),
            row.catalogAmount(),
            row.catalogReceiveBadge(),
            row.catalogCreditPrice(),
            row.catalogActivityPrice(),
            row.catalogActivityType(),
            row.catalogAllowGifts(),
            row.catalogMinClubLevel(),
            row.catalogReplaceDefaultSign()));
    }

    public List<CatalogRow> dealRows() {
        return List.copyOf(deals.values());
    }

    public Map<Long, CatalogRow> dealRowMap() {
        return Map.copyOf(deals);
    }

    public Optional<ProductDeal> productDeal(long productId) {
        CatalogRow row = deals.get(productId);
        if (row == null || row.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new ProductDeal(row.rowId(), row.productDealItemIds()));
    }

    public record CatalogProduct(
        long catalogProductId,
        String sprite,
        long productId,
        long pageId,
        String typeSecondary,
        long amount,
        String receiveBadge,
        long creditPrice,
        long activityPrice,
        long activityType,
        long allowGifts,
        long minClubLevel,
        long replaceDefaultSign
    ) {
        public boolean isDeal() {
            return "products_deals".equalsIgnoreCase(typeSecondary);
        }
    }

    public record ProductDeal(long productId, List<Long> itemProductIds) {
    }

    public record Product(
        long productId,
        long type,
        String name,
        String description,
        String sprite,
        String defaultDecoration,
        long stateCount,
        long squareZ,
        long chargeSize,
        long chargePriceCredits,
        long chargePriceActivityPoints,
        long chargePriceActivityPointsType
    ) {
    }

    private static Map<Long, CatalogRow> productRowsById(Iterable<CatalogDao.ProductCacheRow> productRows) {
        Map<Long, CatalogRow> rows = new LinkedHashMap<>();
        if (productRows != null) {
            for (CatalogDao.ProductCacheRow row : productRows) {
                if (row != null) {
                    CatalogRow catalogRow = CatalogRow.fromProductRow(row);
                    rows.put(catalogRow.rowId(), catalogRow);
                }
            }
        }
        return rows;
    }

    private static Map<Long, CatalogRow> catalogProductRowsById(
        Iterable<CatalogDao.CatalogProductCacheRow> catalogProductRows
    ) {
        Map<Long, CatalogRow> rows = new LinkedHashMap<>();
        if (catalogProductRows != null) {
            for (CatalogDao.CatalogProductCacheRow row : catalogProductRows) {
                if (row != null) {
                    CatalogRow catalogRow = CatalogRow.fromCatalogProductRow(row);
                    rows.put(catalogRow.rowId(), catalogRow);
                }
            }
        }
        return rows;
    }

    private static Map<Long, CatalogRow> dealRowsById(Iterable<CatalogDao.ProductDealRow> dealRows) {
        Map<Long, CatalogRow> rows = new LinkedHashMap<>();
        if (dealRows != null) {
            for (CatalogDao.ProductDealRow row : dealRows) {
                if (row != null) {
                    rows.put(row.dealId(), CatalogRow.fromDeal(row.dealId(), row.itemProductIds()));
                }
            }
        }
        return rows;
    }

    public record CatalogRow(
        long rowId,
        int fieldCount,
        long productType,
        String productName,
        String productDescription,
        String productSprite,
        String defaultDecoration,
        long productStateCount,
        long squareZ,
        long chargeSize,
        long chargePriceCredits,
        long chargePriceActivityPoints,
        long chargePriceActivityPointsType,
        String catalogSprite,
        long catalogProductId,
        long catalogPageId,
        String catalogTypeSecondary,
        long catalogAmount,
        String catalogReceiveBadge,
        long catalogCreditPrice,
        long catalogActivityPrice,
        long catalogActivityType,
        long catalogAllowGifts,
        long catalogMinClubLevel,
        long catalogReplaceDefaultSign,
        List<Long> productDealItemIds
    ) {
        public CatalogRow {
            productName = StringUtils.text(productName);
            productDescription = StringUtils.text(productDescription);
            productSprite = StringUtils.text(productSprite);
            defaultDecoration = StringUtils.text(defaultDecoration);
            catalogSprite = StringUtils.text(catalogSprite);
            catalogTypeSecondary = StringUtils.text(catalogTypeSecondary);
            catalogReceiveBadge = StringUtils.text(catalogReceiveBadge);
            productDealItemIds = productDealItemIds == null ? List.of() : List.copyOf(productDealItemIds);
        }

        public static CatalogRow fromProductRow(CatalogDao.ProductCacheRow row) {
            return new CatalogRow(
                row.productId(),
                row.fieldCount(),
                row.type(),
                row.tradeName(),
                row.displayName(),
                row.primarySprite(),
                row.defaultDecoration(),
                NumberUtils.parseLong(row.defaultSign()),
                row.squareZ(),
                row.chargeSize(),
                row.chargePriceCredits(),
                row.chargePriceActivityPoints(),
                row.chargePriceActivityPointsType(),
                "",
                0L,
                0L,
                "",
                0L,
                "",
                0L,
                0L,
                0L,
                0L,
                0L,
                0L,
                List.of());
        }

        public static CatalogRow fromCatalogProductRow(CatalogDao.CatalogProductCacheRow row) {
            return new CatalogRow(
                row.rowId(),
                row.fieldCount(),
                0L,
                "",
                "",
                "",
                "",
                0L,
                0L,
                0L,
                0L,
                0L,
                0L,
                row.sprite(),
                row.productId(),
                row.pageId(),
                row.typeSecondary(),
                row.amount(),
                row.receiveBadge(),
                row.creditPrice(),
                row.activityPointPrice(),
                row.activityPointType(),
                row.allowGifts(),
                row.minimumHcRank(),
                row.replaceDefaultSign(),
                List.of());
        }

        public static CatalogRow fromDeal(long dealId, List<Long> itemProductIds) {
            return new CatalogRow(
                dealId,
                2,
                0L,
                "",
                "",
                "",
                "",
                0L,
                0L,
                0L,
                0L,
                0L,
                0L,
                "",
                0L,
                0L,
                "",
                0L,
                "",
                0L,
                0L,
                0L,
                0L,
                0L,
                0L,
                itemProductIds);
        }

        public boolean isEmpty() {
            return fieldCount == 0;
        }

    }
}
