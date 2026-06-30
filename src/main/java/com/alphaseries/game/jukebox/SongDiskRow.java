package com.alphaseries.game.jukebox;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public record SongDiskRow(long furnitureId, long destinationId) {
    public static SongDiskRow fromLegacy(String rowText) {
        String[] fields = StringUtils.text(rowText).split("\t", -1);
        return new SongDiskRow(
            NumberUtils.parseLong(StringUtils.field(fields, 0)),
            NumberUtils.parseLong(StringUtils.field(fields, 1)));
    }

    public static List<SongDiskRow> listFromLegacy(String rowsText) {
        List<SongDiskRow> rows = new ArrayList<>();
        for (String row : StringUtils.text(rowsText).split("\r", -1)) {
            String rowValue = row.trim();
            if (!rowValue.isEmpty()) {
                SongDiskRow disk = fromLegacy(rowValue);
                if (disk.furnitureId() > 0L) {
                    rows.add(disk);
                }
            }
        }
        return rows;
    }
}
