package com.alphaseries.game.catalog;

import com.alphaseries.dao.mysql.CatalogDao;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ProductCache {
    private final Map<Long, ProductRow> rows;

    private ProductCache(Iterable<ProductRow> rows) {
        this.rows = rowsById(rows);
    }

    public static ProductCache fromRows(Iterable<CatalogDao.ProductCacheRow> rows) {
        return new ProductCache(productRows(rows));
    }

    public static ProductCache fromProductRows(Iterable<ProductRow> rows) {
        return new ProductCache(rows);
    }

    public static ProductCache empty() {
        return new ProductCache(List.of());
    }

    private String cell(long productId, long columnIndex) {
        ProductRow row = rows.get(productId);
        if (row == null) {
            return "";
        }
        return row.field((int) columnIndex);
    }

    public List<ProductRow> rows() {
        return List.copyOf(rows.values());
    }

    public long type(long productId) {
        return NumberUtils.parseLong(cell(productId, 0));
    }

    public String defaultSign(long productId) {
        return cell(productId, 4);
    }

    public String fallbackDefaultSign(long productId) {
        return cell(productId, 5);
    }

    public String interactionAction(long productId) {
        return cell(productId, 7);
    }

    public long stateCount(long productId) {
        return NumberUtils.parseLong(cell(productId, 10));
    }

    public long maxState(long productId) {
        return NumberUtils.parseLong(cell(productId, 12));
    }

    public String tradeName(long productId) {
        return cell(productId, 13);
    }

    public String displayName(long productId) {
        return cell(productId, 14);
    }

    public String description(long productId) {
        return cell(productId, 15);
    }

    public String primarySprite(long productId) {
        return cell(productId, 17);
    }

    public String alternateSprite(long productId) {
        return cell(productId, 18);
    }

    public boolean isPostItProduct(long productId) {
        return alternateSprite(productId).toLowerCase().startsWith("post.it");
    }

    public long dimensionMapId(long productId) {
        return NumberUtils.parseLong(cell(productId, 20));
    }

    public String itemData(long productId) {
        return cell(productId, 24);
    }

    public String badgeId(long productId) {
        return cell(productId, 26);
    }

    public String fallbackBadgeId(long productId) {
        return cell(productId, 27);
    }

    public long wiredCode(long productId) {
        return NumberUtils.parseLong(cell(productId, 27));
    }

    public boolean hasCharges(long productId) {
        return NumberUtils.parseLong(cell(productId, 34)) != 0L;
    }

    private static List<ProductRow> productRows(Iterable<CatalogDao.ProductCacheRow> rows) {
        if (rows == null) {
            return List.of();
        }
        List<ProductRow> productRows = new ArrayList<>();
        for (CatalogDao.ProductCacheRow row : rows) {
            if (row != null) {
                productRows.add(ProductRow.fromDaoRow(row));
            }
        }
        return List.copyOf(productRows);
    }

    private static Map<Long, ProductRow> rowsById(Iterable<ProductRow> productRows) {
        Map<Long, ProductRow> rows = new LinkedHashMap<>();
        if (productRows == null) {
            return rows;
        }
        for (ProductRow productRow : productRows) {
            if (productRow != null) {
                rows.put(productRow.productId(), productRow);
            }
        }
        return rows;
    }

    public record ProductRow(long productId, List<String> fields) {
        public ProductRow {
            fields = fields == null ? List.of() : List.copyOf(fields);
        }

        public static ProductRow fromDaoRow(CatalogDao.ProductCacheRow row) {
            return new ProductRow(NumberUtils.parseLong(ProductCache.field(row.values(), 0)), productFields(row.values()));
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
