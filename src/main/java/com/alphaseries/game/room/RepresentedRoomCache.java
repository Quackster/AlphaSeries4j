package com.alphaseries.game.room;

import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.util.FileUtils;
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

    static RepresentedRoomCache fromCacheText(String cacheText) {
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

    String cacheText() {
        PacketBuilder result = PacketBuilder.create().appendRaw(leadingText);
        for (RoomRecord record : records) {
            result.appendRaw('\1').appendRaw(record.cacheRecordText()).appendRaw('\2');
        }
        return result.build();
    }

    public void writeCacheFile(String path) {
        FileUtils.writeTextFile(path, cacheText());
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

    private RoomRecord roomRecordOrDefault(long roomSlot) {
        RoomRecord record = roomRecord(roomSlot);
        return record == null ? RoomRecord.empty(roomSlot) : record;
    }

    public long roomSlot(long roomSlot) {
        RoomRecord record = roomRecord(roomSlot);
        return record == null ? 0L : record.roomSlot();
    }

    public List<Long> userEntityIds(long roomSlot) {
        return entityIdsFromMarkers(activeUserMarkers(roomSlot) + movingUserMarkers(roomSlot));
    }

    public List<Long> botEntityIds(long roomSlot) {
        return entityIdsFromMarkers(activeBotMarkers(roomSlot) + movingBotMarkers(roomSlot));
    }

    private String activeUserMarkers(long roomSlot) {
        RoomRecord record = roomRecord(roomSlot);
        return record == null ? "" : record.activeUserMarkers();
    }

    private String activeBotMarkers(long roomSlot) {
        RoomRecord record = roomRecord(roomSlot);
        return record == null ? "" : record.activeBotMarkers();
    }

    private String movingUserMarkers(long roomSlot) {
        RoomRecord record = roomRecord(roomSlot);
        return record == null ? "" : record.movingUserMarkers();
    }

    private String movingBotMarkers(long roomSlot) {
        RoomRecord record = roomRecord(roomSlot);
        return record == null ? "" : record.movingBotMarkers();
    }

    public RepresentedRoomCache setRecord(RoomRecord roomRecord) {
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
        return setRecord(roomRecordOrDefault(roomSlot).withOccupant(entityIndex, occupantType));
    }

    public Position movementPosition(long roomSlot, long entityIndex) {
        RoomRecord roomRecord = roomRecord(roomSlot);
        if (roomRecord == null) {
            return Position.absent();
        }
        return roomRecord.movementPosition(entityIndex);
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
        return setRecord(roomRecordOrDefault(roomSlot)
            .withMovement(entityIndex, occupantType, positionX, positionY, directionValue, movingValue));
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
        for (MovementRecord movement : MovementRecord.recordsFromText(movementText)) {
            if (movement.entityIndex() > 0L && movement.positionX() == fromX && movement.positionY() == fromY) {
                cache = cache.moveOccupant(roomSlot, movement.entityIndex(), occupantType, toX, toY, directionValue, 0L);
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
        return setRecord(roomRecordOrDefault(roomSlot)
            .withFlattenedMovement(entityIndex, positionX, positionY, directionValue, movingValue));
    }

    private static List<Long> entityIdsFromMarkers(String markerText) {
        return EntityMarkers.fromText(markerText).entityIds();
    }

    private static RoomRecord roomRecordFromText(String recordText) {
        return RoomRecord.fromCacheRecord(RoomCacheRecord.fromText(recordText));
    }

    public record RoomRecord(
        long roomSlot,
        int fieldCount,
        String activeUserMarkers,
        String activeBotMarkers,
        String occupantCountText,
        String movingUserMarkers,
        String movingBotMarkers,
        List<String> extraFields
    ) {
        public RoomRecord {
            fieldCount = Math.max(0, fieldCount);
            activeUserMarkers = StringUtils.text(activeUserMarkers);
            activeBotMarkers = StringUtils.text(activeBotMarkers);
            occupantCountText = StringUtils.text(occupantCountText);
            movingUserMarkers = StringUtils.text(movingUserMarkers);
            movingBotMarkers = StringUtils.text(movingBotMarkers);
            extraFields = normalizedFields(extraFields);
        }

        private static RoomRecord fromCacheRecord(RoomCacheRecord record) {
            return new RoomRecord(
                record.roomSlot(),
                record.fieldCount(),
                record.activeUserMarkers(),
                record.activeBotMarkers(),
                record.occupantCountText(),
                record.movingUserMarkers(),
                record.movingBotMarkers(),
                record.extraFields());
        }

        public static RoomRecord empty(long roomSlot) {
            return new RoomRecord(roomSlot, 4, "", "", "0", "", "", List.of());
        }

        public static RoomRecord displayOnly(long roomSlot, String displayText) {
            return new RoomRecord(roomSlot, 2, StringUtils.text(displayText), "", "", "", "", List.of());
        }

        public String displayText() {
            return activeUserMarkers;
        }

        private Position movementPosition(long entityIndex) {
            if (entityIndex <= 0L || fieldCount < 5) {
                return Position.absent();
            }
            for (MovementRecord movementRecord : MovementRecord.recordsFromText(flattenedMovementText())) {
                if (movementRecord.isValid() && movementRecord.entityIndex() == entityIndex) {
                    return Position.found(movementRecord.positionX(), movementRecord.positionY());
                }
            }
            return Position.absent();
        }

        private RoomRecord withOccupant(long entityIndex, long occupantType) {
            EntityMarkers markers = EntityMarkers.fromText(occupantType == 2L ? activeBotMarkers : activeUserMarkers);
            String updatedActiveUserMarkers = activeUserMarkers;
            String updatedActiveBotMarkers = activeBotMarkers;
            String updatedOccupantCountText = occupantCountText;
            if (!markers.contains(entityIndex)) {
                String markerText = markers.withEntity(entityIndex).markerText();
                if (occupantType == 2L) {
                    updatedActiveBotMarkers = markerText;
                } else {
                    updatedActiveUserMarkers = markerText;
                }
                updatedOccupantCountText = String.valueOf(NumberUtils.parseLong(occupantCountText) + 1L);
            }
            return new RoomRecord(
                roomSlot,
                Math.max(fieldCount, 4),
                updatedActiveUserMarkers,
                updatedActiveBotMarkers,
                updatedOccupantCountText,
                movingUserMarkers,
                movingBotMarkers,
                extraFields);
        }

        private RoomRecord withMovement(
            long entityIndex,
            long occupantType,
            long positionX,
            long positionY,
            long directionValue,
            long movingValue
        ) {
            boolean botMovement = occupantType == 2L;
            String updatedMovingUserMarkers = movingUserMarkers;
            String updatedMovingBotMarkers = movingBotMarkers;
            if (botMovement) {
                updatedMovingBotMarkers = MovementRecord.replaceRecord(
                    movingBotMarkers,
                    new MovementRecord(entityIndex, positionX, positionY, directionValue, movingValue));
            } else {
                updatedMovingUserMarkers = MovementRecord.replaceRecord(
                    movingUserMarkers,
                    new MovementRecord(entityIndex, positionX, positionY, directionValue, movingValue));
            }
            return new RoomRecord(
                roomSlot,
                Math.max(fieldCount, botMovement ? 6 : 5),
                activeUserMarkers,
                activeBotMarkers,
                occupantCountText,
                updatedMovingUserMarkers,
                updatedMovingBotMarkers,
                extraFields);
        }

        private RoomRecord withFlattenedMovement(
            long entityIndex,
            long positionX,
            long positionY,
            long directionValue,
            long movingValue
        ) {
            String updatedMovingUserMarkers = MovementRecord.replaceRecord(
                flattenedMovementText(),
                new MovementRecord(entityIndex, positionX, positionY, directionValue, movingValue));
            return new RoomRecord(
                roomSlot,
                5,
                activeUserMarkers,
                activeBotMarkers,
                occupantCountText,
                updatedMovingUserMarkers,
                "",
                List.of());
        }

        private String cacheRecordText() {
            PacketBuilder text = PacketBuilder.create().appendRaw(roomSlot);
            if (fieldCount >= 2) {
                text.appendRaw('\t').appendRaw(activeUserMarkers);
            }
            if (fieldCount >= 3) {
                text.appendRaw('\t').appendRaw(activeBotMarkers);
            }
            if (fieldCount >= 4) {
                text.appendRaw('\t').appendRaw(occupantCountText);
            }
            if (fieldCount >= 5) {
                text.appendRaw('\t').appendRaw(movingUserMarkers);
            }
            if (fieldCount >= 6) {
                text.appendRaw('\t').appendRaw(movingBotMarkers);
            }
            if (fieldCount > 6) {
                for (String extraField : extraFields) {
                    text.appendRaw('\t').appendRaw(extraField);
                }
            }
            return text.build();
        }

        private String flattenedMovementText() {
            if (fieldCount < 5) {
                return "";
            }
            PacketBuilder text = PacketBuilder.create().appendRaw(movingUserMarkers);
            if (fieldCount >= 6) {
                text.appendRaw('\t').appendRaw(movingBotMarkers);
            }
            if (fieldCount > 6) {
                for (String extraField : extraFields) {
                    text.appendRaw('\t').appendRaw(extraField);
                }
            }
            return text.build();
        }

        private static List<String> normalizedFields(List<String> fields) {
            if (fields == null) {
                return List.of();
            }
            return fields.stream().map(StringUtils::text).toList();
        }
    }

    private record RoomCacheRecord(
        long roomSlot,
        int fieldCount,
        String activeUserMarkers,
        String activeBotMarkers,
        String occupantCountText,
        String movingUserMarkers,
        String movingBotMarkers,
        List<String> extraFields
    ) {
        private RoomCacheRecord {
            fieldCount = Math.max(0, fieldCount);
            activeUserMarkers = StringUtils.text(activeUserMarkers);
            activeBotMarkers = StringUtils.text(activeBotMarkers);
            occupantCountText = StringUtils.text(occupantCountText);
            movingUserMarkers = StringUtils.text(movingUserMarkers);
            movingBotMarkers = StringUtils.text(movingBotMarkers);
            extraFields = RoomRecord.normalizedFields(extraFields);
        }

        private static RoomCacheRecord fromText(String recordText) {
            StringUtils.IndexedFields fields = StringUtils.indexedFields(recordText, '\t');
            return new RoomCacheRecord(
                fields.number(0),
                fields.fieldCount(),
                fields.text(1),
                fields.text(2),
                fields.text(3),
                fields.text(4),
                fields.text(5),
                fields.fieldsFrom(6));
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

    private record EntityMarkers(List<Long> entityIds) {
        private EntityMarkers {
            entityIds = entityIds == null ? List.of() : List.copyOf(entityIds);
        }

        private static EntityMarkers fromText(String markerText) {
            Set<Long> entityIds = new LinkedHashSet<>();
            for (String part : StringUtils.delimitedFields(markerText, '\1')) {
                long entityId = NumberUtils.parseLong(part);
                if (entityId > 0L) {
                    entityIds.add(entityId);
                }
            }
            return new EntityMarkers(new ArrayList<>(entityIds));
        }

        private boolean contains(long entityId) {
            return entityIds.contains(entityId);
        }

        private EntityMarkers withEntity(long entityId) {
            if (entityId <= 0L || contains(entityId)) {
                return this;
            }
            List<Long> updatedIds = new ArrayList<>(entityIds);
            updatedIds.add(entityId);
            return new EntityMarkers(updatedIds);
        }

        private String markerText() {
            PacketBuilder text = PacketBuilder.create();
            for (long entityId : entityIds) {
                text.appendRaw('\1').appendRaw(entityId).appendRaw('\2');
            }
            return text.build();
        }
    }

    private record MovementRecord(
        long entityIndex,
        long positionX,
        long positionY,
        long directionValue,
        long movingValue,
        boolean valid
    ) {
        private MovementRecord(long entityIndex, long positionX, long positionY, long directionValue, long movingValue) {
            this(entityIndex, positionX, positionY, directionValue, movingValue, true);
        }

        private static List<MovementRecord> recordsFromText(String movementText) {
            List<MovementRecord> records = new ArrayList<>();
            for (String part : StringUtils.delimitedFields(movementText, '\1')) {
                MovementRecord record = fromText(part);
                if (record.isValid()) {
                    records.add(record);
                }
            }
            return records;
        }

        private static String replaceRecord(String movementText, MovementRecord replacement) {
            if (replacement == null || replacement.entityIndex() <= 0L) {
                return StringUtils.text(movementText);
            }
            List<MovementRecord> records = new ArrayList<>();
            for (MovementRecord record : recordsFromText(movementText)) {
                if (record.entityIndex() != replacement.entityIndex()) {
                    records.add(record);
                }
            }
            records.add(replacement);
            return textFromRecords(records);
        }

        private static String textFromRecords(List<MovementRecord> records) {
            PacketBuilder text = PacketBuilder.create();
            if (records != null) {
                for (MovementRecord record : records) {
                    if (record != null && record.isValid()) {
                        text.appendRaw('\1').appendRaw(record.recordText()).appendRaw('\2');
                    }
                }
            }
            return text.build();
        }

        private String recordText() {
            return PacketBuilder.create()
                .appendRaw(entityIndex)
                .appendRaw('\t')
                .appendRaw(positionX)
                .appendRaw('\t')
                .appendRaw(positionY)
                .appendRaw('\t')
                .appendRaw(directionValue)
                .appendRaw('\t')
                .appendRaw(movingValue)
                .build();
        }

        private boolean isValid() {
            return valid;
        }

        private static MovementRecord fromText(String recordText) {
            MovementCacheRecord record = MovementCacheRecord.fromText(recordText);
            return new MovementRecord(
                record.entityIndex(),
                record.positionX(),
                record.positionY(),
                record.directionValue(),
                record.movingValue(),
                record.isValid());
        }
    }

    private record MovementCacheRecord(
        long entityIndex,
        long positionX,
        long positionY,
        long directionValue,
        long movingValue,
        int fieldCount
    ) {
        private static MovementCacheRecord fromText(String recordText) {
            StringUtils.IndexedFields fields = StringUtils.indexedFields(normalizedMovementText(recordText), '\t');
            return new MovementCacheRecord(
                fields.number(0),
                fields.number(1),
                fields.number(2),
                fields.number(3),
                fields.number(4),
                fields.fieldCount());
        }

        private boolean isValid() {
            return fieldCount >= 3;
        }

        private static String normalizedMovementText(String recordText) {
            return StringUtils.text(recordText).replace("\2", "");
        }
    }
}
