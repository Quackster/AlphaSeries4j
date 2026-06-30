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
    private final Map<Long, String> products;
    private final Map<Long, String> catalogProducts;
    private final Map<Long, String> deals;

    private CatalogRegistry(Object products, Object catalogProducts, Object deals) {
        this.products = rowsById(products);
        this.catalogProducts = rowsById(catalogProducts);
        this.deals = rowsById(deals);
    }

    public static CatalogRegistry fromLegacyCaches(Object products, Object catalogProducts, Object deals) {
        return new CatalogRegistry(products, catalogProducts, deals);
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
        return StringUtils.text(products.get(productId));
    }

    public Optional<Product> product(long productId) {
        String row = StringUtils.text(products.get(productId));
        if (row.isEmpty()) {
            return Optional.empty();
        }
        String[] fields = row.split("\t", -1);
        return Optional.of(new Product(
            NumberUtils.parseLong(StringUtils.field(fields, 0)),
            NumberUtils.parseLong(StringUtils.field(fields, 1)),
            StringUtils.field(fields, 14),
            StringUtils.field(fields, 15),
            StringUtils.field(fields, 18),
            StringUtils.field(fields, 20),
            NumberUtils.parseLong(StringUtils.field(fields, 24)),
            NumberUtils.parseLong(StringUtils.field(fields, 34)),
            NumberUtils.parseLong(StringUtils.field(fields, 35)),
            NumberUtils.parseLong(StringUtils.field(fields, 36)),
            NumberUtils.parseLong(StringUtils.field(fields, 37))));
    }

    public String catalogProductRow(long catalogProductId) {
        return StringUtils.text(catalogProducts.get(catalogProductId));
    }

    public Optional<CatalogProduct> catalogProduct(long catalogProductId) {
        String row = StringUtils.text(catalogProducts.get(catalogProductId));
        if (row.isEmpty()) {
            return Optional.empty();
        }
        String[] fields = row.split("\t", -1);
        return Optional.of(new CatalogProduct(
            NumberUtils.parseLong(StringUtils.field(fields, 0)),
            StringUtils.field(fields, 1),
            NumberUtils.parseLong(StringUtils.field(fields, 2)),
            NumberUtils.parseLong(StringUtils.field(fields, 3)),
            StringUtils.field(fields, 4),
            NumberUtils.parseLong(StringUtils.field(fields, 5)),
            StringUtils.field(fields, 6),
            NumberUtils.parseLong(StringUtils.field(fields, 7)),
            NumberUtils.parseLong(StringUtils.field(fields, 8)),
            NumberUtils.parseLong(StringUtils.field(fields, 9)),
            NumberUtils.parseLong(StringUtils.field(fields, 10)),
            NumberUtils.parseLong(StringUtils.field(fields, 11)),
            NumberUtils.parseLong(StringUtils.field(fields, 12))));
    }

    public String dealRow(long productId) {
        return StringUtils.text(deals.get(productId));
    }

    public Optional<ProductDeal> productDeal(long productId) {
        String row = StringUtils.text(deals.get(productId));
        if (row.isEmpty()) {
            return Optional.empty();
        }
        String[] fields = row.split("\t", -1);
        String itemText = fields.length >= 2 ? fields[1] : row;
        List<Long> itemProductIds = new ArrayList<>();
        for (String item : itemText.replace(',', ';').split(";", -1)) {
            long itemProductId = NumberUtils.parseLong(item);
            if (itemProductId > 0L) {
                itemProductIds.add(itemProductId);
            }
        }
        return Optional.of(new ProductDeal(NumberUtils.parseLong(StringUtils.field(fields, 0)), itemProductIds));
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

    private static String cell(Map<Long, String> rows, long rowId, long columnIndex) {
        String row = StringUtils.text(rows.get(rowId));
        if (row.isEmpty()) {
            return "";
        }
        String[] columns = row.split("\t", -1);
        return columnIndex >= 0 && columnIndex < columns.length ? columns[(int) columnIndex] : "";
    }

    private static Map<Long, String> rowsById(Object cache) {
        Map<Long, String> rows = new LinkedHashMap<>();
        if (cache instanceof Iterable<?> values) {
            for (Object value : values) {
                if (value instanceof CatalogDao.ProductCacheRow row) {
                    String rowText = String.join("\t", row.values());
                    rows.put(NumberUtils.parseLong(field(row.values(), 0)), rowText);
                } else if (value instanceof CatalogDao.CatalogProductCacheRow row) {
                    String rowText = String.join("\t", row.values());
                    rows.put(NumberUtils.parseLong(field(row.values(), 0)), rowText);
                } else if (value instanceof CatalogDao.ProductDealRow row) {
                    rows.put(row.dealId(), row.dealId() + "\t" + StringUtils.text(row.items()));
                }
            }
            return rows;
        }
        if (cache instanceof String[] rowArray) {
            for (int index = 0; index < rowArray.length; index++) {
                String row = StringUtils.text(rowArray[index]);
                if (!row.isEmpty()) {
                    rows.put((long) index, row);
                }
            }
            return rows;
        }
        for (String row : StringUtils.text(cache).split("\r", -1)) {
            if (!row.isEmpty()) {
                String[] fields = row.split("\t", -1);
                long rowId = NumberUtils.parseLong(StringUtils.field(fields, 0));
                rows.put(rowId, row);
            }
        }
        return rows;
    }

    private static String field(List<String> fields, int index) {
        return fields != null && index >= 0 && index < fields.size() ? StringUtils.text(fields.get(index)) : "";
    }
}
