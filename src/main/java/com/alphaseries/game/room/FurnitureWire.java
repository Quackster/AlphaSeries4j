package com.alphaseries.game.room;

import com.alphaseries.protocol.WireEncoding;
import com.alphaseries.protocol.WireReader;
import com.alphaseries.protocol.WireRequests;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.RandomUtils;
import com.alphaseries.util.StringUtils;

public final class FurnitureWire {
    private FurnitureWire() {
    }

    public record StickyNoteUpdate(long furnitureId, String noteColor, String noteCaption) {
        public StickyNoteUpdate {
            noteColor = StringUtils.text(noteColor);
            noteCaption = StringUtils.text(noteCaption);
        }

        public static StickyNoteUpdate empty() {
            return new StickyNoteUpdate(0L, "", "");
        }
    }

    public record FurnitureMoveRequest(long furnitureId, long positionX, long positionY, long rotation) {
        public static FurnitureMoveRequest empty() {
            return new FurnitureMoveRequest(0L, 0L, 0L, 0L);
        }
    }

    public record FloorFurniturePlacement(long furnitureId, long positionX, long positionY, long rotation) {
        public static FloorFurniturePlacement empty() {
            return new FloorFurniturePlacement(0L, 0L, 0L, 0L);
        }
    }

    public record FloorPlacementRequest(String placementPayload, FloorFurniturePlacement placement) {
        public FloorPlacementRequest {
            placementPayload = StringUtils.text(placementPayload);
            placement = placement == null ? FloorFurniturePlacement.empty() : placement;
        }
    }

    public record WallFurniturePlacementRequest(String wallPayload, long furnitureId) {
        public WallFurniturePlacementRequest {
            wallPayload = StringUtils.text(wallPayload);
        }
    }

    public record DimmerPresetRequest(long presetId, long backgroundId, String colourText, long lightLevel) {
        public DimmerPresetRequest {
            colourText = StringUtils.text(colourText).toUpperCase();
        }
    }

    public record CreditFurnitureRequest(long furnitureId) {
    }

    public record FurnitureIdRequest(long furnitureId) {
    }

    public record SimpleFloorItemUseRequest(long furnitureId) {
    }

    public record FloorFurniturePackageRequest(String requestPayload, long furnitureId) {
        public FloorFurniturePackageRequest {
            requestPayload = StringUtils.text(requestPayload);
        }
    }

    /**
     * Original function: Proc_6_66_721D60.
     */
    public static StickyNoteUpdate stickyNoteUpdate(String packetPayload) {
        String payload = WireRequests.stripPrefix(packetPayload, "AT");
        String idText = WireEncoding.readVl64LengthString(payload);
        long furnitureId = NumberUtils.parseLong(idText);
        String notePayload = "";
        if (furnitureId <= 0L) {
            WireReader.Offset offset = new WireReader.Offset(1);
            furnitureId = WireReader.readLong(payload, offset);
            notePayload = StringUtils.mid(payload, (int) offset.value());
        } else {
            long idLengthSize = WireEncoding.encodedVl64LengthByteCount(payload);
            if (idLengthSize > 0L) {
                notePayload = StringUtils.mid(payload, (int) idLengthSize + idText.length() + 1);
            }
        }
        if (notePayload.isEmpty()) {
            notePayload = WireEncoding.readBase64LengthString(payload);
        }
        if (notePayload.isEmpty()) {
            return StickyNoteUpdate.empty();
        }
        if (notePayload.length() > 510) {
            notePayload = notePayload.substring(0, 510);
        }

        int separatorAt = firstPositiveIndex(notePayload, '\r', '\n', '\2');
        String noteColor;
        String noteCaption;
        if (separatorAt >= 0) {
            noteColor = notePayload.substring(0, separatorAt).toUpperCase();
            noteCaption = notePayload.substring(separatorAt + 1);
        } else {
            noteColor = notePayload.substring(0, Math.min(6, notePayload.length())).toUpperCase();
            noteCaption = notePayload.length() > 6 ? notePayload.substring(6) : "";
        }
        if (noteColor.length() > 6) {
            noteColor = noteColor.substring(0, 6);
        }
        if (!isStickyNoteColor(noteColor)) {
            return StickyNoteUpdate.empty();
        }
        if (noteCaption.length() > 510) {
            noteCaption = noteCaption.substring(0, 510);
        }
        noteCaption = noteCaption.replace('\u00a0', '\u001f').replace('\r', '\u001f').replace('\n', '\u001f');
        return new StickyNoteUpdate(furnitureId, noteColor, noteCaption);
    }

    public static boolean isStickyNoteColor(String noteColor) {
        String color = StringUtils.text(noteColor).toUpperCase();
        return "9CFF9C".equals(color) || "FFFF33".equals(color) || "FF9CFF".equals(color) || "9CCEFF".equals(color);
    }

    public static long stickyFurnitureId(String requestPayload) {
        requestPayload = stripStickyFurniturePrefix(requestPayload);
        long furnitureId = NumberUtils.parseLong(WireEncoding.readVl64LengthString(requestPayload));
        if (furnitureId <= 0L) {
            furnitureId = WireReader.readLong(requestPayload, new WireReader.Offset(1));
        }
        return furnitureId;
    }

    private static String stripStickyFurniturePrefix(String packetPayload) {
        String requestPayload = StringUtils.text(packetPayload);
        if (requestPayload.startsWith("AS") || requestPayload.startsWith("AU") || requestPayload.startsWith("AN")
            || requestPayload.startsWith("FI") || requestPayload.startsWith("AB") || requestPayload.startsWith("AC")) {
            return requestPayload.substring(2);
        }
        return requestPayload;
    }

    public static FurnitureIdRequest stickyFurnitureRequest(String packetPayload) {
        return new FurnitureIdRequest(stickyFurnitureId(packetPayload));
    }

    /**
     * Original function: Proc_6_73_725540.
     */
    public static CreditFurnitureRequest creditFurnitureRequest(String packetPayload) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, "AT");
        return new CreditFurnitureRequest(stickyFurnitureId(requestPayload));
    }

    /**
     * Original function: Proc_6_157_7974B0.
     */
    public static WallFurniturePlacementRequest wallFurniturePlacementRequest(String packetPayload) {
        String wallPayload = StringUtils.text(packetPayload);
        if (wallPayload.startsWith("rv")) {
            wallPayload = wallPayload.substring(2);
        }
        long furnitureId = NumberUtils.parseLong(WireEncoding.readVl64LengthString(wallPayload));
        return new WallFurniturePlacementRequest(wallPayload, furnitureId);
    }

    /**
     * Original function: Proc_6_96_747000.
     * Original function: Proc_6_97_747640.
     */
    public static SimpleFloorItemUseRequest simpleFloorItemUseRequest(String packetPayload, String prefix) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, prefix);
        return new SimpleFloorItemUseRequest(WireRequests.id(requestPayload, ""));
    }

    /**
     * Original function: Proc_6_150_777FA0.
     */
    public static FloorFurniturePackageRequest floorFurniturePackageRequest(String packetPayload) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, "FH");
        return new FloorFurniturePackageRequest(requestPayload, floorStateFurnitureId(packetPayload));
    }

    /**
     * Original function: Proc_6_149_775C10.
     * Original function: Proc_6_150_777FA0.
     */
    public static long floorStateFurnitureId(String packetPayload) {
        String requestPayload = StringUtils.text(packetPayload);
        if (requestPayload.startsWith("Ch") || requestPayload.startsWith("FH")) {
            requestPayload = requestPayload.substring(2);
        }
        long furnitureId = WireReader.readLong(requestPayload, new WireReader.Offset(1));
        if (furnitureId <= 0L) {
            furnitureId = NumberUtils.parseLong(WireEncoding.readVl64LengthString(requestPayload));
        }
        return furnitureId;
    }

    /**
     * Original function: Proc_6_141_76A670.
     * Original function: Proc_6_142_76B310.
     */
    public static FloorFurniturePlacement floorPlacement(String packetPayload) {
        long furnitureId = 0L;
        long positionX = 0L;
        long positionY = 0L;
        long rotation = 0L;
        String normalizedPayload = StringUtils.text(packetPayload)
            .replace('\1', ' ')
            .replace('\2', ' ')
            .replace('\t', ' ')
            .replace('\r', ' ')
            .replace('\n', ' ')
            .trim();
        while (normalizedPayload.contains("  ")) {
            normalizedPayload = normalizedPayload.replace("  ", " ");
        }
        if (!normalizedPayload.isEmpty()) {
            String[] tokens = normalizedPayload.split(" ", -1);
            if (tokens.length >= 1) {
                furnitureId = NumberUtils.parseLong(tokens[0]);
            }
            if (tokens.length >= 2) {
                positionX = NumberUtils.parseLong(tokens[1]);
            }
            if (tokens.length >= 3) {
                positionY = NumberUtils.parseLong(tokens[2]);
            }
            if (tokens.length >= 4) {
                rotation = NumberUtils.parseLong(tokens[3]);
            }
        }
        if (furnitureId <= 0L) {
            furnitureId = WireReader.readLong(StringUtils.text(packetPayload), new WireReader.Offset(1));
        }
        return new FloorFurniturePlacement(furnitureId, positionX, positionY, rotation);
    }

    public static FloorPlacementRequest floorPlacementRequest(String packetPayload) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, "A[");
        requestPayload = WireRequests.stripPrefix(requestPayload, "AI");
        requestPayload = WireRequests.stripPrefix(requestPayload, "rv");
        return new FloorPlacementRequest(requestPayload, floorPlacement(requestPayload));
    }

    /**
     * Original function: Proc_6_141_76A670.
     */
    public static FurnitureMoveRequest moveRequest(String packetPayload) {
        long furnitureId = 0L;
        long positionX = 0L;
        long positionY = 0L;
        long rotation = 0L;
        String requestPayload = StringUtils.text(packetPayload);
        if (requestPayload.startsWith("A[")) {
            requestPayload = requestPayload.substring(2);
        }

        String normalizedPayload = requestPayload
            .replace('\1', ' ')
            .replace('\2', ' ')
            .replace('\t', ' ')
            .replace('\r', ' ')
            .replace('\n', ' ');
        while (normalizedPayload.contains("  ")) {
            normalizedPayload = normalizedPayload.replace("  ", " ");
        }
        normalizedPayload = normalizedPayload.trim();

        if (!normalizedPayload.isEmpty()) {
            String[] tokens = normalizedPayload.split(" ", -1);
            furnitureId = tokens.length >= 1 ? NumberUtils.parseLong(tokens[0]) : 0L;
            positionX = tokens.length >= 2 ? NumberUtils.parseLong(tokens[1]) : 0L;
            positionY = tokens.length >= 3 ? NumberUtils.parseLong(tokens[2]) : 0L;
            rotation = tokens.length >= 4 ? NumberUtils.parseLong(tokens[3]) : 0L;
        }

        if (furnitureId <= 0L) {
            WireReader.Offset offset = new WireReader.Offset(1);
            furnitureId = WireReader.readLong(requestPayload, offset);
        }
        return new FurnitureMoveRequest(furnitureId, positionX, positionY, rotation);
    }

    /**
     * Original function: Proc_6_144_76BE70.
     */
    public static long pickupFurnitureId(String packetPayload) {
        String requestPayload = StringUtils.text(packetPayload);
        if (requestPayload.startsWith("AZ")) {
            requestPayload = requestPayload.substring(2);
        }
        long furnitureId = NumberUtils.parseLong(WireEncoding.readVl64LengthString(requestPayload));
        if (furnitureId <= 0L) {
            WireReader.Offset offset = new WireReader.Offset(1);
            furnitureId = WireReader.readLong(requestPayload, offset);
        }
        return furnitureId;
    }

    /**
     * Original function: Proc_6_95_746CD0.
     */
    public static long habbowheelFurnitureId(String requestPayload) {
        return WireRequests.id(requestPayload, "");
    }

    public static boolean isDimmerColour(String colourText) {
        String color = StringUtils.text(colourText).toUpperCase();
        return "#0053F7".equals(color)
            || "#74F5F5".equals(color)
            || "#E759DE".equals(color)
            || "#EA4532".equals(color)
            || "#F2F851".equals(color)
            || "#82F349".equals(color)
            || "#000000".equals(color);
    }

    /**
     * Original function: Proc_6_100_748C80.
     */
    public static DimmerPresetRequest dimmerPresetRequest(String packetPayload) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, "EV");
        WireReader.Offset offset = new WireReader.Offset(1);
        long presetId = WireReader.readLong(requestPayload, offset);
        long backgroundId = WireReader.readLong(requestPayload, offset);
        String colourText = WireReader.readString(requestPayload, offset);
        long lightLevel = WireReader.readLong(requestPayload, offset);
        return new DimmerPresetRequest(presetId, backgroundId, colourText, lightLevel);
    }

    public static long nextState(String productSprite, long currentState, long maxState) {
        String sprite = StringUtils.text(productSprite).toLowerCase();
        if (sprite.contains("dice")) {
            return RandomUtils.longInclusive(1, 6);
        }
        if (sprite.startsWith("bb_score_") || sprite.startsWith("es_score_") || sprite.contains("score")) {
            long resolvedMaxState = maxState <= 0L ? 99L : maxState;
            long nextState = currentState + 1L;
            return nextState > resolvedMaxState ? 0L : nextState;
        }
        long resolvedMaxState = maxState <= 0L ? 1L : maxState;
        long nextState = currentState + 1L;
        return nextState > resolvedMaxState ? 0L : nextState;
    }

    private static int firstPositiveIndex(String text, char... chars) {
        int result = -1;
        for (char ch : chars) {
            int at = text.indexOf(ch);
            if (at >= 0 && (result < 0 || at < result)) {
                result = at;
            }
        }
        return result;
    }
}
