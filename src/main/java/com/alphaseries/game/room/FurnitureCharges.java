package com.alphaseries.game.room;

import com.alphaseries.game.catalog.CatalogRegistry;
import com.alphaseries.messages.outgoing.FurniturePayloads;
import com.alphaseries.util.FileUtils;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.nio.file.Path;

public final class FurnitureCharges {
    private FurnitureCharges() {
    }

    public static String consumeOrPrompt(long furnitureId, CatalogRegistry.Product product, String applicationPath) {
        if (furnitureId <= 0L || product == null || product.chargeSize() == 0L) {
            return "";
        }
        try {
            Path chargePath = Path.of(StringUtils.text(applicationPath), "cache", "items_charges", furnitureId + ".cache");
            long currentCharges = NumberUtils.parseLong(FileUtils.readTextFile(chargePath.toString()));
            if (currentCharges < 1L) {
                return FurniturePayloads.chargePrompt(
                    furnitureId,
                    currentCharges,
                    product.chargeSize(),
                    product.chargePriceCredits(),
                    product.chargePriceActivityPoints(),
                    product.chargePriceActivityPointsType());
            }
            FileUtils.writeTextFile(chargePath.toString(), String.valueOf(currentCharges - 1L));
            return "";
        } catch (Exception ignored) {
            return "";
        }
    }
}
