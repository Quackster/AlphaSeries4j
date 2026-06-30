package com.alphaseries.game.trade;

import com.alphaseries.util.StringUtils;

public record RepresentedTradeOffer(
    long socketIndex,
    long furnitureId,
    long productId,
    String signText,
    long secondaryValue
) {
    public static RepresentedTradeOffer stored(
        long socketIndex,
        long furnitureId,
        long productId,
        String signText,
        long secondaryValue
    ) {
        String cleanSignText = StringUtils.text(signText).replace("\r", "");
        return new RepresentedTradeOffer(
            socketIndex,
            furnitureId,
            productId,
            cleanSignText,
            secondaryValue);
    }
}
