package com.alphaseries.game.catalog;

import java.util.LinkedHashMap;
import java.util.Map;

import com.alphaseries.util.StringUtils;

public final class CatalogPages {
    private final Map<Long, String> pagePayloads;
    private final Map<PageTreeKey, String> pageTrees;

    private CatalogPages(Map<Long, String> pagePayloads, Map<PageTreeKey, String> pageTrees) {
        this.pagePayloads = copyPayloads(pagePayloads);
        this.pageTrees = copyPageTrees(pageTrees);
    }

    public static CatalogPages empty() {
        return new CatalogPages(Map.of(), Map.of());
    }

    public static CatalogPages fromPayloadMaps(Map<Long, String> pagePayloads, Map<PageTreeKey, String> pageTrees) {
        return new CatalogPages(pagePayloads, pageTrees);
    }

    public Map<Long, String> pagePayloads() {
        return Map.copyOf(pagePayloads);
    }

    public Map<PageTreeKey, String> pageTrees() {
        return Map.copyOf(pageTrees);
    }

    public String pagePayload(long pageId) {
        if (pageId < 0L) {
            return "";
        }
        return StringUtils.text(pagePayloads.get(pageId));
    }

    public String pageTree(long rankIndex, long hcLevel) {
        int rank = (int) rankIndex;
        int hc = (int) hcLevel;
        if (rank < 0 || hc < 0) {
            return "";
        }
        return StringUtils.text(pageTrees.get(new PageTreeKey(rank, hc)));
    }

    public String defaultPageTree() {
        return pageTree(0L, 0L);
    }

    private static Map<Long, String> copyPayloads(Map<Long, String> pagePayloads) {
        Map<Long, String> copiedPayloads = new LinkedHashMap<>();
        if (pagePayloads != null) {
            for (Map.Entry<Long, String> entry : pagePayloads.entrySet()) {
                if (entry.getKey() != null && entry.getKey() >= 0L) {
                    copiedPayloads.put(entry.getKey(), StringUtils.text(entry.getValue()));
                }
            }
        }
        return copiedPayloads;
    }

    private static Map<PageTreeKey, String> copyPageTrees(Map<PageTreeKey, String> pageTrees) {
        Map<PageTreeKey, String> copiedTrees = new LinkedHashMap<>();
        if (pageTrees != null) {
            for (Map.Entry<PageTreeKey, String> entry : pageTrees.entrySet()) {
                if (entry.getKey() != null) {
                    copiedTrees.put(entry.getKey(), StringUtils.text(entry.getValue()));
                }
            }
        }
        return copiedTrees;
    }

    public record PageTreeKey(long rankIndex, long hcLevel) {
        public PageTreeKey {
            rankIndex = Math.max(0L, rankIndex);
            hcLevel = Math.max(0L, hcLevel);
        }
    }
}
