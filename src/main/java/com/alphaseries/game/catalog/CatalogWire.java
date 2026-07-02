package com.alphaseries.game.catalog;

import com.alphaseries.protocol.WireEncoding;
import com.alphaseries.protocol.WireReader;
import com.alphaseries.protocol.WireRequests;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

public final class CatalogWire {
    private CatalogWire() {
    }

    public record ProductPurchaseRequest(long catalogProductId, String signText) {
        public ProductPurchaseRequest {
            signText = StringUtils.singleLineText(signText);
        }
    }

    public record ClubGiftClaimRequest(String requestedSprite) {
        public ClubGiftClaimRequest {
            requestedSprite = StringUtils.text(requestedSprite).replace("\2", "").replace("\0", "").trim();
        }
    }

    public record GiftPurchaseRequest(
        long catalogProductId,
        long expectedProductId,
        String recipientName,
        String giftMessage,
        long wrapProductId,
        long ribbonId,
        long colorId
    ) {
        public GiftPurchaseRequest {
            recipientName = StringUtils.singleLineText(recipientName);
            giftMessage = StringUtils.left(StringUtils.singleLineText(giftMessage), 142);
        }
    }

    public record GiftAvailabilityRequest(long itemId) {
    }

    public record PageRequest(long pageId) {
    }

    /**
     * Original function: Proc_6_128_756190.
     */
    public static ProductPurchaseRequest productPurchaseRequest(String packetPayload) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, "Ad");
        WireReader.Offset offset = new WireReader.Offset(1);
        long catalogProductId = WireReader.readLong(requestPayload, offset);
        String signText = WireReader.readString(requestPayload, offset);
        if (catalogProductId <= 0L) {
            catalogProductId = NumberUtils.parseLong(WireEncoding.readVl64LengthString(requestPayload));
        }
        if (signText.isEmpty()) {
            signText = WireEncoding.readBase64LengthString(requestPayload);
        }
        return new ProductPurchaseRequest(catalogProductId, signText);
    }

    /**
     * Original function: Proc_6_130_75B770.
     */
    public static ClubGiftClaimRequest clubGiftClaimRequest(String packetPayload) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, "G[");
        String requestedSprite = WireEncoding.readBase64LengthString(requestPayload);
        if (requestedSprite.isEmpty()) {
            requestedSprite = WireEncoding.readVl64LengthString(requestPayload);
        }
        if (requestedSprite.isEmpty()) {
            requestedSprite = requestPayload;
        }
        return new ClubGiftClaimRequest(requestedSprite);
    }

    /**
     * Original function: Proc_6_132_75D4A0.
     */
    public static GiftPurchaseRequest giftPurchaseRequest(String packetPayload) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, "GX");
        WireReader.Offset offset = new WireReader.Offset(1);
        long catalogProductId = WireReader.readLong(requestPayload, offset);
        long expectedProductId = WireReader.readLong(requestPayload, offset);
        String recipientName = WireReader.readString(requestPayload, offset);
        String giftMessage = WireReader.readString(requestPayload, offset);
        long wrapProductId = WireReader.readLong(requestPayload, offset);
        long ribbonId = WireReader.readLong(requestPayload, offset);
        long colorId = WireReader.readLong(requestPayload, offset);
        if (catalogProductId <= 0L) {
            catalogProductId = NumberUtils.parseLong(WireEncoding.readVl64LengthString(requestPayload));
        }
        if (recipientName.isEmpty()) {
            recipientName = WireEncoding.readBase64LengthString(requestPayload);
        }
        return new GiftPurchaseRequest(catalogProductId, expectedProductId, recipientName, giftMessage,
            wrapProductId, ribbonId, colorId);
    }

    /**
     * Original function: Proc_6_134_765B90.
     */
    public static GiftAvailabilityRequest giftAvailabilityRequest(String packetPayload) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, "oV");
        long itemId = NumberUtils.parseLong(WireEncoding.readVl64LengthString(requestPayload));
        if (itemId <= 0L) {
            itemId = WireReader.readLong(requestPayload, new WireReader.Offset(1));
        }
        return new GiftAvailabilityRequest(itemId);
    }

    /**
     * Original function: Proc_6_136_765F10.
     */
    public static PageRequest pageRequest(String packetPayload) {
        String requestPayload = StringUtils.withoutPacketCode(packetPayload);
        long pageId = NumberUtils.parseLong(WireEncoding.readVl64LengthString(requestPayload));
        if (pageId <= 0L) {
            pageId = WireReader.readLong(requestPayload, new WireReader.Offset(1));
        }
        return new PageRequest(pageId);
    }
}
