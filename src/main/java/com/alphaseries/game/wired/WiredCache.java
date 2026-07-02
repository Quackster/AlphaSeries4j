package com.alphaseries.game.wired;

import com.alphaseries.config.AppPaths;
import com.alphaseries.util.FileUtils;
import com.alphaseries.util.StringUtils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class WiredCache {
    private WiredCache() {
    }

    public static String path(String cacheFolder, long roomId) {
        return Path.of(AppPaths.applicationPath(), "cache", StringUtils.text(cacheFolder), roomId + ".cache").toString();
    }

    public static String read(String cacheFolder, long roomId) {
        return roomId <= 0L ? "" : FileUtils.readTextFile(path(cacheFolder, roomId));
    }

    public static List<WiredPayloads.WiredRecord> records(String cacheFolder, long roomId) {
        List<WiredPayloads.WiredRecord> records = new ArrayList<>();
        for (String row : read(cacheFolder, roomId).replace("\r", "").split("\n", -1)) {
            String recordText = row.trim();
            if (!recordText.isEmpty()) {
                records.add(WiredPayloads.record(recordText));
            }
        }
        return records;
    }

    public static void appendRecord(String cacheFolder, long roomId, String recordText) {
        String cachePath = path(cacheFolder, roomId);
        String cacheText = FileUtils.readTextFile(cachePath);
        FileUtils.writeTextFile(cachePath, WiredPayloads.cacheWithRecord(cacheText, recordText));
    }
}
