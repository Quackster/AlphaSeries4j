package com.alphaseries.game.catalog;

import com.alphaseries.dao.mysql.CatalogDao;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ProductCache {
    private final Map<Long, ProductRow> rows;

    private ProductCache(Object legacyRows) {
        this.rows = rowsById(legacyRows == null ? "" : legacyRows);
    }

    public static ProductCache fromLegacy(Object legacyRows) {
        return new ProductCache(legacyRows);
    }

    public static ProductCache fromRows(Iterable<CatalogDao.ProductCacheRow> rows) {
        return new ProductCache(rows);
    }

    public String cell(long productId, long columnIndex) {
        ProductRow row = rows.get(productId);
        if (row == null) {
            return "";
        }
        return row.field((int) columnIndex);
    }

    public List<ProductRow> rows() {
        return List.copyOf(rows.values());
    }

    /**
     * Original function: DataManager.Proc_8_12_806C30.
     */
    public long type(long productId) {
        return NumberUtils.parseLong(cell(productId, 0));
    }

    /**
     * Original function: DataManager.Proc_8_12_806C30.
     */
    public String defaultSign(long productId) {
        return cell(productId, 4);
    }

    /**
     * Original function: DataManager.Proc_8_12_806C30.
     */
    public String fallbackDefaultSign(long productId) {
        return cell(productId, 5);
    }

    /**
     * Original function: DataManager.Proc_8_12_806C30.
     */
    public String interactionAction(long productId) {
        return cell(productId, 7);
    }

    /**
     * Original function: DataManager.Proc_8_12_806C30.
     */
    public long stateCount(long productId) {
        return NumberUtils.parseLong(cell(productId, 10));
    }

    /**
     * Original function: DataManager.Proc_8_12_806C30.
     */
    public long maxState(long productId) {
        return NumberUtils.parseLong(cell(productId, 12));
    }

    /**
     * Original function: DataManager.Proc_8_12_806C30.
     */
    public String tradeName(long productId) {
        return cell(productId, 13);
    }

    /**
     * Original function: DataManager.Proc_8_12_806C30.
     */
    public String displayName(long productId) {
        return cell(productId, 14);
    }

    /**
     * Original function: DataManager.Proc_8_12_806C30.
     */
    public String description(long productId) {
        return cell(productId, 15);
    }

    /**
     * Original function: DataManager.Proc_8_12_806C30.
     */
    public String primarySprite(long productId) {
        return cell(productId, 17);
    }

    /**
     * Original function: DataManager.Proc_8_12_806C30.
     */
    public String alternateSprite(long productId) {
        return cell(productId, 18);
    }

    /**
     * Original function: DataManager.Proc_8_12_806C30.
     */
    public long dimensionMapId(long productId) {
        return NumberUtils.parseLong(cell(productId, 20));
    }

    /**
     * Original function: DataManager.Proc_8_12_806C30.
     */
    public String itemData(long productId) {
        return cell(productId, 24);
    }

    /**
     * Original function: DataManager.Proc_8_12_806C30.
     */
    public String badgeId(long productId) {
        return cell(productId, 26);
    }

    /**
     * Original function: DataManager.Proc_8_12_806C30.
     */
    public String fallbackBadgeId(long productId) {
        return cell(productId, 27);
    }

    /**
     * Original function: DataManager.Proc_8_12_806C30.
     */
    public long wiredCode(long productId) {
        return NumberUtils.parseLong(cell(productId, 27));
    }

    /**
     * Original function: DataManager.Proc_8_12_806C30.
     */
    public boolean hasCharges(long productId) {
        return NumberUtils.parseLong(cell(productId, 34)) != 0L;
    }

    private static Map<Long, ProductRow> rowsById(Object cache) {
        Map<Long, ProductRow> rows = new LinkedHashMap<>();
        if (cache instanceof Iterable<?> values) {
            for (Object value : values) {
                if (value instanceof CatalogDao.ProductCacheRow row) {
                    ProductRow productRow = ProductRow.fromDaoRow(row);
                    rows.put(productRow.productId(), productRow);
                }
            }
            return rows;
        }
        if (cache instanceof String[] rowArray) {
            for (int index = 0; index < rowArray.length; index++) {
                String row = StringUtils.text(rowArray[index]);
                if (!row.isEmpty()) {
                    rows.put((long) index, new ProductRow(index, Arrays.asList(row.split("\t", -1))));
                }
            }
            return rows;
        }
        for (String row : StringUtils.text(cache).split("\r", -1)) {
            if (!row.isEmpty()) {
                String[] columns = row.split("\t", -1);
                ProductRow productRow = ProductRow.fromLegacyColumns(List.of(columns));
                rows.put(productRow.productId(), productRow);
            }
        }
        return rows;
    }

    public record ProductRow(long productId, List<String> fields) {
        public ProductRow {
            fields = fields == null ? List.of() : List.copyOf(fields);
        }

        private static ProductRow fromDaoRow(CatalogDao.ProductCacheRow row) {
            return new ProductRow(NumberUtils.parseLong(ProductCache.field(row.values(), 0)), productFields(row.values()));
        }

        private static ProductRow fromLegacyColumns(List<String> columns) {
            return new ProductRow(NumberUtils.parseLong(ProductCache.field(columns, 0)), columns == null ? List.of() : columns);
        }

        private String field(int index) {
            return ProductCache.field(fields, index);
        }
    }

    private static List<String> productFields(List<String> rowValues) {
        if (rowValues == null || rowValues.size() <= 1) {
            return List.of();
        }
        return List.copyOf(rowValues.subList(1, rowValues.size()));
    }

    private static String field(List<String> fields, int index) {
        return fields != null && index >= 0 && index < fields.size() ? StringUtils.text(fields.get(index)) : "";
    }
}
