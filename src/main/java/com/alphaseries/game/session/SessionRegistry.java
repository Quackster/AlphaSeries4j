package com.alphaseries.game.session;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public final class SessionRegistry {
    private final String rawCache;
    private final List<Record> records = new ArrayList<>();

    private SessionRegistry(String rawCache) {
        this.rawCache = StringUtils.text(rawCache);
        parseRecords();
    }

    public static SessionRegistry fromLegacyCache(String rawCache) {
        return new SessionRegistry(rawCache);
    }

    public String recordPayload(String recordPrefix, String recordId) {
        Record record = findRecord(StringUtils.text(recordPrefix), StringUtils.text(recordId));
        return record == null ? "" : record.payload;
    }

    public String recordField(String recordPrefix, String recordId, long columnIndex) {
        String payload = recordPayload(recordPrefix, recordId);
        if (payload.isEmpty()) {
            return "";
        }
        String[] fields = payload.split("\2", -1);
        if (columnIndex < 0 || columnIndex >= fields.length) {
            return "";
        }
        String[] valueParts = fields[(int) columnIndex].split("\\]", -1);
        return valueParts.length == 0 ? "" : valueParts[0];
    }

    public long recordLong(String recordPrefix, String recordId, long columnIndex) {
        return NumberUtils.parseLong(recordField(recordPrefix, recordId, columnIndex));
    }

    public String cacheField(String keyName, long columnIndex) {
        Record record = findRecord("", StringUtils.text(keyName));
        if (record == null || columnIndex < 0 || columnIndex >= record.fields.length) {
            return "";
        }
        return record.fields[(int) columnIndex];
    }

    public long cacheLong(String keyName, long columnIndex) {
        return NumberUtils.parseLong(cacheField(keyName, columnIndex));
    }

    public String linkedValue(String recordId, boolean useBracketCount) {
        if (rawCache.isEmpty()) {
            return "";
        }
        String marker = "\2" + StringUtils.text(recordId) + "]";
        String[] parts = rawCache.split(Pattern.quote(marker), -1);
        if (parts.length < 2) {
            return "";
        }
        String sectionText = parts[parts.length - 1];
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
                long userId = record.fields.length >= 1 ? NumberUtils.parseLong(record.fields[0]) : 0L;
                int socketIndex = record.fields.length >= 2 ? NumberUtils.parseInt(record.fields[1]) : 0;
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

    private void parseRecords() {
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

    private Record findRecord(String recordPrefix, String recordId) {
        String wanted = (recordPrefix + recordId).toLowerCase(Locale.ROOT);
        for (Record record : records) {
            if (record.key.toLowerCase(Locale.ROOT).equals(wanted)) {
                return record;
            }
        }
        return null;
    }

    private static final class Record {
        private final String key;
        private final String payload;
        private final String[] fields;

        private Record(String key, String payload) {
            this.key = StringUtils.text(key);
            this.payload = StringUtils.text(payload);
            this.fields = this.payload.split("\2", -1);
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
