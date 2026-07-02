package com.alphaseries.game.advertising;

public final class AdvertisingState {
    private static final AdvertisingState INSTANCE = new AdvertisingState();

    private VisitRoomAds visitRoomAds = VisitRoomAds.empty();

    private AdvertisingState() {
    }

    public static AdvertisingState instance() {
        return INSTANCE;
    }

    public synchronized VisitRoomAds visitRoomAds() {
        return visitRoomAds;
    }

    public synchronized void setVisitRoomAds(VisitRoomAds ads) {
        visitRoomAds = ads == null ? VisitRoomAds.empty() : ads;
    }

}
