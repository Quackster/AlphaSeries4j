package com.alphaseries.game.catalog;

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
                String[] columns = row.split("\t", -1);
                rows.put(NumberUtils.parseLong(StringUtils.field(columns, 0)), row);
            }
        }
        return rows;
    }
}
