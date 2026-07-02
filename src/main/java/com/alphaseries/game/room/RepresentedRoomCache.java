package com.alphaseries.game.room;

import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class RepresentedRoomCache {
    private final String leadingText;
    private final List<RoomRecord> records;

    private RepresentedRoomCache(List<RoomRecord> records) {
        this("", records);
    }

    private RepresentedRoomCache(String leadingText, List<RoomRecord> records) {
        this.leadingText = StringUtils.text(leadingText);
        this.records = List.copyOf(records);
    }

    public static RepresentedRoomCache fromRecords(String leadingText, List<RoomRecord> records) {
        return new RepresentedRoomCache(leadingText, records == null ? List.of() : records);
    }

    public static RepresentedRoomCache fromCacheText(String cacheText) {
        List<RoomRecord> parsedRecords = new ArrayList<>();
        String cache = StringUtils.text(cacheText);
        int recordStart = cache.indexOf('\1');
        String leadingText = recordStart < 0 ? cache : cache.substring(0, recordStart);
        while (recordStart >= 0) {
            int payloadStart = recordStart + 1;
            int recordEnd = cache.indexOf('\2', payloadStart);
            String recordText = recordEnd < 0 ? cache.substring(payloadStart) : cache.substring(payloadStart, recordEnd);
            parsedRecords.add(roomRecordFromText(recordText));
            recordStart = recordEnd < 0 ? -1 : cache.indexOf('\1', recordEnd + 1);
        }
        return fromRecords(leadingText, parsedRecords);
    }

    public static RepresentedRoomCache empty() {
        return new RepresentedRoomCache(List.of());
    }

    public String cacheText() {
        PacketBuilder result = PacketBuilder.create().appendRaw(leadingText);
        for (RoomRecord record : records) {
            result.appendRaw('\1').appendRaw(record.recordText()).appendRaw('\2');
        }
        return result.build();
    }

    public RepresentedRoomCache normalizedForCacheMirror() {
        return fromCacheText(cacheText());
    }

    public List<RoomRecord> roomRecords() {
        return List.copyOf(records);
    }

    private RoomRecord roomRecord(long roomSlot) {
        if (roomSlot <= 0L) {
            return null;
        }
        for (RoomRecord record : records) {
            if (record.roomSlot() == roomSlot) {
                return record;
            }
        }
        return null;
    }

    private String recordField(long roomSlot, long fieldIndex) {
        RoomRecord record = roomRecord(roomSlot);
        return record == null ? "" : record.field(fieldIndex);
    }

    public long roomSlot(long roomSlot) {
        return NumberUtils.parseLong(recordField(roomSlot, 0));
    }

    public List<Long> userEntityIds(long roomSlot) {
        return entityIdsFromMarkers(activeUserMarkers(roomSlot) + movingUserMarkers(roomSlot));
    }

    public List<Long> botEntityIds(long roomSlot) {
        return entityIdsFromMarkers(activeBotMarkers(roomSlot) + movingBotMarkers(roomSlot));
    }

    private String activeUserMarkers(long roomSlot) {
        return recordField(roomSlot, 1);
    }

    private String activeBotMarkers(long roomSlot) {
        return recordField(roomSlot, 2);
    }

    private String movingUserMarkers(long roomSlot) {
        return recordField(roomSlot, 4);
    }

    private String movingBotMarkers(long roomSlot) {
        return recordField(roomSlot, 5);
    }

    public RepresentedRoomCache setRecord(long roomSlot, String roomRecord) {
        return setRecord(new RoomRecord(roomSlot, roomRecord));
    }

    private RepresentedRoomCache setRecord(RoomRecord roomRecord) {
        long roomSlot = roomRecord == null ? 0L : roomRecord.roomSlot();
        if (roomSlot <= 0L) {
            return this;
        }
        List<RoomRecord> nextRecords = new ArrayList<>();
        for (RoomRecord record : records) {
            if (record.roomSlot() != roomSlot) {
                nextRecords.add(record);
            }
        }
        nextRecords.add(roomRecord);
        return new RepresentedRoomCache(leadingText, nextRecords);
    }

    public RepresentedRoomCache addOccupant(long roomSlot, long entityIndex, long occupantType) {
        if (roomSlot <= 0L || entityIndex <= 0L) {
            return this;
        }
        String markerText = "\1" + entityIndex + '\2';
        int fieldIndex = occupantType == 2L ? 2 : 1;
        int countIndex = 3;
        String[] fields = ensureFieldCount(recordFields(roomSlot, defaultRoomFields(roomSlot)), countIndex);
        if (!StringUtils.text(fields[fieldIndex]).contains(markerText)) {
            fields[fieldIndex] = StringUtils.text(fields[fieldIndex]) + markerText;
            fields[countIndex] = String.valueOf(NumberUtils.parseLong(fields[countIndex]) + 1L);
        }
        return setRecord(new RoomRecord(roomSlot, List.of(fields)));
    }

    public Position movementPosition(long roomSlot, long entityIndex) {
        RoomRecord roomRecord = roomRecord(roomSlot);
        if (roomRecord == null) {
            return Position.absent();
        }
        List<String> fields = roomRecord.fields();
        if (fields.size() < 5) {
            return Position.absent();
        }
        String movementText = joinedFieldsFrom(fields, 4);
        for (String part : movementText.split("\1", -1)) {
            MovementRecord movementRecord = MovementRecord.fromText(part);
            if (movementRecord.isValid() && movementRecord.entityIndex == entityIndex) {
                return Position.found(movementRecord.positionX, movementRecord.positionY);
            }
        }
        return Position.absent();
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
        String[] fields = ensureFieldCount(recordFields(roomSlot, defaultRoomFields(roomSlot)), fieldIndex);
        String movementRecord = entityIndex + "\t" + positionX + "\t" + positionY + "\t" + directionValue + "\t" + movingValue;
        fields[fieldIndex] = removeMovementRecord(StringUtils.text(fields[fieldIndex]), "\1" + entityIndex + '\t')
            + '\1' + movementRecord + '\2';
        return setRecord(new RoomRecord(roomSlot, List.of(fields)));
    }

    public RepresentedRoomCache moveOccupantsAt(
        long roomSlot,
        long occupantType,
        long fromX,
        long fromY,
        long toX,
        long toY,
        long directionValue
    ) {
        String movementText = occupantType == 2L ? movingBotMarkers(roomSlot) : movingUserMarkers(roomSlot);
        if (movementText.isEmpty()) {
            return this;
        }
        RepresentedRoomCache cache = this;
        for (String record : movementText.split("\1", -1)) {
            MovementRecord movement = MovementRecord.fromText(record);
            if (movement.entityIndex > 0L && movement.positionX == fromX && movement.positionY == fromY) {
                cache = cache.moveOccupant(roomSlot, movement.entityIndex, occupantType, toX, toY, directionValue, 0L);
            }
        }
        return cache;
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
        String[] rawFields = ensureFieldCount(recordFields(roomSlot, defaultRoomFields(roomSlot)), 4);
        String[] fields = new String[5];
        for (int fieldIndex = 0; fieldIndex < 4; fieldIndex++) {
            fields[fieldIndex] = rawFields[fieldIndex];
        }
        String movementText = joinedFieldsFrom(rawFields, 4);
        fields[4] = removeMovementRecord(movementText, "\1" + entityIndex + '\t')
            + '\1' + entityIndex + "\t" + positionX + "\t" + positionY + "\t" + directionValue + "\t" + movingValue + '\2';
        return setRecord(new RoomRecord(roomSlot, List.of(fields)));
    }

    static String removeRecord(String cacheText, String markerText) {
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

    private static String joinedFieldsFrom(List<String> fields, int firstIndex) {
        if (fields == null || firstIndex >= fields.size()) {
            return "";
        }
        return String.join("\t", fields.subList(Math.max(0, firstIndex), fields.size()));
    }

    private static String joinedFieldsFrom(String[] fields, int firstIndex) {
        if (fields == null || firstIndex >= fields.length) {
            return "";
        }
        List<String> selectedFields = new ArrayList<>();
        for (int fieldIndex = Math.max(0, firstIndex); fieldIndex < fields.length; fieldIndex++) {
            selectedFields.add(StringUtils.text(fields[fieldIndex]));
        }
        return String.join("\t", selectedFields);
    }

    private String[] recordFields(long roomSlot, List<String> defaultFields) {
        RoomRecord record = roomRecord(roomSlot);
        if (record == null) {
            return new RoomRecord(roomSlot, defaultFields).fields().toArray(String[]::new);
        }
        return record.fields().toArray(String[]::new);
    }

    private static List<String> defaultRoomFields(long roomSlot) {
        return List.of(String.valueOf(roomSlot), "", "", "0");
    }

    private static List<Long> entityIdsFromMarkers(String markerText) {
        Set<Long> entityIds = new LinkedHashSet<>();
        for (String part : StringUtils.text(markerText).split("\1", -1)) {
            long entityId = NumberUtils.parseLong(part);
            if (entityId > 0L) {
                entityIds.add(entityId);
            }
        }
        return new ArrayList<>(entityIds);
    }

    private static RoomRecord roomRecordFromText(String recordText) {
        String text = StringUtils.text(recordText);
        long roomSlot = NumberUtils.parseLong(StringUtils.delimitedField(text, '\t', 0));
        return new RoomRecord(roomSlot, text);
    }

    public record RoomRecord(long roomSlot, String recordText, List<String> fields) {
        public RoomRecord(long roomSlot, String recordText) {
            this(roomSlot, StringUtils.text(recordText), splitFields(recordText));
        }

        public RoomRecord(long roomSlot, List<String> fields) {
            this(roomSlot, String.join("\t", normalizedFields(fields)), normalizedFields(fields));
        }

        public RoomRecord {
            recordText = StringUtils.text(recordText);
            fields = normalizedFields(fields);
        }

        private String field(long fieldIndex) {
            return fieldIndex >= 0 && fieldIndex < fields.size() ? fields.get((int) fieldIndex) : "";
        }

        private static List<String> splitFields(String recordText) {
            return StringUtils.delimitedFields(recordText, '\t');
        }

        private static List<String> normalizedFields(List<String> fields) {
            if (fields == null) {
                return List.of();
            }
            return fields.stream().map(StringUtils::text).toList();
        }
    }

    public record Position(long positionX, long positionY, boolean found) {
        public static Position absent() {
            return new Position(0L, 0L, false);
        }

        public static Position found(long positionX, long positionY) {
            return new Position(positionX, positionY, true);
        }
    }

    private static final class MovementRecord {
        private final long entityIndex;
        private final long positionX;
        private final long positionY;
        private final boolean valid;

        private MovementRecord(long entityIndex, long positionX, long positionY, boolean valid) {
            this.entityIndex = entityIndex;
            this.positionX = positionX;
            this.positionY = positionY;
            this.valid = valid;
        }

        private boolean isValid() {
            return valid;
        }

        private static MovementRecord fromText(String recordText) {
            String text = StringUtils.text(recordText).replace("\2", "");
            return new MovementRecord(
                NumberUtils.parseLong(StringUtils.delimitedField(text, '\t', 0)),
                NumberUtils.parseLong(StringUtils.delimitedField(text, '\t', 1)),
                NumberUtils.parseLong(StringUtils.delimitedField(text, '\t', 2)),
                text.indexOf('\t') >= 0 && text.indexOf('\t', text.indexOf('\t') + 1) >= 0);
        }
    }
}
