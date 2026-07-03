package com.alphaseries.game.achievement;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class AchievementSettings {
    private final String questIdPayload;
    private final Map<Long, Achievement> rowsByIndex;

    private AchievementSettings(Iterable<Achievement> achievements) {
        this.questIdPayload = questIdPayload(achievements);
        this.rowsByIndex = rowsFromAchievements(achievements);
    }

    private AchievementSettings(List<IndexedAchievement> indexedAchievements) {
        this.questIdPayload = questIdPayloadFromIndexedAchievements(indexedAchievements);
        this.rowsByIndex = rowsFromIndexedAchievements(indexedAchievements);
    }

    public static AchievementSettings empty() {
        return new AchievementSettings(List.<Achievement>of());
    }

    public static AchievementSettings fromAchievements(Iterable<Achievement> achievements) {
        return new AchievementSettings(achievements);
    }

    public static AchievementSettings fromIndexedAchievements(
        List<IndexedAchievement> indexedAchievements
    ) {
        return new AchievementSettings(indexedAchievements);
    }

    String questIdPayload() {
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

    private static String questIdPayload(Iterable<Achievement> achievements) {
        StringBuilder payload = new StringBuilder();
        if (achievements != null) {
            for (Achievement achievement : achievements) {
                if (achievement != null) {
                    payload.append(achievement.achievementId()).append('\2');
                }
            }
        }
        return payload.toString();
    }

    private static String questIdPayloadFromIndexedAchievements(List<IndexedAchievement> indexedAchievements) {
        List<Achievement> achievements = new ArrayList<>();
        if (indexedAchievements != null) {
            for (IndexedAchievement indexedAchievement : indexedAchievements) {
                if (indexedAchievement != null && indexedAchievement.achievement() != null) {
                    achievements.add(indexedAchievement.achievement());
                }
            }
        }
        return questIdPayload(achievements);
    }
}
