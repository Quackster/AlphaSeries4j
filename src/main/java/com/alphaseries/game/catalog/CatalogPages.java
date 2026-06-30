package com.alphaseries.game.catalog;

import java.util.LinkedHashMap;
import java.util.Map;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

public final class CatalogPages {
    private final Map<Long, String> pagePayloads;
    private final String[][] pageTrees;

    private CatalogPages(Object pagePayloads, Object pageTrees) {
        this.pagePayloads = parsePayloads(pagePayloads);
        this.pageTrees = parsePageTrees(pageTrees);
    }

    private CatalogPages(Map<Long, String> pagePayloads, String[][] pageTrees) {
        this.pagePayloads = copyPayloads(pagePayloads);
        this.pageTrees = copyPageTrees(pageTrees);
    }

    public static CatalogPages fromLegacy(Object pagePayloads, Object pageTrees) {
        if (pagePayloads instanceof CatalogPages catalogPages) {
            return catalogPages;
        }
        return new CatalogPages(pagePayloads, pageTrees);
    }

    public static CatalogPages empty() {
        return new CatalogPages("", "");
    }

    public static CatalogPages fromPayloads(Map<Long, String> pagePayloads, String[][] pageTrees) {
        return new CatalogPages(pagePayloads, pageTrees);
    }

    public Map<Long, String> pagePayloads() {
        return Map.copyOf(pagePayloads);
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
        return rank < pageTrees.length && pageTrees[rank] != null && hc < pageTrees[rank].length
            ? StringUtils.text(pageTrees[rank][hc]) : "";
    }

    public String[][] pageTrees() {
        return copyPageTrees(pageTrees);
    }

    public String defaultPageTree() {
        return pageTree(0L, 0L);
    }

    private static Map<Long, String> parsePayloads(Object cache) {
        if (cache instanceof Map<?, ?> values) {
            Map<Long, String> payloads = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : values.entrySet()) {
                long key = NumberUtils.parseLong(entry.getKey());
                if (key >= 0L) {
                    payloads.put(key, StringUtils.text(entry.getValue()));
                }
            }
            return payloads;
        }
        if (cache instanceof Object[] values) {
            Map<Long, String> payloads = new LinkedHashMap<>();
            for (int index = 0; index < values.length; index++) {
                payloads.put((long) index, StringUtils.text(values[index]));
            }
            return payloads;
        }
        return Map.of();
    }

    private static String[][] parsePageTrees(Object pageTrees) {
        if (pageTrees instanceof String[][] values) {
            return copyPageTrees(values);
        }
        if (pageTrees instanceof Object[][] values) {
            String[][] parsedTrees = new String[values.length][];
            for (int rank = 0; rank < values.length; rank++) {
                if (values[rank] == null) {
                    continue;
                }
                parsedTrees[rank] = new String[values[rank].length];
                for (int hc = 0; hc < values[rank].length; hc++) {
                    parsedTrees[rank][hc] = StringUtils.text(values[rank][hc]);
                }
            }
            return parsedTrees;
        }
        return new String[0][];
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

    private static String[][] copyPageTrees(String[][] pageTrees) {
        if (pageTrees == null) {
            return new String[0][];
        }
        String[][] copiedTrees = new String[pageTrees.length][];
        for (int rank = 0; rank < pageTrees.length; rank++) {
            if (pageTrees[rank] == null) {
                continue;
            }
            copiedTrees[rank] = new String[pageTrees[rank].length];
            for (int hc = 0; hc < pageTrees[rank].length; hc++) {
                copiedTrees[rank][hc] = StringUtils.text(pageTrees[rank][hc]);
            }
        }
        return copiedTrees;
    }
}
