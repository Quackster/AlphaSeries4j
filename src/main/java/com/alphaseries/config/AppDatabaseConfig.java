package com.alphaseries.config;

import com.alphaseries.Crypto;
import com.alphaseries.db.JdbcDatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.LinkedHashMap;
import java.util.Map;

public final class AppDatabaseConfig {
    private AppDatabaseConfig() {
    }

    public static void configureDefaultConnector() {
        Crypto.configureDatabaseConnector(connectionString -> new JdbcDatabase(connectFromOdbcString(connectionString)));
    }

    public static Connection connectFromOdbcString(String connectionString) throws Exception {
        Map<String, String> values = parseOdbcConnectionString(connectionString);
        String host = valueOrDefault(values, "server", "localhost");
        String port = valueOrDefault(values, "port", "3306");
        String database = valueOrDefault(values, "database", "");
        String user = valueOrDefault(values, "user", "root");
        String password = valueOrDefault(values, "password", "");
        return DriverManager.getConnection(jdbcUrl(host, port, database), user, password);
    }

    public static String jdbcUrl(String host, String port, String database) {
        String hostText = text(host);
        String portText = text(port);
        String databaseText = text(database);
        StringBuilder url = new StringBuilder("jdbc:mysql://");
        url.append(hostText.isEmpty() ? "localhost" : hostText);
        url.append(':').append(portText.isEmpty() ? "3306" : portText);
        if (!databaseText.isEmpty()) {
            url.append('/').append(databaseText);
        }
        url.append("?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        return url.toString();
    }

    public static Map<String, String> parseOdbcConnectionString(String connectionString) {
        Map<String, String> result = new LinkedHashMap<String, String>();
        for (String part : text(connectionString).split(";", -1)) {
            int equalsAt = part.indexOf('=');
            if (equalsAt > 0) {
                String key = part.substring(0, equalsAt).trim().toLowerCase();
                String value = part.substring(equalsAt + 1).trim();
                if (value.startsWith("{") && value.endsWith("}") && value.length() >= 2) {
                    value = value.substring(1, value.length() - 1);
                }
                result.put(key, value);
            }
        }
        return result;
    }

    private static String valueOrDefault(Map<String, String> values, String key, String defaultValue) {
        String value = values.get(key);
        return value == null || value.isEmpty() ? defaultValue : value;
    }

    private static String text(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}
