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

    public static RepresentedBotRegistry empty() {
        return new RepresentedBotRegistry("", "");
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

    public RepresentedBotRecord record(long botEntityId) {
        return RepresentedBotRecord.fromFields(recordText(botEntityId).split("\2", -1));
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
        if (botId <= 0L || recordCache.isEmpty()) {
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
        if (roomSlot <= 0L || recordCache.isEmpty()) {
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
                result.add(new Record(entityId, RepresentedBotRecord.fromFields(fields)));
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
        long maxFieldsAway
    ) {
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
                number(fields, 16));
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
