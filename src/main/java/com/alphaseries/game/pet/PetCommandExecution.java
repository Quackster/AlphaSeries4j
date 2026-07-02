package com.alphaseries.game.pet;

public record PetCommandExecution(
    long commandId,
    long botEntityId,
    String actionPayload,
    String speechPayload,
    long experienceDelta
) {
    public PetCommandExecution {
        actionPayload = actionPayload == null ? "" : actionPayload;
        speechPayload = speechPayload == null ? "" : speechPayload;
    }

    public boolean valid() {
        return commandId > 0L;
    }

    public boolean hasActionPayload() {
        return !actionPayload.isEmpty();
    }

    public boolean hasSpeechPayload() {
        return !speechPayload.isEmpty();
    }

    public boolean shouldAwardExperience() {
        return experienceDelta != 0L;
    }
}
