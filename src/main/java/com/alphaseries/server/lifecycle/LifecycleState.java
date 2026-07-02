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

    public synchronized void resetRuntimeDefaults() {
        runtimeState = LicenceRuntimeState.defaults(runtimeState.packetTraceEnabled());
    }

    public synchronized void setPacketTraceEnabled(boolean packetTraceEnabled) {
        runtimeState = runtimeState.withPacketTraceEnabled(packetTraceEnabled);
    }

}
