package com.alphaseries;

import com.alphaseries.game.catalog.ProductCache;
import com.alphaseries.game.room.RoomEventLocales;
import com.alphaseries.vb.Vb;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DataManager {
    public static final String LICENCE_TIME_FORMAT = "yyyy-mm-dd_h-mm-ss";
    public static String global_008291AC = "";
    public static String global_00829050 = "";
    public static int global_00829054 = 0;
    public static final int[] global_00829068 = new int[5001];
    public static Object global_008292BC = "";
    public static String lastLicenceFailureMessage = "";
    private static LicenceHttpFetcher licenceHttpFetcher = (url, action) -> Proc_8_0_804330(url, action);

    private DataManager() {
    }

    public interface LicenceHttpFetcher {
        String read(String requestUrl, int action);
    }

    public static final class LicenceCheckContext {
        public final String productKey;
        public final String version;
        public final LocalDateTime localTime;

        public LicenceCheckContext(String productKey, String version) {
            this(productKey, version, LocalDateTime.now());
        }

        public LicenceCheckContext(String productKey, String version, LocalDateTime localTime) {
            this.productKey = Vb.cStr(productKey);
            this.version = Vb.cStr(version);
            this.localTime = localTime == null ? LocalDateTime.now() : localTime;
        }
    }

    public static void configureLicenceHttpFetcher(LicenceHttpFetcher fetcher) {
        licenceHttpFetcher = fetcher == null ? (url, action) -> Proc_8_0_804330(url, action) : fetcher;
    }

    public static RoomEventLocales roomEventLocales() {
        return RoomEventLocales.fromLegacy(global_008291AC);
    }

    public static void setRoomEventLocaleCache(String cacheText) {
        global_008291AC = cacheText == null ? "" : cacheText;
    }

    public static ProductCache productCache() {
        return ProductCache.fromLegacy(global_008292BC);
    }

    public static void setProductRows(Object productRows) {
        global_008292BC = productRows == null ? "" : productRows;
    }

    public static long Proc_8_1_804400(Object... args) {
        if (args == null || args.length == 0) {
            return 0L;
        }
        return Vb.val(args[0]) * Functions.Proc_10_4_809CA0(1, 4);
    }

    public static String Proc_8_0_804330(Object... args) {
        if (args == null || args.length == 0) {
            return "";
        }
        return PrivSockHTTP.readHTTP(Vb.cStr(args[0]), optionalColumnIndex(args, 1, 0));
    }

    public static long Proc_8_2_804490(Object... args) {
        if (args == null || args.length == 0) {
            return 0L;
        }
        return Vb.val(args[0]) * Functions.Proc_10_4_809CA0(60, 90);
    }

    public static String Proc_8_3_804530(Object... args) {
        if (args == null || args.length == 0) {
            return "";
        }
        long saltValue = Functions.Proc_10_4_809CA0(1, 100);
        if (saltValue == 0L) {
            saltValue = 1L;
        }
        long markerValue = Vb.val(Functions.Proc_10_3_809B90(0x5A, 0x41));
        return buildLicenceToken(Vb.cStr(args[0]), saltValue, markerValue, null);
    }

    public static String buildLicenceToken(String sourceValue, long saltValue, long markerValue, String fillerCharacters) {
        String source = Vb.cStr(sourceValue);
        long salt = saltValue == 0L ? 1L : saltValue;
        String fillers = Vb.cStr(fillerCharacters);
        StringBuilder token = new StringBuilder();
        token.append(source.length() + salt).append((char) markerValue);
        for (int index = 0; index < source.length(); index++) {
            char filler = index < fillers.length() ? fillers.charAt(index) : (char) markerValue;
            token.append(filler);
            token.append(source.charAt(index) * salt * markerValue);
        }
        return token.toString();
    }

    public static void Proc_8_4_804970(Object... args) {
        lastLicenceFailureMessage = "Das Lizenzsystem ist zurzeit nicht erreichbar. Versuch es sp\u00e4ter wieder!";
    }

    public static String Proc_8_5_804AB0(Object... args) {
        if (args == null || args.length == 0) {
            return "";
        }
        String encodedValue = Vb.cStr(args[0]);
        if (encodedValue.isEmpty()) {
            return "";
        }

        int shiftValue = encodedValue.charAt(0) - 87;
        StringBuilder decoded = new StringBuilder();
        for (int index = 1; index < encodedValue.length(); index++) {
            decoded.append((char) (encodedValue.charAt(index) - shiftValue));
        }
        return decoded.toString();
    }

    public static int Proc_8_6_804D80(Object... args) {
        if (args == null || args.length == 0) {
            return 0;
        }
        String keyName = Vb.cStr(args[0]);
        String marker = "\r" + keyName + ":" + global_00829054 + "=";
        if (global_00829050.contains(marker + "1")) {
            return 1;
        }
        int markerAt = global_00829050.indexOf(marker);
        if (markerAt < 0) {
            return 0;
        }
        int valueStart = markerAt + marker.length();
        int valueEnd = global_00829050.indexOf('\r', valueStart);
        String value = valueEnd >= 0 ? global_00829050.substring(valueStart, valueEnd) : global_00829050.substring(valueStart);
        return (int) Vb.val(value);
    }

    public static boolean Proc_8_7_8051C0(Object... args) {
        if (args == null || args.length == 0) {
            Proc_8_4_804970();
            return false;
        }
        if (args[0] instanceof LicenceCheckContext) {
            return checkLicence((LicenceCheckContext) args[0]);
        }
        return applyLicenceResponse(Vb.cStr(args[0]), LICENCE_TIME_FORMAT, 0L);
    }

    public static boolean checkLicence(LicenceCheckContext context) {
        try {
            long checksumSalt = licenceChecksumSalt();
            String requestUrl = buildLicenceRequestUrl(context);
            String responseText = licenceHttpFetcher.read(requestUrl, 1);
            return applyLicenceResponse(responseText, LICENCE_TIME_FORMAT, checksumSalt);
        } catch (Exception ex) {
            Proc_8_4_804970();
            return false;
        }
    }

    public static long licenceChecksumSalt() {
        return Proc_8_2_804490(7, 0x5A) + Proc_8_1_804400(0) + Proc_8_2_804490(1, 10);
    }

    public static String buildLicenceRequestUrl(LicenceCheckContext context) {
        String timeFormatText = context.localTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd_H-mm-ss"));
        String timePrefix = Functions.Proc_10_3_809B90(0x9C4, 0x3E8)
            + Functions.Proc_10_3_809B90(0x9C4, 0x3E8);
        String tokenSeed = Functions.Proc_10_3_809B90(0x9C4, 0x3E8)
            + "/" + Functions.Proc_10_3_809B90(0x9C4, 0x3E8)
            + "/" + Functions.Proc_10_3_809B90(0x9C4, 0x3E8) + "/L:";
        return "http://www.alpha-series.com/check_product_sep11?local_time="
            + timePrefix + timeFormatText + ":"
            + "&version=" + context.version
            + "&productKey=" + context.productKey
            + "&token=" + Proc_8_3_804530(tokenSeed);
    }

    public static String licenceBlockFromResponse(String responseText, String timeFormat) {
        String response = Vb.cStr(responseText);
        String marker = Vb.cStr(timeFormat);
        if (response.isEmpty()) {
            return "";
        }
        if (marker.isEmpty()) {
            return response;
        }
        String[] blocks = response.split(java.util.regex.Pattern.quote(marker), -1);
        String licenseBlock;
        if (blocks.length >= 4) {
            licenseBlock = blocks[3].replace("--*-", "\r").replace("*-*-", "\n");
        } else if (blocks.length >= 2) {
            licenseBlock = blocks[1];
        } else {
            licenseBlock = response;
        }
        return licenseBlock;
    }

    public static String licenceCacheTextFromBlock(String licenseBlock) {
        return "\r" + Vb.cStr(licenseBlock).replace('\n', '\r') + "\r";
    }

    public static String blockedLicenceMessage(String responseText) {
        String response = Vb.cStr(responseText);
        if (!response.contains("{BLOCKED ")) {
            return "";
        }
        return response.replace("%20", " ").replace("{BLOCKED ", "").replace("}", "");
    }

    public static boolean applyLicenceResponse(String responseText, String timeFormat, long checksumSalt) {
        String response = Vb.cStr(responseText);
        String blockedMessage = blockedLicenceMessage(response);
        if (!blockedMessage.isEmpty()) {
            lastLicenceFailureMessage = blockedMessage;
            return false;
        }
        if (response.isEmpty()) {
            Proc_8_4_804970();
            return false;
        }

        String licenseBlock = licenceBlockFromResponse(response, timeFormat);
        global_00829050 = licenceCacheTextFromBlock(licenseBlock);
        global_00829054 = (int) Vb.val(extractLicenceSetting(global_00829050, "rank"));
        for (int rankIndex = 1; rankIndex < global_00829068.length; rankIndex++) {
            global_00829068[rankIndex] = Proc_8_6_804D80(String.valueOf(rankIndex));
        }
        if (!licenceChecksumValid(licenseBlock, checksumSalt)) {
            Proc_8_4_804970();
            return false;
        }
        lastLicenceFailureMessage = "";
        return true;
    }

    public static boolean licenceChecksumValid(String licenseBlock, long checksumSalt) {
        String block = Vb.cStr(licenseBlock);
        String[] parts = block.split("-", -1);
        if (parts.length < 3 || block.length() < 14) {
            return true;
        }
        long licenseCheck = Vb.val(parts[2]) - Vb.val(Vb.mid(block, 9, 6)) + Vb.val(parts[1]) - checksumSalt;
        return licenseCheck == 0L;
    }

    public static boolean Proc_8_8_806720(Object... args) {
        if (args == null || args.length == 0) {
            return false;
        }
        return Files.exists(Path.of(Vb.cStr(args[0])));
    }

    public static void Proc_8_9_806810(Object... args) {
        if (args == null || args.length < 2) {
            return;
        }
        try {
            Files.writeString(Path.of(Vb.cStr(args[0])), Vb.cStr(args[1]) + System.lineSeparator(),
                StandardCharsets.UTF_8, java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
        } catch (IOException ignored) {
            // VB6 source suppresses append failures.
        }
    }

    public static void Proc_8_10_8068E0(Object... args) {
        if (args == null || args.length < 2) {
            return;
        }
        try {
            Files.writeString(Path.of(Vb.cStr(args[0])), Vb.cStr(args[1]) + System.lineSeparator(), StandardCharsets.UTF_8);
        } catch (IOException ignored) {
            // VB6 source suppresses write failures.
        }
    }

    public static String Proc_8_11_8069B0(Object... args) {
        if (args == null || args.length == 0) {
            return "";
        }
        return roomEventLocales().field(Vb.cStr(args[0]), optionalColumnIndex(args, 1, 0));
    }

    public static String Proc_8_12_806C30(Object... args) {
        if (args == null || args.length < 2) {
            return "";
        }
        return getProductCacheCell(Vb.val(args[0]), Vb.val(args[1]));
    }

    public static String extractLicenceSetting(String sourceText, String keyName) {
        String source = Vb.cStr(sourceText);
        String marker = "\r" + keyName + "=";
        int markerAt = source.indexOf(marker);
        if (markerAt < 0) {
            marker = "\r" + keyName + ":";
            markerAt = source.indexOf(marker);
        }
        if (markerAt < 0) {
            return "";
        }
        int valueStart = markerAt + marker.length();
        int valueEnd = source.indexOf('\r', valueStart);
        return valueEnd >= 0 ? source.substring(valueStart, valueEnd) : source.substring(valueStart);
    }

    public static int optionalColumnIndex(Object[] args, int argumentIndex, int defaultValue) {
        if (args != null && argumentIndex >= 0 && argumentIndex < args.length) {
            String value = Vb.cStr(args[argumentIndex]);
            if (!value.isEmpty()) {
                return (int) Vb.val(value);
            }
        }
        return defaultValue;
    }

    public static String getNullDelimitedCacheField(String cacheText, String keyName, long columnIndex) {
        if (cacheText == null || cacheText.isEmpty() || keyName == null || keyName.isEmpty()) {
            return "";
        }
        String marker = "\0" + keyName + "\1";
        int markerAt = cacheText.indexOf(marker);
        if (markerAt < 0) {
            return "";
        }
        String remainder = cacheText.substring(markerAt + marker.length());
        String row = remainder;
        int recordEnd = row.indexOf('\0');
        if (recordEnd >= 0) {
            row = row.substring(0, recordEnd);
        }
        String[] fields = row.split("\2", -1);
        if (columnIndex < 0 || columnIndex >= fields.length) {
            return "";
        }
        return fields[(int) columnIndex];
    }

    public static String getProductCacheCell(long productId, long columnIndex) {
        return productCache().cell(productId, columnIndex);
    }

    public static String getDelimitedProductRow(String tableText, long productId) {
        if (tableText == null || tableText.isEmpty()) {
            return "";
        }
        String[] rows = ("\r" + tableText + "\r").split("\r", -1);
        for (String row : rows) {
            if (!row.isEmpty()) {
                String[] columns = row.split("\t", -1);
                if (columns.length > 0 && Vb.val(columns[0]) == productId) {
                    return row;
                }
            }
        }
        return "";
    }
}
