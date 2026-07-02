package com.alphaseries.game.advertising;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import com.alphaseries.util.StringUtils;

public final class VisitRoomAds {
    private final Map<Long, String> payloadsById;
    private final long count;

    private VisitRoomAds(Map<Long, String> payloadsById, long count) {
        this.payloadsById = copyPayloads(payloadsById);
        this.count = count;
    }

    public static VisitRoomAds empty() {
        return new VisitRoomAds(Map.of(), 0L);
    }

    public static VisitRoomAds fromPayloads(Map<Long, String> payloadsById, long count) {
        return new VisitRoomAds(payloadsById, count);
    }

    public long count() {
        return count;
    }

    public Map<Long, String> payloadsById() {
        return Map.copyOf(payloadsById);
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
