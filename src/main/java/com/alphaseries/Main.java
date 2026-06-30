package com.alphaseries;

import com.alphaseries.dao.mysql.FurnitureDao;
import com.alphaseries.dao.mysql.RoomDao;
import com.alphaseries.dao.mysql.ServerMaintenanceDao;
import com.alphaseries.dao.mysql.UserDao;
import com.alphaseries.db.Database;
import com.alphaseries.game.pet.RepresentedBotRegistry;
import com.alphaseries.game.room.FurnitureRoomCache;
import com.alphaseries.game.room.MovementStep;
import com.alphaseries.game.room.RepresentedRoomCache;
import com.alphaseries.game.room.RoomRollers;
import com.alphaseries.game.session.GameServerSessionState;
import com.alphaseries.server.packet.PacketSink;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

public final class Main {
    public static final String INITIALIZING_CAPTION_TEMPLATE = "Alpha Series [INITIALISIERE] - [%%]";
    public static final String INITIALIZED_STATE_TEXT = "INITIALISIERT";
    public static final String RUNNING_STATE_TEXT = "RUNNING";
    public static final String SERVER_EXIT_ERROR_PREFIX = "Server Exit Suburned following error: \r\n";
    public static final String UNKNOWN_PROBLEM_MESSAGE = "Unbekanntes Problem";
    private static final String[] DESIGN_CAPTIONS = new String[] {
        "Bitte warte...",
        "frame :: ADDONS",
        "Server by Privilege",
        "User Voice",
        "Source is only avaible for the author. Please do not share this Source!"
    };

    private static PacketSink preSessionPacketSink = (socketIndex, payload) -> { };

    private Main() {
    }

    public record GameServerPacket(String commandName, long socketIndex, String payload) {
    }

    public static void configurePreSessionPacketSink(PacketSink sink) {
        preSessionPacketSink = sink == null ? (socketIndex, payload) -> { } : sink;
    }

    public static String shiftIdentityText(String sourceText, long shiftAmount) {
        String source = StringUtils.text(sourceText);
        StringBuilder output = new StringBuilder(source.length());
        for (int index = 0; index < source.length(); index++) {
            output.append((char) ((source.charAt(index) + shiftAmount) & 0xFF));
        }
        return output.toString();
    }

    public static String easyGetIdentity(Object value) {
        return shiftIdentityText(StringUtils.text(value), -25L);
    }

    public static String createSuperEasyIdentity(Object value) {
        return shiftIdentityText(StringUtils.text(value), 2L);
    }

    public static String superEasyGetIdentity(Object value) {
        return shiftIdentityText(StringUtils.text(value), -2L);
    }

    public static String newPremiumCheck(Object valueOffset, Object encodedValue) {
        String encodedText = StringUtils.text(encodedValue);
        if (encodedText.isEmpty()) {
            return "";
        }
        long seedValue = encodedText.charAt(0);
        long offset = NumberUtils.parseLong(valueOffset);
        StringBuilder output = new StringBuilder(encodedText.length() - 1);
        for (int index = 1; index < encodedText.length(); index++) {
            output.append((char) (((encodedText.charAt(index) - seedValue) + offset) & 0xFF));
        }
        return output.toString();
    }

    public static String getIdentity(Object encodedValue, Object seedOffset) {
        String encodedText = StringUtils.text(encodedValue);
        if (encodedText.isEmpty()) {
            return "";
        }
        long seedValue = encodedText.charAt(0) - NumberUtils.parseLong(seedOffset);
        StringBuilder output = new StringBuilder(encodedText.length() - 1);
        for (int index = 1; index < encodedText.length(); index++) {
            output.append((char) (((encodedText.charAt(index) - index) - seedValue) & 0xFF));
        }
        return output.toString();
    }

    public static String Proc_0_22_68C1A0(Object... args) {
        try {
            String sourceText = args != null && args.length >= 1 ? StringUtils.text(args[0]) : "";
            long seedValue = NumberUtils.parseLong(Functions.Proc_10_3_809B90(0x41, 0x5A));
            StringBuilder output = new StringBuilder(sourceText.length() + 1);
            output.append((char) (seedValue & 0xFF));
            for (int index = 0; index < sourceText.length(); index++) {
                output.append((char) ((sourceText.charAt(index) + index + 1L + seedValue) & 0xFF));
            }
            return output.toString();
        } catch (Exception ex) {
            return "";
        }
    }

    public static String Proc_0_23_68C430(Object... args) {
        return shiftIdentityText(args != null && args.length >= 1 ? StringUtils.text(args[0]) : "", 7L);
    }

    public static final class LifecycleResult {
        public boolean success;
        public boolean shouldExit;
        public String caption = "";
        public String consoleTitle = "";
        public String productKey = "";
    }

    public static final class ResizeResult {
        public long width;
        public long height;
        public long logWidth;
        public long logHeight;
        public long frameWidth;
    }

    public static final class StartupResult {
        public boolean success;
        public String stage = "";
        public String message = "";

        public static StartupResult success() {
            StartupResult result = new StartupResult();
            result.success = true;
            return result;
        }

        public static StartupResult failure(String stage, String message) {
            StartupResult result = new StartupResult();
            result.stage = StringUtils.text(stage);
            result.message = StringUtils.text(message);
            return result;
        }
    }

    public static void Proc_0_24_68EEF0(Object... args) {
        // Empty in the recovered VB6 reference.
    }

    public static void Proc_0_25_68FBC0(Object... args) {
        long socketIndex = args != null && args.length >= 1 ? NumberUtils.parseLong(args[0]) : 0L;
        String packetData = args != null && args.length >= 2 ? StringUtils.text(args[1]) : "";
        try {
            if (Guardian.Proc_11_2_821390(socketIndex, 1, 0) != 1) {
                return;
            }
            if (isGameSessionReady(socketIndex)) {
                Filesystems.Proc_7_2_803D60(socketIndex, packetData, 0);
            } else {
                preSessionPacketSink.send((int) socketIndex, packetData);
            }
        } catch (Exception ex) {
            if (Licence.runtimeState().debugLoggingEnabled()) {
                Console.Proc_2_0_6D1510("[" + socketIndex + "] " + ex.getMessage() + " -> " + packetData,
                    "ERROR", "255");
                DataManager.appendTextFile(Functions.applicationPath + "/ERR.log",
                    "ERROR] " + packetData + " (" + ex.getMessage() + ")\r\n0\r\n\r\n\r\n");
                Functions.Proc_10_8_80A580(socketIndex, 0x60, 0);
            }
        }
    }

    public static LifecycleResult formInitialize(String captionTemplate) {
        LifecycleResult result = new LifecycleResult();
        try {
            Licence.resetRuntimeDefaults();
            if (Crypto.Proc_3_3_6D3240("K", -1, 0) != 3L) {
                result.shouldExit = true;
                return result;
            }
            Guardian.Proc_11_1_821240(Path.of(Functions.applicationPath, "CACHE", "ROOMS").toString(), 0, 0);
            Guardian.Proc_11_1_821240(Path.of(Functions.applicationPath, "CACHE", "PATHFINDER").toString(), 0, 0);
            Guardian.Proc_11_1_821240(Path.of(Functions.applicationPath, "CACHE", "USERS").toString(), 0, 0);
            result.consoleTitle = StringUtils.text(captionTemplate).replace("%%", Licence.runtimeState().productName());
            result.caption = javaCaptionFromConsoleTitle(result.consoleTitle);
            result.productKey = productKeyFromConfig(Handling.Proc_6_239_7FC170(
                Path.of(Functions.applicationPath, "config.ini").toString(), 0, 7));
            result.success = true;
            return result;
        } catch (Exception ignored) {
            result.shouldExit = true;
            return result;
        }
    }

    public static boolean formQueryUnload() {
        try {
            ServerMaintenanceDao maintenanceDao = serverMaintenanceDao();
            if (maintenanceDao != null) {
                maintenanceDao.resetConnectedUsers();
                maintenanceDao.resetOccupiedRoomSlots();
            }
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static boolean runServer(String caption, String licenceResponse) {
        try {
            if (DataManager.Proc_8_7_8051C0(licenceResponse, 0, 0)) {
                Boot.Proc_1_3_6BEBA0(0);
                return true;
            }
            return false;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static boolean runServer(LifecycleResult lifecycle) {
        return startServer(lifecycle).success;
    }

    public static StartupResult startServer(LifecycleResult lifecycle) {
        if (lifecycle == null) {
            return StartupResult.failure("lifecycle", "Lifecycle initialization did not return a result.");
        }
        try {
            if (DataManager.Proc_8_7_8051C0(
                new DataManager.LicenceCheckContext(lifecycle.productKey, Licence.runtimeState().productName()), 0, 0)) {
                Boot.Proc_1_3_6BEBA0(0);
                return StartupResult.success();
            }
            String message = DataManager.lastLicenceFailureMessage;
            if (message.isEmpty()) {
                message = "Licence check failed for product key '" + lifecycle.productKey + "'.";
            }
            return StartupResult.failure("licence", message);
        } catch (Exception ex) {
            return StartupResult.failure("server", ex.getMessage());
        }
    }

    public static String getProcessor() {
        return StringUtils.text(System.getenv("USERNAME")).isEmpty()
            ? StringUtils.text(System.getenv("USER"))
            : StringUtils.text(System.getenv("USERNAME"));
    }

    public static String productKeyFromConfig(String configText) {
        java.util.Map<String, String> config = Crypto.parseConfig(configText);
        String productKey = config.get("productkey");
        if (productKey == null || productKey.isEmpty()) {
            productKey = config.get("licence");
        }
        if (productKey != null) {
            return productKey;
        }

        String[] configParts = StringUtils.text(configText).split("=", -1);
        if (configParts.length < 8) {
            return "";
        }
        String[] lines = configParts[7].split("\\r?\\n", -1);
        return lines.length > 0 ? StringUtils.text(lines[0]) : "";
    }

    public static String javaCaptionFromConsoleTitle(String consoleTitle) {
        return StringUtils.text(consoleTitle).replace("[!]", "").trim().replaceAll(" {2,}", " ");
    }

    public static String initializedConsoleTitle(String consoleTitle) {
        String title = StringUtils.text(consoleTitle);
        title = title.replace("INITIALISIERE", INITIALIZED_STATE_TEXT);
        title = title.replace("INITIALIZING", RUNNING_STATE_TEXT);
        return title;
    }

    public static String[] designCaptions() {
        return DESIGN_CAPTIONS.clone();
    }

    public static String serverExitErrorMessage(String description) {
        return SERVER_EXIT_ERROR_PREFIX + StringUtils.text(description);
    }

    public static String gameServerUnknownEventAccept() {
        Guardian.setGameServerConnected(true);
        return "ACCEPT 16387";
    }

    public static String gameServerUnknownEventListen() {
        Guardian.setGameServerConnected(true);
        return "LISTEN";
    }

    public static ResizeResult formResize(long width, long height, long scaleWidth, long scaleHeight) {
        ResizeResult result = new ResizeResult();
        result.width = Math.max(width, 11085L);
        result.height = Math.max(height, 10245L);
        result.logWidth = scaleWidth;
        result.logHeight = scaleHeight - 525L;
        result.frameWidth = scaleWidth;
        return result;
    }

    public static boolean dataProcessTimer(long socketIndex) {
        try {
            if (Guardian.Proc_11_2_821390(socketIndex, 1, 0) != 1) {
                return false;
            }
            String packetData = popGameServerPacketData(socketIndex);
            if (packetData.isEmpty()) {
                return false;
            }
            Proc_0_25_68FBC0(socketIndex, packetData);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static void processGameServerData(String incomingData) {
        try {
            for (String packet : StringUtils.text(incomingData).split("\1", -1)) {
                if (packet.isEmpty()) {
                    continue;
                }
                GameServerPacket gamePacket = gameServerPacket(packet);
                if ("SHUTDOWN".equals(gamePacket.commandName())) {
                    if (gamePacket.socketIndex() > 0L) {
                        Handling.Proc_6_243_7FFEB0(gamePacket.socketIndex(), 0, 0);
                    }
                } else if ("LISTEN".equals(gamePacket.commandName())) {
                    if (gamePacket.socketIndex() > 0L) {
                        Guardian.Proc_11_3_821440(gamePacket.socketIndex(), 0, 0);
                    }
                } else if ("DATA".equals(gamePacket.commandName())) {
                    if (gamePacket.socketIndex() > 0L && !gamePacket.payload().isEmpty()) {
                        appendGameServerPacketPayload(gamePacket.socketIndex(), gamePacket.payload());
                    }
                } else if (gamePacket.socketIndex() > 0L) {
                    Handling.Proc_6_243_7FFEB0(gamePacket.socketIndex(), 0, 0);
                }
            }
        } catch (Exception ignored) {
            // VB6 source suppresses game-server read failures.
        }
    }

    public static long signerTimer() {
        long processed = 0L;
        try {
            FurnitureDao furniture = furnitureDao();
            FurnitureRoomCache.State cacheState = Licence.furnitureRoomCache();
            String furnitureMarkers = mainRepresentedEntityIds(cacheState.pendingFurnitureCache);
            String[] markerParts = furnitureMarkers.split("\\]", -1);
            for (int markerIndex = 0; markerIndex < markerParts.length; markerIndex++) {
                long furnitureId = mainRepresentedEntityIdAt(furnitureMarkers, markerIndex);
                if (furnitureId <= 0L) {
                    continue;
                }
                FurnitureDao.PendingFurnitureState pendingState = furniture.pendingFurnitureState(furnitureId).orElse(null);
                if (pendingState == null) {
                    cacheState.pendingFurnitureCache = FurnitureRoomCache.removePendingFurniture(cacheState.pendingFurnitureCache, furnitureId);
                    Licence.setFurnitureRoomCache(cacheState);
                    continue;
                }
                long roomId = pendingState.roomId();
                long signValue = pendingState.sign();
                if (roomId > 0L && signValue > 0L) {
                    long nextSignValue = signValue - 1L;
                    furniture.updateSignLimited(furnitureId, nextSignValue);
                    Handling.Proc_6_151_78AC20(roomId, furnitureId, nextSignValue);
                    Handling.Proc_6_246_8024C0(roomId, "AX" + furnitureId + '\2' + nextSignValue + '\2', 0);
                    processed++;
                    if (nextSignValue <= 0L) {
                        cacheState.pendingFurnitureCache = FurnitureRoomCache.removePendingFurniture(cacheState.pendingFurnitureCache, furnitureId);
                        cacheState.pendingRoomCache = FurnitureRoomCache.removePendingRoom(cacheState.pendingRoomCache, roomId);
                        Licence.setFurnitureRoomCache(cacheState);
                    }
                } else {
                    cacheState.pendingFurnitureCache = FurnitureRoomCache.removePendingFurniture(cacheState.pendingFurnitureCache, furnitureId);
                    Licence.setFurnitureRoomCache(cacheState);
                }
            }
            Licence.setFurnitureRoomCache(cacheState);
        } catch (Exception ignored) {
            // VB6 source suppresses timer failures.
        }
        return processed;
    }

    public static long botsTimer() {
        long moved = 0L;
        try {
            for (long entityId : Licence.representedBots().allocatedEntityIds()) {
                RepresentedBotRegistry.RepresentedBotRecord bot = Licence.representedBots().record(entityId);
                if (entityId <= 0L || bot.allowWalk() == 0L) {
                    continue;
                }
                long currentX = bot.positionX();
                long currentY = bot.positionY();
                long targetX = currentX + Functions.Proc_10_4_809CA0(-1, 1, 0);
                long targetY = currentY + Functions.Proc_10_4_809CA0(-1, 1, 0);
                Proc_0_28_6AD850(entityId, currentX, currentY, targetX, targetY);
                moved++;
            }
        } catch (Exception ignored) {
            // VB6 source suppresses timer failures.
        }
        return moved;
    }

    public static long walkingTimer(long roomSlot) {
        long moved = 0L;
        try {
            RepresentedRoomCache representedRooms = Licence.representedRooms();
            String userMarkers = mainRepresentedEntityIds(representedRooms.activeUserMarkers(roomSlot)
                + representedRooms.movingUserMarkers(roomSlot));
            String[] userParts = userMarkers.split("\\]", -1);
            for (int markerIndex = 0; markerIndex < userParts.length; markerIndex++) {
                long entityId = mainRepresentedEntityIdAt(userMarkers, markerIndex);
                if (entityId > 0L) {
                    Proc_0_29_6B0E10(entityId);
                    moved++;
                }
            }
            String botMarkers = mainRepresentedEntityIds(representedRooms.activeBotMarkers(roomSlot)
                + representedRooms.movingBotMarkers(roomSlot));
            String[] botParts = botMarkers.split("\\]", -1);
            for (int markerIndex = 0; markerIndex < botParts.length; markerIndex++) {
                long entityId = mainRepresentedEntityIdAt(botMarkers, markerIndex);
                if (entityId > 0L) {
                    Proc_0_28_6AD850(entityId);
                    moved++;
                }
            }
        } catch (Exception ignored) {
            // VB6 source suppresses timer failures.
        }
        return moved;
    }

    public static long pingTimer(long previousMostActiveCount) {
        long activeCount = 0L;
        try {
            ServerMaintenanceDao maintenanceDao = serverMaintenanceDao();
            if (maintenanceDao == null) {
                return 0L;
            }
            maintenanceDao.markSocketCheckTime();
            for (long socketIndex : Guardian.markedSocketIndexes()) {
                if (Guardian.Proc_11_2_821390(socketIndex, 0, 0) == 1) {
                    activeCount++;
                } else {
                    Handling.Proc_6_243_7FFEB0(socketIndex, 0, 0);
                }
            }
            if (activeCount > previousMostActiveCount) {
                maintenanceDao.updateMostActiveSockets(activeCount);
            }
            Handling.Proc_6_103_74A510(0, 0, 0);
        } catch (Exception ignored) {
            // VB6 source suppresses timer failures.
        }
        return activeCount;
    }

    public static long rollersTimer(long roomSlot) {
        long moved = 0L;
        try {
            long roomId = mainCurrentRoomIdForSlot(roomSlot);
            if (roomId <= 0L) {
                return 0L;
            }
            FurnitureDao furniture = furnitureDao();
            List<FurnitureDao.RollerFurniture> rollers = furniture.rollerFurnitureInRoom(roomId);
            if (rollers.isEmpty()) {
                return 0L;
            }
            for (FurnitureDao.RollerFurniture roller : rollers) {
                long rollerId = roller.furnitureId();
                long rollerX = roller.positionX();
                long rollerY = roller.positionY();
                String rollerZ = StringUtils.text(roller.positionZ());
                long rollerR = roller.rotation();
                long targetX = rollerX + RoomRollers.deltaX(rollerR);
                long targetY = rollerY + RoomRollers.deltaY(rollerR);
                if (rollerId <= 0L || (targetX == rollerX && targetY == rollerY)
                    || Functions.roomPositionAvailable(roomId, targetX, targetY) == 0L) {
                    continue;
                }
                long movedId = mainRollerFurnitureOnTile(roomId, rollerId, rollerX, rollerY);
                if (movedId > 0L) {
                    String movedZ = mainRollerTargetHeight(roomId, targetX, targetY, rollerZ);
                    furniture.updateRoomPosition(movedId, roomId, targetX, targetY, movedZ);
                    Handling.Proc_6_151_78AC20(roomId, movedId, 0);
                    String payload = RoomRollers.movePayload(movedId, targetX, targetY, movedZ);
                    if (!payload.isEmpty()) {
                        Handling.Proc_6_246_8024C0(roomId, payload, 0);
                    }
                    moved++;
                }
                mainRollerMoveOccupants(roomSlot, rollerX, rollerY, targetX, targetY, rollerR);
            }
            Handling.Proc_6_106_74B750(Path.of(Functions.applicationPath, "CACHE", "ROOMS", roomId + ".cache").toString(), 0, 0);
            Handling.Proc_6_106_74B750(Path.of(Functions.applicationPath, "CACHE", "PATHFINDER", roomId + ".cache").toString(), 0, 0);
        } catch (Exception ignored) {
            // VB6 source suppresses timer failures.
        }
        return moved;
    }

    public static void appendGameServerPacketPayload(long socketIndex, String packetPayload) {
        GameServerSessionState sessionState = Licence.gameServerSessionState();
        sessionState.appendPacketPayload(socketIndex, packetPayload);
        Licence.setGameServerSessionState(sessionState);
    }

    public static GameServerPacket gameServerPacket(String packetText) {
        String[] fields = StringUtils.text(packetText).split("\2", -1);
        String commandName = fields.length > 0 ? StringUtils.text(fields[0]).toUpperCase(Locale.ROOT) : "";
        long socketIndex = fields.length >= 2 ? NumberUtils.parseLong(fields[1]) : NumberUtils.parseLong(commandName);
        String payload = "DATA".equals(commandName) ? gameServerPacketPayload(fields) : "";
        return new GameServerPacket(commandName, socketIndex, payload);
    }

    private static String gameServerPacketPayload(String[] fields) {
        if (fields == null || fields.length <= 2) {
            return "";
        }
        StringBuilder payload = new StringBuilder();
        for (int fieldIndex = 2; fieldIndex < fields.length; fieldIndex++) {
            if (payload.length() > 0) {
                payload.append('\2');
            }
            payload.append(StringUtils.text(fields[fieldIndex]));
        }
        return payload.toString();
    }

    public static String popGameServerPacketData(long socketIndex) {
        GameServerSessionState sessionState = Licence.gameServerSessionState();
        String payload = sessionState.popPacketData(socketIndex);
        Licence.setGameServerSessionState(sessionState);
        return payload;
    }

    public static boolean isGameSessionReady(long socketIndex) {
        return Licence.gameServerSessionState().isReady(socketIndex);
    }

    public static void Proc_0_26_6ACF30(Object... args) {
        try {
            long socketIndex = args != null && args.length >= 1 ? NumberUtils.parseLong(args[0]) : 0L;
            if (socketIndex <= 0L || Guardian.Proc_11_2_821390(socketIndex, 0, 0) != 1) {
                return;
            }
            long roomSlot = mainRepresentedSocketRoomSlot(socketIndex);
            if (roomSlot > 0L) {
                mainRepresentedRoomOccupantAdd(roomSlot, socketIndex, 1);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses attachment failures.
        }
    }

    public static void Proc_0_27_6AD400(Object... args) {
        try {
            long entityIndex = args != null && args.length >= 1 ? NumberUtils.parseLong(args[0]) : 0L;
            if (entityIndex <= 0L) {
                return;
            }
            long roomSlot = mainRepresentedBotRoomSlot(entityIndex);
            if (roomSlot > 0L) {
                mainRepresentedRoomOccupantAdd(roomSlot, entityIndex, 2);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses attachment failures.
        }
    }

    public static void Proc_0_28_6AD850(Object... args) {
        try {
            long entityIndex = args != null && args.length >= 1 ? NumberUtils.parseLong(args[0]) : 0L;
            if (entityIndex <= 0L) {
                return;
            }
            long roomSlot = mainRepresentedBotRoomSlot(entityIndex);
            if (roomSlot <= 0L) {
                return;
            }
            long roomId = mainCurrentRoomIdForSlot(roomSlot);
            long currentX = args != null && args.length >= 5 ? NumberUtils.parseLong(args[1]) : 0L;
            long currentY = args != null && args.length >= 5 ? NumberUtils.parseLong(args[2]) : 0L;
            long targetX = args != null && args.length >= 5 ? NumberUtils.parseLong(args[3]) : 0L;
            long targetY = args != null && args.length >= 5 ? NumberUtils.parseLong(args[4]) : 0L;
            MovementStep movement = MovementStep.between(currentX, currentY, targetX, targetY);
            if (roomId <= 0L
                || Functions.representedBotPositionAvailable(entityIndex, movement.positionX(), movement.positionY()) != 0L) {
                mainRepresentedRoomOccupantMove(roomSlot, entityIndex, 2,
                    movement.positionX(), movement.positionY(), movement.directionValue(), movement.movingValue());
            }
        } catch (Exception ignored) {
            // VB6 source suppresses walk failures.
        }
    }

    public static void Proc_0_29_6B0E10(Object... args) {
        try {
            long socketIndex = args != null && args.length >= 1 ? NumberUtils.parseLong(args[0]) : 0L;
            if (socketIndex <= 0L || Guardian.Proc_11_2_821390(socketIndex, 0, 0) != 1) {
                return;
            }
            long roomSlot = mainRepresentedSocketRoomSlot(socketIndex);
            if (roomSlot <= 0L) {
                roomSlot = socketIndex;
            }
            long roomId = mainCurrentRoomIdForSocket(socketIndex);
            long currentX = args != null && args.length >= 5 ? NumberUtils.parseLong(args[1]) : 0L;
            long currentY = args != null && args.length >= 5 ? NumberUtils.parseLong(args[2]) : 0L;
            long targetX = args != null && args.length >= 5 ? NumberUtils.parseLong(args[3]) : 0L;
            long targetY = args != null && args.length >= 5 ? NumberUtils.parseLong(args[4]) : 0L;
            MovementStep movement = MovementStep.between(currentX, currentY, targetX, targetY);
            if (roomId <= 0L || Functions.roomPositionAvailable(roomId, movement.positionX(), movement.positionY()) != 0L) {
                mainRepresentedRoomOccupantMove(roomSlot, socketIndex, 1,
                    movement.positionX(), movement.positionY(), movement.directionValue(), movement.movingValue());
            }
        } catch (Exception ignored) {
            // VB6 source suppresses walk failures.
        }
    }

    public static long mainRepresentedSocketRoomSlot(long socketIndex) {
        long roomSlot = Licence.sessionCacheLong(String.valueOf(socketIndex), 1);
        if (roomSlot > 0L) {
            return roomSlot;
        }
        return Licence.representedSockets().roomSlot(socketIndex);
    }

    public static long mainRepresentedBotRoomSlot(long entityIndex) {
        return Licence.representedBots().record(entityIndex).roomSlot();
    }

    public static void mainRepresentedRoomOccupantAdd(long roomSlot, long entityIndex, long occupantType) {
        Licence.setRepresentedRooms(Licence.representedRooms().addOccupant(roomSlot, entityIndex, occupantType));
    }

    public static void mainRepresentedRoomOccupantMove(
        long roomSlot,
        long entityIndex,
        long occupantType,
        long positionX,
        long positionY,
        long directionValue,
        long movingValue
    ) {
        Licence.setRepresentedRooms(Licence.representedRooms()
            .moveOccupant(roomSlot, entityIndex, occupantType, positionX, positionY, directionValue, movingValue));
    }

    public static long mainRollerFurnitureOnTile(long roomId, long rollerId, long positionX, long positionY) {
        try {
            return roomDao().furnitureIdAtExcluding(roomId, rollerId, positionX, positionY);
        } catch (SQLException ignored) {
            return 0L;
        }
    }

    public static String mainRollerTargetHeight(long roomId, long positionX, long positionY, String fallbackHeight) {
        try {
            return RoomRollers.targetHeight(roomDao().topFurnitureHeightAt(roomId, positionX, positionY), fallbackHeight);
        } catch (SQLException ignored) {
            return RoomRollers.targetHeight("", fallbackHeight);
        }
    }

    public static long mainCurrentRoomIdForSlot(long roomSlot) {
        if (roomSlot <= 0L) {
            return 0L;
        }
        try {
            return roomDao().roomIdBySlot(roomSlot);
        } catch (SQLException ignored) {
            return 0L;
        }
    }

    public static long mainCurrentRoomIdForSocket(long socketIndex) {
        if (socketIndex <= 0L) {
            return 0L;
        }
        long roomId = Licence.sessionCacheLong(String.valueOf(socketIndex), 1);
        if (roomId > 0L) {
            return roomId;
        }
        String userId = MySQL.mySqlUserIdFromSocket((int) socketIndex);
        if (userId.isEmpty() || "0".equals(userId)) {
            return 0L;
        }
        try {
            return roomDao().currentRoomIdByUser(NumberUtils.parseLong(userId));
        } catch (SQLException ignored) {
            return 0L;
        }
    }

    public static String mainUserIdFromSocket(long socketIndex) {
        String userId = Licence.socketUserId(String.valueOf(socketIndex));
        if (userId.isEmpty() || "0".equals(userId)) {
            try {
                long databaseUserId = userDao().userIdBySocket(socketIndex);
                userId = databaseUserId <= 0L ? "0" : String.valueOf(databaseUserId);
            } catch (SQLException ignored) {
                userId = "";
            }
        }
        return userId;
    }

    public static void mainRollerMoveOccupants(long roomSlot, long fromX, long fromY, long toX, long toY, long directionValue) {
        RepresentedRoomCache representedRooms = Licence.representedRooms()
            .moveOccupantsAt(roomSlot, 1, fromX, fromY, toX, toY, directionValue)
            .moveOccupantsAt(roomSlot, 2, fromX, fromY, toX, toY, directionValue);
        Licence.setRepresentedRooms(representedRooms);
    }

    public static String mainRepresentedRecordByBracket(String cacheText, long recordId) {
        if (recordId <= 0L || StringUtils.text(cacheText).isEmpty()) {
            return "";
        }
        String markerText = "[" + recordId + "]";
        int startAt = cacheText.indexOf(markerText);
        if (startAt < 0) {
            return "";
        }
        startAt += markerText.length();
        int endAt = cacheText.indexOf('[', startAt);
        if (endAt < 0) {
            endAt = cacheText.length();
        }
        return cacheText.substring(startAt, endAt);
    }

    public static String mainRepresentedRecordByKey(String cacheText, long recordId) {
        if (recordId <= 0L || StringUtils.text(cacheText).isEmpty()) {
            return "";
        }
        String markerText = "\1" + recordId + '\t';
        int startAt = cacheText.indexOf(markerText);
        if (startAt < 0) {
            markerText = "\1" + recordId + '\2';
            startAt = cacheText.indexOf(markerText);
            if (startAt < 0) {
                return "";
            }
        }
        startAt += 1;
        int endAt = cacheText.indexOf('\2', startAt);
        if (endAt < 0) {
            endAt = cacheText.length();
        }
        return cacheText.substring(startAt, endAt);
    }

    public static String mainRepresentedEntityIds(String markerText) {
        StringBuilder outputText = new StringBuilder();
        for (String part : StringUtils.text(markerText).split("\1", -1)) {
            long entityId = NumberUtils.parseLong(part);
            if (entityId > 0L) {
                String marker = "[" + entityId + "]";
                if (outputText.indexOf(marker) < 0) {
                    outputText.append(marker);
                }
            }
        }
        return outputText.toString();
    }

    public static long mainRepresentedEntityIdAt(String entityMarkers, long entityIndex) {
        String[] markerParts = StringUtils.text(entityMarkers).split("\\]", -1);
        if (entityIndex >= 0 && entityIndex < markerParts.length) {
            return NumberUtils.parseLong(markerParts[(int) entityIndex].replace("[", ""));
        }
        return 0L;
    }

    private static RoomDao roomDao() throws SQLException {
        return new RoomDao(configuredDatabase());
    }

    private static UserDao userDao() throws SQLException {
        return new UserDao(configuredDatabase());
    }

    private static FurnitureDao furnitureDao() throws SQLException {
        return new FurnitureDao(configuredDatabase());
    }

    private static ServerMaintenanceDao serverMaintenanceDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new ServerMaintenanceDao(database);
    }

    private static Database configuredDatabase() throws SQLException {
        if (MySQL.configuredDatabase() == null) {
            throw new SQLException("Database is not configured.");
        }
        return MySQL.configuredDatabase();
    }
}
