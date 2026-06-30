package com.alphaseries.messages.outgoing;

import com.alphaseries.game.achievement.AchievementSettings;
import com.alphaseries.protocol.PacketBuilder;

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
        return list(AchievementSettings.achievements(achievementRows), currentLevelsByBadgePrefix);
    }

    public static String list(
        Iterable<AchievementSettings.Achievement> achievements,
        Map<String, Long> currentLevelsByBadgePrefix
    ) {
        PacketBuilder payload = PacketBuilder.create();
        long achievementCount = 0L;
        for (AchievementSettings.Achievement achievement : achievements == null
            ? java.util.List.<AchievementSettings.Achievement>of() : achievements) {
            if (achievement != null && achievement.achievementId() > 0L && !achievement.badgePrefix().isEmpty()) {
                long levelTotal = achievement.levelTotal();
                if (levelTotal <= 0L) {
                    levelTotal = 1L;
                }
                long currentLevel = currentLevelsByBadgePrefix != null
                    && currentLevelsByBadgePrefix.containsKey(achievement.badgePrefix())
                    ? currentLevelsByBadgePrefix.get(achievement.badgePrefix()) : 0L;
                if (currentLevel < 0L) {
                    currentLevel = 0L;
                }
                if (currentLevel > levelTotal) {
                    currentLevel = levelTotal;
                }
                long currentProgress = currentLevel > 0L ? achievement.progressRequired() * currentLevel : 0L;
                if (currentProgress < 0L) {
                    currentProgress = 0L;
                }
                payload.appendInt(achievement.achievementId())
                    .appendInt(currentLevel)
                    .appendInt(currentProgress)
                    .appendInt(achievement.progressRequired())
                    .appendInt(achievement.rewardIncrease())
                    .appendInt(achievement.scoreIncrease())
                    .appendInt(achievement.rewardType())
                    .appendInt(levelTotal)
                    .appendString(achievement.badgePrefix())
                    .appendString(currentLevel);
                achievementCount++;
            }
        }
        return PacketBuilder.message("Ft")
            .appendInt(achievementCount)
            .appendRaw(payload)
            .build();
    }

}
