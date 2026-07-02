package com.alphaseries.game.social;

import com.alphaseries.config.AppConfigState;
import com.alphaseries.dao.mysql.DaoProvider;
import com.alphaseries.dao.mysql.RoomDao;
import com.alphaseries.dao.mysql.UserDao;
import com.alphaseries.game.achievement.AchievementPacketHandlers;
import com.alphaseries.game.chat.ChatCommands;
import com.alphaseries.game.chat.ChatLookups;
import com.alphaseries.game.room.RoomLookups;
import com.alphaseries.game.room.RoomUserTargetRow;
import com.alphaseries.game.session.SessionState;
import com.alphaseries.game.user.UserLookups;
import com.alphaseries.messages.outgoing.UserPayloads;
import com.alphaseries.server.lifecycle.LifecycleState;
import com.alphaseries.server.runtime.SessionLookups;
import com.alphaseries.server.runtime.SocketDelivery;
import com.alphaseries.util.NumberUtils;

import java.util.function.LongConsumer;

public final class SocialPacketHandlers {
    private SocialPacketHandlers() {
    }

    /**
     * Original function: Proc_6_13_6E0A80.
     */
    public static long waveCurrentRoomUser(int socketIndex) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            long roomId = SessionLookups.currentRoomId(socketIndex, userId);
            long roomUserIndex = SessionLookups.representedRoomUserIndex(socketIndex, userId);
            SocialLookups.RoomUserAction action =
                SocialLookups.roomUserWaveAction(NumberUtils.parseLong(userId), roomId, roomUserIndex);
            if (!action.valid()) {
                return 0L;
            }
            SocketDelivery.broadcastToCurrentRoom(socketIndex, action.payload());
            return action.resultValue();
        } catch (Exception ignored) {
            return 0L;
        }
    }

    /**
     * Original function: Proc_6_14_6E10C0.
     */
    public static long danceCurrentRoomUser(int socketIndex, SocialWire.DanceRequest request) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            long roomId = SessionLookups.currentRoomId(socketIndex, userId);
            long roomUserIndex = SessionLookups.representedRoomUserIndex(socketIndex, userId);
            SocialLookups.RoomUserAction action = SocialLookups.roomUserDanceAction(
                NumberUtils.parseLong(userId), roomId, roomUserIndex, request);
            if (!action.valid()) {
                return 0L;
            }
            SocketDelivery.broadcastToCurrentRoom(socketIndex, action.payload());
            return action.resultValue();
        } catch (Exception ignored) {
            return 0L;
        }
    }

    /**
     * Original function: Proc_6_26_7034C0.
     */
    public static String chatInCurrentRoom(int socketIndex, SocialWire.RepresentedChatMessage chatMessage) {
        return representedChatRoute(socketIndex, chatMessage, 0L);
    }

    /**
     * Original function: Proc_6_27_706920.
     */
    public static String shoutInCurrentRoom(int socketIndex, SocialWire.RepresentedChatMessage chatMessage) {
        return representedChatRoute(socketIndex, chatMessage, 1L);
    }

    /**
     * Original function: Proc_6_28_709DA0.
     */
    public static String whisperInCurrentRoom(int socketIndex, SocialWire.RepresentedChatMessage chatMessage) {
        return representedChatRoute(socketIndex, chatMessage, 2L);
    }

    /**
     * Original function: Proc_6_76_726CE0.
     */
    public static String giveRespect(int socketIndex, SocialWire.UserIdRequest request) {
        try {
            String targetUserId = String.valueOf(request.userId());
            if (targetUserId.isEmpty() || "0".equals(targetUserId)) {
                return "";
            }
            String giverUserId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (giverUserId.isEmpty() || "0".equals(giverUserId) || giverUserId.equals(targetUserId)) {
                return "";
            }
            int targetSocketIndex = SessionLookups.socketFromUserIdText(targetUserId);
            if (targetSocketIndex <= 0) {
                return "";
            }
            String payload = SocialLookups.giveRespectPayload(
                NumberUtils.parseLong(giverUserId), NumberUtils.parseLong(targetUserId), userDao());
            if (payload.isEmpty()) {
                return "";
            }
            AchievementPacketHandlers.advanceProgress(socketIndex, 3);
            AchievementPacketHandlers.advanceProgress(targetSocketIndex, 2);
            SocketDelivery.broadcastToCurrentRoom(socketIndex, payload);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_50_7166B0.
     */
    public static void followUserToRoom(
        int socketIndex,
        SocialWire.FollowUserRequest request,
        LongConsumer roomEntry
    ) {
        try {
            SocialLookups.FollowRoomAction action =
                SocialLookups.followRoomAction(request, userDao());
            if (action.hasFailurePayload()) {
                SocketDelivery.sendToSocket(socketIndex, action.failurePayload());
                return;
            }
            if (action.canEnterRoom() && roomEntry != null) {
                roomEntry.accept(action.roomId());
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void broadcastPreReadyRoomUserState(int socketIndex) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long roomUserIndex = SessionLookups.representedRoomUserIndex(socketIndex, userId);
            if (roomUserIndex <= 0L) {
                return;
            }
            SocketDelivery.broadcastToCurrentRoom(socketIndex, SocialLookups.roomUserPreReadyPayload(roomUserIndex));
        } catch (Exception ignored) {
            // VB6 source suppresses dispatcher helper failures.
        }
    }

    /**
     * Original function: Proc_6_193_7D2BB0.
     */
    public static String sendBadgeInventory(int socketIndex) {
        try {
            if (socketIndex <= 0) {
                return "";
            }
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            UserDao users = userDao();
            if (users == null) {
                return "";
            }
            BadgeInventoryPayload badgePayload = SocialLookups.badgeInventoryPayload(NumberUtils.parseLong(userId), users);
            SocketDelivery.sendToSocket(socketIndex, badgePayload.inventoryPayload());
            SocketDelivery.sendToSocket(socketIndex, badgePayload.displayPayload());
            return badgePayload.inventoryPayload();
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_194_7D3180.
     */
    public static String updateEquippedBadges(int socketIndex, BadgeUpdateSelections selections) {
        try {
            if (socketIndex <= 0) {
                return "";
            }
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            UserDao users = userDao();
            if (users == null) {
                return "";
            }
            SocialLookups.BadgeUpdateResult update =
                SocialLookups.updateEquippedBadges(NumberUtils.parseLong(userId), selections, users);
            if (!update.hasDisplayPayload()) {
                return "";
            }
            SocketDelivery.sendToSocket(socketIndex, update.displayPayload());
            if (SessionLookups.currentRoomId(socketIndex, userId) > 0L) {
                SocketDelivery.broadcastToCurrentRoom(socketIndex, update.displayPayload());
            }
            return update.equippedPayload();
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_190_7D11D0.
     */
    public static String sendRoomUserProfile(int socketIndex, SocialWire.RoomUserIndexRequest request) {
        try {
            long requestedRoomUserIndex = request.roomUserIndex();
            if (socketIndex <= 0 || requestedRoomUserIndex <= 0L) {
                return "";
            }
            String callerUserId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return "";
            }
            long roomId = SessionLookups.currentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L) {
                return "";
            }
            SocialLookups.DirectPayload action =
                SocialLookups.roomUserProfileAction(roomId, requestedRoomUserIndex, roomDao());
            if (!action.hasPayload()) {
                return "";
            }
            String payload = action.payload();
            SocketDelivery.sendToSocket(socketIndex, payload);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_191_7D18B0.
     */
    public static String sendUserTags(int socketIndex, SocialWire.UserIdRequest request) {
        try {
            if (socketIndex <= 0) {
                return "";
            }
            String callerUserId = SessionLookups.userIdTextFromSocket(socketIndex);
            SocialLookups.DirectPayload action = SocialLookups.tagDisplayAction(
                NumberUtils.parseLong(callerUserId), SessionLookups.currentRoomId(socketIndex, callerUserId), request, userDao());
            if (!action.hasPayload()) {
                return "";
            }
            String payload = action.payload();
            SocketDelivery.sendToSocket(socketIndex, payload);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_192_7D1B80.
     */
    public static String lookAtRoomUserBadge(int socketIndex, SocialWire.RoomUserIndexRequest request) {
        try {
            long requestedRoomUserIndex = request.roomUserIndex();
            if (socketIndex <= 0 || requestedRoomUserIndex <= 0L) {
                return "";
            }
            String callerUserId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return "";
            }
            long callerRoomId = SessionLookups.currentRoomId(socketIndex, callerUserId);
            if (callerRoomId <= 0L) {
                return "";
            }
            long callerRoomUserIndex = SessionLookups.representedRoomUserIndex(socketIndex, callerUserId);
            RoomUserTargetRow target = RoomLookups.activeRoomUserTarget(
                callerRoomId, requestedRoomUserIndex, roomDao()).orElse(null);
            SocialLookups.RoomUserBadgeLook look =
                SocialLookups.roomUserBadgeLookAction(callerRoomUserIndex, target, userDao());
            if (!look.hasDirectPayload()) {
                return "";
            }
            String targetBadgePayload = look.directPayload();
            SocketDelivery.sendToSocket(socketIndex, targetBadgePayload);
            RoomUserStatusPayloads statusPayloads = look.statusPayloads();
            if (statusPayloads.hasCallerPayload()) {
                SocketDelivery.broadcastToCurrentRoom(socketIndex, statusPayloads.callerPayload());
            }
            if (statusPayloads.hasTargetPayload()) {
                SocketDelivery.broadcastToCurrentRoom(socketIndex, statusPayloads.targetPayload());
            }
            return targetBadgePayload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    private static String representedChatRoute(
        int socketIndex,
        SocialWire.RepresentedChatMessage chatMessage,
        long chatType
    ) {
        try {
            if (socketIndex <= 0) {
                return "";
            }
            String targetName = chatMessage.targetName();
            String messageText = chatMessage.messageText();
            if (messageText.isEmpty()) {
                return "";
            }
            if (chatType == 0L && messageText.startsWith(":")) {
                String commandPayload = ChatCommands.commandPayload(
                    messageText,
                    LifecycleState.instance().runtimeState().productName());
                if (commandPayload.isEmpty()) {
                    commandPayload = ChatCommands.dynamicCommandPayload(
                        messageText, SessionState.instance().socketSessions(), userId -> UserLookups.nameByIdText(userId, userDao()));
                }
                if (!commandPayload.isEmpty()) {
                    SocketDelivery.sendToSocket(socketIndex, commandPayload);
                    return commandPayload;
                }
            }
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long roomId = SessionLookups.currentRoomId(socketIndex, userId);
            long roomUserIndex = SessionLookups.representedRoomUserIndex(socketIndex, userId);
            if (roomId <= 0L || roomUserIndex <= 0L) {
                return "";
            }
            long userRank = UserLookups.rank(NumberUtils.parseLong(userId), userDao());
            long hcLevel = UserLookups.hcLevel(NumberUtils.parseLong(userId), userDao());
            if (!ChatCommands.extractUrlList(messageText).isEmpty()
                && !AppConfigState.instance().permissionMatrix().allows(userRank, "", "fuse_can_chat_links", hcLevel)) {
                return "";
            }
            String filteredText = ChatLookups.filterMessage(messageText);
            if (filteredText.isEmpty()) {
                filteredText = messageText;
            }
            long gestureId = ChatLookups.gestureId(filteredText);
            RoomDao rooms = roomDao();
            if (rooms == null) {
                return "";
            }
            rooms.insertRoomChatLog(
                NumberUtils.parseLong(userId),
                roomId,
                filteredText,
                chatType,
                UserLookups.sessionId(NumberUtils.parseLong(userId), userDao()));
            String payload = UserPayloads.representedChat(roomUserIndex, filteredText, gestureId, chatType);
            if (chatType == 2L) {
                int targetSocketIndex = UserLookups.socketIndexForUserName(targetName, userDao());
                if (targetSocketIndex > 0) {
                    SocketDelivery.sendToSocket(targetSocketIndex, payload);
                    SocketDelivery.sendToSocket(socketIndex, payload);
                } else {
                    SocketDelivery.sendToSocket(socketIndex, payload);
                }
            } else {
                SocketDelivery.broadcastToCurrentRoom(socketIndex, payload);
            }
            return payload;
        } catch (Exception ignored) {
            return "";
        }
    }

    private static UserDao userDao() {
        return DaoProvider.userDao();
    }

    private static RoomDao roomDao() {
        return DaoProvider.roomDao();
    }
}
