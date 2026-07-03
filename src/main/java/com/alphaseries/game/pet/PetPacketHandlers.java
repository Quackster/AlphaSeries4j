package com.alphaseries.game.pet;

import com.alphaseries.config.AppConfigState;
import com.alphaseries.dao.mysql.DaoProvider;
import com.alphaseries.dao.mysql.BotDao;
import com.alphaseries.dao.mysql.FurnitureDao;
import com.alphaseries.dao.mysql.PackageDao;
import com.alphaseries.dao.mysql.RoomDao;
import com.alphaseries.dao.mysql.UserDao;
import com.alphaseries.game.room.FurnitureStateWrites;
import com.alphaseries.game.room.RoomState;
import com.alphaseries.messages.outgoing.FurniturePayloads;
import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.server.runtime.SessionLookups;
import com.alphaseries.server.runtime.SocketDelivery;
import com.alphaseries.util.NumberUtils;

public final class PetPacketHandlers {
    private PetPacketHandlers() {
    }

    /**
     * Original function: Proc_6_86_73B0D0.
     */
    public static String sendPackagePreview(int socketIndex, PetWire.PackagePreviewRequest request) {
        try {
            long furnitureId = request.furnitureId();
            if (socketIndex <= 0 || furnitureId <= 0L) {
                return "";
            }
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long roomId = SessionLookups.currentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return "";
            }
            FurnitureDao furniture = furnitureDao();
            PackageDao packages = packageDao();
            if (furniture == null || packages == null) {
                return "";
            }
            String payload = PetLookups.packagePreviewPayload(furnitureId, roomId, furniture, packages);
            if (payload.isEmpty()) {
                return "";
            }
            SocketDelivery.sendToSocket(socketIndex, payload);
            return payload;
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_87_73C120.
     */
    public static String placeFromPackage(int socketIndex, PetWire.PackagePlacementRequest request) {
        try {
            long furnitureId = request.furnitureId();
            String petName = request.petName();
            long validationCode = PetLookups.nameValidationCode(petName);
            if (validationCode > 0L) {
                SocketDelivery.sendToSocket(socketIndex, PetLookups.packageNameValidationPayload(furnitureId, validationCode, petName));
                return "";
            }
            if (socketIndex <= 0 || furnitureId <= 0L) {
                return "";
            }
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long roomId = SessionLookups.currentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return "";
            }
            long numericUserId = NumberUtils.parseLong(userId);
            FurnitureDao furniture = furnitureDao();
            PetPackagePlacement placement = PetLookups.packagePlacementAction(
                furnitureId,
                roomId,
                numericUserId,
                petName,
                validationCode,
                furniture,
                packageDao(),
                botDao(),
                roomDao());
            if (!placement.valid()) {
                return "";
            }
            if (placement.hasInventoryAddPayload()) {
                SocketDelivery.sendToSocket(socketIndex, placement.inventoryAddPayload());
            }
            RoomState.instance().setFurnitureRoomCache(FurnitureStateWrites.removeMarker(
                RoomState.instance().furnitureRoomCache(), roomId, furnitureId));
            SocketDelivery.broadcastToCurrentRoom(socketIndex, FurniturePayloads.floorItemRemovedWithState(furnitureId, "H"));
            if (furniture != null) {
                furniture.deleteFurniture(furnitureId);
            }
            SocketDelivery.sendToSocket(socketIndex, placement.nameValidationPayload());
            return String.valueOf(placement.botId());
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_177_7C6580.
     */
    public static String sendRaceList(int socketIndex, PetWire.RaceListRequest request) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            String productPet = request.productPet();
            if (productPet.isEmpty()) {
                return "";
            }
            BotDao bots = botDao();
            if (bots == null) {
                return "";
            }
            String payload = PetLookups.raceListPayload(NumberUtils.parseLong(userId), productPet, bots, userDao());
            SocketDelivery.sendToSocket(socketIndex, payload);
            return payload;
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_178_7C6E60.
     */
    public static String sendInventory(int socketIndex) {
        try {
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            BotDao bots = botDao();
            if (bots == null) {
                return "";
            }
            String payload = PetLookups.inventoryPayload(NumberUtils.parseLong(userId), bots);
            SocketDelivery.sendToSocket(socketIndex, payload);
            return payload;
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_182_7CAAD0.
     */
    public static String validateName(int socketIndex, PetWire.NameValidationRequest request) {
        try {
            String requestedName = request.petName();
            String payload = PetLookups.nameValidationPayload(requestedName);
            SocketDelivery.sendToSocket(socketIndex, payload);
            return payload;
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_183_7CABF0.
     */
    public static String sendStatus(int socketIndex, PetWire.PetIdRequest request) {
        try {
            long requestedId = request.petId();
            if (socketIndex <= 0 || requestedId <= 0L) {
                return "";
            }
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            BotDao bots = botDao();
            if (bots == null) {
                return "";
            }
            String payload = PetLookups.statusPayload(requestedId, bots);
            if (!payload.isEmpty()) {
                SocketDelivery.sendToSocket(socketIndex, payload);
            }
            return payload;
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_184_7CBDA0.
     */
    public static String sendCommandList(int socketIndex, long petLevel) {
        try {
            PacketBuilder packet = PacketBuilder.create();
            petSettings().appendCommandListPayloadTo(packet, petLevel);
            String payload = packet.build();
            if (socketIndex > 0) {
                SocketDelivery.sendToSocket(socketIndex, payload);
            }
            return payload;
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_7CC190.
     */
    public static String sendCommandListForTarget(int socketIndex, PetWire.PetIdRequest request) {
        try {
            long requestedId = request.petId();
            long petLevel = 0L;
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (!userId.isEmpty() && !"0".equals(userId)) {
                BotDao bots = botDao();
                if (bots != null) {
                    petLevel = PetLookups.levelForOwnerTarget(requestedId, NumberUtils.parseLong(userId), bots);
                }
            }
            return sendCommandList(socketIndex, petLevel);
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_7CA730.
     */
    public static long performCommand(int socketIndex, PetWire.CommandRequest request) {
        try {
            if (socketIndex <= 0) {
                return 0L;
            }
            long requestedId = request.petId();
            long commandId = request.commandId();
            if (requestedId <= 0L || commandId <= 0L) {
                return 0L;
            }
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            long roomId = SessionLookups.currentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return 0L;
            }
            BotDao bots = botDao();
            if (bots == null) {
                return 0L;
            }
            PetCommandExecution execution = PetLookups.commandExecution(
                requestedId, commandId, roomId, petSettings().commands(), AppConfigState.instance().settingsCache(), bots);
            if (!execution.valid()) {
                return 0L;
            }
            if (execution.hasActionPayload()) {
                SocketDelivery.broadcastToRoomUsers(roomId, execution.actionPayload());
            }
            if (execution.hasSpeechPayload()) {
                SocketDelivery.broadcastToRoomUsers(roomId, execution.speechPayload());
            } else if (execution.shouldAwardExperience()) {
                awardExperience(execution.botEntityId(), execution.experienceDelta());
            }
            return commandId;
        } catch (Exception ignored) {
            return 0L;
        }
    }

    /**
     * Original function: Proc_6_186_7CD040.
     */
    public static long scratch(int socketIndex, PetWire.PetIdRequest request) {
        try {
            long requestedId = request.petId();
            if (socketIndex <= 0 || requestedId <= 0L) {
                return 0L;
            }
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            long userIdValue = NumberUtils.parseLong(userId);
            UserDao users = userDao();
            BotDao bots = botDao();
            if (users == null || bots == null) {
                return 0L;
            }
            PetScratchAction scratch = PetLookups.scratchAction(requestedId, userIdValue, bots, users);
            if (!scratch.valid()) {
                return 0L;
            }
            bots.updatePetScratches(scratch.botId(), scratch.scratches());
            users.spendScratch(userIdValue);
            SocketDelivery.broadcastToCurrentRoom(socketIndex, scratch.payload());
            return scratch.scratches();
        } catch (Exception ignored) {
            return 0L;
        }
    }

    /**
     * Original function: Proc_6_179_7C7790.
     */
    public static long placeInRoom(int socketIndex, PetWire.RoomPlacementRequest request) {
        try {
            if (NumberUtils.parseLong(AppConfigState.instance().settingsCache().valueOrDefault("com.client.rooms.bots.pets.enabled", "0")) == 0L) {
                return 0L;
            }
            long petId = request.petId();
            long positionX = request.positionX();
            long positionY = request.positionY();
            long positionR = request.rotation();
            if (socketIndex <= 0 || petId <= 0L) {
                return 0L;
            }
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            long roomId = SessionLookups.currentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return 0L;
            }
            RoomDao rooms = roomDao();
            BotDao bots = botDao();
            if (rooms == null || bots == null) {
                return 0L;
            }
            PetPlacementAction placement = PetLookups.placementAction(
                petId, NumberUtils.parseLong(userId), roomId, positionX, positionY, positionR, bots, rooms);
            if (!placement.valid()) {
                return 0L;
            }
            bots.placeBotInRoom(
                placement.botId(),
                placement.roomId(),
                placement.positionX(),
                placement.positionY(),
                placement.positionZ(),
                placement.positionR());
            if (placement.hasRoomEntryPayload()) {
                SocketDelivery.broadcastToCurrentRoom(socketIndex, placement.roomEntryPayload());
            }
            SocketDelivery.sendToSocket(socketIndex, placement.placedPayload());
            return placement.botEntityId();
        } catch (Exception ignored) {
            return 0L;
        }
    }

    /**
     * Original function: Proc_6_188_7CF3C0.
     */
    public static long spawnTutorialGuide(int socketIndex) {
        try {
            if (socketIndex <= 0) {
                return 0L;
            }
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            long userIdValue = NumberUtils.parseLong(userId);
            UserDao users = userDao();
            RoomDao rooms = roomDao();
            BotDao bots = botDao();
            if (users == null || rooms == null || bots == null) {
                return 0L;
            }
            long tutorialGuide = users.tutorialGuide(userIdValue);
            if (tutorialGuide == 0L) {
                users.markTutorialGuide(userIdValue);
            }
            long roomId = SessionLookups.currentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return 0L;
            }
            PetTutorialGuideSpawn spawn = PetLookups.tutorialGuideSpawn(
                roomId, AppConfigState.instance().settingsCache(), bots, rooms);
            if (spawn.valid()) {
                SocketDelivery.sendToSocket(socketIndex, spawn.payload());
            }
            return spawn.botEntityId();
        } catch (Exception ignored) {
            return 0L;
        }
    }

    /**
     * Original function: Proc_6_189_7D0630.
     */
    public static long removeTutorialGuides(int socketIndex, PetWire.PetIdRequest request) {
        try {
            long requestedEntityId = request.petId();
            if (socketIndex <= 0) {
                return 0L;
            }
            String userId = SessionLookups.userIdTextFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            long roomId = SessionLookups.currentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return 0L;
            }
            RoomDao rooms = roomDao();
            if (rooms == null) {
                return 0L;
            }
            PetTutorialGuideRemoval removal = PetLookups.tutorialGuideRemoval(
                requestedEntityId, roomId, AppConfigState.instance().settingsCache(), rooms);
            if (!removal.hasRemovals()) {
                return 0L;
            }
            for (String removedPayload : removal) {
                SocketDelivery.broadcastToRoomUsers(roomId, removedPayload);
            }
            return removal.removedCount();
        } catch (Exception ignored) {
            return 0L;
        }
    }

    /**
     * Original function: Proc_6_185_7CC2D0.
     */
    private static long awardExperience(long botEntityId, long experienceDelta) {
        try {
            if (botEntityId <= 0L) {
                return 0L;
            }
            BotDao bots = botDao();
            if (bots == null) {
                return 0L;
            }
            PetExperienceAward award = PetLookups.experienceAward(
                botEntityId, experienceDelta, AppConfigState.instance().settingsCache(), bots);
            if (!award.valid()) {
                return 0L;
            }
            if (award.hasLevelSpeechPayload()) {
                SocketDelivery.broadcastToRoomUsers(award.roomId(), award.levelSpeechPayload());
            }
            bots.updatePetExperience(award.botId(), award.petLevel(), award.petExperience());
            if (award.hasRoomPayloads()) {
                SocketDelivery.broadcastToRoomUsers(award.roomId(), award.statusPayload());
                SocketDelivery.broadcastToRoomUsers(award.roomId(), award.experiencePayload());
            }
            return award.petLevel();
        } catch (Exception ignored) {
            return 0L;
        }
    }

    private static BotDao botDao() {
        return DaoProvider.botDao();
    }

    private static FurnitureDao furnitureDao() {
        return DaoProvider.furnitureDao();
    }

    private static PackageDao packageDao() {
        return DaoProvider.packageDao();
    }

    private static RoomDao roomDao() {
        return DaoProvider.roomDao();
    }

    private static UserDao userDao() {
        return DaoProvider.userDao();
    }

    private static PetSettings petSettings() {
        return PetState.instance().settings();
    }
}
