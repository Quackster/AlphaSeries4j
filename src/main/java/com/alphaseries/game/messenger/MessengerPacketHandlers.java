package com.alphaseries.game.messenger;

import com.alphaseries.config.AppConfigState;
import com.alphaseries.dao.mysql.DaoProvider;
import com.alphaseries.dao.mysql.MessengerDao;
import com.alphaseries.dao.mysql.UserDao;
import com.alphaseries.game.chat.ChatLookups;
import com.alphaseries.messages.outgoing.MessengerPayloads;
import com.alphaseries.server.runtime.Guardian;
import com.alphaseries.server.runtime.SessionLookups;
import com.alphaseries.server.runtime.SocketDelivery;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class MessengerPacketHandlers {
    private MessengerPacketHandlers() {
    }

    /**
     * Original function: Proc_6_170_7C1100.
     */
    public static long deleteFriendRequests(int socketIndex, MessengerWire.FriendTargetList targets) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            MessengerDao messenger = messengerDao();
            if (messenger == null) {
                return 0L;
            }
            if (targets.deleteAllPending()) {
                messenger.deletePendingRequests(NumberUtils.parseLong(userId));
                return 1L;
            }
            if (targets.targetIds().isEmpty()) {
                return 0L;
            }
            messenger.deletePendingRequests(NumberUtils.parseLong(userId), targets.targetIds());
            return 1L;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return 0L;
        }
    }

    /**
     * Original function: Proc_6_171_7C1520.
     */
    public static String removeFriends(int socketIndex, MessengerWire.FriendTargetList targets) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            MessengerDao messenger = messengerDao();
            if (messenger == null) {
                return "";
            }
            RemovedFriendships removed = MessengerLookups.removeAcceptedFriends(
                NumberUtils.parseLong(userId), targets.targetIds(), messenger);
            if (!removed.valid()) {
                return "";
            }
            for (long targetUserId : removed.targetUserIds()) {
                int targetSocketIndex = SessionLookups.socketFromUserIdText(String.valueOf(targetUserId));
                if (targetSocketIndex > 0) {
                    SocketDelivery.sendToSocket(targetSocketIndex, removed.notificationPayload());
                }
            }
            SocketDelivery.sendToSocket(socketIndex, removed.callerPayload());
            return removed.callerPayload();
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_172_7C25B0.
     */
    public static String searchUsers(int socketIndex, MessengerWire.SearchRequest request) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            String searchText = request.searchText();
            if (searchText.isEmpty()) {
                return "";
            }
            MessengerDao messenger = messengerDao();
            if (messenger == null) {
                return "";
            }
            String resultPayload = MessengerLookups.searchResultsPayload(NumberUtils.parseLong(userId), searchText, messenger);
            SocketDelivery.sendToSocket(socketIndex, resultPayload);
            return resultPayload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_173_7C3430.
     */
    public static String sendPrivateMessage(int socketIndex, MessengerPrivateMessage message) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            if (!message.valid()) {
                return "";
            }
            int targetSocketIndex = SessionLookups.socketFromUserIdText(String.valueOf(message.targetUserId()));
            if (targetSocketIndex <= 0) {
                return "";
            }
            long currentRoomId = SessionLookups.currentRoomId(socketIndex, userId);
            MessengerDao messenger = messengerDao();
            if (messenger == null) {
                return "";
            }
            String payload = MessengerLookups.privateMessagePayload(
                NumberUtils.parseLong(userId),
                message.targetUserId(),
                currentRoomId,
                userName(String.valueOf(message.targetUserId())),
                message.messageText(),
                ChatLookups.filterMessage(message.messageText()),
                socketIndex,
                messenger);
            SocketDelivery.sendToSocket(targetSocketIndex, payload);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_174_7C3BC0.
     */
    public static String requestFriend(int socketIndex, MessengerWire.FriendRequest requestPayload) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            String targetName = requestPayload.targetName();
            if (targetName.isEmpty()) {
                return "";
            }
            MessengerDao messenger = messengerDao();
            if (messenger == null) {
                return "";
            }
            long callerUserId = NumberUtils.parseLong(userId);
            MessengerFriendRequest request = MessengerLookups.requestFriend(
                callerUserId, userName(userId), targetName, messenger);
            if (!request.valid()) {
                return "";
            }
            if (request.hasTargetNotification()) {
                int targetSocketIndex = SessionLookups.socketFromUserIdText(String.valueOf(request.targetUserId()));
                if (targetSocketIndex > 0) {
                    SocketDelivery.sendToSocket(targetSocketIndex, request.targetNotificationPayload());
                }
            }
            SocketDelivery.sendToSocket(socketIndex, request.callerPayload());
            return request.callerPayload();
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_175_7C4800.
     */
    public static String sendPendingRequests(int socketIndex) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            MessengerDao messenger = messengerDao();
            if (messenger == null) {
                return "";
            }
            List<PendingFriendRequest> requests = messenger.pendingRequests(NumberUtils.parseLong(userId));
            String payload = MessengerPayloads.pendingRequests(requests);
            SocketDelivery.sendToSocket(socketIndex, payload);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_176_7C4EE0.
     */
    public static String sendFriendList(int socketIndex) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            MessengerDao messenger = messengerDao();
            if (messenger == null) {
                return "";
            }
            MessengerFriendList friendList = MessengerLookups.friendList(NumberUtils.parseLong(userId), messenger);
            String onlineNotificationPayload = friendList.onlineNotificationPayload();
            List<Long> onlineFriendIds = new ArrayList<>();
            for (MessengerFriend friend : friendList.friends()) {
                if (friend != null) {
                    int friendSocketIndex = (int) friend.socketIndex();
                    long friendOnline = friendSocketIndex > 0 && Guardian.isSocketConnected(friendSocketIndex) ? 1L : 0L;
                    if (friendOnline == 1L) {
                        onlineFriendIds.add(friend.userId());
                    }
                    if (friendOnline == 1L && !onlineNotificationPayload.isEmpty()) {
                        SocketDelivery.sendToSocket(friendSocketIndex, onlineNotificationPayload);
                    }
                }
            }
            String payload = friendList.listPayload(onlineFriendIds);
            SocketDelivery.sendToSocket(socketIndex, payload);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_168_7C05F0.
     */
    public static String sendRoomInvite(int socketIndex, MessengerWire.RoomInviteRequest inviteRequest) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long roomId = SessionLookups.currentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return "";
            }
            if (inviteRequest.targetCount() <= 0L) {
                return "";
            }
            long userIdValue = NumberUtils.parseLong(userId);
            String inviteText = inviteRequest.inviteText();
            String filteredText = ChatLookups.filterMessage(inviteText);
            MessengerRoomInvite invite = MessengerLookups.roomInvite(
                userIdValue,
                roomId,
                socketIndex,
                inviteRequest,
                filteredText,
                messengerDao(),
                targetUserId -> SessionLookups.socketFromUserIdText(String.valueOf(targetUserId)),
                targetUserId -> userName(String.valueOf(targetUserId)));
            for (MessengerNotification notification : invite.deliveryPayloads()) {
                SocketDelivery.sendToSocket((int) notification.socketIndex(), notification.payload());
            }
            return invite.payload();
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_167_7BECA0.
     */
    public static String acceptFriendRequests(int socketIndex, MessengerWire.AcceptFriendRequests request) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long userIdValue = NumberUtils.parseLong(userId);
            MessengerDao messenger = messengerDao();
            if (messenger == null) {
                return "";
            }
            if (request.requestedCount() <= 0L) {
                return "";
            }
            String dateFormat = AppConfigState.instance().settingsCache().valueOrDefault("com.mysql.format.date", "%d-%m-%Y");
            String timeFormat = AppConfigState.instance().settingsCache().valueOrDefault("com.mysql.format.time", "%H:%i");
            String dateTimeFormat = StringUtils.sqlEscapedText(dateFormat + " " + timeFormat);
            AcceptedFriendRequests accepted = MessengerLookups.acceptPendingFriends(
                userIdValue, request.targetIds(), dateTimeFormat, messenger);
            if (!accepted.valid()) {
                return "";
            }
            for (MessengerNotification notification : accepted.deliveryPayloads()) {
                SocketDelivery.sendToSocket((int) notification.socketIndex(), notification.payload());
            }
            SocketDelivery.sendToSocket(socketIndex, accepted.callerPayload());
            return accepted.callerPayload();
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_169_7C0DC0.
     */
    public static String followFriend(int socketIndex, MessengerWire.FriendFollowRequest request) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long targetUserIdValue = request.targetUserId();
            if (targetUserIdValue <= 0L) {
                return "";
            }
            String targetUserId = String.valueOf(targetUserIdValue);
            int targetSocketIndex = SessionLookups.socketFromUserIdText(targetUserId);
            if (targetSocketIndex <= 0) {
                return "";
            }
            long targetRoomId = SessionLookups.currentRoomId(targetSocketIndex, targetUserId);
            if (targetRoomId <= 0L) {
                return "";
            }
            long targetRoomUserIndex = SessionLookups.representedRoomUserIndex(targetSocketIndex, targetUserId);
            String payload = MessengerLookups.followRoomPayload(
                NumberUtils.parseLong(userId),
                targetUserIdValue,
                targetRoomUserIndex,
                targetRoomId,
                messengerDao());
            if (payload.isEmpty()) {
                return "";
            }
            SocketDelivery.sendToSocket(socketIndex, payload);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    private static String userName(String userId) {
        try {
            if (StringUtils.text(userId).isEmpty() || "0".equals(StringUtils.text(userId))) {
                return "";
            }
            UserDao users = DaoProvider.userDao();
            return users == null ? "" : users.name(NumberUtils.parseLong(userId));
        } catch (Exception ignored) {
            return "";
        }
    }

    private static MessengerDao messengerDao() {
        return DaoProvider.messengerDao();
    }
}
