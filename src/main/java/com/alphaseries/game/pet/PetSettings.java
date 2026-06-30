package com.alphaseries.game.pet;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class PetSettings {
    private final String raceRows;
    private final List<PetLevelRow> levelRows;
    private final List<PetCommandRow> commandRows;
    private final int levelRowSlotCount;
    private final int commandRowSlotCount;
    private final long commandCount;

    private PetSettings(String raceRows, Object levelRows, Object commandRows, long commandCount) {
        this.raceRows = StringUtils.text(raceRows);
        this.levelRows = levelRows(levelRows);
        this.commandRows = commandRows(commandRows);
        this.levelRowSlotCount = rowSlotCount(levelRows, this.levelRows);
        this.commandRowSlotCount = rowSlotCount(commandRows, this.commandRows);
        this.commandCount = commandCount;
    }

    private PetSettings(String raceRows, List<PetLevelRow> levelRows, List<PetCommandRow> commandRows, long commandCount) {
        this.raceRows = StringUtils.text(raceRows);
        this.levelRows = copyRows(levelRows);
        this.commandRows = copyRows(commandRows);
        this.levelRowSlotCount = rowSlotCount(null, this.levelRows);
        this.commandRowSlotCount = rowSlotCount(null, this.commandRows);
        this.commandCount = commandCount;
    }

    public static PetSettings fromLegacy(String raceRows, Object levelRows, Object commandRows, long commandCount) {
        if (levelRows instanceof PetSettings settings) {
            return settings;
        }
        return new PetSettings(raceRows, levelRows, commandRows, commandCount);
    }

    public static PetSettings empty() {
        return new PetSettings("", "", "", 0L);
    }

    public static PetSettings fromRows(String raceRows, List<PetLevelRow> levelRows,
                                       List<PetCommandRow> commandRows, long commandCount) {
        return new PetSettings(raceRows, levelRows, commandRows, commandCount);
    }

    public String raceRows() {
        return raceRows;
    }

    public Object levelRows() {
        String[] rows = new String[levelRowSlotCount];
        for (PetLevelRow row : levelRows) {
            int index = (int) row.level();
            if (index >= 0 && index < rows.length) {
                rows[index] = row.maxEnergy() + "\t" + row.maxExperience() + "\t" + row.maxNutrition();
            }
        }
        return rows;
    }

    public Object commandRows() {
        PetCommandRow[] rows = new PetCommandRow[commandRowSlotCount];
        for (PetCommandRow row : commandRows) {
            int index = (int) row.commandId();
            if (index >= 0 && index < rows.length) {
                rows[index] = row;
            }
        }
        return rows;
    }

    public List<PetLevelRow> levels() {
        return List.copyOf(levelRows);
    }

    public List<PetCommandRow> commands() {
        return List.copyOf(commandRows);
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
        if (rows instanceof List<?> rowList) {
            List<PetLevelRow> levels = new ArrayList<>();
            for (Object row : rowList) {
                if (row instanceof PetLevelRow level) {
                    levels.add(level);
                }
            }
            return levels;
        }
        if (rows instanceof String[] rowArray) {
            List<PetLevelRow> levels = new ArrayList<>();
            for (int index = 0; index < rowArray.length; index++) {
                PetLevelRow level = indexedLevelRow(index, rowArray[index]);
                if (level != null) {
                    levels.add(level);
                }
            }
            return levels;
        }
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
            0L,
            NumberUtils.parseLong(StringUtils.field(fields, 1)),
            NumberUtils.parseLong(StringUtils.field(fields, 2)),
            fields.length);
    }

    private static PetLevelRow indexedLevelRow(long level, String rowText) {
        if (StringUtils.text(rowText).isEmpty()) {
            return null;
        }
        String[] fields = StringUtils.text(rowText).split("\t", -1);
        return new PetLevelRow(
            level,
            NumberUtils.parseLong(StringUtils.field(fields, 0)),
            NumberUtils.parseLong(StringUtils.field(fields, 1)),
            NumberUtils.parseLong(StringUtils.field(fields, 2)),
            fields.length);
    }

    private static String[] normalizeRows(Object rows) {
        if (rows == null) {
            return new String[0];
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

    public record PetLevelRow(long level, long maxEnergy, long maxExperience, long maxNutrition, int fieldCount) {
    }

    private static <T> List<T> copyRows(List<T> rows) {
        return rows == null ? List.of() : List.copyOf(rows);
    }

    private static int rowSlotCount(Object sourceRows, List<?> parsedRows) {
        if (sourceRows instanceof Object[] values) {
            return values.length;
        }
        long maxId = -1L;
        if (parsedRows != null) {
            for (Object row : parsedRows) {
                if (row instanceof PetLevelRow level) {
                    maxId = Math.max(maxId, level.level());
                } else if (row instanceof PetCommandRow command) {
                    maxId = Math.max(maxId, command.commandId());
                }
            }
        }
        return (int) Math.max(0L, maxId + 1L);
    }
}
