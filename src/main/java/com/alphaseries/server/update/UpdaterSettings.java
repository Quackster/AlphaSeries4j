package com.alphaseries.server.update;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

public final class UpdaterSettings {
    private final String executableName;
    private final String updateRows;
    private final String updateSql;

    private UpdaterSettings(String executableName, String updateRows, String updateSql) {
        this.executableName = StringUtils.text(executableName);
        this.updateRows = StringUtils.text(updateRows);
        this.updateSql = StringUtils.text(updateSql);
    }

    public static UpdaterSettings fromLegacy(String executableName, String updateRows, String updateSql) {
        return new UpdaterSettings(executableName, updateRows, updateSql);
    }

    public String executableName() {
        return executableName;
    }

    public String executableNameOr(String fallbackName) {
        return !executableName.isEmpty() ? executableName : StringUtils.text(fallbackName);
    }

    public String[] updateEntries() {
        return updateRows.split("\n", -1);
    }

    public UpdateEntry[] entries() {
        String[] rows = updateEntries();
        UpdateEntry[] entries = new UpdateEntry[rows.length];
        for (int index = 0; index < rows.length; index++) {
            entries[index] = UpdateEntry.fromLegacyRow(rows[index]);
        }
        return entries;
    }

    public long updateCountOrOne() {
        long updateCount = updateEntries().length - 1L;
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
