package com.alphaseries.messages.outgoing;

import com.alphaseries.dao.mysql.HelpDao;
import com.alphaseries.game.help.HelpCenterCache;
import com.alphaseries.protocol.PacketBuilder;

import java.util.List;

public final class HelpPayloads {
    private HelpPayloads() {
    }

    public static String importantFaqs(HelpCenterCache cache) {
        return importantFaqs(cache == null ? "" : cache.importantFaqPayload());
    }

    private static String importantFaqs(String payload) {
        return PacketBuilder.message("HF")
            .appendRaw(payload)
            .build();
    }

    public static String categories(HelpCenterCache cache) {
        return categories(cache == null ? "" : cache.categoryPayload());
    }

    private static String categories(String payload) {
        return PacketBuilder.message("HG")
            .appendRaw(payload)
            .build();
    }

    public static String categoryFaqs(HelpCenterCache cache, long categoryId) {
        return categoryFaqs(categoryId, cache == null ? "" : cache.categoryFaqPayload(categoryId));
    }

    private static String categoryFaqs(long categoryId, String payload) {
        return PacketBuilder.message("HJ")
            .appendInt(categoryId)
            .appendString("")
            .appendRaw(payload)
            .build();
    }

    public static String searchResults(List<HelpDao.FaqNameRow> rows) {
        long resultCount = 0L;
        PacketBuilder resultPayload = PacketBuilder.create();
        for (HelpDao.FaqNameRow row : rows == null ? List.<HelpDao.FaqNameRow>of() : rows) {
            if (row != null) {
                resultPayload.appendInt(row.id()).appendString(row.name());
                resultCount++;
            }
        }
        return PacketBuilder.message("HI")
            .appendInt(resultCount)
            .appendRaw(resultPayload)
            .build();
    }

    public static String description(HelpCenterCache cache, long faqId) {
        return description(cache == null ? "" : cache.descriptionPayload(faqId));
    }

    private static String description(String payload) {
        return PacketBuilder.message("HH")
            .appendRaw(payload)
            .build();
    }
}
