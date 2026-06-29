package com.alphaseries.game.catalog;

import com.alphaseries.util.StringUtils;

public final class CatalogPages {
    private final Object pagePayloads;
    private final Object pageTrees;

    private CatalogPages(Object pagePayloads, Object pageTrees) {
        this.pagePayloads = pagePayloads == null ? "" : pagePayloads;
        this.pageTrees = pageTrees == null ? "" : pageTrees;
    }

    public static CatalogPages fromLegacy(Object pagePayloads, Object pageTrees) {
        return new CatalogPages(pagePayloads, pageTrees);
    }

    public String pagePayload(long pageId) {
        return indexedPayload(pagePayloads, pageId);
    }

    public String pageTree(long rankIndex, long hcLevel) {
        int rank = (int) rankIndex;
        int hc = (int) hcLevel;
        if (rank < 0 || hc < 0) {
            return "";
        }
        if (pageTrees instanceof String[][] values) {
            return rank < values.length && values[rank] != null && hc < values[rank].length
                ? StringUtils.text(values[rank][hc]) : "";
        }
        if (pageTrees instanceof Object[][] values) {
            return rank < values.length && values[rank] != null && hc < values[rank].length
                ? StringUtils.text(values[rank][hc]) : "";
        }
        return "";
    }

    public String defaultPageTree() {
        return pageTree(0L, 0L);
    }

    private static String indexedPayload(Object cache, long index) {
        int idx = (int) index;
        if (idx < 0) {
            return "";
        }
        if (cache instanceof String[] values) {
            return idx < values.length ? StringUtils.text(values[idx]) : "";
        }
        if (cache instanceof Object[] values) {
            return idx < values.length ? StringUtils.text(values[idx]) : "";
        }
        return "";
    }
}
