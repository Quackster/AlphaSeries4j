package com.alphaseries.game.pet;

public record PetStatusRow(
    long petId,
    String name,
    String figure,
    long level,
    long experience,
    long energy,
    long nutrition,
    long scratches,
    long ageDays,
    long ownerId,
    String ownerName
) {
}
