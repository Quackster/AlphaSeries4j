package com.alphaseries.game.quest;

public record QuestAcceptResult(boolean accepted, long questId, long numericQuestId, boolean complete) {
    public static QuestAcceptResult empty() {
        return new QuestAcceptResult(false, 0L, 0L, false);
    }
}
