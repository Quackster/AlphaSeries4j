package com.alphaseries.server.update;

import com.alphaseries.config.AppPaths;
import com.alphaseries.dao.mysql.UpdaterDao;
import com.alphaseries.db.Database;
import com.alphaseries.db.MySQL;
import com.alphaseries.util.FileUtils;
import com.alphaseries.util.StringUtils;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class Updater {
    public static final long PROGRESS_WIDTH_MAX = 11535L;
    public static final String FORM_CAPTION = "Downloade Updates...";
    public static final String DOWNLOAD_LABEL_CAPTION = "Downloade...";
    public static final String CMS_REDOWNLOAD_CAPTION = "CMS muss im Store erneut heruntergeladen werden";
    public static final String DEFAULT_FREE_FEATURE_CAPTION = "Kostenloses Feature";
    public static final String DEFAULT_COST_FEATURE_CAPTION = "Kostet 10 Punkte";
    public static final String RETRY_ERROR_MESSAGE = "Es ist ein Fehler aufgetreten. Versuche es erneut!";
    public static final String MYSQL_CONNECTION_ERROR_MESSAGE =
        "Es kann keine Verbindung zur MySQL Datenbank hergestellt werden.";
    public static final String SUCCESS_FORUM_MESSAGE =
        "Bitte schauen Sie doch einmal in unserem User Voice Forum nach neuen Meldungen. "
            + "Die Webseite wurde automatisch ge\u00f6ffnet.";

    public long height = 1000L;
    public long imageWidth = 0L;
    public long pendingHeightTarget = 0L;
    public long pendingAnimationInterval = 0L;
    public long pendingProgressWidth = 0L;
    public long currentUpdateIndex = 0L;
    public boolean timer1Enabled = false;
    public boolean timer2Enabled = false;
    public boolean timer3Enabled = false;
    public boolean walkPercentEnabled = false;
    public String currentUpdateEntry = "";
    public String initCaption = "";

    public final FeatureState freeFeature = new FeatureState();
    public final FeatureState unfreeFeature = new FeatureState();
    public final FeatureState downloadFeature = new FeatureState();

    public record HeightStep(long height, boolean timer2Enabled) {
    }

    public record ProgressStep(long width, boolean walkPercentEnabled, boolean complete) {
    }

    public record RenderStep(boolean rendered, boolean complete, String title, String[] bodyLines, long featureTop) {
        public RenderStep {
            title = StringUtils.text(title);
            bodyLines = bodyLines == null ? new String[0] : bodyLines.clone();
        }

        @Override
        public String[] bodyLines() {
            return bodyLines.clone();
        }

        public static RenderStep empty() {
            return new RenderStep(false, false, "", new String[0], 0L);
        }

        public static RenderStep completed() {
            return new RenderStep(false, true, "", new String[0], 0L);
        }

        public static RenderStep rendered(String title, String[] bodyLines, long featureTop) {
            return new RenderStep(true, false, title, bodyLines, featureTop);
        }
    }

    public record DownloadPlan(String executableName, String destinationPath, String sourceUrl, long targetWidth) {
    }

    public void queueHeightAnimation(long targetHeight, long animationInterval) {
        pendingHeightTarget = targetHeight;
        pendingAnimationInterval = animationInterval;
        timer1Enabled = true;
    }

    public void queueProgressWidth(long targetWidth) {
        pendingProgressWidth = targetWidth;
        walkPercentEnabled = true;
    }

    public void advanceUpdateProgress(long updateCount) {
        currentUpdateIndex++;
        if (updateCount <= 0L) {
            updateCount = 1L;
        }
        queueProgressWidth((PROGRESS_WIDTH_MAX / updateCount) * (currentUpdateIndex + 1L));
    }

    public void applyFeatureState(UpdaterSettings.UpdateEntry entry) {
        freeFeature.visible = false;
        unfreeFeature.visible = false;
        downloadFeature.visible = false;

        if (entry.featureMode() == 0L) {
            freeFeature.caption = "Kostenlose Funktion";
            freeFeature.visible = true;
        } else if (entry.featureMode() == 1L) {
            downloadFeature.visible = true;
        } else {
            unfreeFeature.caption = "Kostet " + entry.featureCost() + " Punkte";
            unfreeFeature.visible = true;
        }
    }

    public void startHeightAnimationTimer() {
        timer1Enabled = false;
        if (pendingAnimationInterval <= 0L) {
            pendingAnimationInterval = 1L;
        }
        timer2Enabled = true;
    }

    public HeightStep applyHeightTimerStep(long currentHeight) {
        HeightStep step = heightTimerStep(currentHeight, pendingHeightTarget);
        timer2Enabled = step.timer2Enabled();
        return step;
    }

    public ProgressStep applyProgressTimerStep(long currentWidth, boolean renderTimerEnabled) {
        ProgressStep step = progressTimerStep(currentWidth, pendingProgressWidth, renderTimerEnabled);
        walkPercentEnabled = step.walkPercentEnabled();
        return step;
    }

    public RenderStep timer3Step() {
        try {
            if (height != 1000L) {
                queueHeightAnimation(1000, 5);
                return RenderStep.empty();
            }
            java.util.List<UpdaterSettings.UpdateEntry> entries = UpdaterState.instance().settings().entryList();
            if (currentUpdateIndex > entries.size() - 1L) {
                queueHeightAnimation(1000, 5);
                timer3Enabled = false;
                return RenderStep.completed();
            }
            UpdaterSettings.UpdateEntry entry = entries.get((int) currentUpdateIndex);
            currentUpdateEntry = entry.sourceText();
            if (!entry.valid()) {
                advanceUpdateProgress(entries.size());
                return RenderStep.empty();
            }
            String[] bodyLines = visibleBodyLines(entry.bodyText(), 25);
            applyFeatureState(entry);
            long visibleLineCount = bodyLines.length == 0 ? 1L : bodyLines.length;
            long featureTop = visibleLineCount * 240L + 720L;
            queueHeightAnimation(featureTop + 920L, 10);
            advanceUpdateProgress(entries.size());
            return RenderStep.rendered(entry.title(), bodyLines, featureTop);
        } catch (Exception ignored) {
            timer3Enabled = false;
            return RenderStep.completed();
        }
    }

    public boolean downloadFileTimer(String appExeName) {
        DownloadPlan plan = downloadPlan(appExeName, LocalDateTime.now());
        queueProgressWidth(plan.targetWidth());
        currentUpdateIndex = 0L;
        boolean downloaded = FileUtils.downloadFile(plan.sourceUrl(), plan.destinationPath());
        if (downloaded) {
            initCaption = "Installiere...";
            timer3Enabled = true;
        }
        return downloaded;
    }

    public DownloadPlan downloadPlan(String appExeName, LocalDateTime timestamp) {
        long updateCount = UpdaterState.instance().settings().updateCountOrOne();
        String executableName = getUpdaterExecutableName(appExeName);
        return new DownloadPlan(
            executableName,
            Path.of(AppPaths.applicationPath(), executableName + ".exe").toString(),
            "http://www.alpha-series.com/upgrades/" + executableName
                + "/file.database?timestamp=" + timestamp.format(DateTimeFormatter.ofPattern("dMuuHms")),
            Math.max(1L, PROGRESS_WIDTH_MAX / updateCount));
    }

    public boolean formLoad(boolean databaseConnected) {
        try {
            UpdaterSettings settings = UpdaterState.instance().settings();
            if (settings.hasUpdateSql()) {
                if (!databaseConnected) {
                    return false;
                }
                String sqlText = settings.normalizedUpdateSql();
                UpdaterDao updaterDao = updaterDao();
                if (updaterDao == null) {
                    return false;
                }
                for (String line : sqlText.split("\n", -1)) {
                    if (line.trim().length() > 5) {
                        updaterDao.executeUpdateSql(line.trim());
                    }
                }
            }
            height = 1000L;
            imageWidth = 0L;
            pendingProgressWidth = 0L;
            pendingHeightTarget = 0L;
            pendingAnimationInterval = 0L;
            currentUpdateIndex = 0L;
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public boolean formUnload() {
        return true;
    }

    public boolean formQueryUnload() {
        return true;
    }

    public static HeightStep heightTimerStep(long currentHeight, long targetHeight) {
        if (targetHeight <= 0L) {
            return new HeightStep(currentHeight, false);
        }
        if (currentHeight < targetHeight) {
            long height = currentHeight + 50L;
            return height >= targetHeight
                ? new HeightStep(targetHeight, false)
                : new HeightStep(height, true);
        }
        if (currentHeight > targetHeight) {
            long height = currentHeight - 50L;
            return height <= targetHeight
                ? new HeightStep(targetHeight, false)
                : new HeightStep(height, true);
        }
        return new HeightStep(currentHeight, false);
    }

    public static ProgressStep progressTimerStep(long currentWidth, long targetWidth, boolean renderTimerEnabled) {
        if (targetWidth <= 0L) {
            return new ProgressStep(currentWidth, false, false);
        }
        long width = currentWidth;
        if (currentWidth < targetWidth) {
            width = currentWidth + 50L;
            if (width > targetWidth) {
                width = targetWidth;
            }
        } else if (currentWidth > targetWidth) {
            width = targetWidth;
        }
        boolean complete = width >= PROGRESS_WIDTH_MAX && !renderTimerEnabled;
        return new ProgressStep(width, !complete, complete);
    }

    public static String getUpdaterExecutableName(String configuredName, String appExeName) {
        String configuredText = StringUtils.text(configuredName);
        return !configuredText.isEmpty() ? configuredText : StringUtils.text(appExeName);
    }

    public static String getUpdaterExecutableName(String appExeName) {
        return UpdaterState.instance().settings().executableNameOr(appExeName);
    }

    public static String normalizedUpdateSql(String updateSql) {
        return UpdaterSettings.normalizeUpdateSql(updateSql);
    }

    public static String successfulDownloadMessage(String executableName) {
        return "Update erfolgreich heruntergeladen. Die Datei wurde nach \""
            + StringUtils.text(executableName) + ".exe\" benannt.\r\n\r\n"
            + SUCCESS_FORUM_MESSAGE;
    }

    public static String[] visibleBodyLines(String bodyText, int maxLines) {
        String[] rawLines = StringUtils.text(bodyText).split("\\\\n", -1);
        int visible = 0;
        int limit = Math.min(maxLines, rawLines.length);
        for (int index = 0; index < limit; index++) {
            if (!rawLines[index].isEmpty()) {
                visible = index + 1;
            }
        }
        String[] lines = new String[visible];
        for (int index = 0; index < visible; index++) {
            lines[index] = rawLines[index];
        }
        return lines;
    }

    private static UpdaterDao updaterDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new UpdaterDao(database);
    }

    public static final class FeatureState {
        public boolean visible = false;
        public String caption = "";
    }
}
