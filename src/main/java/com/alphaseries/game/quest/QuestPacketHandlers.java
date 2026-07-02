package com.alphaseries.game.quest;

import com.alphaseries.dao.mysql.DaoProvider;
import com.alphaseries.dao.mysql.QuestDao;
import com.alphaseries.dao.mysql.UserDao;
import com.alphaseries.messages.outgoing.QuestPayloads;
import com.alphaseries.messages.outgoing.UserPayloads;
import com.alphaseries.server.runtime.SessionLookups;
import com.alphaseries.server.runtime.SocketDelivery;
import com.alphaseries.util.NumberUtils;

public final class QuestPacketHandlers {
    private QuestPacketHandlers() {
    }

    @FunctionalInterface
    public interface CompletionHandler {
        void complete(int socketIndex, long questId, long numericQuestId);
    }

    /**
     * Original function: Proc_6_232_7F45A0.
     */
    public static String acceptQuest(
        int socketIndex,
        QuestWire.QuestIdRequest request,
        CompletionHandler completionHandler
    ) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long requestedQuestId = request.questId();
            if (requestedQuestId <= 0L) {
                return "";
            }
            QuestAcceptResult result = QuestProgress.acceptQuest(
                NumberUtils.parseLong(userId), requestedQuestId, questSettings(), questDao());
            if (!result.accepted()) {
                return "";
            }
            if (result.complete()) {
                complete(completionHandler, socketIndex, result.questId(), result.numericQuestId());
            } else {
                sendList(socketIndex);
            }
            return "";
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_233_7F5D60.
     */
    public static String autoAcceptNextQuest(int socketIndex, CompletionHandler completionHandler) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long requestedQuestId = QuestProgress.nextQuestIdForUser(
                NumberUtils.parseLong(userId), questSettings(), questDao());
            if (requestedQuestId > 0L) {
                acceptQuest(socketIndex, QuestWire.questIdRequest(QuestPayloads.request(requestedQuestId), "p^"), completionHandler);
            }
            return "";
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_234_7F75C0.
     */
    public static String resetQuests(int socketIndex) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            QuestResetResult result = QuestProgress.resetQuests(NumberUtils.parseLong(userId), questSettings(), questDao());
            if (!result.reset()) {
                return "";
            }
            SocketDelivery.sendToSocket(socketIndex, result.deliveryPayloads().payloads());
            return "";
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_164_7BC820.
     */
    public static String completeQuest(int socketIndex, long questId, long numericQuestId) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            QuestDao quests = questDao();
            UserDao users = userDao();
            if (quests == null || users == null) {
                return "";
            }
            long userIdValue = NumberUtils.parseLong(userId);
            QuestCompletion completion = QuestProgress.completion(
                quests,
                QuestProgress.settingsFromSource(questSettings(), questDao()),
                userIdValue,
                questId,
                numericQuestId);
            if (!completion.valid()) {
                return "";
            }
            String completionPayload = completion.payload();
            SocketDelivery.sendToSocket(socketIndex, "Lb" + completionPayload);
            if (!completion.complete()) {
                return "";
            }
            if (completion.hasActivityPointReward()) {
                long currentPoints = users.activityPoints(userIdValue, completion.rewardType());
                users.addActivityPointsLimited(userIdValue, completion.rewardType(), completion.rewardAmount());
                SocketDelivery.sendToSocket(socketIndex,
                    UserPayloads.activityPointAward(completion.rewardType(), currentPoints + completion.rewardAmount()));
            }
            quests.completeQuest(userIdValue, completion.questId());
            SocketDelivery.sendToSocket(socketIndex, "La" + completionPayload);
            sendList(socketIndex);
            return "";
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_235_7F77E0.
     */
    public static String refreshProgress(int socketIndex, CompletionHandler completionHandler) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            QuestProgressDecision decision = QuestProgress.refreshDecision(
                NumberUtils.parseLong(userId), questSettings(), questDao());
            if (decision.shouldComplete()) {
                complete(completionHandler, socketIndex, decision.questId(), decision.numericQuestId());
            } else if (decision.shouldSendList()) {
                sendList(socketIndex);
            }
            return "";
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_236_7F8540.
     */
    public static String sendList(int socketIndex) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            String payload = QuestProgress.listPayload(NumberUtils.parseLong(userId), questSettings(), questDao());
            SocketDelivery.sendToSocket(socketIndex, payload);
            return payload;
        } catch (Exception ignored) {
            return "";
        }
    }

    private static void complete(CompletionHandler completionHandler, int socketIndex, long questId, long numericQuestId) {
        if (completionHandler != null) {
            completionHandler.complete(socketIndex, questId, numericQuestId);
        }
    }

    private static QuestSettings questSettings() {
        return QuestState.instance().settings();
    }

    private static QuestDao questDao() {
        return DaoProvider.questDao();
    }

    private static UserDao userDao() {
        return DaoProvider.userDao();
    }
}
