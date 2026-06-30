package com.alphaseries.server.update;

public final class UpdaterState {
    private static final UpdaterState INSTANCE = new UpdaterState();

    private UpdaterSettings settings = UpdaterSettings.empty();

    private UpdaterState() {
    }

    public static UpdaterState instance() {
        return INSTANCE;
    }

    public synchronized UpdaterSettings settings() {
        return settings;
    }

    public synchronized void setSettings(UpdaterSettings settings) {
        this.settings = settings == null ? UpdaterSettings.empty() : settings;
    }

    public synchronized void setSettingsFromLegacy(String executableName, String updateRows, String updateSql) {
        settings = UpdaterSettings.fromLegacy(executableName, updateRows, updateSql);
    }
}
