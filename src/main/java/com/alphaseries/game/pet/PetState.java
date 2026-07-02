package com.alphaseries.game.pet;

public final class PetState {
    private static final PetState INSTANCE = new PetState();

    private PetSettings settings = PetSettings.empty();
    private RepresentedBotRegistry representedBots = RepresentedBotRegistry.empty();

    private PetState() {
    }

    public static PetState instance() {
        return INSTANCE;
    }

    public synchronized PetSettings settings() {
        return settings;
    }

    public synchronized RepresentedBotRegistry representedBots() {
        return representedBots;
    }

    public synchronized void setRepresentedBots(RepresentedBotRegistry representedBots) {
        this.representedBots = representedBots == null ? RepresentedBotRegistry.empty() : representedBots;
    }

    public synchronized void setSettings(PetSettings settings) {
        this.settings = settings == null ? PetSettings.empty() : settings;
    }

    public synchronized void setRaceRows(java.util.List<PetRaceCacheRow> raceRows) {
        settings = PetSettings.fromRaceRows(
            raceRows == null ? java.util.List.of() : java.util.List.copyOf(raceRows),
            settings.levels(),
            settings.commands(),
            settings.commandCount());
    }

    public synchronized void setLevelRows(java.util.List<PetSettings.PetLevelRow> levelRows) {
        settings = PetSettings.fromRaceRows(
            settings.races(),
            levelRows == null ? java.util.List.of() : java.util.List.copyOf(levelRows),
            settings.commands(),
            settings.commandCount());
    }

    public synchronized void setCommandRows(java.util.List<PetSettings.PetCommandRow> commandRows, long commandCount) {
        settings = PetSettings.fromRaceRows(
            settings.races(),
            settings.levels(),
            commandRows == null ? java.util.List.of() : java.util.List.copyOf(commandRows),
            commandCount);
    }

    public synchronized long reserveRepresentedBotSlot() {
        return representedBots.reserveSlot();
    }

    /**
     * Original function: Proc_6_187_7CD700.
     */
    public synchronized long allocateRepresentedBot(long roomSlot, RepresentedBotEntry botEntry) {
        if (roomSlot <= 0L) {
            return 0L;
        }
        long botEntityId = reserveRepresentedBotSlot();
        if (botEntityId <= 0L) {
            return 0L;
        }
        representedBots.storeEntry(botEntityId, roomSlot, botEntry);
        return botEntityId;
    }

    public synchronized void removeRepresentedBotRecord(long botEntityId) {
        representedBots.removeRecord(botEntityId);
    }

    public synchronized void storeRepresentedBotPosition(
        long botEntityId,
        long positionX,
        long positionY,
        String positionZ,
        long positionR
    ) {
        representedBots.storePosition(botEntityId, positionX, positionY, positionZ, positionR);
    }

    public synchronized String representedBotRoomEntryPayload(long botEntityId) {
        RepresentedBotRegistry.RepresentedBotRecord bot = representedBots.record(botEntityId);
        if (botEntityId <= 0L || bot.botId() <= 0L) {
            return "";
        }
        return PetPayloads.representedBotRoomEntry(
            botEntityId,
            bot.name(),
            bot.positionX(),
            bot.positionY(),
            bot.positionZ(),
            bot.positionR(),
            bot.figure());
    }
}
