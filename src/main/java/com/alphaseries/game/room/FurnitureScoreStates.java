package com.alphaseries.game.room;

import com.alphaseries.dao.mysql.FurnitureDao;
import com.alphaseries.game.catalog.ProductCache;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

public final class FurnitureScoreStates {
    private FurnitureScoreStates() {
    }

    public static Target refreshTarget(long firstId, long secondId, FurnitureDao furniture, ProductCache productCache) {
        if (furniture == null || productCache == null) {
            return Target.empty();
        }
        try {
            long furnitureId = 0L;
            long productId = 0L;
            FurnitureDao.LocatedFurnitureState furnitureState = null;
            if (secondId > 0L) {
                furnitureState = furniture.locatedFurnitureState(secondId).orElse(null);
                if (furnitureState != null) {
                    furnitureId = secondId;
                    productId = firstId;
                }
            }
            if (furnitureState == null && firstId > 0L) {
                furnitureState = furniture.locatedFurnitureState(firstId).orElse(null);
                if (furnitureState != null) {
                    furnitureId = firstId;
                }
            }
            if (furnitureState == null && secondId > 0L) {
                furnitureState = furniture.newestLocatedFurnitureStateByProduct(secondId).orElse(null);
                if (furnitureState != null) {
                    productId = secondId;
                }
            }
            if (furnitureState == null) {
                return Target.empty();
            }
            long roomId = furnitureState.roomId();
            if (productId <= 0L) {
                productId = furnitureState.productId();
            }
            String signText = StringUtils.text(furnitureState.sign());
            if (roomId <= 0L || productId <= 0L) {
                return Target.empty();
            }
            if (furnitureId <= 0L) {
                furnitureId = furniture.newestFurnitureIdByRoomAndProduct(roomId, productId);
            }
            if (furnitureId <= 0L) {
                return Target.empty();
            }
            String productSprite = productCache.primarySprite(productId).toLowerCase();
            if (productSprite.isEmpty()) {
                productSprite = productCache.alternateSprite(productId).toLowerCase();
            }
            if (productSprite.startsWith("bb_score_") || productSprite.startsWith("es_score_")) {
                long stateValue = NumberUtils.parseLong(signText);
                long maxState = productCache.maxState(productId);
                if (maxState <= 0L) {
                    maxState = 99L;
                }
                if (stateValue < 0L) {
                    stateValue = 0L;
                }
                if (stateValue > maxState) {
                    stateValue = maxState;
                }
                if (!String.valueOf(stateValue).equals(signText)) {
                    furniture.updateSignLimited(furnitureId, stateValue);
                }
            }
            return new Target(furnitureId, productId);
        } catch (Exception ignored) {
            return Target.empty();
        }
    }

    public record Target(long furnitureId, long productId) {
        public static Target empty() {
            return new Target(0L, 0L);
        }

        public boolean found() {
            return furnitureId > 0L && productId > 0L;
        }
    }
}
