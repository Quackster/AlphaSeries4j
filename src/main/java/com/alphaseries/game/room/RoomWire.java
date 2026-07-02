package com.alphaseries.game.room;

import com.alphaseries.messages.outgoing.RoomPayloads;
import com.alphaseries.protocol.WireEncoding;
import com.alphaseries.protocol.WireReader;
import com.alphaseries.protocol.WireRequests;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class RoomWire {
    private RoomWire() {
    }

    public record PositionRequest(long positionX, long positionY) {
    }

    public record RoomIdRequest(long roomId) {
    }

    public record RoomSettingsReadRequest(long requestedRoomId) {
    }

    public record RoomIconRequest(String iconPayload) {
        public RoomIconRequest {
            iconPayload = StringUtils.text(iconPayload);
        }

        public boolean valid() {
            return !iconPayload.isEmpty();
        }
    }

    public record RoomRatingRequest(long voteValue) {
    }

    public record RoomEntryRequest(long roomId, String roomPassword) {
    }

    public record CreateRoomRequest(String roomName, String modelName) {
        public CreateRoomRequest {
            roomName = StringUtils.left(StringUtils.singleLineText(roomName), 25);
            modelName = StringUtils.left(StringUtils.sqlEscapedText(modelName), 10);
        }
    }

    public record DeleteRoomRequest(long requestFlag) {
    }

    public record RoomRightGrantRequest(long targetUserId) {
    }

    public record RoomUserTargetRequest(long targetUserId) {
    }

    public record RoomRightNameRequest(String targetName) {
        public RoomRightNameRequest {
            targetName = StringUtils.singleLineText(targetName).trim();
        }
    }

    public record RoomRightRevokeRequest(List<Long> targetUserIds) {
        public RoomRightRevokeRequest {
            targetUserIds = targetUserIds == null ? List.of() : List.copyOf(targetUserIds);
        }
    }

    public static WallPlacement wallPlacementFromPayload(String packetPayload) {
        String normalizedPayload = StringUtils.text(packetPayload)
            .replace('\1', ' ')
            .replace('\2', ' ')
            .replace('\t', ' ')
            .replace('\r', ' ')
            .replace('\n', ' ');
        while (normalizedPayload.contains("  ")) {
            normalizedPayload = normalizedPayload.replace("  ", " ");
        }
        normalizedPayload = normalizedPayload.trim();
        String lower = normalizedPayload.toLowerCase();
        int wallAt = lower.indexOf(":w=");
        int localAt = lower.indexOf("l=");
        if (wallAt < 0 || localAt <= wallAt) {
            return WallPlacement.empty();
        }
        String wallText = normalizedPayload.substring(wallAt + 3, localAt).trim().replace(" ", "");
        String localText = normalizedPayload.substring(localAt + 2).trim().replace(" ", "");
        String[] wallParts = wallText.split(",", -1);
        String[] localParts = localText.split(",", -1);
        if (wallParts.length < 2 || localParts.length < 2) {
            return WallPlacement.empty();
        }
        return WallPlacement.valid(
            NumberUtils.parseLong(wallParts[0]),
            NumberUtils.parseLong(wallParts[1]),
            NumberUtils.parseLong(localParts[0]),
            NumberUtils.parseLong(localParts[1]));
    }

    public static String normalizeModelMap(String modelMap) {
        String modelPayload = StringUtils.text(modelMap).replace('\n', '\r');
        while (modelPayload.contains("\r\r")) {
            modelPayload = modelPayload.replace("\r\r", "\r");
        }
        return modelPayload;
    }

    public static RoomIconRequest roomIconRequest(String packetPayload) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, "FB");
        WireReader.Offset offset = new WireReader.Offset(1);
        long previousOffset = offset.value();
        long backgroundId = WireReader.readLong(requestPayload, offset);
        if (offset.value() <= previousOffset || backgroundId < 0L || backgroundId > 24L) {
            return new RoomIconRequest("");
        }

        previousOffset = offset.value();
        long foregroundId = WireReader.readLong(requestPayload, offset);
        if (offset.value() <= previousOffset || foregroundId < 0L || foregroundId > 11L) {
            return new RoomIconRequest("");
        }

        previousOffset = offset.value();
        long itemCount = WireReader.readLong(requestPayload, offset);
        if (offset.value() <= previousOffset || itemCount < 0L || itemCount > 12L) {
            return new RoomIconRequest("");
        }

        List<RoomPayloads.RoomIconItem> items = new ArrayList<>();
        for (long itemIndex = 1L; itemIndex <= itemCount; itemIndex++) {
            previousOffset = offset.value();
            long itemType = WireReader.readLong(requestPayload, offset);
            if (offset.value() <= previousOffset || itemType < 0L) {
                return new RoomIconRequest("");
            }

            previousOffset = offset.value();
            long itemPosition = WireReader.readLong(requestPayload, offset);
            if (offset.value() <= previousOffset || itemPosition < 0L) {
                return new RoomIconRequest("");
            }

            items.add(new RoomPayloads.RoomIconItem(itemType, itemPosition));
        }
        return new RoomIconRequest(RoomPayloads.icon(backgroundId, foregroundId, items));
    }

    /**
     * Original function: Proc_6_47_714F60.
     * Original function: Proc_6_77_727590.
     * Original function: Proc_6_109_74DBD0.
     * Original function: Proc_6_110_74DDA0.
     */
    public static RoomIdRequest roomIdRequest(String packetPayload, String prefix) {
        String requestPayload = StringUtils.text(packetPayload);
        String prefixText = StringUtils.text(prefix);
        if (!prefixText.isEmpty() && requestPayload.startsWith(prefixText)) {
            requestPayload = requestPayload.substring(prefixText.length());
        }
        long roomId = WireReader.readLong(requestPayload, new WireReader.Offset(1));
        if (roomId <= 0L) {
            roomId = NumberUtils.parseLong(WireEncoding.readVl64LengthString(requestPayload));
        }
        return new RoomIdRequest(roomId);
    }

    /**
     * Original function: Proc_6_43_713680.
     */
    public static RoomSettingsReadRequest roomSettingsReadRequest(String packetPayload) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, "FF");
        long requestedRoomId = NumberUtils.parseLong(WireEncoding.readVl64LengthString(requestPayload));
        if (requestedRoomId <= 0L) {
            requestedRoomId = WireReader.readLong(requestPayload, new WireReader.Offset(1));
        }
        return new RoomSettingsReadRequest(requestedRoomId);
    }

    /**
     * Original function: Proc_6_63_721050.
     */
    public static RoomRatingRequest roomRatingRequest(String packetPayload) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, "DE");
        long voteValue = WireReader.readLong(requestPayload, new WireReader.Offset(1));
        return new RoomRatingRequest(voteValue);
    }

    /**
     * Original function: Proc_6_58_71FCA0.
     */
    public static RoomEntryRequest roomEntryRequest(String packetPayload) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, "FG");
        String roomIdText = WireEncoding.readBase64LengthString(requestPayload);
        if (roomIdText.isEmpty()) {
            roomIdText = requestPayload;
        }
        long roomId = NumberUtils.parseLong(roomIdText);
        int passwordStart = 2 + roomIdText.length();
        String roomPassword = "";
        if (passwordStart < requestPayload.length()) {
            roomPassword = WireEncoding.readVl64LengthString(requestPayload.substring(passwordStart));
        }
        return new RoomEntryRequest(roomId, roomPassword);
    }

    /**
     * Original function: Proc_6_105_74AD50.
     */
    public static CreateRoomRequest createRoomRequest(String packetPayload) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, "@]");
        WireReader.Offset offset = new WireReader.Offset(1);
        String roomName = WireReader.readString(requestPayload, offset);
        String modelName = WireReader.readString(requestPayload, offset);
        return new CreateRoomRequest(roomName, modelName);
    }

    /**
     * Original function: Proc_6_72_7250D0.
     */
    public static DeleteRoomRequest deleteRoomRequest(String packetPayload) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, "@W");
        long requestFlag = NumberUtils.parseLong(WireEncoding.readVl64LengthString(requestPayload));
        if (requestFlag == 0L && !requestPayload.isEmpty()) {
            requestFlag = WireReader.readLong(requestPayload, new WireReader.Offset(1));
        }
        return new DeleteRoomRequest(requestFlag);
    }

    /**
     * Original function: Proc_6_65_721A10.
     */
    public static RoomRightGrantRequest roomRightGrantRequest(String packetPayload) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, "A`");
        long targetUserId = WireReader.readLong(requestPayload, new WireReader.Offset(1));
        return new RoomRightGrantRequest(targetUserId);
    }

    /**
     * Original function: Proc_6_61_720490.
     * Original function: Proc_6_62_7209F0.
     */
    public static RoomUserTargetRequest roomUserTargetRequest(String packetPayload, String prefix) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, prefix);
        long targetUserId = WireReader.readLong(requestPayload, new WireReader.Offset(1));
        return new RoomUserTargetRequest(targetUserId);
    }

    /**
     * Original function: Proc_6_64_721650.
     * Original function: Proc_6_75_7269D0.
     */
    public static RoomRightNameRequest roomRightNameRequest(String packetPayload, String prefix) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, prefix);
        String targetName = WireReader.readString(requestPayload, new WireReader.Offset(1));
        return new RoomRightNameRequest(targetName);
    }

    /**
     * Original function: Proc_6_74_7265B0.
     */
    public static RoomRightRevokeRequest roomRightRevokeRequest(String packetPayload) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, "Aa");
        WireReader.Offset offset = new WireReader.Offset(1);
        long revokeCount = WireReader.readLong(requestPayload, offset);
        if (revokeCount < 1L || revokeCount > 150L) {
            return new RoomRightRevokeRequest(List.of());
        }
        List<Long> targetUserIds = new ArrayList<>();
        for (long revokeIndex = 1L; revokeIndex <= revokeCount; revokeIndex++) {
            long targetUserId = WireReader.readLong(requestPayload, offset);
            if (targetUserId > 0L) {
                targetUserIds.add(targetUserId);
            }
        }
        return new RoomRightRevokeRequest(targetUserIds);
    }

    /**
     * Original function: Proc_6_197_7D43C0.
     * Original function: Proc_6_198_7D4B70.
     */
    public static PositionRequest positionRequest(String packetPayload, String prefix) {
        String requestPayload = StringUtils.text(packetPayload);
        String prefixText = StringUtils.text(prefix);
        if (!prefixText.isEmpty() && requestPayload.startsWith(prefixText)) {
            requestPayload = requestPayload.substring(prefixText.length());
        }
        WireReader.Offset offset = new WireReader.Offset(1);
        long positionX = WireReader.readLong(requestPayload, offset);
        long positionY = WireReader.readLong(requestPayload, offset);
        if (positionX == 0L && positionY == 0L) {
            positionX = NumberUtils.parseLong(WireEncoding.readVl64LengthString(requestPayload));
            positionY = NumberUtils.parseLong(WireEncoding.readVl64LengthString(requestPayload));
        }
        return new PositionRequest(positionX, positionY);
    }

    public static RoomEventPayload roomEventCreatePayloadFromWire(String packetPayload) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, "EZ");
        WireReader.Offset offset = new WireReader.Offset(1);
        long categoryId = WireReader.readLong(requestPayload, offset);
        if (categoryId < 1L) {
            return null;
        }
        String categoryName = RoomState.instance().eventLocales().categoryName(categoryId);
        if (categoryName.isEmpty()) {
            return null;
        }
        return readRoomEventCommon(requestPayload, offset, categoryId, categoryName, true);
    }

    public static RoomEventPayload roomEventEditPayloadFromWire(String packetPayload) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, "E\\");
        return readRoomEventCommon(requestPayload, new WireReader.Offset(1), 0L, "", true);
    }

    public static RoomSettingsPayload roomSettingsFromWire(String packetPayload) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, "FQ");
        WireReader.Offset offset = new WireReader.Offset(1);
        String roomName = StringUtils.singleLineText(WireReader.readString(requestPayload, offset));
        if (roomName.length() < 3) {
            return null;
        }
        String normalizedRoomName = StringUtils.left(roomName, 60);
        String roomPassword = StringUtils.left(StringUtils.singleLineText(WireReader.readString(requestPayload, offset)), 60);
        long doorStatus = WireReader.readLong(requestPayload, offset);
        if (doorStatus < 0L || doorStatus > 2L) {
            return null;
        }
        String roomDescription = StringUtils.left(StringUtils.singleLineText(WireReader.readString(requestPayload, offset)), 255);
        long visitorsMax = WireReader.readLong(requestPayload, offset);
        if (visitorsMax < 1L) {
            visitorsMax = 1L;
        }
        if (visitorsMax > 250L) {
            visitorsMax = 250L;
        }
        long categoryId = WireReader.readLong(requestPayload, offset);
        if (categoryId <= 0L) {
            return null;
        }
        long tagCount = WireReader.readLong(requestPayload, offset);
        if (tagCount < 0L || tagCount > 2L) {
            return null;
        }
        String tagOne = "";
        String tagTwo = "";
        for (long tagIndex = 1L; tagIndex <= tagCount; tagIndex++) {
            String tagText = StringUtils.left(StringUtils.singleLineText(WireReader.readString(requestPayload, offset)), 60)
                .toLowerCase();
            if (tagIndex == 1L) {
                tagOne = tagText;
            } else if (tagIndex == 2L) {
                tagTwo = tagText;
            }
        }

        long allowOthersPets = roomSettingsFlag(optionalWireLong(requestPayload, offset, 0L));
        long allowFeedPets = roomSettingsFlag(optionalWireLong(requestPayload, offset, 0L));
        long allowWalkthrough = roomSettingsFlag(optionalWireLong(requestPayload, offset, 0L));
        long disableWalls = roomSettingsFlag(optionalWireLong(requestPayload, offset, 0L));
        long thicknessFloor = roomSettingsThickness(optionalWireLong(requestPayload, offset, 0L));
        long thicknessWallpaper = roomSettingsThickness(optionalWireLong(requestPayload, offset, 0L));
        return new RoomSettingsPayload(
            normalizedRoomName,
            roomPassword,
            doorStatus,
            roomDescription,
            visitorsMax,
            categoryId,
            tagOne,
            tagTwo,
            allowOthersPets,
            allowFeedPets,
            allowWalkthrough,
            disableWalls,
            thicknessFloor,
            thicknessWallpaper);
    }

    public static long roomSettingsFlag(long flagValue) {
        return flagValue != 0L ? 1L : 0L;
    }

    public static long roomSettingsThickness(long thicknessValue) {
        if (thicknessValue < -2L) {
            return -2L;
        }
        if (thicknessValue > 1L) {
            return 1L;
        }
        return thicknessValue;
    }

    private static RoomEventPayload readRoomEventCommon(
        String packetPayload,
        WireReader.Offset offset,
        long categoryId,
        String categoryName,
        boolean requireText
    ) {
        String eventName = StringUtils.singleLineText(WireReader.readString(packetPayload, offset));
        if (requireText && eventName.length() < 3) {
            return null;
        }
        String eventDescription = StringUtils.singleLineText(WireReader.readString(packetPayload, offset));
        if (requireText && eventDescription.length() < 3) {
            return null;
        }
        long tagCount = WireReader.readLong(packetPayload, offset);
        if (tagCount < 0L || tagCount > 2L) {
            return null;
        }
        String tagOne = "";
        String tagTwo = "";
        for (long tagIndex = 1L; tagIndex <= tagCount; tagIndex++) {
            String tagText = StringUtils.left(StringUtils.singleLineText(WireReader.readString(packetPayload, offset)), 30)
                .toLowerCase();
            if (tagIndex == 1L) {
                tagOne = tagText;
            } else if (tagIndex == 2L) {
                tagTwo = tagText;
            }
        }
        return new RoomEventPayload(categoryId, categoryName, eventName, eventDescription, tagOne, tagTwo);
    }

    private static long optionalWireLong(String packetPayload, WireReader.Offset offset, long defaultValue) {
        long previousOffset = offset.value();
        long value = WireReader.readLong(packetPayload, offset);
        return offset.value() <= previousOffset ? defaultValue : value;
    }

}
