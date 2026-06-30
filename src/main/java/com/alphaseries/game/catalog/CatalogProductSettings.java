package com.alphaseries.game.catalog;

import com.alphaseries.dao.mysql.PackageDao;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.List;

public final class CatalogProductSettings {
    private final String counterProductIds;
    private final long teleportProductId;
    private final long moodlightProductId;
    private final Object packageRows;
    private final Object petPackageRows;
    private final String clubProductRows;

    private CatalogProductSettings(
        String counterProductIds,
        long teleportProductId,
        long moodlightProductId,
        Object packageRows,
        Object petPackageRows,
        String clubProductRows
    ) {
        this.counterProductIds = StringUtils.text(counterProductIds);
        this.teleportProductId = Math.max(0L, teleportProductId);
        this.moodlightProductId = Math.max(0L, moodlightProductId);
        this.packageRows = packageRows == null ? "" : packageRows;
        this.petPackageRows = petPackageRows == null ? "" : petPackageRows;
        this.clubProductRows = StringUtils.text(clubProductRows);
    }

    public static CatalogProductSettings fromLegacy(
        String counterProductIds,
        long teleportProductId,
        long moodlightProductId,
        Object packageRows,
        Object petPackageRows,
        String clubProductRows
    ) {
        return new CatalogProductSettings(counterProductIds, teleportProductId, moodlightProductId,
            packageRows, petPackageRows, clubProductRows);
    }

    public String counterProductIds() {
        return counterProductIds;
    }

    public long teleportProductId() {
        return teleportProductId;
    }

    public long moodlightProductId() {
        return moodlightProductId;
    }

    public String packageRows() {
        if (packageRows instanceof List<?> rows) {
            StringBuilder joined = new StringBuilder();
            for (Object value : rows) {
                if (value instanceof PackageDao.PackageRow row) {
                    appendRow(joined, row.productId() + "\t" + StringUtils.text(row.secondaryType()) + "\t"
                        + row.containedId() + "\t" + StringUtils.text(row.checkType()));
                }
            }
            return joined.toString();
        }
        return StringUtils.text(packageRows);
    }

    public String petPackageRows() {
        if (petPackageRows instanceof List<?> rows) {
            StringBuilder joined = new StringBuilder();
            for (Object value : rows) {
                if (value instanceof PackageDao.PetPackageRow row) {
                    appendRow(joined, row.packageId() + "\t" + row.petType() + "\t" + row.race() + "\t"
                        + StringUtils.text(row.color()));
                }
            }
            return joined.toString();
        }
        return StringUtils.text(petPackageRows);
    }

    public String clubProductRows() {
        return clubProductRows;
    }

    public boolean containsClubProduct(long productId) {
        return productId > 0L && clubProductRows.contains("\r" + productId + "\r");
    }

    public boolean containsCounterProduct(long productId) {
        String wanted = String.valueOf(NumberUtils.parseLong(productId));
        for (String idText : counterProductIds.split("\t", -1)) {
            if (wanted.equals(String.valueOf(NumberUtils.parseLong(idText)))) {
                return true;
            }
        }
        return false;
    }

    private static void appendRow(StringBuilder rows, String rowText) {
        if (rows.length() > 0) {
            rows.append('\r');
        }
        rows.append(rowText);
    }
}
