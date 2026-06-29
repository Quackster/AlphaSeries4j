package com.alphaseries.game.pet;

public record PetCommandCacheRow(
    long commandId,
    long requiredLevel,
    String command,
    String action
) {
}
