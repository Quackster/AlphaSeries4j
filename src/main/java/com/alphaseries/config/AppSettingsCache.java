package com.alphaseries.config;

import java.util.LinkedHashMap;
import java.util.Map;

import com.alphaseries.util.StringUtils;

public final class AppSettingsCache {
    private final Map<String, String> settings;

    private AppSettingsCache(Map<String, String> settings) {
        this.settings = new LinkedHashMap<>(settings);
    }

    public static AppSettingsCache empty() {
        return fromSettings(Map.of());
    }

    public static AppSettingsCache fromSettings(Map<String, String> settings) {
        Map<String, String> normalizedSettings = new LinkedHashMap<>();
        if (settings != null) {
            for (Map.Entry<String, String> entry : settings.entrySet()) {
                putSetting(normalizedSettings, entry.getKey(), entry.getValue());
            }
        }
        return new AppSettingsCache(normalizedSettings);
    }

    public String value(String keyName) {
        String key = StringUtils.text(keyName);
        if (key.isEmpty()) {
            return "";
        }
        return settings.getOrDefault(normalizedKey(key), "");
    }

    private static void putSetting(Map<String, String> settings, String keyName, String value) {
        String key = normalizedKey(StringUtils.text(keyName).trim());
        if (!key.isEmpty()) {
            settings.putIfAbsent(key, StringUtils.text(value));
        }
    }

    private static String normalizedKey(String keyName) {
        return StringUtils.text(keyName).toLowerCase();
    }

    public String valueOrDefault(String keyName, String defaultValue) {
        String settingValue = value(keyName);
        return settingValue.isEmpty() ? StringUtils.text(defaultValue) : settingValue;
    }

    public long longValueOrDefault(String keyName, long defaultValue) {
        String settingValue = value(keyName);
        return settingValue.isEmpty() ? defaultValue : com.alphaseries.util.NumberUtils.parseLong(settingValue);
    }
}
