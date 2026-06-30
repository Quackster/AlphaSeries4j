package com.alphaseries.game.messenger;

public record MessengerFriend(
    long userId,
    String userName,
    String motto,
    String figure,
    long level,
    long socketIndex,
    String lastOnline
) {
}
