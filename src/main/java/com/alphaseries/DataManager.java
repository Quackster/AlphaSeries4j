package com.alphaseries;

import com.alphaseries.game.catalog.CatalogState;
import com.alphaseries.game.catalog.ProductCache;
import com.alphaseries.game.room.RoomEventLocales;
import com.alphaseries.game.room.RoomState;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DataManager {
    public static final String LICENCE_TIME_FORMAT = "yyyy-mm-dd_h-mm-ss";
    public static final String LICENCE_ENDPOINT_ENV = "ALPHASERIES_LICENCE_ENDPOINT";
    public static final String DEFAULT_LICENCE_ENDPOINT = "http://www.alpha-series.com/check_product_sep11";
    public static Object global_008291AC = "";
    public static String global_00829050 = "";
    public static int global_00829054 = 0;
    public static final int[] global_00829068 = new int[5001];
    public static Object global_008292BC = "";
    public static String lastLicenceFailureMessage = "";
    private static LicenceHttpFetcher licenceHttpFetcher = DataManager::readHttp;

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
            this.productKey = StringUtils.text(productKey);
            this.version = StringUtils.text(version);
            this.localTime = localTime == null ? LocalDateTime.now() : localTime;
        }
    }

    public static void configureLicenceHttpFetcher(LicenceHttpFetcher fetcher) {
        licenceHttpFetcher = fetcher == null ? DataManager::readHttp : fetcher;
    }

    public static RoomEventLocales roomEventLocales() {
        RoomState.instance().setEventLocalesFromLegacy(global_008291AC);
        return RoomState.instance().eventLocales();
    }

    public static void setRoomEventLocaleCache(String cacheText) {
        global_008291AC = cacheText == null ? "" : cacheText;
        RoomState.instance().setEventLocalesFromLegacy(global_008291AC);
    }

    public static void setRoomEventLocales(RoomEventLocales eventLocales) {
        RoomState.instance().setEventLocales(eventLocales);
        global_008291AC = RoomState.instance().eventLocales();
    }

    public static ProductCache productCache() {
        CatalogState.instance().setProductCacheFromLegacy(global_008292BC);
        return CatalogState.instance().productCache();
    }

    public static void setProductRows(Object productRows) {
        global_008292BC = productRows == null ? "" : productRows;
        CatalogState.instance().setProductCacheFromLegacy(global_008292BC);
    }

    /**
     * Original function: Proc_8_1_804400.
     */
    public static long Proc_8_1_804400(Object... args) {
        if (args == null || args.length == 0) {
            return 0L;
        }
        return randomSmallLicenceFactor(NumberUtils.parseLong(args[0]));
    }

    /**
     * Original function: Proc_8_0_804330.
     */
    public static String Proc_8_0_804330(Object... args) {
        if (args == null || args.length == 0) {
            return "";
        }
        return readHttp(StringUtils.text(args[0]), optionalColumnIndex(args, 1, 0));
    }

    /**
     * Original function: Proc_8_2_804490.
     */
    public static long Proc_8_2_804490(Object... args) {
        if (args == null || args.length == 0) {
            return 0L;
        }
        return randomLargeLicenceFactor(NumberUtils.parseLong(args[0]));
    }

    /**
     * Original function: Proc_8_3_804530.
     */
    public static String Proc_8_3_804530(Object... args) {
        if (args == null || args.length == 0) {
            return "";
        }
        return randomisedLicenceToken(StringUtils.text(args[0]));
    }

    /**
     * Original function: Proc_8_0_804330.
     */
    public static String readHttp(String requestUrl, int action) {
        return PrivSockHTTP.readHTTP(StringUtils.text(requestUrl), action);
    }

    /**
     * Original function: Proc_8_1_804400.
     */
    public static long randomSmallLicenceFactor(long value) {
        return value * Functions.randomLongInclusive(1, 4);
    }

    /**
     * Original function: Proc_8_2_804490.
     */
    public static long randomLargeLicenceFactor(long value) {
        return value * Functions.randomLongInclusive(60, 90);
    }

    /**
     * Original function: Proc_8_3_804530.
     */
    public static String randomisedLicenceToken(String sourceValue) {
        long saltValue = Functions.randomLongInclusive(1, 100);
        if (saltValue == 0L) {
            saltValue = 1L;
        }
        long markerValue = Functions.randomLongInclusive(0x5A, 0x41);
        return buildLicenceToken(sourceValue, saltValue, markerValue, null);
    }

    public static String buildLicenceToken(String sourceValue, long saltValue, long markerValue, String fillerCharacters) {
        String source = StringUtils.text(sourceValue);
        long salt = saltValue == 0L ? 1L : saltValue;
        String fillers = StringUtils.text(fillerCharacters);
        StringBuilder token = new StringBuilder();
        token.append(source.length() + salt).append((char) markerValue);
        for (int index = 0; index < source.length(); index++) {
            char filler = index < fillers.length() ? fillers.charAt(index) : (char) markerValue;
            token.append(filler);
            token.append(source.charAt(index) * salt * markerValue);
        }
        return token.toString();
    }

    /**
     * Original function: Proc_8_4_804970.
     */
    public static void Proc_8_4_804970(Object... args) {
        markLicenceUnavailable();
    }

    /**
     * Original function: Proc_8_5_804AB0.
     */
    public static String Proc_8_5_804AB0(Object... args) {
        if (args == null || args.length == 0) {
            return "";
        }
        return decodeShiftedLicenceText(StringUtils.text(args[0]));
    }

    /**
     * Original function: Proc_8_4_804970.
     */
    public static void markLicenceUnavailable() {
        lastLicenceFailureMessage = "Das Lizenzsystem ist zurzeit nicht erreichbar. Versuch es sp\u00e4ter wieder!";
    }

    /**
     * Original function: Proc_8_5_804AB0.
     */
    public static String decodeShiftedLicenceText(String encodedValue) {
        String encodedText = StringUtils.text(encodedValue);
        if (encodedText.isEmpty()) {
            return "";
        }

        int shiftValue = encodedText.charAt(0) - 87;
        StringBuilder decoded = new StringBuilder();
        for (int index = 1; index < encodedText.length(); index++) {
            decoded.append((char) (encodedText.charAt(index) - shiftValue));
        }
        return decoded.toString();
    }

    /**
     * Original function: Proc_8_6_804D80.
     */
    public static int Proc_8_6_804D80(Object... args) {
        if (args == null || args.length == 0) {
            return 0;
        }
        return licenceRankValue(StringUtils.text(args[0]));
    }

    /**
     * Original function: Proc_8_6_804D80.
     */
    public static int licenceRankValue(String keyName) {
        String keyText = StringUtils.text(keyName);
        String marker = "\r" + keyText + ":" + global_00829054 + "=";
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
        return NumberUtils.parseInt(value);
    }

    /**
     * Original function: Proc_8_7_8051C0.
     */
    public static boolean Proc_8_7_8051C0(Object... args) {
        if (args == null || args.length == 0) {
            markLicenceUnavailable();
            return false;
        }
        if (args[0] instanceof LicenceCheckContext) {
            return checkLicence((LicenceCheckContext) args[0]);
        }
        return applyLicenceResponse(StringUtils.text(args[0]), LICENCE_TIME_FORMAT, 0L);
    }

    /**
     * Original function: Proc_8_7_8051C0.
     */
    public static boolean checkLicence(LicenceCheckContext context) {
        try {
            long checksumSalt = licenceChecksumSalt();
            String requestUrl = buildLicenceRequestUrl(context);
            String responseText = licenceHttpFetcher.read(requestUrl, 1);
            return applyLicenceResponse(responseText, LICENCE_TIME_FORMAT, checksumSalt);
        } catch (Exception ex) {
            markLicenceUnavailable();
            return false;
        }
    }

    public static long licenceChecksumSalt() {
        return randomLargeLicenceFactor(7) + randomSmallLicenceFactor(0) + randomLargeLicenceFactor(1);
    }

    public static String buildLicenceRequestUrl(LicenceCheckContext context) {
        return buildLicenceRequestUrl(context, licenceEndpointFromEnvironment(System.getenv()));
    }

    public static String licenceEndpointFromEnvironment(java.util.Map<String, String> environment) {
        String endpoint = environment == null ? "" : StringUtils.text(environment.get(LICENCE_ENDPOINT_ENV));
        return endpoint.isEmpty() ? DEFAULT_LICENCE_ENDPOINT : endpoint;
    }

    public static String buildLicenceRequestUrl(LicenceCheckContext context, String endpoint) {
        String timeFormatText = context.localTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd_H-mm-ss"));
        String timePrefix = String.valueOf(Functions.randomLongInclusive(0x9C4, 0x3E8))
            + Functions.randomLongInclusive(0x9C4, 0x3E8);
        String tokenSeed = Functions.randomLongInclusive(0x9C4, 0x3E8)
            + "/" + Functions.randomLongInclusive(0x9C4, 0x3E8)
            + "/" + Functions.randomLongInclusive(0x9C4, 0x3E8) + "/L:";
        return StringUtils.text(endpoint) + "?local_time="
            + urlEncode(timePrefix + timeFormatText + ":")
            + "&version=" + urlEncode(context.version)
            + "&productKey=" + urlEncode(context.productKey)
            + "&token=" + urlEncode(randomisedLicenceToken(tokenSeed));
    }

    public static String urlEncode(String value) {
        return URLEncoder.encode(StringUtils.text(value), StandardCharsets.UTF_8);
    }

    public static String licenceBlockFromResponse(String responseText, String timeFormat) {
        String response = StringUtils.text(responseText);
        String marker = StringUtils.text(timeFormat);
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
        return "\r" + StringUtils.text(licenseBlock).replace('\n', '\r') + "\r";
    }

    public static String blockedLicenceMessage(String responseText) {
        String response = StringUtils.text(responseText);
        if (!response.contains("{BLOCKED ")) {
            return "";
        }
        return response.replace("%20", " ").replace("{BLOCKED ", "").replace("}", "");
    }

    public static boolean applyLicenceResponse(String responseText, String timeFormat, long checksumSalt) {
        String response = StringUtils.text(responseText);
        String blockedMessage = blockedLicenceMessage(response);
        if (!blockedMessage.isEmpty()) {
            lastLicenceFailureMessage = blockedMessage;
            return false;
        }
        if (response.isEmpty()) {
            markLicenceUnavailable();
            return false;
        }

        String licenseBlock = licenceBlockFromResponse(response, timeFormat);
        global_00829050 = licenceCacheTextFromBlock(licenseBlock);
        global_00829054 = NumberUtils.parseInt(extractLicenceSetting(global_00829050, "rank"));
        for (int rankIndex = 1; rankIndex < global_00829068.length; rankIndex++) {
            global_00829068[rankIndex] = licenceRankValue(String.valueOf(rankIndex));
        }
        if (!licenceChecksumValid(licenseBlock, checksumSalt)) {
            markLicenceUnavailable();
            return false;
        }
        lastLicenceFailureMessage = "";
        return true;
    }

    public static boolean licenceChecksumValid(String licenseBlock, long checksumSalt) {
        String block = StringUtils.text(licenseBlock);
        String[] parts = block.split("-", -1);
        if (parts.length < 3 || block.length() < 14) {
            return true;
        }
        long licenseCheck = NumberUtils.parseLong(parts[2])
            - NumberUtils.parseLong(StringUtils.mid(block, 9, 6))
            + NumberUtils.parseLong(parts[1])
            - checksumSalt;
        return licenseCheck == 0L;
    }

    /**
     * Original function: Proc_8_8_806720.
     */
    public static boolean Proc_8_8_806720(Object... args) {
        if (args == null || args.length == 0) {
            return false;
        }
        return fileExists(StringUtils.text(args[0]));
    }

    /**
     * Original function: Proc_8_8_806720.
     */
    public static boolean fileExists(String path) {
        return Files.exists(Path.of(StringUtils.text(path)));
    }

    /**
     * Original function: Proc_8_9_806810.
     */
    public static void Proc_8_9_806810(Object... args) {
        if (args == null || args.length < 2) {
            return;
        }
        appendTextFile(StringUtils.text(args[0]), StringUtils.text(args[1]));
    }

    /**
     * Original function: Proc_8_9_806810.
     */
    public static void appendTextFile(String path, String text) {
        try {
            Files.writeString(Path.of(StringUtils.text(path)), StringUtils.text(text) + System.lineSeparator(),
                StandardCharsets.UTF_8, java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
        } catch (IOException ignored) {
            // VB6 source suppresses append failures.
        }
    }

    /**
     * Original function: Proc_8_10_8068E0.
     */
    public static void Proc_8_10_8068E0(Object... args) {
        if (args == null || args.length < 2) {
            return;
        }
        writeTextFile(StringUtils.text(args[0]), StringUtils.text(args[1]));
    }

    /**
     * Original function: Proc_8_10_8068E0.
     */
    public static void writeTextFile(String path, String text) {
        try {
            Files.writeString(Path.of(StringUtils.text(path)), StringUtils.text(text) + System.lineSeparator(), StandardCharsets.UTF_8);
        } catch (IOException ignored) {
            // VB6 source suppresses write failures.
        }
    }

    /**
     * Original function: Proc_8_11_8069B0.
     */
    public static String Proc_8_11_8069B0(Object... args) {
        if (args == null || args.length == 0) {
            return "";
        }
        return roomEventLocaleField(StringUtils.text(args[0]), optionalColumnIndex(args, 1, 0));
    }

    /**
     * Original function: Proc_8_12_806C30.
     */
    public static String Proc_8_12_806C30(Object... args) {
        if (args == null || args.length < 2) {
            return "";
        }
        return productCacheField(NumberUtils.parseLong(args[0]), NumberUtils.parseLong(args[1]));
    }

    /**
     * Original function: Proc_8_11_8069B0.
     */
    public static String roomEventLocaleField(String keyName, long columnIndex) {
        return roomEventLocales().field(StringUtils.text(keyName), columnIndex);
    }

    /**
     * Original function: Proc_8_12_806C30.
     */
    public static String productCacheField(long productId, long columnIndex) {
        return productCache().cell(productId, columnIndex);
    }

    public static String extractLicenceSetting(String sourceText, String keyName) {
        String source = StringUtils.text(sourceText);
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
            String value = StringUtils.text(args[argumentIndex]);
            if (!value.isEmpty()) {
                return NumberUtils.parseInt(value);
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

    public static String getDelimitedProductRow(String tableText, long productId) {
        if (tableText == null || tableText.isEmpty()) {
            return "";
        }
        String[] rows = ("\r" + tableText + "\r").split("\r", -1);
        for (String row : rows) {
            if (!row.isEmpty()) {
                String[] columns = row.split("\t", -1);
                if (columns.length > 0 && NumberUtils.parseLong(columns[0]) == productId) {
                    return row;
                }
            }
        }
        return "";
    }
}
