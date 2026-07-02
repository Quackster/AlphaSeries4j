package com.alphaseries.protocol;

import com.alphaseries.util.StringUtils;

public final class WireReader {
    private WireReader() {
    }

    public static final class Offset {
        private long value;

        public Offset(long value) {
            this.value = value;
        }

        public long value() {
            return value;
        }

        public void setValue(long value) {
            this.value = value;
        }

        public void advance(long amount) {
            value += amount;
        }
    }

    public static String readString(String packetPayload, Offset offset) {
        if (offset == null) {
            return "";
        }
        String payload = StringUtils.text(packetPayload);
        if (offset.value() < 1L) {
            offset.setValue(1L);
        }
        if (offset.value() + 1L > payload.length()) {
            return "";
        }
        int start = (int) offset.value() - 1;
        long fieldLength = WireEncoding.decodeBase64Length(payload.substring(start));
        if (fieldLength <= 0L) {
            return "";
        }
        int valueStart = start + 2;
        int valueEnd = Math.min(payload.length(), valueStart + (int) fieldLength);
        if (valueEnd - valueStart < fieldLength) {
            return "";
        }
        offset.advance(2L + fieldLength);
        return payload.substring(valueStart, valueEnd);
    }

    public static long readLong(String packetPayload, Offset offset) {
        if (offset == null) {
            return 0L;
        }
        String payload = StringUtils.text(packetPayload);
        if (offset.value() < 1L) {
            offset.setValue(1L);
        }
        if (offset.value() > payload.length()) {
            return 0L;
        }
        String remainingPayload = payload.substring((int) offset.value() - 1);
        long encodedLengthSize = WireEncoding.vl64FieldLength(remainingPayload);
        if (encodedLengthSize <= 0L) {
            return 0L;
        }
        long value = WireEncoding.decodeVl64(remainingPayload);
        offset.advance(encodedLengthSize);
        return value;
    }
}
