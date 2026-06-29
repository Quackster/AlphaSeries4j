package com.alphaseries;

import com.alphaseries.game.room.FurnitureRoomCache;
import com.alphaseries.server.packet.PacketSink;
import com.alphaseries.game.session.GameServerSessionState;
import com.alphaseries.vb.Vb;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;

public final class Main {
    private static PacketSink preSessionPacketSink = (socketIndex, payload) -> { };

    private Main() {
    }

    public static void configurePreSessionPacketSink(PacketSink sink) {
        preSessionPacketSink = sink == null ? (socketIndex, payload) -> { } : sink;
    }

    public static String shiftIdentityText(String sourceText, long shiftAmount) {
        String source = Vb.cStr(sourceText);
        StringBuilder output = new StringBuilder(source.length());
        for (int index = 0; index < source.length(); index++) {
            output.append((char) ((source.charAt(index) + shiftAmount) & 0xFF));
        }
        return output.toString();
    }

    public static String easyGetIdentity(Object value) {
        return shiftIdentityText(Vb.cStr(value), -25L);
    }

    public static String createSuperEasyIdentity(Object value) {
        return shiftIdentityText(Vb.cStr(value), 2L);
    }

    public static String superEasyGetIdentity(Object value) {
        return shiftIdentityText(Vb.cStr(value), -2L);
    }

    public static String newPremiumCheck(Object valueOffset, Object encodedValue) {
        String encodedText = Vb.cStr(encodedValue);
        if (encodedText.isEmpty()) {
            return "";
        }
        long seedValue = encodedText.charAt(0);
        long offset = Vb.val(valueOffset);
        StringBuilder output = new StringBuilder(encodedText.length() - 1);
        for (int index = 1; index < encodedText.length(); index++) {
            output.append((char) (((encodedText.charAt(index) - seedValue) + offset) & 0xFF));
        }
        return output.toString();
    }

    public static String getIdentity(Object encodedValue, Object seedOffset) {
        String encodedText = Vb.cStr(encodedValue);
        if (encodedText.isEmpty()) {
            return "";
        }
        long seedValue = encodedText.charAt(0) - Vb.val(seedOffset);
        StringBuilder output = new StringBuilder(encodedText.length() - 1);
        for (int index = 1; index < encodedText.length(); index++) {
            output.append((char) (((encodedText.charAt(index) - index) - seedValue) & 0xFF));
        }
        return output.toString();
    }

    public static String Proc_0_22_68C1A0(Object... args) {
        try {
            String sourceText = args != null && args.length >= 1 ? Vb.cStr(args[0]) : "";
            long seedValue = Vb.val(Functions.Proc_10_3_809B90(0x41, 0x5A));
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
        return shiftIdentityText(args != null && args.length >= 1 ? Vb.cStr(args[0]) : "", 7L);
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
            result.stage = Vb.cStr(stage);
            result.message = Vb.cStr(message);
            return result;
        }
    }

    public static void Proc_0_24_68EEF0(Object... args) {
        // Empty in the recovered VB6 reference.
    }

    public static void Proc_0_25_68FBC0(Object... args) {
        long socketIndex = args != null && args.length >= 1 ? Vb.val(args[0]) : 0L;
        String packetData = args != null && args.length >= 2 ? Vb.cStr(args[1]) : "";
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
                DataManager.Proc_8_9_806810(Functions.applicationPath + "/ERR.log",
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
            result.consoleTitle = Vb.cStr(captionTemplate).replace("%%", Licence.runtimeState().productName());
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
            MySQL.Proc_5_0_6D3CD0("UPDATE users SET id_socket=null,lastonline_time=UNIX_TIMESTAMP() WHERE id_socket IS NOT NULL", 1, 0);
            MySQL.Proc_5_0_6D3CD0("UPDATE rooms SET id_slot=null,visitors_now='0' WHERE id_slot IS NOT NULL OR visitors_now!='0'", 0, 0);
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
        return Vb.cStr(System.getenv("USERNAME")).isEmpty()
            ? Vb.cStr(System.getenv("USER"))
            : Vb.cStr(System.getenv("USERNAME"));
    }

    public static String productKeyFromConfig(String configText) {
        String[] configParts = Vb.cStr(configText).split("=", -1);
        if (configParts.length < 8) {
            return "";
        }
        String[] lines = configParts[7].split("\\r?\\n", -1);
        return lines.length > 0 ? Vb.cStr(lines[0]) : "";
    }

    public static String javaCaptionFromConsoleTitle(String consoleTitle) {
        return Vb.cStr(consoleTitle).replace("[!]", "").trim().replaceAll(" {2,}", " ");
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
            for (String packet : Vb.cStr(incomingData).split("\1", -1)) {
                if (packet.isEmpty()) {
                    continue;
                }
                String[] fields = packet.split("\2", -1);
                String commandName = fields.length > 0 ? Vb.cStr(fields[0]).toUpperCase(Locale.ROOT) : "";
                long socketIndex;
                if ("SHUTDOWN".equals(commandName)) {
                    if (fields.length >= 2) {
                        socketIndex = Vb.val(fields[1]);
                        Handling.Proc_6_243_7FFEB0(socketIndex, 0, 0);
                    }
                } else if ("LISTEN".equals(commandName)) {
                    if (fields.length >= 2) {
                        socketIndex = Vb.val(fields[1]);
                        Guardian.Proc_11_3_821440(socketIndex, 0, 0);
                    }
                } else if ("DATA".equals(commandName)) {
                    if (fields.length >= 3) {
                        socketIndex = Vb.val(fields[1]);
                        appendGameServerPacketData(socketIndex, fields);
                    }
                } else if (fields.length >= 1) {
                    socketIndex = Vb.val(fields[0]);
                    Handling.Proc_6_243_7FFEB0(socketIndex, 0, 0);
                }
            }
        } catch (Exception ignored) {
            // VB6 source suppresses game-server read failures.
        }
    }

    public static long signerTimer() {
        long processed = 0L;
        try {
            FurnitureRoomCache.State cacheState = Licence.furnitureRoomCache();
            String furnitureMarkers = mainRepresentedEntityIds(cacheState.pendingFurnitureCache);
            String[] markerParts = furnitureMarkers.split("\\]", -1);
            for (int markerIndex = 0; markerIndex < markerParts.length; markerIndex++) {
                long furnitureId = mainRepresentedEntityIdAt(furnitureMarkers, markerIndex);
                if (furnitureId <= 0L) {
                    continue;
                }
                String rowText = MySQL.Proc_5_2_6D4690("SELECT id_room,sign FROM furnitures WHERE id='"
                    + furnitureId + "' LIMIT 1", 0, 0);
                if (rowText.isEmpty()) {
                    cacheState.pendingFurnitureCache = FurnitureRoomCache.removePendingFurniture(cacheState.pendingFurnitureCache, furnitureId);
                    Licence.setFurnitureRoomCache(cacheState);
                    continue;
                }
                String[] fields = rowText.split("\t", -1);
                long roomId = Vb.val(mainArrayField(fields, 0));
                long signValue = Vb.val(mainArrayField(fields, 1));
                if (roomId > 0L && signValue > 0L) {
                    long nextSignValue = signValue - 1L;
                    MySQL.Proc_5_0_6D3CD0("UPDATE furnitures SET sign='" + nextSignValue + "' WHERE id='"
                        + furnitureId + "' LIMIT 1", 0, 0);
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
                if (entityId <= 0L || Vb.val(mainRepresentedBotRecordField(entityId, 15)) == 0L) {
                    continue;
                }
                long currentX = Vb.val(mainRepresentedBotRecordField(entityId, 6));
                long currentY = Vb.val(mainRepresentedBotRecordField(entityId, 7));
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
            String userMarkers = mainRepresentedEntityIds(mainRepresentedRoomRecordField(roomSlot, 1)
                + mainRepresentedRoomRecordField(roomSlot, 4));
            String[] userParts = userMarkers.split("\\]", -1);
            for (int markerIndex = 0; markerIndex < userParts.length; markerIndex++) {
                long entityId = mainRepresentedEntityIdAt(userMarkers, markerIndex);
                if (entityId > 0L) {
                    Proc_0_29_6B0E10(entityId);
                    moved++;
                }
            }
            String botMarkers = mainRepresentedEntityIds(mainRepresentedRoomRecordField(roomSlot, 2)
                + mainRepresentedRoomRecordField(roomSlot, 5));
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
            MySQL.Proc_5_0_6D3CD0("UPDATE settings SET value=UNIX_TIMESTAMP() WHERE variable='com.server.socket.check.time'", 1, 0);
            for (long socketIndex = 1L; socketIndex <= Guardian.global_0082919C; socketIndex++) {
                String socketMarker = "[" + socketIndex + "]";
                if (!Guardian.global_008291A0.contains(socketMarker)) {
                    continue;
                }
                if (Guardian.Proc_11_2_821390(socketIndex, 0, 0) == 1) {
                    activeCount++;
                } else {
                    Handling.Proc_6_243_7FFEB0(socketIndex, 0, 0);
                }
            }
            if (activeCount > previousMostActiveCount) {
                MySQL.Proc_5_0_6D3CD0("UPDATE settings SET value='" + activeCount
                    + "' WHERE variable='com.server.socket.mostactive'", 0, 0);
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
            String rollerRows = MySQL.Proc_5_2_6D4690("SELECT furnitures.id,furnitures.position_x,furnitures.position_y,"
                + "furnitures.position_z,furnitures.position_r FROM furnitures,products WHERE furnitures.id_room='"
                + roomId + "' AND furnitures.id_product=products.id AND (products.action LIKE '%roller%' OR "
                + "products.name LIKE '%roller%' OR products.sprite LIKE '%roller%') ORDER BY furnitures.id", 0, 0);
            if (rollerRows.isEmpty()) {
                return 0L;
            }
            for (String rollerRow : rollerRows.split("\r", -1)) {
                rollerRow = rollerRow.trim();
                if (rollerRow.isEmpty()) {
                    continue;
                }
                String[] fields = rollerRow.split("\t", -1);
                long rollerId = Vb.val(mainArrayField(fields, 0));
                long rollerX = Vb.val(mainArrayField(fields, 1));
                long rollerY = Vb.val(mainArrayField(fields, 2));
                String rollerZ = mainArrayField(fields, 3);
                long rollerR = Vb.val(mainArrayField(fields, 4));
                long targetX = rollerX + mainRollerDeltaX(rollerR);
                long targetY = rollerY + mainRollerDeltaY(rollerR);
                if (rollerId <= 0L || (targetX == rollerX && targetY == rollerY)
                    || Functions.Proc_10_25_80F5D0(roomId, targetX, targetY) == 0L) {
                    continue;
                }
                long movedId = mainRollerFurnitureOnTile(roomId, rollerId, rollerX, rollerY);
                if (movedId > 0L) {
                    String movedZ = mainRollerTargetHeight(roomId, targetX, targetY, rollerZ);
                    MySQL.Proc_5_0_6D3CD0("UPDATE furnitures SET position_x='" + targetX + "',position_y='"
                        + targetY + "',position_z='" + Functions.Proc_10_11_80A9C0(movedZ, 0, 0)
                        + "' WHERE id='" + movedId + "' AND id_room='" + roomId + "' LIMIT 1", 0, 0);
                    Handling.Proc_6_151_78AC20(roomId, movedId, 0);
                    String payload = mainRollerMovePayload(movedId, targetX, targetY, movedZ);
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

    public static void appendGameServerPacketData(long socketIndex, String[] fields) {
        GameServerSessionState sessionState = Licence.gameServerSessionState();
        sessionState.appendPacketData(socketIndex, fields);
        Licence.setGameServerSessionState(sessionState);
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
            long socketIndex = args != null && args.length >= 1 ? Vb.val(args[0]) : 0L;
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
            long entityIndex = args != null && args.length >= 1 ? Vb.val(args[0]) : 0L;
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
            long entityIndex = args != null && args.length >= 1 ? Vb.val(args[0]) : 0L;
            if (entityIndex <= 0L) {
                return;
            }
            long roomSlot = mainRepresentedBotRoomSlot(entityIndex);
            if (roomSlot <= 0L) {
                return;
            }
            long roomId = mainCurrentRoomIdForSlot(roomSlot);
            long currentX = args != null && args.length >= 5 ? Vb.val(args[1]) : 0L;
            long currentY = args != null && args.length >= 5 ? Vb.val(args[2]) : 0L;
            long targetX = args != null && args.length >= 5 ? Vb.val(args[3]) : 0L;
            long targetY = args != null && args.length >= 5 ? Vb.val(args[4]) : 0L;
            String movementText = Functions.Proc_10_26_81E4E0(entityIndex, currentX, currentY, targetX, targetY);
            long nextX = mainMovementField(movementText, 0);
            long nextY = mainMovementField(movementText, 1);
            long directionValue = mainMovementField(movementText, 2);
            long movingValue = mainMovementField(movementText, 3);
            if (roomId <= 0L || Functions.Proc_10_27_81F1A0(entityIndex, nextX, nextY) != 0L) {
                mainRepresentedRoomOccupantMove(roomSlot, entityIndex, 2, nextX, nextY, directionValue, movingValue);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses walk failures.
        }
    }

    public static void Proc_0_29_6B0E10(Object... args) {
        try {
            long socketIndex = args != null && args.length >= 1 ? Vb.val(args[0]) : 0L;
            if (socketIndex <= 0L || Guardian.Proc_11_2_821390(socketIndex, 0, 0) != 1) {
                return;
            }
            long roomSlot = mainRepresentedSocketRoomSlot(socketIndex);
            if (roomSlot <= 0L) {
                roomSlot = socketIndex;
            }
            long roomId = mainCurrentRoomIdForSocket(socketIndex);
            long currentX = args != null && args.length >= 5 ? Vb.val(args[1]) : 0L;
            long currentY = args != null && args.length >= 5 ? Vb.val(args[2]) : 0L;
            long targetX = args != null && args.length >= 5 ? Vb.val(args[3]) : 0L;
            long targetY = args != null && args.length >= 5 ? Vb.val(args[4]) : 0L;
            String movementText = Functions.Proc_10_24_80E790(socketIndex, currentX, currentY, targetX, targetY);
            long nextX = mainMovementField(movementText, 0);
            long nextY = mainMovementField(movementText, 1);
            long directionValue = mainMovementField(movementText, 2);
            long movingValue = mainMovementField(movementText, 3);
            if (roomId <= 0L || Functions.Proc_10_25_80F5D0(roomId, nextX, nextY) != 0L) {
                mainRepresentedRoomOccupantMove(roomSlot, socketIndex, 1, nextX, nextY, directionValue, movingValue);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses walk failures.
        }
    }

    public static long mainRepresentedSocketRoomSlot(long socketIndex) {
        long roomSlot = Licence.Proc_9_10_808F30(String.valueOf(socketIndex), 1, 0);
        if (roomSlot > 0L) {
            return roomSlot;
        }
        return Licence.representedSockets().roomSlot(socketIndex);
    }

    public static long mainRepresentedBotRoomSlot(long entityIndex) {
        return Vb.val(mainRepresentedBotRecordField(entityIndex, 0));
    }

    public static String mainRepresentedBotRecordText(long entityIndex) {
        return Licence.representedBots().recordText(entityIndex);
    }

    public static String mainRepresentedBotRecordField(long entityIndex, long fieldIndex) {
        return Licence.representedBots().recordField(entityIndex, fieldIndex);
    }

    public static void mainRepresentedRoomOccupantAdd(long roomSlot, long entityIndex, long occupantType) {
        if (roomSlot <= 0L || entityIndex <= 0L) {
            return;
        }
        String markerText = "\1" + entityIndex + '\2';
        int fieldIndex = occupantType == 2L ? 2 : 1;
        int countIndex = 3;
        String roomRecord = mainRepresentedRoomRecord(roomSlot);
        if (roomRecord.isEmpty()) {
            roomRecord = roomSlot + "\t\t\t0";
        }
        String[] fields = ensureFieldCount(roomRecord.split("\t", -1), countIndex);
        if (!Vb.cStr(fields[fieldIndex]).contains(markerText)) {
            fields[fieldIndex] = Vb.cStr(fields[fieldIndex]) + markerText;
            fields[countIndex] = String.valueOf(Vb.val(fields[countIndex]) + 1L);
        }
        mainRepresentedRoomRecordSet(roomSlot, joinTab(fields));
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
        if (roomSlot <= 0L || entityIndex <= 0L) {
            return;
        }
        int fieldIndex = occupantType == 2L ? 5 : 4;
        String roomRecord = mainRepresentedRoomRecord(roomSlot);
        if (roomRecord.isEmpty()) {
            roomRecord = roomSlot + "\t\t\t0";
        }
        String[] fields = ensureFieldCount(roomRecord.split("\t", -1), fieldIndex);
        String movementRecord = entityIndex + "\t" + positionX + "\t" + positionY + "\t" + directionValue + "\t" + movingValue;
        fields[fieldIndex] = mainRepresentedCacheRemove(Vb.cStr(fields[fieldIndex]), "\1" + entityIndex + '\t');
        fields[fieldIndex] = Vb.cStr(fields[fieldIndex]) + '\1' + movementRecord + '\2';
        mainRepresentedRoomRecordSet(roomSlot, joinTab(fields));
    }

    public static long mainMovementField(String movementText, long fieldIndex) {
        String[] fields = Vb.cStr(movementText).split("\0", -1);
        return fieldIndex >= 0 && fieldIndex < fields.length ? Vb.val(fields[(int) fieldIndex]) : 0L;
    }

    public static String mainArrayField(String[] fields, long fieldIndex) {
        return fields != null && fieldIndex >= 0 && fieldIndex < fields.length ? Vb.cStr(fields[(int) fieldIndex]) : "";
    }

    public static long mainRollerDeltaX(long rotationValue) {
        if (rotationValue == 2L) {
            return 1L;
        }
        if (rotationValue == 6L) {
            return -1L;
        }
        return 0L;
    }

    public static long mainRollerDeltaY(long rotationValue) {
        if (rotationValue == 0L) {
            return -1L;
        }
        if (rotationValue == 4L) {
            return 1L;
        }
        return 0L;
    }

    public static String mainRollerTargetHeight(String heightText, String fallbackHeight) {
        return !Vb.cStr(heightText).isEmpty() ? String.valueOf(Vb.val(heightText)) : String.valueOf(Vb.val(fallbackHeight));
    }

    public static long mainRollerFurnitureOnTile(long roomId, long rollerId, long positionX, long positionY) {
        return Vb.val(MySQL.Proc_5_2_6D4690("SELECT id FROM furnitures WHERE id_room='" + roomId
            + "' AND position_x='" + positionX + "' AND position_y='" + positionY + "' AND id<>'"
            + rollerId + "' ORDER BY position_z DESC,id DESC LIMIT 1", 0, 0));
    }

    public static String mainRollerTargetHeight(long roomId, long positionX, long positionY, String fallbackHeight) {
        String heightText = MySQL.Proc_5_2_6D4690("SELECT position_z FROM furnitures WHERE id_room='" + roomId
            + "' AND position_x='" + positionX + "' AND position_y='" + positionY
            + "' ORDER BY position_z DESC,id DESC LIMIT 1", 0, 0);
        return mainRollerTargetHeight(heightText, fallbackHeight);
    }

    public static long mainCurrentRoomIdForSlot(long roomSlot) {
        if (roomSlot <= 0L) {
            return 0L;
        }
        return Vb.val(MySQL.Proc_5_2_6D4690("SELECT id FROM rooms WHERE id_slot='" + roomSlot + "' LIMIT 1", 0, 0));
    }

    public static long mainCurrentRoomIdForSocket(long socketIndex) {
        if (socketIndex <= 0L) {
            return 0L;
        }
        long roomId = Vb.val(Licence.Proc_9_10_808F30(String.valueOf(socketIndex), 1, 0));
        if (roomId > 0L) {
            return roomId;
        }
        String userId = MySQL.mySqlUserIdFromSocket((int) socketIndex);
        if (userId.isEmpty() || "0".equals(userId)) {
            return 0L;
        }
        return Vb.val(MySQL.Proc_5_2_6D4690("SELECT id_room FROM logs_visitedrooms WHERE id_user='"
            + Functions.Proc_10_11_80A9C0(userId, 0, 0)
            + "' AND timestamp_left IS NULL ORDER BY timestamp_enter DESC LIMIT 1", 0, 0));
    }

    public static String mainUserIdFromSocket(long socketIndex) {
        String userId = Licence.Proc_9_6_808080(String.valueOf(socketIndex), 0, 0);
        if (userId.isEmpty() || "0".equals(userId)) {
            userId = MySQL.Proc_5_2_6D4690("SELECT id FROM users WHERE id_socket='" + socketIndex + "' LIMIT 1", 0, 0);
        }
        return userId;
    }

    public static String mainRollerMovePayload(long furnitureId, long positionX, long positionY, String positionZ) {
        String payload = Crypto.Proc_3_0_6D2AF0(furnitureId, null, "AZ");
        payload = Crypto.Proc_3_0_6D2AF0(positionX, null, payload);
        payload = Crypto.Proc_3_0_6D2AF0(positionY, null, payload);
        payload = Crypto.Proc_3_0_6D2AF0(Vb.val(positionZ), null, payload);
        return payload;
    }

    public static void mainRollerMoveOccupants(long roomSlot, long fromX, long fromY, long toX, long toY, long directionValue) {
        mainRollerMoveOccupantsInField(roomSlot, 4, 1, fromX, fromY, toX, toY, directionValue);
        mainRollerMoveOccupantsInField(roomSlot, 5, 2, fromX, fromY, toX, toY, directionValue);
    }

    public static void mainRollerMoveOccupantsInField(
        long roomSlot,
        long fieldIndex,
        long occupantType,
        long fromX,
        long fromY,
        long toX,
        long toY,
        long directionValue
    ) {
        String movementText = mainRepresentedRoomRecordField(roomSlot, fieldIndex);
        if (movementText.isEmpty()) {
            return;
        }
        for (String record : movementText.split("\1", -1)) {
            if (!record.isEmpty()) {
                String[] fields = record.replace("\2", "").split("\t", -1);
                long entityIndex = Vb.val(mainArrayField(fields, 0));
                if (entityIndex > 0L && Vb.val(mainArrayField(fields, 1)) == fromX && Vb.val(mainArrayField(fields, 2)) == fromY) {
                    mainRepresentedRoomOccupantMove(roomSlot, entityIndex, occupantType, toX, toY, directionValue, 0);
                }
            }
        }
    }

    public static String mainRepresentedRecordByBracket(String cacheText, long recordId) {
        if (recordId <= 0L || Vb.cStr(cacheText).isEmpty()) {
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
        if (recordId <= 0L || Vb.cStr(cacheText).isEmpty()) {
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

    public static String mainRepresentedRoomRecord(long roomSlot) {
        return Licence.representedRooms().record(roomSlot);
    }

    public static String mainRepresentedRoomRecordField(long roomSlot, long fieldIndex) {
        String[] fields = mainRepresentedRoomRecord(roomSlot).split("\t", -1);
        return fieldIndex >= 0 && fieldIndex < fields.length ? fields[(int) fieldIndex] : "";
    }

    public static void mainRepresentedRoomRecordSet(long roomSlot, String roomRecord) {
        if (roomSlot <= 0L) {
            return;
        }
        Licence.setRepresentedRooms(Licence.representedRooms().setRecord(roomSlot, roomRecord));
    }

    public static String mainRepresentedCacheRemove(String cacheText, String markerText) {
        String cache = Vb.cStr(cacheText);
        String marker = Vb.cStr(markerText);
        if (cache.isEmpty() || marker.isEmpty()) {
            return cache;
        }
        int startAt = cache.indexOf(marker);
        while (startAt >= 0) {
            int endAt = cache.indexOf('\2', startAt + marker.length());
            if (endAt < 0) {
                cache = cache.substring(0, startAt);
            } else {
                cache = cache.substring(0, startAt) + cache.substring(endAt + 1);
            }
            startAt = cache.indexOf(marker);
        }
        return cache;
    }

    public static String mainRepresentedEntityIds(String markerText) {
        StringBuilder outputText = new StringBuilder();
        for (String part : Vb.cStr(markerText).split("\1", -1)) {
            long entityId = Vb.val(part);
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
        String[] markerParts = Vb.cStr(entityMarkers).split("\\]", -1);
        if (entityIndex >= 0 && entityIndex < markerParts.length) {
            return Vb.val(markerParts[(int) entityIndex].replace("[", ""));
        }
        return 0L;
    }

    private static String[] ensureFieldCount(String[] fields, int requiredIndex) {
        if (fields.length > requiredIndex) {
            return fields;
        }
        return Arrays.copyOf(fields, requiredIndex + 1);
    }

    private static String joinTab(String[] fields) {
        StringBuilder joined = new StringBuilder();
        for (int index = 0; index < fields.length; index++) {
            if (index > 0) {
                joined.append('\t');
            }
            joined.append(Vb.cStr(fields[index]));
        }
        return joined.toString();
    }
}
