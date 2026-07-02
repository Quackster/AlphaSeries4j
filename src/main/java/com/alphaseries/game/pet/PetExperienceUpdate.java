package com.alphaseries.game.pet;

public record PetExperienceUpdate(
    long petLevel,
    long petExperience,
    boolean leveledUp,
    String statusPayload,
    String experiencePayload
) {
    public PetExperienceUpdate {
        statusPayload = statusPayload == null ? "" : statusPayload;
        experiencePayload = experiencePayload == null ? "" : experiencePayload;
    }
}
