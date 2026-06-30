package com.alphaseries.messages.outgoing;

import com.alphaseries.protocol.PacketBuilder;

public final class QuestPayloads {
    private QuestPayloads() {
    }

    public static String completion(
        long campaignId,
        String questName,
        long campaignLevelCount,
        long questId,
        long userQuestLevel,
        long progressValue,
        long activityCount
    ) {
        long resolvedActivityCount = activityCount <= 0L ? 1L : activityCount;
        return PacketBuilder.create()
            .appendInt(campaignId)
            .appendString(questName)
            .appendInt(campaignLevelCount)
            .appendInt(questId)
            .appendInt(userQuestLevel)
            .appendInt(progressValue)
            .appendInt(resolvedActivityCount)
            .appendInt(0L)
            .build();
    }
}
