package com.alphaseries;

import com.alphaseries.vb.Vb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public final class PrivSockHTTP {
    private PrivSockHTTP() {
    }

    public static String buildGetRequest(String requestPath, String requestHost, String requestPort) {
        if (Vb.cStr(requestPath).isEmpty() || Vb.cStr(requestHost).isEmpty()) {
            return "";
        }
        String hostPort = "";
        if (!Vb.cStr(requestPort).isEmpty() && Vb.val(requestPort) != 80L) {
            hostPort = ":" + Vb.val(requestPort);
        }
        return "GET " + requestPath + " HTTP/1.1\r\n"
            + "Host:   " + requestHost + hostPort + "\r\n"
            + "Connection:   keep-alive\r\n"
            + "Accept:   application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5\r\n"
            + "User-Agent:   FireFox/1.0\r\n"
            + "Accept-Language:   en-US,en;q=0.8;q=0.6,en;q=0.4\r\n"
            + "Accept-Charset:   ISO-8859-1,utf-8;q=0.7,*;q=0.3\r\n\r\n";
    }

    public static String readHTTP(Object url, Object action) {
        String urlText = Vb.cStr(url);
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
