package com.alphaseries.game.catalog;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class GiftSettings {
    private final String clubGiftPayload;
    private final String clubGiftLookup;
    private final String giftWrapLookup;
    private final String giftWrapPayload;
    private final List<ClubGift> clubGifts;

    private GiftSettings(String clubGiftPayload, String clubGiftLookup, String giftWrapLookup, String giftWrapPayload) {
        this.clubGiftPayload = StringUtils.text(clubGiftPayload);
        this.clubGiftLookup = StringUtils.text(clubGiftLookup);
        this.giftWrapLookup = StringUtils.text(giftWrapLookup);
        this.giftWrapPayload = StringUtils.text(giftWrapPayload);
        this.clubGifts = parseClubGifts(this.clubGiftLookup);
    }

    public static GiftSettings fromLegacy(String clubGiftPayload, String clubGiftLookup, String giftWrapLookup, String giftWrapPayload) {
        return new GiftSettings(clubGiftPayload, clubGiftLookup, giftWrapLookup, giftWrapPayload);
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

    public ClubGift clubGiftByCatalogProductId(long catalogProductId) {
        for (ClubGift gift : clubGifts) {
            if (gift.catalogProductId() == catalogProductId) {
                return gift;
            }
        }
        return ClubGift.empty();
    }

    public boolean containsGiftWrapProduct(long productId) {
        return productId > 0L && giftWrapLookup.contains("\r" + productId + "\r");
    }

    private static List<ClubGift> parseClubGifts(String lookup) {
        List<ClubGift> gifts = new ArrayList<>();
        for (String row : lookup.replace("[", "").split("]", -1)) {
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

    public record ClubGift(long catalogProductId, long productId, long requiredDays) {
        private static ClubGift empty() {
            return new ClubGift(0L, 0L, 0L);
        }
    }
}
