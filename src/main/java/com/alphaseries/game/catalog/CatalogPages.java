package com.alphaseries.game.catalog;

import java.util.LinkedHashMap;
import java.util.Map;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

public final class CatalogPages {
    private final Map<Long, String> pagePayloads;
    private final Map<PageTreeKey, String> pageTrees;

    private CatalogPages(Object pagePayloads, Object pageTrees) {
        this.pagePayloads = parsePayloads(pagePayloads);
        this.pageTrees = parsePageTrees(pageTrees);
    }

    private CatalogPages(Map<Long, String> pagePayloads, String[][] pageTrees) {
        this.pagePayloads = copyPayloads(pagePayloads);
        this.pageTrees = parsePageTrees(pageTrees);
    }

    private CatalogPages(Map<Long, String> pagePayloads, Map<PageTreeKey, String> pageTrees) {
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

    public static CatalogPages fromPayloadMaps(Map<Long, String> pagePayloads, Map<PageTreeKey, String> pageTrees) {
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
        return StringUtils.text(pageTrees.get(new PageTreeKey(rank, hc)));
    }

    public String[][] pageTrees() {
        return pageTreeArray(pageTrees);
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

    private static Map<PageTreeKey, String> parsePageTrees(Object pageTrees) {
        if (pageTrees instanceof Map<?, ?> values) {
            Map<PageTreeKey, String> parsedTrees = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : values.entrySet()) {
                PageTreeKey key = pageTreeKey(entry.getKey());
                if (key != null) {
                    parsedTrees.put(key, StringUtils.text(entry.getValue()));
                }
            }
            return parsedTrees;
        }
        if (pageTrees instanceof String[][] values) {
            return copyPageTrees(values);
        }
        if (pageTrees instanceof Object[][] values) {
            Map<PageTreeKey, String> parsedTrees = new LinkedHashMap<>();
            for (int rank = 0; rank < values.length; rank++) {
                if (values[rank] == null) {
                    continue;
                }
                for (int hc = 0; hc < values[rank].length; hc++) {
                    parsedTrees.put(new PageTreeKey(rank, hc), StringUtils.text(values[rank][hc]));
                }
            }
            return parsedTrees;
        }
        return Map.of();
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

    private static Map<PageTreeKey, String> copyPageTrees(String[][] pageTrees) {
        Map<PageTreeKey, String> copiedTrees = new LinkedHashMap<>();
        if (pageTrees == null) {
            return copiedTrees;
        }
        for (int rank = 0; rank < pageTrees.length; rank++) {
            if (pageTrees[rank] == null) {
                continue;
            }
            for (int hc = 0; hc < pageTrees[rank].length; hc++) {
                copiedTrees.put(new PageTreeKey(rank, hc), StringUtils.text(pageTrees[rank][hc]));
            }
        }
        return copiedTrees;
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

    private static String[][] pageTreeArray(Map<PageTreeKey, String> pageTrees) {
        long maxRank = -1L;
        long maxHc = -1L;
        for (PageTreeKey key : pageTrees.keySet()) {
            maxRank = Math.max(maxRank, key.rankIndex());
            maxHc = Math.max(maxHc, key.hcLevel());
        }
        if (maxRank < 0L || maxHc < 0L) {
            return new String[0][];
        }
        String[][] values = new String[(int) maxRank + 1][(int) maxHc + 1];
        for (Map.Entry<PageTreeKey, String> entry : pageTrees.entrySet()) {
            values[(int) entry.getKey().rankIndex()][(int) entry.getKey().hcLevel()] = StringUtils.text(entry.getValue());
        }
        return values;
    }

    private static PageTreeKey pageTreeKey(Object key) {
        if (key instanceof PageTreeKey pageTreeKey) {
            return pageTreeKey;
        }
        if (key instanceof String text) {
            String[] fields = text.split(":", -1);
            if (fields.length >= 2) {
                return new PageTreeKey(NumberUtils.parseLong(fields[0]), NumberUtils.parseLong(fields[1]));
            }
        }
        return null;
    }

    public record PageTreeKey(long rankIndex, long hcLevel) {
        public PageTreeKey {
            rankIndex = Math.max(0L, rankIndex);
            hcLevel = Math.max(0L, hcLevel);
        }
    }
}
