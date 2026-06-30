package com.alphaseries.game.catalog;

public final class CatalogState {
    private static final CatalogState INSTANCE = new CatalogState();

    private GiftSettings giftSettings = GiftSettings.empty();

    private CatalogState() {
    }

    public static CatalogState instance() {
        return INSTANCE;
    }

    public synchronized GiftSettings giftSettings() {
        return giftSettings;
    }

    public synchronized void setGiftSettings(GiftSettings giftSettings) {
        this.giftSettings = giftSettings == null ? GiftSettings.empty() : giftSettings;
    }

    public synchronized void setGiftSettingsFromLegacy(
        Object clubGiftPayload,
        Object clubGiftLookup,
        String giftWrapLookup,
        String giftWrapPayload
    ) {
        giftSettings = GiftSettings.fromLegacy(clubGiftPayload, clubGiftLookup, giftWrapLookup, giftWrapPayload);
    }
}
