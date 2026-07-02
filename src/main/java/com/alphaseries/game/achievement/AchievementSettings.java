package com.alphaseries.game.achievement;

import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class AchievementSettings {
    private final String questIdPayload;
    private final Map<Long, Achievement> rowsByIndex;

    private AchievementSettings(String questIdPayload, Iterable<Achievement> achievements) {
        this.questIdPayload = StringUtils.text(questIdPayload);
        this.rowsByIndex = rowsFromAchievements(achievements);
    }

    private AchievementSettings(String questIdPayload, List<IndexedAchievement> indexedAchievements) {
        this.questIdPayload = StringUtils.text(questIdPayload);
        this.rowsByIndex = rowsFromIndexedAchievements(indexedAchievements);
    }

    public static AchievementSettings empty() {
        return new AchievementSettings("", List.<Achievement>of());
    }

    public static AchievementSettings fromAchievements(String questIdPayload, Iterable<Achievement> achievements) {
        return new AchievementSettings(questIdPayload, achievements);
    }

    public static AchievementSettings fromIndexedAchievements(
        String questIdPayload,
        List<IndexedAchievement> indexedAchievements
    ) {
        return new AchievementSettings(questIdPayload, indexedAchievements);
    }

    public String questIdPayload() {
        return questIdPayload;
    }

    public Achievement achievementByIndex(long achievementIndex) {
        return rowsByIndex.get(achievementIndex);
    }

    public List<Achievement> achievements() {
        List<Achievement> achievements = new ArrayList<>();
        for (Achievement achievement : rowsByIndex.values()) {
            if (achievement != null) {
                achievements.add(achievement);
            }
        }
        return achievements;
    }

    public List<IndexedAchievement> indexedAchievements() {
        List<IndexedAchievement> achievements = new ArrayList<>();
        for (Map.Entry<Long, Achievement> entry : rowsByIndex.entrySet()) {
            if (entry.getValue() != null) {
                achievements.add(new IndexedAchievement(entry.getKey(), entry.getValue()));
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

    private static Map<Long, Achievement> rowsFromAchievements(Iterable<Achievement> achievements) {
        Map<Long, Achievement> parsedRows = new LinkedHashMap<>();
        long achievementIndex = 0L;
        if (achievements != null) {
            for (Achievement achievement : achievements) {
                if (achievement != null) {
                    parsedRows.put(achievementIndex, achievement);
                }
                achievementIndex++;
            }
        }
        return parsedRows;
    }

    private static Map<Long, Achievement> rowsFromIndexedAchievements(List<IndexedAchievement> achievements) {
        Map<Long, Achievement> parsedRows = new LinkedHashMap<>();
        if (achievements != null) {
            for (IndexedAchievement achievement : achievements) {
                if (achievement != null && achievement.achievement() != null) {
                    parsedRows.put(achievement.achievementIndex(), achievement.achievement());
                }
            }
        }
        return parsedRows;
    }
}
