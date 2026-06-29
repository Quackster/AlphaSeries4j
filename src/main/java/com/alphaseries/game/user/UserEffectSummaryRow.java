package com.alphaseries.game.user;

public record UserEffectSummaryRow(
    long effectId,
    long rentSeconds,
    long effectCount,
    long expireTimestamp,
    long currentTimestamp
) {
}
