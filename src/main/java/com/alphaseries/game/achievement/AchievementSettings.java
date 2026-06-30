package com.alphaseries.game.achievement;

import com.alphaseries.util.StringUtils;
import com.alphaseries.util.NumberUtils;

public final class AchievementSettings {
    private final String questIdPayload;
    private final Object rows;

    private AchievementSettings(String questIdPayload, Object rows) {
        this.questIdPayload = StringUtils.text(questIdPayload);
        this.rows = rows == null ? "" : rows;
    }

    public static AchievementSettings fromLegacy(String questIdPayload, Object rows) {
        return new AchievementSettings(questIdPayload, rows);
    }

    public String questIdPayload() {
        return questIdPayload;
    }

    public String rowsAsText() {
        if (rows instanceof String[][] values) {
            StringBuilder result = new StringBuilder();
            for (String[] row : values) {
                if (row != null && row.length > 0) {
                    if (result.length() > 0) {
                        result.append('\r');
                    }
                    result.append(String.join("\t", row));
                }
            }
            return result.toString();
        }
        if (rows instanceof String[] values) {
            return String.join("\r", values);
        }
        return StringUtils.text(rows);
    }

    public String rowByIndex(long achievementIndex) {
        if (achievementIndex < 0L) {
            return "";
        }
        if (rows instanceof String[][] values) {
            return achievementIndex < values.length && values[(int) achievementIndex] != null
                ? String.join("\t", values[(int) achievementIndex]) : "";
        }
        String[] textRows = rowsAsText().split("\r", -1);
        return achievementIndex < textRows.length ? textRows[(int) achievementIndex] : "";
    }

    public Achievement achievementByIndex(long achievementIndex) {
        return achievement(rowByIndex(achievementIndex));
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
}
