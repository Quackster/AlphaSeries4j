package com.alphaseries.game.pet;

public record PetScratchRow(
    long petId,
    String name,
    String figure,
    long scratches
) {
}
