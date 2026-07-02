package com.alphaseries.game.pet;

public record PetTutorialGuideSpawn(
    long botEntityId,
    String payload
) {
    public PetTutorialGuideSpawn {
        payload = payload == null ? "" : payload;
    }

    public boolean valid() {
        return botEntityId > 0L;
    }
}
