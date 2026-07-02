package com.alphaseries.game.messenger;

import java.util.List;

public record MessengerFriendList(
    List<MessengerFriend> friends,
    long maxFriends0,
    long maxFriends1,
    long maxFriends2,
    MessengerFriend callerFriend
) {
    public MessengerFriendList {
        friends = friends == null ? List.of() : List.copyOf(friends);
    }

    public boolean valid() {
        return maxFriends2 > 0L || !friends.isEmpty();
    }

    public String onlineNotificationPayload() {
        return MessengerViews.friendOnlineNotification(callerFriend, 1L);
    }

    public String listPayload(List<Long> onlineFriendIds) {
        return MessengerViews.friendListPayload(friends, maxFriends0, maxFriends1, maxFriends2, onlineFriendIds);
    }
}
