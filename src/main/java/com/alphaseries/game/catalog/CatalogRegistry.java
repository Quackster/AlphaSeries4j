package com.alphaseries.game.catalog;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

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

    public String dealRow(long productId) {
        return StringUtils.text(deals.get(productId));
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
