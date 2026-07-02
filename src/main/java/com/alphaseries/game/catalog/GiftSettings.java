package com.alphaseries.game.catalog;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class GiftSettings {
    private final String clubGiftPayload;
    private final String giftWrapPayload;
    private final List<ClubGift> clubGifts;
    private final Set<Long> giftWrapProductIds;

    private GiftSettings(String clubGiftPayload, List<ClubGift> clubGifts, List<Long> giftWrapProductIds,
                         String giftWrapPayload) {
        this.clubGiftPayload = StringUtils.text(clubGiftPayload);
        this.giftWrapPayload = StringUtils.text(giftWrapPayload);
        this.clubGifts = clubGifts == null ? List.of() : List.copyOf(clubGifts);
        this.giftWrapProductIds = copyGiftWrapProductIds(giftWrapProductIds);
    }

    public static GiftSettings fromRows(String clubGiftPayload, List<ClubGift> clubGifts,
                                        List<Long> giftWrapProductIds, String giftWrapPayload) {
        return new GiftSettings(clubGiftPayload, clubGifts, giftWrapProductIds, giftWrapPayload);
    }

    public static GiftSettings empty() {
        return new GiftSettings("", List.of(), List.of(), "");
    }

    public String clubGiftPayload() {
        return clubGiftPayload;
    }

    public String giftWrapPayload() {
        return giftWrapPayload;
    }

    public List<ClubGift> clubGifts() {
        return clubGifts;
    }

    public List<Long> giftWrapProductIds() {
        return List.copyOf(giftWrapProductIds);
    }

    public ClubGift clubGiftByCatalogProductId(long catalogProductId) {
        for (ClubGift gift : clubGifts) {
            if (gift.catalogProductId() == catalogProductId) {
                return gift;
            }
        }
        return ClubGift.empty();
    }

    public boolean containsGiftWrapProduct(long productId) {
        return productId > 0L && giftWrapProductIds.contains(productId);
    }

    private static Set<Long> copyGiftWrapProductIds(List<Long> productIds) {
        Set<Long> copiedProductIds = new LinkedHashSet<>();
        if (productIds != null) {
            for (Long productId : productIds) {
                long value = NumberUtils.parseLong(productId);
                if (value > 0L) {
                    copiedProductIds.add(value);
                }
            }
        }
        return Collections.unmodifiableSet(copiedProductIds);
    }

    public record ClubGift(long catalogProductId, long productId, long requiredDays) {
        private static ClubGift empty() {
            return new ClubGift(0L, 0L, 0L);
        }
    }

    public record ClubGiftState(String payload, List<ClubGift> gifts) {
        public ClubGiftState {
            payload = StringUtils.text(payload);
            gifts = gifts == null ? List.of() : List.copyOf(gifts);
        }
    }
}
