package com.alphaseries.protocol;

public final class PacketReader {
    private final String payload;
    private int offset;

    public PacketReader(String payload) {
        this.payload = payload == null ? "" : payload;
    }

    public static PacketReader of(String payload) {
        return new PacketReader(payload);
    }

    public long readInt() {
        if (offset >= payload.length()) {
            return 0L;
        }
        String remaining = payload.substring(offset);
        long length = WireEncoding.vl64FieldLength(remaining);
        if (length <= 0L) {
            return 0L;
        }
        long value = WireEncoding.decodeVl64(remaining);
        offset += (int) length;
        return value;
    }

    public String readString() {
        if (offset >= payload.length()) {
            return "";
        }
        long length = WireEncoding.decodeBase64Length(payload.substring(offset));
        if (length <= 0L) {
            return "";
        }
        int valueStart = offset + 2;
        int valueEnd = Math.min(payload.length(), valueStart + (int) length);
        if (valueEnd - valueStart < length) {
            return "";
        }
        offset = valueEnd;
        return payload.substring(valueStart, valueEnd);
    }

    public String remaining() {
        return offset >= payload.length() ? "" : payload.substring(offset);
    }

    public int offset() {
        return offset;
    }
}
