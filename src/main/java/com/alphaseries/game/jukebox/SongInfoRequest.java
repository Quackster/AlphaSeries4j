package com.alphaseries.game.jukebox;

import java.util.List;

public record SongInfoRequest(long requestedCount, List<Long> requestedIdList) {
    public SongInfoRequest {
        requestedIdList = requestedIdList == null ? List.of() : List.copyOf(requestedIdList);
    }
}
