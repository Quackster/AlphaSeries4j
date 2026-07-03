package com.alphaseries.game.advertising;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import com.alphaseries.protocol.PacketBuilder;
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

    public static VisitRoomAds fromPayloads(Iterable<Payload> payloads, long count) {
        Map<Long, String> payloadsById = new LinkedHashMap<>();
        if (payloads != null) {
            for (Payload payload : payloads) {
                if (payload != null) {
                    payloadsById.put(payload.visitRoomId(), payload.payload());
                }
            }
        }
        return new VisitRoomAds(payloadsById, count);
    }

    public long count() {
        return count;
    }

    public boolean appendPayloadTo(PacketBuilder packet, long visitRoomId) {
        if (packet == null) {
            return false;
        }
        String payload = payload(visitRoomId);
        if (payload.isEmpty()) {
            return false;
        }
        packet.appendRaw(payload);
        return true;
    }

    public boolean appendRandomPayloadTo(PacketBuilder packet) {
        if (count <= 0L) {
            return false;
        }
        return appendPayloadTo(packet, randomInclusive(1L, count));
    }

    String payload(long visitRoomId) {
        if (visitRoomId < 0L) {
            return "";
        }
        return StringUtils.text(payloadsById.get(visitRoomId));
    }

    String randomPayload() {
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

    public static final class Payload {
        private final long visitRoomId;
        private final String payload;

        private Payload(long visitRoomId, String payload) {
            this.visitRoomId = Math.max(0L, visitRoomId);
            this.payload = StringUtils.text(payload);
        }

        public static Payload fromAdvertisement(long visitRoomId, String assetPath, String address) {
            return fromPayloadText(visitRoomId, PacketBuilder.create()
                .appendRaw(StringUtils.text(assetPath))
                .appendString(visitRoomId)
                .appendString(address)
                .build());
        }

        static Payload fromPayloadText(long visitRoomId, String payload) {
            return new Payload(visitRoomId, payload);
        }

        public long visitRoomId() {
            return visitRoomId;
        }

        String payload() {
            return payload;
        }
    }
}
