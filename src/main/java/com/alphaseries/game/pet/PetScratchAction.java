package com.alphaseries.game.pet;

public record PetScratchAction(
    long botId,
    long userId,
    long scratches,
    String payload
) {
    public PetScratchAction {
        payload = payload == null ? "" : payload;
    }

    public boolean valid() {
        return botId > 0L && userId > 0L && scratches > 0L;
    }
}
