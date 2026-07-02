package com.alphaseries.game.inventory;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

public record CreditFurniture(long value) {
    public boolean redeemable() {
        return value > 0L;
    }

    public static CreditFurniture fromSprite(String productSprite) {
        String sprite = StringUtils.text(productSprite);
        int valueStart;
        if (sprite.startsWith("CF_")) {
            valueStart = 3;
        } else if (sprite.startsWith("CFC_")) {
            valueStart = 4;
        } else {
            return new CreditFurniture(0L);
        }

        int valueEnd = sprite.indexOf('_', valueStart);
        String valueText = valueEnd >= 0 ? sprite.substring(valueStart, valueEnd) : sprite.substring(valueStart);
        return new CreditFurniture(NumberUtils.parseLong(valueText));
    }
}
