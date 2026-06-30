package com.alphaseries.game.trade;

public record RepresentedInteractionPair(
    long socketIndex,
    long partnerSocketIndex,
    long interactionState
) {
    public static RepresentedInteractionPair stored(long socketIndex, long partnerSocketIndex, long interactionState) {
        return new RepresentedInteractionPair(socketIndex, partnerSocketIndex, interactionState);
    }
}
