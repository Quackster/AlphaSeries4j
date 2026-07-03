package com.alphaseries.game.navigator;

import java.util.LinkedHashMap;
import java.util.Map;

import com.alphaseries.util.StringUtils;

public final class RecommendedRooms {
    private final Map<Long, String> payloadsByIndex;
    private final long count;

    private RecommendedRooms(Map<Long, String> payloadsByIndex, long count) {
        this.payloadsByIndex = copyPayloads(payloadsByIndex);
        this.count = Math.max(0L, count);
    }

    public static RecommendedRooms empty() {
        return new RecommendedRooms(Map.of(), 0L);
    }

    public static RecommendedRooms fromPayloads(Iterable<Payload> payloads, long count) {
        Map<Long, String> payloadsByIndex = new LinkedHashMap<>();
        if (payloads != null) {
            for (Payload payload : payloads) {
                if (payload != null) {
                    payloadsByIndex.put(payload.index(), payload.payload());
                }
            }
        }
        return new RecommendedRooms(payloadsByIndex, count);
    }

    public long count() {
        return count;
    }

    public String payload(long oneBasedTreeIndex) {
        long normalizedIndex = oneBasedTreeIndex > 0L ? oneBasedTreeIndex - 1L : oneBasedTreeIndex;
        if (normalizedIndex < 0L) {
            return "";
        }
        return StringUtils.text(payloadsByIndex.get(normalizedIndex));
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

    public record Payload(long index, String payload) {
        public Payload {
            index = Math.max(0L, index);
            payload = StringUtils.text(payload);
        }
    }
}
