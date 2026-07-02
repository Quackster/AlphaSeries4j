package com.alphaseries.game.jukebox;

import com.alphaseries.protocol.WireEncoding;
import com.alphaseries.protocol.WireReader;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class JukeboxRequests {
    private JukeboxRequests() {
    }

    public static SongInfoRequest songInfoFromWire(String packetPayload) {
        String requestPayload = StringUtils.withoutPrefix(packetPayload, "C]");
        WireReader.Offset offset = new WireReader.Offset(1);
        long requestedCount = WireReader.readLong(requestPayload, offset);
        if (requestedCount <= 0L) {
            requestedCount = NumberUtils.parseLong(WireEncoding.readVl64LengthString(requestPayload));
        }
        if (requestedCount > 60L) {
            requestedCount = 60L;
        }
        List<Long> requestedIdList = new ArrayList<>();
        for (long requestIndex = 0L; requestIndex < requestedCount; requestIndex++) {
            long cdId = WireReader.readLong(requestPayload, offset);
            if (cdId > 0L) {
                requestedIdList.add(cdId);
            }
        }
        return new SongInfoRequest(requestedCount, requestedIdList);
    }

    public static JukeboxAddRequest addRequestFromWire(String packetPayload) {
        String requestPayload = StringUtils.withoutPrefix(packetPayload, "C" + '\177');
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

    public static JukeboxRemoveRequest removeRequestFromWire(String packetPayload) {
        String requestPayload = StringUtils.withoutPrefix(packetPayload, "D@");
        WireReader.Offset offset = new WireReader.Offset(1);
        long playlistOrder = WireReader.readLong(requestPayload, offset);
        return new JukeboxRemoveRequest(Math.max(0L, playlistOrder));
    }

}
