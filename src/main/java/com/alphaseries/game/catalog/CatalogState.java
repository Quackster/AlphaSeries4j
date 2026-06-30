package com.alphaseries.game.catalog;

public final class CatalogState {
    private static final CatalogState INSTANCE = new CatalogState();

    private GiftSettings giftSettings = GiftSettings.empty();
    private CatalogPages catalogPages = CatalogPages.empty();
    private CatalogProductSettings productSettings = CatalogProductSettings.empty();
    private CatalogRegistry registry = CatalogRegistry.empty();
    private ProductCache productCache = ProductCache.fromLegacy("");

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

    public synchronized CatalogRegistry registry() {
        return registry;
    }

    public synchronized ProductCache productCache() {
        return productCache;
    }

    public synchronized void setRegistry(CatalogRegistry registry) {
        this.registry = registry == null ? CatalogRegistry.empty() : registry;
    }

    public synchronized void setProductCache(ProductCache productCache) {
        this.productCache = productCache == null ? ProductCache.fromLegacy("") : productCache;
    }

    public synchronized void setProductCacheFromLegacy(Object products) {
        productCache = ProductCache.fromLegacy(products);
    }

    public synchronized void setRegistryFromLegacy(Object products, Object catalogProducts, Object deals) {
        registry = CatalogRegistry.fromLegacyCaches(products, catalogProducts, deals);
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
