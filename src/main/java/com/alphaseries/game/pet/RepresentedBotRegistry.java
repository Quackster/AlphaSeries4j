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
        this.allocatedEntityIds = new LinkedHashSet<>(allocatedEntityIds);
        this.records = new LinkedHashMap<>(records);
    }

    public static RepresentedBotRegistry fromLegacy(String allocatedEntityMarkers, String recordCache) {
        return new RepresentedBotRegistry(allocatedEntityIds(allocatedEntityMarkers), records(recordCache));
    }

    public static RepresentedBotRegistry empty() {
        return new RepresentedBotRegistry(Set.of(), Map.of());
    }

    public String allocatedEntityMarkers() {
        StringBuilder result = new StringBuilder();
        for (Long entityId : allocatedEntityIds) {
            result.append(marker(entityId));
        }
        return result.toString();
    }

    public String recordCache() {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<Long, RepresentedBotRecord> record : records.entrySet()) {
            result.append('[')
                .append(record.getKey())
                .append(':')
                .append(record.getValue().recordText())
                .append(']');
        }
        return result.toString();
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

    public void storeRecord(long botEntityId, String recordText) {
        if (botEntityId <= 0L) {
            return;
        }
        records.remove(botEntityId);
        records.put(botEntityId, RepresentedBotRecord.fromText(recordText));
    }

    public void removeRecord(long botEntityId) {
        if (botEntityId <= 0L) {
            return;
        }
        records.remove(botEntityId);
        allocatedEntityIds.remove(botEntityId);
    }

    public String recordText(long botEntityId) {
        if (botEntityId <= 0L) {
            return "";
        }
        RepresentedBotRecord record = records.get(botEntityId);
        return record == null ? "" : record.recordText();
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

    public String entitiesForRoom(long roomSlot, long onlyBotId) {
        if (roomSlot <= 0L || records.isEmpty()) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        for (Record record : records()) {
            if (record.bot.roomSlot() == roomSlot
                && (onlyBotId <= 0L || record.bot.botId() == onlyBotId)) {
                if (result.length() > 0) {
                    result.append('\r');
                }
                result.append(record.entityId);
            }
        }
        return result.toString();
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

    private static Set<Long> allocatedEntityIds(String allocatedEntityMarkers) {
        Set<Long> result = new LinkedHashSet<>();
        for (String part : StringUtils.text(allocatedEntityMarkers).split("\\]", -1)) {
            long entityId = NumberUtils.parseLong(part.replace("[", ""));
            if (entityId > 0L) {
                result.add(entityId);
            }
        }
        return result;
    }

    private static Map<Long, RepresentedBotRecord> records(String recordCache) {
        Map<Long, RepresentedBotRecord> result = new LinkedHashMap<>();
        for (String recordText : StringUtils.text(recordCache).split("\\[", -1)) {
            int payloadAt = recordText.indexOf(':');
            int endAt = recordText.indexOf(']');
            if (payloadAt > 0 && endAt > payloadAt) {
                long entityId = NumberUtils.parseLong(recordText.substring(0, payloadAt));
                if (entityId > 0L) {
                    result.put(entityId, RepresentedBotRecord.fromText(recordText.substring(payloadAt + 1, endAt)));
                }
            }
        }
        return result;
    }

    private static String marker(long entityId) {
        return "[" + entityId + "]";
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

        private String recordText() {
            return String.join("\2", serializedFields);
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
