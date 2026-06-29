package com.alphaseries.game.pet;

public record PetCommandTargetRow(
    long petId,
    long roomId,
    long level,
    long energy,
    long nutrition
) {
}
