package com.alphaseries.game.navigator;

import com.alphaseries.dao.mysql.RoomDao;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class RoomCategoryCache {
    private final Object defaultCategoryIds;
    private final String legacyCategoryRows;
    private final List<RoomDao.RoomCategoryRow> categoryRows;
    private final String[][] payloads;

    private RoomCategoryCache(Object defaultCategoryIds, Object categoryRows, Object payloads) {
        this.defaultCategoryIds = defaultCategoryIds == null ? "" : defaultCategoryIds;
        this.legacyCategoryRows = categoryRows instanceof List<?> ? "" : StringUtils.text(categoryRows);
        this.categoryRows = parseCategoryRows(categoryRows);
        this.payloads = parsePayloads(payloads);
    }

    private RoomCategoryCache(Object defaultCategoryIds, List<RoomDao.RoomCategoryRow> categoryRows, String[][] payloads) {
        this.defaultCategoryIds = defaultCategoryIds == null ? "" : defaultCategoryIds;
        this.legacyCategoryRows = "";
        this.categoryRows = copyCategoryRows(categoryRows);
        this.payloads = copyPayloads(payloads);
    }

    public static RoomCategoryCache fromLegacy(Object defaultCategoryIds, Object categoryRows, Object payloads) {
        if (defaultCategoryIds instanceof RoomCategoryCache roomCategoryCache) {
            return roomCategoryCache;
        }
        return new RoomCategoryCache(defaultCategoryIds, categoryRows, payloads);
    }

    public static RoomCategoryCache empty() {
        return new RoomCategoryCache("", "", "");
    }

    public static RoomCategoryCache fromRows(Object defaultCategoryIds, List<RoomDao.RoomCategoryRow> categoryRows,
                                             String[][] payloads) {
        return new RoomCategoryCache(defaultCategoryIds, categoryRows, payloads);
    }

    public Object defaultCategoryIds() {
        return defaultCategoryIds;
    }

    public String categoryRows() {
        if (!categoryRows.isEmpty()) {
            StringBuilder joined = new StringBuilder();
            for (RoomDao.RoomCategoryRow row : categoryRows) {
                appendRow(joined, row.categoryId() + "\t" + StringUtils.text(row.name()) + "\t"
                    + row.trading() + "\t" + row.minimumRank() + "\t" + row.minimumHcRank());
            }
            return joined.toString();
        }
        return legacyCategoryRows;
    }

    public List<RoomDao.RoomCategoryRow> categoryRowList() {
        return List.copyOf(categoryRows);
    }

    public String payload(long rankIndex, long hcLevel) {
        int rank = (int) rankIndex;
        int hc = (int) hcLevel;
        if (rank < 0 || hc < 0) {
            return "";
        }
        return rank < payloads.length && payloads[rank] != null && hc < payloads[rank].length
            ? StringUtils.text(payloads[rank][hc]) : "";
    }

    private static List<RoomDao.RoomCategoryRow> parseCategoryRows(Object categoryRows) {
        if (categoryRows instanceof List<?> rows) {
            List<RoomDao.RoomCategoryRow> parsedRows = new ArrayList<>();
            for (Object value : rows) {
                if (value instanceof RoomDao.RoomCategoryRow row) {
                    parsedRows.add(row);
                }
            }
            return List.copyOf(parsedRows);
        }
        return List.of();
    }

    private static String[][] parsePayloads(Object payloads) {
        if (payloads instanceof String[][] values) {
            return copyPayloads(values);
        }
        if (payloads instanceof Object[][] values) {
            String[][] parsedPayloads = new String[values.length][];
            for (int rank = 0; rank < values.length; rank++) {
                if (values[rank] == null) {
                    continue;
                }
                parsedPayloads[rank] = new String[values[rank].length];
                for (int hc = 0; hc < values[rank].length; hc++) {
                    parsedPayloads[rank][hc] = StringUtils.text(values[rank][hc]);
                }
            }
            return parsedPayloads;
        }
        return new String[0][];
    }

    private static List<RoomDao.RoomCategoryRow> copyCategoryRows(List<RoomDao.RoomCategoryRow> rows) {
        return rows == null ? List.of() : List.copyOf(rows);
    }

    private static String[][] copyPayloads(String[][] payloads) {
        if (payloads == null) {
            return new String[0][];
        }
        String[][] copiedPayloads = new String[payloads.length][];
        for (int rank = 0; rank < payloads.length; rank++) {
            if (payloads[rank] == null) {
                continue;
            }
            copiedPayloads[rank] = new String[payloads[rank].length];
            for (int hc = 0; hc < payloads[rank].length; hc++) {
                copiedPayloads[rank][hc] = StringUtils.text(payloads[rank][hc]);
            }
        }
        return copiedPayloads;
    }

    private static void appendRow(StringBuilder rows, String rowText) {
        if (rows.length() > 0) {
            rows.append('\r');
        }
        rows.append(rowText);
    }
}
