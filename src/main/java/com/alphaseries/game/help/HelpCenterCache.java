package com.alphaseries.game.help;

import java.util.LinkedHashMap;
import java.util.Map;

import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.util.StringUtils;

public final class HelpCenterCache {
    private final ImportantFaqState importantFaqState;
    private final CategoryState categoryState;
    private final Map<Long, String> categoryFaqPayloads;
    private final Map<Long, String> descriptionPayloads;

    private HelpCenterCache(ImportantFaqState importantFaqState, CategoryState categoryState,
                            Map<Long, String> categoryFaqPayloads, Map<Long, String> descriptionPayloads) {
        this.importantFaqState = importantFaqState == null ? ImportantFaqState.empty() : importantFaqState;
        this.categoryState = categoryState == null ? CategoryState.empty() : categoryState;
        this.categoryFaqPayloads = copyPayloads(categoryFaqPayloads);
        this.descriptionPayloads = copyPayloads(descriptionPayloads);
    }

    public static HelpCenterCache empty() {
        return new HelpCenterCache(ImportantFaqState.empty(), CategoryState.empty(), Map.of(), Map.of());
    }

    public static HelpCenterCache fromPayloads(ImportantFaqState importantFaqState, CategoryState categoryState,
                                               Iterable<CategoryFaqPayload> categoryFaqPayloads,
                                               Iterable<DescriptionPayload> descriptionPayloads) {
        return new HelpCenterCache(importantFaqState, categoryState,
            categoryFaqPayloadMap(categoryFaqPayloads), descriptionPayloadMap(descriptionPayloads));
    }

    public HelpCenterCache withImportantFaqState(ImportantFaqState state) {
        return new HelpCenterCache(state, categoryState, categoryFaqPayloads, descriptionPayloads);
    }

    public HelpCenterCache withFaqCategoryState(CategoryState state, Iterable<CategoryFaqPayload> categoryFaqPayloads) {
        return new HelpCenterCache(importantFaqState, state, categoryFaqPayloadMap(categoryFaqPayloads),
            descriptionPayloads);
    }

    public HelpCenterCache withDescriptionPayloads(Iterable<DescriptionPayload> descriptionPayloads) {
        return new HelpCenterCache(importantFaqState, categoryState, categoryFaqPayloads,
            descriptionPayloadMap(descriptionPayloads));
    }

    public void appendImportantFaqPayloadTo(PacketBuilder payload) {
        if (payload != null) {
            payload.appendRaw(importantFaqState.payload());
        }
    }

    public void appendCategoryPayloadTo(PacketBuilder payload) {
        if (payload != null) {
            payload.appendRaw(categoryState.payload());
        }
    }

    public void appendCategoryFaqPayloadTo(PacketBuilder payload, long categoryId) {
        if (payload != null) {
            payload.appendRaw(categoryFaqPayload(categoryId));
        }
    }

    public void appendDescriptionPayloadTo(PacketBuilder payload, long faqId) {
        if (payload != null) {
            payload.appendRaw(descriptionPayload(faqId));
        }
    }

    private String categoryFaqPayload(long categoryId) {
        return payload(categoryFaqPayloads, categoryId);
    }

    private String descriptionPayload(long faqId) {
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
                    payloadMap.put(payload.categoryId(), payload.payloadText());
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
                    payloadMap.put(payload.faqId(), payload.payloadText());
                }
            }
        }
        return payloadMap;
    }

    public static final class CategoryFaqPayload {
        private final long categoryId;
        private final String payload;

        private CategoryFaqPayload(long categoryId, String payload) {
            this.categoryId = Math.max(0L, categoryId);
            this.payload = StringUtils.text(payload);
        }

        public static CategoryFaqPayload fromFaqEntries(long categoryId, Iterable<FaqEntry> faqs) {
            long count = 0L;
            PacketBuilder payload = PacketBuilder.create();
            if (faqs != null) {
                for (FaqEntry faq : faqs) {
                    if (faq != null) {
                        payload.appendInt(faq.faqId()).appendString(faq.name());
                        count++;
                    }
                }
            }
            return fromPayloadText(categoryId, PacketBuilder.create().appendInt(count).appendRaw(payload.build()).build());
        }

        static CategoryFaqPayload fromPayloadText(long categoryId, String payload) {
            return new CategoryFaqPayload(categoryId, payload);
        }

        public long categoryId() {
            return categoryId;
        }

        String payloadText() {
            return payload;
        }
    }

    public record FaqEntry(long faqId, String name) {
        public FaqEntry {
            faqId = Math.max(0L, faqId);
            name = StringUtils.text(name);
        }
    }

    public static final class DescriptionPayload {
        private final long faqId;
        private final String payload;

        private DescriptionPayload(long faqId, String payload) {
            this.faqId = Math.max(0L, faqId);
            this.payload = StringUtils.text(payload);
        }

        public static DescriptionPayload fromDescription(long faqId, String description) {
            String descriptionText = StringUtils.newlinesAsCarriageReturns(description);
            return fromPayloadText(faqId, PacketBuilder.create()
                .appendInt(faqId)
                .appendString(descriptionText)
                .build());
        }

        static DescriptionPayload fromPayloadText(long faqId, String payload) {
            return new DescriptionPayload(faqId, payload);
        }

        public long faqId() {
            return faqId;
        }

        String payloadText() {
            return payload;
        }
    }

    public static final class ImportantFaqState {
        private final String payload;

        private ImportantFaqState(String payload) {
            this.payload = StringUtils.text(payload);
        }

        public static ImportantFaqState empty() {
            return new ImportantFaqState("");
        }

        static ImportantFaqState fromPayload(String payload) {
            return new ImportantFaqState(payload);
        }

        String payload() {
            return payload;
        }
    }

    public static final class CategoryState {
        private final String payload;

        private CategoryState(String payload) {
            this.payload = StringUtils.text(payload);
        }

        public static CategoryState empty() {
            return new CategoryState("");
        }

        static CategoryState fromPayload(String payload) {
            return new CategoryState(payload);
        }

        String payload() {
            return payload;
        }
    }
}
