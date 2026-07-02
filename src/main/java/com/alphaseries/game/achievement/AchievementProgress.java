package com.alphaseries.game.achievement;

import com.alphaseries.dao.mysql.UserDao;
import com.alphaseries.messages.outgoing.AchievementPayloads;
import com.alphaseries.util.NumberUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AchievementProgress {
    private AchievementProgress() {
    }

    public static AchievementProgressDecision decision(
        Iterable<AchievementSettings.IndexedAchievement> indexedAchievements,
        long achievementQuestId,
        Map<String, Long> currentLevelsByBadgePrefix,
        long currentProgress
    ) {
        Iterable<AchievementSettings.IndexedAchievement> rows = indexedAchievements == null
            ? List.<AchievementSettings.IndexedAchievement>of() : indexedAchievements;
        for (AchievementSettings.IndexedAchievement indexedAchievement : rows) {
            AchievementSettings.Achievement achievement = indexedAchievement.achievement();
            if (achievement.achievementId() == achievementQuestId) {
                long levelTotal = achievement.levelTotal();
                if (levelTotal <= 0L) {
                    levelTotal = 1L;
                }
                long currentLevel = currentLevelsByBadgePrefix != null
                    && currentLevelsByBadgePrefix.containsKey(achievement.badgePrefix())
                    ? currentLevelsByBadgePrefix.get(achievement.badgePrefix()) : 0L;
                if (!achievement.badgePrefix().isEmpty() && achievement.progressRequired() > 0L
                    && currentLevel >= 0L && currentLevel < levelTotal) {
                    long nextLevel = currentLevel + 1L;
                    long requiredProgress = achievement.progressRequired() * nextLevel;
                    return new AchievementProgressDecision(
                        indexedAchievement.achievementIndex(),
                        nextLevel,
                        requiredProgress,
                        currentProgress >= requiredProgress);
                }
                return AchievementProgressDecision.empty();
            }
        }
        return AchievementProgressDecision.empty();
    }

    public static Map<String, Long> currentLevels(
        String userId,
        Iterable<AchievementSettings.Achievement> achievements,
        UserDao users
    ) {
        Map<String, Long> result = new HashMap<>();
        long userIdValue = NumberUtils.parseLong(userId);
        Iterable<AchievementSettings.Achievement> rows = achievements == null
            ? List.<AchievementSettings.Achievement>of() : achievements;
        for (AchievementSettings.Achievement achievement : rows) {
            String badgePrefix = achievement.badgePrefix();
            if (!badgePrefix.isEmpty() && !result.containsKey(badgePrefix)) {
                long currentLevel = 0L;
                if (users != null && userIdValue > 0L) {
                    try {
                        currentLevel = users.badgeLevelByPrefix(userIdValue, badgePrefix);
                    } catch (Exception ignored) {
                        currentLevel = 0L;
                    }
                }
                result.put(badgePrefix, Math.max(0L, currentLevel));
            }
        }
        return result;
    }

    public static long representedProgress(String userId, long achievementQuestId, UserDao users) {
        if (users == null) {
            return 0L;
        }
        long userIdValue = NumberUtils.parseLong(userId);
        try {
            long progress;
            if (achievementQuestId == 1L) {
                progress = users.distinctVisitedRoomCount(userIdValue);
            } else if (achievementQuestId == 2L) {
                progress = users.respectReceived(userIdValue);
            } else if (achievementQuestId == 3L) {
                progress = users.respectGiven(userIdValue);
            } else if (achievementQuestId == 4L) {
                progress = users.onlineTime(userIdValue) / 60L;
            } else if (achievementQuestId == 6L) {
                progress = users.giftsGiven(userIdValue);
            } else if (achievementQuestId == 7L) {
                progress = users.giftsReceived(userIdValue);
            } else if (achievementQuestId == 8L) {
                progress = users.hcPeriods(userIdValue);
            } else if (achievementQuestId == 9L) {
                progress = users.hc2Periods(userIdValue);
            } else if (achievementQuestId == 11L) {
                progress = users.staffPickedAmount(userIdValue);
            } else {
                progress = users.achievementProgressSummary(userIdValue)
                    .map(UserDao.AchievementProgressSummary::respectReceived)
                    .orElse(0L);
            }
            return Math.max(0L, progress);
        } catch (Exception ignored) {
            return 0L;
        }
    }

    public static AchievementRewardGrant prepareRewardGrant(
        UserDao users,
        long userId,
        long achievementIndex,
        long badgeLevel,
        AchievementSettings.Achievement achievement
    )
        throws Exception {

        if (users == null || userId <= 0L || achievement == null || achievement.achievementId() == 0L) {
            return emptyRewardGrant();
        }
        long resolvedBadgeLevel = badgeLevel <= 0L ? 1L : badgeLevel;
        String badgePrefix = achievement.badgePrefix();
        if (badgePrefix.isEmpty()) {
            return emptyRewardGrant();
        }
        String badgeId = badgePrefix + resolvedBadgeLevel;
        users.deleteBadgesByPrefix(userId, badgePrefix);
        users.insertBadge(userId, badgeId);
        long badgeRowId = users.badgeRowId(userId, badgeId);
        return new AchievementRewardGrant(
            AchievementPayloads.reward(achievementIndex, achievement, resolvedBadgeLevel, badgeRowId),
            AchievementPayloads.award(achievement),
            achievement.rewardType(),
            achievement.rewardIncrease(),
            achievement.scoreIncrease());
    }

    private static AchievementRewardGrant emptyRewardGrant() {
        return new AchievementRewardGrant("", "", 0L, 0L, 0L);
    }
}
