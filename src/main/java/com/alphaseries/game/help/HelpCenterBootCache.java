package com.alphaseries.game.help;

import com.alphaseries.dao.mysql.HelpDao;
import com.alphaseries.db.Database;
import com.alphaseries.db.MySQL;
import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class HelpCenterBootCache {
    private HelpCenterBootCache() {
    }

    public record FaqCategoryCache(String categoryPayload, Map<Long, String> faqPayloadByCategoryId) {
        public FaqCategoryCache {
            categoryPayload = StringUtils.text(categoryPayload);
            faqPayloadByCategoryId = faqPayloadByCategoryId == null
                ? Map.of() : Map.copyOf(faqPayloadByCategoryId);
        }
    }

    public static void loadImportantFaqCache() {
        Map<Long, List<HelpDao.FaqNameRow>> rows = new LinkedHashMap<Long, List<HelpDao.FaqNameRow>>();
        HelpDao help = helpDao();
        if (help != null) {
            try {
                rows.put(1L, help.importantFaqRows(1L));
                rows.put(2L, help.importantFaqRows(2L));
            } catch (Exception ignored) {
                // Legacy startup cache loading tolerated missing tables or SQL failures.
            }
        }
        mergeImportantFaqPayload(buildImportantFaqPayloadFromRows(rows));
    }

    public static void loadFaqCategoryCache() {
        HelpDao help = helpDao();
        List<HelpDao.FaqNameRow> categoryRows = List.of();
        Map<Long, List<HelpDao.FaqNameRow>> faqRows = new LinkedHashMap<Long, List<HelpDao.FaqNameRow>>();
        if (help != null) {
            try {
                List<HelpDao.FaqNameRow> categories = help.categoryRows();
                categoryRows = categories;
                for (HelpDao.FaqNameRow category : categories) {
                    long categoryId = category.id();
                    if (categoryId >= 0L) {
                        faqRows.put(categoryId, help.faqRowsByCategory(categoryId));
                    }
                }
            } catch (Exception ignored) {
                // Legacy startup cache loading tolerated missing tables or SQL failures.
            }
        }
        FaqCategoryCache cache = buildFaqCategoryCacheFromRows(categoryRows, faqRows);
        mergeFaqCategoryCache(cache.categoryPayload(), cache.faqPayloadByCategoryId());
    }

    public static void loadFaqDescriptionCache() {
        HelpDao help = helpDao();
        Map<Long, String> cache = new LinkedHashMap<Long, String>();
        if (help != null) {
            try {
                cache = buildFaqDescriptionCache(help.descriptionRows());
            } catch (Exception ignored) {
                // Legacy startup cache loading tolerated missing tables or SQL failures.
            }
        }
        mergeFaqDescriptionCache(cache);
    }

    private static void mergeImportantFaqPayload(String payload) {
        HelpCenterCache current = HelpCenterState.instance().cache();
        HelpCenterState.instance().setCache(HelpCenterCache.fromPayloads(
            payload,
            current.categoryPayload(),
            current.categoryFaqPayloads(),
            current.descriptionPayloads()));
    }

    private static void mergeFaqCategoryCache(String categoryPayload, Map<Long, String> categoryFaqs) {
        HelpCenterCache current = HelpCenterState.instance().cache();
        HelpCenterState.instance().setCache(HelpCenterCache.fromPayloads(
            current.importantFaqPayload(),
            categoryPayload,
            categoryFaqs,
            current.descriptionPayloads()));
    }

    private static void mergeFaqDescriptionCache(Map<Long, String> descriptions) {
        HelpCenterCache current = HelpCenterState.instance().cache();
        HelpCenterState.instance().setCache(HelpCenterCache.fromPayloads(
            current.importantFaqPayload(),
            current.categoryPayload(),
            current.categoryFaqPayloads(),
            descriptions));
    }

    public static String buildImportantFaqPayloadFromRows(Map<Long, List<HelpDao.FaqNameRow>> faqRowsByImportance) {
        PacketBuilder payload = PacketBuilder.create();
        for (long importanceLevel = 1L; importanceLevel <= 2L; importanceLevel++) {
            List<HelpDao.FaqNameRow> rows = faqRowsByImportance == null ? List.of() : faqRowsByImportance.get(importanceLevel);
            payload
                .appendInt(countFaqNameRows(rows))
                .appendRaw(buildFaqNamePayloadFromRows(rows));
        }
        return PacketBuilder.create().appendInt(2).appendRaw(payload.build()).build();
    }

    public static FaqCategoryCache buildFaqCategoryCacheFromRows(List<HelpDao.FaqNameRow> categoryRows,
            Map<Long, List<HelpDao.FaqNameRow>> faqRowsByCategoryId) {
        long categoryCount = 0L;
        PacketBuilder categoryPayload = PacketBuilder.create();
        Map<Long, String> faqPayloadByCategoryId = new LinkedHashMap<Long, String>();
        if (categoryRows != null) {
            for (HelpDao.FaqNameRow category : categoryRows) {
                if (category != null) {
                    long categoryId = category.id();
                    List<HelpDao.FaqNameRow> faqRows = faqRowsByCategoryId == null ? List.of() : faqRowsByCategoryId.get(categoryId);
                    faqPayloadByCategoryId.put(categoryId,
                        PacketBuilder.create()
                            .appendInt(countFaqNameRows(faqRows))
                            .appendRaw(buildFaqNamePayloadFromRows(faqRows))
                            .build());
                    categoryPayload
                        .appendInt(categoryId)
                        .appendString(category.name());
                    categoryCount++;
                }
            }
        }
        return new FaqCategoryCache(
            PacketBuilder.create().appendInt(categoryCount).appendRaw(categoryPayload.build()).build(),
            faqPayloadByCategoryId);
    }

    public static Map<Long, String> buildFaqDescriptionCache(List<HelpDao.FaqDescriptionRow> faqRows) {
        Map<Long, String> cache = new LinkedHashMap<Long, String>();
        if (faqRows != null) {
            for (HelpDao.FaqDescriptionRow row : faqRows) {
                if (row != null) {
                    long faqId = row.id();
                    String descriptionText = StringUtils.text(row.description()).replace('\n', '\r');
                    cache.put(faqId, PacketBuilder.create()
                        .appendInt(faqId)
                        .appendString(descriptionText)
                        .build());
                }
            }
        }
        return cache;
    }

    private static String buildFaqNamePayloadFromRows(List<HelpDao.FaqNameRow> faqRows) {
        PacketBuilder payload = PacketBuilder.create();
        if (faqRows != null) {
            for (HelpDao.FaqNameRow row : faqRows) {
                if (row != null) {
                    payload
                        .appendInt(row.id())
                        .appendString(row.name());
                }
            }
        }
        return payload.build();
    }

    private static long countFaqNameRows(List<HelpDao.FaqNameRow> rows) {
        long count = 0L;
        if (rows != null) {
            for (HelpDao.FaqNameRow row : rows) {
                if (row != null) {
                    count++;
                }
            }
        }
        return count;
    }

    private static HelpDao helpDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new HelpDao(database);
    }
}
