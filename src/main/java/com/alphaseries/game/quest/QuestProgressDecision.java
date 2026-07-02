package com.alphaseries.game.quest;

public record QuestProgressDecision(
    long questId,
    long numericQuestId,
    long progressValue,
    long amountRequired,
    long waitAmount,
    long remainingWait,
    boolean shouldComplete,
    boolean shouldScheduleWait,
    boolean shouldSendList
) {
    public static QuestProgressDecision empty() {
        return new QuestProgressDecision(0L, 0L, 0L, 0L, 0L, 0L, false, false, false);
    }
}
