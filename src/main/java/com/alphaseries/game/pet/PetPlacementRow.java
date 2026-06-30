package com.alphaseries.game.pet;

public record PetPlacementRow(
    long petId,
    String name,
    String motto,
    String speech,
    String responses,
    String figure,
    long handleId,
    long handleActionId,
    String cacheAction,
    String speechSubmit,
    long allowWalk,
    long maxFieldsAway
) {
}
