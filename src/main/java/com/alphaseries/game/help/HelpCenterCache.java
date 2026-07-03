package com.alphaseries.game.help;

import java.util.LinkedHashMap;
import java.util.Map;

import com.alphaseries.util.StringUtils;

public final class HelpCenterCache {
    private final String importantFaqPayload;
    private final String categoryPayload;
    private final Map<Long, String> categoryFaqPayloads;
    private final Map<Long, String> descriptionPayloads;

    private HelpCenterCache(String importantFaqPayload, String categoryPayload,
                            Map<Long, String> categoryFaqPayloads, Map<Long, String> descriptionPayloads) {
        this.importantFaqPayload = StringUtils.text(importantFaqPayload);
        this.categoryPayload = StringUtils.text(categoryPayload);
        this.categoryFaqPayloads = copyPayloads(categoryFaqPayloads);
        this.descriptionPayloads = copyPayloads(descriptionPayloads);
    }

    public static HelpCenterCache empty() {
        return new HelpCenterCache("", "", Map.of(), Map.of());
    }

    public static HelpCenterCache fromPayloads(String importantFaqPayload, String categoryPayload,
                                               Iterable<CategoryFaqPayload> categoryFaqPayloads,
                                               Iterable<DescriptionPayload> descriptionPayloads) {
        return new HelpCenterCache(importantFaqPayload, categoryPayload,
            categoryFaqPayloadMap(categoryFaqPayloads), descriptionPayloadMap(descriptionPayloads));
    }

    public HelpCenterCache withImportantFaqPayload(String payload) {
        return new HelpCenterCache(payload, categoryPayload, categoryFaqPayloads, descriptionPayloads);
    }

    public HelpCenterCache withFaqCategoryPayloads(String categoryPayload, Iterable<CategoryFaqPayload> categoryFaqPayloads) {
        return new HelpCenterCache(importantFaqPayload, categoryPayload, categoryFaqPayloadMap(categoryFaqPayloads),
            descriptionPayloads);
    }

    public HelpCenterCache withDescriptionPayloads(Iterable<DescriptionPayload> descriptionPayloads) {
        return new HelpCenterCache(importantFaqPayload, categoryPayload, categoryFaqPayloads,
            descriptionPayloadMap(descriptionPayloads));
    }

    public String importantFaqPayload() {
        return importantFaqPayload;
    }

    public String categoryPayload() {
        return categoryPayload;
    }

    public String categoryFaqPayload(long categoryId) {
        return payload(categoryFaqPayloads, categoryId);
    }

    public String descriptionPayload(long faqId) {
        return payload(descriptionPayloads, faqId);
    }

    private static String payload(Map<Long, String> payloads, long index) {
        if (index < 0L) {
            return "";
        }
        return StringUtils.text(payloads.get(index));
    }

    private static Map<Long, String> copyPayloads(Map<Long, String> payloads) {
        Map<Long, String> copiedPayloads = new LinkedHashMap<>();
        if (payloads != null) {
            for (Map.Entry<Long, String> entry : payloads.entrySet()) {
                if (entry.getKey() != null && entry.getKey() >= 0L) {
                    copiedPayloads.put(entry.getKey(), StringUtils.text(entry.getValue()));
                }
            }
        }
        return copiedPayloads;
    }

    private static Map<Long, String> categoryFaqPayloadMap(Iterable<CategoryFaqPayload> payloads) {
        Map<Long, String> payloadMap = new LinkedHashMap<>();
        if (payloads != null) {
            for (CategoryFaqPayload payload : payloads) {
                if (payload != null) {
                    payloadMap.put(payload.categoryId(), payload.payload());
                }
            }
        }
        return payloadMap;
    }

    private static Map<Long, String> descriptionPayloadMap(Iterable<DescriptionPayload> payloads) {
        Map<Long, String> payloadMap = new LinkedHashMap<>();
        if (payloads != null) {
            for (DescriptionPayload payload : payloads) {
                if (payload != null) {
                    payloadMap.put(payload.faqId(), payload.payload());
                }
            }
        }
        return payloadMap;
    }

    public record CategoryFaqPayload(long categoryId, String payload) {
        public CategoryFaqPayload {
            categoryId = Math.max(0L, categoryId);
            payload = StringUtils.text(payload);
        }
    }

    public record DescriptionPayload(long faqId, String payload) {
        public DescriptionPayload {
            faqId = Math.max(0L, faqId);
            payload = StringUtils.text(payload);
        }
    }
}
