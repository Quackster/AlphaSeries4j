package com.alphaseries.game.catalog;

import com.alphaseries.dao.mysql.CatalogDao;
import com.alphaseries.dao.mysql.ClubDao;
import com.alphaseries.dao.mysql.PackageDao;
import com.alphaseries.db.Database;
import com.alphaseries.db.MySQL;
import com.alphaseries.game.GameDataCaches;
import com.alphaseries.game.recycler.RecyclerBootCache;
import com.alphaseries.game.recycler.RecyclerState;

import java.util.List;

public final class CatalogStartupBootCache {
    private CatalogStartupBootCache() {
    }

    /**
     * Original function: Proc_1_1_6BB340.
     */
    public static void loadCatalogStartupCache() {
        CatalogDao catalog = catalogDao();
        List<CatalogDao.ProductCacheRow> products = List.of();
        if (catalog != null) {
            try {
                products = catalog.productCacheRows();
            } catch (Exception ignored) {
                // Legacy startup cache loading tolerated missing tables or SQL failures.
            }
        }
        GameDataCaches.setProductRows(products);

        List<CatalogDao.CatalogProductCacheRow> catalogProducts = List.of();
        if (catalog != null) {
            try {
                catalogProducts = catalog.catalogProductCacheRows();
            } catch (Exception ignored) {
                // Legacy startup cache loading tolerated missing tables or SQL failures.
            }
        }
        List<CatalogDao.ProductDealRow> productDeals = List.of();
        if (catalog != null) {
            try {
                productDeals = catalog.productDealRows();
            } catch (Exception ignored) {
                // Legacy startup cache loading tolerated missing tables or SQL failures.
            }
        }
        CatalogState.instance().setRegistry(CatalogRegistry.fromRows(products, catalogProducts, productDeals));

        PackageDao packages = packageDao();
        ClubDao clubs = clubDao();
        List<Long> counterProductIds = List.of();
        long teleportProductId = 0L;
        long moodlightProductId = 0L;
        if (catalog != null) {
            try {
                RecyclerState.instance().setBoxProductId(catalog.productIdBySprite("ecotron_box"));
                counterProductIds = catalog.counterProductIds();
                teleportProductId = catalog.firstProductIdByType(11L);
                moodlightProductId = catalog.firstProductIdByType(19L);
            } catch (Exception ignored) {
                // Legacy startup cache loading tolerated missing tables or SQL failures.
            }
        }
        List<PackageDao.PackageRow> packageRows = List.of();
        List<PackageDao.PetPackageRow> petPackageRows = List.of();
        if (packages != null) {
            try {
                packageRows = packages.packageRows();
                petPackageRows = packages.petPackageRows();
            } catch (Exception ignored) {
                // Legacy startup cache loading tolerated missing tables or SQL failures.
            }
        }
        List<ClubDao.ContainedClubProductRow> clubProductRows = List.of();
        if (clubs != null) {
            try {
                clubProductRows = clubs.containedClubProductRows();
            } catch (Exception ignored) {
                // Legacy startup cache loading tolerated missing tables or SQL failures.
            }
        }
        CatalogState.instance().setProductSettings(CatalogProductSettings.fromRows(
            counterProductIds,
            teleportProductId,
            moodlightProductId,
            packageRows,
            petPackageRows,
            clubProductRows));
        CatalogPageBootCache.loadCatalogPageTreeCache();
        CatalogPageBootCache.loadCatalogPagePayloadCache();
        CatalogGiftBootCache.loadClubGiftCache();
        CatalogGiftBootCache.loadGiftWrapCache();
        RecyclerBootCache.loadRecyclerRewardsCache();
    }

    private static CatalogDao catalogDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new CatalogDao(database);
    }

    private static PackageDao packageDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new PackageDao(database);
    }

    private static ClubDao clubDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new ClubDao(database);
    }
}
