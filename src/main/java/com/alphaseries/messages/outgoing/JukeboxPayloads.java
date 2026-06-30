package com.alphaseries.messages.outgoing;

import com.alphaseries.game.jukebox.JukeboxPlaylistEntry;
import com.alphaseries.game.jukebox.SongDiskRow;
import com.alphaseries.game.jukebox.SongInfoRow;
import com.alphaseries.protocol.PacketBuilder;

import java.util.List;

public final class JukeboxPayloads {
    private JukeboxPayloads() {
    }

    public static String songInfo(String cdRows) {
        return songInfo(SongInfoRow.listFromLegacy(cdRows));
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
        return playlist(playlistLimit, JukeboxPlaylistEntry.listFromLegacy(playlistRows));
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
        return diskInventory(SongDiskRow.listFromLegacy(diskRows));
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
