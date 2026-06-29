package com.alphaseries.messages.outgoing;

import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.protocol.WireEncoding;

import java.util.Map;

public final class AchievementPayloads {
    private AchievementPayloads() {
    }

    public static String reward(long achievementIndex, String achievementRow, long badgeLevel, long badgeRowId) {
        String[] fields = text(achievementRow).split("\t", -1);
        if (fields.length < 7) {
            return "";
        }
        long achievementId = number(fields[0]);
        String badgePrefix = fields[1];
        long rewardIncrease = number(fields[3]);
        long levelTotal = number(fields[4]);
        long scoreIncrease = number(fields[5]);
        if (achievementId == 0L || badgePrefix.isEmpty()) {
            return "";
        }
        long normalizedBadgeLevel = badgeLevel <= 0L ? 1L : badgeLevel;
        String badgeId = badgePrefix + normalizedBadgeLevel;
        return PacketBuilder.message("Fu")
            .appendInt(achievementIndex)
            .appendInt(achievementId)
            .appendInt(badgeRowId)
            .appendString(badgeId)
            .appendInt(scoreIncrease)
            .appendInt(rewardIncrease)
            .appendString("HHH")
            .appendString(levelTotal)
            .build();
    }

    public static String award(String achievementRow) {
        String[] fields = text(achievementRow).split("\t", -1);
        if (fields.length < 7) {
            return "";
        }
        long rewardIncrease = number(fields[3]);
        long scoreIncrease = number(fields[5]);
        long rewardType = number(fields[6]);
        if (rewardIncrease == 0L && scoreIncrease == 0L) {
            return "";
        }
        return PacketBuilder.message("Fv")
            .appendInt(scoreIncrease)
            .appendInt(rewardIncrease)
            .appendInt(rewardType)
            .build();
    }

    public static String list(String achievementRows, Map<String, Long> currentLevelsByBadgePrefix) {
        PacketBuilder payload = PacketBuilder.create();
        long achievementCount = 0L;
        for (String row : text(achievementRows).split("\r", -1)) {
            if (!row.isEmpty()) {
                String[] fields = row.split("\t", -1);
                if (fields.length >= 7) {
                    long achievementId = number(fields[0]);
                    String badgePrefix = fields[1];
                    if (achievementId > 0L && !badgePrefix.isEmpty()) {
                        long progressRequired = number(fields[2]);
                        long rewardIncrease = number(fields[3]);
                        long levelTotal = number(fields[4]);
                        long scoreIncrease = number(fields[5]);
                        long rewardType = number(fields[6]);
                        if (levelTotal <= 0L) {
                            levelTotal = 1L;
                        }
                        long currentLevel = currentLevelsByBadgePrefix != null && currentLevelsByBadgePrefix.containsKey(badgePrefix)
                            ? currentLevelsByBadgePrefix.get(badgePrefix) : 0L;
                        if (currentLevel < 0L) {
                            currentLevel = 0L;
                        }
                        if (currentLevel > levelTotal) {
                            currentLevel = levelTotal;
                        }
                        long currentProgress = currentLevel > 0L ? progressRequired * currentLevel : 0L;
                        if (currentProgress < 0L) {
                            currentProgress = 0L;
                        }
                        payload.appendInt(achievementId)
                            .appendInt(currentLevel)
                            .appendInt(currentProgress)
                            .appendInt(progressRequired)
                            .appendInt(rewardIncrease)
                            .appendInt(scoreIncrease)
                            .appendInt(rewardType)
                            .appendInt(levelTotal)
                            .appendString(badgePrefix)
                            .appendString(currentLevel);
                        achievementCount++;
                    }
                }
            }
        }
        return PacketBuilder.message("Ft")
            .appendInt(achievementCount)
            .appendRaw(payload)
            .build();
    }

    private static long number(Object value) {
        return WireEncoding.parseLeadingLong(value);
    }

    private static String text(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}
