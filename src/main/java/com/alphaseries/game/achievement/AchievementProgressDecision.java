package com.alphaseries.game.achievement;

public record AchievementProgressDecision(
    long achievementIndex,
    long nextLevel,
    long requiredProgress,
    boolean shouldReward
) {
    public static AchievementProgressDecision empty() {
        return new AchievementProgressDecision(-1L, 0L, 0L, false);
    }
}
