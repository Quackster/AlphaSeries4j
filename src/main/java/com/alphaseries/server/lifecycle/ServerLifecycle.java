package com.alphaseries.server.lifecycle;

import com.alphaseries.config.AppPaths;
import com.alphaseries.config.AppDatabaseConfig;
import com.alphaseries.dao.mysql.ServerMaintenanceDao;
import com.alphaseries.db.Database;
import com.alphaseries.db.MySQL;
import com.alphaseries.protocol.WireEncoding;
import com.alphaseries.server.runtime.Guardian;
import com.alphaseries.util.FileUtils;
import com.alphaseries.util.StringLines;
import com.alphaseries.util.StringUtils;

import java.nio.file.Path;

public final class ServerLifecycle {
    public static final String INITIALIZING_CAPTION_TEMPLATE = "Alpha Series [INITIALISIERE] - [%%]";
    public static final String INITIALIZED_STATE_TEXT = "INITIALISIERT";
    public static final String RUNNING_STATE_TEXT = "RUNNING";
    public static final String SERVER_EXIT_ERROR_PREFIX = "Server Exit Suburned following error: \r\n";
    public static final String UNKNOWN_PROBLEM_MESSAGE = "Unbekanntes Problem";
    private static final StringLines DESIGN_CAPTIONS = StringLines.of(
        "Bitte warte...",
        "frame :: ADDONS",
        "Server by Privilege",
        "User Voice",
        "Source is only avaible for the author. Please do not share this Source!"
    );

    private ServerLifecycle() {
    }

    public record LifecycleResult(
        boolean success,
        boolean shouldExit,
        String caption,
        String consoleTitle,
        String productKey
    ) {
        public LifecycleResult {
            caption = StringUtils.text(caption);
            consoleTitle = StringUtils.text(consoleTitle);
            productKey = StringUtils.text(productKey);
        }

        public static LifecycleResult exitRequested() {
            return new LifecycleResult(false, true, "", "", "");
        }

        public static LifecycleResult initialized(String caption, String consoleTitle, String productKey) {
            return new LifecycleResult(true, false, caption, consoleTitle, productKey);
        }
    }

    public record ResizeResult(long width, long height, long logWidth, long logHeight, long frameWidth) {
    }

    public record StartupResult(boolean success, String stage, String message) {
        public StartupResult {
            stage = StringUtils.text(stage);
            message = StringUtils.text(message);
        }

        public static StartupResult succeeded() {
            return new StartupResult(true, "", "");
        }

        public static StartupResult failure(String stage, String message) {
            return new StartupResult(false, stage, message);
        }
    }

    /**
     * Original function: Proc_0_24_68EEF0.
     */
    public static void runRecoveredStartupNoop() {
        // Empty in the recovered VB6 reference.
    }

    public static LifecycleResult formInitialize(String captionTemplate) {
        try {
            LifecycleState.instance().resetRuntimeDefaults();
            if (WireEncoding.decodeVl64("K") != 3L) {
                return LifecycleResult.exitRequested();
            }
            Guardian.deleteDirectory(Path.of(AppPaths.applicationPath(), "CACHE", "ROOMS"));
            Guardian.deleteDirectory(Path.of(AppPaths.applicationPath(), "CACHE", "PATHFINDER"));
            Guardian.deleteDirectory(Path.of(AppPaths.applicationPath(), "CACHE", "USERS"));
            String consoleTitle = StringUtils.text(captionTemplate)
                .replace("%%", LifecycleState.instance().runtimeState().productName());
            String caption = javaCaptionFromConsoleTitle(consoleTitle);
            String productKey = productKeyFromConfig(FileUtils.readTextFile(
                Path.of(AppPaths.applicationPath(), "config.ini").toString()));
            return LifecycleResult.initialized(caption, consoleTitle, productKey);
        } catch (Exception ignored) {
            return LifecycleResult.exitRequested();
        }
    }

    public static boolean formQueryUnload() {
        try {
            ServerMaintenanceDao maintenanceDao = serverMaintenanceDao();
            if (maintenanceDao != null) {
                maintenanceDao.resetConnectedUsers();
                maintenanceDao.resetOccupiedRoomSlots();
            }
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static boolean runServer(String caption, String licenceResponse) {
        try {
            if (LicenceChecker.applyLicenceResponse(licenceResponse, LicenceChecker.LICENCE_TIME_FORMAT, 0L)) {
                StartupCacheCoordinator.initializeStartupCaches();
                return true;
            }
            return false;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static boolean runServer(LifecycleResult lifecycle) {
        return startServer(lifecycle).success();
    }

    public static StartupResult startServer(LifecycleResult lifecycle) {
        if (lifecycle == null) {
            return StartupResult.failure("lifecycle", "Lifecycle initialization did not return a result.");
        }
        try {
            if (LicenceChecker.checkLicence(
                new LicenceChecker.LicenceCheckContext(
                    lifecycle.productKey(),
                    LifecycleState.instance().runtimeState().productName()))) {
                StartupCacheCoordinator.initializeStartupCaches();
                return StartupResult.succeeded();
            }
            String message = LicenceChecker.lastLicenceFailureMessage();
            if (message.isEmpty()) {
                message = "Licence check failed for product key '" + lifecycle.productKey() + "'.";
            }
            return StartupResult.failure("licence", message);
        } catch (Exception ex) {
            return StartupResult.failure("server", ex.getMessage());
        }
    }

    public static String getProcessor() {
        return StringUtils.text(System.getenv("USERNAME")).isEmpty()
            ? StringUtils.text(System.getenv("USER"))
            : StringUtils.text(System.getenv("USERNAME"));
    }

    public static String productKeyFromConfig(String configText) {
        java.util.Map<String, String> config = AppDatabaseConfig.parseConfig(configText);
        String productKey = config.get("productkey");
        if (productKey == null || productKey.isEmpty()) {
            productKey = config.get("licence");
        }
        if (productKey != null) {
            return productKey;
        }

        String fallbackProductKey = StringUtils.indexedFields(configText, '=').text(7);
        if (fallbackProductKey.isEmpty()) {
            return "";
        }
        return firstLine(fallbackProductKey);
    }

    public static String javaCaptionFromConsoleTitle(String consoleTitle) {
        return StringUtils.text(consoleTitle).replace("[!]", "").trim().replaceAll(" {2,}", " ");
    }

    public static String initializedConsoleTitle(String consoleTitle) {
        String title = StringUtils.text(consoleTitle);
        title = title.replace("INITIALISIERE", INITIALIZED_STATE_TEXT);
        title = title.replace("INITIALIZING", RUNNING_STATE_TEXT);
        return title;
    }

    public static StringLines designCaptions() {
        return DESIGN_CAPTIONS;
    }

    public static String serverExitErrorMessage(String description) {
        return SERVER_EXIT_ERROR_PREFIX + StringUtils.text(description);
    }

    public static ResizeResult formResize(long width, long height, long scaleWidth, long scaleHeight) {
        return new ResizeResult(
            Math.max(width, 11085L),
            Math.max(height, 10245L),
            scaleWidth,
            scaleHeight - 525L,
            scaleWidth);
    }

    private static ServerMaintenanceDao serverMaintenanceDao() {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : new ServerMaintenanceDao(database);
    }

    private static String firstLine(String text) {
        String value = StringUtils.text(text);
        int lineEnd = value.length();
        int carriageReturnAt = value.indexOf('\r');
        if (carriageReturnAt >= 0) {
            lineEnd = Math.min(lineEnd, carriageReturnAt);
        }
        int lineFeedAt = value.indexOf('\n');
        if (lineFeedAt >= 0) {
            lineEnd = Math.min(lineEnd, lineFeedAt);
        }
        return value.substring(0, lineEnd);
    }
}
