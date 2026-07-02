package com.alphaseries.config;

public final class AppConfigState {
    private static final AppConfigState INSTANCE = new AppConfigState();

    private AppSettingsCache settingsCache = AppSettingsCache.empty();
    private PermissionMatrix permissionMatrix = PermissionMatrix.empty();

    private AppConfigState() {
    }

    public static AppConfigState instance() {
        return INSTANCE;
    }

    public synchronized AppSettingsCache settingsCache() {
        return settingsCache;
    }

    /**
     * Original function: Proc_10_0_809570.
     */
    public synchronized String settingValueOrDefault(String keyName, String defaultValue) {
        return settingsCache.valueOrDefault(keyName, defaultValue);
    }

    public synchronized void setSettingsCache(AppSettingsCache settingsCache) {
        this.settingsCache = settingsCache == null ? AppSettingsCache.empty() : settingsCache;
    }

    public synchronized PermissionMatrix permissionMatrix() {
        return permissionMatrix;
    }

    /**
     * Original function: Proc_10_1_809790.
     */
    public synchronized boolean allowsPermission(long rankIndex, String basePermissions, String permissionName, long hcLevel) {
        return permissionMatrix.allows(rankIndex, basePermissions, permissionName, hcLevel);
    }

    public synchronized void setPermissionMatrix(PermissionMatrix permissionMatrix) {
        this.permissionMatrix = permissionMatrix == null ? PermissionMatrix.empty() : permissionMatrix;
    }
}
