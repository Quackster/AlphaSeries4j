package com.alphaseries.game.achievement;

import com.alphaseries.dao.mysql.DaoProvider;
import com.alphaseries.dao.mysql.UserDao;
import com.alphaseries.server.runtime.SessionLookups;
import com.alphaseries.server.runtime.SocketDelivery;
import com.alphaseries.util.NumberUtils;

public final class AchievementPacketHandlers {
    private AchievementPacketHandlers() {
    }

    /**
     * Original function: Proc_6_204_7D82E0.
     */
    public static String grantReward(int socketIndex, long achievementIndex, long badgeLevel) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            AchievementRewardGrant grant = AchievementLookups.grantReward(
                NumberUtils.parseLong(userId), achievementIndex, badgeLevel, achievementSettings(), userDao());
            if (!grant.valid()) {
                return "";
            }
            String payload = grant.rewardPayload();
            SocketDelivery.sendToSocket(socketIndex, grant.deliveryPayloads());
            return payload;
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_205_7D9780.
     */
    public static String advanceProgress(int socketIndex, long achievementQuestId) {
        try {
            if (socketIndex <= 0 || achievementQuestId <= 0L) {
                return "";
            }
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            AchievementRewardGrant grant = AchievementLookups.advanceProgress(
                NumberUtils.parseLong(userId), achievementQuestId, achievementSettings(), userDao());
            if (grant.valid()) {
                SocketDelivery.sendToSocket(socketIndex, grant.deliveryPayloads());
            }
            return "";
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_206_7DA450.
     */
    public static String sendList(int socketIndex) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            String payload = AchievementLookups.listPayload(NumberUtils.parseLong(userId), achievementSettings(), userDao());
            if (payload.isEmpty()) {
                return "";
            }
            SocketDelivery.sendToSocket(socketIndex, payload);
            return payload;
        } catch (Exception ignored) {
            return "";
        }
    }

    private static AchievementSettings achievementSettings() {
        return AchievementState.instance().settings();
    }

    private static UserDao userDao() {
        return DaoProvider.userDao();
    }
}
