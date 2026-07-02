package com.alphaseries.game.social;

public record InteractionStatePayloads(
    String sourcePayload,
    String targetPayload
) {
    public InteractionStatePayloads {
        sourcePayload = sourcePayload == null ? "" : sourcePayload;
        targetPayload = targetPayload == null ? "" : targetPayload;
    }
}
