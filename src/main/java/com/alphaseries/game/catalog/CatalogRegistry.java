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

    public String productCell(long productId, long columnIndex) {
        return cell(products, productId, columnIndex);
    }

    public String catalogProductCell(long catalogProductId, long columnIndex) {
        return cell(catalogProducts, catalogProductId, columnIndex);
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
            NumberUtils.parseLong(row.field(0)),
            NumberUtils.parseLong(row.field(1)),
            row.field(14),
            row.field(15),
            row.field(18),
            row.field(20),
            NumberUtils.parseLong(row.field(24)),
            NumberUtils.parseLong(row.field(34)),
            NumberUtils.parseLong(row.field(35)),
            NumberUtils.parseLong(row.field(36)),
            NumberUtils.parseLong(row.field(37))));
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
            NumberUtils.parseLong(row.field(0)),
            row.field(1),
            NumberUtils.parseLong(row.field(2)),
            NumberUtils.parseLong(row.field(3)),
            row.field(4),
            NumberUtils.parseLong(row.field(5)),
            row.field(6),
            NumberUtils.parseLong(row.field(7)),
            NumberUtils.parseLong(row.field(8)),
            NumberUtils.parseLong(row.field(9)),
            NumberUtils.parseLong(row.field(10)),
            NumberUtils.parseLong(row.field(11)),
            NumberUtils.parseLong(row.field(12))));
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
        return Optional.of(new ProductDeal(NumberUtils.parseLong(row.field(0)), row.productDealItemIds()));
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
        long squareZ,
        long chargeSize,
        long chargePriceCredits,
        long chargePriceActivityPoints,
        long chargePriceActivityPointsType
    ) {
    }

    private static String cell(Map<Long, CatalogRow> rows, long rowId, long columnIndex) {
        CatalogRow row = rows.get(rowId);
        if (row == null || row.isEmpty()) {
            return "";
        }
        return columnIndex >= 0 && columnIndex < row.fieldCount() ? row.field((int) columnIndex) : "";
    }

    private static Map<Long, CatalogRow> productRowsById(Iterable<CatalogDao.ProductCacheRow> productRows) {
        Map<Long, CatalogRow> rows = new LinkedHashMap<>();
        if (productRows != null) {
            for (CatalogDao.ProductCacheRow row : productRows) {
                if (row != null) {
                    CatalogRow catalogRow = CatalogRow.fromFields(row.values());
                    rows.put(NumberUtils.parseLong(catalogRow.field(0)), catalogRow);
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
                    CatalogRow catalogRow = CatalogRow.fromFields(row.values());
                    rows.put(NumberUtils.parseLong(catalogRow.field(0)), catalogRow);
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

    public record CatalogRow(String text, List<String> fields, List<Long> productDealItemIds) {
        public CatalogRow(String text, List<String> fields) {
            this(text, fields, productDealItemIds(fields));
        }

        public CatalogRow {
            text = StringUtils.text(text);
            fields = fields == null ? List.of() : List.copyOf(fields);
            productDealItemIds = productDealItemIds == null ? List.of() : List.copyOf(productDealItemIds);
        }

        public static CatalogRow fromFields(List<String> fields) {
            List<String> copiedFields = fields == null
                ? List.of()
                : fields.stream().map(StringUtils::text).toList();
            return new CatalogRow(String.join("\t", copiedFields), copiedFields);
        }

        public static CatalogRow fromDeal(long dealId, List<Long> itemProductIds) {
            List<Long> copiedItemProductIds = itemProductIds == null ? List.of() : List.copyOf(itemProductIds);
            List<String> itemTexts = new ArrayList<>();
            for (long itemProductId : copiedItemProductIds) {
                itemTexts.add(String.valueOf(itemProductId));
            }
            String itemsText = String.join(";", itemTexts);
            return new CatalogRow(
                dealId + "\t" + itemsText,
                List.of(String.valueOf(dealId), itemsText),
                copiedItemProductIds);
        }

        public boolean isEmpty() {
            return text.isEmpty();
        }

        public int fieldCount() {
            return fields.size();
        }

        private String field(int index) {
            return index >= 0 && index < fields.size() ? fields.get(index) : "";
        }

        private static List<Long> productDealItemIds(List<String> fields) {
            String itemText = fields != null && fields.size() >= 2 ? fields.get(1) : "";
            List<Long> itemProductIds = new ArrayList<>();
            for (String item : StringUtils.text(itemText).replace(',', ';').split(";", -1)) {
                long itemProductId = NumberUtils.parseLong(item);
                if (itemProductId > 0L) {
                    itemProductIds.add(itemProductId);
                }
            }
            return List.copyOf(itemProductIds);
        }
    }
}
