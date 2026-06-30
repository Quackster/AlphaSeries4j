package com.alphaseries.messages.outgoing;

import com.alphaseries.dao.mysql.ClubDao;
import com.alphaseries.game.catalog.GiftSettings;
import com.alphaseries.protocol.PacketBuilder;

import java.util.List;

public final class ClubPayloads {
    private ClubPayloads() {
    }

    public static String subscriptionOffers(List<ClubDao.ClubProductRow> offers, ClubDao.UserClubStatus status) {
        ClubDao.UserClubStatus resolvedStatus = status == null
            ? new ClubDao.UserClubStatus(0L, 0L, 0L, 0L, 0L, 0L, 0L)
            : status;
        PacketBuilder offerPayload = PacketBuilder.create();
        long offerCount = 0L;
        for (ClubDao.ClubProductRow offer : offers == null ? List.<ClubDao.ClubProductRow>of() : offers) {
            if (offer != null) {
                offerPayload
                    .appendInt(offer.productId())
                    .appendString(offer.spriteName())
                    .appendInt(offer.months())
                    .appendInt(offer.months() * 31L)
                    .appendInt(offer.level())
                    .appendInt(offer.creditPrice())
                    .appendInt(0L);
                offerCount++;
            }
        }
        return PacketBuilder.message("Iq")
            .appendInt(offerCount)
            .appendRaw(offerPayload)
            .appendInt(resolvedStatus.hcLevel())
            .appendInt(resolvedStatus.activeDays())
            .appendInt(resolvedStatus.periodsLeft())
            .appendInt(resolvedStatus.presentsAvailable())
            .build();
    }

    public static String clubGiftStatus(GiftSettings giftSettings, ClubDao.ClubGiftStatus status) {
        ClubDao.ClubGiftStatus resolvedStatus = status == null
            ? new ClubDao.ClubGiftStatus(0L, 0L, 0L, 0L, 0L)
            : status;
        GiftSettings resolvedSettings = giftSettings == null ? GiftSettings.empty() : giftSettings;
        long presentsAvailable = resolvedStatus.presentsAvailable();
        long activeDays = resolvedStatus.activeDays();
        PacketBuilder statusRows = PacketBuilder.create();
        long statusCount = 0L;
        for (GiftSettings.ClubGift gift : resolvedSettings.clubGifts()) {
            if (gift != null) {
                long canClaim = presentsAvailable > 0L && activeDays >= gift.requiredDays() ? 1L : 0L;
                statusRows
                    .appendInt(gift.catalogProductId())
                    .appendInt(gift.productId())
                    .appendInt(gift.requiredDays())
                    .appendInt(canClaim)
                    .appendRaw('H');
                statusCount++;
            }
        }
        return PacketBuilder.message("IoM")
            .appendInt(presentsAvailable)
            .appendRaw(resolvedSettings.clubGiftPayload())
            .appendInt(statusCount)
            .appendRaw(statusRows)
            .build();
    }
}
