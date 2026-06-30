package com.alphaseries.game.quest;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class QuestSettings {
    private final String rows;

    private QuestSettings(String rows) {
        this.rows = StringUtils.text(rows);
    }

    public static QuestSettings fromLegacy(String rows) {
        return new QuestSettings(rows);
    }

    public String rows() {
        return rows;
    }

    public boolean hasRows() {
        return !rows.isEmpty();
    }

    public List<QuestDefinitionRow> definitions() {
        List<QuestDefinitionRow> definitions = new ArrayList<>();
        for (String row : rows.split("\r", -1)) {
            QuestDefinitionRow definition = questDefinition(row);
            if (definition != null) {
                definitions.add(definition);
            }
        }
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

    public static QuestDefinitionRow questDefinition(String rowText) {
        String row = StringUtils.text(rowText).trim();
        if (row.isEmpty()) {
            return null;
        }
        String[] fields = row.split("\t", -1);
        if (fields.length < 9) {
            return null;
        }
        return new QuestDefinitionRow(
            NumberUtils.parseLong(StringUtils.field(fields, 0)),
            NumberUtils.parseLong(StringUtils.field(fields, 1)),
            StringUtils.field(fields, 2),
            StringUtils.field(fields, 3),
            NumberUtils.parseLong(StringUtils.field(fields, 4)),
            NumberUtils.parseLong(StringUtils.field(fields, 5)),
            StringUtils.field(fields, 6),
            NumberUtils.parseLong(StringUtils.field(fields, 7)),
            NumberUtils.parseLong(StringUtils.field(fields, 8)),
            NumberUtils.parseLong(StringUtils.field(fields, 9)),
            NumberUtils.parseLong(StringUtils.field(fields, 10)),
            fields.length);
    }

    public static UserQuestListRow userQuestListRow(String rowText) {
        String[] fields = StringUtils.text(rowText).split("\t", -1);
        if (fields.length < 1) {
            return null;
        }
        return new UserQuestListRow(
            NumberUtils.parseLong(StringUtils.field(fields, 0)),
            NumberUtils.parseLong(StringUtils.field(fields, 1)),
            StringUtils.field(fields, 2),
            StringUtils.field(fields, 3),
            StringUtils.field(fields, 4),
            NumberUtils.parseLong(StringUtils.field(fields, 5)),
            NumberUtils.parseLong(StringUtils.field(fields, 6)),
            fields.length);
    }

    public static UserQuestListRow userQuestListRowByQuestId(String userQuestRows, long questId) {
        if (questId <= 0L) {
            return null;
        }
        String marker = "\r" + questId + '\t';
        String text = "\r" + StringUtils.text(userQuestRows) + "\r";
        int start = text.indexOf(marker);
        if (start < 0) {
            return null;
        }
        int rowStart = start + marker.length();
        int rowEnd = text.indexOf('\r', rowStart);
        if (rowEnd < 0) {
            rowEnd = text.length();
        }
        return userQuestListRow(questId + "\t" + text.substring(rowStart, rowEnd));
    }

    public record QuestDefinitionRow(
        long questId,
        long level,
        String name,
        String legacyNullSlot,
        long reward,
        long rewardType,
        String requiredAction,
        long additionalId,
        long campaignId,
        long activityAmount,
        long waitAmount,
        int fieldCount
    ) {
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
