package com.alphaseries.server.lifecycle;

import com.alphaseries.server.http.PrivSockHTTP;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.RandomUtils;
import com.alphaseries.util.StringUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class LicenceChecker {
    public static final String LICENCE_TIME_FORMAT = "yyyy-mm-dd_h-mm-ss";
    public static final String LICENCE_ENDPOINT_ENV = "ALPHASERIES_LICENCE_ENDPOINT";
    public static final String DEFAULT_LICENCE_ENDPOINT = "http://www.alpha-series.com/check_product_sep11";
    private static LicenceCheckState licenceCheckState = LicenceCheckState.empty();
    public static String lastLicenceFailureMessage = "";
    private static LicenceHttpFetcher licenceHttpFetcher = LicenceChecker::readHttp;

    private LicenceChecker() {
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
        licenceHttpFetcher = fetcher == null ? LicenceChecker::readHttp : fetcher;
    }

    public static String readHttp(String requestUrl, int action) {
        return PrivSockHTTP.readHTTP(StringUtils.text(requestUrl));
    }

    public static long randomSmallLicenceFactor(long value) {
        return value * RandomUtils.longInclusive(1, 4);
    }

    public static long randomLargeLicenceFactor(long value) {
        return value * RandomUtils.longInclusive(60, 90);
    }

    public static String randomisedLicenceToken(String sourceValue) {
        long saltValue = RandomUtils.longInclusive(1, 100);
        if (saltValue == 0L) {
            saltValue = 1L;
        }
        long markerValue = RandomUtils.longInclusive(0x5A, 0x41);
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

    public static void markLicenceUnavailable() {
        lastLicenceFailureMessage = "Das Lizenzsystem ist zurzeit nicht erreichbar. Versuch es sp\u00e4ter wieder!";
    }

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

    public static int licenceRankValue(String keyName) {
        return licenceCheckState.rankValue(keyName);
    }

    public static int licenceRank() {
        return licenceCheckState.rank();
    }

    public static int cachedLicenceRankValue(int rankIndex) {
        return licenceCheckState.cachedRankValue(rankIndex);
    }

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
        String timePrefix = String.valueOf(RandomUtils.longInclusive(0x9C4, 0x3E8))
            + RandomUtils.longInclusive(0x9C4, 0x3E8);
        String tokenSeed = RandomUtils.longInclusive(0x9C4, 0x3E8)
            + "/" + RandomUtils.longInclusive(0x9C4, 0x3E8)
            + "/" + RandomUtils.longInclusive(0x9C4, 0x3E8) + "/L:";
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
        licenceCheckState = LicenceCheckState.fromCacheText(licenceCacheTextFromBlock(licenseBlock));
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
}
