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

    private CatalogRegistry(Object products, Object catalogProducts, Object deals) {
        this.products = rowsById(products);
        this.catalogProducts = rowsById(catalogProducts);
        this.deals = rowsById(deals);
    }

    private CatalogRegistry(
        Map<Long, CatalogRow> products,
        Map<Long, CatalogRow> catalogProducts,
        Map<Long, CatalogRow> deals
    ) {
        this.products = new LinkedHashMap<>(products);
        this.catalogProducts = new LinkedHashMap<>(catalogProducts);
        this.deals = new LinkedHashMap<>(deals);
    }

    public static CatalogRegistry fromLegacyCaches(Object products, Object catalogProducts, Object deals) {
        return new CatalogRegistry(products, catalogProducts, deals);
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
        return new CatalogRegistry("", "", "");
    }

    public String productCell(long productId, long columnIndex) {
        return cell(products, productId, columnIndex);
    }

    public String catalogProductCell(long catalogProductId, long columnIndex) {
        return cell(catalogProducts, catalogProductId, columnIndex);
    }

    public String productRow(long productId) {
        CatalogRow row = products.get(productId);
        return row == null ? "" : row.text();
    }

    public List<CatalogRow> productRows() {
        return List.copyOf(products.values());
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

    public String catalogProductRow(long catalogProductId) {
        CatalogRow row = catalogProducts.get(catalogProductId);
        return row == null ? "" : row.text();
    }

    public List<CatalogRow> catalogProductRows() {
        return List.copyOf(catalogProducts.values());
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

    public String dealRow(long productId) {
        CatalogRow row = deals.get(productId);
        return row == null ? "" : row.text();
    }

    public List<CatalogRow> dealRows() {
        return List.copyOf(deals.values());
    }

    public Optional<ProductDeal> productDeal(long productId) {
        CatalogRow row = deals.get(productId);
        if (row == null || row.isEmpty()) {
            return Optional.empty();
        }
        String itemText = row.fieldCount() >= 2 ? row.field(1) : row.text();
        List<Long> itemProductIds = new ArrayList<>();
        for (String item : itemText.replace(',', ';').split(";", -1)) {
            long itemProductId = NumberUtils.parseLong(item);
            if (itemProductId > 0L) {
                itemProductIds.add(itemProductId);
            }
        }
        return Optional.of(new ProductDeal(NumberUtils.parseLong(row.field(0)), itemProductIds));
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

    private static Map<Long, CatalogRow> rowsById(Object cache) {
        Map<Long, CatalogRow> rows = new LinkedHashMap<>();
        if (cache instanceof Iterable<?> values) {
            for (Object value : values) {
                if (value instanceof CatalogDao.ProductCacheRow row) {
                    CatalogRow catalogRow = CatalogRow.fromFields(row.values());
                    rows.put(NumberUtils.parseLong(catalogRow.field(0)), catalogRow);
                } else if (value instanceof CatalogDao.CatalogProductCacheRow row) {
                    CatalogRow catalogRow = CatalogRow.fromFields(row.values());
                    rows.put(NumberUtils.parseLong(catalogRow.field(0)), catalogRow);
                } else if (value instanceof CatalogDao.ProductDealRow row) {
                    rows.put(row.dealId(), CatalogRow.fromFields(List.of(
                        String.valueOf(row.dealId()),
                        StringUtils.text(row.items()))));
                }
            }
            return rows;
        }
        if (cache instanceof String[] rowArray) {
            for (int index = 0; index < rowArray.length; index++) {
                String row = StringUtils.text(rowArray[index]);
                if (!row.isEmpty()) {
                    rows.put((long) index, CatalogRow.fromText(row));
                }
            }
            return rows;
        }
        for (String row : StringUtils.text(cache).split("\r", -1)) {
            if (!row.isEmpty()) {
                String[] fields = row.split("\t", -1);
                long rowId = NumberUtils.parseLong(StringUtils.field(fields, 0));
                rows.put(rowId, new CatalogRow(row, List.of(fields)));
            }
        }
        return rows;
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
                    rows.put(row.dealId(), CatalogRow.fromFields(List.of(
                        String.valueOf(row.dealId()),
                        StringUtils.text(row.items()))));
                }
            }
        }
        return rows;
    }

    public record CatalogRow(String text, List<String> fields) {
        public CatalogRow {
            text = StringUtils.text(text);
            fields = fields == null ? List.of() : List.copyOf(fields);
        }

        private static CatalogRow fromFields(List<String> fields) {
            List<String> copiedFields = fields == null
                ? List.of()
                : fields.stream().map(StringUtils::text).toList();
            return new CatalogRow(String.join("\t", copiedFields), copiedFields);
        }

        private static CatalogRow fromText(String rowText) {
            String text = StringUtils.text(rowText);
            return new CatalogRow(text, List.of(text.split("\t", -1)));
        }

        public boolean isEmpty() {
            return text.isEmpty();
        }

        public int fieldCount() {
            return fields.size();
        }

        public String field(int index) {
            return index >= 0 && index < fields.size() ? fields.get(index) : "";
        }
    }
}
