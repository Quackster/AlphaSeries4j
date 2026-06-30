package com.alphaseries.game.catalog;

import com.alphaseries.dao.mysql.CatalogDao;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ProductCache {
    private final Object legacyRows;
    private final Map<Long, String> rows;

    private ProductCache(Object legacyRows) {
        this.legacyRows = legacyRows == null ? "" : legacyRows;
        this.rows = rowsById(this.legacyRows);
    }

    public static ProductCache fromLegacy(Object legacyRows) {
        return new ProductCache(legacyRows);
    }

    public Object legacyRows() {
        return legacyRows;
    }

    public String cell(long productId, long columnIndex) {
        String row = StringUtils.text(rows.get(productId));
        if (row.isEmpty()) {
            return "";
        }
        String[] columns = row.split("\t", -1);
        return columnIndex >= 0 && columnIndex < columns.length ? columns[(int) columnIndex] : "";
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
    public String itemData(long productId) {
        return cell(productId, 24);
    }

    /**
     * Original function: DataManager.Proc_8_12_806C30.
     */
    public long wiredCode(long productId) {
        return NumberUtils.parseLong(cell(productId, 27));
    }

    private static Map<Long, String> rowsById(Object cache) {
        Map<Long, String> rows = new LinkedHashMap<>();
        if (cache instanceof Iterable<?> values) {
            for (Object value : values) {
                if (value instanceof CatalogDao.ProductCacheRow row) {
                    String rowText = String.join("\t", row.values());
                    rows.put(NumberUtils.parseLong(field(row.values(), 0)), rowText);
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
                String[] columns = row.split("\t", -1);
                rows.put(NumberUtils.parseLong(StringUtils.field(columns, 0)), row);
            }
        }
        return rows;
    }

    private static String field(java.util.List<String> fields, int index) {
        return fields != null && index >= 0 && index < fields.size() ? StringUtils.text(fields.get(index)) : "";
    }
}
