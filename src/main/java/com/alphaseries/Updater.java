package com.alphaseries;

import com.alphaseries.vb.Vb;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class Updater {
    public static final long PROGRESS_WIDTH_MAX = 11535L;

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

    public static final class HeightStep {
        public long height;
        public boolean timer2Enabled;
    }

    public static final class ProgressStep {
        public long width;
        public boolean walkPercentEnabled;
        public boolean complete;
    }

    public static final class RenderStep {
        public boolean rendered;
        public boolean complete;
        public String title = "";
        public String[] bodyLines = new String[0];
        public long featureTop;
    }

    public static final class DownloadPlan {
        public String executableName = "";
        public String destinationPath = "";
        public String sourceUrl = "";
        public long targetWidth;
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

    public void applyFeatureState(String[] fields) {
        long featureMode = getUpdateFieldNumber(fields, 3);
        long featureCost = getUpdateFieldNumber(fields, 4);

        freeFeature.visible = false;
        unfreeFeature.visible = false;
        downloadFeature.visible = false;

        if (featureMode == 0L) {
            freeFeature.caption = "Kostenlose Funktion";
            freeFeature.visible = true;
        } else if (featureMode == 1L) {
            downloadFeature.visible = true;
        } else {
            unfreeFeature.caption = "Kostet " + featureCost + " Punkte";
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
        timer2Enabled = step.timer2Enabled;
        return step;
    }

    public ProgressStep applyProgressTimerStep(long currentWidth, boolean renderTimerEnabled) {
        ProgressStep step = progressTimerStep(currentWidth, pendingProgressWidth, renderTimerEnabled);
        walkPercentEnabled = step.walkPercentEnabled;
        return step;
    }

    public RenderStep timer3Step() {
        RenderStep step = new RenderStep();
        try {
            if (height != 1000L) {
                queueHeightAnimation(1000, 5);
                return step;
            }
            String[] entries = Licence.global_00829044.split("\n", -1);
            if (currentUpdateIndex > entries.length - 1L) {
                queueHeightAnimation(1000, 5);
                timer3Enabled = false;
                step.complete = true;
                return step;
            }
            currentUpdateEntry = entries[(int) currentUpdateIndex];
            String[] fields = currentUpdateEntry.split("\t", -1);
            if (fields.length < 3) {
                advanceUpdateProgress(entries.length);
                return step;
            }
            step.title = fields[1];
            step.bodyLines = visibleBodyLines(fields[2], 25);
            applyFeatureState(fields);
            long visibleLineCount = step.bodyLines.length == 0 ? 1L : step.bodyLines.length;
            step.featureTop = visibleLineCount * 240L + 720L;
            queueHeightAnimation(step.featureTop + 920L, 10);
            advanceUpdateProgress(entries.length);
            step.rendered = true;
            return step;
        } catch (Exception ignored) {
            step.complete = true;
            timer3Enabled = false;
            return step;
        }
    }

    public boolean downloadFileTimer(String appExeName) {
        DownloadPlan plan = downloadPlan(appExeName, LocalDateTime.now());
        queueProgressWidth(plan.targetWidth);
        currentUpdateIndex = 0L;
        boolean downloaded = Functions.Proc_10_28_8210C0(plan.sourceUrl, plan.destinationPath);
        if (downloaded) {
            initCaption = "Installiere...";
            timer3Enabled = true;
        }
        return downloaded;
    }

    public DownloadPlan downloadPlan(String appExeName, LocalDateTime timestamp) {
        String[] updateRows = Licence.global_00829044.split("\n", -1);
        long updateCount = updateRows.length - 1L;
        if (updateCount <= 0L) {
            updateCount = 1L;
        }
        DownloadPlan plan = new DownloadPlan();
        plan.targetWidth = Math.max(1L, PROGRESS_WIDTH_MAX / updateCount);
        plan.executableName = getUpdaterExecutableName(appExeName);
        plan.destinationPath = Path.of(Functions.applicationPath, plan.executableName + ".exe").toString();
        plan.sourceUrl = "http://www.alpha-series.com/upgrades/" + plan.executableName
            + "/file.database?timestamp=" + timestamp.format(DateTimeFormatter.ofPattern("dMuuHms"));
        return plan;
    }

    public boolean formLoad(boolean databaseConnected) {
        try {
            if (!Licence.global_00829048.isEmpty()) {
                if (!databaseConnected) {
                    return false;
                }
                String sqlText = normalizedUpdateSql(Licence.global_00829048);
                for (String line : sqlText.split("\n", -1)) {
                    if (line.trim().length() > 5) {
                        MySQL.Proc_5_1_6D4110(line.trim(), 0, 0);
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
        HeightStep step = new HeightStep();
        step.height = currentHeight;
        if (targetHeight <= 0L) {
            step.timer2Enabled = false;
            return step;
        }
        if (currentHeight < targetHeight) {
            step.height = currentHeight + 50L;
            if (step.height >= targetHeight) {
                step.height = targetHeight;
                step.timer2Enabled = false;
            } else {
                step.timer2Enabled = true;
            }
        } else if (currentHeight > targetHeight) {
            step.height = currentHeight - 50L;
            if (step.height <= targetHeight) {
                step.height = targetHeight;
                step.timer2Enabled = false;
            } else {
                step.timer2Enabled = true;
            }
        } else {
            step.timer2Enabled = false;
        }
        return step;
    }

    public static ProgressStep progressTimerStep(long currentWidth, long targetWidth, boolean renderTimerEnabled) {
        ProgressStep step = new ProgressStep();
        step.width = currentWidth;
        if (targetWidth <= 0L) {
            step.walkPercentEnabled = false;
            return step;
        }
        if (currentWidth < targetWidth) {
            step.width = currentWidth + 50L;
            if (step.width > targetWidth) {
                step.width = targetWidth;
            }
        } else if (currentWidth > targetWidth) {
            step.width = targetWidth;
        }
        step.complete = step.width >= PROGRESS_WIDTH_MAX && !renderTimerEnabled;
        step.walkPercentEnabled = !step.complete;
        return step;
    }

    public static long getUpdateFieldNumber(String[] fields, long fieldIndex) {
        if (fields != null && fieldIndex >= 0L && fieldIndex < fields.length) {
            return Vb.val(fields[(int) fieldIndex]);
        }
        return 0L;
    }

    public static String getUpdaterExecutableName(String configuredName, String appExeName) {
        return !Vb.cStr(configuredName).isEmpty() ? Vb.cStr(configuredName) : Vb.cStr(appExeName);
    }

    public static String getUpdaterExecutableName(String appExeName) {
        return getUpdaterExecutableName(Licence.global_00829040, appExeName);
    }

    public static String normalizedUpdateSql(String updateSql) {
        return Vb.cStr(updateSql).replace("\r", "").replaceAll("(?i)INSERT INTO", "INSERT IGNORE INTO");
    }

    public static String[] visibleBodyLines(String bodyText, int maxLines) {
        String[] rawLines = Vb.cStr(bodyText).split("\\\\n", -1);
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

    public static final class FeatureState {
        public boolean visible = false;
        public String caption = "";
    }
}
