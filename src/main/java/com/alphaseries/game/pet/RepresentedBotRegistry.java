package com.alphaseries.game.pet;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class RepresentedBotRegistry {
    private final Set<Long> allocatedEntityIds;
    private final Map<Long, RepresentedBotRecord> records;

    private RepresentedBotRegistry(Set<Long> allocatedEntityIds, Map<Long, RepresentedBotRecord> records) {
        this.allocatedEntityIds = allocatedEntityIds == null ? new LinkedHashSet<>() : new LinkedHashSet<>(allocatedEntityIds);
        this.records = records == null ? new LinkedHashMap<>() : new LinkedHashMap<>(records);
    }

    public static RepresentedBotRegistry fromState(Set<Long> allocatedEntityIds, Map<Long, RepresentedBotRecord> records) {
        return new RepresentedBotRegistry(allocatedEntityIds, records);
    }

    public static RepresentedBotRegistry empty() {
        return new RepresentedBotRegistry(Set.of(), Map.of());
    }

    public long reserveSlot() {
        for (long slotIndex = 1L; slotIndex <= 5000L; slotIndex++) {
            if (!allocatedEntityIds.contains(slotIndex)) {
                allocatedEntityIds.add(slotIndex);
                return slotIndex;
            }
        }
        return 0L;
    }

    public void storeEntry(long botEntityId, long roomSlot, RepresentedBotEntry botEntry) {
        if (botEntityId <= 0L || roomSlot <= 0L || botEntry == null) {
            return;
        }
        records.remove(botEntityId);
        records.put(botEntityId, RepresentedBotRecord.fromEntry(roomSlot, botEntry));
    }

    public void removeRecord(long botEntityId) {
        if (botEntityId <= 0L) {
            return;
        }
        records.remove(botEntityId);
        allocatedEntityIds.remove(botEntityId);
    }

    public RepresentedBotRecord record(long botEntityId) {
        return records.getOrDefault(botEntityId, RepresentedBotRecord.empty());
    }

    public boolean isEntityInRoom(long botEntityId, long roomSlot) {
        return botEntityId > 0L && roomSlot > 0L && record(botEntityId).roomSlot() == roomSlot;
    }

    public RepresentedBotIdentity identityFromEntityOrBotId(long requestedId) {
        RepresentedBotRecord bot = record(requestedId);
        if (bot.botId() > 0L) {
            return new RepresentedBotIdentity(requestedId, bot.botId());
        }
        long entityId = entityFromBotId(requestedId);
        return new RepresentedBotIdentity(entityId, requestedId);
    }

    public long entityFromBotId(long botId) {
        if (botId <= 0L || records.isEmpty()) {
            return 0L;
        }
        for (Record record : records()) {
            if (record.bot.botId() == botId) {
                return record.entityId;
            }
        }
        return 0L;
    }

    public List<Long> entityIdsForRoom(long roomSlot, long onlyBotId) {
        if (roomSlot <= 0L || records.isEmpty()) {
            return List.of();
        }
        List<Long> entityIds = new ArrayList<>();
        for (Record record : records()) {
            if (record.bot.roomSlot() == roomSlot
                && (onlyBotId <= 0L || record.bot.botId() == onlyBotId)) {
                entityIds.add(record.entityId);
            }
        }
        return List.copyOf(entityIds);
    }

    public void storePosition(long botEntityId, long positionX, long positionY, String positionZ, long positionR) {
        RepresentedBotRecord record = records.get(botEntityId);
        if (record == null) {
            return;
        }
        records.put(botEntityId, record.withPosition(positionX, positionY, positionZ, positionR));
    }

    public List<Long> allocatedEntityIds() {
        return List.copyOf(allocatedEntityIds);
    }

    public Map<Long, RepresentedBotRecord> recordsByEntityId() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(records));
    }

    private List<Record> records() {
        List<Record> result = new ArrayList<>();
        for (Map.Entry<Long, RepresentedBotRecord> record : records.entrySet()) {
            result.add(new Record(record.getKey(), record.getValue()));
        }
        return result;
    }

    private static final class Record {
        private final long entityId;
        private final RepresentedBotRecord bot;

        private Record(long entityId, RepresentedBotRecord bot) {
            this.entityId = entityId;
            this.bot = bot;
        }
    }

    public record RepresentedBotRecord(
        long roomSlot,
        long botId,
        String name,
        String motto,
        String speech,
        String responses,
        long positionX,
        long positionY,
        String positionZ,
        long positionR,
        String figure,
        long handleId,
        long handleActionId,
        String cacheAction,
        String speechSubmit,
        long allowWalk,
        long maxFieldsAway,
        List<String> serializedFields
    ) {
        public RepresentedBotRecord {
            serializedFields = List.copyOf(serializedFields);
        }

        private static RepresentedBotRecord empty() {
            return fromFields(new String[0]);
        }

        private static RepresentedBotRecord fromText(String recordText) {
            return fromFields(StringUtils.text(recordText).split("\2", -1));
        }

        private static RepresentedBotRecord fromEntry(long roomSlot, RepresentedBotEntry entry) {
            return new RepresentedBotRecord(
                roomSlot,
                entry.botId(),
                StringUtils.text(entry.name()),
                StringUtils.text(entry.motto()),
                StringUtils.text(entry.speech()),
                StringUtils.text(entry.responses()),
                entry.positionX(),
                entry.positionY(),
                StringUtils.text(entry.positionZ()),
                entry.positionR(),
                StringUtils.text(entry.figure()),
                entry.handleId(),
                entry.handleActionId(),
                StringUtils.text(entry.cacheAction()),
                StringUtils.text(entry.speechSubmit()),
                entry.allowWalk(),
                entry.maxFieldsAway(),
                List.of(
                    String.valueOf(roomSlot),
                    String.valueOf(entry.botId()),
                    StringUtils.text(entry.name()),
                    StringUtils.text(entry.motto()),
                    StringUtils.text(entry.speech()),
                    StringUtils.text(entry.responses()),
                    String.valueOf(entry.positionX()),
                    String.valueOf(entry.positionY()),
                    StringUtils.text(entry.positionZ()),
                    String.valueOf(entry.positionR()),
                    StringUtils.text(entry.figure()),
                    String.valueOf(entry.handleId()),
                    String.valueOf(entry.handleActionId()),
                    StringUtils.text(entry.cacheAction()),
                    StringUtils.text(entry.speechSubmit()),
                    String.valueOf(entry.allowWalk()),
                    String.valueOf(entry.maxFieldsAway())));
        }

        private static RepresentedBotRecord fromFields(String[] fields) {
            return new RepresentedBotRecord(
                number(fields, 0),
                number(fields, 1),
                field(fields, 2),
                field(fields, 3),
                field(fields, 4),
                field(fields, 5),
                number(fields, 6),
                number(fields, 7),
                field(fields, 8),
                number(fields, 9),
                field(fields, 10),
                number(fields, 11),
                number(fields, 12),
                field(fields, 13),
                field(fields, 14),
                number(fields, 15),
                number(fields, 16),
                List.of(fields));
        }

        private RepresentedBotRecord withPosition(long positionX, long positionY, String positionZ, long positionR) {
            if (serializedFields.size() < 10) {
                return this;
            }
            List<String> updatedFields = new ArrayList<>(serializedFields);
            updatedFields.set(6, String.valueOf(positionX));
            updatedFields.set(7, String.valueOf(positionY));
            updatedFields.set(8, StringUtils.text(positionZ));
            updatedFields.set(9, String.valueOf(positionR));
            return new RepresentedBotRecord(
                roomSlot,
                botId,
                name,
                motto,
                speech,
                responses,
                positionX,
                positionY,
                positionZ,
                positionR,
                figure,
                handleId,
                handleActionId,
                cacheAction,
                speechSubmit,
                allowWalk,
                maxFieldsAway,
                updatedFields);
        }

        private static String field(String[] fields, int index) {
            return StringUtils.field(fields, index);
        }

        private static long number(String[] fields, int index) {
            return NumberUtils.parseLong(field(fields, index));
        }
    }

    public record RepresentedBotIdentity(long entityId, long botId) {
    }
}
