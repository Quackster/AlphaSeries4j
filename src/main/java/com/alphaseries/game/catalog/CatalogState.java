package com.alphaseries.game.catalog;

public final class CatalogState {
    private static final CatalogState INSTANCE = new CatalogState();

    private GiftSettings giftSettings = GiftSettings.empty();
    private CatalogPages catalogPages = CatalogPages.empty();
    private CatalogProductSettings productSettings = CatalogProductSettings.empty();

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

    public synchronized CatalogPages catalogPages() {
        return catalogPages;
    }

    public synchronized CatalogProductSettings productSettings() {
        return productSettings;
    }

    public synchronized void setProductSettings(CatalogProductSettings productSettings) {
        this.productSettings = productSettings == null ? CatalogProductSettings.empty() : productSettings;
    }

    public synchronized void setProductSettingsFromLegacy(
        String counterProductIds,
        long teleportProductId,
        long moodlightProductId,
        Object packageRows,
        Object petPackageRows,
        Object clubProductRows
    ) {
        productSettings = CatalogProductSettings.fromLegacy(
            counterProductIds,
            teleportProductId,
            moodlightProductId,
            packageRows,
            petPackageRows,
            clubProductRows);
    }

    public synchronized void setCatalogPages(CatalogPages catalogPages) {
        this.catalogPages = catalogPages == null ? CatalogPages.empty() : catalogPages;
    }

    public synchronized void setCatalogPagesFromLegacy(Object pagePayloads, Object pageTrees) {
        catalogPages = CatalogPages.fromLegacy(pagePayloads, pageTrees);
    }
}
