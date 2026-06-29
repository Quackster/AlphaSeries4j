package com.alphaseries.game.user;

public record UserEffectActivationRow(
    long rowId,
    long rentSeconds,
    long expireTimestamp
) {
}
