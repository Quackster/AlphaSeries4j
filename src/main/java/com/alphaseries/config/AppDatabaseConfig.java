package com.alphaseries.config;

import com.alphaseries.db.Database;
import com.alphaseries.db.JdbcDatabase;
import com.alphaseries.db.MySQL;
import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.util.StringUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.LinkedHashMap;
import java.util.Map;

public final class AppDatabaseConfig {
    private static DatabaseConnector databaseConnector = connectionString -> null;

    private AppDatabaseConfig() {
    }

    public interface DatabaseConnector {
        Database connect(String connectionString) throws Exception;
    }

    public static void configureDefaultConnector() {
        configureDatabaseConnector(connectionString -> new JdbcDatabase(connectFromOdbcString(connectionString)));
    }

    public static void configureDatabaseConnector(DatabaseConnector connector) {
        databaseConnector = connector == null ? connectionString -> null : connector;
    }

    public static long connectDatabaseFromConfig(String configText) {
        try {
            String connectionString = buildDatabaseConnectionString(configText);
            if (connectionString.isEmpty()) {
                return 0L;
            }
            Database database = databaseConnector.connect(connectionString);
            if (database == null) {
                return 0L;
            }
            MySQL.configureDatabaseConnection(database);
            return 1L;
        } catch (Exception ex) {
            return 0L;
        }
    }

    public static String buildDatabaseConnectionString(String configText) {
        String text = StringUtils.text(configText);
        if (text.isEmpty() || !text.toLowerCase().contains("mysql_")) {
            return "";
        }

        Map<String, String> config = parseConfig(text);
        return buildDatabaseConnectionString(new DatabaseConnectionSettings(
            config.getOrDefault("mysql_host", "localhost"),
            config.getOrDefault("mysql_port", "3306"),
            config.getOrDefault("mysql_db", ""),
            config.getOrDefault("mysql_username", ""),
            config.getOrDefault("mysql_password", ""),
            config.getOrDefault("mysql_driver", "MySQL ODBC 3.51 Driver")));
    }

    public static String buildDatabaseConnectionString(DatabaseConnectionSettings settings) {
        if (settings == null || settings.databaseName().isEmpty() || settings.driverName().isEmpty()) {
            return "";
        }
        return "Driver={" + settings.driverName() + "};Server=" + settings.hostName()
            + ";Port=" + settings.portNumber() + ";Database=" + settings.databaseName()
            + ";User=" + settings.userName() + ";Password=" + settings.password() + ";Option=3;";
    }

    public static Map<String, String> parseConfig(String configText) {
        Map<String, String> result = new LinkedHashMap<>();
        for (String line : StringUtils.delimitedFields(StringUtils.normalizedNewlines(configText), '\n')) {
            int equalsAt = line.indexOf('=');
            if (equalsAt > 0) {
                result.put(line.substring(0, equalsAt).trim().toLowerCase(), line.substring(equalsAt + 1).trim());
            }
        }
        return result;
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
        String hostText = StringUtils.text(host);
        String portText = StringUtils.text(port);
        String databaseText = StringUtils.text(database);
        PacketBuilder url = PacketBuilder.create()
            .appendRaw("jdbc:mysql://")
            .appendRaw(hostText.isEmpty() ? "localhost" : hostText)
            .appendRaw(':')
            .appendRaw(portText.isEmpty() ? "3306" : portText);
        if (!databaseText.isEmpty()) {
            url.appendRaw('/').appendRaw(databaseText);
        }
        return url.appendRaw("?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC").build();
    }

    public static Map<String, String> parseOdbcConnectionString(String connectionString) {
        Map<String, String> result = new LinkedHashMap<String, String>();
        for (String part : StringUtils.delimitedFields(connectionString, ';')) {
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

    public record DatabaseConnectionSettings(
        String hostName,
        String portNumber,
        String databaseName,
        String userName,
        String password,
        String driverName
    ) {
        public DatabaseConnectionSettings {
            hostName = StringUtils.text(hostName);
            portNumber = StringUtils.text(portNumber);
            databaseName = StringUtils.text(databaseName);
            userName = StringUtils.text(userName);
            password = StringUtils.text(password);
            driverName = StringUtils.text(driverName);
        }
    }
}
