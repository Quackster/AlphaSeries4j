package com.alphaseries.game.catalog;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

public final class CatalogProductSettings {
    private final String counterProductIds;
    private final long teleportProductId;
    private final long moodlightProductId;
    private final String packageRows;
    private final String petPackageRows;
    private final String clubProductRows;

    private CatalogProductSettings(
        String counterProductIds,
        long teleportProductId,
        long moodlightProductId,
        String packageRows,
        String petPackageRows,
        String clubProductRows
    ) {
        this.counterProductIds = StringUtils.text(counterProductIds);
        this.teleportProductId = Math.max(0L, teleportProductId);
        this.moodlightProductId = Math.max(0L, moodlightProductId);
        this.packageRows = StringUtils.text(packageRows);
        this.petPackageRows = StringUtils.text(petPackageRows);
        this.clubProductRows = StringUtils.text(clubProductRows);
    }

    public static CatalogProductSettings fromLegacy(
        String counterProductIds,
        long teleportProductId,
        long moodlightProductId,
        String packageRows,
        String petPackageRows,
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
        return packageRows;
    }

    public String petPackageRows() {
        return petPackageRows;
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
}
