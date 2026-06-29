package com.alphaseries.game.user;

public record ExpiredUserEffectRow(
    long effectId,
    long socketIndex,
    long rowId
) {
}
