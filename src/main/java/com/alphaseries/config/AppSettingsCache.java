package com.alphaseries.config;

import com.alphaseries.util.StringUtils;

public final class AppSettingsCache {
    private final String settingsText;

    private AppSettingsCache(String settingsText) {
        this.settingsText = StringUtils.text(settingsText);
    }

    public static AppSettingsCache fromLegacy(String settingsText) {
        return new AppSettingsCache(settingsText);
    }

    public String value(String keyName) {
        String key = StringUtils.text(keyName);
        if (settingsText.isEmpty() || key.isEmpty()) {
            return "";
        }

        String marker = "[" + key + "=";
        int valueStart = settingsText.toLowerCase().indexOf(marker.toLowerCase());
        if (valueStart >= 0) {
            valueStart += marker.length();
            int valueEnd = settingsText.indexOf(']', valueStart);
            if (valueEnd < 0) {
                valueEnd = settingsText.length();
            }
            return settingsText.substring(valueStart, valueEnd);
        }

        String normalizedText = settingsText.replace("\r\n", "\n").replace('\r', '\n').replace(']', '\n');
        for (String settingLine : normalizedText.split("\n", -1)) {
            String currentLine = settingLine.trim();
            if (currentLine.startsWith("[")) {
                currentLine = currentLine.substring(1);
            }
            int equalsAt = currentLine.indexOf('=');
            if (equalsAt > 0 && currentLine.substring(0, equalsAt).trim().equalsIgnoreCase(key)) {
                return currentLine.substring(equalsAt + 1);
            }
        }
        return "";
    }

    public String valueOrDefault(String keyName, Object defaultValue) {
        String settingValue = value(keyName);
        return settingValue.isEmpty() ? StringUtils.text(defaultValue) : settingValue;
    }
}
