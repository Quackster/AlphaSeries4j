package com.alphaseries.game.navigator;

import java.util.LinkedHashMap;
import java.util.Map;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

public final class RecommendedRooms {
    private final Map<Long, String> payloadsByIndex;
    private final long count;

    private RecommendedRooms(Object payloads, long count) {
        this.payloadsByIndex = parsePayloads(payloads);
        this.count = Math.max(0L, count);
    }

    private RecommendedRooms(Map<Long, String> payloadsByIndex, long count) {
        this.payloadsByIndex = copyPayloads(payloadsByIndex);
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

    public static RecommendedRooms fromPayloads(Map<Long, String> payloadsByIndex, long count) {
        return new RecommendedRooms(payloadsByIndex, count);
    }

    public long count() {
        return count;
    }

    public Map<Long, String> payloadsByIndex() {
        return Map.copyOf(payloadsByIndex);
    }

    public String payload(long oneBasedTreeIndex) {
        long normalizedIndex = oneBasedTreeIndex > 0L ? oneBasedTreeIndex - 1L : oneBasedTreeIndex;
        if (normalizedIndex < 0L) {
            return "";
        }
        return StringUtils.text(payloadsByIndex.get(normalizedIndex));
    }

    private static Map<Long, String> parsePayloads(Object payloads) {
        if (payloads instanceof Map<?, ?> values) {
            Map<Long, String> parsedPayloads = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : values.entrySet()) {
                long key = NumberUtils.parseLong(entry.getKey());
                if (key >= 0L) {
                    parsedPayloads.put(key, StringUtils.text(entry.getValue()));
                }
            }
            return parsedPayloads;
        }
        if (payloads instanceof Object[] values) {
            Map<Long, String> parsedPayloads = new LinkedHashMap<>();
            for (int index = 0; index < values.length; index++) {
                parsedPayloads.put((long) index, StringUtils.text(values[index]));
            }
            return parsedPayloads;
        }
        return Map.of();
    }

    private static Map<Long, String> copyPayloads(Map<Long, String> payloadsByIndex) {
        Map<Long, String> copiedPayloads = new LinkedHashMap<>();
        if (payloadsByIndex != null) {
            for (Map.Entry<Long, String> entry : payloadsByIndex.entrySet()) {
                if (entry.getKey() != null && entry.getKey() >= 0L) {
                    copiedPayloads.put(entry.getKey(), StringUtils.text(entry.getValue()));
                }
            }
        }
        return copiedPayloads;
    }
}
