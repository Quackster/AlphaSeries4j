package com.alphaseries.messages.outgoing;

import com.alphaseries.game.jukebox.JukeboxPlaylistEntry;
import com.alphaseries.game.jukebox.SongDiskRow;
import com.alphaseries.game.jukebox.SongInfoRow;
import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class JukeboxPayloads {
    private JukeboxPayloads() {
    }

    public static String songInfo(String cdRows) {
        List<SongInfoRow> songs = new ArrayList<>();
        for (String row : StringUtils.text(cdRows).split("\r", -1)) {
            String rowValue = row.trim();
            if (!rowValue.isEmpty()) {
                String[] fields = rowValue.split("\t", -1);
                if (fields.length >= 5) {
                    songs.add(new SongInfoRow(
                        StringUtils.field(fields, 0),
                        NumberUtils.parseLong(StringUtils.field(fields, 1)),
                        StringUtils.field(fields, 2),
                        StringUtils.field(fields, 3),
                        NumberUtils.parseLong(StringUtils.field(fields, 4))));
                }
            }
        }
        return songInfo(songs);
    }

    public static String songInfo(List<SongInfoRow> songs) {
        long responseCount = 0L;
        PacketBuilder cdPayload = PacketBuilder.create();
        for (SongInfoRow song : songs == null ? List.<SongInfoRow>of() : songs) {
            if (song != null && song.cdId() > 0L) {
                cdPayload.appendInt(song.cdId())
                    .appendInt(song.sequenceId())
                    .appendString(song.title())
                    .appendString(song.author())
                    .appendString(song.sound());
                responseCount++;
            }
        }
        return PacketBuilder.message("Dl")
            .appendInt(responseCount)
            .appendRaw(cdPayload)
            .build();
    }

    public static String playlist(long playlistLimit, String playlistRows) {
        List<JukeboxPlaylistEntry> entries = new ArrayList<>();
        for (String row : StringUtils.text(playlistRows).split("\r", -1)) {
            String rowValue = row.trim();
            if (!rowValue.isEmpty()) {
                String[] fields = rowValue.split("\t", -1);
                long cdId = NumberUtils.parseLong(StringUtils.field(fields, 0));
                long destinationId = NumberUtils.parseLong(StringUtils.field(fields, 1));
                if (cdId > 0L) {
                    entries.add(new JukeboxPlaylistEntry(cdId, destinationId));
                }
            }
        }
        return playlist(playlistLimit, entries);
    }

    public static String playlist(long playlistLimit, List<JukeboxPlaylistEntry> entries) {
        long effectiveLimit = playlistLimit <= 0L ? 100L : playlistLimit;
        long playlistCount = 0L;
        PacketBuilder playlistPayload = PacketBuilder.create();
        for (JukeboxPlaylistEntry entry : entries == null ? List.<JukeboxPlaylistEntry>of() : entries) {
            if (entry != null && entry.diskFurnitureId() > 0L) {
                playlistPayload.appendInt(entry.diskFurnitureId()).appendInt(entry.destinationId());
                playlistCount++;
            }
        }
        return PacketBuilder.message("EN")
            .appendInt(playlistCount)
            .appendInt(effectiveLimit)
            .appendRaw(playlistPayload)
            .build();
    }

    public static String diskInventory(String diskRows) {
        List<SongDiskRow> disks = new ArrayList<>();
        for (String row : StringUtils.text(diskRows).split("\r", -1)) {
            String rowValue = row.trim();
            if (!rowValue.isEmpty()) {
                String[] fields = rowValue.split("\t", -1);
                long diskId = NumberUtils.parseLong(StringUtils.field(fields, 0));
                long destinationId = NumberUtils.parseLong(StringUtils.field(fields, 1));
                if (diskId > 0L) {
                    disks.add(new SongDiskRow(diskId, destinationId));
                }
            }
        }
        return diskInventory(disks);
    }

    public static String diskInventory(List<SongDiskRow> disks) {
        long diskCount = 0L;
        PacketBuilder diskPayload = PacketBuilder.create();
        for (SongDiskRow disk : disks == null ? List.<SongDiskRow>of() : disks) {
            if (disk != null && disk.furnitureId() > 0L) {
                diskPayload.appendInt(disk.furnitureId()).appendInt(disk.destinationId());
                diskCount++;
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
