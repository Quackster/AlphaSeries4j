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

    private SessionRegistry(List<SessionRecord> records, List<LinkedSessionSection> linkedSections) {
        if (records != null) {
            for (SessionRecord record : records) {
                if (record != null) {
                    this.records.add(new Record(record.key(), record.payload()));
                }
            }
        }
        if (linkedSections != null) {
            for (LinkedSessionSection section : linkedSections) {
                if (section != null) {
                    this.linkedSections.add(new LinkedSection(
                        StringUtils.text(section.recordId()),
                        StringUtils.text(section.text())));
                }
            }
        }
    }

    public static SessionRegistry fromEntries(List<SessionRecord> records, List<LinkedSessionSection> linkedSections) {
        return new SessionRegistry(records, linkedSections);
    }

    public static SessionRegistry empty() {
        return new SessionRegistry(List.of(), List.of());
    }

    String recordField(String recordPrefix, String recordId, long columnIndex) {
        Record record = findRecord(StringUtils.text(recordPrefix), StringUtils.text(recordId));
        if (record == null || columnIndex < 0 || columnIndex >= record.fieldCount()) {
            return "";
        }
        String[] valueParts = record.field((int) columnIndex).split("\\]", -1);
        return valueParts.length == 0 ? "" : valueParts[0];
    }

    long recordLong(String recordPrefix, String recordId, long columnIndex) {
        return NumberUtils.parseLong(recordField(recordPrefix, recordId, columnIndex));
    }

    long userIdBySocket(int socketIndex) {
        if (socketIndex <= 0) {
            return 0L;
        }
        return recordLong("1:", String.valueOf(socketIndex), 0);
    }

    private String cacheField(String keyName, long columnIndex) {
        Record record = findRecord("", StringUtils.text(keyName));
        if (record == null || columnIndex < 0 || columnIndex >= record.fieldCount()) {
            return "";
        }
        return record.field((int) columnIndex);
    }

    long cacheLong(String keyName, long columnIndex) {
        return NumberUtils.parseLong(cacheField(keyName, columnIndex));
    }

    private String linkedValue(String recordId, boolean useBracketCount) {
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

    long linkedLong(String recordId, boolean useBracketCount) {
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

    void storeSocketSession(int socketIndex, String sessionRecord) {
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

    public record SessionRecord(String key, String payload) {
        public SessionRecord {
            key = StringUtils.text(key);
            payload = StringUtils.text(payload);
        }
    }

    public record LinkedSessionSection(String recordId, String text) {
        public LinkedSessionSection {
            recordId = StringUtils.text(recordId);
            text = StringUtils.text(text);
        }
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
