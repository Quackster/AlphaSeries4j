package com.alphaseries.game.navigator;

import com.alphaseries.util.StringUtils;

public final class RecommendedRooms {
    private final Object payloads;
    private final long count;

    private RecommendedRooms(Object payloads, long count) {
        this.payloads = payloads == null ? "" : payloads;
        this.count = Math.max(0L, count);
    }

    public static RecommendedRooms fromLegacy(Object payloads, long count) {
        if (payloads instanceof RecommendedRooms recommendedRooms) {
            return recommendedRooms;
        }
        return new RecommendedRooms(payloads, count);
    }

    public static RecommendedRooms empty() {
        return new RecommendedRooms("", 0L);
    }

    public long count() {
        return count;
    }

    public String payload(long oneBasedTreeIndex) {
        long normalizedIndex = oneBasedTreeIndex > 0L ? oneBasedTreeIndex - 1L : oneBasedTreeIndex;
        if (payloads instanceof String[] rows && normalizedIndex >= 0L && normalizedIndex < rows.length) {
            return StringUtils.text(rows[(int) normalizedIndex]);
        }
        return "";
    }
}
