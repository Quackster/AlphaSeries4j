package com.alphaseries.config;

import com.alphaseries.config.AppPaths;
import com.alphaseries.dao.mysql.SettingsDao;
import com.alphaseries.db.Database;
import com.alphaseries.db.MySQL;
import com.alphaseries.server.logging.Console;
import com.alphaseries.util.FileUtils;

import java.nio.file.Path;

public final class FiguredataBootCache {
    private FiguredataBootCache() {
    }

    /**
     * Original function: Proc_1_3_6BEBA0.
     */
    public static boolean writeFiguredataCache() {
        String figureData = "";
        SettingsDao settings = settingsDao();
        if (settings != null) {
            try {
                figureData = settings.value("com.cache.figuredata");
            } catch (Exception ignored) {
                // Legacy startup cache loading tolerated missing tables or SQL failures.
            }
        }
        String cachePath = Path.of(AppPaths.applicationPath(), "figuredata.cache").toString();
        FileUtils.writeTextFile(cachePath, figureData);
        if (FileUtils.readTextFile(cachePath).trim().isEmpty()) {
            Console.logSourceLine("\"Figuredata\" Datei konnte nicht gefunden werden!", "ERROR", 255L);
            return false;
        }
        return true;
    }

    private static SettingsDao settingsDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new SettingsDao(database);
    }
}
