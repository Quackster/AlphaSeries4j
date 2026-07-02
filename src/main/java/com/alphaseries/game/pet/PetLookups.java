package com.alphaseries.game.pet;

import java.util.ArrayList;
import java.util.List;

import com.alphaseries.config.AppSettingsCache;
import com.alphaseries.dao.mysql.BotDao;
import com.alphaseries.dao.mysql.FurnitureDao;
import com.alphaseries.dao.mysql.PackageDao;
import com.alphaseries.dao.mysql.RoomDao;
import com.alphaseries.dao.mysql.UserDao;
import com.alphaseries.game.room.RoomObjectEntryPayloadArgs;
import com.alphaseries.game.room.RoomLookups;
import com.alphaseries.game.user.UserLookups;
import com.alphaseries.messages.outgoing.SocialPayloads;
import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.RandomUtils;
import com.alphaseries.util.StringUtils;

public final class PetLookups {
    private PetLookups() {
    }

    public static PetCommandAction commandAction(
        long commandId,
        List<PetSettings.PetCommandRow> commandRows,
        BotDao bots
    ) {
        PetCommandAction result = PetProgress.commandAction(commandId, commandRows);
        if (result.found() || commandId <= 0L || bots == null) {
            return result;
        }
        try {
            PetCommandActionRow row = bots.petCommandAction(commandId).orElse(null);
            if (row != null) {
                return new PetCommandAction(true, row.requiredLevel(), row.action());
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
        return result;
    }

    public static long levelMaxExperience(long petLevel, List<PetSettings.PetLevelRow> levelRows, BotDao bots) {
        long maxExperience = PetProgress.levelMaxExperience(petLevel, levelRows);
        if (maxExperience > 0L || bots == null) {
            return maxExperience;
        }
        try {
            return bots.petLevelMaxExperience(petLevel);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return 0L;
        }
    }

    public static void loadRepresentedRoomBots(long roomSlot, long roomId, boolean botsEnabled, BotDao bots) {
        if (roomSlot <= 0L || roomId <= 0L || !botsEnabled || bots == null) {
            return;
        }
        try {
            for (BotRoomEntryRow row : bots.roomBotEntries(roomId)) {
                PetState.instance().allocateRepresentedBot(roomSlot, RepresentedBotEntry.from(row));
            }
        } catch (Exception ignored) {
            // VB6 source suppresses bot loading failures.
        }
    }

    public static String raceListPayload(String userId, String productPet, BotDao bots, UserDao users) {
        if (StringUtils.text(userId).isEmpty() || StringUtils.text(productPet).isEmpty() || bots == null) {
            return "";
        }
        try {
            long rankIndex = UserLookups.rank(userId, users);
            long hcLevel = UserLookups.hcLevel(userId, users);
            return PetPayloads.raceList(productPet, bots.petRaces(productPet), rankIndex, hcLevel);
        } catch (Exception ignored) {
            return "";
        }
    }

    public static String inventoryPayload(long userId, BotDao bots) {
        if (userId <= 0L || bots == null) {
            return "";
        }
        try {
            return PetPayloads.inventoryList(bots.inventoryPets(userId));
        } catch (Exception ignored) {
            return "";
        }
    }

    public static String packagePreviewPayload(
        long furnitureId,
        long roomId,
        FurnitureDao furniture,
        PackageDao packages
    )
        throws Exception {

        if (furnitureId <= 0L || roomId <= 0L || furniture == null || packages == null) {
            return "";
        }
        FurnitureDao.RoomFurnitureProduct furnitureProduct = furniture.roomFurnitureProductById(furnitureId, roomId)
            .orElse(null);
        if (furnitureProduct == null || furnitureProduct.productId() <= 0L) {
            return "";
        }
        PackageDao.PetPackage petPackage = petPackageForProduct(furnitureProduct.productId(), packages);
        if (petPackage == null) {
            return "";
        }
        return PetPayloads.packagePreview(
            furnitureId,
            petPackage.petType(),
            petPackage.race(),
            StringUtils.text(petPackage.color()));
    }

    public static String packagePetFigure(long productId, PackageDao packages)
        throws Exception {

        PackageDao.PetPackage petPackage = petPackageForProduct(productId, packages);
        if (petPackage == null) {
            return "";
        }
        return petPackage.petType() + " " + petPackage.race() + " " + StringUtils.text(petPackage.color());
    }

    public static PetPackagePlacement packagePlacement(
        long botId,
        String petName,
        String petFigure,
        long furnitureId,
        long validationCode
    ) {
        if (botId <= 0L) {
            return emptyPackagePlacement();
        }
        PetInventoryRow inventoryRow = new PetInventoryRow(botId, petName, petFigure, 0L);
        return new PetPackagePlacement(
            botId,
            PetPayloads.inventoryAdd(inventoryRow),
            PetPayloads.packageNameValidation(furnitureId, validationCode, petName));
    }

    public static PetPackagePlacement packagePlacementAction(
        long furnitureId,
        long roomId,
        long userId,
        String petName,
        long validationCode,
        FurnitureDao furniture,
        PackageDao packages,
        BotDao bots,
        RoomDao rooms
    )
        throws Exception {

        if (furnitureId <= 0L || roomId <= 0L || userId <= 0L
            || furniture == null || packages == null || bots == null) {
            return emptyPackagePlacement();
        }
        FurnitureDao.RoomFurnitureOwnerProduct furnitureProduct = furniture.roomFurnitureOwnerProduct(furnitureId, roomId)
            .orElse(null);
        if (furnitureProduct == null || furnitureProduct.productId() <= 0L) {
            return emptyPackagePlacement();
        }
        String userIdText = String.valueOf(userId);
        if (furnitureProduct.ownerId() != userId
            && !RoomLookups.userOwnsRoom(userIdText, roomId, rooms)
            && !RoomLookups.userHasRoomRight(userIdText, roomId, rooms)) {
            return emptyPackagePlacement();
        }
        String petFigure = packagePetFigure(furnitureProduct.productId(), packages);
        if (petFigure.isEmpty()) {
            return emptyPackagePlacement();
        }
        bots.insertPetBot(userId, petFigure.toLowerCase(), petName);
        long botId = bots.newestPetBotId(userId);
        if (botId <= 0L) {
            return emptyPackagePlacement();
        }
        bots.insertPetData(botId, userId);
        return packagePlacement(botId, petName, petFigure, furnitureId, validationCode);
    }

    public static long nameValidationCode(String petName) {
        return PetPayloads.nameValidationCode(petName);
    }

    public static String nameValidationPayload(String petName) {
        return PetPayloads.nameValidation(petName);
    }

    public static String packageNameValidationPayload(long furnitureId, long validationCode, String petName) {
        return PetPayloads.packageNameValidation(furnitureId, validationCode, petName);
    }

    public static String statusPayload(long requestedId, BotDao bots) {
        if (requestedId <= 0L || bots == null) {
            return "";
        }
        try {
            RepresentedBotRegistry.RepresentedBotIdentity identity =
                PetState.instance().representedBots().identityFromEntityOrBotId(requestedId);
            long botId = identity.botId();
            if (botId <= 0L) {
                return "";
            }
            long botEntityId = identity.entityId() <= 0L ? botId : identity.entityId();
            PetStatusRow petStatus = bots.petStatus(botId).orElse(null);
            return petStatus == null ? "" : PetPayloads.status(botEntityId, petStatus);
        } catch (Exception ignored) {
            return "";
        }
    }

    private static PackageDao.PetPackage petPackageForProduct(long productId, PackageDao packages)
        throws Exception {

        if (productId <= 0L || packages == null) {
            return null;
        }
        PackageDao.PackageRow packageRow = packages.packageByProduct(productId).orElse(null);
        if (packageRow == null) {
            return null;
        }
        String packageType = StringUtils.text(packageRow.secondaryType()).toLowerCase();
        long containedPetId = packageRow.containedId();
        if (!"packages_pets".equals(packageType) || containedPetId <= 0L) {
            return null;
        }
        return packages.petPackage(containedPetId).orElse(null);
    }

    public static PetRoomOccupants roomOccupants(long roomSlot) {
        if (roomSlot <= 0L) {
            return emptyRoomOccupants();
        }
        PacketBuilder occupantPayload = PacketBuilder.create();
        PacketBuilder statusPayload = PacketBuilder.create();
        long occupantCount = 0L;
        long statusCount = 0L;
        for (long botEntityId : PetState.instance().representedBots().entityIdsForRoom(roomSlot, 0)) {
            if (botEntityId <= 0L) {
                continue;
            }
            RepresentedBotRegistry.RepresentedBotRecord bot = PetState.instance().representedBots().record(botEntityId);
            long positionX = bot.positionX();
            long positionY = bot.positionY();
            String positionZ = bot.positionZ().isEmpty() ? "0.0" : bot.positionZ();
            long directionValue = bot.positionR();
            String botEntry = SocialPayloads.roomObjectEntry(new RoomObjectEntryPayloadArgs(
                String.valueOf(botEntityId),
                bot.name(),
                bot.figure(),
                "M",
                String.valueOf(botEntityId),
                String.valueOf(positionX),
                String.valueOf(positionY),
                positionZ,
                "2"));
            if (!botEntry.isEmpty()) {
                occupantPayload.appendRaw(botEntry);
                statusPayload.appendRaw(SocialPayloads.roomOccupantStatus(
                    botEntityId, positionX, positionY, positionZ, directionValue));
                occupantCount++;
                statusCount++;
            }
        }
        return new PetRoomOccupants(occupantCount, statusCount, occupantPayload.build(), statusPayload.build());
    }

    public static long levelForOwnerTarget(long requestedId, long userId, BotDao bots) {
        if (requestedId <= 0L || userId <= 0L || bots == null) {
            return 0L;
        }
        try {
            long botId = PetState.instance().representedBots().identityFromEntityOrBotId(requestedId).botId();
            return botId <= 0L ? 0L : bots.petLevelForOwner(botId, userId);
        } catch (Exception ignored) {
            return 0L;
        }
    }

    public static PetCommandExecution commandExecution(
        long requestedId,
        long commandId,
        long roomId,
        List<PetSettings.PetCommandRow> commandRows,
        AppSettingsCache settings,
        BotDao bots
    )
        throws Exception {

        if (requestedId <= 0L || commandId <= 0L || roomId <= 0L || bots == null) {
            return emptyCommandExecution();
        }
        RepresentedBotRegistry.RepresentedBotIdentity identity =
            PetState.instance().representedBots().identityFromEntityOrBotId(requestedId);
        long botEntityId = identity.entityId();
        long botId = identity.botId();
        if (botId <= 0L) {
            return emptyCommandExecution();
        }
        PetCommandTargetRow pet = bots.petCommandTarget(botId, roomId).orElse(null);
        if (pet == null) {
            return emptyCommandExecution();
        }
        PetCommandAction commandAction = commandAction(commandId, commandRows, bots);
        if (!commandAction.found() || commandAction.requiredLevel() > pet.level()) {
            return emptyCommandExecution();
        }
        String actionPayload = commandAction.action().isEmpty()
            ? ""
            : PetPayloads.commandAction(botEntityId, commandAction.action(), commandId);
        String speechPayload = "";
        long experienceDelta = 0L;
        if (pet.energy() < 250L || pet.nutrition() < 250L) {
            AppSettingsCache settingsCache = settings == null ? AppSettingsCache.empty() : settings;
            String commandSpeech = RandomUtils.longInclusive(0, 2) == 0L
                ? settingsCache.valueOrDefault("com.client.bot.pet.sad.speech", "gst thr")
                : settingsCache.valueOrDefault("com.client.bot.pet.angry.speech", "gst grr");
            if (!commandSpeech.isEmpty()) {
                speechPayload = PetPayloads.speech(botEntityId, commandSpeech);
            }
        } else {
            experienceDelta = commandId * 10L;
        }
        return new PetCommandExecution(commandId, botEntityId, actionPayload, speechPayload, experienceDelta);
    }

    public static PetExperienceAward experienceAward(
        long requestedId,
        long experienceDelta,
        AppSettingsCache settings,
        BotDao bots
    )
        throws Exception {

        if (requestedId <= 0L || bots == null) {
            return emptyExperienceAward();
        }
        RepresentedBotRegistry.RepresentedBotIdentity identity =
            PetState.instance().representedBots().identityFromEntityOrBotId(requestedId);
        long botEntityId = identity.entityId();
        long botId = identity.botId();
        if (botId <= 0L) {
            return emptyExperienceAward();
        }
        PetExperienceStateRow petState = bots.petExperienceState(botId).orElse(null);
        if (petState == null) {
            return emptyExperienceAward();
        }
        if (botEntityId <= 0L) {
            botEntityId = botId;
        }
        PetExperienceUpdate update = PetProgress.experienceUpdateFromRows(
            botEntityId,
            petState.name(),
            petState.figure(),
            petState.level(),
            petState.experience(),
            petState.energy(),
            petState.nutrition(),
            petState.scratches(),
            experienceDelta,
            bots.petLevelExperienceRows());

        String levelSpeechPayload = "";
        if (update.leveledUp() && petState.roomId() > 0L) {
            AppSettingsCache settingsCache = settings == null ? AppSettingsCache.empty() : settings;
            String levelSpeech = settingsCache.valueOrDefault("com.client.bot.pet.level_up.speech", "gst sml");
            if (!levelSpeech.isEmpty()) {
                levelSpeechPayload = PetPayloads.speech(botEntityId, levelSpeech);
            }
        }
        return new PetExperienceAward(
            botId,
            botEntityId,
            petState.roomId(),
            update.petLevel(),
            update.petExperience(),
            levelSpeechPayload,
            update.statusPayload(),
            update.experiencePayload());
    }

    public static PetScratchAction scratchAction(long requestedId, long userId, BotDao bots, UserDao users)
        throws Exception {

        if (requestedId <= 0L || userId <= 0L || bots == null || users == null) {
            return emptyScratchAction();
        }
        RepresentedBotRegistry.RepresentedBotIdentity identity =
            PetState.instance().representedBots().identityFromEntityOrBotId(requestedId);
        long botEntityId = identity.entityId();
        long botId = identity.botId();
        if (botId <= 0L || users.scratchAmount(userId) <= 0L) {
            return emptyScratchAction();
        }
        PetScratchRow pet = bots.scratchTarget(botId).orElse(null);
        if (pet == null) {
            return emptyScratchAction();
        }
        long scratches = pet.scratches() + 1L;
        if (botEntityId <= 0L) {
            botEntityId = botId;
        }
        return new PetScratchAction(
            botId,
            userId,
            scratches,
            PetPayloads.scratch(botEntityId, userId, scratches, pet.name(), pet.figure()));
    }

    public static PetPickupAction pickupAction(long botEntityId, BotDao bots)
        throws Exception {

        if (botEntityId <= 0L || bots == null) {
            return emptyPickupAction();
        }
        RepresentedBotRegistry.RepresentedBotRecord bot = PetState.instance().representedBots().record(botEntityId);
        long botId = bot.botId();
        if (botId <= 0L) {
            return emptyPickupAction();
        }
        long scratches = bots.petScratches(botId);
        PetInventoryRow inventoryRow = new PetInventoryRow(botId, bot.name(), bot.figure().toLowerCase(), scratches);
        return new PetPickupAction(
            botId,
            botEntityId,
            PetPayloads.removedFromRoom(botEntityId),
            PetPayloads.inventoryAdd(inventoryRow));
    }

    public static void completePickup(PetPickupAction pickup) {
        if (pickup != null && pickup.valid()) {
            PetState.instance().removeRepresentedBotRecord(pickup.botEntityId());
        }
    }

    public static PetPlacementAction placementAction(
        long petId,
        long userId,
        long roomId,
        long positionX,
        long positionY,
        long positionR,
        BotDao bots,
        RoomDao rooms
    )
        throws Exception {

        if (petId <= 0L || userId <= 0L || roomId <= 0L || bots == null || rooms == null) {
            return emptyPlacementAction();
        }
        long roomSlot = rooms.roomSlot(roomId);
        if (roomSlot <= 0L) {
            return emptyPlacementAction();
        }
        String positionZ = String.valueOf(NumberUtils.parseLong(rooms.modelHeightmap(roomId)));
        PetPlacementRow pet = bots.availablePetForPlacement(petId, userId).orElse(null);
        if (pet == null) {
            return emptyPlacementAction();
        }
        PetState petRegistry = PetState.instance();
        long botEntityId = petRegistry.allocateRepresentedBot(
            roomSlot,
            RepresentedBotEntry.from(pet, positionX, positionY, positionZ, positionR));
        if (botEntityId <= 0L) {
            return emptyPlacementAction();
        }
        petRegistry.storeRepresentedBotPosition(botEntityId, positionX, positionY, positionZ, positionR);
        return new PetPlacementAction(
            pet.petId(),
            botEntityId,
            roomId,
            positionX,
            positionY,
            positionZ,
            positionR,
            petRegistry.representedBotRoomEntryPayload(botEntityId),
            PetPayloads.placed(pet.petId()));
    }

    public static PetTutorialGuideSpawn tutorialGuideSpawn(
        long roomId,
        AppSettingsCache settings,
        BotDao bots,
        RoomDao rooms
    )
        throws Exception {

        if (roomId <= 0L || bots == null || rooms == null) {
            return emptyTutorialGuideSpawn();
        }
        AppSettingsCache settingsCache = settings == null ? AppSettingsCache.empty() : settings;
        if (NumberUtils.parseLong(settingsCache.valueOrDefault("com.client.rooms.bots.guide.enabled", "0")) == 0L) {
            return emptyTutorialGuideSpawn();
        }
        long roomSlot = rooms.roomSlot(roomId);
        if (roomSlot <= 0L) {
            return emptyTutorialGuideSpawn();
        }
        long guideBotId = NumberUtils.parseLong(settingsCache.valueOrDefault("com.client.bot.guide.id", "0"));
        if (guideBotId <= 0L || !PetState.instance().representedBots().entityIdsForRoom(roomSlot, guideBotId).isEmpty()) {
            return emptyTutorialGuideSpawn();
        }
        BotRoomEntryRow guide = bots.botRoomEntry(guideBotId).orElse(null);
        if (guide == null) {
            return emptyTutorialGuideSpawn();
        }
        long botEntityId = PetState.instance().allocateRepresentedBot(roomSlot, RepresentedBotEntry.from(guide));
        return botEntityId <= 0L
            ? emptyTutorialGuideSpawn()
            : new PetTutorialGuideSpawn(botEntityId, "@aYjO");
    }

    public static PetTutorialGuideRemoval tutorialGuideRemoval(
        long requestedEntityId,
        long roomId,
        AppSettingsCache settings,
        RoomDao rooms
    )
        throws Exception {

        if (roomId <= 0L || rooms == null) {
            return emptyTutorialGuideRemoval();
        }
        long roomSlot = rooms.roomSlot(roomId);
        if (roomSlot <= 0L) {
            return emptyTutorialGuideRemoval();
        }
        List<Long> entityIds = List.of();
        if (requestedEntityId > 0L) {
            if (PetState.instance().representedBots().isEntityInRoom(requestedEntityId, roomSlot)) {
                entityIds = List.of(requestedEntityId);
            }
        } else {
            AppSettingsCache settingsCache = settings == null ? AppSettingsCache.empty() : settings;
            long guideBotId = NumberUtils.parseLong(settingsCache.valueOrDefault("com.client.bot.guide.id", "0"));
            entityIds = PetState.instance().representedBots().entityIdsForRoom(roomSlot, guideBotId);
        }
        if (entityIds.isEmpty()) {
            return emptyTutorialGuideRemoval();
        }
        long removedCount = 0L;
        List<String> removedPayloads = new ArrayList<>();
        for (long botEntityId : entityIds) {
            if (botEntityId > 0L) {
                removedPayloads.add(PetPayloads.removedFromRoom(botEntityId));
                PetState.instance().removeRepresentedBotRecord(botEntityId);
                removedCount++;
            }
        }
        return new PetTutorialGuideRemoval(removedCount, removedPayloads);
    }

    private static PetCommandExecution emptyCommandExecution() {
        return new PetCommandExecution(0L, 0L, "", "", 0L);
    }

    private static PetExperienceAward emptyExperienceAward() {
        return new PetExperienceAward(0L, 0L, 0L, 0L, 0L, "", "", "");
    }

    private static PetScratchAction emptyScratchAction() {
        return new PetScratchAction(0L, 0L, 0L, "");
    }

    private static PetPickupAction emptyPickupAction() {
        return new PetPickupAction(0L, 0L, "", "");
    }

    private static PetPlacementAction emptyPlacementAction() {
        return new PetPlacementAction(0L, 0L, 0L, 0L, 0L, "", 0L, "", "");
    }

    private static PetPackagePlacement emptyPackagePlacement() {
        return new PetPackagePlacement(0L, "", "");
    }

    private static PetRoomOccupants emptyRoomOccupants() {
        return new PetRoomOccupants(0L, 0L, "", "");
    }

    private static PetTutorialGuideSpawn emptyTutorialGuideSpawn() {
        return new PetTutorialGuideSpawn(0L, "");
    }

    private static PetTutorialGuideRemoval emptyTutorialGuideRemoval() {
        return new PetTutorialGuideRemoval(0L, List.of());
    }
}
