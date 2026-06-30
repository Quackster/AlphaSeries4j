package com.alphaseries.game.catalog;

import com.alphaseries.dao.mysql.ClubDao;
import com.alphaseries.dao.mysql.PackageDao;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class CatalogProductSettings {
    private final List<CounterProductSetting> counterProducts;
    private final long teleportProductId;
    private final long moodlightProductId;
    private final List<PackageDao.PackageRow> packageRows;
    private final List<PackageDao.PetPackageRow> petPackageRows;
    private final List<ClubProductSetting> clubProductRows;
    private final boolean typedClubProductRows;

    private CatalogProductSettings(
        String counterProductIds,
        long teleportProductId,
        long moodlightProductId,
        Object packageRows,
        Object petPackageRows,
        Object clubProductRows
    ) {
        this.counterProducts = parseCounterProducts(counterProductIds);
        this.teleportProductId = Math.max(0L, teleportProductId);
        this.moodlightProductId = Math.max(0L, moodlightProductId);
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
        this.counterProducts = parseCounterProducts(counterProductIds);
        this.teleportProductId = Math.max(0L, teleportProductId);
        this.moodlightProductId = Math.max(0L, moodlightProductId);
        this.packageRows = copyRows(packageRows);
        this.petPackageRows = copyRows(petPackageRows);
        this.clubProductRows = clubProductSettings(clubProductRows);
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

    public static CatalogProductSettings fromCounterProductIds(
        List<Long> counterProductIds,
        long teleportProductId,
        long moodlightProductId,
        List<PackageDao.PackageRow> packageRows,
        List<PackageDao.PetPackageRow> petPackageRows,
        List<ClubDao.ContainedClubProductRow> clubProductRows
    ) {
        return new CatalogProductSettings(counterProducts(counterProductIds), teleportProductId, moodlightProductId,
            packageRows, petPackageRows, clubProductRows);
    }

    public String counterProductIds() {
        StringBuilder joined = new StringBuilder();
        for (CounterProductSetting counterProduct : counterProducts) {
            if (joined.length() > 0) {
                joined.append('\t');
            }
            joined.append(counterProduct.serialized());
        }
        return joined.toString();
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

    public String packageRows() {
        if (!packageRows.isEmpty()) {
            StringBuilder joined = new StringBuilder();
            for (PackageDao.PackageRow row : packageRows) {
                appendRow(joined, row.productId() + "\t" + StringUtils.text(row.secondaryType()) + "\t"
                    + row.containedId() + "\t" + StringUtils.text(row.checkType()));
            }
            return joined.toString();
        }
        return "";
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
        return "";
    }

    public String clubProductRows() {
        if (typedClubProductRows) {
            StringBuilder joined = new StringBuilder("\r");
            for (ClubProductSetting row : clubProductRows) {
                joined.append(row.serialized()).append('\r');
            }
            if (joined.length() == 1) {
                joined.append('\r');
            }
            return joined.toString();
        }
        StringBuilder joined = new StringBuilder();
        for (ClubProductSetting row : clubProductRows) {
            appendRow(joined, row.serialized());
        }
        return joined.toString();
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

    private static void appendRow(StringBuilder rows, String rowText) {
        if (rows.length() > 0) {
            rows.append('\r');
        }
        rows.append(rowText);
    }

    private static List<CounterProductSetting> parseCounterProducts(String counterProductIds) {
        List<CounterProductSetting> parsedRows = new ArrayList<>();
        for (String idText : StringUtils.text(counterProductIds).split("\t", -1)) {
            if (!idText.isEmpty()) {
                parsedRows.add(CounterProductSetting.fromLegacy(idText));
            }
        }
        return List.copyOf(parsedRows);
    }

    private static String counterProducts(List<Long> counterProductIds) {
        StringBuilder joined = new StringBuilder();
        if (counterProductIds != null) {
            for (Long productId : counterProductIds) {
                if (productId != null) {
                    if (joined.length() > 0) {
                        joined.append('\t');
                    }
                    joined.append(productId);
                }
            }
        }
        return joined.toString();
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
        List<PackageDao.PackageRow> parsedRows = new ArrayList<>();
        for (String row : StringUtils.text(packageRows).split("\r", -1)) {
            if (!row.isEmpty()) {
                String[] fields = row.split("\t", -1);
                parsedRows.add(new PackageDao.PackageRow(
                    NumberUtils.parseLong(StringUtils.field(fields, 0)),
                    StringUtils.field(fields, 1),
                    NumberUtils.parseLong(StringUtils.field(fields, 2)),
                    StringUtils.field(fields, 3)));
            }
        }
        if (!parsedRows.isEmpty()) {
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
        List<PackageDao.PetPackageRow> parsedRows = new ArrayList<>();
        for (String row : StringUtils.text(petPackageRows).split("\r", -1)) {
            if (!row.isEmpty()) {
                String[] fields = row.split("\t", -1);
                parsedRows.add(new PackageDao.PetPackageRow(
                    NumberUtils.parseLong(StringUtils.field(fields, 0)),
                    NumberUtils.parseLong(StringUtils.field(fields, 1)),
                    NumberUtils.parseLong(StringUtils.field(fields, 2)),
                    StringUtils.field(fields, 3)));
            }
        }
        if (!parsedRows.isEmpty()) {
            return List.copyOf(parsedRows);
        }
        return List.of();
    }

    private static List<ClubProductSetting> parseClubProductRows(Object clubProductRows) {
        if (clubProductRows instanceof List<?> rows) {
            List<ClubProductSetting> parsedRows = new ArrayList<>();
            for (Object value : rows) {
                if (value instanceof ClubDao.ContainedClubProductRow row) {
                    parsedRows.add(ClubProductSetting.fromRow(row));
                }
            }
            return List.copyOf(parsedRows);
        }
        List<ClubProductSetting> parsedRows = new ArrayList<>();
        for (String row : StringUtils.text(clubProductRows).split("\r", -1)) {
            if (!row.isEmpty()) {
                parsedRows.add(ClubProductSetting.fromLegacy(row));
            }
        }
        if (!parsedRows.isEmpty()) {
            return List.copyOf(parsedRows);
        }
        return List.of();
    }

    private static <T> List<T> copyRows(List<T> rows) {
        return rows == null ? List.of() : List.copyOf(rows);
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

    private record CounterProductSetting(long productId, String serialized) {
        private CounterProductSetting {
            serialized = StringUtils.text(serialized);
        }

        private static CounterProductSetting fromLegacy(String idText) {
            return new CounterProductSetting(NumberUtils.parseLong(idText), StringUtils.text(idText));
        }
    }

    private record ClubProductSetting(long productId, long months, long level, int fieldCount) {
        private static ClubProductSetting fromRow(ClubDao.ContainedClubProductRow row) {
            return new ClubProductSetting(row.productId(), row.months(), row.level(), 3);
        }

        private static ClubProductSetting fromLegacy(String row) {
            String[] fields = StringUtils.text(row).split("\t", -1);
            return new ClubProductSetting(
                NumberUtils.parseLong(StringUtils.field(fields, 0)),
                NumberUtils.parseLong(StringUtils.field(fields, 1)),
                NumberUtils.parseLong(StringUtils.field(fields, 2)),
                fields.length);
        }

        private String serialized() {
            if (fieldCount <= 1) {
                return String.valueOf(productId);
            }
            if (fieldCount == 2) {
                return productId + "\t" + months;
            }
            return productId + "\t" + months + "\t" + level;
        }
    }
}
