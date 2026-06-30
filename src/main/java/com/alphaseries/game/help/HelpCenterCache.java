package com.alphaseries.game.help;

import java.util.LinkedHashMap;
import java.util.Map;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

public final class HelpCenterCache {
    private final String importantFaqPayload;
    private final String categoryPayload;
    private final Map<Long, String> categoryFaqPayloads;
    private final Map<Long, String> descriptionPayloads;

    private HelpCenterCache(String importantFaqPayload, String categoryPayload, Object categoryFaqs, Object descriptions) {
        this.importantFaqPayload = StringUtils.text(importantFaqPayload);
        this.categoryPayload = StringUtils.text(categoryPayload);
        this.categoryFaqPayloads = parsePayloads(categoryFaqs);
        this.descriptionPayloads = parsePayloads(descriptions);
    }

    private HelpCenterCache(String importantFaqPayload, String categoryPayload,
                            Map<Long, String> categoryFaqPayloads, Map<Long, String> descriptionPayloads) {
        this.importantFaqPayload = StringUtils.text(importantFaqPayload);
        this.categoryPayload = StringUtils.text(categoryPayload);
        this.categoryFaqPayloads = copyPayloads(categoryFaqPayloads);
        this.descriptionPayloads = copyPayloads(descriptionPayloads);
    }

    public static HelpCenterCache fromLegacy(String importantFaqPayload, String categoryPayload, Object categoryFaqs, Object descriptions) {
        return new HelpCenterCache(importantFaqPayload, categoryPayload, categoryFaqs, descriptions);
    }

    public static HelpCenterCache empty() {
        return new HelpCenterCache("", "", "", "");
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
