package com.alphaseries.game.pet;

import com.alphaseries.util.StringUtils;

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
}
