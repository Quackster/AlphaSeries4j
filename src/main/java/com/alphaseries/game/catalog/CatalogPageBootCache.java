package com.alphaseries.game.catalog;

import com.alphaseries.config.AppConfigState;
import com.alphaseries.dao.mysql.CatalogDao;
import com.alphaseries.db.Database;
import com.alphaseries.db.MySQL;
import com.alphaseries.protocol.PacketBuilder;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class CatalogPageBootCache {
    private CatalogPageBootCache() {
    }

    /**
     * Original function: Proc_1_15_6CA000.
     */
    public static void loadCatalogPagePayloadCache() {
        CatalogDao catalog = catalogDao();
        Map<Long, String> pages = new LinkedHashMap<>();
        if (catalog != null) {
            try {
                for (CatalogDao.CatalogPageRow row : catalog.catalogPageRows()) {
                    long pageId = row.pageId();
                    if (pageId >= 0L) {
                        pages.put(pageId, buildCatalogPagePayload(row, catalog.catalogPageProductRows(pageId)));
                    }
                }
            } catch (Exception ignored) {
                // Legacy startup cache loading tolerated missing tables or SQL failures.
            }
        }
        CatalogPages currentPages = CatalogState.instance().catalogPages();
        CatalogState.instance().setCatalogPages(CatalogPages.fromPayloadMaps(
            pages,
            currentPages.pageTrees()));
    }

    /**
     * Original function: Proc_1_17_6CCDC0.
     */
    public static void loadCatalogPageTreeCache() {
        Map<CatalogPages.PageTreeKey, String> trees = new LinkedHashMap<>();
        CatalogDao catalog = catalogDao();
        for (int rank = 0; rank <= 20; rank++) {
            for (int hc = 0; hc <= 2; hc++) {
                if (catalog != null) {
                    try {
                        List<CatalogDao.CatalogPageTreeRow> rootRows = catalog.catalogPageTreeRows(0L, rank, hc);
                        Map<Long, Long> childCounts = new LinkedHashMap<Long, Long>();
                        Map<Long, List<CatalogDao.CatalogPageTreeRow>> children = new LinkedHashMap<Long, List<CatalogDao.CatalogPageTreeRow>>();
                        for (CatalogDao.CatalogPageTreeRow row : rootRows) {
                            long pageId = row.pageId();
                            childCounts.put(pageId, catalog.catalogPageChildCount(pageId, rank, hc));
                            children.put(pageId, catalog.catalogPageTreeRows(pageId, rank, hc));
                        }
                        trees.put(new CatalogPages.PageTreeKey(rank, hc),
                            buildCatalogPageTreePayload(rootRows, childCounts, children, rank, hc));
                    } catch (Exception ignored) {
                        // Legacy startup cache loading tolerated missing tables or SQL failures.
                    }
                }
            }
        }
        CatalogPages currentPages = CatalogState.instance().catalogPages();
        CatalogState.instance().setCatalogPages(CatalogPages.fromPayloadMaps(
            currentPages.pagePayloads(),
            trees));
    }

    /**
     * Original function: Proc_1_15_6CA000.
     */
    public static String buildCatalogPagePayload(CatalogDao.CatalogPageRow page, List<CatalogDao.CatalogPageProductRow> productRows) {
        if (page == null) {
            return "";
        }
        PacketBuilder payload = PacketBuilder.create()
            .appendString(page.name())
            .appendInt(page.clickable())
            .appendString(page.template())
            .appendString(page.headerImage())
            .appendString(page.specialImage())
            .appendString(page.specialTemplate());

        PageTextPayload textPayload = PageTextFields.fromPage(page).payload();
        payload.appendInt(textPayload.textCount()).appendRaw(textPayload.payload());

        if (catalogTextFieldPresent(page.link())) {
            payload.appendInt(1).appendString(page.link());
        } else {
            payload.appendInt(0);
        }
        return payload.appendRaw(buildCatalogProductPayload(page.pageId(), productRows)).build();
    }

    private record PageTextFields(
        String textOne,
        String textTwo,
        String textThree,
        String textFour,
        String textFive,
        String textSix,
        String textSeven,
        String textEight,
        String textNine,
        String textTen,
        String textEleven
    ) {
        private static PageTextFields fromPage(CatalogDao.CatalogPageRow page) {
            return new PageTextFields(
                page.textOne(),
                page.textTwo(),
                page.textThree(),
                page.textFour(),
                page.textFive(),
                page.textSix(),
                page.textSeven(),
                page.textEight(),
                page.textNine(),
                page.textTen(),
                page.textEleven());
        }

        private PageTextPayload payload() {
            PageTextAccumulator accumulator = PageTextAccumulator.empty()
                .append(textOne)
                .append(textTwo)
                .append(textThree)
                .append(textFour)
                .append(textFive)
                .append(textSix)
                .append(textSeven)
                .append(textEight)
                .append(textNine)
                .append(textTen)
                .append(textEleven);
            return new PageTextPayload(accumulator.textCount(), accumulator.payload().build());
        }
    }

    private record PageTextAccumulator(long textCount, PacketBuilder payload) {
        private static PageTextAccumulator empty() {
            return new PageTextAccumulator(0L, PacketBuilder.create());
        }

        private PageTextAccumulator append(String textField) {
            if (!catalogTextFieldPresent(textField)) {
                return this;
            }
            payload.appendString(textField);
            return new PageTextAccumulator(textCount + 1L, payload);
        }
    }

    private record PageTextPayload(long textCount, String payload) {
    }

    /**
     * Original function: Proc_1_15_6CA000.
     */
    private static String buildCatalogProductPayload(long pageId, List<CatalogDao.CatalogPageProductRow> productRows) {
        long productCount = 0L;
        PacketBuilder productPayload = PacketBuilder.create();
        if (productRows != null) {
            for (CatalogDao.CatalogPageProductRow row : productRows) {
                if (row != null) {
                    productPayload.appendRaw(buildCatalogProductEntry(row));
                    productCount++;
                }
            }
        }
        return PacketBuilder.create().appendInt(productCount).appendRaw(productPayload.build()).build();
    }

    /**
     * Original function: Proc_1_15_6CA000.
     */
    private static String buildCatalogProductEntry(CatalogDao.CatalogPageProductRow row) {
        if (row == null) {
            return "";
        }
        CatalogRegistry.Product product = CatalogState.instance().registry().product(row.productId()).orElse(null);
        long productType = product == null ? 0L : product.type();
        String productClass = catalogProductClass(productType);
        long amountValue = row.amount();
        if (amountValue <= 0L) {
            amountValue = 1L;
        }

        return PacketBuilder.create()
            .appendInt(row.catalogProductId())
            .appendString(row.sprite())
            .appendInt(row.productId())
            .appendString(productClass)
            .appendInt(row.creditPrice())
            .appendInt(row.activityPointPrice())
            .appendInt(row.activityPointType())
            .appendInt(amountValue)
            .appendString(row.secondaryType())
            .appendInt(row.replaceDefaultSign())
            .appendInt(row.minimumHcRank())
            .build();
    }

    public static boolean catalogTextFieldPresent(String fieldValue) {
        return fieldValue != null && !fieldValue.isEmpty() && !"NULL".equalsIgnoreCase(fieldValue);
    }

    public static String catalogProductClass(long productType) {
        return productType == 9L ? "i" : "s";
    }

    /**
     * Original function: Proc_1_17_6CCDC0.
     */
    public static String buildCatalogPageTreePayload(List<CatalogDao.CatalogPageTreeRow> rootRows,
            Map<Long, Long> childCountByPageId, Map<Long, List<CatalogDao.CatalogPageTreeRow>> childRowsByParentId,
            long rankIndex, long hcLevel) {
        long rootCount = 0L;
        PacketBuilder payload = PacketBuilder.create();
        if (rootRows != null) {
            for (CatalogDao.CatalogPageTreeRow row : rootRows) {
                if (row != null && catalogPageVisible(row, rankIndex, hcLevel)) {
                    long pageId = row.pageId();
                    long childCount = mapLong(childCountByPageId, pageId);
                    payload
                        .appendRaw(buildCatalogPageTreeEntry(row, childCount))
                        .appendRaw(buildCatalogPageChildPayload(
                            childRowsByParentId == null ? null : childRowsByParentId.get(pageId),
                            rankIndex,
                            hcLevel));
                    rootCount++;
                }
            }
        }
        return PacketBuilder.create().appendInt(rootCount).appendRaw(payload.build()).build();
    }

    /**
     * Original function: Proc_1_17_6CCDC0.
     */
    private static String buildCatalogPageChildPayload(List<CatalogDao.CatalogPageTreeRow> childRows, long rankIndex, long hcLevel) {
        PacketBuilder payload = PacketBuilder.create();
        if (childRows != null) {
            for (CatalogDao.CatalogPageTreeRow row : childRows) {
                if (row != null && catalogPageVisible(row, rankIndex, hcLevel)) {
                    payload.appendRaw(buildCatalogPageTreeEntry(row, 0));
                }
            }
        }
        return payload.build();
    }

    /**
     * Original function: Proc_1_17_6CCDC0.
     */
    private static String buildCatalogPageTreeEntry(CatalogDao.CatalogPageTreeRow row, long childCount) {
        if (row == null) {
            return "";
        }
        return PacketBuilder.create()
            .appendRaw("0")
            .appendInt(row.pageId())
            .appendInt(row.color())
            .appendInt(row.icon())
            .appendInt(row.visible())
            .appendString(row.name())
            .appendInt(childCount)
            .build();
    }

    public static boolean catalogPageVisible(CatalogDao.CatalogPageTreeRow row, long rankIndex, long hcLevel) {
        if (row == null) {
            return false;
        }
        boolean visible = row.visible() != 0L;
        if (row.develop() != 0L) {
            visible = AppConfigState.instance().permissionMatrix().allows(rankIndex, "", "fuse_developer", hcLevel);
        }
        return visible;
    }

    private static long mapLong(Map<Long, Long> valuesById, long id) {
        if (valuesById == null) {
            return 0L;
        }
        Long value = valuesById.get(id);
        return value == null ? 0L : value.longValue();
    }

    private static CatalogDao catalogDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new CatalogDao(database);
    }
}
