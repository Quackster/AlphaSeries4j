package com.alphaseries.game.advertising;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

public final class VisitRoomAds {
    private final Map<Long, String> payloadsById;
    private final long count;

    private VisitRoomAds(Object payloadsById, long count) {
        this.payloadsById = parsePayloads(payloadsById);
        this.count = count;
    }

    private VisitRoomAds(Map<Long, String> payloadsById, long count) {
        this.payloadsById = copyPayloads(payloadsById);
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

    public static VisitRoomAds fromPayloads(Map<Long, String> payloadsById, long count) {
        return new VisitRoomAds(payloadsById, count);
    }

    public long count() {
        return count;
    }

    public String payload(long visitRoomId) {
        if (visitRoomId < 0L) {
            return "";
        }
        return StringUtils.text(payloadsById.get(visitRoomId));
    }

    public String randomPayload() {
        if (count <= 0L) {
            return "";
        }
        return payload(randomInclusive(1L, count));
    }

    private static Map<Long, String> parsePayloads(Object cache) {
        if (cache instanceof Map<?, ?> values) {
            Map<Long, String> payloads = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : values.entrySet()) {
                long key = NumberUtils.parseLong(entry.getKey());
                if (key >= 0L) {
                    payloads.put(key, StringUtils.text(entry.getValue()));
                }
            }
            return payloads;
        }
        if (cache instanceof Object[] values) {
            Map<Long, String> payloads = new LinkedHashMap<>();
            for (int index = 0; index < values.length; index++) {
                payloads.put((long) index, StringUtils.text(values[index]));
            }
            return payloads;
        }
        return Map.of();
    }

    private static Map<Long, String> copyPayloads(Map<Long, String> payloadsById) {
        Map<Long, String> copiedPayloads = new LinkedHashMap<>();
        if (payloadsById != null) {
            for (Map.Entry<Long, String> entry : payloadsById.entrySet()) {
                if (entry.getKey() != null && entry.getKey() >= 0L) {
                    copiedPayloads.put(entry.getKey(), StringUtils.text(entry.getValue()));
                }
            }
        }
        return copiedPayloads;
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
