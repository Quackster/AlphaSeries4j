package com.alphaseries.game.help;

public final class HelpCenterState {
    private static final HelpCenterState INSTANCE = new HelpCenterState();

    private HelpCenterCache cache = HelpCenterCache.empty();

    private HelpCenterState() {
    }

    public static HelpCenterState instance() {
        return INSTANCE;
    }

    public synchronized HelpCenterCache cache() {
        return cache;
    }

    public synchronized void setCache(HelpCenterCache cache) {
        this.cache = cache == null ? HelpCenterCache.empty() : cache;
    }

}
