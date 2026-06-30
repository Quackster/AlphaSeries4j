package com.alphaseries.server.update;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class UpdaterSettings {
    private final String executableName;
    private final List<UpdateEntry> entries;
    private final String updateSql;

    private UpdaterSettings(String executableName, List<UpdateEntry> entries, String updateSql) {
        this.executableName = StringUtils.text(executableName);
        this.entries = List.copyOf(entries);
        this.updateSql = StringUtils.text(updateSql);
    }

    public static UpdaterSettings fromLegacy(String executableName, String updateRows, String updateSql) {
        return new UpdaterSettings(executableName, parseEntries(updateRows), updateSql);
    }

    public static UpdaterSettings fromEntries(String executableName, List<UpdateEntry> entries, String updateSql) {
        return new UpdaterSettings(executableName, entries == null ? List.of() : entries, updateSql);
    }

    public static UpdaterSettings empty() {
        return new UpdaterSettings("", parseEntries(""), "");
    }

    public String executableName() {
        return executableName;
    }

    public String executableNameOr(String fallbackName) {
        return !executableName.isEmpty() ? executableName : StringUtils.text(fallbackName);
    }

    public String[] updateEntries() {
        String[] result = new String[entries.size()];
        for (int index = 0; index < entries.size(); index++) {
            result[index] = entries.get(index).rawText();
        }
        return result;
    }

    public UpdateEntry[] entries() {
        return entries.toArray(UpdateEntry[]::new);
    }

    public List<UpdateEntry> entryList() {
        return List.copyOf(entries);
    }

    public long updateCountOrOne() {
        long updateCount = entries.size() - 1L;
        return updateCount <= 0L ? 1L : updateCount;
    }

    public boolean hasUpdateSql() {
        return !updateSql.isEmpty();
    }

    public static String normalizeUpdateSql(String updateSql) {
        return StringUtils.text(updateSql).replace("\r", "").replaceAll("(?i)INSERT INTO", "INSERT IGNORE INTO");
    }

    public String normalizedUpdateSql() {
        return normalizeUpdateSql(updateSql);
    }

    private static List<UpdateEntry> parseEntries(String updateRows) {
        List<UpdateEntry> parsedEntries = new ArrayList<>();
        for (String row : StringUtils.text(updateRows).split("\n", -1)) {
            parsedEntries.add(UpdateEntry.fromLegacyRow(row));
        }
        return parsedEntries;
    }

    public record UpdateEntry(String rawText, String id, String title, String bodyText, long featureMode,
                              long featureCost, boolean valid) {
        public UpdateEntry {
            rawText = StringUtils.text(rawText);
            id = StringUtils.text(id);
            title = StringUtils.text(title);
            bodyText = StringUtils.text(bodyText);
        }

        public static UpdateEntry fromLegacyRow(String rowText) {
            String row = StringUtils.text(rowText);
            String[] fields = row.split("\t", -1);
            return new UpdateEntry(
                row,
                field(fields, 0),
                field(fields, 1),
                field(fields, 2),
                fieldNumber(fields, 3),
                fieldNumber(fields, 4),
                fields.length >= 3);
        }

        private static String field(String[] fields, int index) {
            return index < fields.length ? fields[index] : "";
        }

        private static long fieldNumber(String[] fields, int index) {
            return index < fields.length ? NumberUtils.parseLong(fields[index]) : 0L;
        }
    }
}
