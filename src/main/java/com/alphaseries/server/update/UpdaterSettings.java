package com.alphaseries.server.update;

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
}
