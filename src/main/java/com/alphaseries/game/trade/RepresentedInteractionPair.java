package com.alphaseries.game.trade;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

public record RepresentedInteractionPair(
    long socketIndex,
    long partnerSocketIndex,
    long interactionState,
    int fieldCount,
    String cacheRow
) {
    public static RepresentedInteractionPair stored(long socketIndex, long partnerSocketIndex, long interactionState) {
        String rowText = socketIndex + "\t" + partnerSocketIndex + "\t" + interactionState;
        return new RepresentedInteractionPair(socketIndex, partnerSocketIndex, interactionState, 3, rowText);
    }

    public static RepresentedInteractionPair fromLegacy(String rowText) {
        String[] fields = StringUtils.text(rowText).split("\t", -1);
        if (fields.length < 1) {
            return null;
        }
        return new RepresentedInteractionPair(
            NumberUtils.parseLong(StringUtils.field(fields, 0)),
            NumberUtils.parseLong(StringUtils.field(fields, 1)),
            NumberUtils.parseLong(StringUtils.field(fields, 2)),
            fields.length,
            StringUtils.text(rowText));
    }
}
