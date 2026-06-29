package com.alphaseries.messages.outgoing;

import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.protocol.WireEncoding;

public final class JukeboxPayloads {
    private JukeboxPayloads() {
    }

    public static String songInfo(String cdRows) {
        long responseCount = 0L;
        PacketBuilder cdPayload = PacketBuilder.create();
        for (String row : text(cdRows).split("\r", -1)) {
            String rowValue = row.trim();
            if (!rowValue.isEmpty()) {
                String[] fields = rowValue.split("\t", -1);
                if (fields.length >= 5) {
                    long cdId = number(field(fields, 4));
                    long sequenceId = number(field(fields, 1));
                    cdPayload.appendInt(cdId)
                        .appendInt(sequenceId)
                        .appendString(field(fields, 0))
                        .appendString(field(fields, 2))
                        .appendString(field(fields, 3));
                    responseCount++;
                }
            }
        }
        return PacketBuilder.message("Dl")
            .appendInt(responseCount)
            .appendRaw(cdPayload)
            .build();
    }

    public static String playlist(long playlistLimit, String playlistRows) {
        long effectiveLimit = playlistLimit <= 0L ? 100L : playlistLimit;
        long playlistCount = 0L;
        PacketBuilder playlistPayload = PacketBuilder.create();
        for (String row : text(playlistRows).split("\r", -1)) {
            String rowValue = row.trim();
            if (!rowValue.isEmpty()) {
                String[] fields = rowValue.split("\t", -1);
                long cdId = number(field(fields, 0));
                long destinationId = number(field(fields, 1));
                if (cdId > 0L) {
                    playlistPayload.appendInt(cdId).appendInt(destinationId);
                    playlistCount++;
                }
            }
        }
        return PacketBuilder.message("EN")
            .appendInt(playlistCount)
            .appendInt(effectiveLimit)
            .appendRaw(playlistPayload)
            .build();
    }

    public static String diskInventory(String diskRows) {
        long diskCount = 0L;
        PacketBuilder diskPayload = PacketBuilder.create();
        for (String row : text(diskRows).split("\r", -1)) {
            String rowValue = row.trim();
            if (!rowValue.isEmpty()) {
                String[] fields = rowValue.split("\t", -1);
                long diskId = number(field(fields, 0));
                long destinationId = number(field(fields, 1));
                if (diskId > 0L) {
                    diskPayload.appendInt(diskId).appendInt(destinationId);
                    diskCount++;
                }
            }
        }
        return PacketBuilder.message("EM")
            .appendInt(diskCount)
            .appendRaw(diskPayload)
            .build();
    }

    public static String playback(long startedAt, long sequenceId, long destinationId, long diskFurnitureId) {
        if (destinationId <= 0L || sequenceId <= 0L) {
            return "";
        }
        return PacketBuilder.message("EG")
            .appendInt(startedAt)
            .appendInt(sequenceId)
            .appendInt(destinationId)
            .appendInt(diskFurnitureId)
            .appendInt(0L)
            .appendInt(0L)
            .build();
    }

    private static String field(String[] fields, int index) {
        return fields != null && index >= 0 && index < fields.length ? text(fields[index]) : "";
    }

    private static long number(Object value) {
        return WireEncoding.parseLeadingLong(value);
    }

    private static String text(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}
