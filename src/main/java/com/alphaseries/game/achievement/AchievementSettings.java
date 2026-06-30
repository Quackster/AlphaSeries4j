package com.alphaseries.game.achievement;

import com.alphaseries.util.StringUtils;
import com.alphaseries.util.NumberUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class AchievementSettings {
    private final String questIdPayload;
    private final Map<Long, AchievementRow> rowsByIndex;

    private AchievementSettings(String questIdPayload, Object rows) {
        this.questIdPayload = StringUtils.text(questIdPayload);
        this.rowsByIndex = parseRows(rows);
    }

    private AchievementSettings(String questIdPayload, Iterable<Achievement> achievements) {
        this.questIdPayload = StringUtils.text(questIdPayload);
        this.rowsByIndex = rowsFromAchievements(achievements);
    }

    public static AchievementSettings empty() {
        return new AchievementSettings("", "");
    }

    public static AchievementSettings fromLegacy(String questIdPayload, Object rows) {
        if (rows instanceof AchievementSettings settings) {
            return settings;
        }
        return new AchievementSettings(questIdPayload, rows);
    }

    public static AchievementSettings fromAchievements(String questIdPayload, Iterable<Achievement> achievements) {
        return new AchievementSettings(questIdPayload, achievements);
    }

    public String questIdPayload() {
        return questIdPayload;
    }

    public String rowsAsText() {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (AchievementRow row : rowsByIndex.values()) {
            if (row.includeInRowsAsText()) {
                if (!first) {
                    result.append('\r');
                }
                result.append(row.rowText());
                first = false;
            }
        }
        return result.toString();
    }

    public String rowByIndex(long achievementIndex) {
        if (achievementIndex < 0L) {
            return "";
        }
        AchievementRow row = rowsByIndex.get(achievementIndex);
        return row == null ? "" : row.rowText();
    }

    public Achievement achievementByIndex(long achievementIndex) {
        AchievementRow row = rowsByIndex.get(achievementIndex);
        return row == null ? null : row.achievement();
    }

    public List<Achievement> achievements() {
        List<Achievement> achievements = new ArrayList<>();
        for (AchievementRow row : rowsByIndex.values()) {
            if (row.achievement() != null) {
                achievements.add(row.achievement());
            }
        }
        return achievements;
    }

    public List<IndexedAchievement> indexedAchievements() {
        List<IndexedAchievement> achievements = new ArrayList<>();
        for (Map.Entry<Long, AchievementRow> entry : rowsByIndex.entrySet()) {
            if (entry.getValue().achievement() != null) {
                achievements.add(new IndexedAchievement(entry.getKey(), entry.getValue().achievement()));
            }
        }
        return achievements;
    }

    public List<AchievementRow> rows() {
        return List.copyOf(rowsByIndex.values());
    }

    public static List<Achievement> achievements(String rowsText) {
        List<Achievement> achievements = new ArrayList<>();
        for (String row : StringUtils.text(rowsText).split("\r", -1)) {
            if (!row.isEmpty()) {
                Achievement achievement = achievement(row);
                if (achievement != null) {
                    achievements.add(achievement);
                }
            }
        }
        return achievements;
    }

    public static List<IndexedAchievement> indexedAchievements(Iterable<Achievement> achievements) {
        List<IndexedAchievement> indexedAchievements = new ArrayList<>();
        long achievementIndex = 0L;
        if (achievements != null) {
            for (Achievement achievement : achievements) {
                if (achievement != null) {
                    indexedAchievements.add(new IndexedAchievement(achievementIndex, achievement));
                }
                achievementIndex++;
            }
        }
        return indexedAchievements;
    }

    public static List<IndexedAchievement> indexedAchievements(String rowsText) {
        List<IndexedAchievement> achievements = new ArrayList<>();
        long achievementIndex = 0L;
        for (String row : StringUtils.text(rowsText).split("\r", -1)) {
            if (!row.isEmpty()) {
                Achievement achievement = achievement(row);
                if (achievement != null) {
                    achievements.add(new IndexedAchievement(achievementIndex, achievement));
                }
                achievementIndex++;
            }
        }
        return achievements;
    }

    public static Achievement achievement(String row) {
        String[] fields = StringUtils.text(row).split("\t", -1);
        if (fields.length < 7) {
            return null;
        }
        return new Achievement(
            NumberUtils.parseLong(fields[0]),
            fields[1],
            NumberUtils.parseLong(fields[2]),
            NumberUtils.parseLong(fields[3]),
            NumberUtils.parseLong(fields[4]),
            NumberUtils.parseLong(fields[5]),
            NumberUtils.parseLong(fields[6]));
    }

    public record Achievement(
        long achievementId,
        String badgePrefix,
        long progressRequired,
        long rewardIncrease,
        long levelTotal,
        long scoreIncrease,
        long rewardType
    ) {
    }

    public record IndexedAchievement(long achievementIndex, Achievement achievement) {
    }

    public record AchievementRow(Achievement achievement, String rowText, boolean includeInRowsAsText) {
        public AchievementRow {
            rowText = StringUtils.text(rowText);
        }
    }

    private static Map<Long, AchievementRow> parseRows(Object rows) {
        if (rows instanceof String[][] values) {
            Map<Long, AchievementRow> parsedRows = new LinkedHashMap<>();
            for (int index = 0; index < values.length; index++) {
                if (values[index] != null && values[index].length > 0) {
                    putRow(parsedRows, index, String.join("\t", values[index]), true);
                }
            }
            return parsedRows;
        }
        if (rows instanceof String[] values) {
            Map<Long, AchievementRow> parsedRows = new LinkedHashMap<>();
            for (int index = 0; index < values.length; index++) {
                putRow(parsedRows, index, StringUtils.text(values[index]), true);
            }
            return parsedRows;
        }
        return parseRowText(StringUtils.text(rows));
    }

    private static Map<Long, AchievementRow> parseRowText(String rowsText) {
        Map<Long, AchievementRow> parsedRows = new LinkedHashMap<>();
        long achievementIndex = 0L;
        for (String row : StringUtils.text(rowsText).split("\r", -1)) {
            putRow(parsedRows, achievementIndex, row, true);
            achievementIndex++;
        }
        return parsedRows;
    }

    private static Map<Long, AchievementRow> rowsFromAchievements(Iterable<Achievement> achievements) {
        Map<Long, AchievementRow> parsedRows = new LinkedHashMap<>();
        long achievementIndex = 0L;
        if (achievements != null) {
            for (Achievement achievement : achievements) {
                if (achievement != null) {
                    parsedRows.put(achievementIndex, new AchievementRow(achievement, rowText(achievement), true));
                }
                achievementIndex++;
            }
        }
        return parsedRows;
    }

    private static void putRow(Map<Long, AchievementRow> parsedRows, long achievementIndex, String rowText,
                               boolean includeInRowsAsText) {
        String normalizedRow = StringUtils.text(rowText);
        Achievement achievement = normalizedRow.isEmpty() ? null : achievement(normalizedRow);
        parsedRows.put(achievementIndex, new AchievementRow(achievement, normalizedRow, includeInRowsAsText));
    }

    private static String rowText(Achievement achievement) {
        return achievement.achievementId() + "\t" + StringUtils.text(achievement.badgePrefix()) + "\t"
            + achievement.progressRequired() + "\t" + achievement.rewardIncrease() + "\t"
            + achievement.levelTotal() + "\t" + achievement.scoreIncrease() + "\t" + achievement.rewardType();
    }
}
