package com.alphaseries.game.catalog;

import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class GiftSettings {
    private final ClubGiftState clubGiftState;
    private final GiftWrapState giftWrapState;
    private final List<ClubGift> clubGifts;
    private final Set<Long> giftWrapProductIds;

    private GiftSettings(ClubGiftState clubGiftState, GiftWrapState giftWrapState) {
        this.clubGiftState = clubGiftState == null ? ClubGiftState.empty() : clubGiftState;
        this.giftWrapState = giftWrapState == null ? GiftWrapState.empty() : giftWrapState;
        this.clubGifts = this.clubGiftState.gifts();
        this.giftWrapProductIds = copyGiftWrapProductIds(this.giftWrapState.productIds());
    }

    public static GiftSettings fromRows(List<ClubGift> clubGifts, List<Long> giftWrapProductIds) {
        return new GiftSettings(ClubGiftState.fromGifts(clubGifts), GiftWrapState.fromProductIds(giftWrapProductIds));
    }

    public static GiftSettings fromStates(ClubGiftState clubGiftState, GiftWrapState giftWrapState) {
        return new GiftSettings(clubGiftState, giftWrapState);
    }

    public static GiftSettings empty() {
        return new GiftSettings(ClubGiftState.empty(), GiftWrapState.empty());
    }

    public void appendClubGiftPayloadTo(PacketBuilder payload) {
        if (payload != null) {
            payload.appendRaw(clubGiftState.payload());
        }
    }

    public void appendGiftWrapPayloadTo(PacketBuilder payload) {
        if (payload != null) {
            payload.appendRaw(giftWrapState.payload());
        }
    }

    ClubGiftState clubGiftState() {
        return clubGiftState;
    }

    GiftWrapState giftWrapState() {
        return giftWrapState;
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

    public static final class ClubGiftState {
        private final String payload;
        private final List<ClubGift> gifts;

        private ClubGiftState(String payload, List<ClubGift> gifts) {
            this.payload = StringUtils.text(payload);
            this.gifts = gifts == null ? List.of() : List.copyOf(gifts);
        }

        public static ClubGiftState empty() {
            return new ClubGiftState("", List.of());
        }

        public static ClubGiftState fromGifts(List<ClubGift> gifts) {
            return new ClubGiftState("", gifts);
        }

        static ClubGiftState fromPayload(String payload, List<ClubGift> gifts) {
            return new ClubGiftState(payload, gifts);
        }

        String payload() {
            return payload;
        }

        public List<ClubGift> gifts() {
            return gifts;
        }
    }

    public static final class GiftWrapState {
        private final String payload;
        private final List<Long> productIds;

        private GiftWrapState(String payload, List<Long> productIds) {
            payload = StringUtils.text(payload);
            this.payload = payload;
            this.productIds = productIds == null ? List.of() : List.copyOf(copyGiftWrapProductIds(productIds));
        }

        public static GiftWrapState empty() {
            return new GiftWrapState("", List.of());
        }

        public static GiftWrapState fromProductIds(List<Long> productIds) {
            return new GiftWrapState("", productIds);
        }

        static GiftWrapState fromPayload(String payload, List<Long> productIds) {
            return new GiftWrapState(payload, productIds);
        }

        String payload() {
            return payload;
        }

        public List<Long> productIds() {
            return productIds;
        }
    }
}
