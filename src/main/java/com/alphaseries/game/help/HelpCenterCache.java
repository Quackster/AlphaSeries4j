package com.alphaseries.game.help;

import com.alphaseries.util.StringUtils;

public final class HelpCenterCache {
    private final String importantFaqPayload;
    private final String categoryPayload;
    private final Object categoryFaqs;
    private final Object descriptions;

    private HelpCenterCache(String importantFaqPayload, String categoryPayload, Object categoryFaqs, Object descriptions) {
        this.importantFaqPayload = StringUtils.text(importantFaqPayload);
        this.categoryPayload = StringUtils.text(categoryPayload);
        this.categoryFaqs = categoryFaqs;
        this.descriptions = descriptions;
    }

    public static HelpCenterCache fromLegacy(String importantFaqPayload, String categoryPayload, Object categoryFaqs, Object descriptions) {
        return new HelpCenterCache(importantFaqPayload, categoryPayload, categoryFaqs, descriptions);
    }

    public static HelpCenterCache empty() {
        return new HelpCenterCache("", "", "", "");
    }

    public String importantFaqPayload() {
        return importantFaqPayload;
    }

    public String categoryPayload() {
        return categoryPayload;
    }

    public String categoryFaqPayload(long categoryId) {
        return indexedPayload(categoryFaqs, categoryId);
    }

    public String descriptionPayload(long faqId) {
        return indexedPayload(descriptions, faqId);
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
