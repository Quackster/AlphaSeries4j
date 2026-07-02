package com.alphaseries.game.pet;

import java.util.List;

public final class PetProgress {
    private PetProgress() {
    }

    public static PetCommandAction commandAction(long commandId, List<PetSettings.PetCommandRow> commandRows) {
        if (commandId <= 0L) {
            return PetCommandAction.empty();
        }
        List<PetSettings.PetCommandRow> rows = commandRows == null ? List.of() : commandRows;
        for (PetSettings.PetCommandRow row : rows) {
            if (row != null && row.commandId() == commandId) {
                return new PetCommandAction(true, row.requiredLevel(), row.action());
            }
        }
        return PetCommandAction.empty();
    }

    public static PetExperienceUpdate experienceUpdate(
        long botEntityId,
        String petName,
        String petFigure,
        long petLevel,
        long petExperience,
        long petEnergy,
        long petNutrition,
        long petScratches,
        long experienceDelta,
        List<PetSettings.PetLevelRow> levelRows
    ) {
        PetExperienceUpdate result = updateBase(petLevel, petExperience, experienceDelta,
            level -> levelMaxExperience(level, levelRows));
        return withPayloads(result, botEntityId, petName, petFigure, petEnergy, petNutrition, petScratches, experienceDelta);
    }

    public static PetExperienceUpdate experienceUpdateFromRows(
        long botEntityId,
        String petName,
        String petFigure,
        long petLevel,
        long petExperience,
        long petEnergy,
        long petNutrition,
        long petScratches,
        long experienceDelta,
        List<PetLevelExperienceRow> levelRows
    ) {
        PetExperienceUpdate result = updateBase(petLevel, petExperience, experienceDelta,
            level -> levelMaxExperienceFromRows(level, levelRows));
        return withPayloads(result, botEntityId, petName, petFigure, petEnergy, petNutrition, petScratches, experienceDelta);
    }

    public static long levelMaxExperience(long petLevel, List<PetSettings.PetLevelRow> levelRows) {
        if (levelRows != null) {
            for (PetSettings.PetLevelRow row : levelRows) {
                if (row != null && row.level() == petLevel) {
                    return row.maxExperience();
                }
            }
        }
        return 0L;
    }

    public static long levelMaxExperienceFromRows(long petLevel, List<PetLevelExperienceRow> levelRows) {
        if (levelRows != null) {
            for (PetLevelExperienceRow row : levelRows) {
                if (row != null && row.level() == petLevel) {
                    return row.maxExperience();
                }
            }
        }
        return 0L;
    }

    private static PetExperienceUpdate updateBase(long petLevel, long petExperience, long experienceDelta, LevelExperience levels) {
        long nextExperience = petExperience + experienceDelta;
        if (nextExperience < 0L) {
            nextExperience = 0L;
        }
        long nextLevel = petLevel;
        boolean leveledUp = false;
        long maxExperience = levels.maxExperience(petLevel);
        if (maxExperience > 0L && nextExperience >= maxExperience && levels.maxExperience(petLevel + 1L) > 0L) {
            nextLevel = petLevel + 1L;
            nextExperience = 0L;
            leveledUp = true;
        }
        return new PetExperienceUpdate(nextLevel, nextExperience, leveledUp, "", "");
    }

    private static PetExperienceUpdate withPayloads(
        PetExperienceUpdate result,
        long botEntityId,
        String petName,
        String petFigure,
        long petEnergy,
        long petNutrition,
        long petScratches,
        long experienceDelta
    ) {
        return new PetExperienceUpdate(
            result.petLevel(),
            result.petExperience(),
            result.leveledUp(),
            PetPayloads.experienceStatus(
                botEntityId,
                petName,
                petFigure,
                result.petLevel(),
                result.petExperience(),
                petEnergy,
                petNutrition,
                petScratches),
            PetPayloads.experience(botEntityId, experienceDelta, result.petExperience()));
    }

    private interface LevelExperience {
        long maxExperience(long petLevel);
    }
}
