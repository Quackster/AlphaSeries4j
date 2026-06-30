package com.alphaseries.game.advertising;

import com.alphaseries.util.StringUtils;

import java.util.concurrent.ThreadLocalRandom;

public final class VisitRoomAds {
    private final Object payloadsById;
    private final long count;

    private VisitRoomAds(Object payloadsById, long count) {
        this.payloadsById = payloadsById == null ? "" : payloadsById;
        this.count = count;
    }

    public static VisitRoomAds fromLegacy(Object payloadsById, long count) {
        if (payloadsById instanceof VisitRoomAds visitRoomAds) {
            return visitRoomAds;
        }
        return new VisitRoomAds(payloadsById, count);
    }

    public static VisitRoomAds empty() {
        return new VisitRoomAds("", 0L);
    }

    public long count() {
        return count;
    }

    public String payload(long visitRoomId) {
        return indexedPayload(payloadsById, visitRoomId);
    }

    public String randomPayload() {
        if (count <= 0L) {
            return "";
        }
        return payload(randomInclusive(1L, count));
    }

    private static String indexedPayload(Object cache, long index) {
        int idx = (int) index;
        if (idx < 0) {
            return "";
        }
        if (cache instanceof String[] values) {
            return idx < values.length ? StringUtils.text(values[idx]) : "";
        }
        if (cache instanceof Object[] values) {
            return idx < values.length ? StringUtils.text(values[idx]) : "";
        }
        return "";
    }

    private static long randomInclusive(long lower, long upper) {
        long min = Math.min(lower, upper);
        long max = Math.max(lower, upper);
        if (min == max) {
            return min;
        }
        return ThreadLocalRandom.current().nextLong(min, max + 1L);
    }
}
