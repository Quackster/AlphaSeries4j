package com.alphaseries.game.navigator;

import com.alphaseries.dao.mysql.RoomDao;
import com.alphaseries.util.StringUtils;

import java.util.List;

public final class RoomCategoryCache {
    private final Object defaultCategoryIds;
    private final Object categoryRows;
    private final Object payloads;

    private RoomCategoryCache(Object defaultCategoryIds, Object categoryRows, Object payloads) {
        this.defaultCategoryIds = defaultCategoryIds == null ? "" : defaultCategoryIds;
        this.categoryRows = categoryRows == null ? "" : categoryRows;
        this.payloads = payloads == null ? "" : payloads;
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

    public Object defaultCategoryIds() {
        return defaultCategoryIds;
    }

    public String categoryRows() {
        if (categoryRows instanceof List<?> rows) {
            StringBuilder joined = new StringBuilder();
            for (Object value : rows) {
                if (value instanceof RoomDao.RoomCategoryRow row) {
                    appendRow(joined, row.categoryId() + "\t" + StringUtils.text(row.name()) + "\t"
                        + row.trading() + "\t" + row.minimumRank() + "\t" + row.minimumHcRank());
                }
            }
            return joined.toString();
        }
        return StringUtils.text(categoryRows);
    }

    public List<RoomDao.RoomCategoryRow> categoryRowList() {
        if (categoryRows instanceof List<?> rows) {
            return rows.stream()
                .filter(RoomDao.RoomCategoryRow.class::isInstance)
                .map(RoomDao.RoomCategoryRow.class::cast)
                .toList();
        }
        return List.of();
    }

    public String payload(long rankIndex, long hcLevel) {
        int rank = (int) rankIndex;
        int hc = (int) hcLevel;
        if (rank < 0 || hc < 0) {
            return "";
        }
        if (payloads instanceof String[][] values) {
            return rank < values.length && values[rank] != null && hc < values[rank].length
                ? StringUtils.text(values[rank][hc]) : "";
        }
        if (payloads instanceof Object[][] values) {
            return rank < values.length && values[rank] != null && hc < values[rank].length
                ? StringUtils.text(values[rank][hc]) : "";
        }
        return "";
    }

    private static void appendRow(StringBuilder rows, String rowText) {
        if (rows.length() > 0) {
            rows.append('\r');
        }
        rows.append(rowText);
    }
}
