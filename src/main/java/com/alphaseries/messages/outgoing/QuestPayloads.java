package com.alphaseries.messages.outgoing;

import com.alphaseries.game.quest.QuestSettings;
import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.util.StringUtils;

import java.util.List;

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

    public static String request(long questId) {
        return PacketBuilder.message("p^")
            .appendInt(questId)
            .build();
    }

    public static String list(QuestSettings questSettings, List<QuestSettings.UserQuestListRow> userQuestRows) {
        long lastCampaignId = -1L;
        long campaignLevelCount = 0L;
        long questCount = 0L;
        PacketBuilder questPayload = PacketBuilder.create();
        QuestSettings settings = questSettings == null ? QuestSettings.empty() : questSettings;
        for (QuestSettings.QuestDefinitionRow quest : settings.definitions()) {
            if (quest.fieldCount() >= 11) {
                long waitSeconds = quest.waitAmount();

                if (quest.campaignId() != lastCampaignId) {
                    lastCampaignId = quest.campaignId();
                    campaignLevelCount = 0L;
                }
                campaignLevelCount++;

                QuestSettings.UserQuestListRow userQuest = userQuestListRowByQuestId(userQuestRows, quest.questId());
                long userLevel = userQuest == null ? 0L : userQuest.level();
                String timestampDone = userQuest == null ? "" : StringUtils.text(userQuest.timestampDone());
                String timestampAccepted = userQuest == null ? "" : StringUtils.text(userQuest.timestampAccepted());
                String timeNextText = userQuest == null ? "" : StringUtils.text(userQuest.timeNext());
                long progressValue = userQuest == null ? 0L : userQuest.progress();
                long remainingWait = userQuest == null ? 0L : userQuest.remainingWait();

                long stateCode = 0L;
                if (!timestampDone.isEmpty() && !"0".equals(timestampDone)) {
                    stateCode = 2L;
                } else if (!timestampAccepted.isEmpty() && !"0".equals(timestampAccepted)) {
                    stateCode = 1L;
                }
                if (!timeNextText.isEmpty() && !"0".equals(timeNextText)) {
                    waitSeconds = remainingWait;
                }

                questPayload
                    .appendInt(quest.campaignId())
                    .appendString(quest.name())
                    .appendInt(quest.questId())
                    .appendInt(quest.level())
                    .appendInt(campaignLevelCount)
                    .appendInt(stateCode)
                    .appendInt(userLevel)
                    .appendInt(progressValue)
                    .appendInt(quest.activityAmount())
                    .appendInt(quest.rewardType())
                    .appendInt(quest.reward())
                    .appendRaw("HHH\2\2H\2HHH")
                    .appendInt(waitSeconds);
                questCount++;
            }
        }
        return PacketBuilder.message("L`")
            .appendInt(questCount)
            .appendInt(0L)
            .appendRaw(questPayload)
            .build();
    }

    private static QuestSettings.UserQuestListRow userQuestListRowByQuestId(
        List<QuestSettings.UserQuestListRow> userQuestRows,
        long questId
    ) {
        if (questId <= 0L || userQuestRows == null) {
            return null;
        }
        for (QuestSettings.UserQuestListRow row : userQuestRows) {
            if (row != null && row.questId() == questId) {
                return row;
            }
        }
        return null;
    }
}
