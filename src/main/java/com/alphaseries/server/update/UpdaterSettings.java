package com.alphaseries.server.update;

import com.alphaseries.util.StringUtils;

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

    public static UpdaterSettings fromEntries(String executableName, List<UpdateEntry> entries, String updateSql) {
        return new UpdaterSettings(executableName, entries == null ? List.of() : entries, updateSql);
    }

    public static UpdaterSettings empty() {
        return new UpdaterSettings("", List.of(), "");
    }

    public String executableName() {
        return executableName;
    }

    public String executableNameOr(String fallbackName) {
        return !executableName.isEmpty() ? executableName : StringUtils.text(fallbackName);
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

    public String updateSql() {
        return updateSql;
    }

    public static String normalizeUpdateSql(String updateSql) {
        return StringUtils.text(updateSql).replace("\r", "").replaceAll("(?i)INSERT INTO", "INSERT IGNORE INTO");
    }

    public String normalizedUpdateSql() {
        return normalizeUpdateSql(updateSql);
    }

    public record UpdateEntry(String sourceText, String id, String title, String bodyText, long featureMode,
                              long featureCost, boolean valid) {
        public UpdateEntry {
            sourceText = StringUtils.text(sourceText);
            id = StringUtils.text(id);
            title = StringUtils.text(title);
            bodyText = StringUtils.text(bodyText);
        }

        public static UpdateEntry fromFields(
            String id,
            String title,
            String bodyText,
            long featureMode,
            long featureCost
        ) {
            String normalizedId = StringUtils.text(id);
            String normalizedTitle = StringUtils.text(title);
            String normalizedBodyText = StringUtils.text(bodyText);
            return new UpdateEntry(
                normalizedId + "\t" + normalizedTitle + "\t" + normalizedBodyText + "\t" + featureMode + "\t" + featureCost,
                normalizedId,
                normalizedTitle,
                normalizedBodyText,
                featureMode,
                featureCost,
                true);
        }

    }
}
