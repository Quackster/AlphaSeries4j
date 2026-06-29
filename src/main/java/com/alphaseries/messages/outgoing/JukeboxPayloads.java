package com.alphaseries.messages.outgoing;

import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

public final class JukeboxPayloads {
    private JukeboxPayloads() {
    }

    public static String songInfo(String cdRows) {
        long responseCount = 0L;
        PacketBuilder cdPayload = PacketBuilder.create();
        for (String row : StringUtils.text(cdRows).split("\r", -1)) {
            String rowValue = row.trim();
            if (!rowValue.isEmpty()) {
                String[] fields = rowValue.split("\t", -1);
                if (fields.length >= 5) {
                    long cdId = NumberUtils.parseLong(StringUtils.field(fields, 4));
                    long sequenceId = NumberUtils.parseLong(StringUtils.field(fields, 1));
                    cdPayload.appendInt(cdId)
                        .appendInt(sequenceId)
                        .appendString(StringUtils.field(fields, 0))
                        .appendString(StringUtils.field(fields, 2))
                        .appendString(StringUtils.field(fields, 3));
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
        for (String row : StringUtils.text(playlistRows).split("\r", -1)) {
            String rowValue = row.trim();
            if (!rowValue.isEmpty()) {
                String[] fields = rowValue.split("\t", -1);
                long cdId = NumberUtils.parseLong(StringUtils.field(fields, 0));
                long destinationId = NumberUtils.parseLong(StringUtils.field(fields, 1));
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
        for (String row : StringUtils.text(diskRows).split("\r", -1)) {
            String rowValue = row.trim();
            if (!rowValue.isEmpty()) {
                String[] fields = rowValue.split("\t", -1);
                long diskId = NumberUtils.parseLong(StringUtils.field(fields, 0));
                long destinationId = NumberUtils.parseLong(StringUtils.field(fields, 1));
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

}
