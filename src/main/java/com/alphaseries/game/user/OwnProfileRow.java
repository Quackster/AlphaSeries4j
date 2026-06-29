package com.alphaseries.game.user;

public record OwnProfileRow(
    long userId,
    String name,
    String motto,
    String gender,
    long respectAmount,
    long scratchAmount
) {
}
