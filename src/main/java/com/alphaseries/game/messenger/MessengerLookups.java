package com.alphaseries.game.messenger;

import com.alphaseries.config.AppConfigState;
import com.alphaseries.dao.mysql.MessengerDao;
import com.alphaseries.messages.outgoing.MessengerPayloads;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.LongFunction;
import java.util.function.LongToIntFunction;

public final class MessengerLookups {
    private MessengerLookups() {
    }

    public static MessengerFriend friendSummary(long userId, MessengerDao messenger) {
        try {
            if (userId <= 0L || messenger == null) {
                return null;
            }
            String dateFormat = AppConfigState.instance().settingsCache().valueOrDefault("com.mysql.format.date", "%d-%m-%Y");
            String timeFormat = AppConfigState.instance().settingsCache().valueOrDefault("com.mysql.format.time", "%H:%i");
            return messenger
                .messengerFriend(userId, dateFormat + " " + timeFormat)
                .orElse(null);
        } catch (Exception ignored) {
            return null;
        }
    }

    public static AcceptedFriendRequests acceptPendingFriends(
        long userId,
        List<Long> targetIds,
        String dateTimeFormat,
        MessengerDao messenger
    )
        throws Exception {

        if (userId <= 0L || targetIds == null || targetIds.isEmpty() || messenger == null) {
            return emptyAcceptedFriendRequests();
        }
        List<MessengerFriend> acceptedFriends = new ArrayList<>();
        List<MessengerNotification> notifications = new ArrayList<>();
        for (long targetUserId : targetIds) {
            if (targetUserId <= 0L || !messenger.pendingRequestExists(userId, targetUserId, dateTimeFormat)) {
                continue;
            }
            MessengerFriend friend = messenger.messengerFriend(targetUserId, dateTimeFormat).orElse(null);
            if (friend == null) {
                continue;
            }
            long targetSocketIndex = friend.socketIndex();
            acceptedFriends.add(friend);
            messenger.insertReversePendingFriendship(targetUserId, userId);
            messenger.acceptFriendshipPair(userId, targetUserId);
            if (targetSocketIndex > 0L) {
                notifications.add(new MessengerNotification(
                    targetSocketIndex,
                    MessengerViews.friendOnlineNotification(friendSummary(userId, messenger), 1L)));
            }
        }
        if (acceptedFriends.isEmpty()) {
            return emptyAcceptedFriendRequests();
        }
        return new AcceptedFriendRequests(
            MessengerViews.acceptedFriendsPayload(acceptedFriends),
            acceptedFriends.size(),
            notifications);
    }

    private static AcceptedFriendRequests emptyAcceptedFriendRequests() {
        return new AcceptedFriendRequests("", 0L, List.of());
    }

    public static RemovedFriendships removeAcceptedFriends(
        long userId,
        List<Long> targetIds,
        MessengerDao messenger
    )
        throws Exception {

        if (userId <= 0L || targetIds == null || targetIds.isEmpty() || messenger == null) {
            return emptyRemovedFriendships();
        }
        List<Long> removedTargetIds = new ArrayList<>();
        for (long targetUserId : targetIds) {
            if (targetUserId <= 0L || targetUserId == userId || removedTargetIds.contains(targetUserId)) {
                continue;
            }
            if (messenger.acceptedFriendshipExists(userId, targetUserId)) {
                removedTargetIds.add(targetUserId);
            }
        }
        if (removedTargetIds.isEmpty()) {
            return emptyRemovedFriendships();
        }
        messenger.deleteAcceptedFriendships(userId, removedTargetIds);
        return new RemovedFriendships(
            MessengerPayloads.removeFriends(removedTargetIds),
            removedTargetIds,
            MessengerPayloads.friendRemovedNotification(userId));
    }

    private static RemovedFriendships emptyRemovedFriendships() {
        return new RemovedFriendships("", List.of(), "");
    }

    public static String searchResultsPayload(long callerUserId, String searchText, MessengerDao messenger)
        throws Exception {

        String normalizedSearch = StringUtils.text(searchText).trim().toLowerCase();
        if (callerUserId <= 0L || normalizedSearch.isEmpty() || messenger == null) {
            return "";
        }
        String dateFormat = AppConfigState.instance().settingsCache().valueOrDefault("com.mysql.format.date", "%d-%m-%Y");
        String timeFormat = AppConfigState.instance().settingsCache().valueOrDefault("com.mysql.format.time", "%H:%i");
        List<MessengerSearchResult> results = new ArrayList<>();
        for (MessengerDao.SearchUser searchUser : messenger.searchUsers(normalizedSearch, dateFormat + " " + timeFormat)) {
            if (searchUser.userId() != callerUserId) {
                results.add(new MessengerSearchResult(
                    searchUser.userId(),
                    searchUser.userName(),
                    searchUser.figure(),
                    searchUser.motto(),
                    searchUser.nickname(),
                    searchUser.lastOnline(),
                    searchUser.socketIndex() > 0L,
                    messenger.acceptedFriendshipExists(callerUserId, searchUser.userId())));
            }
        }
        return MessengerPayloads.searchResults(results);
    }

    public static MessengerFriendRequest requestFriend(
        long callerUserId,
        String callerUserName,
        String targetName,
        MessengerDao messenger
    )
        throws Exception {

        String normalizedTargetName = StringUtils.text(targetName).trim();
        if (callerUserId <= 0L || normalizedTargetName.isEmpty() || messenger == null) {
            return emptyFriendRequest();
        }
        long targetUserId = messenger.userIdByName(normalizedTargetName);
        if (targetUserId <= 0L || targetUserId == callerUserId) {
            return deniedFriendRequest();
        }
        if (messenger.friendshipExists(callerUserId, targetUserId) || messenger.acceptFriends(targetUserId) != 1L) {
            return deniedFriendRequest();
        }
        messenger.insertFriendRequest(targetUserId, callerUserId);
        return new MessengerFriendRequest(
            MessengerPayloads.requestAcceptedCaller(targetUserId),
            targetUserId,
            MessengerPayloads.requestNotify(callerUserId, callerUserName));
    }

    private static MessengerFriendRequest deniedFriendRequest() {
        return new MessengerFriendRequest(MessengerPayloads.requestDenied(), 0L, "");
    }

    private static MessengerFriendRequest emptyFriendRequest() {
        return new MessengerFriendRequest("", 0L, "");
    }

    public static String privateMessagePayload(
        long callerUserId,
        long targetUserId,
        long roomId,
        String targetUserName,
        String rawMessageText,
        String filteredMessageText,
        long socketIndex,
        MessengerDao messenger
    )
        throws Exception {

        if (callerUserId <= 0L || targetUserId <= 0L || StringUtils.text(rawMessageText).isEmpty() || messenger == null) {
            return "";
        }
        messenger.insertPrivateChatLog(
            callerUserId,
            roomId,
            "(Chat To:     " + StringUtils.text(targetUserName) + ") -- " + StringUtils.text(rawMessageText),
            socketIndex);
        return MessengerPayloads.privateChatMessage(callerUserId, filteredMessageText);
    }

    /**
     * Original function: Proc_6_168_7C05F0.
     */
    public static MessengerRoomInvite roomInvite(
        long callerUserId,
        long roomId,
        long socketIndex,
        MessengerWire.RoomInviteRequest request,
        String filteredInviteText,
        MessengerDao messenger,
        LongToIntFunction socketResolver,
        LongFunction<String> userNameResolver
    )
        throws Exception {

        if (callerUserId <= 0L || roomId <= 0L || request == null || request.targetCount() <= 0L) {
            return emptyRoomInvite();
        }
        String payload = MessengerPayloads.roomInviteMessage(callerUserId, filteredInviteText);
        if (messenger == null || socketResolver == null) {
            return new MessengerRoomInvite(payload, List.of());
        }
        LongFunction<String> names = userNameResolver == null ? ignored -> "" : userNameResolver;
        List<MessengerNotification> notifications = new ArrayList<>();
        List<Long> targetIds = new ArrayList<>();
        for (long targetUserId : request.targetIds()) {
            if (targetUserId <= 0L || targetIds.contains(targetUserId)) {
                continue;
            }
            if (!messenger.acceptedFriendshipExists(callerUserId, targetUserId)) {
                continue;
            }
            int targetSocketIndex = socketResolver.applyAsInt(targetUserId);
            if (targetSocketIndex <= 0) {
                continue;
            }
            targetIds.add(targetUserId);
            notifications.add(new MessengerNotification(targetSocketIndex, payload));
            messenger.insertInviteChatLog(
                callerUserId,
                roomId,
                "(Invite To: " + StringUtils.text(names.apply(targetUserId)) + ") -- " + request.inviteText(),
                socketIndex);
        }
        return new MessengerRoomInvite(payload, notifications);
    }

    private static MessengerRoomInvite emptyRoomInvite() {
        return new MessengerRoomInvite("", List.of());
    }

    /**
     * Original function: Proc_6_169_7C0DC0.
     */
    public static String followRoomPayload(
        long callerUserId,
        long targetUserId,
        long targetRoomUserIndex,
        long targetRoomId,
        MessengerDao messenger
    )
        throws Exception {

        if (callerUserId <= 0L || targetUserId <= 0L || targetRoomUserIndex <= 0L || targetRoomId <= 0L
            || messenger == null || !messenger.acceptedFriendshipExists(callerUserId, targetUserId)) {
            return "";
        }
        return MessengerPayloads.followRoom(targetRoomUserIndex, targetRoomId);
    }

    public static MessengerFriendList friendList(long userId, MessengerDao messenger)
        throws Exception {

        if (userId <= 0L || messenger == null) {
            return emptyFriendList();
        }
        long maxFriends0 = MessengerViews.maxFriends(0L);
        long maxFriends1 = MessengerViews.maxFriends(2L);
        long maxFriends2 = MessengerViews.maxFriends(4L);
        String dateFormat = AppConfigState.instance().settingsCache().valueOrDefault("com.mysql.format.date", "%d-%m-%Y");
        String timeFormat = AppConfigState.instance().settingsCache().valueOrDefault("com.mysql.format.time", "%H:%i");
        return new MessengerFriendList(
            messenger.acceptedFriends(userId, dateFormat + " " + timeFormat, maxFriends2),
            maxFriends0,
            maxFriends1,
            maxFriends2,
            friendSummary(userId, messenger));
    }

    private static MessengerFriendList emptyFriendList() {
        return new MessengerFriendList(List.of(), 0L, 0L, 0L, null);
    }
}
