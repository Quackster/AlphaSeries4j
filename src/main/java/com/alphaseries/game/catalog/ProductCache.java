package com.alphaseries.game.catalog;

import com.alphaseries.dao.mysql.CatalogDao;
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

    private ProductRow row(long productId) {
        ProductRow row = rows.get(productId);
        return row == null ? ProductRow.empty(productId) : row;
    }

    public List<ProductRow> rows() {
        return List.copyOf(rows.values());
    }

    public long type(long productId) {
        return row(productId).type();
    }

    public String defaultSign(long productId) {
        return row(productId).defaultSign();
    }

    public String fallbackDefaultSign(long productId) {
        return row(productId).fallbackDefaultSign();
    }

    public String interactionAction(long productId) {
        return row(productId).interactionAction();
    }

    public long stateCount(long productId) {
        return row(productId).stateCount();
    }

    public long maxState(long productId) {
        return row(productId).maxState();
    }

    public String tradeName(long productId) {
        return row(productId).tradeName();
    }

    public String displayName(long productId) {
        return row(productId).displayName();
    }

    public String description(long productId) {
        return row(productId).description();
    }

    public String primarySprite(long productId) {
        return row(productId).primarySprite();
    }

    public String alternateSprite(long productId) {
        return row(productId).alternateSprite();
    }

    public boolean isPostItProduct(long productId) {
        return alternateSprite(productId).toLowerCase().startsWith("post.it");
    }

    public long dimensionMapId(long productId) {
        return row(productId).dimensionMapId();
    }

    public String itemData(long productId) {
        return row(productId).itemData();
    }

    public String badgeId(long productId) {
        return row(productId).badgeId();
    }

    public String fallbackBadgeId(long productId) {
        return row(productId).fallbackBadgeId();
    }

    public long wiredCode(long productId) {
        return row(productId).wiredCode();
    }

    public boolean hasCharges(long productId) {
        return row(productId).hasCharges();
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

    public record ProductRow(
        long productId,
        long type,
        String defaultSign,
        String fallbackDefaultSign,
        String interactionAction,
        long stateCount,
        long maxState,
        String tradeName,
        String displayName,
        String description,
        String primarySprite,
        String alternateSprite,
        long dimensionMapId,
        String itemData,
        String badgeId,
        String fallbackBadgeId,
        long wiredCode,
        boolean hasCharges
    ) {
        public ProductRow {
            defaultSign = StringUtils.text(defaultSign);
            fallbackDefaultSign = StringUtils.text(fallbackDefaultSign);
            interactionAction = StringUtils.text(interactionAction);
            tradeName = StringUtils.text(tradeName);
            displayName = StringUtils.text(displayName);
            description = StringUtils.text(description);
            primarySprite = StringUtils.text(primarySprite);
            alternateSprite = StringUtils.text(alternateSprite);
            itemData = StringUtils.text(itemData);
            badgeId = StringUtils.text(badgeId);
            fallbackBadgeId = StringUtils.text(fallbackBadgeId);
        }

        public static ProductRow fromDaoRow(CatalogDao.ProductCacheRow row) {
            return new ProductRow(
                row.productId(),
                row.type(),
                row.defaultSign(),
                row.fallbackDefaultSign(),
                row.interactionAction(),
                row.stateCount(),
                row.maxState(),
                row.tradeName(),
                row.displayName(),
                row.description(),
                row.primarySprite(),
                row.alternateSprite(),
                row.dimensionMapId(),
                row.itemData(),
                row.badgeId(),
                row.fallbackBadgeId(),
                row.wiredCode(),
                row.hasCharges());
        }

        private static ProductRow empty(long productId) {
            return new ProductRow(productId, 0L, "", "", "", 0L, 0L, "", "", "", "", "", 0L, "", "", "", 0L, false);
        }
    }

}
