package com.alphaseries;

import com.alphaseries.db.Database;
import com.alphaseries.protocol.WireEncoding;

import java.util.LinkedHashMap;
import java.util.Map;

public final class Crypto {
    private static DatabaseConnector databaseConnector = connectionString -> null;

    private Crypto() {
    }

    public interface DatabaseConnector {
        Database connect(String connectionString) throws Exception;
    }

    public static void configureDatabaseConnector(DatabaseConnector connector) {
        databaseConnector = connector == null ? connectionString -> null : connector;
    }

    /**
     * Original function: Proc_3_0_6D2AF0.
     */
    public static String Proc_3_0_6D2AF0(Object... args) {
        if (args == null || args.length == 0) {
            return "";
        }
        long value = WireEncoding.parseLeadingLong(args[0]);
        String prefix = args.length >= 3 ? text(args[2]) : "";
        return prefix + WireEncoding.encodeVl64(value);
    }

    /**
     * Original function: Proc_3_1_6D2E00.
     */
    public static long Proc_3_1_6D2E00(Object... args) {
        if (args == null || args.length == 0) {
            return 0L;
        }
        String valueText = text(args[0]).replace('.', ',');
        int comma = valueText.indexOf(',');
        if (comma >= 0) {
            valueText = valueText.substring(0, comma);
        }
        return WireEncoding.parseLeadingLong(valueText) + 1L;
    }

    /**
     * Original function: Proc_3_2_6D30A0.
     */
    public static long Proc_3_2_6D30A0(Object... args) {
        if (args == null || args.length == 0) {
            return 0L;
        }
        return encodedVl64LengthByteCount(text(args[0]));
    }

    /**
     * Original function: Proc_3_2_6D30A0.
     */
    public static long encodedVl64LengthByteCount(String encodedValue) {
        String value = text(encodedValue);
        if (value.isEmpty()) {
            return 0L;
        }
        long firstByte = value.charAt(0) - 72L;
        return (firstByte / 8L) + 1L;
    }

    /**
     * Original function: Proc_3_3_6D3240.
     */
    public static long Proc_3_3_6D3240(Object... args) {
        if (args == null || args.length == 0) {
            return 0L;
        }
        return decodeVl64(text(args[0]));
    }

    /**
     * Original function: Proc_3_4_6D3620.
     */
    public static long Proc_3_4_6D3620(Object... args) {
        if (args == null || args.length == 0) {
            return 0L;
        }
        return decodeBase64Length(text(args[0]));
    }

    public static String buildDatabaseConnectionString(Object... args) {
        if (args == null || args.length == 0) {
            return "";
        }

        String configText = text(args[0]);
        String hostName = "";
        String portNumber = "";
        String databaseName = "";
        String userName = "";
        String password = "";
        String driverName = "";

        if (configText.toLowerCase().contains("mysql_")) {
            Map<String, String> config = parseConfig(configText);
            hostName = config.getOrDefault("mysql_host", "localhost");
            portNumber = config.getOrDefault("mysql_port", "3306");
            databaseName = config.getOrDefault("mysql_db", "");
            userName = config.getOrDefault("mysql_username", "");
            password = config.getOrDefault("mysql_password", "");
            driverName = config.getOrDefault("mysql_driver", "MySQL ODBC 3.51 Driver");
        } else if (args.length >= 6) {
            hostName = text(args[0]);
            portNumber = text(args[1]);
            databaseName = text(args[2]);
            userName = text(args[3]);
            password = text(args[4]);
            driverName = text(args[5]);
        } else if (args.length >= 5) {
            hostName = "localhost";
            portNumber = text(args[0]);
            databaseName = text(args[1]);
            userName = text(args[2]);
            password = text(args[3]);
            driverName = text(args[4]);
        }

        if (databaseName.isEmpty() || driverName.isEmpty()) {
            return "";
        }
        return "Driver={" + driverName + "};Server=" + hostName + ";Port=" + portNumber
            + ";Database=" + databaseName + ";User=" + userName + ";Password=" + password + ";Option=3;";
    }

    /**
     * Original function: Proc_3_5_6D3880.
     */
    public static long Proc_3_5_6D3880(Object... args) {
        return connectDatabaseFromConfig(args);
    }

    /**
     * Original function: Proc_3_5_6D3880.
     */
    public static long connectDatabaseFromConfig(Object... args) {
        try {
            String connectionString = buildDatabaseConnectionString(args);
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

    public static Map<String, String> parseConfig(String configText) {
        Map<String, String> result = new LinkedHashMap<>();
        String normalized = text(configText).replace("\r\n", "\n").replace('\r', '\n');
        for (String line : normalized.split("\n", -1)) {
            int equalsAt = line.indexOf('=');
            if (equalsAt > 0) {
                result.put(line.substring(0, equalsAt).trim().toLowerCase(), line.substring(equalsAt + 1).trim());
            }
        }
        return result;
    }

    /**
     * Original function: Proc_3_0_6D2AF0.
     */
    public static String encodeVl64(long value) {
        return WireEncoding.encodeVl64(value);
    }

    /**
     * Original function: Proc_3_3_6D3240.
     */
    public static long decodeVl64(String encodedValue) {
        return WireEncoding.decodeVl64(encodedValue);
    }

    /**
     * Original function: Proc_3_4_6D3620.
     */
    public static long decodeBase64Length(String encodedValue) {
        return WireEncoding.decodeBase64Length(encodedValue);
    }

    private static String text(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}
