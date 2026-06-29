package com.alphaseries.game.pet;

public record PetRaceCacheRow(
    String productPet,
    long petId,
    long breed,
    long minRank,
    long minHcRank,
    String name
) {
}
