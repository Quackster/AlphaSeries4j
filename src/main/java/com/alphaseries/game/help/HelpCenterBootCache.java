package com.alphaseries.game.help;

import com.alphaseries.dao.mysql.HelpDao;
import com.alphaseries.db.Database;
import com.alphaseries.db.MySQL;
import com.alphaseries.protocol.PacketBuilder;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public final class HelpCenterBootCache {
    private HelpCenterBootCache() {
    }

    public record FaqCategoryCache(HelpCenterCache.CategoryState categoryState,
                                   List<HelpCenterCache.CategoryFaqPayload> faqPayloads) {
        public FaqCategoryCache {
            categoryState = categoryState == null ? HelpCenterCache.CategoryState.empty() : categoryState;
            faqPayloads = faqPayloads == null ? List.of() : List.copyOf(faqPayloads);
        }

        String faqPayload(long categoryId) {
            if (faqPayloads != null) {
                for (HelpCenterCache.CategoryFaqPayload payload : faqPayloads) {
                    if (payload != null && payload.categoryId() == categoryId) {
                        return payload.payloadText();
                    }
                }
            }
            return "";
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
        mergeImportantFaqPayload(buildImportantFaqStateFromRows(rows));
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
        mergeFaqCategoryCache(cache.categoryState(), cache.faqPayloads());
    }

    public static void loadFaqDescriptionCache() {
        HelpDao help = helpDao();
        List<HelpCenterCache.DescriptionPayload> cache = List.of();
        if (help != null) {
            try {
                cache = buildFaqDescriptionCache(help.descriptionRows());
            } catch (Exception ignored) {
                // Legacy startup cache loading tolerated missing tables or SQL failures.
            }
        }
        mergeFaqDescriptionCache(cache);
    }

    private static void mergeImportantFaqPayload(HelpCenterCache.ImportantFaqState state) {
        HelpCenterCache current = HelpCenterState.instance().cache();
        HelpCenterState.instance().setCache(current.withImportantFaqState(state));
    }

    private static void mergeFaqCategoryCache(HelpCenterCache.CategoryState categoryState,
                                              Iterable<HelpCenterCache.CategoryFaqPayload> categoryFaqs) {
        HelpCenterCache current = HelpCenterState.instance().cache();
        HelpCenterState.instance().setCache(current.withFaqCategoryState(categoryState, categoryFaqs));
    }

    private static void mergeFaqDescriptionCache(Iterable<HelpCenterCache.DescriptionPayload> descriptions) {
        HelpCenterCache current = HelpCenterState.instance().cache();
        HelpCenterState.instance().setCache(current.withDescriptionPayloads(descriptions));
    }

    public static HelpCenterCache.ImportantFaqState buildImportantFaqStateFromRows(
            Map<Long, List<HelpDao.FaqNameRow>> faqRowsByImportance) {
        PacketBuilder payload = PacketBuilder.create();
        for (long importanceLevel = 1L; importanceLevel <= 2L; importanceLevel++) {
            List<HelpDao.FaqNameRow> rows = faqRowsByImportance == null ? List.of() : faqRowsByImportance.get(importanceLevel);
            payload
                .appendInt(countFaqNameRows(rows))
                .appendRaw(buildFaqNamePayloadFromRows(rows));
        }
        return HelpCenterCache.ImportantFaqState.fromPayload(
            PacketBuilder.create().appendInt(2).appendRaw(payload.build()).build());
    }

    public static FaqCategoryCache buildFaqCategoryCacheFromRows(List<HelpDao.FaqNameRow> categoryRows,
            Map<Long, List<HelpDao.FaqNameRow>> faqRowsByCategoryId) {
        long categoryCount = 0L;
        PacketBuilder categoryPayload = PacketBuilder.create();
        List<HelpCenterCache.CategoryFaqPayload> faqPayloads = new ArrayList<>();
        if (categoryRows != null) {
            for (HelpDao.FaqNameRow category : categoryRows) {
                if (category != null) {
                    long categoryId = category.id();
                    List<HelpDao.FaqNameRow> faqRows = faqRowsByCategoryId == null ? List.of() : faqRowsByCategoryId.get(categoryId);
                    faqPayloads.add(HelpCenterCache.CategoryFaqPayload.fromFaqEntries(
                        categoryId, faqEntries(faqRows)));
                    categoryPayload
                        .appendInt(categoryId)
                        .appendString(category.name());
                    categoryCount++;
                }
            }
        }
        return new FaqCategoryCache(
            HelpCenterCache.CategoryState.fromPayload(
                PacketBuilder.create().appendInt(categoryCount).appendRaw(categoryPayload.build()).build()),
            faqPayloads);
    }

    public static List<HelpCenterCache.DescriptionPayload> buildFaqDescriptionCache(List<HelpDao.FaqDescriptionRow> faqRows) {
        List<HelpCenterCache.DescriptionPayload> cache = new ArrayList<>();
        if (faqRows != null) {
            for (HelpDao.FaqDescriptionRow row : faqRows) {
                if (row != null) {
                    long faqId = row.id();
                    cache.add(HelpCenterCache.DescriptionPayload.fromDescription(faqId, row.description()));
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

    private static List<HelpCenterCache.FaqEntry> faqEntries(List<HelpDao.FaqNameRow> faqRows) {
        List<HelpCenterCache.FaqEntry> entries = new ArrayList<>();
        if (faqRows != null) {
            for (HelpDao.FaqNameRow row : faqRows) {
                if (row != null) {
                    entries.add(new HelpCenterCache.FaqEntry(row.id(), row.name()));
                }
            }
        }
        return entries;
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
