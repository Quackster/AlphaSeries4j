package com.alphaseries.game.pet;

import com.alphaseries.dao.mysql.BotDao;
import com.alphaseries.db.Database;
import com.alphaseries.db.MySQL;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class PetBootCache {
    private PetBootCache() {
    }

    public record PetCommandCache(long commandCount, Map<Long, PetSettings.PetCommandRow> commandById) {
        public PetCommandCache {
            commandById = commandById == null ? Map.of() : Map.copyOf(commandById);
        }
    }

    /**
     * Original function: Proc_1_6_6C5830.
     */
    public static void loadPetRaceCache() {
        try {
            BotDao bots = botDao();
            if (bots == null) {
                return;
            }
            PetState.instance().setRaceRows(bots.petRaceCacheRows());
        } catch (Exception ignored) {
            // VB6 source suppresses boot cache failures.
        }
    }

    /**
     * Original function: Proc_1_7_6C5E10.
     */
    public static void loadPetLevelAndCommandCache() {
        try {
            BotDao bots = botDao();
            if (bots == null) {
                return;
            }
            PetState.instance().setLevelRows(buildPetLevelRows(bots.petLevelCacheRows()));
            long commandCount = bots.petCommandCount();
            PetState.instance().setCommandRows(buildPetCommandRows(bots.petCommandCacheRows()), commandCount);
        } catch (Exception ignored) {
            // VB6 source suppresses boot cache failures.
        }
    }

    /**
     * Original function: Proc_1_7_6C5E10.
     */
    public static List<PetSettings.PetLevelRow> buildPetLevelRows(List<PetLevelCacheRow> levelRows) {
        List<PetSettings.PetLevelRow> rows = new ArrayList<>();
        if (levelRows != null) {
            for (PetLevelCacheRow row : levelRows) {
                if (row != null) {
                    rows.add(new PetSettings.PetLevelRow(
                        row.level(),
                        row.maxEnergy(),
                        row.maxExperience(),
                        row.maxNutrition(),
                        4));
                }
            }
        }
        return rows;
    }

    /**
     * Original function: Proc_1_7_6C5E10.
     */
    public static PetCommandCache buildPetCommandCache(List<PetCommandCacheRow> commandRows) {
        long commandCount = 0L;
        Map<Long, PetSettings.PetCommandRow> commandById = new LinkedHashMap<Long, PetSettings.PetCommandRow>();
        for (PetSettings.PetCommandRow row : buildPetCommandRows(commandRows)) {
            commandById.put(row.commandId(), row);
            commandCount++;
        }
        return new PetCommandCache(commandCount, commandById);
    }

    /**
     * Original function: Proc_1_7_6C5E10.
     */
    public static List<PetSettings.PetCommandRow> buildPetCommandRows(List<PetCommandCacheRow> commandRows) {
        List<PetSettings.PetCommandRow> rows = new ArrayList<>();
        if (commandRows != null) {
            for (PetCommandCacheRow row : commandRows) {
                if (row != null) {
                    rows.add(new PetSettings.PetCommandRow(
                        row.commandId(),
                        row.requiredLevel(),
                        StringUtils.text(row.command()),
                        StringUtils.text(row.action()),
                        4));
                }
            }
        }
        return rows;
    }

    private static BotDao botDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new BotDao(database);
    }
}
