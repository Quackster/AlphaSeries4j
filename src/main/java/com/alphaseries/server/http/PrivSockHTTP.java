package com.alphaseries.server.http;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public final class PrivSockHTTP {
    private PrivSockHTTP() {
    }

    public static final class AliveState {
        private long ticks;
        private boolean enabled = true;
        private String responseBuffer = "";
        private String requestPath = "";
        private String requestHost = "";
        private String requestPort = "";

        public static AliveState request(String requestPath, String requestHost, String requestPort) {
            AliveState state = new AliveState();
            state.requestPath = StringUtils.text(requestPath);
            state.requestHost = StringUtils.text(requestHost);
            state.requestPort = StringUtils.text(requestPort);
            return state;
        }

        public long ticks() {
            return ticks;
        }

        public boolean enabled() {
            return enabled;
        }

        public void setTicks(long ticks) {
            this.ticks = Math.max(0L, ticks);
        }
    }

    public static String tmrCheckAliveTimer(AliveState state) {
        if (state == null || !state.enabled) {
            return "";
        }
        if (state.ticks >= 200L) {
            state.enabled = false;
            return "";
        }
        state.ticks++;
        if ("-1".equals(state.responseBuffer) || state.requestPath.isEmpty() || state.requestHost.isEmpty()) {
            return "";
        }
        state.responseBuffer = "";
        return buildGetRequest(state.requestPath, state.requestHost, state.requestPort);
    }

    public static String buildGetRequest(String requestPath, String requestHost, String requestPort) {
        String path = StringUtils.text(requestPath);
        String host = StringUtils.text(requestHost);
        String portText = StringUtils.text(requestPort);
        if (path.isEmpty() || host.isEmpty()) {
            return "";
        }
        String hostPort = "";
        long port = NumberUtils.parseLong(portText);
        if (!portText.isEmpty() && port != 80L) {
            hostPort = ":" + port;
        }
        return "GET " + path + " HTTP/1.1\r\n"
            + "Host:   " + host + hostPort + "\r\n"
            + "Connection:   keep-alive\r\n"
            + "Accept:   application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5\r\n"
            + "User-Agent:   FireFox/1.0\r\n"
            + "Accept-Language:   en-US,en;q=0.8;q=0.6,en;q=0.4\r\n"
            + "Accept-Charset:   ISO-8859-1,utf-8;q=0.7,*;q=0.3\r\n\r\n";
    }

    public static String readHTTP(String url) {
        String urlText = StringUtils.text(url);
        if (urlText.isEmpty()) {
            return "";
        }
        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(new URL(urlText).openStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (response.length() > 0) {
                    response.append('\n');
                }
                response.append(line);
            }
            return response.toString();
        } catch (IOException ex) {
            return "";
        }
    }
}
