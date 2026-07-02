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
        return CacheFile.fromText(read(cacheFolder, roomId)).records();
    }

    public static void appendRecord(String cacheFolder, long roomId, WiredPayloads.WiredRecord record) {
        String cachePath = path(cacheFolder, roomId);
        CacheFile cache = CacheFile.fromText(FileUtils.readTextFile(cachePath));
        FileUtils.writeTextFile(cachePath, cache.withRecord(record).cacheText());
    }

    private record CacheFile(String cacheText, List<WiredPayloads.WiredRecord> records) {
        private CacheFile {
            cacheText = StringUtils.text(cacheText);
            records = List.copyOf(records);
        }

        private static CacheFile fromText(String cacheText) {
            List<WiredPayloads.WiredRecord> records = new ArrayList<>();
            for (String row : StringUtils.delimitedFields(StringUtils.withoutCarriageReturns(cacheText), '\n')) {
                String recordText = row.trim();
                if (!recordText.isEmpty()) {
                    records.add(WiredPayloads.record(recordText));
                }
            }
            return new CacheFile(cacheText, records);
        }

        private CacheFile withRecord(WiredPayloads.WiredRecord record) {
            return fromText(WiredPayloads.cacheWithRecord(cacheText, record));
        }
    }
}
