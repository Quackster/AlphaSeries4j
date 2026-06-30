package com.alphaseries.game.catalog;

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

    private CatalogRegistry(Object products, Object catalogProducts, String deals) {
        this.products = rowsById(products);
        this.catalogProducts = rowsById(catalogProducts);
        this.deals = rowsById(deals);
    }

    public static CatalogRegistry fromLegacyCaches(Object products, Object catalogProducts, String deals) {
        return new CatalogRegistry(products, catalogProducts, deals);
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
}
