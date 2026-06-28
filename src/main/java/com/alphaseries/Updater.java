package com.alphaseries;

import com.alphaseries.vb.Vb;

public final class Updater {
    public static final long PROGRESS_WIDTH_MAX = 11535L;

    public long pendingHeightTarget = 0L;
    public long pendingAnimationInterval = 0L;
    public long pendingProgressWidth = 0L;
    public long currentUpdateIndex = 0L;
    public boolean timer1Enabled = false;
    public boolean timer2Enabled = false;
    public boolean walkPercentEnabled = false;

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

    public static final class FeatureState {
        public boolean visible = false;
        public String caption = "";
    }
}
