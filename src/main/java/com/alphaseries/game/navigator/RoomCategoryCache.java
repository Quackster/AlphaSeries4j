package com.alphaseries.game.navigator;

import com.alphaseries.util.StringUtils;

public final class RoomCategoryCache {
    private final Object defaultCategoryIds;
    private final String categoryRows;
    private final Object payloads;

    private RoomCategoryCache(Object defaultCategoryIds, String categoryRows, Object payloads) {
        this.defaultCategoryIds = defaultCategoryIds == null ? "" : defaultCategoryIds;
        this.categoryRows = StringUtils.text(categoryRows);
        this.payloads = payloads == null ? "" : payloads;
    }

    public static RoomCategoryCache fromLegacy(Object defaultCategoryIds, String categoryRows, Object payloads) {
        return new RoomCategoryCache(defaultCategoryIds, categoryRows, payloads);
    }

    public Object defaultCategoryIds() {
        return defaultCategoryIds;
    }

    public String categoryRows() {
        return categoryRows;
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
}
