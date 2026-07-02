package com.alphaseries.game.quest;

import com.alphaseries.dao.mysql.QuestDao;
import com.alphaseries.messages.outgoing.QuestPayloads;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class QuestProgress {
    private QuestProgress() {
    }

    public static long nextQuestId(QuestSettings questSettings, QuestDao.UserQuestLevelRow activeQuest) {
        long currentQuestId = 0L;
        long currentLevel = 0L;
        if (activeQuest != null) {
            currentQuestId = activeQuest.questId();
            currentLevel = activeQuest.level();
        }

        long currentCampaignId = 0L;
        long fallbackQuestId = 0L;
        long fallbackCampaignId = 0L;
        long fallbackLevel = Integer.MAX_VALUE;
        boolean foundCurrent = false;
        QuestSettings settings = questSettings == null ? QuestSettings.empty() : questSettings;
        for (QuestSettings.QuestDefinitionRow definition : settings.definitions()) {
            if (definition.fieldCount() >= 9) {
                if (fallbackQuestId <= 0L || definition.level() < fallbackLevel) {
                    fallbackQuestId = definition.questId();
                    fallbackCampaignId = definition.campaignId();
                    fallbackLevel = definition.level();
                }
                if (definition.questId() == currentQuestId) {
                    currentCampaignId = definition.campaignId();
                    currentLevel = definition.level();
                    foundCurrent = true;
                }
            }
        }
        if (!foundCurrent) {
            currentCampaignId = fallbackCampaignId;
            currentLevel = fallbackLevel - 1L;
        }

        long requestedQuestId = 0L;
        long bestLevel = Integer.MAX_VALUE;
        for (QuestSettings.QuestDefinitionRow definition : settings.definitions()) {
            if (definition.fieldCount() >= 9
                && definition.campaignId() == currentCampaignId
                && definition.level() > currentLevel
                && definition.level() < bestLevel) {
                requestedQuestId = definition.questId();
                bestLevel = definition.level();
            }
        }
        return requestedQuestId > 0L ? requestedQuestId : fallbackQuestId;
    }

    /**
     * Original function: Proc_6_233_7F5D60.
     */
    public static long nextQuestIdForUser(long userId, QuestSettings questSettings, QuestDao quests) {
        try {
            if (userId <= 0L || quests == null) {
                return 0L;
            }
            QuestDao.UserQuestLevelRow activeRow = quests.activeLevelRow(userId)
                .or(() -> {
                    try {
                        return quests.latestLevelRow(userId);
                    } catch (Exception ignored) {
                        return java.util.Optional.empty();
                    }
                })
                .orElse(null);
            return nextQuestId(settingsFromSource(questSettings, quests), activeRow);
        } catch (Exception ignored) {
            return 0L;
        }
    }

    public static QuestProgressDecision decision(
        QuestDao.UserQuestProgressRow activeQuest,
        QuestSettings questSettings,
        long remainingWait
    ) {
        if (activeQuest == null) {
            return QuestProgressDecision.empty();
        }
        long questId = activeQuest.questId();
        long numericQuestId = activeQuest.numericQuestId();
        long progressValue = activeQuest.progress();
        String timeNextText = StringUtils.text(activeQuest.timeNext());
        if (questId <= 0L) {
            return QuestProgressDecision.empty();
        }

        QuestSettings settings = questSettings == null ? QuestSettings.empty() : questSettings;
        QuestSettings.QuestDefinitionRow questDefinition = settings.definitionById(questId);
        if (questDefinition == null || questDefinition.fieldCount() < 11) {
            return QuestProgressDecision.empty();
        }
        long amountRequired = questDefinition.activityAmount();
        long waitAmount = questDefinition.waitAmount();

        if (!timeNextText.isEmpty() && !"0".equals(timeNextText)) {
            long resolvedRemainingWait = Math.max(0L, remainingWait);
            if (resolvedRemainingWait > 0L) {
                return new QuestProgressDecision(
                    questId,
                    numericQuestId,
                    progressValue,
                    amountRequired,
                    waitAmount,
                    resolvedRemainingWait,
                    false,
                    false,
                    true);
            }
        } else if (waitAmount > 0L && progressValue > 0L && progressValue < amountRequired) {
            return new QuestProgressDecision(
                questId,
                numericQuestId,
                progressValue,
                amountRequired,
                waitAmount,
                0L,
                false,
                true,
                true);
        }

        if (amountRequired <= 0L) {
            amountRequired = 1L;
        }
        boolean shouldComplete = progressValue >= amountRequired;
        return new QuestProgressDecision(
            questId,
            numericQuestId,
            progressValue,
            amountRequired,
            waitAmount,
            0L,
            shouldComplete,
            false,
            !shouldComplete);
    }

    /**
     * Original function: Proc_6_232_7F45A0.
     */
    public static QuestAcceptResult acceptQuest(
        long userId,
        long requestedQuestId,
        QuestSettings questSettings,
        QuestDao quests
    ) {
        try {
            if (userId <= 0L || requestedQuestId <= 0L || quests == null) {
                return QuestAcceptResult.empty();
            }
            QuestSettings settings = settingsFromSource(questSettings, quests);
            QuestSettings.QuestDefinitionRow questDefinition = settings.definitionById(requestedQuestId);
            if (questDefinition == null || questDefinition.fieldCount() < 11) {
                return QuestAcceptResult.empty();
            }
            long questId = questDefinition.questId();
            long activityCount = questDefinition.activityAmount();
            long waitAmount = questDefinition.waitAmount();
            if (activityCount <= 0L) {
                activityCount = 1L;
            }
            quests.clearAcceptedQuest(userId);
            long existingLevel = quests.existingLevel(userId, questId);
            if (existingLevel != Long.MIN_VALUE) {
                quests.reactivateQuest(userId, questId, requestedQuestId);
            } else {
                quests.insertQuest(userId, questId, requestedQuestId);
            }
            long progressValue = quests.progress(userId, questId);
            if (waitAmount > 0L && progressValue > 0L && progressValue < activityCount) {
                String timeNextText = quests.timeNext(userId, questId);
                if (timeNextText.isEmpty() || "0".equals(timeNextText)) {
                    quests.scheduleNextTime(userId, questId, waitAmount);
                }
            }
            return new QuestAcceptResult(true, questId, requestedQuestId, progressValue >= activityCount);
        } catch (Exception ignored) {
            return QuestAcceptResult.empty();
        }
    }

    /**
     * Original function: Proc_6_235_7F77E0.
     */
    public static QuestProgressDecision refreshDecision(long userId, QuestSettings questSettings, QuestDao quests) {
        try {
            if (userId <= 0L || quests == null) {
                return QuestProgressDecision.empty();
            }
            QuestDao.UserQuestProgressRow activeQuest = quests.activeProgressRow(userId).orElse(null);
            if (activeQuest == null) {
                return QuestProgressDecision.empty();
            }
            long remainingWait = 0L;
            String timeNextText = StringUtils.text(activeQuest.timeNext());
            if (!timeNextText.isEmpty() && !"0".equals(timeNextText)) {
                remainingWait = quests.remainingWait(activeQuest.timeNext());
            }
            QuestProgressDecision decision = decision(activeQuest, settingsFromSource(questSettings, quests), remainingWait);
            if (decision.shouldScheduleWait()) {
                quests.scheduleNextTime(userId, activeQuest.questId(), decision.waitAmount());
            }
            return decision;
        } catch (Exception ignored) {
            return QuestProgressDecision.empty();
        }
    }

    public static QuestCompletion completion(
        QuestDao quests,
        QuestSettings questSettings,
        long userId,
        long questId,
        long numericQuestId
    )
        throws Exception {

        if (quests == null || userId <= 0L) {
            return emptyCompletion();
        }
        QuestDao.UserQuestCompletionRow activeRow = quests.completionRow(userId, questId).orElse(null);
        if (activeRow == null || activeRow.questId() <= 0L) {
            return emptyCompletion();
        }

        long resolvedQuestId = activeRow.questId();
        long resolvedNumericQuestId = numericQuestId <= 0L ? activeRow.numericQuestId() : numericQuestId;
        QuestSettings settings = questSettings == null ? QuestSettings.empty() : questSettings;
        QuestSettings.QuestDefinitionRow questDefinition = settings.definitionById(resolvedQuestId);
        if (questDefinition == null || questDefinition.fieldCount() < 11) {
            return emptyCompletion();
        }

        long activityCount = questDefinition.activityAmount();
        if (activityCount <= 0L) {
            activityCount = 1L;
        }
        long campaignId = questDefinition.campaignId();
        String payload = QuestPayloads.completion(
            campaignId,
            questDefinition.name(),
            settings.campaignLevelCount(campaignId),
            resolvedQuestId,
            activeRow.level(),
            activeRow.progress(),
            activityCount);
        return new QuestCompletion(
            resolvedQuestId,
            resolvedNumericQuestId,
            activeRow.progress(),
            activityCount,
            questDefinition.reward(),
            questDefinition.rewardType(),
            payload);
    }

    public static QuestSettings settingsFromSource(QuestSettings currentSettings, QuestDao quests) {
        QuestSettings settings = currentSettings == null ? QuestSettings.empty() : currentSettings;
        if (settings.hasRows()) {
            return settings;
        }
        if (quests == null) {
            return QuestSettings.empty();
        }
        try {
            List<QuestSettings.QuestDefinitionRow> rows = new ArrayList<>();
            for (QuestDao.QuestDefinition quest : quests.questDefinitions()) {
                if (quest != null) {
                    rows.add(QuestSettings.QuestDefinitionRow.fromDefinition(quest));
                }
            }
            return QuestSettings.fromDefinitions(rows);
        } catch (Exception ignored) {
            return QuestSettings.empty();
        }
    }

    public static List<QuestSettings.UserQuestListRow> userQuestRowsWithRemainingWait(
        QuestDao quests,
        List<QuestDao.UserQuestListRow> userQuestRows
    )
        throws Exception {

        List<QuestSettings.UserQuestListRow> rows = new ArrayList<>();
        for (QuestDao.UserQuestListRow row : userQuestRows == null ? List.<QuestDao.UserQuestListRow>of() : userQuestRows) {
            String timeNextText = StringUtils.text(row.timeNext());
            long remainingWait = 0L;
            if (!timeNextText.isEmpty() && !"0".equals(timeNextText)) {
                remainingWait = quests.remainingWait(timeNextText);
            }
            rows.add(new QuestSettings.UserQuestListRow(
                row.questId(),
                row.level(),
                row.timestampDone(),
                row.timestampAccepted(),
                row.timeNext(),
                row.progress(),
                remainingWait,
                7));
        }
        return rows;
    }

    /**
     * Original function: Proc_6_236_7F8540.
     */
    public static String listPayload(long userId, QuestSettings questSettings, QuestDao quests) {
        try {
            if (userId <= 0L || quests == null) {
                return "";
            }
            return QuestPayloads.list(
                settingsFromSource(questSettings, quests),
                userQuestRowsWithRemainingWait(quests, quests.listRows(userId)));
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_234_7F75C0.
     */
    public static QuestResetResult resetQuests(long userId, QuestSettings questSettings, QuestDao quests) {
        try {
            if (userId <= 0L || quests == null) {
                return QuestResetResult.empty();
            }
            quests.resetUserQuests(userId);
            String listPayload = listPayload(userId, questSettings, quests);
            return new QuestResetResult(true, new QuestResetResult.DeliveryPayloads("Lc", listPayload));
        } catch (Exception ignored) {
            return QuestResetResult.empty();
        }
    }

    private static QuestCompletion emptyCompletion() {
        return new QuestCompletion(0L, 0L, 0L, 1L, 0L, 0L, "");
    }
}
