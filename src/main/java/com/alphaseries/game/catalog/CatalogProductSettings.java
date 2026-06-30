package com.alphaseries.game.catalog;

import com.alphaseries.dao.mysql.ClubDao;
import com.alphaseries.dao.mysql.PackageDao;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class CatalogProductSettings {
    private final String counterProductIds;
    private final long teleportProductId;
    private final long moodlightProductId;
    private final String legacyPackageRows;
    private final String legacyPetPackageRows;
    private final String legacyClubProductRows;
    private final List<PackageDao.PackageRow> packageRows;
    private final List<PackageDao.PetPackageRow> petPackageRows;
    private final List<ClubDao.ContainedClubProductRow> clubProductRows;
    private final boolean typedClubProductRows;

    private CatalogProductSettings(
        String counterProductIds,
        long teleportProductId,
        long moodlightProductId,
        Object packageRows,
        Object petPackageRows,
        Object clubProductRows
    ) {
        this.counterProductIds = StringUtils.text(counterProductIds);
        this.teleportProductId = Math.max(0L, teleportProductId);
        this.moodlightProductId = Math.max(0L, moodlightProductId);
        this.legacyPackageRows = packageRows instanceof List<?> ? "" : StringUtils.text(packageRows);
        this.legacyPetPackageRows = petPackageRows instanceof List<?> ? "" : StringUtils.text(petPackageRows);
        this.legacyClubProductRows = clubProductRows instanceof List<?> ? "" : StringUtils.text(clubProductRows);
        this.packageRows = parsePackageRows(packageRows);
        this.petPackageRows = parsePetPackageRows(petPackageRows);
        this.clubProductRows = parseClubProductRows(clubProductRows);
        this.typedClubProductRows = clubProductRows instanceof List<?>;
    }

    private CatalogProductSettings(
        String counterProductIds,
        long teleportProductId,
        long moodlightProductId,
        List<PackageDao.PackageRow> packageRows,
        List<PackageDao.PetPackageRow> petPackageRows,
        List<ClubDao.ContainedClubProductRow> clubProductRows
    ) {
        this.counterProductIds = StringUtils.text(counterProductIds);
        this.teleportProductId = Math.max(0L, teleportProductId);
        this.moodlightProductId = Math.max(0L, moodlightProductId);
        this.legacyPackageRows = "";
        this.legacyPetPackageRows = "";
        this.legacyClubProductRows = "";
        this.packageRows = copyRows(packageRows);
        this.petPackageRows = copyRows(petPackageRows);
        this.clubProductRows = copyRows(clubProductRows);
        this.typedClubProductRows = true;
    }

    public static CatalogProductSettings fromLegacy(
        String counterProductIds,
        long teleportProductId,
        long moodlightProductId,
        Object packageRows,
        Object petPackageRows,
        Object clubProductRows
    ) {
        return new CatalogProductSettings(counterProductIds, teleportProductId, moodlightProductId,
            packageRows, petPackageRows, clubProductRows);
    }

    public static CatalogProductSettings empty() {
        return new CatalogProductSettings("", 0L, 0L, "", "", "");
    }

    public static CatalogProductSettings fromRows(
        String counterProductIds,
        long teleportProductId,
        long moodlightProductId,
        List<PackageDao.PackageRow> packageRows,
        List<PackageDao.PetPackageRow> petPackageRows,
        List<ClubDao.ContainedClubProductRow> clubProductRows
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
        if (!packageRows.isEmpty()) {
            StringBuilder joined = new StringBuilder();
            for (PackageDao.PackageRow row : packageRows) {
                appendRow(joined, row.productId() + "\t" + StringUtils.text(row.secondaryType()) + "\t"
                    + row.containedId() + "\t" + StringUtils.text(row.checkType()));
            }
            return joined.toString();
        }
        return legacyPackageRows;
    }

    public String petPackageRows() {
        if (!petPackageRows.isEmpty()) {
            StringBuilder joined = new StringBuilder();
            for (PackageDao.PetPackageRow row : petPackageRows) {
                appendRow(joined, row.packageId() + "\t" + row.petType() + "\t" + row.race() + "\t"
                    + StringUtils.text(row.color()));
            }
            return joined.toString();
        }
        return legacyPetPackageRows;
    }

    public String clubProductRows() {
        if (typedClubProductRows) {
            StringBuilder joined = new StringBuilder("\r");
            for (ClubDao.ContainedClubProductRow row : clubProductRows) {
                joined.append(row.productId()).append('\t').append(row.months()).append('\t').append(row.level()).append('\r');
            }
            if (joined.length() == 1) {
                joined.append('\r');
            }
            return joined.toString();
        }
        return legacyClubProductRows;
    }

    public boolean containsClubProduct(long productId) {
        if (productId <= 0L) {
            return false;
        }
        if (!clubProductRows.isEmpty()) {
            for (ClubDao.ContainedClubProductRow row : clubProductRows) {
                if (row.productId() == productId) {
                    return true;
                }
            }
            return false;
        }
        return legacyClubProductRows.contains("\r" + productId + "\r");
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

    private static List<PackageDao.PackageRow> parsePackageRows(Object packageRows) {
        if (packageRows instanceof List<?> rows) {
            List<PackageDao.PackageRow> parsedRows = new ArrayList<>();
            for (Object value : rows) {
                if (value instanceof PackageDao.PackageRow row) {
                    parsedRows.add(row);
                }
            }
            return List.copyOf(parsedRows);
        }
        return List.of();
    }

    private static List<PackageDao.PetPackageRow> parsePetPackageRows(Object petPackageRows) {
        if (petPackageRows instanceof List<?> rows) {
            List<PackageDao.PetPackageRow> parsedRows = new ArrayList<>();
            for (Object value : rows) {
                if (value instanceof PackageDao.PetPackageRow row) {
                    parsedRows.add(row);
                }
            }
            return List.copyOf(parsedRows);
        }
        return List.of();
    }

    private static List<ClubDao.ContainedClubProductRow> parseClubProductRows(Object clubProductRows) {
        if (clubProductRows instanceof List<?> rows) {
            List<ClubDao.ContainedClubProductRow> parsedRows = new ArrayList<>();
            for (Object value : rows) {
                if (value instanceof ClubDao.ContainedClubProductRow row) {
                    parsedRows.add(row);
                }
            }
            return List.copyOf(parsedRows);
        }
        return List.of();
    }

    private static <T> List<T> copyRows(List<T> rows) {
        return rows == null ? List.of() : List.copyOf(rows);
    }
}
