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

    public static CatalogPages fromPayloads(Iterable<PagePayload> pagePayloads, Iterable<PageTreePayload> pageTrees) {
        return new CatalogPages(pagePayloadMap(pagePayloads), pageTreeMap(pageTrees));
    }

    public CatalogPages withPagePayloads(Iterable<PagePayload> pagePayloads) {
        return new CatalogPages(pagePayloadMap(pagePayloads), pageTrees);
    }

    public CatalogPages withPageTrees(Iterable<PageTreePayload> pageTrees) {
        return new CatalogPages(pagePayloads, pageTreeMap(pageTrees));
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

    private static Map<Long, String> pagePayloadMap(Iterable<PagePayload> payloads) {
        Map<Long, String> payloadMap = new LinkedHashMap<>();
        if (payloads != null) {
            for (PagePayload payload : payloads) {
                if (payload != null) {
                    payloadMap.put(payload.pageId(), payload.payload());
                }
            }
        }
        return payloadMap;
    }

    private static Map<PageTreeKey, String> pageTreeMap(Iterable<PageTreePayload> payloads) {
        Map<PageTreeKey, String> payloadMap = new LinkedHashMap<>();
        if (payloads != null) {
            for (PageTreePayload payload : payloads) {
                if (payload != null) {
                    payloadMap.put(new PageTreeKey(payload.rankIndex(), payload.hcLevel()), payload.payload());
                }
            }
        }
        return payloadMap;
    }

    public record PagePayload(long pageId, String payload) {
        public PagePayload {
            pageId = Math.max(0L, pageId);
            payload = StringUtils.text(payload);
        }
    }

    public record PageTreePayload(long rankIndex, long hcLevel, String payload) {
        public PageTreePayload {
            rankIndex = Math.max(0L, rankIndex);
            hcLevel = Math.max(0L, hcLevel);
            payload = StringUtils.text(payload);
        }
    }

    public record PageTreeKey(long rankIndex, long hcLevel) {
        public PageTreeKey {
            rankIndex = Math.max(0L, rankIndex);
            hcLevel = Math.max(0L, hcLevel);
        }
    }
}
