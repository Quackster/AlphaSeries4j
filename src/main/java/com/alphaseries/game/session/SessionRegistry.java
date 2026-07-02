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
        String value = record.field((int) columnIndex);
        int bracketAt = value.indexOf(']');
        return bracketAt < 0 ? value : value.substring(0, bracketAt);
    }

    long recordLong(String recordPrefix, String recordId, long columnIndex) {
        return NumberUtils.parseLong(recordField(recordPrefix, recordId, columnIndex));
    }

    String socketUserId(long socketIndex) {
        return recordField("0:", String.valueOf(socketIndex), 0);
    }

    long socketUserIdValue(long socketIndex) {
        return recordLong("0:", String.valueOf(socketIndex), 0);
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

    long cacheLong(long keyName, long columnIndex) {
        return cacheLong(String.valueOf(keyName), columnIndex);
    }

    private String linkedValue(String recordId, boolean useBracketCount) {
        LinkedSection section = linkedSection(StringUtils.text(recordId));
        if (section == null) {
            return "";
        }
        String sectionText = section.text();
        int targetIndex = countOccurrences(sectionText, '[');
        char delimiter = useBracketCount ? '\1' : '\0';
        return StringUtils.indexedFields(sectionText, delimiter).text(targetIndex);
    }

    long linkedLong(String recordId, boolean useBracketCount) {
        return NumberUtils.parseLong(linkedValue(recordId, useBracketCount));
    }

    long linkedLong(long recordId, boolean useBracketCount) {
        return linkedLong(String.valueOf(recordId), useBracketCount);
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
        private final StringUtils.IndexedFields fields;

        private Record(String key, String payload) {
            this.key = StringUtils.text(key);
            this.payload = StringUtils.text(payload);
            this.fields = StringUtils.indexedFields(payload, '\2');
        }

        private int fieldCount() {
            return fields.fieldCount();
        }

        private String field(int index) {
            return fields.text(index);
        }
    }

    private static int countOccurrences(String text, char character) {
        String value = StringUtils.text(text);
        int count = 0;
        int characterAt = value.indexOf(character);
        while (characterAt >= 0) {
            count++;
            characterAt = value.indexOf(character, characterAt + 1);
        }
        return count;
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
