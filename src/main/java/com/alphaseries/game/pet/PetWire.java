package com.alphaseries.game.pet;

import com.alphaseries.protocol.WireEncoding;
import com.alphaseries.protocol.WireReader;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

public final class PetWire {
    private PetWire() {
    }

    public record PackagePreviewRequest(long furnitureId) {
    }

    public record PackagePlacementRequest(long furnitureId, String petName) {
        public PackagePlacementRequest {
            petName = StringUtils.singleLineText(petName);
        }
    }

    public record RaceListRequest(String productPet) {
        public RaceListRequest {
            productPet = StringUtils.text(productPet);
        }
    }

    public record RoomPlacementRequest(long petId, long positionX, long positionY, long rotation) {
    }

    public record PetIdRequest(long petId) {
    }

    public record CommandRequest(long petId, long commandId) {
    }

    public record NameValidationRequest(String petName) {
        public NameValidationRequest {
            petName = StringUtils.sqlEscapedText(StringUtils.singleLineText(petName));
        }
    }

    /**
     * Original function: Proc_6_86_73B0D0.
     */
    public static PackagePreviewRequest packagePreviewRequest(String packetPayload) {
        String requestPayload = StringUtils.withoutAnyPrefix(packetPayload, "p`", "rt");
        return new PackagePreviewRequest(furnitureId(requestPayload, new WireReader.Offset(1)));
    }

    /**
     * Original function: Proc_6_87_73C120.
     */
    public static PackagePlacementRequest packagePlacementRequest(String packetPayload) {
        String requestPayload = StringUtils.withoutPrefix(packetPayload, "n~");
        WireReader.Offset offset = new WireReader.Offset(1);
        long furnitureId = furnitureId(requestPayload, offset);
        String petName = StringUtils.singleLineText(WireReader.readString(requestPayload, offset));
        if (petName.isEmpty()) {
            petName = StringUtils.singleLineText(WireEncoding.readBase64LengthString(requestPayload));
        }
        return new PackagePlacementRequest(furnitureId, petName);
    }

    /**
     * Original function: Proc_6_177_7C6580.
     */
    public static RaceListRequest raceListRequest(String packetPayload) {
        String requestPayload = StringUtils.withoutPrefix(packetPayload, "n" + '\177');
        String productPet = WireEncoding.readBase64LengthString(requestPayload);
        if (productPet.isEmpty()) {
            productPet = WireReader.readString(requestPayload, new WireReader.Offset(1));
        }
        return new RaceListRequest(productPet);
    }

    /**
     * Original function: Proc_6_179_7C7790.
     */
    public static RoomPlacementRequest roomPlacementRequest(String packetPayload) {
        String requestPayload = StringUtils.withoutPrefix(packetPayload, "nz");
        WireReader.Offset offset = new WireReader.Offset(1);
        return new RoomPlacementRequest(
            WireReader.readLong(requestPayload, offset),
            WireReader.readLong(requestPayload, offset),
            WireReader.readLong(requestPayload, offset),
            WireReader.readLong(requestPayload, offset));
    }

    /**
     * Original function: Proc_6_182_7CAAD0.
     */
    public static NameValidationRequest nameValidationRequest(String packetPayload) {
        String requestPayload = StringUtils.withoutPrefix(packetPayload, "@c");
        String petName = WireEncoding.readBase64LengthString(requestPayload);
        if (petName.isEmpty()) {
            petName = WireReader.readString(requestPayload, new WireReader.Offset(1));
        }
        return new NameValidationRequest(petName);
    }

    /**
     * Original function: Proc_6_183_7CABF0.
     * Original function: Proc_7CC190.
     */
    public static PetIdRequest petIdRequest(String packetPayload, String prefix) {
        String requestPayload = StringUtils.withoutPrefix(packetPayload, prefix);
        return new PetIdRequest(WireReader.readLong(requestPayload, new WireReader.Offset(1)));
    }

    /**
     * Original function: Proc_7CA730.
     */
    public static CommandRequest commandRequest(String packetPayload) {
        String requestPayload = StringUtils.withoutPrefix(packetPayload, "n{");
        WireReader.Offset offset = new WireReader.Offset(1);
        return new CommandRequest(WireReader.readLong(requestPayload, offset), WireReader.readLong(requestPayload, offset));
    }

    private static long furnitureId(String requestPayload, WireReader.Offset offset) {
        long furnitureId = WireReader.readLong(requestPayload, offset);
        if (furnitureId <= 0L) {
            furnitureId = NumberUtils.parseLong(WireEncoding.readVl64LengthString(requestPayload));
        }
        return furnitureId;
    }
}
