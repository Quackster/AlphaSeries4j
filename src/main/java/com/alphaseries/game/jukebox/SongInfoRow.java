package com.alphaseries.game.jukebox;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public record SongInfoRow(String title, long sequenceId, String author, String sound, long cdId) {
    public static SongInfoRow fromLegacy(String rowText) {
        String[] fields = StringUtils.text(rowText).split("\t", -1);
        if (fields.length < 5) {
            return null;
        }
        return new SongInfoRow(
            StringUtils.field(fields, 0),
            NumberUtils.parseLong(StringUtils.field(fields, 1)),
            StringUtils.field(fields, 2),
            StringUtils.field(fields, 3),
            NumberUtils.parseLong(StringUtils.field(fields, 4)));
    }

    public static List<SongInfoRow> listFromLegacy(String rowsText) {
        List<SongInfoRow> rows = new ArrayList<>();
        for (String row : StringUtils.text(rowsText).split("\r", -1)) {
            String rowValue = row.trim();
            if (!rowValue.isEmpty()) {
                SongInfoRow song = fromLegacy(rowValue);
                if (song != null) {
                    rows.add(song);
                }
            }
        }
        return rows;
    }
}
