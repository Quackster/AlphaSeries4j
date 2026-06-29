package com.alphaseries.game.pet;

public record PetExperienceStateRow(
    String name,
    String figure,
    long level,
    long experience,
    long energy,
    long nutrition,
    long scratches,
    long roomId
) {
}
