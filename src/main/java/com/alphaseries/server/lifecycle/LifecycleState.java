package com.alphaseries.server.lifecycle;

public final class LifecycleState {
    private static final LifecycleState INSTANCE = new LifecycleState();

    private LicenceRuntimeState runtimeState = LicenceRuntimeState.empty();

    private LifecycleState() {
    }

    public static LifecycleState instance() {
        return INSTANCE;
    }

    public synchronized LicenceRuntimeState runtimeState() {
        return runtimeState;
    }

    public synchronized void setRuntimeState(LicenceRuntimeState runtimeState) {
        this.runtimeState = runtimeState == null ? LicenceRuntimeState.empty() : runtimeState;
    }

    public synchronized void setRuntimeStateFromLegacy(
        long primaryColor,
        String productName,
        long version,
        boolean debugLoggingEnabled,
        long secondaryColor,
        boolean packetTraceEnabled
    ) {
        runtimeState = LicenceRuntimeState.fromLegacy(
            primaryColor,
            productName,
            version,
            debugLoggingEnabled,
            secondaryColor,
            packetTraceEnabled);
    }
}
