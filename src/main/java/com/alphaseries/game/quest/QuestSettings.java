package com.alphaseries.game.quest;

import com.alphaseries.dao.mysql.QuestDao;

import java.util.List;

public final class QuestSettings {
    private final List<QuestDefinitionRow> definitions;

    private QuestSettings(List<QuestDefinitionRow> definitions) {
        this.definitions = definitions == null ? List.of() : List.copyOf(definitions);
    }

    public static QuestSettings fromDefinitions(List<QuestDefinitionRow> definitions) {
        return new QuestSettings(definitions);
    }

    public static QuestSettings empty() {
        return new QuestSettings(List.of());
    }

    public boolean hasRows() {
        return !definitions.isEmpty();
    }

    public List<QuestDefinitionRow> definitions() {
        return definitions;
    }

    public QuestDefinitionRow definitionById(long questId) {
        if (questId <= 0L) {
            return null;
        }
        for (QuestDefinitionRow definition : definitions()) {
            if (definition.questId() == questId) {
                return definition;
            }
        }
        return null;
    }

    public long campaignLevelCount(long campaignId) {
        if (campaignId <= 0L) {
            return 0L;
        }
        long count = 0L;
        for (QuestDefinitionRow definition : definitions()) {
            if (definition.campaignId() == campaignId) {
                count++;
            }
        }
        return count;
    }

    public record QuestDefinitionRow(
        long questId,
        long level,
        String name,
        String reservedSlot,
        long reward,
        long rewardType,
        String requiredAction,
        long additionalId,
        long campaignId,
        long activityAmount,
        long waitAmount,
        int fieldCount
    ) {
        public static QuestDefinitionRow fromDefinition(QuestDao.QuestDefinition definition) {
            return new QuestDefinitionRow(
                definition.questId(),
                definition.level(),
                definition.name(),
                definition.reservedSlot(),
                definition.reward(),
                definition.rewardType(),
                definition.requiredAction(),
                definition.additionalId(),
                definition.campaignId(),
                definition.activityAmount(),
                definition.waitAmount(),
                11);
        }
    }

    public record UserQuestListRow(
        long questId,
        long level,
        String timestampDone,
        String timestampAccepted,
        String timeNext,
        long progress,
        long remainingWait,
        int fieldCount
    ) {
    }
}
