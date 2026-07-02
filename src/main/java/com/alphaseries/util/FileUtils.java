package com.alphaseries.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class FileUtils {
    private FileUtils() {
    }

    public static boolean exists(String path) {
        return Files.exists(Path.of(StringUtils.text(path)));
    }

    /**
     * Original function: Proc_6_239_7FC170.
     */
    public static String readTextFile(String path) {
        if (StringUtils.text(path).isEmpty()) {
            return "";
        }
        Path filePath = Path.of(StringUtils.text(path));
        if (!Files.exists(filePath)) {
            return "";
        }
        try {
            return Files.readString(filePath, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            return "";
        }
    }

    public static void appendTextFile(String path, String text) {
        try {
            Files.writeString(Path.of(StringUtils.text(path)), StringUtils.text(text) + System.lineSeparator(),
                StandardCharsets.UTF_8, java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
        } catch (IOException ignored) {
            // VB6 source suppresses append failures.
        }
    }

    public static void writeTextFile(String path, String text) {
        try {
            Path filePath = Path.of(StringUtils.text(path));
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(filePath, StringUtils.text(text) + System.lineSeparator(), StandardCharsets.UTF_8);
        } catch (IOException ignored) {
            // VB6 source suppresses write failures.
        }
    }

    /**
     * Original function: Proc_6_106_74B750.
     */
    public static void deleteFile(String path) {
        try {
            if (!StringUtils.text(path).isEmpty()) {
                Files.deleteIfExists(Path.of(StringUtils.text(path)));
            }
        } catch (Exception ignored) {
            // VB6 source suppresses file delete failures.
        }
    }

    public static String ensureTextFile(String path) {
        if (StringUtils.text(path).isEmpty()) {
            return "";
        }
        if (!Files.exists(Path.of(StringUtils.text(path)))) {
            writeTextFile(path, "");
        }
        return readTextFile(path);
    }

    /**
     * Original function: Proc_10_28_8210C0.
     */
    public static boolean downloadFile(String sourceUrl, String destinationPath) {
        try (InputStream input = new URL(StringUtils.text(sourceUrl)).openStream()) {
            Files.copy(input, Path.of(StringUtils.text(destinationPath)), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
