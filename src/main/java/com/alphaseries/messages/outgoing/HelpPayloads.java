package com.alphaseries.messages.outgoing;

import com.alphaseries.dao.mysql.HelpDao;
import com.alphaseries.game.help.HelpCenterCache;
import com.alphaseries.protocol.PacketBuilder;

import java.util.List;

public final class HelpPayloads {
    private HelpPayloads() {
    }

    public static String importantFaqs(HelpCenterCache cache) {
        PacketBuilder payload = PacketBuilder.message("HF");
        if (cache != null) {
            cache.appendImportantFaqPayloadTo(payload);
        }
        return payload.build();
    }

    public static String categories(HelpCenterCache cache) {
        PacketBuilder payload = PacketBuilder.message("HG");
        if (cache != null) {
            cache.appendCategoryPayloadTo(payload);
        }
        return payload.build();
    }

    public static String categoryFaqs(HelpCenterCache cache, long categoryId) {
        PacketBuilder payload = PacketBuilder.message("HJ")
            .appendInt(categoryId)
            .appendString("");
        if (cache != null) {
            cache.appendCategoryFaqPayloadTo(payload, categoryId);
        }
        return payload.build();
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
        PacketBuilder payload = PacketBuilder.message("HH");
        if (cache != null) {
            cache.appendDescriptionPayloadTo(payload, faqId);
        }
        return payload.build();
    }
}
