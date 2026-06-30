package com.alphaseries.game.messenger;

public record MessengerSearchResult(
    long userId,
    String userName,
    String figure,
    String motto,
    String nickname,
    String lastOnline,
    boolean online,
    boolean acceptedFriend
) {
}
