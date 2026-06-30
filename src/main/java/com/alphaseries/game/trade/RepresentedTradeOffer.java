package com.alphaseries.game.trade;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

public record RepresentedTradeOffer(
    long socketIndex,
    long furnitureId,
    long productId,
    String signText,
    long secondaryValue,
    int fieldCount,
    String cacheRow
) {
    public static RepresentedTradeOffer stored(
        long socketIndex,
        long furnitureId,
        long productId,
        String signText,
        long secondaryValue
    ) {
        String cleanSignText = StringUtils.text(signText).replace("\r", "");
        String rowText = socketIndex + "\t" + furnitureId + "\t" + productId + "\t"
            + cleanSignText + "\t" + secondaryValue;
        return new RepresentedTradeOffer(
            socketIndex,
            furnitureId,
            productId,
            cleanSignText,
            secondaryValue,
            5,
            rowText);
    }

    public static RepresentedTradeOffer fromLegacy(String rowText) {
        String[] fields = StringUtils.text(rowText).split("\t", -1);
        if (fields.length < 2) {
            return null;
        }
        return new RepresentedTradeOffer(
            NumberUtils.parseLong(StringUtils.field(fields, 0)),
            NumberUtils.parseLong(StringUtils.field(fields, 1)),
            NumberUtils.parseLong(StringUtils.field(fields, 2)),
            StringUtils.field(fields, 3),
            NumberUtils.parseLong(StringUtils.field(fields, 4)),
            fields.length,
            StringUtils.text(rowText));
    }
}
