package com.alphaseries.config;

import com.alphaseries.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

public final class AppSettingsCache {
    private final Map<String, String> settings;

    private AppSettingsCache(Map<String, String> settings) {
        this.settings = new LinkedHashMap<>(settings);
    }

    public static AppSettingsCache fromLegacy(String settingsText) {
        return new AppSettingsCache(parseSettings(settingsText));
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

    private static Map<String, String> parseSettings(String settingsText) {
        Map<String, String> parsedSettings = new LinkedHashMap<>();
        String text = StringUtils.text(settingsText);
        int markerAt = text.indexOf('[');
        while (markerAt >= 0) {
            int valueStart = text.indexOf('=', markerAt + 1);
            int markerEnd = text.indexOf(']', markerAt + 1);
            if (valueStart > markerAt && (markerEnd < 0 || valueStart < markerEnd)) {
                String key = text.substring(markerAt + 1, valueStart);
                String value = markerEnd < 0 ? text.substring(valueStart + 1) : text.substring(valueStart + 1, markerEnd);
                putSetting(parsedSettings, key, value);
            }
            markerAt = markerEnd < 0 ? -1 : text.indexOf('[', markerEnd + 1);
        }

        String normalizedText = text.replace("\r\n", "\n").replace('\r', '\n').replace(']', '\n');
        for (String settingLine : normalizedText.split("\n", -1)) {
            String currentLine = settingLine.trim();
            if (currentLine.startsWith("[")) {
                currentLine = currentLine.substring(1);
            }
            int equalsAt = currentLine.indexOf('=');
            if (equalsAt > 0) {
                putSetting(parsedSettings, currentLine.substring(0, equalsAt).trim(), currentLine.substring(equalsAt + 1));
            }
        }
        return parsedSettings;
    }

    private static void putSetting(Map<String, String> settings, Object keyName, Object value) {
        String key = normalizedKey(StringUtils.text(keyName).trim());
        if (!key.isEmpty()) {
            settings.putIfAbsent(key, StringUtils.text(value));
        }
    }

    private static String normalizedKey(String keyName) {
        return StringUtils.text(keyName).toLowerCase();
    }

    public String valueOrDefault(String keyName, Object defaultValue) {
        String settingValue = value(keyName);
        return settingValue.isEmpty() ? StringUtils.text(defaultValue) : settingValue;
    }
}
