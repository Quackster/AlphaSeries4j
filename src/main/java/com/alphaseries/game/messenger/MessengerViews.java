package com.alphaseries.game.messenger;

import com.alphaseries.config.AppConfigState;
import com.alphaseries.messages.outgoing.MessengerPayloads;
import com.alphaseries.util.NumberUtils;

import java.util.List;

public final class MessengerViews {
    private MessengerViews() {
    }

    /**
     * Original function: Proc_6_166_7BE940.
     */
    public static String friendPayload(
        long userId,
        String userName,
        String motto,
        String figure,
        long rankValue,
        long followCount,
        long isOnline,
        String lastOnlineText,
        long relationshipState
    ) {
        return MessengerPayloads.friend(
            userId,
            userName,
            motto,
            figure,
            rankValue,
            followCount,
            isOnline,
            lastOnlineText,
            relationshipState,
            followEnabled());
    }

    public static String friendSummaryPayload(MessengerFriend friend, long relationshipState) {
        return MessengerPayloads.friendSummary(friend, relationshipState, followEnabled());
    }

    public static String friendOnlineNotification(MessengerFriend friend, long relationshipState) {
        return friend == null ? "" : MessengerPayloads.friendOnlineNotification(friend, relationshipState, followEnabled());
    }

    public static String acceptedFriendsPayload(List<MessengerFriend> friends) {
        return MessengerPayloads.acceptedFriends(friends, followEnabled());
    }

    public static boolean followEnabled() {
        return AppConfigState.instance().settingsCache().longValueOrDefault("com.client.messenger.follow.enabled", 0) != 0L;
    }

    public static long maxFriends(long configIndex) {
        return MessengerState.instance().settings().maxFriends(configIndex);
    }

    public static String friendListPayload(
        List<MessengerFriend> friends,
        long maxFriends0,
        long maxFriends1,
        long maxFriends2
    ) {
        return MessengerPayloads.friendList(friends, maxFriends0, maxFriends1, maxFriends2, followEnabled());
    }

    public static String friendListPayload(
        List<MessengerFriend> friends,
        long maxFriends0,
        long maxFriends1,
        long maxFriends2,
        List<Long> onlineUserIds
    ) {
        return MessengerPayloads.friendList(friends, maxFriends0, maxFriends1, maxFriends2, onlineUserIds, followEnabled());
    }
}
