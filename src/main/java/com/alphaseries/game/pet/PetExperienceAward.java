package com.alphaseries.game.pet;

public record PetExperienceAward(
    long botId,
    long botEntityId,
    long roomId,
    long petLevel,
    long petExperience,
    String levelSpeechPayload,
    String statusPayload,
    String experiencePayload
) {
    public PetExperienceAward {
        levelSpeechPayload = levelSpeechPayload == null ? "" : levelSpeechPayload;
        statusPayload = statusPayload == null ? "" : statusPayload;
        experiencePayload = experiencePayload == null ? "" : experiencePayload;
    }

    public boolean valid() {
        return botId > 0L;
    }

    public boolean hasRoomPayloads() {
        return roomId > 0L;
    }

    public boolean hasLevelSpeechPayload() {
        return !levelSpeechPayload.isEmpty();
    }
}
