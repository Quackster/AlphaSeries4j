package com.alphaseries.game.pet;

import com.alphaseries.protocol.PacketBuilder;
import java.util.List;

public final class PetSettings {
    private final List<PetRaceCacheRow> raceRows;
    private final List<PetLevelRow> levelRows;
    private final List<PetCommandRow> commandRows;
    private final long commandCount;

    private PetSettings(List<PetRaceCacheRow> raceRows, List<PetLevelRow> levelRows, List<PetCommandRow> commandRows, long commandCount) {
        this.raceRows = copyRows(raceRows);
        this.levelRows = copyRows(levelRows);
        this.commandRows = copyRows(commandRows);
        this.commandCount = commandCount;
    }

    public static PetSettings empty() {
        return new PetSettings(List.of(), List.of(), List.of(), 0L);
    }

    public static PetSettings fromRaceRows(List<PetRaceCacheRow> raceRows, List<PetLevelRow> levelRows,
                                           List<PetCommandRow> commandRows, long commandCount) {
        return new PetSettings(raceRows, levelRows, commandRows, commandCount);
    }

    public List<PetLevelRow> levels() {
        return List.copyOf(levelRows);
    }

    public List<PetRaceCacheRow> races() {
        return List.copyOf(raceRows);
    }

    public List<PetCommandRow> commands() {
        return List.copyOf(commandRows);
    }

    public long commandCount() {
        return commandCount;
    }

    public void appendCommandListPayloadTo(PacketBuilder packet, long petLevel) {
        PetPayloads.appendCommandListTo(packet, petLevel, commandRows);
    }

    public record PetCommandRow(long commandId, long requiredLevel, String command, String action, int fieldCount) {
    }

    public record PetLevelRow(long level, long maxEnergy, long maxExperience, long maxNutrition, int fieldCount) {
    }

    private static <T> List<T> copyRows(List<T> rows) {
        return rows == null ? List.of() : List.copyOf(rows);
    }

}
