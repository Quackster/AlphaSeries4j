package com.alphaseries.game.achievement;

import com.alphaseries.dao.mysql.UserDao;
import com.alphaseries.messages.outgoing.AchievementPayloads;
import com.alphaseries.util.NumberUtils;

import java.util.List;

public final class AchievementLookups {
    private AchievementLookups() {
    }

    /**
     * Original function: Proc_6_204_7D82E0.
     */
    public static AchievementRewardGrant grantReward(
        String userId,
        long achievementIndex,
        long badgeLevel,
        AchievementSettings settings,
        UserDao users
    ) {
        try {
            long userIdValue = NumberUtils.parseLong(userId);
            AchievementSettings effectiveSettings = settings == null ? AchievementSettings.empty() : settings;
            AchievementSettings.Achievement achievement = effectiveSettings.achievementByIndex(achievementIndex);
            if (userIdValue <= 0L || achievement == null || users == null) {
                return emptyGrant();
            }
            AchievementRewardGrant grant = AchievementProgress.prepareRewardGrant(
                users, userIdValue, achievementIndex, badgeLevel, achievement);
            if (grant.valid() && grant.hasAward()) {
                users.addAchievementReward(
                    userIdValue,
                    grant.rewardType(),
                    grant.rewardIncrease(),
                    grant.scoreIncrease());
            }
            return grant;
        } catch (Exception ignored) {
            return emptyGrant();
        }
    }

    /**
     * Original function: Proc_6_205_7D9780.
     */
    public static AchievementRewardGrant advanceProgress(
        String userId,
        long achievementQuestId,
        AchievementSettings settings,
        UserDao users
    ) {
        try {
            AchievementSettings effectiveSettings = settings == null ? AchievementSettings.empty() : settings;
            List<AchievementSettings.Achievement> achievements = effectiveSettings.achievements();
            if (NumberUtils.parseLong(userId) <= 0L || achievementQuestId <= 0L || achievements.isEmpty()) {
                return emptyGrant();
            }
            AchievementProgressDecision decision = AchievementProgress.decision(
                effectiveSettings.indexedAchievements(),
                achievementQuestId,
                AchievementProgress.currentLevels(userId, achievements, users),
                AchievementProgress.representedProgress(userId, achievementQuestId, users));
            if (!decision.shouldReward()) {
                return emptyGrant();
            }
            return grantReward(userId, decision.achievementIndex(), decision.nextLevel(), effectiveSettings, users);
        } catch (Exception ignored) {
            return emptyGrant();
        }
    }

    /**
     * Original function: Proc_6_206_7DA450.
     */
    public static String listPayload(String userId, AchievementSettings settings, UserDao users) {
        try {
            AchievementSettings effectiveSettings = settings == null ? AchievementSettings.empty() : settings;
            List<AchievementSettings.Achievement> achievements = effectiveSettings.achievements();
            if (NumberUtils.parseLong(userId) <= 0L || achievements.isEmpty()) {
                return "";
            }
            return AchievementPayloads.list(
                achievements,
                AchievementProgress.currentLevels(userId, achievements, users));
        } catch (Exception ignored) {
            return "";
        }
    }

    private static AchievementRewardGrant emptyGrant() {
        return new AchievementRewardGrant("", "", 0L, 0L, 0L);
    }
}
