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
                                               Map<Long, String> categoryFaqPayloads,
                                               Map<Long, String> descriptionPayloads) {
        return new HelpCenterCache(importantFaqPayload, categoryPayload, categoryFaqPayloads, descriptionPayloads);
    }

    public String importantFaqPayload() {
        return importantFaqPayload;
    }

    public String categoryPayload() {
        return categoryPayload;
    }

    public Map<Long, String> categoryFaqPayloads() {
        return Map.copyOf(categoryFaqPayloads);
    }

    public String categoryFaqPayload(long categoryId) {
        return payload(categoryFaqPayloads, categoryId);
    }

    public Map<Long, String> descriptionPayloads() {
        return Map.copyOf(descriptionPayloads);
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
}
