package com.alphaseries.game.session;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public final class SessionRegistry {
    private final List<Record> records = new ArrayList<>();
    private final List<LinkedSection> linkedSections = new ArrayList<>();

    private SessionRegistry(String rawCache) {
        String cacheText = StringUtils.text(rawCache);
        parseRecords(cacheText);
        parseLinkedSections(cacheText);
    }

    public static SessionRegistry fromLegacyCache(String rawCache) {
        return new SessionRegistry(rawCache);
    }

    public static SessionRegistry empty() {
        return new SessionRegistry("");
    }

    public String recordPayload(String recordPrefix, String recordId) {
        Record record = findRecord(StringUtils.text(recordPrefix), StringUtils.text(recordId));
        return record == null ? "" : record.payload;
    }

    public String recordField(String recordPrefix, String recordId, long columnIndex) {
        Record record = findRecord(StringUtils.text(recordPrefix), StringUtils.text(recordId));
        if (record == null || columnIndex < 0 || columnIndex >= record.fieldCount()) {
            return "";
        }
        String[] valueParts = record.field((int) columnIndex).split("\\]", -1);
        return valueParts.length == 0 ? "" : valueParts[0];
    }

    public long recordLong(String recordPrefix, String recordId, long columnIndex) {
        return NumberUtils.parseLong(recordField(recordPrefix, recordId, columnIndex));
    }

    public String cacheField(String keyName, long columnIndex) {
        Record record = findRecord("", StringUtils.text(keyName));
        if (record == null || columnIndex < 0 || columnIndex >= record.fieldCount()) {
            return "";
        }
        return record.field((int) columnIndex);
    }

    public long cacheLong(String keyName, long columnIndex) {
        return NumberUtils.parseLong(cacheField(keyName, columnIndex));
    }

    public String linkedValue(String recordId, boolean useBracketCount) {
        LinkedSection section = linkedSection(StringUtils.text(recordId));
        if (section == null) {
            return "";
        }
        String sectionText = section.text();
        String[] bracketParts = sectionText.split("\\[", -1);
        int targetIndex = bracketParts.length - 1;
        String[] valueParts = useBracketCount ? sectionText.split("\1", -1) : sectionText.split("\0", -1);
        if (targetIndex < 0 || targetIndex >= valueParts.length) {
            return "";
        }
        return valueParts[targetIndex];
    }

    public long linkedLong(String recordId, boolean useBracketCount) {
        return NumberUtils.parseLong(linkedValue(recordId, useBracketCount));
    }

    public List<SocketSession> socketSessions() {
        List<SocketSession> sessions = new ArrayList<>();
        for (Record record : records) {
            if (record.key.startsWith("1:")) {
                long userId = record.fieldCount() >= 1 ? NumberUtils.parseLong(record.field(0)) : 0L;
                int socketIndex = record.fieldCount() >= 2 ? NumberUtils.parseInt(record.field(1)) : 0;
                sessions.add(new SocketSession(userId, socketIndex));
            }
        }
        return sessions;
    }

    public void storeSocketSession(int socketIndex, String sessionRecord) {
        if (socketIndex <= 0 || StringUtils.text(sessionRecord).isEmpty()) {
            return;
        }
        String key = "1:" + socketIndex;
        for (Iterator<Record> iterator = records.iterator(); iterator.hasNext();) {
            if (iterator.next().key.equals(key)) {
                iterator.remove();
            }
        }
        records.add(new Record(key, sessionRecord));
    }

    public String toLegacyCache() {
        StringBuilder cache = new StringBuilder();
        for (Record record : records) {
            cache.append('[').append(record.key).append('\1').append(record.payload).append(']');
        }
        return cache.toString();
    }

    private void parseRecords(String rawCache) {
        if (rawCache.isEmpty()) {
            return;
        }
        for (String part : rawCache.split("\\[", -1)) {
            if (part.isEmpty()) {
                continue;
            }
            int separator = part.indexOf('\1');
            if (separator < 0) {
                continue;
            }
            String key = part.substring(0, separator);
            String payload = part.substring(separator + 1);
            int end = payload.indexOf(']');
            if (end >= 0) {
                payload = payload.substring(0, end);
            }
            records.add(new Record(key, payload));
        }
    }

    private void parseLinkedSections(String rawCache) {
        int markerAt = rawCache.indexOf('\2');
        while (markerAt >= 0) {
            int idEndAt = rawCache.indexOf(']', markerAt + 1);
            if (idEndAt < 0) {
                return;
            }
            String recordId = rawCache.substring(markerAt + 1, idEndAt);
            String sectionText = rawCache.substring(idEndAt + 1);
            linkedSections.add(new LinkedSection(recordId, sectionText));
            markerAt = rawCache.indexOf('\2', markerAt + 1);
        }
    }

    private Record findRecord(String recordPrefix, String recordId) {
        String wanted = (recordPrefix + recordId).toLowerCase(Locale.ROOT);
        for (Record record : records) {
            if (record.key.toLowerCase(Locale.ROOT).equals(wanted)) {
                return record;
            }
        }
        return null;
    }

    private LinkedSection linkedSection(String recordId) {
        LinkedSection result = null;
        for (LinkedSection section : linkedSections) {
            if (section.recordId().equals(recordId)) {
                result = section;
            }
        }
        return result;
    }

    private static final class Record {
        private final String key;
        private final String payload;
        private final List<String> fields;

        private Record(String key, String payload) {
            this.key = StringUtils.text(key);
            this.payload = StringUtils.text(payload);
            this.fields = List.of(this.payload.split("\2", -1));
        }

        private int fieldCount() {
            return fields.size();
        }

        private String field(int index) {
            return index >= 0 && index < fields.size() ? fields.get(index) : "";
        }
    }

    private record LinkedSection(String recordId, String text) {
    }

    public static final class SocketSession {
        private final long userId;
        private final int socketIndex;

        private SocketSession(long userId, int socketIndex) {
            this.userId = userId;
            this.socketIndex = socketIndex;
        }

        public long userId() {
            return userId;
        }

        public int socketIndex() {
            return socketIndex;
        }
    }
}
