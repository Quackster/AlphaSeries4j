package com.alphaseries.game;

import com.alphaseries.dao.mysql.CatalogDao;
import com.alphaseries.game.catalog.CatalogState;
import com.alphaseries.game.catalog.ProductCache;
import com.alphaseries.game.room.RoomEventLocales;
import com.alphaseries.game.room.RoomState;

import java.util.List;

public final class GameDataCaches {
    private GameDataCaches() {
    }

    public static RoomEventLocales roomEventLocales() {
        return RoomState.instance().eventLocales();
    }

    public static void setRoomEventLocales(RoomEventLocales eventLocales) {
        RoomState.instance().setEventLocales(eventLocales);
    }

    public static ProductCache productCache() {
        return CatalogState.instance().productCache();
    }

    public static void setProductRows(List<CatalogDao.ProductCacheRow> productRows) {
        CatalogState.instance().setProductCache(ProductCache.fromRows(productRows));
    }

    public static void setProductCache(ProductCache productCache) {
        ProductCache normalized = productCache == null ? ProductCache.empty() : productCache;
        CatalogState.instance().setProductCache(normalized);
    }

}
