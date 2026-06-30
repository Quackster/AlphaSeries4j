package com.alphaseries.config;

public final class AppConfigState {
    private static final AppConfigState INSTANCE = new AppConfigState();

    private AppSettingsCache settingsCache = AppSettingsCache.fromLegacy("");
    private PermissionMatrix permissionMatrix = PermissionMatrix.fromLegacy("");

    private AppConfigState() {
    }

    public static AppConfigState instance() {
        return INSTANCE;
    }

    public synchronized AppSettingsCache settingsCache() {
        return settingsCache;
    }

    public synchronized void setSettingsCache(AppSettingsCache settingsCache) {
        this.settingsCache = settingsCache == null ? AppSettingsCache.fromLegacy("") : settingsCache;
    }

    public synchronized void setSettingsCacheFromLegacy(String settingsText) {
        settingsCache = AppSettingsCache.fromLegacy(settingsText);
    }

    public synchronized PermissionMatrix permissionMatrix() {
        return permissionMatrix;
    }

    public synchronized void setPermissionMatrix(PermissionMatrix permissionMatrix) {
        this.permissionMatrix = permissionMatrix == null ? PermissionMatrix.fromLegacy("") : permissionMatrix;
    }

    public synchronized void setPermissionMatrixFromLegacy(Object permissions) {
        permissionMatrix = PermissionMatrix.fromLegacy(permissions);
    }
}
