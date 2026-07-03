package com.alphaseries.game.navigator;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.alphaseries.dao.mysql.RoomDao;
import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.protocol.WireEncoding;
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

    public boolean appendPayloadTo(PacketBuilder packet, long oneBasedTreeIndex) {
        if (packet == null) {
            return false;
        }
        String payload = payload(oneBasedTreeIndex);
        if (payload.isEmpty()) {
            return false;
        }
        packet.appendRaw(payload);
        return true;
    }

    String payload(long oneBasedTreeIndex) {
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

    public static final class Payload {
        private final long index;
        private final String payload;

        private Payload(long index, String payload) {
            this.index = Math.max(0L, index);
            this.payload = StringUtils.text(payload);
        }

        public static Payload fromRecommendedRooms(long index, long treeId, List<RoomDao.RecommendedRoomRow> roomRows) {
            return fromPayloadText(index, WireEncoding.encodeVl64(treeId) + recommendedRoomPayload(roomRows));
        }

        static Payload fromPayloadText(long index, String payload) {
            return new Payload(index, payload);
        }

        public long index() {
            return index;
        }

        String payload() {
            return payload;
        }

        private static String recommendedRoomPayload(List<RoomDao.RecommendedRoomRow> roomRows) {
            long roomCount = 0L;
            PacketBuilder payload = PacketBuilder.create();
            if (roomRows != null) {
                for (RoomDao.RecommendedRoomRow row : roomRows) {
                    if (row != null) {
                        roomCount++;
                        payload
                            .appendInt(row.type())
                            .appendInt(row.style())
                            .appendInt(row.icon())
                            .appendString(row.caption())
                            .appendString(row.captionTwo())
                            .appendString(row.captionThree())
                            .appendString(row.reservedSlot())
                            .appendString(row.roomId())
                            .appendString(row.roomName())
                            .appendString(row.ownerName())
                            .appendString(row.doorStatus())
                            .appendString(row.visitorsNow())
                            .appendString(row.visitorsMax())
                            .appendString(row.description())
                            .appendString(row.trading())
                            .appendString(row.reservedSecondSlot())
                            .appendString(row.rating())
                            .appendString(row.categoryId())
                            .appendString(row.roomIcon())
                            .appendString(row.tagOne())
                            .appendString(row.tagTwo())
                            .appendString(row.allowOtherPets())
                            .appendString(row.modelName())
                            .appendString(row.requiredFiles())
                            .appendString(row.modelVisitorsMax())
                            .appendInt(row.treeId())
                            .appendInt(row.recommendedId());
                    }
                }
            }
            return PacketBuilder.create().appendInt(roomCount).appendRaw(payload.build()).build();
        }
    }
}
