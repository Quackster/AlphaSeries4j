package com.alphaseries.game.pet;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class PetSettings {
    private final String raceRows;
    private final Object levelRows;
    private final Object commandRows;
    private final long commandCount;

    private PetSettings(String raceRows, Object levelRows, Object commandRows, long commandCount) {
        this.raceRows = StringUtils.text(raceRows);
        this.levelRows = levelRows == null ? "" : levelRows;
        this.commandRows = commandRows == null ? "" : commandRows;
        this.commandCount = commandCount;
    }

    public static PetSettings fromLegacy(String raceRows, Object levelRows, Object commandRows, long commandCount) {
        return new PetSettings(raceRows, levelRows, commandRows, commandCount);
    }

    public String raceRows() {
        return raceRows;
    }

    public Object levelRows() {
        return levelRows;
    }

    public Object commandRows() {
        return commandRows;
    }

    public long commandCount() {
        return commandCount;
    }

    public String commandListPayload(long petLevel) {
        return PetPayloads.commandList(petLevel, commandRows);
    }

    public static List<PetCommandRow> commandRows(Object rows) {
        List<PetCommandRow> commands = new ArrayList<>();
        if (rows instanceof PetCommandRow[] rowArray) {
            for (PetCommandRow command : rowArray) {
                if (command != null) {
                    commands.add(command);
                }
            }
            return commands;
        }
        if (rows instanceof List<?> rowList) {
            for (Object row : rowList) {
                if (row instanceof PetCommandRow command) {
                    commands.add(command);
                }
            }
            return commands;
        }
        return commands;
    }

    public static List<PetLevelRow> levelRows(Object rows) {
        List<PetLevelRow> levels = new ArrayList<>();
        for (String row : normalizeRows(rows)) {
            PetLevelRow level = levelRow(row);
            if (level != null) {
                levels.add(level);
            }
        }
        return levels;
    }

    public static PetLevelRow levelRow(String rowText) {
        if (StringUtils.text(rowText).isEmpty()) {
            return null;
        }
        String[] fields = StringUtils.text(rowText).split("\t", -1);
        return new PetLevelRow(
            NumberUtils.parseLong(StringUtils.field(fields, 0)),
            NumberUtils.parseLong(StringUtils.field(fields, 1)),
            fields.length);
    }

    private static String[] normalizeRows(Object rows) {
        if (rows == null) {
            return new String[0];
        }
        if (rows instanceof String[] rowArray) {
            return rowArray;
        }
        if (rows instanceof String[][] table) {
            String[] normalized = new String[table.length];
            for (int index = 0; index < table.length; index++) {
                normalized[index] = table[index] == null ? "" : String.join("\t", table[index]);
            }
            return normalized;
        }
        return StringUtils.text(rows).split("\r", -1);
    }

    public record PetCommandRow(long commandId, long requiredLevel, String command, String action, int fieldCount) {
    }

    public record PetLevelRow(long level, long maxExperience, int fieldCount) {
    }
}
