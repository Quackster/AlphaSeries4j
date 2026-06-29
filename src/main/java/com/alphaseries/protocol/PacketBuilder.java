package com.alphaseries.protocol;

public final class PacketBuilder {
    private final StringBuilder payload = new StringBuilder();

    private PacketBuilder() {
    }

    public static PacketBuilder create() {
        return new PacketBuilder();
    }

    public static PacketBuilder message(String header) {
        return create().appendRaw(header);
    }

    public PacketBuilder appendRaw(Object value) {
        if (value != null) {
            payload.append(value);
        }
        return this;
    }

    public PacketBuilder appendInt(long value) {
        payload.append(WireEncoding.encodeVl64(value));
        return this;
    }

    public PacketBuilder appendString(Object value) {
        if (value != null) {
            payload.append(value);
        }
        payload.append('\2');
        return this;
    }

    public PacketBuilder appendBoolean(boolean value) {
        return appendInt(value ? 1L : 0L);
    }

    public PacketBuilder appendObject(OutgoingPayload object) {
        if (object != null) {
            object.compose(this);
        }
        return this;
    }

    public String build() {
        return payload.toString();
    }

    @Override
    public String toString() {
        return build();
    }
}
