package com.alphaseries.game.jukebox;

import java.util.List;

public record SongInfoRequest(long requestedCount, String requestedIds, List<Long> requestedIdList) {
    public SongInfoRequest {
        requestedIds = requestedIds == null ? "" : requestedIds;
        requestedIdList = requestedIdList == null ? List.of() : List.copyOf(requestedIdList);
    }
}
