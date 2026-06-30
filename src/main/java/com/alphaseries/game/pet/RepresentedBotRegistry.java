package com.alphaseries.game.pet;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class RepresentedBotRegistry {
    private String allocatedEntityMarkers;
    private String recordCache;

    private RepresentedBotRegistry(String allocatedEntityMarkers, String recordCache) {
        this.allocatedEntityMarkers = StringUtils.text(allocatedEntityMarkers);
        this.recordCache = StringUtils.text(recordCache);
    }

    public static RepresentedBotRegistry fromLegacy(String allocatedEntityMarkers, String recordCache) {
        return new RepresentedBotRegistry(allocatedEntityMarkers, recordCache);
    }

    public static String[] fieldsFromLegacy(Object fieldSource) {
        if (fieldSource instanceof String[] fields) {
            return fields;
        }
        return StringUtils.text(fieldSource).split("\t", -1);
    }

    public String allocatedEntityMarkers() {
        return allocatedEntityMarkers;
    }

    public String recordCache() {
        return recordCache;
    }

    public long reserveSlot() {
        for (long slotIndex = 1L; slotIndex <= 5000L; slotIndex++) {
            String marker = marker(slotIndex);
            if (!allocatedEntityMarkers.contains(marker)) {
                allocatedEntityMarkers += marker;
                return slotIndex;
            }
        }
        return 0L;
    }

    public void storeRecord(long botEntityId, String recordText) {
        if (botEntityId <= 0L) {
            return;
        }
        removeRecordOnly(botEntityId);
        recordCache += "[" + botEntityId + ":" + StringUtils.text(recordText) + "]";
    }

    public void removeRecord(long botEntityId) {
        if (botEntityId <= 0L) {
            return;
        }
        removeRecordOnly(botEntityId);
        allocatedEntityMarkers = allocatedEntityMarkers.replace(marker(botEntityId), "");
    }

    public String recordText(long botEntityId) {
        if (botEntityId <= 0L || recordCache.isEmpty()) {
            return "";
        }
        int recordStart = recordStart(botEntityId);
        if (recordStart < 0) {
            return "";
        }
        int recordEnd = recordCache.indexOf(']', recordStart);
        if (recordEnd <= recordStart) {
            return "";
        }
        return recordCache.substring(recordStart, recordEnd);
    }

    public String recordField(long botEntityId, long fieldIndex) {
        String[] fields = recordText(botEntityId).split("\2", -1);
        return fieldIndex >= 0L && fieldIndex < fields.length ? fields[(int) fieldIndex] : "";
    }

    public long recordLong(long botEntityId, long fieldIndex) {
        return NumberUtils.parseLong(recordField(botEntityId, fieldIndex));
    }

    public long entityFromBotId(long botId) {
        if (botId <= 0L || recordCache.isEmpty()) {
            return 0L;
        }
        for (Record record : records()) {
            if (record.fields.length >= 2 && NumberUtils.parseLong(record.fields[1]) == botId) {
                return record.entityId;
            }
        }
        return 0L;
    }

    public String entitiesForRoom(long roomSlot, long onlyBotId) {
        if (roomSlot <= 0L || recordCache.isEmpty()) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        for (Record record : records()) {
            if (record.fields.length >= 2
                && NumberUtils.parseLong(record.fields[0]) == roomSlot
                && (onlyBotId <= 0L || NumberUtils.parseLong(record.fields[1]) == onlyBotId)) {
                if (result.length() > 0) {
                    result.append('\r');
                }
                result.append(record.entityId);
            }
        }
        return result.toString();
    }

    public void storePosition(long botEntityId, long positionX, long positionY, String positionZ, long positionR) {
        String[] fields = recordText(botEntityId).split("\2", -1);
        if (fields.length < 10) {
            return;
        }
        fields[6] = String.valueOf(positionX);
        fields[7] = String.valueOf(positionY);
        fields[8] = StringUtils.text(positionZ);
        fields[9] = String.valueOf(positionR);
        storeRecord(botEntityId, String.join("\2", fields));
    }

    public List<Long> allocatedEntityIds() {
        List<Long> result = new ArrayList<>();
        for (String part : allocatedEntityMarkers.split("\\]", -1)) {
            long entityId = NumberUtils.parseLong(part.replace("[", ""));
            if (entityId > 0L) {
                result.add(entityId);
            }
        }
        return result;
    }

    private void removeRecordOnly(long botEntityId) {
        String startMarker = "[" + botEntityId + ":";
        int startAt = recordCache.indexOf(startMarker);
        if (startAt >= 0) {
            int endAt = recordCache.indexOf(']', startAt + startMarker.length());
            if (endAt >= 0) {
                recordCache = recordCache.substring(0, startAt) + recordCache.substring(endAt + 1);
            }
        }
    }

    private int recordStart(long botEntityId) {
        String startMarker = "[" + botEntityId + ":";
        int startAt = recordCache.indexOf(startMarker);
        return startAt < 0 ? -1 : startAt + startMarker.length();
    }

    private List<Record> records() {
        List<Record> result = new ArrayList<>();
        for (String recordText : recordCache.split("\\[", -1)) {
            int payloadAt = recordText.indexOf(':');
            int endAt = recordText.indexOf(']');
            if (payloadAt > 0 && endAt > payloadAt) {
                long entityId = NumberUtils.parseLong(recordText.substring(0, payloadAt));
                String[] fields = recordText.substring(payloadAt + 1, endAt).split("\2", -1);
                result.add(new Record(entityId, fields));
            }
        }
        return result;
    }

    private static String marker(long entityId) {
        return "[" + entityId + "]";
    }

    private static final class Record {
        private final long entityId;
        private final String[] fields;

        private Record(long entityId, String[] fields) {
            this.entityId = entityId;
            this.fields = fields;
        }
    }
}
