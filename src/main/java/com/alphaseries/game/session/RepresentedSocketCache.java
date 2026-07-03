package com.alphaseries.game.session;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class RepresentedSocketCache {
    private final Map<Long, RepresentedSocketRecord> records;

    private RepresentedSocketCache(Map<Long, RepresentedSocketRecord> records) {
        this.records = new LinkedHashMap<>(records);
    }

    public static RepresentedSocketCache empty() {
        return new RepresentedSocketCache(Map.of());
    }

    public static RepresentedSocketCache fromRecords(Map<Long, RepresentedSocketRecord> records) {
        return new RepresentedSocketCache(records == null ? Map.of() : records);
    }

    public Map<Long, RepresentedSocketRecord> recordsBySocketIndex() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(records));
    }

    public long roomSlot(long socketIndex) {
        RepresentedSocketRecord record = records.get(socketIndex);
        return record == null ? 0L : record.roomSlot();
    }

    public boolean isBusy(long socketIndex) {
        RepresentedSocketRecord record = records.get(socketIndex);
        return record != null && record.busy();
    }

    public record RepresentedSocketRecord(long roomSlot, boolean busy) {
    }
}
