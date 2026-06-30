package com.alphaseries.messages.outgoing;

import com.alphaseries.dao.mysql.ClubDao;
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
}
