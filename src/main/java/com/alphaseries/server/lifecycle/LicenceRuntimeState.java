package com.alphaseries.server.lifecycle;

import com.alphaseries.util.StringUtils;

public final class LicenceRuntimeState {
    public static final long DEFAULT_PRIMARY_COLOR = 0xFFFFFFL;
    public static final long DEFAULT_SECONDARY_COLOR = 0xFFFFFFL;
    public static final long DEFAULT_VERSION = 0x17L;
    public static final String DEFAULT_PRODUCT_NAME = "ALPHASERIES_FINAL (PREMIUM)";

    private final long primaryColor;
    private final String productName;
    private final long version;
    private final boolean debugLoggingEnabled;
    private final long secondaryColor;
    private final boolean packetTraceEnabled;

    private LicenceRuntimeState(
        long primaryColor,
        String productName,
        long version,
        boolean debugLoggingEnabled,
        long secondaryColor,
        boolean packetTraceEnabled
    ) {
        this.primaryColor = primaryColor;
        this.productName = StringUtils.text(productName);
        this.version = version;
        this.debugLoggingEnabled = debugLoggingEnabled;
        this.secondaryColor = secondaryColor;
        this.packetTraceEnabled = packetTraceEnabled;
    }

    public static LicenceRuntimeState fromLegacy(
        long primaryColor,
        String productName,
        long version,
        boolean debugLoggingEnabled,
        long secondaryColor,
        boolean packetTraceEnabled
    ) {
        return new LicenceRuntimeState(primaryColor, productName, version, debugLoggingEnabled, secondaryColor, packetTraceEnabled);
    }

    public static LicenceRuntimeState defaults(boolean packetTraceEnabled) {
        return new LicenceRuntimeState(
            DEFAULT_PRIMARY_COLOR,
            DEFAULT_PRODUCT_NAME,
            DEFAULT_VERSION,
            false,
            DEFAULT_SECONDARY_COLOR,
            packetTraceEnabled);
    }

    public static LicenceRuntimeState empty() {
        return new LicenceRuntimeState(0L, "", 0L, false, 0L, false);
    }

    public long primaryColor() {
        return primaryColor;
    }

    public String productName() {
        return productName;
    }

    public long version() {
        return version;
    }

    public boolean debugLoggingEnabled() {
        return debugLoggingEnabled;
    }

    public long secondaryColor() {
        return secondaryColor;
    }

    public boolean packetTraceEnabled() {
        return packetTraceEnabled;
    }

    public boolean shouldTracePackets() {
        return packetTraceEnabled && !debugLoggingEnabled;
    }

    public LicenceRuntimeState withPacketTraceEnabled(boolean packetTraceEnabled) {
        return new LicenceRuntimeState(primaryColor, productName, version, debugLoggingEnabled, secondaryColor, packetTraceEnabled);
    }
}
