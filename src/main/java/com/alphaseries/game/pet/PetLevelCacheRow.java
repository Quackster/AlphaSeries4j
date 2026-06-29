package com.alphaseries.game.pet;

public record PetLevelCacheRow(
    long level,
    long maxEnergy,
    long maxExperience,
    long maxNutrition
) {
}
