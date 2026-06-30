package com.alphaseries.game.room;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

public final class RepresentedRoomCache {
    private final String cacheText;

    private RepresentedRoomCache(String cacheText) {
        this.cacheText = StringUtils.text(cacheText);
    }

    public static RepresentedRoomCache fromLegacy(String cacheText) {
        return new RepresentedRoomCache(cacheText);
    }

    public String cacheText() {
        return cacheText;
    }

    public String record(long roomSlot) {
        if (roomSlot <= 0L) {
            return "";
        }
        String markerText = "\1" + roomSlot + '\t';
        int startAt = cacheText.indexOf(markerText);
        if (startAt < 0) {
            markerText = "\1" + roomSlot + '\2';
            startAt = cacheText.indexOf(markerText);
        }
        if (startAt < 0) {
            return "";
        }
        int recordStart = startAt + 1;
        int recordEnd = cacheText.indexOf('\2', recordStart);
        if (recordEnd < 0) {
            recordEnd = cacheText.length();
        }
        return cacheText.substring(recordStart, recordEnd);
    }

    public String recordField(long roomSlot, long fieldIndex) {
        String[] fields = record(roomSlot).split("\t", -1);
        return fieldIndex >= 0 && fieldIndex < fields.length ? fields[(int) fieldIndex] : "";
    }

    public RepresentedRoomCache setRecord(long roomSlot, String roomRecord) {
        if (roomSlot <= 0L) {
            return this;
        }
        String nextCache = removeRecord(cacheText, "\1" + roomSlot + '\t');
        nextCache = removeRecord(nextCache, "\1" + roomSlot + '\2');
        while (nextCache.startsWith("\2")) {
            nextCache = nextCache.substring(1);
        }
        return new RepresentedRoomCache(nextCache + '\1' + StringUtils.text(roomRecord) + '\2');
    }

    public RepresentedRoomCache addOccupant(long roomSlot, long entityIndex, long occupantType) {
        if (roomSlot <= 0L || entityIndex <= 0L) {
            return this;
        }
        String markerText = "\1" + entityIndex + '\2';
        int fieldIndex = occupantType == 2L ? 2 : 1;
        int countIndex = 3;
        String roomRecord = record(roomSlot);
        if (roomRecord.isEmpty()) {
            roomRecord = roomSlot + "\t\t\t0";
        }
        String[] fields = ensureFieldCount(roomRecord.split("\t", -1), countIndex);
        if (!StringUtils.text(fields[fieldIndex]).contains(markerText)) {
            fields[fieldIndex] = StringUtils.text(fields[fieldIndex]) + markerText;
            fields[countIndex] = String.valueOf(NumberUtils.parseLong(fields[countIndex]) + 1L);
        }
        return setRecord(roomSlot, String.join("\t", fields));
    }

    public Position movementPosition(long roomSlot, long entityIndex) {
        Position result = new Position();
        String roomRecord = record(roomSlot);
        if (roomRecord.isEmpty()) {
            return result;
        }
        String[] fields = roomRecord.split("\t", -1);
        if (fields.length < 5) {
            return result;
        }
        StringBuilder movementText = new StringBuilder();
        for (int fieldIndex = 4; fieldIndex < fields.length; fieldIndex++) {
            if (fieldIndex > 4) {
                movementText.append('\t');
            }
            movementText.append(fields[fieldIndex]);
        }
        for (String part : movementText.toString().split("\1", -1)) {
            String movementRecord = part;
            if (!movementRecord.isEmpty()) {
                if (movementRecord.endsWith("\2")) {
                    movementRecord = movementRecord.substring(0, movementRecord.length() - 1);
                }
                String[] movementFields = movementRecord.split("\t", -1);
                if (movementFields.length >= 3 && NumberUtils.parseLong(StringUtils.field(movementFields, 0)) == entityIndex) {
                    result.positionX = NumberUtils.parseLong(StringUtils.field(movementFields, 1));
                    result.positionY = NumberUtils.parseLong(StringUtils.field(movementFields, 2));
                    result.found = true;
                    return result;
                }
            }
        }
        return result;
    }

    public RepresentedRoomCache moveOccupant(
        long roomSlot,
        long entityIndex,
        long occupantType,
        long positionX,
        long positionY,
        long directionValue,
        long movingValue
    ) {
        if (roomSlot <= 0L || entityIndex <= 0L) {
            return this;
        }
        int fieldIndex = occupantType == 2L ? 5 : 4;
        String roomRecord = record(roomSlot);
        if (roomRecord.isEmpty()) {
            roomRecord = roomSlot + "\t\t\t0";
        }
        String[] fields = ensureFieldCount(roomRecord.split("\t", -1), fieldIndex);
        String movementRecord = entityIndex + "\t" + positionX + "\t" + positionY + "\t" + directionValue + "\t" + movingValue;
        fields[fieldIndex] = removeMovementRecord(StringUtils.text(fields[fieldIndex]), "\1" + entityIndex + '\t')
            + '\1' + movementRecord + '\2';
        return setRecord(roomSlot, String.join("\t", fields));
    }

    public RepresentedRoomCache moveOccupant(
        long roomSlot,
        long entityIndex,
        long positionX,
        long positionY,
        long directionValue,
        long movingValue
    ) {
        if (roomSlot <= 0L || entityIndex <= 0L) {
            return this;
        }
        String roomRecord = record(roomSlot);
        if (roomRecord.isEmpty()) {
            roomRecord = roomSlot + "\t\t\t0";
        }
        String[] rawFields = ensureFieldCount(roomRecord.split("\t", -1), 4);
        String[] fields = new String[5];
        for (int fieldIndex = 0; fieldIndex < 4; fieldIndex++) {
            fields[fieldIndex] = rawFields[fieldIndex];
        }
        StringBuilder movementText = new StringBuilder();
        for (int fieldIndex = 4; fieldIndex < rawFields.length; fieldIndex++) {
            if (fieldIndex > 4) {
                movementText.append('\t');
            }
            movementText.append(rawFields[fieldIndex]);
        }
        fields[4] = removeMovementRecord(movementText.toString(), "\1" + entityIndex + '\t')
            + '\1' + entityIndex + "\t" + positionX + "\t" + positionY + "\t" + directionValue + "\t" + movingValue + '\2';
        return setRecord(roomSlot, String.join("\t", fields));
    }

    public static String removeRecord(String cacheText, String markerText) {
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

    private static String removeMovementRecord(String movementText, String markerText) {
        String result = StringUtils.text(movementText);
        String marker = StringUtils.text(markerText);
        int markerAt = result.indexOf(marker);
        while (markerAt >= 0) {
            int endAt = result.indexOf('\2', markerAt + marker.length());
            if (endAt < 0) {
                result = result.substring(0, markerAt);
            } else {
                result = result.substring(0, markerAt) + result.substring(endAt + 1);
            }
            markerAt = result.indexOf(marker);
        }
        return result;
    }

    private static String[] ensureFieldCount(String[] fields, int lastIndex) {
        if (fields.length > lastIndex) {
            return fields;
        }
        String[] expanded = new String[lastIndex + 1];
        for (int index = 0; index < expanded.length; index++) {
            expanded[index] = index < fields.length ? fields[index] : "";
        }
        return expanded;
    }

    public static final class Position {
        public long positionX;
        public long positionY;
        public boolean found;
    }
}
