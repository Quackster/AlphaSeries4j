package com.alphaseries.game.jukebox;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public record JukeboxPlaylistEntry(long diskFurnitureId, long destinationId) {
    public static JukeboxPlaylistEntry fromLegacy(String rowText) {
        String[] fields = StringUtils.text(rowText).split("\t", -1);
        return new JukeboxPlaylistEntry(
            NumberUtils.parseLong(StringUtils.field(fields, 0)),
            NumberUtils.parseLong(StringUtils.field(fields, 1)));
    }

    public static List<JukeboxPlaylistEntry> listFromLegacy(String rowsText) {
        List<JukeboxPlaylistEntry> rows = new ArrayList<>();
        for (String row : StringUtils.text(rowsText).split("\r", -1)) {
            String rowValue = row.trim();
            if (!rowValue.isEmpty()) {
                JukeboxPlaylistEntry entry = fromLegacy(rowValue);
                if (entry.diskFurnitureId() > 0L) {
                    rows.add(entry);
                }
            }
        }
        return rows;
    }
}
