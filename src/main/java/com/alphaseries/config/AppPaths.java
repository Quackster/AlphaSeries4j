package com.alphaseries.config;

import com.alphaseries.util.StringUtils;

import java.nio.file.Path;

public final class AppPaths {
    private static String applicationPath = Path.of("").toAbsolutePath().toString();

    private AppPaths() {
    }

    public static synchronized String applicationPath() {
        return applicationPath;
    }

    public static synchronized void setApplicationPath(String path) {
        String value = StringUtils.text(path);
        applicationPath = value.isEmpty() ? Path.of("").toAbsolutePath().toString() : value;
    }
}
