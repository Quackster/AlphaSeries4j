package com.alphaseries.game.room;

import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class FurnitureRoomCache {
    private FurnitureRoomCache() {
    }

    public static State trackMarker(State cacheState, long roomId, long furnitureId) {
        State state = normalizedState(cacheState);
        if (furnitureId <= 0L) {
            return state;
        }

        state.pendingFurnitureCache = state.pendingFurnitureCache
            .withoutSimpleMarker(furnitureId)
            .withoutRecord(furnitureId)
            .withSimpleMarker(furnitureId);

        if (!state.representedRoomCache.isEmpty()) {
            state.representedRoomCache = removeRecord(state.representedRoomCache, marker(furnitureId));
            state.representedRoomCache = removeRecord(state.representedRoomCache, recordMarker(furnitureId));
        }

        if (roomId > 0L) {
            state.pendingRoomCache = state.pendingRoomCache
                .withoutSimpleMarker(roomId)
                .withoutRecord(roomId)
                .withSimpleMarker(roomId);
        }
        return state;
    }

    public static State removeMarker(State cacheState, long furnitureId) {
        State state = normalizedState(cacheState);
        if (furnitureId <= 0L) {
            return state;
        }

        state.pendingRoomCache = state.pendingRoomCache
            .withoutSimpleMarker(furnitureId)
            .withoutRecord(furnitureId);
        state.pendingFurnitureCache = state.pendingFurnitureCache
            .withoutSimpleMarker(furnitureId)
            .withoutRecord(furnitureId);
        if (!state.representedRoomCache.isEmpty()) {
            state.representedRoomCache = removeRecord(state.representedRoomCache, marker(furnitureId));
            state.representedRoomCache = removeRecord(state.representedRoomCache, recordMarker(furnitureId));
        }
        return state;
    }

    public static State stateCache(State cacheState, long roomId, long furnitureId, long stateValue) {
        State state = normalizedState(cacheState);
        if (roomId <= 0L || furnitureId <= 0L) {
            return state;
        }

        state.pendingRoomCache = state.pendingRoomCache
            .withoutSimpleMarker(roomId)
            .withoutRecord(roomId)
            .withSimpleMarker(roomId);

        state.pendingFurnitureCache = state.pendingFurnitureCache
            .withoutSimpleMarker(furnitureId)
            .withSimpleMarker(furnitureId);

        state.representedRoomCache = MarkerCache.fromText(state.representedRoomCache)
            .withoutRecord(roomId)
            .withoutRecord(furnitureId)
            .cacheText();
        state.representedRoomCache = removeRecord(state.representedRoomCache, marker(furnitureId));
        state.representedRoomCache = appendRecord(
            state.representedRoomCache,
            stateCacheRecord(roomId, furnitureId, stateValue));
        return state;
    }

    public static State stateWrite(State cacheState, long roomId, long furnitureId, String stateText) {
        State state = normalizedState(cacheState);
        if (furnitureId <= 0L) {
            return state;
        }

        state.pendingFurnitureCache = state.pendingFurnitureCache
            .withoutSimpleMarker(furnitureId)
            .withSimpleMarker(furnitureId);

        if (roomId > 0L) {
            state.pendingRoomCache = state.pendingRoomCache
                .withoutSimpleMarker(roomId)
                .withSimpleMarker(roomId);
        }

        state.representedRoomCache = removeRecord(state.representedRoomCache, marker(furnitureId));
        state.representedRoomCache = removeRecord(state.representedRoomCache, recordMarker(furnitureId));
        state.representedRoomCache = appendRecord(
            state.representedRoomCache,
            stateWriteRecord(furnitureId, roomId, stateText));
        return state;
    }

    private static State normalizedState(State cacheState) {
        return cacheState == null ? State.empty() : cacheState;
    }

    private static String removeRecord(String cacheText, String markerText) {
        String cache = StringUtils.text(cacheText);
        String marker = StringUtils.text(markerText);
        if (cache.isEmpty() || marker.isEmpty()) {
            return cache;
        }
        int markerAt = cache.indexOf(marker);
        while (markerAt >= 0) {
            int recordStart = cache.lastIndexOf('\1', markerAt);
            if (recordStart < 0) {
                recordStart = markerAt;
            }
            int recordEnd = cache.indexOf('\2', markerAt + marker.length());
            if (recordEnd < 0) {
                recordEnd = markerAt + marker.length() - 1;
            }
            cache = cache.substring(0, recordStart) + cache.substring(recordEnd + 1);
            markerAt = cache.indexOf(marker);
        }
        return cache;
    }

    private static List<Long> markerIds(MarkerCache markerCache) {
        Set<Long> ids = new LinkedHashSet<>();
        for (MarkerRecord record : markerCache.records()) {
            if (record.id() > 0L) {
                ids.add(record.id());
            }
        }
        return new ArrayList<>(ids);
    }

    private static String marker(long id) {
        return "\1" + id + '\2';
    }

    private static String recordMarker(long id) {
        return "\1" + id + '\t';
    }

    private static String appendRecord(String cacheText, String recordText) {
        return PacketBuilder.create()
            .appendRaw(StringUtils.text(cacheText))
            .appendRaw('\1')
            .appendRaw(recordText)
            .appendRaw('\2')
            .build();
    }

    private static String stateCacheRecord(long roomId, long furnitureId, long stateValue) {
        return PacketBuilder.create()
            .appendRaw(roomId)
            .appendRaw('\t')
            .appendRaw(furnitureId)
            .appendRaw('\t')
            .appendRaw(stateValue)
            .build();
    }

    private static String stateWriteRecord(long furnitureId, long roomId, String stateText) {
        return PacketBuilder.create()
            .appendRaw(furnitureId)
            .appendRaw('\t')
            .appendRaw(roomId)
            .appendRaw('\t')
            .appendRaw(StringUtils.text(stateText))
            .build();
    }

    private record MarkerRecord(long id, String valueText) {
        private MarkerRecord {
            valueText = StringUtils.text(valueText);
        }

        private static MarkerRecord simple(long id) {
            return new MarkerRecord(id, "");
        }

        private static MarkerRecord fromText(String recordText) {
            String text = StringUtils.text(recordText);
            int delimiterAt = text.indexOf('\t');
            if (delimiterAt < 0) {
                return simple(NumberUtils.parseLong(text));
            }
            return new MarkerRecord(
                NumberUtils.parseLong(text.substring(0, delimiterAt)),
                text.substring(delimiterAt + 1));
        }

        private boolean isSimple() {
            return valueText.isEmpty();
        }

        private String cacheText() {
            if (isSimple()) {
                return String.valueOf(id);
            }
            return PacketBuilder.create()
                .appendRaw(id)
                .appendRaw('\t')
                .appendRaw(valueText)
                .build();
        }
    }

    private static final class MarkerCache {
        private final List<MarkerRecord> records;

        private MarkerCache(List<MarkerRecord> records) {
            this.records = records;
        }

        private static MarkerCache empty() {
            return new MarkerCache(List.of());
        }

        private static MarkerCache fromText(String cacheText) {
            List<MarkerRecord> records = new ArrayList<>();
            String cache = StringUtils.text(cacheText);
            int recordStart = cache.indexOf('\1');
            while (recordStart >= 0) {
                int bodyStart = recordStart + 1;
                int recordEnd = cache.indexOf('\2', bodyStart);
                if (recordEnd < 0) {
                    String body = cache.substring(bodyStart);
                    if (!body.isEmpty()) {
                        records.add(MarkerRecord.fromText(body));
                    }
                    break;
                }
                records.add(MarkerRecord.fromText(cache.substring(bodyStart, recordEnd)));
                recordStart = cache.indexOf('\1', recordEnd + 1);
            }
            return new MarkerCache(records);
        }

        private List<MarkerRecord> records() {
            return List.copyOf(records);
        }

        private MarkerCache withoutSimpleMarker(long id) {
            if (id <= 0L || records.isEmpty()) {
                return this;
            }
            List<MarkerRecord> updatedRecords = new ArrayList<>();
            for (MarkerRecord record : records) {
                if (!record.isSimple() || record.id() != id) {
                    updatedRecords.add(record);
                }
            }
            return new MarkerCache(updatedRecords);
        }

        private MarkerCache withoutRecord(long id) {
            if (id <= 0L || records.isEmpty()) {
                return this;
            }
            List<MarkerRecord> updatedRecords = new ArrayList<>();
            for (MarkerRecord record : records) {
                if (record.isSimple() || record.id() != id) {
                    updatedRecords.add(record);
                }
            }
            return new MarkerCache(updatedRecords);
        }

        private MarkerCache withSimpleMarker(long id) {
            if (id <= 0L) {
                return this;
            }
            List<MarkerRecord> updatedRecords = new ArrayList<>(records);
            updatedRecords.add(MarkerRecord.simple(id));
            return new MarkerCache(updatedRecords);
        }

        private MarkerCache withRecord(MarkerRecord record) {
            if (record == null || record.id() <= 0L) {
                return this;
            }
            List<MarkerRecord> updatedRecords = new ArrayList<>(records);
            updatedRecords.add(record);
            return new MarkerCache(updatedRecords);
        }

        private String cacheText() {
            PacketBuilder cache = PacketBuilder.create();
            for (MarkerRecord record : records) {
                cache.appendRaw('\1').appendRaw(record.cacheText()).appendRaw('\2');
            }
            return cache.build();
        }
    }

    public static final class State {
        private MarkerCache pendingRoomCache = MarkerCache.empty();
        private MarkerCache pendingFurnitureCache = MarkerCache.empty();
        private String representedRoomCache = "";

        public static State empty() {
            return new State();
        }

        static State from(State markerState, RepresentedRoomCache representedRoomCache) {
            State source = markerState == null ? State.empty() : markerState;
            State state = new State();
            state.pendingRoomCache = source.pendingRoomCache;
            state.pendingFurnitureCache = source.pendingFurnitureCache;
            state.representedRoomCache = representedRoomCache == null ? "" : representedRoomCache.cacheText();
            return state;
        }

        static State markerStateFrom(State state) {
            State source = state == null ? State.empty() : state;
            State markerState = new State();
            markerState.pendingRoomCache = source.pendingRoomCache;
            markerState.pendingFurnitureCache = source.pendingFurnitureCache;
            return markerState;
        }

        public List<Long> pendingFurnitureIds() {
            return FurnitureRoomCache.markerIds(pendingFurnitureCache);
        }

        public List<Long> pendingRoomIds() {
            return FurnitureRoomCache.markerIds(pendingRoomCache);
        }

        private String pendingRoomCache() {
            return pendingRoomCache.cacheText();
        }

        private String pendingFurnitureCache() {
            return pendingFurnitureCache.cacheText();
        }

        public RepresentedRoomCache representedRooms() {
            return RepresentedRoomCache.fromCacheText(representedRoomCache);
        }

        public State withRepresentedRooms(RepresentedRoomCache representedRoomCache) {
            this.representedRoomCache = representedRoomCache == null ? "" : representedRoomCache.cacheText();
            return this;
        }

        public State withPendingFurnitureMarkers(long... furnitureIds) {
            if (furnitureIds == null) {
                return this;
            }
            for (long furnitureId : furnitureIds) {
                pendingFurnitureCache = pendingFurnitureCache.withSimpleMarker(furnitureId);
            }
            return this;
        }

        public State withPendingFurnitureRecord(long furnitureId, String recordText) {
            if (furnitureId > 0L) {
                pendingFurnitureCache = pendingFurnitureCache.withRecord(new MarkerRecord(furnitureId, recordText));
            }
            return this;
        }

        public State withPendingRoomMarkers(long... roomIds) {
            if (roomIds == null) {
                return this;
            }
            for (long roomId : roomIds) {
                pendingRoomCache = pendingRoomCache.withSimpleMarker(roomId);
            }
            return this;
        }

        public State withPendingRoomRecord(long roomId, String recordText) {
            if (roomId > 0L) {
                pendingRoomCache = pendingRoomCache.withRecord(new MarkerRecord(roomId, recordText));
            }
            return this;
        }

        public State removePendingFurniture(long furnitureId) {
            pendingFurnitureCache = pendingFurnitureCache
                .withoutSimpleMarker(furnitureId)
                .withoutRecord(furnitureId);
            return this;
        }

        public State removePendingFurnitureMarkers(long... furnitureIds) {
            if (furnitureIds == null) {
                return this;
            }
            for (long furnitureId : furnitureIds) {
                pendingFurnitureCache = pendingFurnitureCache.withoutSimpleMarker(furnitureId);
            }
            return this;
        }

        public State removePendingRoom(long roomId) {
            pendingRoomCache = pendingRoomCache.withoutSimpleMarker(roomId);
            return this;
        }

    }
}
