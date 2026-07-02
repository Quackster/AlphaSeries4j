package com.alphaseries.game.jukebox;

import com.alphaseries.protocol.WireEncoding;
import com.alphaseries.protocol.WireReader;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public final class JukeboxRequests {
    private JukeboxRequests() {
    }

    public static SongInfoRequest songInfoFromWire(String packetPayload) {
        String requestPayload = StringUtils.text(packetPayload);
        if (requestPayload.startsWith("C]")) {
            requestPayload = requestPayload.substring(2);
        }
        WireReader.Offset offset = new WireReader.Offset(1);
        long requestedCount = WireReader.readLong(requestPayload, offset);
        if (requestedCount <= 0L) {
            requestedCount = NumberUtils.parseLong(WireEncoding.readVl64LengthString(requestPayload));
        }
        if (requestedCount > 60L) {
            requestedCount = 60L;
        }
        List<Long> requestedIdList = new ArrayList<>();
        List<String> requestedTokens = new ArrayList<>();
        for (long requestIndex = 0L; requestIndex < requestedCount; requestIndex++) {
            long cdId = WireReader.readLong(requestPayload, offset);
            if (cdId > 0L) {
                requestedIdList.add(cdId);
                requestedTokens.add(String.valueOf(cdId));
            }
        }
        return new SongInfoRequest(requestedCount, String.join(",", requestedTokens), requestedIdList);
    }

    static String removeSoundMachineMarkers(String representedRoomCache, long jukeboxId, long activeDestinationId) {
        String cache = StringUtils.text(representedRoomCache);
        if (activeDestinationId > 0L) {
            cache = cache.replaceFirst(Pattern.quote("\1" + activeDestinationId + '\2'), "");
        }
        if (jukeboxId > 0L) {
            cache = cache.replaceFirst(Pattern.quote("\1" + jukeboxId + '\2'), "");
        }
        return cache;
    }

    public static JukeboxAddRequest addRequestFromWire(String packetPayload) {
        String requestPayload = StringUtils.text(packetPayload);
        if (requestPayload.startsWith("C" + '\177')) {
            requestPayload = requestPayload.substring(2);
        }
        WireReader.Offset offset = new WireReader.Offset(1);
        long diskFurnitureId = WireReader.readLong(requestPayload, offset);
        long playlistOrder = WireReader.readLong(requestPayload, offset);
        return new JukeboxAddRequest(diskFurnitureId, Math.max(0L, playlistOrder));
    }

    public static boolean canAddDisk(long playlistOrder, String maxOrderText, long playlistCount, long playlistLimit) {
        long effectiveLimit = playlistLimit <= 0L ? 100L : playlistLimit;
        if (playlistCount >= effectiveLimit) {
            return false;
        }
        String maxText = StringUtils.text(maxOrderText);
        long maxOrder = NumberUtils.parseLong(maxText);
        if (!maxText.isEmpty()) {
            return playlistOrder == maxOrder || playlistOrder == maxOrder + 1L;
        }
        return playlistOrder == 0L;
    }

    public static long removeOrderFromWire(String packetPayload) {
        String requestPayload = StringUtils.text(packetPayload);
        if (requestPayload.startsWith("D@")) {
            requestPayload = requestPayload.substring(2);
        }
        WireReader.Offset offset = new WireReader.Offset(1);
        long playlistOrder = WireReader.readLong(requestPayload, offset);
        return Math.max(0L, playlistOrder);
    }

}
