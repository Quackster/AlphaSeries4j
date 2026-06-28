package com.alphaseries;

import com.alphaseries.vb.Vb;

import java.util.Arrays;

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
            if (Licence.global_00829034) {
                Console.Proc_2_0_6D1510("[" + socketIndex + "] " + ex.getMessage() + " -> " + packetData,
                    "ERROR", "255");
                DataManager.Proc_8_9_806810(Functions.applicationPath + "/ERR.log",
                    "ERROR] " + packetData + " (" + ex.getMessage() + ")\r\n0\r\n\r\n\r\n");
                Functions.Proc_10_8_80A580(socketIndex, 0x60, 0);
            }
        }
    }

    public static void appendGameServerPacketData(long socketIndex, String[] fields) {
        if (fields == null || fields.length <= 2) {
            return;
        }
        StringBuilder payload = new StringBuilder();
        for (int fieldIndex = 2; fieldIndex < fields.length; fieldIndex++) {
            if (payload.length() > 0) {
                payload.append('\2');
            }
            payload.append(Vb.cStr(fields[fieldIndex]));
        }
        if (payload.length() > 0) {
            Licence.global_00829350 += "[" + socketIndex + ":" + payload + "]";
        }
    }

    public static String popGameServerPacketData(long socketIndex) {
        String marker = "[" + socketIndex + ":";
        int recordStart = Licence.global_00829350.indexOf(marker);
        if (recordStart < 0) {
            return "";
        }
        int payloadStart = recordStart + marker.length();
        int recordEnd = Licence.global_00829350.indexOf(']', payloadStart);
        if (recordEnd < 0) {
            return "";
        }
        String payload = Licence.global_00829350.substring(payloadStart, recordEnd);
        Licence.global_00829350 = Licence.global_00829350.substring(0, recordStart)
            + Licence.global_00829350.substring(recordEnd + 1);
        return payload;
    }

    public static boolean isGameSessionReady(long socketIndex) {
        return Licence.global_00829354.contains("[" + socketIndex + "]");
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
        String[] fields = mainRepresentedRecordByBracket(Vb.cStr(Licence.global_0082934C), socketIndex).split("\2", -1);
        return fields.length >= 2 ? Vb.val(fields[1]) : 0L;
    }

    public static long mainRepresentedBotRoomSlot(long entityIndex) {
        return Vb.val(mainRepresentedBotRecordField(entityIndex, 0));
    }

    public static String mainRepresentedBotRecordText(long entityIndex) {
        if (entityIndex <= 0L || Licence.global_00829358.isEmpty()) {
            return "";
        }
        String marker = "[" + entityIndex + ":";
        int startAt = Licence.global_00829358.indexOf(marker);
        if (startAt < 0) {
            return "";
        }
        startAt += marker.length();
        int endAt = Licence.global_00829358.indexOf(']', startAt);
        if (endAt <= startAt) {
            return "";
        }
        return Licence.global_00829358.substring(startAt, endAt);
    }

    public static String mainRepresentedBotRecordField(long entityIndex, long fieldIndex) {
        String[] fields = mainRepresentedBotRecordText(entityIndex).split("\2", -1);
        return fieldIndex >= 0 && fieldIndex < fields.length ? fields[(int) fieldIndex] : "";
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
        return mainRepresentedRecordByKey(Licence.global_00829310, roomSlot);
    }

    public static String mainRepresentedRoomRecordField(long roomSlot, long fieldIndex) {
        String[] fields = mainRepresentedRoomRecord(roomSlot).split("\t", -1);
        return fieldIndex >= 0 && fieldIndex < fields.length ? fields[(int) fieldIndex] : "";
    }

    public static void mainRepresentedRoomRecordSet(long roomSlot, String roomRecord) {
        if (roomSlot <= 0L) {
            return;
        }
        String cacheText = mainRepresentedCacheRemove(Licence.global_00829310, "\1" + roomSlot + '\t');
        cacheText = mainRepresentedCacheRemove(cacheText, "\1" + roomSlot + '\2');
        Licence.global_00829310 = cacheText + '\1' + Vb.cStr(roomRecord) + '\2';
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
