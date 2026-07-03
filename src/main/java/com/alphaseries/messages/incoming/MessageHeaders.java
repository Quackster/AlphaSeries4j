package com.alphaseries.messages.incoming;

import com.alphaseries.util.StringUtils;

import java.util.Iterator;
import java.util.List;

public final class MessageHeaders implements Iterable<String> {
    private final List<String> values;

    private MessageHeaders(List<String> values) {
        this.values = List.copyOf(values == null ? List.of() : values);
    }

    public static MessageHeaders of(String header) {
        String value = StringUtils.text(header);
        return value.isEmpty() ? empty() : new MessageHeaders(List.of(value));
    }

    public static MessageHeaders empty() {
        return new MessageHeaders(List.of());
    }

    @Override
    public Iterator<String> iterator() {
        return values.iterator();
    }
}
