package com.alphaseries.messages.outgoing;

import com.alphaseries.game.achievement.AchievementSettings;
import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.Map;

public final class AchievementPayloads {
    private AchievementPayloads() {
    }

    public static String reward(long achievementIndex, String achievementRow, long badgeLevel, long badgeRowId) {
        return reward(achievementIndex, AchievementSettings.achievement(achievementRow), badgeLevel, badgeRowId);
    }

    public static String reward(
        long achievementIndex,
        AchievementSettings.Achievement achievement,
        long badgeLevel,
        long badgeRowId
    ) {
        if (achievement == null) {
            return "";
        }
        if (achievement.achievementId() == 0L || achievement.badgePrefix().isEmpty()) {
            return "";
        }
        long normalizedBadgeLevel = badgeLevel <= 0L ? 1L : badgeLevel;
        String badgeId = achievement.badgePrefix() + normalizedBadgeLevel;
        return PacketBuilder.message("Fu")
            .appendInt(achievementIndex)
            .appendInt(achievement.achievementId())
            .appendInt(badgeRowId)
            .appendString(badgeId)
            .appendInt(achievement.scoreIncrease())
            .appendInt(achievement.rewardIncrease())
            .appendString("HHH")
            .appendString(achievement.levelTotal())
            .build();
    }

    public static String award(String achievementRow) {
        return award(AchievementSettings.achievement(achievementRow));
    }

    public static String award(AchievementSettings.Achievement achievement) {
        if (achievement == null) {
            return "";
        }
        if (achievement.rewardIncrease() == 0L && achievement.scoreIncrease() == 0L) {
            return "";
        }
        return PacketBuilder.message("Fv")
            .appendInt(achievement.scoreIncrease())
            .appendInt(achievement.rewardIncrease())
            .appendInt(achievement.rewardType())
            .build();
    }

    public static String list(String achievementRows, Map<String, Long> currentLevelsByBadgePrefix) {
        PacketBuilder payload = PacketBuilder.create();
        long achievementCount = 0L;
        for (String row : StringUtils.text(achievementRows).split("\r", -1)) {
            if (!row.isEmpty()) {
                String[] fields = row.split("\t", -1);
                if (fields.length >= 7) {
                    long achievementId = NumberUtils.parseLong(fields[0]);
                    String badgePrefix = fields[1];
                    if (achievementId > 0L && !badgePrefix.isEmpty()) {
                        long progressRequired = NumberUtils.parseLong(fields[2]);
                        long rewardIncrease = NumberUtils.parseLong(fields[3]);
                        long levelTotal = NumberUtils.parseLong(fields[4]);
                        long scoreIncrease = NumberUtils.parseLong(fields[5]);
                        long rewardType = NumberUtils.parseLong(fields[6]);
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

}
