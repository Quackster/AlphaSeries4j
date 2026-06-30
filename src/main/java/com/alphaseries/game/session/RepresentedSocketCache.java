package com.alphaseries.game.session;

import java.util.LinkedHashMap;
import java.util.Map;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

public final class RepresentedSocketCache {
    private final Map<Long, RepresentedSocketRecord> records;

    private RepresentedSocketCache(Object cacheText) {
        this.records = parseRecords(StringUtils.text(cacheText));
    }

    private RepresentedSocketCache(Map<Long, RepresentedSocketRecord> records) {
        this.records = new LinkedHashMap<>(records);
    }

    public static RepresentedSocketCache fromLegacy(Object cacheText) {
        if (cacheText instanceof RepresentedSocketCache representedSocketCache) {
            return representedSocketCache;
        }
        return new RepresentedSocketCache(cacheText);
    }

    public static RepresentedSocketCache empty() {
        return new RepresentedSocketCache("");
    }

    public static RepresentedSocketCache fromRecords(Map<Long, RepresentedSocketRecord> records) {
        return new RepresentedSocketCache(records == null ? Map.of() : records);
    }

    public String record(long socketIndex) {
        RepresentedSocketRecord record = records.get(socketIndex);
        return record == null ? "" : record.payload();
    }

    public long roomSlot(long socketIndex) {
        RepresentedSocketRecord record = records.get(socketIndex);
        return record == null ? 0L : record.roomSlot();
    }

    public boolean isBusy(long socketIndex) {
        RepresentedSocketRecord record = records.get(socketIndex);
        return record != null && record.busy();
    }

    private static Map<Long, RepresentedSocketRecord> parseRecords(String cacheText) {
        Map<Long, RepresentedSocketRecord> parsedRecords = new LinkedHashMap<>();
        int markerAt = cacheText.indexOf('[');
        while (markerAt >= 0) {
            int markerEnd = cacheText.indexOf(']', markerAt + 1);
            if (markerEnd < 0) {
                break;
            }
            long socketIndex = NumberUtils.parseLong(cacheText.substring(markerAt + 1, markerEnd));
            int payloadStart = markerEnd + 1;
            int nextMarkerAt = cacheText.indexOf('[', payloadStart);
            int payloadEnd = nextMarkerAt < 0 ? cacheText.length() : nextMarkerAt;
            if (socketIndex > 0L) {
                parsedRecords.putIfAbsent(socketIndex, RepresentedSocketRecord.fromPayload(cacheText.substring(payloadStart, payloadEnd)));
            }
            markerAt = nextMarkerAt;
        }
        return parsedRecords;
    }

    public record RepresentedSocketRecord(String payload, long roomSlot, boolean busy) {
        public static RepresentedSocketRecord fromPayload(String payload) {
            String payloadText = StringUtils.text(payload);
            String[] fields = payloadText.split("\2", -1);
            long roomSlot = fields.length >= 2 ? NumberUtils.parseLong(fields[1]) : 0L;
            boolean busy = fields.length >= 6 && NumberUtils.parseLong(fields[5]) != 0L;
            return new RepresentedSocketRecord(payloadText, roomSlot, busy);
        }
    }
}
