package com.alphaseries.game.quest;

public record QuestCompletion(
    long questId,
    long numericQuestId,
    long progressValue,
    long activityCount,
    long rewardAmount,
    long rewardType,
    String payload
) {
    public QuestCompletion {
        payload = payload == null ? "" : payload;
    }

    public boolean valid() {
        return questId > 0L && !payload.isEmpty();
    }

    public boolean complete() {
        return progressValue >= activityCount;
    }

    public boolean hasActivityPointReward() {
        return rewardAmount != 0L && rewardType >= 0L && rewardType <= 20L;
    }
}
