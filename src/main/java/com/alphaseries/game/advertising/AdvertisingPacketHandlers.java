package com.alphaseries.game.advertising;

import com.alphaseries.server.runtime.SocketDelivery;

public final class AdvertisingPacketHandlers {
    private AdvertisingPacketHandlers() {
    }

    /**
     * Original function: Proc_6_59_71FEE0.
     */
    public static void sendVisitRoomAdvertisement(int socketIndex) {
        try {
            String advertisementPayload = "\2\2";
            VisitRoomAds visitRoomAds = AdvertisingState.instance().visitRoomAds();
            if (visitRoomAds.count() > 0L) {
                String candidate = visitRoomAds.randomPayload();
                if (!candidate.isEmpty()) {
                    advertisementPayload = candidate;
                }
            }
            SocketDelivery.sendToSocket(socketIndex, "DB" + advertisementPayload);
        } catch (Exception ignored) {
        }
    }
}
