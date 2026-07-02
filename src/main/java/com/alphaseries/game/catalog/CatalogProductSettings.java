package com.alphaseries.game.catalog;

import com.alphaseries.dao.mysql.ClubDao;
import com.alphaseries.dao.mysql.PackageDao;
import com.alphaseries.util.NumberUtils;

import java.util.ArrayList;
import java.util.List;

public final class CatalogProductSettings {
    private final List<CounterProductSetting> counterProducts;
    private final long teleportProductId;
    private final long moodlightProductId;
    private final List<PackageDao.PackageRow> packageRows;
    private final List<PackageDao.PetPackageRow> petPackageRows;
    private final List<ClubProductSetting> clubProductRows;

    private CatalogProductSettings(
        List<Long> counterProductIds,
        long teleportProductId,
        long moodlightProductId,
        List<PackageDao.PackageRow> packageRows,
        List<PackageDao.PetPackageRow> petPackageRows,
        List<ClubProductSetting> clubProductRows
    ) {
        this.counterProducts = counterProductSettings(counterProductIds);
        this.teleportProductId = Math.max(0L, teleportProductId);
        this.moodlightProductId = Math.max(0L, moodlightProductId);
        this.packageRows = copyRows(packageRows);
        this.petPackageRows = copyRows(petPackageRows);
        this.clubProductRows = copyRows(clubProductRows);
    }

    public static CatalogProductSettings empty() {
        return new CatalogProductSettings(List.of(), 0L, 0L, List.of(), List.of(), List.of());
    }

    public static CatalogProductSettings fromRows(
        List<Long> counterProductIds,
        long teleportProductId,
        long moodlightProductId,
        List<PackageDao.PackageRow> packageRows,
        List<PackageDao.PetPackageRow> petPackageRows,
        List<ClubDao.ContainedClubProductRow> clubProductRows
    ) {
        return new CatalogProductSettings(counterProductIds, teleportProductId, moodlightProductId,
            packageRows, petPackageRows, clubProductSettings(clubProductRows));
    }

    public static CatalogProductSettings fromCounterProductIds(
        List<Long> counterProductIds,
        long teleportProductId,
        long moodlightProductId,
        List<PackageDao.PackageRow> packageRows,
        List<PackageDao.PetPackageRow> petPackageRows,
        List<ClubDao.ContainedClubProductRow> clubProductRows
    ) {
        return new CatalogProductSettings(counterProductIds, teleportProductId, moodlightProductId,
            packageRows, petPackageRows, clubProductSettings(clubProductRows));
    }

    public static CatalogProductSettings fromSettings(
        List<Long> counterProductIds,
        long teleportProductId,
        long moodlightProductId,
        List<PackageDao.PackageRow> packageRows,
        List<PackageDao.PetPackageRow> petPackageRows,
        List<ClubProductSetting> clubProductRows
    ) {
        return new CatalogProductSettings(counterProductIds, teleportProductId, moodlightProductId,
            packageRows, petPackageRows, clubProductRows);
    }

    public List<Long> counterProducts() {
        List<Long> productIds = new ArrayList<>();
        for (CounterProductSetting counterProduct : counterProducts) {
            productIds.add(counterProduct.productId());
        }
        return List.copyOf(productIds);
    }

    public long teleportProductId() {
        return teleportProductId;
    }

    public long moodlightProductId() {
        return moodlightProductId;
    }

    public List<PackageDao.PackageRow> packages() {
        return List.copyOf(packageRows);
    }

    public List<PackageDao.PetPackageRow> petPackages() {
        return List.copyOf(petPackageRows);
    }

    public List<ClubProductSetting> clubProducts() {
        return List.copyOf(clubProductRows);
    }

    public boolean containsClubProduct(long productId) {
        if (productId <= 0L) {
            return false;
        }
        if (!clubProductRows.isEmpty()) {
            for (ClubProductSetting row : clubProductRows) {
                if (row.productId() == productId) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public boolean containsCounterProduct(long productId) {
        String wanted = String.valueOf(NumberUtils.parseLong(productId));
        for (CounterProductSetting counterProduct : counterProducts) {
            if (wanted.equals(String.valueOf(counterProduct.productId()))) {
                return true;
            }
        }
        return false;
    }

    private static <T> List<T> copyRows(List<T> rows) {
        return rows == null ? List.of() : List.copyOf(rows);
    }

    private static List<CounterProductSetting> counterProductSettings(List<Long> counterProductIds) {
        if (counterProductIds == null) {
            return List.of();
        }
        List<CounterProductSetting> settings = new ArrayList<>();
        for (Long productId : counterProductIds) {
            long normalizedProductId = NumberUtils.parseLong(productId);
            if (normalizedProductId > 0L) {
                settings.add(new CounterProductSetting(normalizedProductId));
            }
        }
        return List.copyOf(settings);
    }

    private static List<ClubProductSetting> clubProductSettings(List<ClubDao.ContainedClubProductRow> rows) {
        if (rows == null) {
            return List.of();
        }
        List<ClubProductSetting> settings = new ArrayList<>();
        for (ClubDao.ContainedClubProductRow row : rows) {
            settings.add(ClubProductSetting.fromRow(row));
        }
        return List.copyOf(settings);
    }

    private record CounterProductSetting(long productId) {
    }

    public record ClubProductSetting(long productId, long months, long level, int fieldCount) {
        private static ClubProductSetting fromRow(ClubDao.ContainedClubProductRow row) {
            return new ClubProductSetting(row.productId(), row.months(), row.level(), 3);
        }
    }
}
