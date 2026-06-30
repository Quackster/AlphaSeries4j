package com.alphaseries.game.catalog;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class GiftSettings {
    private final String clubGiftPayload;
    private final String giftWrapPayload;
    private final List<ClubGift> clubGifts;
    private final Set<Long> giftWrapProductIds;

    private GiftSettings(Object clubGiftPayload, Object clubGiftLookup, Object giftWrapLookup, String giftWrapPayload) {
        ClubGiftState typedState = clubGiftPayload instanceof ClubGiftState state ? state : null;
        this.clubGiftPayload = typedState == null ? StringUtils.text(clubGiftPayload) : typedState.payload();
        this.giftWrapPayload = StringUtils.text(giftWrapPayload);
        this.clubGifts = typedState == null ? parseClubGifts(clubGiftLookup) : typedState.gifts();
        this.giftWrapProductIds = parseGiftWrapProductIds(giftWrapLookup);
    }

    private GiftSettings(String clubGiftPayload, List<ClubGift> clubGifts, List<Long> giftWrapProductIds,
                         String giftWrapPayload) {
        this.clubGiftPayload = StringUtils.text(clubGiftPayload);
        this.giftWrapPayload = StringUtils.text(giftWrapPayload);
        this.clubGifts = clubGifts == null ? List.of() : List.copyOf(clubGifts);
        this.giftWrapProductIds = copyGiftWrapProductIds(giftWrapProductIds);
    }

    public static GiftSettings fromLegacy(Object clubGiftPayload, Object clubGiftLookup, Object giftWrapLookup, String giftWrapPayload) {
        if (clubGiftPayload instanceof GiftSettings giftSettings) {
            return giftSettings;
        }
        return new GiftSettings(clubGiftPayload, clubGiftLookup, giftWrapLookup, giftWrapPayload);
    }

    public static GiftSettings fromRows(String clubGiftPayload, List<ClubGift> clubGifts,
                                        List<Long> giftWrapProductIds, String giftWrapPayload) {
        return new GiftSettings(clubGiftPayload, clubGifts, giftWrapProductIds, giftWrapPayload);
    }

    public static GiftSettings empty() {
        return new GiftSettings("", "", "", "");
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

    private static List<ClubGift> parseClubGifts(Object lookup) {
        List<ClubGift> gifts = new ArrayList<>();
        for (String row : StringUtils.text(lookup).replace("[", "").split("]", -1)) {
            if (!row.isEmpty()) {
                String[] fields = row.replace('\1', '\0').split("\0", -1);
                if (fields.length >= 3) {
                    gifts.add(new ClubGift(
                        NumberUtils.parseLong(StringUtils.field(fields, 0)),
                        NumberUtils.parseLong(StringUtils.field(fields, 1)),
                        NumberUtils.parseLong(StringUtils.field(fields, 2))));
                }
            }
        }
        return Collections.unmodifiableList(gifts);
    }

    private static Set<Long> parseGiftWrapProductIds(Object lookup) {
        Set<Long> productIds = new LinkedHashSet<>();
        if (lookup instanceof Iterable<?> values) {
            for (Object productIdValue : values) {
                long productId = NumberUtils.parseLong(productIdValue);
                if (productId > 0L) {
                    productIds.add(productId);
                }
            }
            return Collections.unmodifiableSet(productIds);
        }
        for (String productIdText : StringUtils.text(lookup).split("\r", -1)) {
            long productId = NumberUtils.parseLong(productIdText);
            if (productId > 0L) {
                productIds.add(productId);
            }
        }
        return Collections.unmodifiableSet(productIds);
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

    public record ClubGiftState(String payload, String lookup, List<ClubGift> gifts) {
        public ClubGiftState {
            payload = StringUtils.text(payload);
            lookup = StringUtils.text(lookup);
            gifts = gifts == null ? List.of() : List.copyOf(gifts);
        }
    }
}
