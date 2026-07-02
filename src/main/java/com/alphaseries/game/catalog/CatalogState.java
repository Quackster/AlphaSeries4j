package com.alphaseries.game.catalog;

import com.alphaseries.dao.mysql.CatalogDao;

public final class CatalogState {
    private static final CatalogState INSTANCE = new CatalogState();

    private GiftSettings giftSettings = GiftSettings.empty();
    private CatalogPages catalogPages = CatalogPages.empty();
    private CatalogProductSettings productSettings = CatalogProductSettings.empty();
    private CatalogRegistry registry = CatalogRegistry.empty();
    private ProductCache productCache = ProductCache.empty();

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
        this.productCache = productCache == null ? ProductCache.empty() : productCache;
    }

    public synchronized void setRegistryFromRows(
        Iterable<CatalogDao.ProductCacheRow> products,
        Iterable<CatalogDao.CatalogProductCacheRow> catalogProducts,
        Iterable<CatalogDao.ProductDealRow> deals
    ) {
        registry = CatalogRegistry.fromRows(products, catalogProducts, deals);
    }

    public synchronized void setProductSettings(CatalogProductSettings productSettings) {
        this.productSettings = productSettings == null ? CatalogProductSettings.empty() : productSettings;
    }

    public synchronized void setCatalogPages(CatalogPages catalogPages) {
        this.catalogPages = catalogPages == null ? CatalogPages.empty() : catalogPages;
    }
}
