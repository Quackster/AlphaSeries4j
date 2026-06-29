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
    public String[] representedBotFields(long positionX, long positionY, String positionZ, long positionR) {
        return new String[]{
            String.valueOf(petId),
            name,
            motto,
            speech,
            responses,
            String.valueOf(positionX),
            String.valueOf(positionY),
            positionZ,
            String.valueOf(positionR),
            figure,
            "",
            String.valueOf(handleId),
            String.valueOf(handleActionId),
            cacheAction,
            speechSubmit,
            String.valueOf(allowWalk),
            String.valueOf(maxFieldsAway)
        };
    }
}
