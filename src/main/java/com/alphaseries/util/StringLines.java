package com.alphaseries.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class StringLines implements Iterable<String> {
    private final List<String> lines;

    private StringLines(Iterable<String> lines) {
        List<String> copiedLines = new ArrayList<>();
        if (lines != null) {
            for (String line : lines) {
                copiedLines.add(StringUtils.text(line));
            }
        }
        this.lines = List.copyOf(copiedLines);
    }

    public static StringLines from(Iterable<String> lines) {
        return new StringLines(lines);
    }

    public static StringLines of(String... lines) {
        List<String> copiedLines = new ArrayList<>();
        if (lines != null) {
            for (String line : lines) {
                copiedLines.add(StringUtils.text(line));
            }
        }
        return new StringLines(copiedLines);
    }

    public static StringLines empty() {
        return new StringLines(List.of());
    }

    public boolean isEmpty() {
        return lines.isEmpty();
    }

    public int size() {
        return lines.size();
    }

    public String lineAt(int index) {
        return index < 0 || index >= lines.size() ? "" : lines.get(index);
    }

    @Override
    public Iterator<String> iterator() {
        return lines.iterator();
    }
}
