package com.alphaseries.server.lifecycle;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class LicenceCheckState {
    private static final int RANK_CACHE_SIZE = 5001;

    private final int rank;
    private final Map<String, Integer> rankValuesByKey;
    private final int[] rankValues;

    private LicenceCheckState(int rank, Map<String, Integer> rankValuesByKey, int[] rankValues) {
        this.rank = rank;
        this.rankValuesByKey = rankValuesByKey == null ? Map.of() : Map.copyOf(rankValuesByKey);
        this.rankValues = rankValues == null ? new int[RANK_CACHE_SIZE] : rankValues.clone();
    }

    static LicenceCheckState fromCacheText(String cacheText) {
        Map<String, String> settings = parseSettings(cacheText);
        int parsedRank = NumberUtils.parseInt(settings.get("rank"));
        Map<String, Integer> parsedRankValuesByKey = parseRankValues(cacheText, parsedRank);
        return fromRankValues(parsedRank, parsedRankValuesByKey);
    }

    public static LicenceCheckState fromRankValues(int rank, Map<String, Integer> rankValuesByKey) {
        Map<String, Integer> parsedRankValuesByKey = rankValuesByKey == null ? Map.of() : Map.copyOf(rankValuesByKey);
        int[] parsedRankValues = new int[RANK_CACHE_SIZE];
        for (int rankIndex = 1; rankIndex < parsedRankValues.length; rankIndex++) {
            parsedRankValues[rankIndex] = parsedRankValuesByKey.getOrDefault(String.valueOf(rankIndex), 0);
        }
        return new LicenceCheckState(rank, parsedRankValuesByKey, parsedRankValues);
    }

    public static LicenceCheckState empty() {
        return new LicenceCheckState(0, Map.of(), new int[RANK_CACHE_SIZE]);
    }

    public int rankValue(String keyName) {
        return rankValuesByKey.getOrDefault(StringUtils.text(keyName), 0);
    }

    public int rank() {
        return rank;
    }

    public int cachedRankValue(int rankIndex) {
        return rankIndex >= 0 && rankIndex < rankValues.length ? rankValues[rankIndex] : 0;
    }

    private static Map<String, String> parseSettings(String cacheText) {
        Map<String, String> settings = new LinkedHashMap<>();
        for (String entry : entries(cacheText)) {
            int separator = settingSeparator(entry);
            if (separator <= 0) {
                continue;
            }
            String key = StringUtils.text(entry.substring(0, separator));
            if (!key.contains(":")) {
                settings.put(key, StringUtils.text(entry.substring(separator + 1)));
            }
        }
        return settings;
    }

    private static Map<String, Integer> parseRankValues(String cacheText, int rank) {
        Map<String, Integer> values = new LinkedHashMap<>();
        String rankSuffix = ":" + rank;
        for (String entry : entries(cacheText)) {
            int equalsAt = entry.indexOf('=');
            if (equalsAt <= 0) {
                continue;
            }
            String key = entry.substring(0, equalsAt);
            if (key.endsWith(rankSuffix)) {
                values.put(key.substring(0, key.length() - rankSuffix.length()),
                    rankValueFromEntry(entry.substring(equalsAt + 1)));
            }
        }
        return values;
    }

    private static List<String> entries(String cacheText) {
        return StringUtils.delimitedFields(StringUtils.newlinesAsCarriageReturns(cacheText), '\r');
    }

    private static int settingSeparator(String entry) {
        int equalsAt = entry.indexOf('=');
        if (equalsAt >= 0) {
            return equalsAt;
        }
        return entry.indexOf(':');
    }

    private static int rankValueFromEntry(String valueText) {
        String value = StringUtils.text(valueText);
        return value.startsWith("1") ? 1 : NumberUtils.parseInt(value);
    }
}
