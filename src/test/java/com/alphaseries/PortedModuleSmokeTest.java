package com.alphaseries;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PortedModuleSmokeTest {
    private PortedModuleSmokeTest() {
    }

    public static void main(String[] args) throws Exception {
        assertEquals(0L, Crypto.decodeVl64(Crypto.encodeVl64(0)));
        assertEquals(1L, Crypto.decodeVl64(Crypto.encodeVl64(1)));
        assertEquals(42L, Crypto.decodeVl64(Crypto.encodeVl64(42)));
        assertEquals(-42L, Crypto.decodeVl64(Crypto.encodeVl64(-42)));
        assertEquals(4096L, Crypto.decodeVl64(Crypto.encodeVl64(4096)));
        assertEquals(1L, Crypto.Proc_3_2_6D30A0(Crypto.encodeVl64(0)));
        assertEquals(64L, Crypto.Proc_3_4_6D3620("@\u0080"));

        String config = "mySQL_host=db\r\nmySQL_port=3307\nmySQL_db=alpha\nmySQL_username=user\nmySQL_password=pass";
        assertEquals("Driver={MySQL ODBC 3.51 Driver};Server=db;Port=3307;Database=alpha;User=user;Password=pass;Option=3;",
            Crypto.buildDatabaseConnectionString(config));
        final List<String> connectionStrings = new ArrayList<>();
        Crypto.configureDatabaseConnector(connectionString -> {
            connectionStrings.add(connectionString);
            return new Database() {
                @Override
                public void execute(String sqlText) {
                }

                @Override
                public List<List<Object>> query(String sqlText) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList("connected"));
                }
            };
        });
        assertEquals(1L, Crypto.Proc_3_5_6D3880(config));
        assertEquals(Crypto.buildDatabaseConnectionString(config), connectionStrings.get(0));
        assertEquals("connected", MySQL.Proc_5_2_6D4690("SELECT 1"));
        assertEquals(0L, Crypto.Proc_3_5_6D3880(""));
        Crypto.configureDatabaseConnector(connectionString -> {
            throw new IllegalStateException("connect failed");
        });
        assertEquals(0L, Crypto.Proc_3_5_6D3880(config));
        Crypto.configureDatabaseConnector(null);
        MySQL.configureDatabaseConnection(null);

        DataManager.global_008291AC = "\0chair\1id\2name\2price\0";
        assertEquals("name", DataManager.Proc_8_11_8069B0("chair", 1));
        DataManager.global_008292BC = "10\tsofa\t5\r11\ttable\t7";
        assertEquals("table", DataManager.Proc_8_12_806C30(11, 1));
        assertEquals("pro", DataManager.extractLicenceSetting("\rrank=7\rmode:pro\r", "mode"));
        assertEquals("rank=7\rmode\nok", DataManager.licenceBlockFromResponse("aFMTbFMTcFMTrank=7--*-mode*-*-ok", "FMT"));
        assertEquals("fallback", DataManager.licenceBlockFromResponse("prefixFMTfallback", "FMT"));
        assertEquals("\rrank=7\rmode=pro\r", DataManager.licenceCacheTextFromBlock("rank=7\nmode=pro"));
        assertEquals("reason text", DataManager.blockedLicenceMessage("{BLOCKED reason%20text}"));
        assertEquals("5AZ12675B12870", DataManager.buildLicenceToken("AB", 3, 65, "ZB"));
        assertEquals("AB", DataManager.Proc_8_5_804AB0("YCD"));
        assertEquals(true, DataManager.applyLicenceResponse("rank=2\r7:2=5\r8:2=1", "FMT", 0));
        assertEquals(2, DataManager.global_00829054);
        assertEquals(5, DataManager.global_00829068[7]);
        assertEquals(1, DataManager.global_00829068[8]);
        assertEquals("", DataManager.lastLicenceFailureMessage);
        assertEquals(true, DataManager.licenceChecksumValid("12345678" + "100000" + "-20-99980", 0));
        assertEquals(false, DataManager.licenceChecksumValid("12345678" + "100000" + "-20-99981", 0));
        assertEquals(false, DataManager.applyLicenceResponse("12345678" + "100000" + "-20-99981", "FMT", 0));
        assertEquals("Das Lizenzsystem ist zurzeit nicht erreichbar. Versuch es sp\u00e4ter wieder!",
            DataManager.lastLicenceFailureMessage);
        assertEquals(false, DataManager.applyLicenceResponse("{BLOCKED no%20licence}", "FMT", 0));

        assertEquals("SELECT * FROM users", MySQL.buildSqlFromArgs("SELECT ", 0, "*", -1, " FROM users"));
        assertEquals(42, MySQL.mySqlSocketIndex("42"));
        assertEquals(0, MySQL.mySqlSocketIndex());
        assertEquals("third", MySQL.mySqlPacketPayload(1, "second", "third"));
        assertEquals("second", MySQL.mySqlPacketPayload(1, "second", ""));
        assertEquals("payload", MySQL.mySqlRequestPayload("GHpayload", "GH"));
        assertEquals("GKpayload", MySQL.mySqlRequestPayload("GKpayload", "GH"));
        Functions.global_008292A8 = new String[][]{{""}, {"\2fuse_mod\2fuse_chatlog\2fuse_receive_calls_for_help\2"}};
        final List<String> mysqlHandlerPayloads = new ArrayList<>();
        HandlingMUS.configureMusSink((socketIndex, payload) -> mysqlHandlerPayloads.add(socketIndex + ":" + payload));
        MySQL.configureDatabaseConnection(new Database() {
            @Override
            public void execute(String sqlText) {
            }

            @Override
            public List<List<Object>> query(String sqlText) {
                if (sqlText.contains("SELECT id FROM users WHERE id_socket='4'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(77));
                }
                if (sqlText.contains("SELECT level_hc FROM users")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(0));
                }
                if (sqlText.contains("SELECT level_hc,hc_days,hc2_days,hc_presents")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(2, 40, 80, 1, 10));
                }
                if (sqlText.contains("SELECT level FROM users")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(1));
                }
                if (sqlText.contains("staff_cfh WHERE staff_cfh.id='12'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(9, "Help room", 2, 501, 502, 2000));
                }
                if (sqlText.contains("logs_chat.id_room='9'") && sqlText.contains("logs_chat.timestamp < 2000")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(10, 20, 501, "alice", "help"));
                }
                if (sqlText.contains("rooms.id='12'") && sqlText.contains("models.id=rooms.id_model")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(12, "Chat room", 3));
                }
                if (sqlText.contains("logs_chat.id_room='12'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(11, 21, 502, "bob", "chat"));
                }
                if (sqlText.contains("rooms.id='13'") && sqlText.contains("users.id=rooms.id_owner")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(13, 4, 88, "owner", "Room", "Desc", "tag1", "tag2"));
                }
                if (sqlText.contains("rooms_events WHERE id_room='13'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList("Event", "Event desc", "e1", "e2"));
                }
                return Arrays.<List<Object>>asList(Arrays.<Object>asList(0));
            }
        });
        String roomId12Field = "B12";
        String roomId13Field = "B13";
        MySQL.Proc_5_4_6D55E0(4, "GI" + roomId12Field);
        assertEquals(true, mysqlHandlerPayloads.get(0).startsWith("4:DATA\6" + "4\6HV"));
        MySQL.Proc_5_5_6D64D0(4, "GH" + roomId12Field);
        assertEquals(true, mysqlHandlerPayloads.get(1).startsWith("4:DATA\6" + "4\6HW"));
        MySQL.Proc_5_6_6D7090(4, "GK" + roomId13Field);
        assertEquals(true, mysqlHandlerPayloads.get(2).startsWith("4:DATA\6" + "4\6HZ"));
        assertEquals("77", MySQL.mySqlUserIdFromSocket(4));
        assertEquals(true, MySQL.mySqlUserHasPermission("77", "fuse_chatlog"));
        MySQL.configureDatabaseConnection(null);
        HandlingMUS.configureMusSink(null);
        List<List<Object>> rows = Arrays.asList(
            new ArrayList<Object>(Arrays.<Object>asList(1, "alice")),
            new ArrayList<Object>(Arrays.<Object>asList(2, "bob")));
        assertEquals("1\talice\r2\tbob", MySQL.formatSqlRows(rows));
        Functions.global_0082928C = "[server.port=1234]\nname=alpha";
        assertEquals("1234", Functions.Proc_10_0_809570("server.port", "0"));
        assertEquals("fallback", Functions.Proc_10_0_809570("missing", "fallback"));
        Functions.global_008292A8 = new String[][]{{"\2base\2"}, {"\2fuse_mod\2"}};
        assertEquals(true, Functions.Proc_10_1_809790(1, "", "fuse_mod", 0));
        assertEquals("bcd", Functions.Proc_10_5_809D80("abcdef", 2, 3));
        assertEquals("O''Reilly test ", Functions.Proc_10_11_80A9C0("O'Reilly\\rtest\""));
        assertEquals("\1" + "123\t45\tdata\t6\2", Functions.inventoryCacheRecord(123, 45, "data", 6));
        assertEquals("x", Functions.trimInventoryCache("x\r\n"));
        String inventoryCache = Functions.inventoryCacheAddRecord("x\r\n", 123, 45, "data", 6);
        assertEquals("x\1" + "123\t45\tdata\t6\2", inventoryCache);
        assertEquals(inventoryCache, Functions.inventoryCacheAddRecord(inventoryCache, 123, 99, "other", 1));
        assertEquals("x", Functions.inventoryCacheRemoveRecord(inventoryCache, 123));
        assertEquals(inventoryCache, Functions.inventoryCacheRemoveRecord(inventoryCache, 999));
        assertEquals("Ab" + Handling.inventoryItemPayload(123, 45, "data", 6) + '\1',
            Functions.inventoryAddPayload(123, 45, "data", 6));
        assertEquals(Crypto.Proc_3_0_6D2AF0(123, null, "Ac"), Functions.inventoryRemovePayload(123));
        Path inventoryRoot = Files.createTempDirectory("alphaseries-inventory");
        String previousApplicationPath = Functions.applicationPath;
        Functions.applicationPath = inventoryRoot.toString();
        final List<String> inventoryRefreshPayloads = new ArrayList<>();
        Licence.global_00829268 = "\2" + "200]33\0";
        HandlingMUS.configureMusSink((socketIndex, payload) -> inventoryRefreshPayloads.add(socketIndex + ":" + payload));
        MySQL.configureDatabaseConnection(new Database() {
            @Override
            public void execute(String sqlText) {
            }

            @Override
            public List<List<Object>> query(String sqlText) {
                if (sqlText.contains("id ='501'") && sqlText.contains("id_secondary")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(45, 200, "data", 6));
                }
                if (sqlText.contains("id ='501'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(45, 200, "data"));
                }
                return Arrays.<List<Object>>asList(Arrays.<Object>asList());
            }
        });
        assertEquals(1L, Functions.Proc_10_14_80B010(501));
        Path inventoryCachePath = inventoryRoot.resolve("cache").resolve("users").resolve("200.cache");
        assertEquals(Functions.inventoryCacheRecord(501, 45, "data", 6) + System.lineSeparator(),
            new String(Files.readAllBytes(inventoryCachePath), "UTF-8"));
        assertEquals("33:DATA\6" + "33\6" + Functions.inventoryAddPayload(501, 45, "data", 6) + "\7",
            inventoryRefreshPayloads.get(0));
        assertEquals(1L, Functions.Proc_10_15_80BA40(501));
        assertEquals(System.lineSeparator(), new String(Files.readAllBytes(inventoryCachePath), "UTF-8"));
        assertEquals("33:DATA\6" + "33\6" + Functions.inventoryRemovePayload(501) + "\7", inventoryRefreshPayloads.get(1));
        MySQL.configureDatabaseConnection(null);
        HandlingMUS.configureMusSink(null);
        Licence.global_00829268 = "";
        Functions.applicationPath = previousApplicationPath;
        assertEquals("@F250.0\2", Functions.creditsRefreshPayload(250));
        assertEquals(Crypto.Proc_3_0_6D2AF0(2, null, Crypto.Proc_3_0_6D2AF0(300, null, "Fv") + "H"),
            Functions.activityPointRefreshPayload(2, 300));
        String expectedPointRefreshes = "";
        for (int pointType = 0; pointType <= 4; pointType++) {
            expectedPointRefreshes += Functions.activityPointRefreshPayload(pointType, pointType * 10L);
        }
        assertEquals(expectedPointRefreshes, Functions.activityPointRefreshPayloads(0, 10, 20, 30, 40));
        final List<String> refreshQueries = new ArrayList<>();
        final List<String> refreshPayloads = new ArrayList<>();
        Licence.global_00829268 = "\2" + "77]42\0";
        HandlingMUS.configureMusSink((socketIndex, payload) -> refreshPayloads.add(socketIndex + ":" + payload));
        MySQL.configureDatabaseConnection(new Database() {
            @Override
            public void execute(String sqlText) {
            }

            @Override
            public List<List<Object>> query(String sqlText) {
                refreshQueries.add(sqlText);
                if (sqlText.contains("SELECT credits")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(250));
                }
                for (int pointType = 0; pointType <= 4; pointType++) {
                    if (sqlText.contains("activitypoints_" + pointType)) {
                        return Arrays.<List<Object>>asList(Arrays.<Object>asList(pointType * 10));
                    }
                }
                return Arrays.<List<Object>>asList(Arrays.<Object>asList(0));
            }
        });
        assertEquals(1L, Functions.Proc_10_16_80C480("77"));
        assertEquals("42:DATA\6" + "42\6" + Functions.creditsRefreshPayload(250) + "\7", refreshPayloads.get(0));
        assertEquals(5L, Functions.Proc_10_17_80C6B0("77"));
        assertEquals(6, refreshPayloads.size());
        assertEquals("42:DATA\6" + "42\6" + Functions.activityPointRefreshPayload(4, 40) + "\7", refreshPayloads.get(5));
        assertEquals(0L, Functions.Proc_10_16_80C480("missing"));
        assertEquals(6, refreshPayloads.size());
        MySQL.configureDatabaseConnection(null);
        HandlingMUS.configureMusSink(null);
        Licence.global_00829268 = "";
        final List<String> readyPayloads = new ArrayList<>();
        HandlingMUS.configureMusSink((socketIndex, payload) -> readyPayloads.add(socketIndex + ":" + payload));
        MySQL.configureDatabaseConnection(new Database() {
            @Override
            public void execute(String sqlText) {
            }

            @Override
            public List<List<Object>> query(String sqlText) {
                if (sqlText.contains("logs_visitedrooms.id_room='77'")) {
                    return Arrays.asList(
                        Arrays.<Object>asList(5),
                        Arrays.<Object>asList(5),
                        Arrays.<Object>asList(0),
                        Arrays.<Object>asList(6));
                }
                return Arrays.<List<Object>>asList(Arrays.<Object>asList(0));
            }
        });
        assertEquals(2L, Functions.Proc_10_18_80C9E0(77));
        assertEquals("5:DATA\6" + "5\6@R\7", readyPayloads.get(0));
        assertEquals("6:DATA\6" + "6\6@R\7", readyPayloads.get(1));
        assertEquals(0L, Functions.Proc_10_18_80C9E0(0));
        MySQL.configureDatabaseConnection(null);
        HandlingMUS.configureMusSink(null);
        assertEquals("Baalert\2hello\2", Functions.roomAlertPayload("alert", "hello"));
        final List<String> alertPayloads = new ArrayList<>();
        Licence.global_00829268 = "\2" + "81]9\0";
        HandlingMUS.configureMusSink((socketIndex, payload) -> alertPayloads.add(socketIndex + ":" + payload));
        assertEquals(1L, Functions.Proc_10_20_80CF60("81", "notice", "hello"));
        assertEquals("9:DATA\6" + "9\6" + Functions.roomAlertPayload("notice", "hello") + "\7", alertPayloads.get(0));
        assertEquals(0L, Functions.Proc_10_20_80CF60("missing", "notice", "hello"));
        MySQL.configureDatabaseConnection(new Database() {
            @Override
            public void execute(String sqlText) {
            }

            @Override
            public List<List<Object>> query(String sqlText) {
                if (sqlText.contains("logs_visitedrooms.id_room='55'")) {
                    return Arrays.asList(
                        Arrays.<Object>asList(9),
                        Arrays.<Object>asList(9),
                        Arrays.<Object>asList(0),
                        Arrays.<Object>asList(10));
                }
                return Arrays.<List<Object>>asList(Arrays.<Object>asList(0));
            }
        });
        assertEquals(2L, Functions.Proc_10_21_80D0A0(55, "room", "message"));
        assertEquals("9:DATA\6" + "9\6" + Functions.roomAlertPayload("room", "message") + "\7", alertPayloads.get(1));
        assertEquals("10:DATA\6" + "10\6" + Functions.roomAlertPayload("room", "message") + "\7", alertPayloads.get(2));
        assertEquals(0L, Functions.Proc_10_21_80D0A0(0, "room", "message"));
        MySQL.configureDatabaseConnection(null);
        HandlingMUS.configureMusSink(null);
        Licence.global_00829268 = "";
        assertEquals("L}1\2" + Crypto.Proc_3_0_6D2AF0(1, null, "") + Crypto.Proc_3_0_6D2AF0(1, null, "") + "HH",
            Functions.emailValidatedPayload(0));
        assertEquals(Crypto.Proc_3_0_6D2AF0(7, null, "DJ") + "motto\2M\2figure\2",
            Functions.userIdentityRefreshPayload(7, "motto", "figure", "M"));
        assertEquals("UPDATE users SET hc_startperiod=UNIX_TIMESTAMP(),hc2_periods=hc2_periods+2,hc_presents=hc_presents+0 WHERE id='7'",
            Functions.clubPeriodUpdateQuery(7, 2, 0, 62, 5));
        assertEquals("UPDATE users SET hc_startperiod=UNIX_TIMESTAMP(),hc_periods=hc_periods+1,hc_presents=hc_presents+3 WHERE id='7'",
            Functions.clubPeriodUpdateQuery(7, 1, 0, 0, 3));
        final List<String> userStateExecutions = new ArrayList<>();
        final List<String> userStatePayloads = new ArrayList<>();
        Licence.global_00829268 = "\2" + "91]14\0\2" + "92]15\0";
        Functions.global_0082928C = "[com.server.socket.game.club.gifts.hcrank1.amount=4]";
        HandlingMUS.configureMusSink((socketIndex, payload) -> userStatePayloads.add(socketIndex + ":" + payload));
        MySQL.configureDatabaseConnection(new Database() {
            @Override
            public void execute(String sqlText) {
                userStateExecutions.add(sqlText);
            }

            @Override
            public List<List<Object>> query(String sqlText) {
                if (sqlText.contains("SELECT email_validated")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(0));
                }
                if (sqlText.contains("SELECT id,id_socket,motto,figure,gender") && sqlText.contains("id='92'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(92, 0, "motto", "figure", "F"));
                }
                return Arrays.<List<Object>>asList(Arrays.<Object>asList(0));
            }
        });
        assertEquals(1L, Functions.Proc_10_19_80CCD0(91));
        assertEquals("UPDATE users SET email_validated='1' WHERE id='91' LIMIT 1", userStateExecutions.get(0));
        assertEquals("14:DATA\6" + "14\6" + Functions.emailValidatedPayload(1) + "\7", userStatePayloads.get(0));
        assertEquals(1L, Functions.Proc_10_22_80D460("92"));
        assertEquals("15:DATA\6" + "15\6" + Functions.userIdentityRefreshPayload(92, "motto", "figure", "F") + "\7",
            userStatePayloads.get(1));
        assertEquals(1L, Functions.Proc_10_23_80E110(93, 1, 0));
        assertEquals("UPDATE users SET hc_startperiod=UNIX_TIMESTAMP(),hc_periods=hc_periods+1,hc_presents=hc_presents+4 WHERE id='93'",
            userStateExecutions.get(1));
        assertEquals(0L, Functions.Proc_10_19_80CCD0(0));
        MySQL.configureDatabaseConnection(null);
        HandlingMUS.configureMusSink(null);
        Licence.global_00829268 = "";
        assertEquals("1\0" + "1\0" + "3\0" + "1\0", Functions.Proc_10_24_80E790(0, 0, 0, 2, 2));
        assertEquals("0\0" + "0\0" + "0\0" + "0\0", Functions.Proc_10_24_80E790(0));
        String botCache = "[10:3\2" + "501\2bot][11:4\2" + "502\2next]";
        assertEquals("501", Functions.representedBotRecordField(botCache, 10, 1));
        assertEquals("", Functions.representedBotRecordField(botCache, 10, 9));
        assertEquals("", Functions.representedBotRecordField(botCache, 12, 1));
        assertEquals(1L, Functions.representedPositionAvailable(0, 5, 5));
        assertEquals(0L, Functions.representedPositionAvailable(7, 1, 0));
        assertEquals(0L, Functions.representedPositionAvailable(7, 0, 1));
        assertEquals(1L, Functions.representedPositionAvailable(7, 0, 0));
        final List<String> occupancyQueries = new ArrayList<>();
        MySQL.configureDatabaseConnection(new Database() {
            @Override
            public void execute(String sqlText) {
            }

            @Override
            public List<List<Object>> query(String sqlText) {
                occupancyQueries.add(sqlText);
                if (sqlText.contains("FROM furnitures") && sqlText.contains("position_x='2'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(1));
                }
                if (sqlText.contains("FROM bots") && sqlText.contains("position_y='3'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(1));
                }
                return Arrays.<List<Object>>asList(Arrays.<Object>asList(0));
            }
        });
        assertEquals(1L, Functions.Proc_10_25_80F5D0(0, 2, 3));
        assertEquals(0, occupancyQueries.size());
        assertEquals(0L, Functions.Proc_10_25_80F5D0(7, 2, 4));
        assertEquals(1, occupancyQueries.size());
        assertEquals(0L, Functions.Proc_10_25_80F5D0(7, 4, 3));
        assertEquals(3, occupancyQueries.size());
        assertEquals(1L, Functions.Proc_10_25_80F5D0(7, 4, 4));
        MySQL.configureDatabaseConnection(null);
        final List<String> botAvailabilityQueries = new ArrayList<>();
        Licence.global_00829358 = "[10:3\2" + "501\2room-bot][11:0\2" + "502\2fallback-bot][12:0\2" + "0\2self-bot]";
        MySQL.configureDatabaseConnection(new Database() {
            @Override
            public void execute(String sqlText) {
            }

            @Override
            public List<List<Object>> query(String sqlText) {
                botAvailabilityQueries.add(sqlText);
                if (sqlText.contains("FROM rooms") && sqlText.contains("id_slot='3'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(7));
                }
                if (sqlText.contains("id_room FROM bots") && sqlText.contains("id='502'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(8));
                }
                if (sqlText.contains("id_room FROM bots") && sqlText.contains("id='12'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(0));
                }
                return Arrays.<List<Object>>asList(Arrays.<Object>asList(0));
            }
        });
        assertEquals(1L, Functions.Proc_10_27_81F1A0(10, 4, 4));
        assertEquals(true, botAvailabilityQueries.get(0).contains("FROM rooms"));
        assertEquals(1L, Functions.Proc_10_27_81F1A0(11, 4, 4));
        assertEquals(true, botAvailabilityQueries.get(3).contains("id_room FROM bots"));
        assertEquals(1L, Functions.Proc_10_27_81F1A0(12, 4, 4));
        assertEquals(0L, Functions.Proc_10_27_81F1A0(0, 4, 4));
        MySQL.configureDatabaseConnection(null);
        Path downloadSource = Files.createTempFile("alphaseries-download-source", ".txt");
        Path downloadDestination = Files.createTempFile("alphaseries-download-destination", ".txt");
        Files.write(downloadSource, "update-payload".getBytes("UTF-8"));
        assertEquals(true, Functions.Proc_10_28_8210C0(downloadSource.toUri().toURL().toString(), downloadDestination.toString()));
        assertEquals("update-payload", new String(Files.readAllBytes(downloadDestination), "UTF-8"));
        assertEquals(false, Functions.Proc_10_28_8210C0(downloadSource.toUri().toURL().toString()));
        assertEquals("@@", Console.Proc_2_4_6D28B0(-1));
        assertEquals("@A", Console.Proc_2_4_6D28B0(1));
        assertEquals("A@", Console.Proc_2_4_6D28B0(64));
        assertEquals(15.0f, Console.elapsedSeconds(86395.0f, 10.0f));
        assertEquals(250L, Console.delayMilliseconds("0.25"));
        assertEquals(250L, Console.delayMilliseconds("0,25"));
        assertEquals(0L, Console.delayMilliseconds("-1"));
        Console.Proc_2_3_6D27D0(0);
        Console.clear();
        Console.Proc_2_0_6D1510("boot", "SYS", 123);
        Console.Proc_2_1_6D1B60("raw", "", 456);
        Console.Proc_2_2_6D21D0("secret", "HIDDEN", 789);
        Console.Proc_2_2_6D21D0("shown", "GAME", 321);
        assertEquals("[SYS] boot", Console.entries().get(0).lineText());
        assertEquals(123L, Console.entries().get(0).foreColor());
        assertEquals("raw", Console.entries().get(1).lineText());
        assertEquals("secret", Console.entries().get(2).lineText());
        assertEquals("[GAME] shown", Console.entries().get(3).lineText());

        List<String> sent = new ArrayList<>();
        Filesystems.configurePacketSink((socketIndex, payload) -> sent.add(socketIndex + ":" + payload));
        Filesystems.global_00829268 = "[1:\1Alice\2" + "5][1:\1Bob\2" + "6]";
        assertEquals(2L, Filesystems.Proc_7_0_8034A0("hello"));
        assertEquals(Arrays.asList("5:hello", "6:hello"), sent);
        sent.clear();
        assertEquals(1L, Filesystems.Proc_7_1_8038A0("bob", "one"));
        assertEquals(Arrays.asList("6:one"), sent);
        assertEquals(true, Filesystems.isCrossDomainPolicyRequest("<policy-file-request/>\0"));
        assertEquals(false, Filesystems.isCrossDomainPolicyRequest("normal"));
        String framedPackets = "x@CCN1" + "x@DF_22" + "x@Z";
        assertEquals(Arrays.asList("CN1", "F_22"), Filesystems.readyPacketPayloadsFromBuffer(framedPackets));
        List<Filesystems.ReadyPacket> readyPackets = Filesystems.readyPacketsFromBuffer(framedPackets);
        assertEquals(2, readyPackets.size());
        assertEquals("CN", readyPackets.get(0).code);
        assertEquals("CN1", readyPackets.get(0).payload);
        assertEquals("F_", readyPackets.get(1).code);
        assertEquals("F_22", readyPackets.get(1).payload);
        assertEquals(Arrays.asList(), Filesystems.readyPacketPayloadsFromBuffer("<policy/>\0"));

        sent.clear();
        HandlingMUS.configureMusSink((socketIndex, payload) -> sent.add(socketIndex + ":" + payload));
        HandlingMUS.Proc_12_1_821AA0(4, "payload");
        HandlingMUS.Proc_12_0_8218C0(4);
        assertEquals(Arrays.asList("4:DATA\6" + "4\6payload\7", "4:SHUTDOWN\6" + "4\7"), sent);
        sent.clear();
        Handling_MUS.Proc_12_1_821AA0(5, "relay");
        Handling_MUS.Proc_12_0_8218C0(5);
        assertEquals(Arrays.asList("5:DATA\6" + "5\6relay\7", "5:SHUTDOWN\6" + "5\7"), sent);

        Guardian.global_008291A0 = "";
        Guardian.global_0082919C = 0L;
        Guardian.setGameServerConnected(false);
        Guardian.setSocketConnected(8, true);
        assertEquals(1, Guardian.Proc_11_2_821390(8));
        Guardian.setSocketConnected(8, false);
        assertEquals(0, Guardian.Proc_11_2_821390(8));
        Guardian.setGameServerConnected(true);
        assertEquals(1, Guardian.Proc_11_2_821390());
        Guardian.setGameServerConnected(false);
        Guardian.SocketMarkerState addedMarkerState = Guardian.toggleSocketMarkerState("[1]", 1, 12);
        assertEquals("[1][12]", addedMarkerState.markers);
        assertEquals(12L, addedMarkerState.highestIndex);
        assertEquals(true, addedMarkerState.accepted);
        assertEquals(true, addedMarkerState.added);
        Guardian.SocketMarkerState removedMarkerState = Guardian.toggleSocketMarkerState("[1][12]", 12, 12);
        assertEquals("[1]", removedMarkerState.markers);
        assertEquals(12L, removedMarkerState.highestIndex);
        assertEquals(true, removedMarkerState.accepted);
        assertEquals(false, removedMarkerState.added);
        Guardian.SocketMarkerState rejectedMarkerState = Guardian.toggleSocketMarkerState("[1]", 1, 2500);
        assertEquals("[1]", rejectedMarkerState.markers);
        assertEquals(1L, rejectedMarkerState.highestIndex);
        assertEquals(false, rejectedMarkerState.accepted);
        Guardian.Proc_11_3_821440(12);
        assertEquals("[12]", Guardian.global_008291A0);
        assertEquals(12L, Guardian.global_0082919C);
        Guardian.Proc_11_3_821440(12);
        assertEquals("", Guardian.global_008291A0);
        assertEquals(12L, Guardian.global_0082919C);

        Licence.global_008292BC = "10\t100\tchair\r11\t200\ttable";
        Licence.global_008292C0 = new String[]{"", "1\talpha\t7"};
        Licence.global_00829258 = "5\trow-five\r6\trow-six";
        assertEquals(100L, Licence.Proc_9_0_806F70(10, 1));
        assertEquals("alpha", Licence.Proc_9_1_8072B0(1, 1));
        assertEquals(7L, Licence.Proc_9_2_8075F0(1, 2));
        assertEquals("11\t200\ttable", Licence.Proc_9_3_807930(11));
        assertEquals("1\talpha\t7", Licence.Proc_9_4_807B90(1));
        assertEquals("6\trow-six", Licence.Proc_9_5_807DF0(6));
        Licence.global_00829268 = "[0:5\1u5\2sock5][1:bob\1bob\2" + "6][room\1" + "7\2" + "8]";
        assertEquals("u5", Licence.Proc_9_6_808080(5, 0));
        assertEquals(6L, Licence.Proc_9_7_808320("bob", 1));
        assertEquals(7L, Licence.Proc_9_10_808F30("room", 0));

        String[] cache = new String[12];
        Boot.cacheRowsById(cache, "10\t100\tchair\r11\t200\ttable");
        assertEquals("11\t200\ttable", cache[11]);
        assertEquals(Crypto.Proc_3_0_6D2AF0(2, null, "") + "a\2b\2c\2d\2",
            Boot.buildCampaignReplacementCache("a\tb\rc\td"));
        Boot.AchievementSettingsCache achievementSettings = Boot.buildAchievementSettingsCache(
            "7\tACH_ONE\t10\t2\t3\t4\t1\r8\tACH_TWO\t20\t5\t6\t7\t2");
        assertEquals("7\2" + "8\2", achievementSettings.questIdPayload);
        assertEquals("ACH_TWO", achievementSettings.rowsByIndex.get(1L)[1]);
        assertEquals("20", achievementSettings.rowsByIndex.get(1L)[2]);
        int[] messengerFriendLimits = Boot.buildMessengerFriendLimitCache(50, 75, 100);
        assertEquals(50, messengerFriendLimits[0]);
        assertEquals(0, messengerFriendLimits[1]);
        assertEquals(75, messengerFriendLimits[2]);
        assertEquals(100, messengerFriendLimits[4]);
        assertEquals(
            Crypto.Proc_3_0_6D2AF0(2, null, "")
                + Crypto.Proc_3_0_6D2AF0(1, null, "") + "public\2"
                + Crypto.Proc_3_0_6D2AF0(0, null, "")
                + Crypto.Proc_3_0_6D2AF0(3, null, "") + "hc\2"
                + Crypto.Proc_3_0_6D2AF0(1, null, ""),
            Boot.buildRoomCategoryPayload("1\tpublic\t0\t0\t0\r2\tstaff\t1\t5\t0\r3\thc\t1\t2\t1", 2, 1));
        Map<Long, String> recyclerProducts = new HashMap<>();
        recyclerProducts.put(80L, "10\r0\r11");
        recyclerProducts.put(20L, "12\rbad\r13");
        Boot.RecyclerCache recyclerCache = Boot.buildRecyclerCache("80\r20", recyclerProducts);
        assertEquals(2L, recyclerCache.groupCount);
        assertEquals(80L, recyclerCache.chanceByGroupIndex.get(0L).longValue());
        assertEquals("10\2" + "11\2", recyclerCache.productListByGroupIndex.get(0L));
        assertEquals("12\2" + "13\2", recyclerCache.productListByGroupIndex.get(1L));
        assertEquals(
            Crypto.Proc_3_0_6D2AF0(2, null, "")
                + Crypto.Proc_3_0_6D2AF0(80, null, "")
                + Crypto.Proc_3_0_6D2AF0(2, null, "")
                + Crypto.Proc_3_0_6D2AF0(10, null, "")
                + Crypto.Proc_3_0_6D2AF0(11, null, "")
                + Crypto.Proc_3_0_6D2AF0(20, null, "")
                + Crypto.Proc_3_0_6D2AF0(2, null, "")
                + Crypto.Proc_3_0_6D2AF0(12, null, "")
                + Crypto.Proc_3_0_6D2AF0(13, null, ""),
            recyclerCache.payload);
        assertEquals("[pet_dog\t1\t2\t3\t4\tDog][pet_cat\t5\t6\t7\t8\tCat]",
            Boot.buildPetRaceCache("pet_dog\t1.2\t2\t3\t4\tDog\rpet_cat\t5\t6\t7\t8\tCat"));
        assertEquals("20\t30\t40", Boot.buildPetLevelCache("2\t20\t30\t40").get(2L));
        Boot.PetCommandCache petCommandCache = Boot.buildPetCommandCache("1\t0\tsit\tidle\r2\t3\tjump\tmove");
        assertEquals(2L, petCommandCache.commandCount);
        assertEquals("2\t3\tjump\tmove", petCommandCache.commandById.get(2L));
        assertEquals("base" + "\0" + "5\1party\2" + "\0" + "7\1game\2",
            Boot.buildRoomEventLocaleCache("roomevent_type_5\tparty\rroomevent_type_7\tgame", "base"));
        assertEquals("[site.name=Alpha][com.client.format.date=dd.mm.yyyy][com.client.format.time=hh:nn:ss]"
                + "[com.mysql.format.date=%d.%m.%Y][com.mysql.format.time=%H:%i:%s]",
            Boot.buildSettingsCache("site.name\tAlpha", "d.m.Y", "h:i:s"));
        assertEquals(
            Crypto.Proc_3_0_6D2AF0(2, null, "")
                + Crypto.Proc_3_0_6D2AF0(1, null, "")
                + Crypto.Proc_3_0_6D2AF0(2, null, "")
                + Crypto.Proc_3_0_6D2AF0(2, null, "")
                + Crypto.Proc_3_0_6D2AF0(10, null, "")
                + Crypto.Proc_3_0_6D2AF0(12, null, "")
                + Crypto.Proc_3_0_6D2AF0(3, null, "")
                + Crypto.Proc_3_0_6D2AF0(1, null, "")
                + Crypto.Proc_3_0_6D2AF0(2, null, "")
                + Crypto.Proc_3_0_6D2AF0(3, null, ""),
            Boot.buildGiftWrapPayload("10\r0\r12", 2, 3));
        Map<Long, Long> giftProductIds = new HashMap<>();
        giftProductIds.put(100L, 200L);
        Map<Long, Long> giftProductTypes = new HashMap<>();
        giftProductTypes.put(200L, 9L);
        giftProductTypes.put(300L, 1L);
        Map<Long, String> giftNames = new HashMap<>();
        giftNames.put(200L, "VIP badge");
        giftNames.put(300L, "Fallback sofa");
        Map<Long, String> giftDescriptions = new HashMap<>();
        giftDescriptions.put(200L, "Badge desc");
        giftDescriptions.put(300L, "Sofa desc");
        Boot.ClubGiftCache clubGiftCache = Boot.buildClubGiftCache("100\t1\t30\r300\t0\t5",
            giftProductIds, giftProductTypes, giftNames, giftDescriptions);
        assertEquals(
            Crypto.Proc_3_0_6D2AF0(2, null, "")
                + Crypto.Proc_3_0_6D2AF0(100, null, "")
                + Crypto.Proc_3_0_6D2AF0(200, null, "")
                + "VIP badge\2Badge desc\2IHHIi\2"
                + Crypto.Proc_3_0_6D2AF0(1, null, "")
                + Crypto.Proc_3_0_6D2AF0(30, null, "")
                + Crypto.Proc_3_0_6D2AF0(300, null, "")
                + Crypto.Proc_3_0_6D2AF0(300, null, "")
                + "Fallback sofa\2Sofa desc\2IHHIs\2"
                + Crypto.Proc_3_0_6D2AF0(0, null, "")
                + Crypto.Proc_3_0_6D2AF0(5, null, ""),
            clubGiftCache.giftPayload);
        assertEquals("[100\0" + "200\1" + "30][300\0" + "300\1" + "5]", clubGiftCache.giftLookup);
        assertEquals("one\2two\2", Boot.buildStaffMessageList("one\rtwo"));
        Map<Long, String> staffCategoryChildren = new HashMap<>();
        staffCategoryChildren.put(10L, "11\tchild-a\r12\tchild-b");
        assertEquals(
            Crypto.Proc_3_0_6D2AF0(10, null, "") + "root-a\2"
                + Crypto.Proc_3_0_6D2AF0(2, null, "")
                + Crypto.Proc_3_0_6D2AF0(11, null, "") + "child-a\2"
                + Crypto.Proc_3_0_6D2AF0(12, null, "") + "child-b\2"
                + Crypto.Proc_3_0_6D2AF0(20, null, "") + "root-b\2"
                + Crypto.Proc_3_0_6D2AF0(0, null, ""),
            Boot.buildStaffCategoryPayload("10\troot-a\r20\troot-b", staffCategoryChildren));
        Map<Long, String> importantFaqRows = new HashMap<>();
        importantFaqRows.put(1L, "31\tfirst");
        importantFaqRows.put(2L, "41\tsecond\r42\tthird");
        assertEquals(
            Crypto.Proc_3_0_6D2AF0(2, null, "")
                + Crypto.Proc_3_0_6D2AF0(1, null, "")
                + Crypto.Proc_3_0_6D2AF0(31, null, "") + "first\2"
                + Crypto.Proc_3_0_6D2AF0(2, null, "")
                + Crypto.Proc_3_0_6D2AF0(41, null, "") + "second\2"
                + Crypto.Proc_3_0_6D2AF0(42, null, "") + "third\2",
            Boot.buildImportantFaqPayload(importantFaqRows));
        Map<Long, String> faqRowsByCategory = new HashMap<>();
        faqRowsByCategory.put(7L, "70\tfaq-a\r71\tfaq-b");
        Boot.FaqCategoryCache faqCategoryCache = Boot.buildFaqCategoryCache("7\tcat-a\r9\tcat-b", faqRowsByCategory);
        assertEquals(
            Crypto.Proc_3_0_6D2AF0(2, null, "")
                + Crypto.Proc_3_0_6D2AF0(7, null, "") + "cat-a\2"
                + Crypto.Proc_3_0_6D2AF0(9, null, "") + "cat-b\2",
            faqCategoryCache.categoryPayload);
        assertEquals(
            Crypto.Proc_3_0_6D2AF0(2, null, "")
                + Crypto.Proc_3_0_6D2AF0(70, null, "") + "faq-a\2"
                + Crypto.Proc_3_0_6D2AF0(71, null, "") + "faq-b\2",
            faqCategoryCache.faqPayloadByCategoryId.get(7L));
        assertEquals(Crypto.Proc_3_0_6D2AF0(0, null, ""), faqCategoryCache.faqPayloadByCategoryId.get(9L));
        assertEquals(Crypto.Proc_3_0_6D2AF0(5, null, "") + "line1\rline2\2",
            Boot.buildFaqDescriptionCache("5\tline1\nline2").get(5L));
        Boot.VisitRoomCache visitRoomCache = Boot.buildAdvertisementVisitRoomCache("2\t/lobby\r4\t/cafe", "/ad/");
        assertEquals(2L, visitRoomCache.count);
        assertEquals("/ad/4\2/cafe\2", visitRoomCache.payloadByVisitRoomId.get(4L));
        assertEquals(true, Boot.buildRecommendedRoomsQuery(3).contains("id_tree='3'"));
        String roomRow = "1\t2\t3\tc1\tc2\tc3\tc4\tc5\tc6\tc7\tc8\tc9\tc10\tc11\tc12\tc13\tc14\tc15\tc16\tc17\tc18\tc19\tc20\tc21\tc22\t4\t5";
        assertEquals(Crypto.Proc_3_0_6D2AF0(1, null, "")
            + Crypto.Proc_3_0_6D2AF0(1, null, "")
            + Crypto.Proc_3_0_6D2AF0(2, null, "")
            + Crypto.Proc_3_0_6D2AF0(3, null, "")
            + "c1\2c2\2c3\2c4\2c5\2c6\2c7\2c8\2c9\2c10\2c11\2c12\2c13\2c14\2c15\2c16\2c17\2c18\2c19\2c20\2c21\2c22\2"
            + Crypto.Proc_3_0_6D2AF0(4, null, "")
            + Crypto.Proc_3_0_6D2AF0(5, null, ""),
            Boot.buildRecommendedRoomsPayload(roomRow));
        assertEquals("i", Boot.catalogProductClass(9));
        assertEquals("s", Boot.catalogProductClass(0));
        assertEquals(false, Boot.catalogTextFieldPresent("NULL"));
        assertEquals(true, Boot.catalogPageVisible(new String[]{"1", "name", "1", "2", "0", "1"}, 0, 0));
        Functions.global_008292A8 = new String[][]{{}, {"\2fuse_developer\2"}};
        assertEquals(true, Boot.catalogPageVisible(new String[]{"1", "name", "1", "2", "1", "1"}, 1, 0));
        Map<Long, Long> catalogChildCounts = new HashMap<>();
        catalogChildCounts.put(10L, 2L);
        Map<Long, String> catalogChildren = new HashMap<>();
        catalogChildren.put(10L, "11\tChild A\t1\t2\t0\t1\r12\tChild B\t1\t3\t0\t0");
        String expectedCatalogTree = Crypto.Proc_3_0_6D2AF0(1, null, "")
            + Boot.buildCatalogPageTreeEntry(new String[]{"10", "Root", "5", "6", "0", "1"}, 2)
            + Boot.buildCatalogPageTreeEntry(new String[]{"11", "Child A", "1", "2", "0", "1"}, 0);
        assertEquals(expectedCatalogTree, Boot.buildCatalogPageTreePayload(
            "10\tRoot\t5\t6\t0\t1\r20\tHidden\t1\t2\t0\t0", catalogChildCounts, catalogChildren, 1, 0));
        assertEquals("fuse_developer\2payload", Boot.appendPermissionPayload(1, 0, "fuse_developer", "payload"));
        Functions.global_0082928C = "[com.system.format.date=d.m.Y][com.system.format.time=h:i:s]"
            + "[com.client.catalog.gifts.wrap.count.accessories=2][com.client.catalog.gifts.wrap.count.colors=3]"
            + "[com.client.navigator.categories.default.private.id=1][com.client.navigator.categories.default.public.id=2]"
            + "[com.server.socket.game.advertisement.visitrooms.path=/ad/]";
        MySQL.configureDatabaseConnection(new Database() {
            @Override
            public void execute(String sqlText) {
            }

            @Override
            public List<List<Object>> query(String sqlText) {
                if (sqlText.contains("settings_recycler.chance")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(80));
                }
                if (sqlText.contains("settings_recycler WHERE chance='80'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(10), Arrays.<Object>asList(11));
                }
                if (sqlText.contains("settings_petraces")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList("pet_dog", 1, 2, 3, 4, "Dog"));
                }
                if (sqlText.contains("MAX(id_level)")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(2));
                }
                if (sqlText.contains("bots_petlevels ORDER")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(2, 20, 30, 40));
                }
                if (sqlText.contains("COUNT(id_command)")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(1));
                }
                if (sqlText.contains("MAX(id_command)")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(2));
                }
                if (sqlText.contains("bots_petcommands")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(2, 3, "jump", "move"));
                }
                if (sqlText.contains("locales WHERE category='2'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList("roomevent_type_5", "party"));
                }
                if (sqlText.equals("SELECT variable,value FROM settings")) {
                    return Arrays.<List<Object>>asList(
                        Arrays.<Object>asList("com.system.format.date", "d.m.Y"),
                        Arrays.<Object>asList("com.system.format.time", "h:i:s"),
                        Arrays.<Object>asList("com.server.socket.game.advertisement.visitrooms.path", "/ad/"));
                }
                if (sqlText.contains("level_privileges")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList("fuse_mod"), Arrays.<Object>asList("fuse_chatlog"));
                }
                if (sqlText.contains("rooms_categories WHERE id_parent='1'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(1, "public", 0, 0, 0));
                }
                if (sqlText.contains("present_wrap")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(10), Arrays.<Object>asList(12));
                }
                if (sqlText.contains("faq WHERE is_important='1'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(5, "important"));
                }
                if (sqlText.contains("faq WHERE is_important='2'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(6, "also"));
                }
                if (sqlText.contains("MAX(id) FROM faq_categories")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(7));
                }
                if (sqlText.equals("SELECT id,name FROM faq_categories")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(7, "cat"));
                }
                if (sqlText.contains("faq WHERE id_category='7'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(5, "faq"));
                }
                if (sqlText.contains("MAX(id) FROM faq")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(5));
                }
                if (sqlText.equals("SELECT id,description FROM faq")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(5, "line1\nline2"));
                }
                if (sqlText.contains("MAX(id) FROM advertisement_visitrooms")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(4));
                }
                if (sqlText.equals("SELECT id,address FROM advertisement_visitrooms")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(4, "/cafe"));
                }
                return Arrays.<List<Object>>asList(Arrays.<Object>asList(0));
            }
        });
        Boot.Proc_1_0_6BA9D0();
        assertEquals(1L, Licence.global_00829168);
        assertEquals("10\2" + "11\2", ((String[]) Licence.global_00829140)[0]);
        Boot.Proc_1_6_6C5830();
        assertEquals(true, Licence.global_008291EC.contains("pet_dog"));
        Boot.Proc_1_7_6C5E10();
        assertEquals("20\t30\t40", ((String[]) Licence.global_008292D0)[2]);
        assertEquals("2\t3\tjump\tmove", ((String[]) Licence.global_008292CC)[2]);
        Boot.Proc_1_8_6C6850();
        assertEquals(true, DataManager.global_008291AC.contains("party"));
        Boot.Proc_1_9_6C6DF0();
        assertEquals(true, Functions.global_0082928C.contains("com.mysql.format.time=%H:%i:%s"));
        Boot.Proc_1_11_6C8D10();
        Boot.Proc_1_12_6C8EF0();
        assertEquals(true, ((String[][]) Licence.global_00829244)[0][0].contains("public"));
        Boot.Proc_1_13_6C9820();
        assertEquals(true, Licence.global_00829260.length() > 0);
        Boot.Proc_1_16_6CCA60();
        assertEquals(true, ((String[][]) Functions.global_008292A8)[1][0].contains("fuse_mod"));
        Boot.Proc_1_19_6CF190();
        assertEquals(true, Licence.global_00829204.contains("important"));
        Boot.Proc_1_20_6CF830();
        assertEquals(true, Licence.global_00829208.contains("cat"));
        Boot.Proc_1_21_6D08C0();
        assertEquals(true, ((String[]) Licence.global_00829210)[5].contains("line1\rline2"));
        Boot.Proc_1_22_6D0F00();
        assertEquals("/ad/4\2/cafe\2", ((String[]) Licence.global_008291D4)[4]);
        Boot.Proc_1_23_6D1480("booted", "DEBUG");
        MySQL.configureDatabaseConnection(null);

        Licence.global_00829350 = "";
        Main.appendGameServerPacketData(7, new String[]{"ignored", "ignored", "A", "B", "C"});
        assertEquals("A\2B\2C", Main.popGameServerPacketData(7));
        assertEquals("", Licence.global_00829350);
        assertEquals("bcd", Main.shiftIdentityText("abc", 1));
        assertEquals("abc", Main.easyGetIdentity(Main.shiftIdentityText("abc", 25)));
        assertEquals("cde", Main.createSuperEasyIdentity("abc"));
        assertEquals("abc", Main.superEasyGetIdentity("cde"));
        String encodedIdentity = Main.Proc_0_22_68C1A0("AZ");
        assertEquals("AZ", Main.getIdentity(encodedIdentity, 0));
        assertEquals("hij", Main.Proc_0_23_68C430("abc"));
        Main.Proc_0_24_68EEF0();
        assertEquals("AB", Main.newPremiumCheck(2, "d" + Character.toString((char) 163) + Character.toString((char) 164)));
        assertEquals("AZ", Main.getIdentity("e" + Character.toString((char) 164) + Character.toString((char) 190), 3));
        Licence.global_00829354 = "[3][7]";
        assertEquals(true, Main.isGameSessionReady(7));
        assertEquals(false, Main.isGameSessionReady(8));
        Guardian.setSocketConnected(7, true);
        List<String> preSessionPackets = new ArrayList<>();
        Main.configurePreSessionPacketSink((socketIndex, payload) -> preSessionPackets.add(socketIndex + ":" + payload));
        Licence.global_00829354 = "";
        Main.Proc_0_25_68FBC0(7, "login-data");
        assertEquals(Arrays.asList("7:login-data"), preSessionPackets);
        List<String> readyPacketsSent = new ArrayList<>();
        HandlingMUS.configureMusSink((socketIndex, payload) -> readyPacketsSent.add(socketIndex + ":" + payload));
        Licence.global_00829354 = "[7]";
        Main.Proc_0_25_68FBC0(7, "<policy-file-request/>\0");
        assertEquals(1, readyPacketsSent.size());
        assertEquals(true, readyPacketsSent.get(0).startsWith("7:DATA\6" + "7\6<?xml"));
        Main.configurePreSessionPacketSink(null);
        HandlingMUS.configureMusSink(null);
        Guardian.setSocketConnected(7, false);
        assertEquals("payload", Main.mainRepresentedRecordByBracket("[5]payload[6]other", 5));
        assertEquals("11\talpha", Main.mainRepresentedRecordByKey("\1" + "11\talpha\2\1" + "12\tbeta\2", 11));
        Licence.global_00829358 = "[50:2\2bot-id\2name]";
        assertEquals("2\2bot-id\2name", Main.mainRepresentedBotRecordText(50));
        assertEquals("name", Main.mainRepresentedBotRecordField(50, 2));
        Licence.global_00829310 = "";
        Main.mainRepresentedRoomOccupantAdd(4, 9, 1);
        assertEquals("\1" + "4\t\1" + "9\2\t\t1\2", Licence.global_00829310);
        assertEquals("4", Main.mainRepresentedRoomRecordField(4, 0));
        assertEquals("\1" + "9", Main.mainRepresentedRoomRecordField(4, 1));
        Main.mainRepresentedRoomOccupantMove(4, 9, 1, 2, 3, 4, 1);
        assertEquals(true, Licence.global_00829310.contains("\1" + "9\t2\t3\t4\t1\2"));
        assertEquals(1L, Main.mainRollerDeltaX(2));
        assertEquals(-1L, Main.mainRollerDeltaX(6));
        assertEquals(-1L, Main.mainRollerDeltaY(0));
        assertEquals(1L, Main.mainRollerDeltaY(4));
        assertEquals("12", Main.mainRollerTargetHeight("12.5", "7"));
        assertEquals("7", Main.mainRollerTargetHeight("", "7.9"));
        MySQL.configureDatabaseConnection(new Database() {
            @Override
            public void execute(String sqlText) {
            }

            @Override
            public List<List<Object>> query(String sqlText) {
                if (sqlText.contains("SELECT id FROM rooms WHERE id_slot='4'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(44));
                }
                if (sqlText.contains("SELECT id FROM users WHERE id_socket='8'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(88));
                }
                if (sqlText.contains("logs_visitedrooms WHERE id_user='88'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(44));
                }
                if (sqlText.contains("COUNT(*) FROM furnitures")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(0));
                }
                if (sqlText.contains("COUNT(*) FROM bots")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(0));
                }
                return Arrays.<List<Object>>asList(Arrays.<Object>asList(0));
            }
        });
        assertEquals(44L, Main.mainCurrentRoomIdForSlot(4));
        Licence.global_0082934C = "[8]user\2" + "4";
        Licence.global_00829310 = "";
        Guardian.setSocketConnected(8, true);
        Main.Proc_0_26_6ACF30(8);
        assertEquals(true, Licence.global_00829310.contains("\1" + "4\t\1" + "8\2"));
        Licence.global_00829358 = "[70:4\2bot-id\2name]";
        Main.Proc_0_27_6AD400(70);
        assertEquals(true, Licence.global_00829310.contains("\1" + "70"));
        Main.Proc_0_28_6AD850(70, 1, 1, 2, 1);
        assertEquals(true, Licence.global_00829310.contains("\1" + "70\t2\t1"));
        Main.Proc_0_29_6B0E10(8, 1, 1, 1, 2);
        assertEquals(true, Licence.global_00829310.contains("\1" + "8\t1\t2"));
        Guardian.setSocketConnected(8, false);
        MySQL.configureDatabaseConnection(null);
        assertEquals(Crypto.Proc_3_0_6D2AF0(3, null,
            Crypto.Proc_3_0_6D2AF0(2, null,
                Crypto.Proc_3_0_6D2AF0(1, null,
                    Crypto.Proc_3_0_6D2AF0(99, null, "AZ")))),
            Main.mainRollerMovePayload(99, 1, 2, "3"));
        assertEquals("[9][10]", Main.mainRepresentedEntityIds("\1" + "9\tdata\2\1" + "10\2\1" + "9\2"));
        assertEquals(10L, Main.mainRepresentedEntityIdAt("[9][10]", 1));

        Updater updater = new Updater();
        updater.queueHeightAnimation(1000, 5);
        assertEquals(1000L, updater.pendingHeightTarget);
        assertEquals(5L, updater.pendingAnimationInterval);
        assertEquals(true, updater.timer1Enabled);
        updater.startHeightAnimationTimer();
        assertEquals(false, updater.timer1Enabled);
        assertEquals(true, updater.timer2Enabled);
        Updater.HeightStep heightStep = updater.applyHeightTimerStep(900);
        assertEquals(950L, heightStep.height);
        assertEquals(true, heightStep.timer2Enabled);
        Updater.HeightStep clampedHeightStep = Updater.heightTimerStep(990, 1000);
        assertEquals(1000L, clampedHeightStep.height);
        assertEquals(false, clampedHeightStep.timer2Enabled);
        Updater.HeightStep shrinkingHeightStep = Updater.heightTimerStep(1100, 1000);
        assertEquals(1050L, shrinkingHeightStep.height);
        assertEquals(true, shrinkingHeightStep.timer2Enabled);
        updater.queueProgressWidth(250);
        assertEquals(250L, updater.pendingProgressWidth);
        assertEquals(true, updater.walkPercentEnabled);
        Updater.ProgressStep progressStep = updater.applyProgressTimerStep(225, true);
        assertEquals(250L, progressStep.width);
        assertEquals(true, progressStep.walkPercentEnabled);
        Updater.ProgressStep completeProgressStep = Updater.progressTimerStep(11500, Updater.PROGRESS_WIDTH_MAX, false);
        assertEquals(Updater.PROGRESS_WIDTH_MAX, completeProgressStep.width);
        assertEquals(true, completeProgressStep.complete);
        assertEquals(false, completeProgressStep.walkPercentEnabled);
        updater.currentUpdateIndex = 0;
        updater.advanceUpdateProgress(4);
        assertEquals(5766L, updater.pendingProgressWidth);
        updater.applyFeatureState(new String[]{"id", "title", "body", "0", "99"});
        assertEquals(true, updater.freeFeature.visible);
        assertEquals("Kostenlose Funktion", updater.freeFeature.caption);
        updater.applyFeatureState(new String[]{"id", "title", "body", "2", "99"});
        assertEquals(true, updater.unfreeFeature.visible);
        assertEquals("Kostet 99 Punkte", updater.unfreeFeature.caption);
        updater.applyFeatureState(new String[]{"id", "title", "body", "1", "0"});
        assertEquals(true, updater.downloadFeature.visible);
        assertEquals(42L, Updater.getUpdateFieldNumber(new String[]{"x", "42"}, 1));
        assertEquals(0L, Updater.getUpdateFieldNumber(new String[]{"x"}, 2));
        assertEquals("custom", Updater.getUpdaterExecutableName("custom", "app"));
        assertEquals("app", Updater.getUpdaterExecutableName("", "app"));
        assertEquals("INSERT IGNORE INTO a\nINSERT IGNORE INTO b", Updater.normalizedUpdateSql("INSERT INTO a\r\ninsert into b"));

        String httpRequest = PrivSockHTTP.buildGetRequest("/path", "example.com", "8080");
        assertEquals(true, httpRequest.startsWith("GET /path HTTP/1.1\r\nHost:   example.com:8080\r\n"));
        assertEquals(true, httpRequest.contains("User-Agent:   FireFox/1.0\r\n"));
        assertEquals("", PrivSockHTTP.buildGetRequest("", "example.com", "80"));
        assertEquals("", DataManager.Proc_8_0_804330(""));

        assertEquals(Crypto.Proc_3_0_6D2AF0(2, null, "") + "hd-180-1\2M\2",
            Handling.wardrobeSlotPayload(2, "hd-180-1", "M"));
        assertEquals(true, Handling.isValidWardrobeFigure("hd-180-1.ch-255-66", "M"));
        assertEquals(false, Handling.isValidWardrobeFigure("bad-1", "M"));
        assertEquals(false, Handling.isValidWardrobeFigure("hd-'1", "M"));
        String figureData = "<settype type=\"hd\"><set id=\"180\" gender=\"M\"></set></settype>"
            + "<settype type=\"ch\"><set id=\"255\" gender=\"U\"/></settype>";
        assertEquals(true, Handling.isValidWardrobeFigure("hd-180-1.ch-255-66", "M", figureData));
        assertEquals(false, Handling.isValidWardrobeFigure("hd-180-1", "F", figureData));
        assertEquals(true, Handling.figureSetAllowsGender("<set id=\"1\" gender=\"U\"/>", "<set id=\"1\"", "F"));
        assertEquals(Crypto.Proc_3_0_6D2AF0(44, null, "DJ") + "hello\2M\2hd-180-1\2",
            Handling.userIdentityPayload(44, "hello", "M", "hd-180-1"));
        assertEquals(Crypto.Proc_3_0_6D2AF0(7, null,
            Crypto.Proc_3_0_6D2AF0(3, null, "@Y") + "hi\2"),
            Handling.representedChatPayload(3, "hi", 7, 1));
        assertEquals("www.example.com;http://alpha;https://beta;",
            Handling.extractUrlList("see www.example.com and http://alpha or https://beta"));
        assertEquals("", Handling.extractUrlList("www bad example.com"));
        assertEquals("hello *** and ***", Handling.filterChatText("hello badword and BADWORD", true, "***", "badword\rxx"));
        assertEquals("***", Handling.filterChatText("xx", true, "***", "badword\rxx"));
        assertEquals("xx now", Handling.filterChatText("xx now", true, "***", "xx"));
        assertEquals(5L, Handling.findGestureId("hello :)", true, ":)\t5\r:(\t6"));
        assertEquals(0L, Handling.findGestureId("hello", false, ":)\t5"));
        String complexPayload = Handling.Proc_6_29_70D800(1, 2, 3, 4, "four", 5, "six", "seven", 8, "nine", 10, "eleven");
        assertEquals(true, complexPayload.endsWith("eleven\2"));
        assertEquals(true, complexPayload.contains("four\2"));
        assertEquals("keep\nalso", Handling.removeRepresentedLineRecord("keep\r\nremove-this\nalso", "remove"));
        assertEquals("\1" + "1\talpha\2", Handling.removeRepresentedCacheRecord("\1" + "1\talpha\2\1" + "2\tbeta\2", "\1" + "2\t"));
        assertEquals("2\tbeta", Handling.representedRoomRecord("\1" + "1\talpha\2\1" + "2\tbeta\2", 2));
        assertEquals("3", Handling.representedRoomRecord("\1" + "3\2", 3));
        assertEquals("", Handling.representedRoomRecord("\1" + "1\talpha\2", 4));
        String roomRecordCache = Handling.representedRoomRecordSet("\1" + "1\talpha\2\1" + "2\told\2", 2, "2\tnew");
        assertEquals("\1" + "1\talpha\2\1" + "2\tnew\2", roomRecordCache);
        assertEquals("\1" + "1\talpha\2", Handling.representedRoomRecordSet("\1" + "1\talpha\2", 0, "0\tignored"));
        String movementCache = Handling.representedRoomOccupantMove("", 4, 9, 2, 3, 4, 1);
        assertEquals("\1" + "4\t\t\t0\t\1" + "9\t2\t3\t4\t1\2\2", movementCache);
        Handling.MovementPosition movementPosition = Handling.representedMovementPosition(movementCache, 4, 9);
        assertEquals(true, movementPosition.found);
        assertEquals(2L, movementPosition.positionX);
        assertEquals(3L, movementPosition.positionY);
        assertEquals(false, Handling.representedMovementPosition(movementCache, 4, 10).found);
        movementCache = Handling.representedRoomOccupantMove(movementCache, 4, 9, 5, 6, 2, 0);
        assertEquals("\1" + "4\t\t\t0\t\1" + "9\t5\t6\t2\t0\2\2", movementCache);
        Path tempFile = Files.createTempFile("alphaseries4j", ".cache");
        Handling.Proc_6_240_7FC2B0(tempFile.toString(), "cache-data");
        assertEquals("cache-data" + System.lineSeparator(), Handling.Proc_6_239_7FC170(tempFile.toString()));
        Path missingCache = Files.createTempDirectory("alphaseries4j-cache").resolve("room.cache");
        assertEquals(System.lineSeparator(), Handling.handlingEnsureRoomCacheFile(missingCache.toString()));
        assertEquals("beta", Handling.handlingField(new String[]{"alpha", "beta"}, 1));
        assertEquals("", Handling.handlingField(new String[]{"alpha"}, 3));
        String userEntryPayload = Handling.Proc_6_41_712730(7, "alice", "hd-1", "motto", "F", 8, 2, 3, "1.0", 4, 5);
        assertEquals(true, userEntryPayload.contains("alice\2hd-1"));
        assertEquals(true, userEntryPayload.contains("motto\2"));
        assertEquals(userEntryPayload, Handling.Proc_6_41_712730("7\talice\thd-1\tmotto\tF\t8\t2\t3\t1.0\t4\t5"));
        String botPayload = Handling.Proc_6_42_712FB0(9, "bot", "figure", "M", 10, 4, 5, "0.0", 2);
        assertEquals(true, botPayload.startsWith("Mbot\2figure\2M\2"));
        assertEquals(true, botPayload.endsWith("0.0\2HK"));
        String petPayload = Handling.Proc_6_42_712FB0(11, "pet", "figure", "F", 12, 6, 7, "0.5", 3);
        assertEquals(true, petPayload.startsWith(Crypto.Proc_3_0_6D2AF0(11, null, "") + "pet\2"));
        assertEquals(true, petPayload.endsWith("0.5\2PAJJ"));
        assertEquals(2L, Handling.avatarNameValidationCode("ab", "", 0));
        assertEquals(1L, Handling.avatarNameValidationCode("abcdefghijklmnop", "", 0));
        assertEquals(2L, Handling.avatarNameValidationCode("MOD-user", "", 0));
        assertEquals(2L, Handling.avatarNameValidationCode("bad name", "", 0));
        assertEquals(0L, Handling.avatarNameValidationCode("Alice_1", "alice_1", 1));
        assertEquals(3L, Handling.avatarNameValidationCode("Alice_2", "alice_1", 1));
        assertEquals(0L, Handling.avatarNameValidationCode("Alice_2", "alice_1", 0));
        assertEquals(7L, Handling.handlingMovementField("5\0" + "6\0" + "7\0", 2));
        assertEquals(3L, Handling.handlingDirectionCode(1, 1));
        String wireStringPayload = "@Cabc";
        Handling.LongRef wireOffset = new Handling.LongRef(1);
        assertEquals("abc", Handling.readWireString(wireStringPayload, wireOffset));
        assertEquals(6L, wireOffset.value);
        String wireLongPayload = Crypto.Proc_3_0_6D2AF0(123, null, "") + "tail";
        Handling.LongRef longOffset = new Handling.LongRef(1);
        assertEquals(123L, Handling.readWireLong(wireLongPayload, longOffset));
        assertEquals(3L, longOffset.value);
        Handling.StickyNoteUpdate note = new Handling.StickyNoteUpdate();
        assertEquals(true, Handling.stickyNoteUpdateFromWire("A5" + "9CFF9Chello\nworld", note));
        assertEquals(5L, note.furnitureId);
        assertEquals("9CFF9C", note.noteColor);
        assertEquals("world", note.noteCaption);
        assertEquals(5L, Handling.stickyFurnitureIdFromPayload(wireLong(5)));
        assertEquals(true, Handling.isStickyNoteColor("ffff33"));
        assertEquals(false, Handling.isStickyNoteColor("ffffff"));
        assertEquals(true, Handling.isDimmerColour("#82f349"));
        assertEquals(false, Handling.isDimmerColour("#ffffff"));
        Handling.WallPlacement placement = new Handling.WallPlacement();
        assertEquals(true, Handling.wallPlacementFromPayload(":w= 10,20 l= 3,4", placement));
        assertEquals(10L, placement.wallX);
        assertEquals(20L, placement.wallY);
        assertEquals(3L, placement.localX);
        assertEquals(4L, placement.localY);
        assertEquals(false, Handling.wallPlacementFromPayload("bad", new Handling.WallPlacement()));
        String iconWire = "A" + "B" + "A" + "C" + "B";
        String iconPayload = Crypto.Proc_3_0_6D2AF0(1, null, "");
        iconPayload = Crypto.Proc_3_0_6D2AF0(2, null, iconPayload);
        iconPayload = Crypto.Proc_3_0_6D2AF0(1, null, iconPayload);
        iconPayload = Crypto.Proc_3_0_6D2AF0(3, null, iconPayload);
        iconPayload = Crypto.Proc_3_0_6D2AF0(2, null, iconPayload);
        assertEquals(iconPayload, Handling.roomIconPayloadFromWire(iconWire));
        assertEquals("", Handling.roomIconPayloadFromWire("Z"));
        DataManager.global_008291AC = "\0" + "1\1events\2";
        Handling.RoomEventPayload createdEvent = new Handling.RoomEventPayload();
        String eventWire = "A"
            + "@CJam"
            + "@DDesc"
            + "B"
            + "@COne"
            + "@DTwo2";
        assertEquals(true, Handling.roomEventCreatePayloadFromWire(eventWire, createdEvent));
        assertEquals(1L, createdEvent.categoryId);
        assertEquals("events", createdEvent.categoryName);
        assertEquals("Jam", createdEvent.eventName);
        assertEquals("Desc", createdEvent.eventDescription);
        assertEquals("one", createdEvent.tagOne);
        assertEquals("two2", createdEvent.tagTwo);
        Handling.RoomEventPayload editedEvent = new Handling.RoomEventPayload();
        assertEquals(true, Handling.roomEventEditPayloadFromWire("@CJam@DDescA@CTag", editedEvent));
        assertEquals("tag", editedEvent.tagOne);
        Handling.RoomSettingsPayload roomSettings = new Handling.RoomSettingsPayload();
        String roomSettingsWire = "@DRoom"
            + "@Epass1"
            + "A"
            + "@EDesc1"
            + "C"
            + "A"
            + "B"
            + "@CTag"
            + "@EOther"
            + "A"
            + "A"
            + "A"
            + "A"
            + "F"
            + "A";
        assertEquals(true, Handling.roomSettingsFromWire(roomSettingsWire, roomSettings));
        assertEquals("Room", roomSettings.roomName);
        assertEquals(3L, roomSettings.visitorsMax);
        assertEquals("tag", roomSettings.tagOne);
        assertEquals(1L, roomSettings.allowOthersPets);
        assertEquals(1L, roomSettings.allowFeedPets);
        assertEquals(1L, roomSettings.allowWalkthrough);
        assertEquals(-2L, roomSettings.thicknessFloor);
        assertEquals(1L, roomSettings.thicknessWallpaper);
        assertEquals(1L, Handling.roomSettingsFlag(99));
        assertEquals(0L, Handling.roomSettingsFlag(0));
        assertEquals(-2L, Handling.roomSettingsThickness(-99));
        assertEquals(1L, Handling.roomSettingsThickness(99));
        assertEquals("null", Handling.nullableSqlText(""));
        assertEquals("'O''Reilly'", Handling.nullableSqlText("O'Reilly"));
        Functions.global_0082928C = "[com.client.navigator.list.limit=25]";
        assertEquals(25L, Handling.navigatorListLimit());
        Functions.global_0082928C = "[com.client.navigator.list.limit=0]";
        assertEquals(50L, Handling.navigatorListLimit());
        assertEquals("100'' ok", Handling.navigatorSearchTerm("100%' ok"));
        String officialNavigatorQuery = Handling.officialNavigatorQuery();
        assertEquals(4, officialNavigatorQuery.split(" UNION ALL ", -1).length);
        assertEquals(true, officialNavigatorQuery.contains("rooms_official.id_type='1'"));
        assertEquals(true, officialNavigatorQuery.contains("rooms_official.id_type='2'"));
        assertEquals(true, officialNavigatorQuery.contains("rooms_official.id_type='3'"));
        assertEquals(true, officialNavigatorQuery.contains("rooms_official.id_type='4'"));
        assertEquals(true, officialNavigatorQuery.endsWith("ORDER BY 27 ASC LIMIT 255"));
        String[] navigatorFields = new String[]{
            "10", "room", "owner", "desc", "3", "25", "open", "1",
            "unused", "9", "4", "tag1", "tag2", "event", "1", "0"
        };
        assertEquals("room", Handling.navigatorField(navigatorFields, 1));
        assertEquals("", Handling.navigatorField(navigatorFields, 99));
        String expectedEventFragment = Crypto.Proc_3_0_6D2AF0(10, null, "");
        expectedEventFragment = Crypto.Proc_3_0_6D2AF0(3, null, expectedEventFragment);
        expectedEventFragment = Crypto.Proc_3_0_6D2AF0(25, null, expectedEventFragment);
        expectedEventFragment = Crypto.Proc_3_0_6D2AF0(9, null, expectedEventFragment);
        expectedEventFragment = Crypto.Proc_3_0_6D2AF0(4, null, expectedEventFragment);
        expectedEventFragment = Crypto.Proc_3_0_6D2AF0(1, null, expectedEventFragment)
            + " room\2owner\2desc\2open\2tag1\2tag2\2event\2" + "1\2H";
        assertEquals(expectedEventFragment, Handling.navigatorEventFragment(navigatorFields));
        String expectedRoomFragment = Crypto.Proc_3_0_6D2AF0(10, null, "");
        expectedRoomFragment = Crypto.Proc_3_0_6D2AF0(3, null, expectedRoomFragment);
        expectedRoomFragment = Crypto.Proc_3_0_6D2AF0(25, null, expectedRoomFragment);
        expectedRoomFragment = Crypto.Proc_3_0_6D2AF0(9, null, expectedRoomFragment);
        expectedRoomFragment = Crypto.Proc_3_0_6D2AF0(4, null, expectedRoomFragment);
        expectedRoomFragment = Crypto.Proc_3_0_6D2AF0(1, null, expectedRoomFragment);
        expectedRoomFragment = Crypto.Proc_3_0_6D2AF0(1, null, expectedRoomFragment);
        expectedRoomFragment = Crypto.Proc_3_0_6D2AF0(0, null, expectedRoomFragment)
            + "room\2owner\2desc\2open\2tag1\2tag2\2event\2H";
        assertEquals(expectedRoomFragment, Handling.navigatorRoomFragment(navigatorFields));
        String navigatorRow = String.join("\t", navigatorFields);
        assertEquals(Crypto.Proc_3_0_6D2AF0(1, null, expectedRoomFragment),
            Handling.navigatorRoomListPayloadFromRows(navigatorRow));
        assertEquals(expectedRoomFragment, Handling.singleNavigatorRoomPayloadFromRows(navigatorRow));
        assertEquals("", Handling.singleNavigatorRoomPayloadFromRows(navigatorRow + "\r" + navigatorRow));
        assertEquals(Crypto.Proc_3_0_6D2AF0(1, null, expectedEventFragment),
            Handling.navigatorEventListPayloadFromRows(navigatorRow));
        assertEquals(Crypto.Proc_3_0_6D2AF0(2, null, expectedEventFragment + expectedRoomFragment),
            Handling.navigatorCombinedRoomListPayloadFromRows(navigatorRow, navigatorRow));
        assertEquals(Crypto.Proc_3_0_6D2AF0(0, null, ""), Handling.navigatorRoomListPayloadFromRows(""));
        assertEquals(Crypto.Proc_3_0_6D2AF0(0, null, ""), Handling.navigatorEventListPayloadFromRows(""));
        String[] officialFields = new String[]{
            "1", "2", "3", "caption", "cap2", "cap3", "7", "8", "9", "10",
            "11", "12", "13", "description", "15", "16", "17", "18", "icon",
            "tag1", "tag2", "22", "model", "files", "250", "5", "6", "7"
        };
        String expectedOfficialRow = Crypto.Proc_3_0_6D2AF0(1, null, "")
            + Crypto.Proc_3_0_6D2AF0(2, null, "")
            + Crypto.Proc_3_0_6D2AF0(3, null, "");
        for (int fieldIndex = 3; fieldIndex <= 24; fieldIndex++) {
            expectedOfficialRow += officialFields[fieldIndex] + '\2';
        }
        expectedOfficialRow += Crypto.Proc_3_0_6D2AF0(5, null, "")
            + Crypto.Proc_3_0_6D2AF0(6, null, "")
            + Crypto.Proc_3_0_6D2AF0(7, null, "");
        String officialRow = String.join("\t", officialFields);
        assertEquals(expectedOfficialRow, Handling.officialNavigatorRowPayload(officialFields));
        assertEquals(expectedOfficialRow, Handling.officialNavigatorRowsPayload(officialRow, false));
        assertEquals(Crypto.Proc_3_0_6D2AF0(1, null, "") + expectedOfficialRow,
            Handling.officialNavigatorRowsPayload(officialRow, true));
        assertEquals(Crypto.Proc_3_0_6D2AF0(0, null, ""), Handling.officialNavigatorRowsPayload("1\t2", true));
        assertEquals("", Handling.Proc_6_122_752280("1\t2", false));
        Licence.global_008292BC = "20\t1\t2\t3\t4\t5\t6\t7\t8\t9\t10\t11\t12\t13\tchair\tseat\t16\t17\tchair_sprite\r"
            + "21\t9\t2\t3\t4\t5\t6\t7\t8\t9\t10\t11\t12\t13\tposter\twall\t16\t17\tposter_sprite";
        String expectedInventoryItem = Crypto.Proc_3_0_6D2AF0(100, null, "0") + "S\2"
            + Crypto.Proc_3_0_6D2AF0(100, null, "")
            + Crypto.Proc_3_0_6D2AF0(20, null, "")
            + Crypto.Proc_3_0_6D2AF0(1, null, "")
            + "a\tb\2"
            + Crypto.Proc_3_0_6D2AF0(4, null, "")
            + "chair\2seat\2chair_sprite\2M\2"
            + Crypto.Proc_3_0_6D2AF0(4, null, "");
        assertEquals(expectedInventoryItem, Handling.inventoryItemPayload(100, 20, "a\bb", 4));
        assertEquals(expectedInventoryItem, Handling.Proc_6_138_7678A0(100, 20, "a\bb", 4));
        String iconInventoryItem = Handling.inventoryItemPayload(101, 21, "", 0);
        assertEquals(true, iconInventoryItem.contains("0" + Crypto.Proc_3_0_6D2AF0(101, null, "") + "I\2"));
        assertEquals(true, iconInventoryItem.contains("poster\2wall\2poster_sprite\2"));
        String[] productTypeCache = new String[22];
        productTypeCache[20] = "1";
        productTypeCache[21] = "9";
        DataManager.global_008292BC = productTypeCache;
        Handling.InventoryPayloads inventoryPayloads = Handling.inventoryPayloadsFromRows("100\t20\ta\bb\t4\r101\t21\t\t0");
        assertEquals(1L, inventoryPayloads.regularCount);
        assertEquals(1L, inventoryPayloads.iconCount);
        assertEquals(expectedInventoryItem, inventoryPayloads.regularPayload);
        assertEquals(iconInventoryItem, inventoryPayloads.iconPayload);
        String tradeOffers = Handling.representedTradeOfferStore("", 2, 100, 20, "a\rb", 4);
        assertEquals("2\t100\t20\tab\t4", tradeOffers);
        tradeOffers = Handling.representedTradeOfferStore(tradeOffers, 2, 101, 21, "", 0);
        assertEquals("2\t100\t20\tab\t4\r2\t101\t21\t\t0", tradeOffers);
        tradeOffers = Handling.representedTradeOfferStore(tradeOffers, 3, 102, 20, "x", 5);
        assertEquals("2\t100\t20\tab\t4\r2\t101\t21\t\t0\r3\t102\t20\tx\t5", tradeOffers);
        tradeOffers = Handling.representedTradeOfferStore(tradeOffers, 2, 100, 20, "new", 6);
        assertEquals("2\t100\t20\tnew\t6\r2\t101\t21\t\t0\r3\t102\t20\tx\t5", tradeOffers);
        assertEquals("'100','101'", Handling.representedTradeOfferSqlIds(tradeOffers, 2));
        assertEquals("100:20\1" + "101:21", Handling.representedTradeOfferLogItems(tradeOffers, 2));
        Handling.TradeOfferItemPayload sourceTradeItems = Handling.representedTradeOfferItemPayload(tradeOffers, 2);
        String expectedSourceTradeItems = Handling.inventoryItemPayload(100, 20, "new", 6) + iconInventoryItem;
        assertEquals(2L, sourceTradeItems.itemCount);
        assertEquals(expectedSourceTradeItems, sourceTradeItems.payload);
        Handling.TradeOfferItemPayload targetTradeItems = Handling.representedTradeOfferItemPayload(tradeOffers, 3);
        String expectedTargetTradeItems = Handling.inventoryItemPayload(102, 20, "x", 5);
        assertEquals(1L, targetTradeItems.itemCount);
        assertEquals(expectedTargetTradeItems, targetTradeItems.payload);
        String expectedTradePayload = Crypto.Proc_3_0_6D2AF0(5, null, "Al");
        expectedTradePayload = Crypto.Proc_3_0_6D2AF0(6, null, expectedTradePayload);
        expectedTradePayload = Crypto.Proc_3_0_6D2AF0(2, null, expectedTradePayload) + expectedSourceTradeItems;
        expectedTradePayload = Crypto.Proc_3_0_6D2AF0(1, null, expectedTradePayload) + expectedTargetTradeItems;
        assertEquals(expectedTradePayload, Handling.representedTradeOfferPayload(tradeOffers, 2, 3, "5", "6"));
        assertEquals("2\t100\t20\tnew\t6\r3\t102\t20\tx\t5", Handling.representedTradeOfferRemove(tradeOffers, 2, 101));
        assertEquals("3\t102\t20\tx\t5", Handling.representedTradeOfferRemove(tradeOffers, 2, 0));
        Handling.FurnitureMoveRequest moveRequest = Handling.furnitureMoveRequestFromPayload("A[100\1" + "3\2" + "4\t2");
        assertEquals(100L, moveRequest.furnitureId);
        assertEquals(3L, moveRequest.positionX);
        assertEquals(4L, moveRequest.positionY);
        assertEquals(2L, moveRequest.rotation);
        Handling.FurnitureMoveRequest wireMoveRequest = Handling.furnitureMoveRequestFromPayload("A");
        assertEquals(1L, wireMoveRequest.furnitureId);
        assertEquals(0L, wireMoveRequest.positionX);
        String expectedPointBalance = Crypto.Proc_3_0_6D2AF0(4, null, "M@")
            + Crypto.Proc_3_0_6D2AF0(1, null, "")
            + Crypto.Proc_3_0_6D2AF0(10, null, "")
            + Crypto.Proc_3_0_6D2AF0(2, null, "")
            + Crypto.Proc_3_0_6D2AF0(20, null, "")
            + Crypto.Proc_3_0_6D2AF0(3, null, "")
            + Crypto.Proc_3_0_6D2AF0(0, null, "")
            + Crypto.Proc_3_0_6D2AF0(4, null, "")
            + Crypto.Proc_3_0_6D2AF0(40, null, "");
        assertEquals(expectedPointBalance, Handling.activityPointBalancePayload("10\t20\t\t40"));
        assertEquals(1L, Handling.pickupFurnitureIdFromPayload("AZA"));
        assertEquals(1L, Handling.pickupFurnitureIdFromPayload("A"));
        Handling.FurnitureCacheState tracked = Handling.trackFurnitureCacheMarker(
            "\1" + "8\2\1" + "5\tstale\2",
            "\1" + "7\2\1" + "7\told\2",
            "\1" + "7\2\1" + "7\troom\2\1" + "9\2",
            5,
            7);
        assertEquals("\1" + "8\2\1" + "5\2", tracked.pendingRoomCache);
        assertEquals("\1" + "7\2", tracked.pendingFurnitureCache);
        assertEquals("\1" + "9\2", tracked.representedRoomCache);
        Handling.FurnitureCacheState removed = Handling.removeFurnitureCacheMarker(
            "\1" + "7\2\1" + "7\troom\2\1" + "8\2",
            "\1" + "7\2\1" + "7\told\2",
            "\1" + "7\2\1" + "7\troom\2\1" + "9\2",
            7);
        assertEquals("\1" + "8\2", removed.pendingRoomCache);
        assertEquals("", removed.pendingFurnitureCache);
        assertEquals("\1" + "9\2", removed.representedRoomCache);
        assertEquals(1L, Handling.nextFurnitureState("chair", 0, 1));
        assertEquals(0L, Handling.nextFurnitureState("chair", 1, 1));
        assertEquals(99L, Handling.nextFurnitureState("bb_score_blue", 98, 0));
        assertEquals(0L, Handling.nextFurnitureState("scoreboard", 99, 0));
        long diceState = Handling.nextFurnitureState("dice_red", 0, 0);
        assertEquals(true, diceState >= 1L && diceState <= 6L);
        assertEquals("AX77\2" + "3\2", Handling.furnitureStatePayload(77, 3));
        Handling.FurnitureStateCache stateCache = Handling.representedFurnitureStateCache(
            "\1" + "5\2\1" + "9\told\2",
            "\1" + "77\2",
            "\1" + "5\t77\t1\2\1" + "77\2\1" + "88\2",
            5,
            77,
            4);
        assertEquals("\1" + "9\told\2\1" + "5\2", stateCache.pendingRoomCache);
        assertEquals("\1" + "77\2", stateCache.pendingFurnitureCache);
        assertEquals("\1" + "5\t77\t4\2", stateCache.representedRoomCache);
        Handling.FurnitureStateCache stateWrite = Handling.representedFurnitureStateWrite(
            "\1" + "5\2\1" + "8\2",
            "\1" + "77\2\1" + "88\2",
            "\1" + "77\2\1" + "77\t5\told\2\1" + "99\t5\tkeep\2",
            5,
            77,
            "new-state");
        assertEquals("\1" + "8\2\1" + "5\2", stateWrite.pendingRoomCache);
        assertEquals("\1" + "88\2\1" + "77\2", stateWrite.pendingFurnitureCache);
        assertEquals("\1" + "99\t5\tkeep\2\1" + "77\t5\tnew-state\2", stateWrite.representedRoomCache);
        Handling.FurnitureStateCache stateWriteNoRoom = Handling.representedFurnitureStateWrite("", "", "", 0, 77, "off");
        assertEquals("", stateWriteNoRoom.pendingRoomCache);
        assertEquals("\1" + "77\2", stateWriteNoRoom.pendingFurnitureCache);
        assertEquals("\1" + "77\t0\toff\2", stateWriteNoRoom.representedRoomCache);
        String expectedWallInventory = "0" + Crypto.Proc_3_0_6D2AF0(9, null,
            Crypto.Proc_3_0_6D2AF0(20, null, "77\2") + ":w=1,2 l=3,4\2data\2");
        assertEquals(expectedWallInventory, Handling.wallInventoryPlacementPayload(77, 20, ":w=1,2 l=3,4", "data", 9));
        assertEquals(expectedWallInventory, Handling.Proc_6_156_7972B0(77, 20, ":w=1,2 l=3,4", "data", 9));
        String expectedFloorPlacement = Crypto.Proc_3_0_6D2AF0(99, null, "0");
        expectedFloorPlacement = Crypto.Proc_3_0_6D2AF0(1, null, expectedFloorPlacement);
        expectedFloorPlacement = "0" + Crypto.Proc_3_0_6D2AF0(2, null, expectedFloorPlacement);
        expectedFloorPlacement = "0" + Crypto.Proc_3_0_6D2AF0(4, null, expectedFloorPlacement);
        expectedFloorPlacement = Crypto.Proc_3_0_6D2AF0(3, null, expectedFloorPlacement) + "state\2";
        expectedFloorPlacement = Crypto.Proc_3_0_6D2AF0(7, null, expectedFloorPlacement) + "a\tb\tc\2M";
        expectedFloorPlacement = Crypto.Proc_3_0_6D2AF0(20, null, expectedFloorPlacement);
        assertEquals(expectedFloorPlacement, Handling.floorItemPlacementPayload(99, 1, 2, 4, 3, "state", "a\bb{{9}}c", 7, 20));
        assertEquals(expectedFloorPlacement, Handling.Proc_6_161_7B2EE0(99, 1, 2, 4, 3, "state", "a\bb{{9}}c", 7, 20));
        assertEquals("0DAQBHHIIKHJHPAHQA\2SAHPBhttp://www.alpha-series.com/\2QBH", Handling.systemHandshakePayload(""));
        assertEquals("0FMT\2SAHPBhttp://www.alpha-series.com/\2QBH", Handling.systemHandshakePayload("FMT"));
        assertEquals("ticket one", Handling.handlingLoginTicketFromPayload("F_ticket\none"));
        assertEquals("ticket two", Handling.handlingLoginTicketFromPayload(" F_ticket two "));
        assertEquals(Crypto.Proc_3_0_6D2AF0(2, null, Crypto.Proc_3_0_6D2AF0(300, null, "Fv") + "H"),
            Handling.handlingLoginActivityPointPayload(2, 300));
        assertEquals(Crypto.Proc_3_0_6D2AF0(2, null, Crypto.Proc_3_0_6D2AF0(300, null, "Fv")) + "H",
            Handling.representedActivityPointAwardPayload(2, 300));
        Handling.ActivityPointAward pointAward = Handling.activityPointAwardDecision(120, 2, 60, 500, 25, 300);
        assertEquals(true, pointAward.shouldAward);
        assertEquals(325L, pointAward.newPoints);
        assertEquals(Handling.representedActivityPointAwardPayload(2, 325), pointAward.payload);
        assertEquals(false, Handling.activityPointAwardDecision(121, 2, 60, 500, 25, 300).shouldAward);
        String busyCache = "[4]a\2b\2c\2d\2e\2" + "1[5]a\2b\2c\2d\2e\2" + "0";
        assertEquals(true, Handling.isSocketMarkedBusy(busyCache, 4));
        assertEquals(false, Handling.isSocketMarkedBusy(busyCache, 5));
        assertEquals(false, Handling.isSocketMarkedBusy("[6]a\2b", 6));
        assertEquals(false, Handling.isSocketMarkedBusy(busyCache, 9));
        String expectedOwnProfile = "@E7\2Alice\2hello\2F\2\2\2H\2HIH";
        expectedOwnProfile = Crypto.Proc_3_0_6D2AF0(4, null, expectedOwnProfile);
        expectedOwnProfile = Crypto.Proc_3_0_6D2AF0(2, null, expectedOwnProfile);
        assertEquals(expectedOwnProfile, Handling.ownProfilePayload("7\tAlice\thello\tfemale\t4\t2"));
        assertEquals(50L, Handling.soundSettingFromWire("Ce50"));
        assertEquals(0L, Handling.soundSettingFromWire("Ce101"));
        String expectedGroupPayload = Crypto.Proc_3_0_6D2AF0(55, null, "Dt")
            + "Group\2Desc\2BADGE\2"
            + Crypto.Proc_3_0_6D2AF0(77, null, "")
            + "H";
        assertEquals(expectedGroupPayload, Handling.loginGroupPayload(55, "Group\tDesc\tBADGE\t77"));
        String expectedQuestPayload = Crypto.Proc_3_0_6D2AF0(7, null, "") + "Quest\2"
            + Crypto.Proc_3_0_6D2AF0(3, null, "")
            + Crypto.Proc_3_0_6D2AF0(44, null, "")
            + Crypto.Proc_3_0_6D2AF0(2, null, "")
            + Crypto.Proc_3_0_6D2AF0(5, null, "")
            + Crypto.Proc_3_0_6D2AF0(1, null, "")
            + Crypto.Proc_3_0_6D2AF0(0, null, "");
        assertEquals(expectedQuestPayload, Handling.questCompletionPayload(7, "Quest", 3, 44, 2, 5, 0));
        String questRows = "10\t1\tFirst\t\t5\t2\tvisit\t0\t7\t3\t30\r11\t2\tSecond\t\t6\t2\tvisit\t0\t7\t4\t0";
        assertEquals(10L, Handling.questRequestIdFromWire("p^" + Crypto.Proc_3_0_6D2AF0(10, null, ""), "p^"));
        assertEquals(11L, Handling.nextQuestId(questRows, "10\t1"));
        assertEquals(10L, Handling.nextQuestId(questRows, ""));
        Handling.QuestProgressDecision waitDecision = Handling.questProgressDecision("10\t10\t1\t0\t\t1", questRows, 0);
        assertEquals(10L, waitDecision.questId);
        assertEquals(3L, waitDecision.amountRequired);
        assertEquals(true, waitDecision.shouldScheduleWait);
        assertEquals(true, waitDecision.shouldSendList);
        Handling.QuestProgressDecision completeDecision = Handling.questProgressDecision("10\t10\t3\t0\t0\t3", questRows, 0);
        assertEquals(true, completeDecision.shouldComplete);
        String expectedQuestListRow = Crypto.Proc_3_0_6D2AF0(7, null, "") + "First\2"
            + Crypto.Proc_3_0_6D2AF0(10, null, "")
            + Crypto.Proc_3_0_6D2AF0(1, null, "")
            + Crypto.Proc_3_0_6D2AF0(1, null, "")
            + Crypto.Proc_3_0_6D2AF0(1, null, "")
            + Crypto.Proc_3_0_6D2AF0(0, null, "")
            + Crypto.Proc_3_0_6D2AF0(1, null, "")
            + Crypto.Proc_3_0_6D2AF0(3, null, "")
            + Crypto.Proc_3_0_6D2AF0(2, null, "")
            + Crypto.Proc_3_0_6D2AF0(5, null, "")
            + "HHH\2\2H\2HHH"
            + Crypto.Proc_3_0_6D2AF0(12, null, "");
        String expectedSecondQuestListRow = Crypto.Proc_3_0_6D2AF0(7, null, "") + "Second\2"
            + Crypto.Proc_3_0_6D2AF0(11, null, "")
            + Crypto.Proc_3_0_6D2AF0(2, null, "")
            + Crypto.Proc_3_0_6D2AF0(2, null, "")
            + Crypto.Proc_3_0_6D2AF0(0, null, "")
            + Crypto.Proc_3_0_6D2AF0(0, null, "")
            + Crypto.Proc_3_0_6D2AF0(0, null, "")
            + Crypto.Proc_3_0_6D2AF0(4, null, "")
            + Crypto.Proc_3_0_6D2AF0(2, null, "")
            + Crypto.Proc_3_0_6D2AF0(6, null, "")
            + "HHH\2\2H\2HHH"
            + Crypto.Proc_3_0_6D2AF0(0, null, "");
        assertEquals(Crypto.Proc_3_0_6D2AF0(0, null, Crypto.Proc_3_0_6D2AF0(2, null, "L`"))
                + expectedQuestListRow + expectedSecondQuestListRow,
            Handling.questListPayload(questRows, "10\t0\t0\t1\t2026-01-01\t1\t12"));
        Functions.global_0082928C = "[com.client.messenger.follow.enabled=1]";
        String expectedOnlineFriend = Crypto.Proc_3_0_6D2AF0(5, null, "0") + "Alice\2";
        expectedOnlineFriend = Crypto.Proc_3_0_6D2AF0(3, null, expectedOnlineFriend);
        expectedOnlineFriend = Crypto.Proc_3_0_6D2AF0(1, null, expectedOnlineFriend);
        expectedOnlineFriend = Crypto.Proc_3_0_6D2AF0(1, null, expectedOnlineFriend) + "motto\2today\2\2";
        assertEquals(expectedOnlineFriend, Handling.messengerFriendPayload(5, "Alice", "motto", "fig", 3, 2, 1, "today", 1));
        String expectedOfflineFriend = Crypto.Proc_3_0_6D2AF0(6, null, "0") + "Bob\2";
        expectedOfflineFriend = Crypto.Proc_3_0_6D2AF0(2, null, expectedOfflineFriend);
        expectedOfflineFriend = Crypto.Proc_3_0_6D2AF0(0, null, expectedOfflineFriend)
            + "\2Hfig2\2" + "4\2yesterday\2\2";
        assertEquals(expectedOfflineFriend, Handling.Proc_6_166_7BE940(6, "Bob", "motto2", "fig2", 2, 0, 0, "yesterday", 4));
        assertEquals(expectedOnlineFriend, Handling.messengerFriendSummaryPayloadFromRow("5\tAlice\tmotto\tfig\t3\t22\ttoday", 1));
        String expectedSearch = Crypto.Proc_3_0_6D2AF0(8, null, "") + "Carol\2hi\2";
        expectedSearch = "1" + Crypto.Proc_3_0_6D2AF0(1, null, expectedSearch) + "H\2nick\2fig\2now\2";
        assertEquals(expectedSearch, Handling.messengerSearchResultPayload("8", "Carol", "fig", "hi", "nick", "now", 1));
        Licence.global_0082927C = new String[]{"10", "x", "20", "x", "30"};
        assertEquals(20L, Handling.messengerMaxFriends(2));
        assertEquals(0L, Handling.messengerMaxFriends(99));
        assertEquals("hello world", Handling.requestTextFromWirePayload("@i@Khello world", "@i", 50));
        assertEquals("abc", Handling.requestTextFromWirePayload("@g@Cabc", "@g", 2_000));
        assertEquals("he", Handling.requestTextFromWirePayload("@g@Khello", "@g", 2));
        Handling.FriendTargetList deleteAll = Handling.friendDeleteTargetsFromPayload("@fA");
        assertEquals(true, deleteAll.deleteAllPending);
        Handling.FriendTargetList deleteTargets = Handling.friendDeleteTargetsFromPayload("@fCABA");
        assertEquals(false, deleteTargets.deleteAllPending);
        assertEquals("1,2", deleteTargets.targetList);
        assertEquals(2L, deleteTargets.targetCount);
        assertEquals(Crypto.Proc_3_0_6D2AF0(2, null, "@MH") + expectedOnlineFriend + expectedOfflineFriend,
            Handling.messengerAcceptedFriendsPayload(expectedOnlineFriend + expectedOfflineFriend, 2));
        assertEquals(Crypto.Proc_3_0_6D2AF0(44, null, ""), Handling.messengerRemovedIdPayload(44));
        assertEquals(Crypto.Proc_3_0_6D2AF0(1, null, "@MM") + Crypto.Proc_3_0_6D2AF0(44, null, ""),
            Handling.messengerRemoveFriendsPayload(Handling.messengerRemovedIdPayload(44), 1));
        assertEquals(Crypto.Proc_3_0_6D2AF0(88, null, "DD") + "H", Handling.messengerRequestAcceptedCallerPayload(88));
        assertEquals("DDH\2", Handling.messengerRequestDeniedPayload());
        assertEquals(Crypto.Proc_3_0_6D2AF0(5, null, "BD") + "Alice\2" + "5\2",
            Handling.messengerRequestNotifyPayload(5, "Alice"));
        String expectedPendingRequestRows = "0" + Crypto.Proc_3_0_6D2AF0(5, null, "") + "Alice\2Alice\2"
            + "0" + Crypto.Proc_3_0_6D2AF0(6, null, "") + "Bob\2Bob\2";
        String expectedPendingPayload = Crypto.Proc_3_0_6D2AF0(2, null, "Dz")
            + Crypto.Proc_3_0_6D2AF0(2, null, "Dz");
        expectedPendingPayload = Crypto.Proc_3_0_6D2AF0(2, null, expectedPendingPayload) + expectedPendingRequestRows;
        assertEquals(expectedPendingPayload, Handling.messengerPendingRequestsPayload("5\tAlice\r6\tBob"));
        String friendRows = "5\tAlice\t22\tfig\tmotto\t3\ttoday\r6\tBob\t0\tfig2\tmotto2\t2\tyesterday";
        String expectedListedOfflineFriend = Crypto.Proc_3_0_6D2AF0(6, null, "0") + "Bob\2";
        expectedListedOfflineFriend = Crypto.Proc_3_0_6D2AF0(2, null, expectedListedOfflineFriend);
        expectedListedOfflineFriend = Crypto.Proc_3_0_6D2AF0(0, null, expectedListedOfflineFriend)
            + "\2Hfig2\2" + "1\2yesterday\2\2";
        String expectedFriendList = Crypto.Proc_3_0_6D2AF0(10, null, "@L")
            + Crypto.Proc_3_0_6D2AF0(20, null, "")
            + Crypto.Proc_3_0_6D2AF0(30, null, "")
            + Crypto.Proc_3_0_6D2AF0(2, null, "")
            + expectedOnlineFriend
            + expectedListedOfflineFriend
            + "PYH";
        assertEquals(expectedFriendList, Handling.messengerFriendListPayload(friendRows, 10, 20, 30));
        Handling.FriendTargetList removeTargets = Handling.friendRemoveTargetsFromPayload("@hCBCA", "2");
        assertEquals("3,1", removeTargets.targetList);
        assertEquals(2L, removeTargets.targetCount);
        String expectedRacePayload = Crypto.Proc_3_0_6D2AF0(2, null, "L{dog\2")
            + Crypto.Proc_3_0_6D2AF0(1, null, "") + "II"
            + Crypto.Proc_3_0_6D2AF0(3, null, "") + "II";
        assertEquals(expectedRacePayload, Handling.petRaceListPayload("dog", "1\t1\t0\t0\tA\r2\t2\t5\t0\tB\r3\t3\t2\t1\tC", 3, 1));
        String expectedPetRow = "0" + Crypto.Proc_3_0_6D2AF0(10, null, "") + "Rex\2"
            + Crypto.Proc_3_0_6D2AF0(1, null, "")
            + Crypto.Proc_3_0_6D2AF0(2, null, "")
            + "0ff00aa\2"
            + Crypto.Proc_3_0_6D2AF0(4, null, "");
        assertEquals(expectedPetRow, Handling.petInventoryRowPayload(new String[]{"10", "Rex", "1 2 FF00AA", "4"}));
        String expectedPetList = Crypto.Proc_3_0_6D2AF0(2, null, "IX")
            + expectedPetRow
            + "0" + Crypto.Proc_3_0_6D2AF0(11, null, "") + "Mia\2"
            + Crypto.Proc_3_0_6D2AF0(3, null, "")
            + Crypto.Proc_3_0_6D2AF0(0, null, "")
            + "0\2"
            + Crypto.Proc_3_0_6D2AF0(0, null, "");
        assertEquals(expectedPetList, Handling.petInventoryListPayload("10\tRex\t1 2 FF00AA\t4\r11\tMia\t3\t0"));
        assertEquals(0L, Handling.petNameValidationCode("Rex"));
        assertEquals(1L, Handling.petNameValidationCode("abcdefghijklmnopqrstuvwxyzabcde"));
        assertEquals(2L, Handling.petNameValidationCode(""));
        assertEquals(2L, Handling.petNameValidationCode("Rex1"));
        assertEquals(Crypto.Proc_3_0_6D2AF0(2, null, "@d"), Handling.petNameValidationPayload("Rex1"));
        String commandRows = "1\t0\tgst ok\r2\t3\tgst jump\r3\t5\tgst high";
        String expectedCommandList = Crypto.Proc_3_0_6D2AF0(3, null, "I]")
            + Crypto.Proc_3_0_6D2AF0(3, null, "")
            + "0" + Crypto.Proc_3_0_6D2AF0(1, null, "")
            + "0" + Crypto.Proc_3_0_6D2AF0(2, null, "")
            + "0" + Crypto.Proc_3_0_6D2AF0(3, null, "")
            + Crypto.Proc_3_0_6D2AF0(2, null, "")
            + "0" + Crypto.Proc_3_0_6D2AF0(1, null, "")
            + "0" + Crypto.Proc_3_0_6D2AF0(2, null, "");
        assertEquals(expectedCommandList, Handling.petCommandListPayload(3, commandRows));
        Handling.PetCommandAction commandAction = Handling.petCommandAction(2, commandRows);
        assertEquals(true, commandAction.found);
        assertEquals(3L, commandAction.requiredLevel);
        assertEquals("gst jump", commandAction.action);
        assertEquals(false, Handling.petCommandAction(9, commandRows).found);
        String expectedPetStatus = "IY" + Crypto.Proc_3_0_6D2AF0(50, null, "") + "Rex\2"
            + Crypto.Proc_3_0_6D2AF0(2, null, "")
            + Crypto.Proc_3_0_6D2AF0(7, null, "")
            + Crypto.Proc_3_0_6D2AF0(100, null, "")
            + Crypto.Proc_3_0_6D2AF0(90, null, "")
            + Crypto.Proc_3_0_6D2AF0(4, null, "") + "1 2 ff\2"
            + Crypto.Proc_3_0_6D2AF0(12, null, "")
            + Crypto.Proc_3_0_6D2AF0(5, null, "") + "Owner\2";
        assertEquals(expectedPetStatus, Handling.representedPetStatusPayload(50,
            new String[]{"10", "Rex", "1 2 ff", "2", "7", "100", "90", "4", "12", "5", "Owner"}));
        String levelRows = "1\t10\r2\t20\r3\t30";
        assertEquals(20L, Handling.petLevelMaxExperience(2, levelRows));
        Handling.PetExperienceUpdate expUpdate = Handling.petExperienceUpdate(50, "Rex", "1 2 ff", 2, 18, 100, 90, 4, 3, levelRows);
        assertEquals(3L, expUpdate.petLevel);
        assertEquals(0L, expUpdate.petExperience);
        assertEquals(true, expUpdate.leveledUp);
        assertEquals("IY" + Crypto.Proc_3_0_6D2AF0(50, null, "") + "Rex\2"
            + Crypto.Proc_3_0_6D2AF0(3, null, "")
            + Crypto.Proc_3_0_6D2AF0(0, null, "")
            + Crypto.Proc_3_0_6D2AF0(100, null, "")
            + Crypto.Proc_3_0_6D2AF0(90, null, "")
            + Crypto.Proc_3_0_6D2AF0(4, null, "") + "1 2 ff\2", expUpdate.statusPayload);
        assertEquals("Ia" + Crypto.Proc_3_0_6D2AF0(50, null, "")
            + Crypto.Proc_3_0_6D2AF0(3, null, "")
            + Crypto.Proc_3_0_6D2AF0(0, null, ""), expUpdate.experiencePayload);
        assertEquals("I^" + Crypto.Proc_3_0_6D2AF0(50, null, "")
            + Crypto.Proc_3_0_6D2AF0(5, null, "")
            + Crypto.Proc_3_0_6D2AF0(6, null, "")
            + "Rex\2" + "1 2 ff\2", Handling.petScratchPayload(50, 5, 6, "Rex", "1 2 ff"));
        assertEquals("IZ" + Crypto.Proc_3_0_6D2AF0(50, null, "") + "gst jump\2"
            + Crypto.Proc_3_0_6D2AF0(2, null, ""), Handling.petCommandActionPayload(50, "gst jump", 2));
        Licence.global_008292D4 = "";
        Licence.global_00829358 = "";
        String[] representedBotFields = new String[]{
            "501", "Guide", "hello", "speech", "responses", "2", "3", "0.5", "4", "1 2 ff",
            "", "3", "4", "cache", "submit", "1", "6"
        };
        assertEquals("3\2" + "501\2Guide\2hello\2speech\2responses\2" + "2\2" + "3\2" + "0.5\2" + "4\2"
            + "1 2 ff\2" + "3\2" + "4\2cache\2submit\2" + "1\2" + "6",
            Handling.representedBotRecordFromFields(3, representedBotFields));
        long botEntityId = Handling.allocateRepresentedBot(3, representedBotFields);
        assertEquals(1L, botEntityId);
        assertEquals("[1]", Licence.global_008292D4);
        assertEquals("501", Handling.representedBotRecordField(botEntityId, 1));
        assertEquals(501L, Handling.representedBotRecordLong(botEntityId, 1));
        assertEquals(1L, Handling.representedBotEntityFromBotId(501));
        assertEquals("1", Handling.representedBotEntitiesForRoom(3, 501));
        assertEquals(true, Handling.isRepresentedBotAllocated(3, 501));
        Handling.storeRepresentedBotPosition(botEntityId, 5, 6, "1.0", 7);
        assertEquals(5L, Handling.representedBotRecordLong(botEntityId, 6));
        assertEquals("1.0", Handling.representedBotRecordField(botEntityId, 8));
        String expectedBotEntry = "@\\" + Crypto.Proc_3_0_6D2AF0(botEntityId, null, "")
            + "Guide\2" + "5 6 1.0\2" + "7\2" + "1 2 ff\2";
        assertEquals(expectedBotEntry, Handling.representedBotRoomEntryPayload(botEntityId));
        Handling.removeRepresentedBotRecord(botEntityId);
        assertEquals("", Handling.representedBotRecordText(botEntityId));
        assertEquals("", Licence.global_008292D4);
        String expectedProfile = Crypto.Proc_3_0_6D2AF0(9, null, "Jf")
            + "Alice\2motto\2"
            + Crypto.Proc_3_0_6D2AF0(123, null, "")
            + "fig\2";
        assertEquals(expectedProfile, Handling.representedRoomUserProfilePayload(9, "Alice", "motto", 123, "fig"));
        String equippedBadges = "0" + Crypto.Proc_3_0_6D2AF0(1, null, "") + "ACH1\2"
            + "0" + Crypto.Proc_3_0_6D2AF0(3, null, "") + "VIP\2";
        assertEquals(Crypto.Proc_3_0_6D2AF0(2, null, "") + equippedBadges,
            Handling.equippedBadgePayload("ACH1\t1\t10\rVIP\t3\t11"));
        String expectedBadgeInventory = Crypto.Proc_3_0_6D2AF0(2, null, "Ce")
            + "0" + Crypto.Proc_3_0_6D2AF0(20, null, "") + "ACH2\2"
            + "0" + Crypto.Proc_3_0_6D2AF0(21, null, "") + "MOD\2"
            + Crypto.Proc_3_0_6D2AF0(2, null, "") + equippedBadges;
        assertEquals(expectedBadgeInventory, Handling.badgeInventoryPayload("ACH2\t0\t20\rMOD\t0\t21",
            Crypto.Proc_3_0_6D2AF0(2, null, "") + equippedBadges));
        assertEquals("Cd" + Crypto.Proc_3_0_6D2AF0(5, null, "") + Crypto.Proc_3_0_6D2AF0(2, null, "") + equippedBadges,
            Handling.badgeDisplayPayload(5, Crypto.Proc_3_0_6D2AF0(2, null, "") + equippedBadges));
        assertEquals(Crypto.Proc_3_0_6D2AF0(2, null, "") + "one\2two\2", Handling.tagListPayload("one\rtwo"));
        assertEquals("E^" + Crypto.Proc_3_0_6D2AF0(5, null, "") + Crypto.Proc_3_0_6D2AF0(2, null, "") + "one\2two\2",
            Handling.tagDisplayPayload(5, Handling.tagListPayload("one\rtwo")));
        String badgeWire = "A@CONEA@CTWO";
        String[] badgeSlots = Handling.badgeUpdateSelectionsFromWire("B^" + badgeWire);
        assertEquals("ONE", badgeSlots[0]);
        assertEquals("TWO", badgeSlots[1]);
        assertEquals("", badgeSlots[2]);
        assertEquals("", badgeSlots[3]);
        assertEquals("", badgeSlots[4]);
        assertEquals(1L, Handling.idRequestFromWire("CkA", "Ck"));
        assertEquals(Crypto.Proc_3_0_6D2AF0(0, null, Crypto.Proc_3_0_6D2AF0(1, null, "G{")),
            Handling.recyclerStatusPayload(42, 0));
        assertEquals(7L, Handling.pollIdFromWire("Ck" + Crypto.Proc_3_0_6D2AF0(7, null, ""), "Ck"));
        Handling.PollAnswerSubmission pollAnswer = Handling.pollAnswerFromWire("Cl"
            + Crypto.Proc_3_0_6D2AF0(7, null, "")
            + Crypto.Proc_3_0_6D2AF0(8, null, "")
            + Crypto.Proc_3_0_6D2AF0(4, null, "")
            + "@Cyes", "Cl");
        assertEquals(true, pollAnswer.valid);
        assertEquals(7L, pollAnswer.pollId);
        assertEquals(8L, pollAnswer.questionId);
        assertEquals(4L, pollAnswer.answerValue);
        assertEquals("yes", pollAnswer.answerText);
        Handling.PollAnswerSubmission numericPollAnswer = Handling.pollAnswerFromWire("Cl"
            + Crypto.Proc_3_0_6D2AF0(7, null, "")
            + Crypto.Proc_3_0_6D2AF0(8, null, "")
            + Crypto.Proc_3_0_6D2AF0(4, null, ""), "Cl");
        assertEquals("4", numericPollAnswer.answerText);
        Map<Long, String> pollAnswers = new HashMap<>();
        pollAnswers.put(8L, "1\t8\tYes\r2\t8\tNo");
        String expectedQuestionPayload = Crypto.Proc_3_0_6D2AF0(8, null, "");
        expectedQuestionPayload = Crypto.Proc_3_0_6D2AF0(1, null, expectedQuestionPayload);
        expectedQuestionPayload = Crypto.Proc_3_0_6D2AF0(2, null, expectedQuestionPayload);
        expectedQuestionPayload += "Question?\2";
        expectedQuestionPayload = Crypto.Proc_3_0_6D2AF0(2, null, expectedQuestionPayload);
        expectedQuestionPayload = Crypto.Proc_3_0_6D2AF0(0, null, expectedQuestionPayload);
        expectedQuestionPayload = Crypto.Proc_3_0_6D2AF0(2, null, expectedQuestionPayload) + "Yes\2No\2";
        String expectedPollPayload = Crypto.Proc_3_0_6D2AF0(7, null, "D}") + "Title\2Thanks\2";
        expectedPollPayload = Crypto.Proc_3_0_6D2AF0(1, null, expectedPollPayload) + expectedQuestionPayload;
        assertEquals(expectedPollPayload, Handling.pollPayloadFromRows("7\tTitle\tThanks", "8\tQuestion?\t2", pollAnswers));
        String recyclerWire = "F^" + Crypto.Proc_3_0_6D2AF0(5, null, "")
            + Crypto.Proc_3_0_6D2AF0(10, null, "")
            + Crypto.Proc_3_0_6D2AF0(11, null, "")
            + Crypto.Proc_3_0_6D2AF0(12, null, "")
            + Crypto.Proc_3_0_6D2AF0(13, null, "")
            + Crypto.Proc_3_0_6D2AF0(14, null, "");
        Handling.RecyclerSelection recyclerSelection = Handling.recyclerSelectionFromWire(recyclerWire);
        assertEquals(true, recyclerSelection.valid);
        assertEquals(5L, recyclerSelection.requestedCount);
        assertEquals("10,11,12,13,14", recyclerSelection.selectedItems);
        Handling.RecyclerSelection duplicateRecyclerSelection = Handling.recyclerSelectionFromWire("F^"
            + Crypto.Proc_3_0_6D2AF0(5, null, "")
            + Crypto.Proc_3_0_6D2AF0(10, null, "")
            + Crypto.Proc_3_0_6D2AF0(11, null, "")
            + Crypto.Proc_3_0_6D2AF0(10, null, "")
            + Crypto.Proc_3_0_6D2AF0(13, null, "")
            + Crypto.Proc_3_0_6D2AF0(14, null, ""));
        assertEquals(false, duplicateRecyclerSelection.valid);
        String achievementRow = "42\tACH_\t10\t5\t3\t7\t2";
        String expectedAchievementReward = Crypto.Proc_3_0_6D2AF0(1, null, "Fu");
        expectedAchievementReward = Crypto.Proc_3_0_6D2AF0(42, null, expectedAchievementReward);
        expectedAchievementReward = Crypto.Proc_3_0_6D2AF0(99, null, expectedAchievementReward) + "ACH_2\2";
        expectedAchievementReward = Crypto.Proc_3_0_6D2AF0(5, null,
            Crypto.Proc_3_0_6D2AF0(7, null, expectedAchievementReward)) + "HHH\2" + "3\2";
        assertEquals(expectedAchievementReward, Handling.achievementRewardPayload(1, achievementRow, 2, 99));
        assertEquals(Crypto.Proc_3_0_6D2AF0(2, null,
            Crypto.Proc_3_0_6D2AF0(5, null, Crypto.Proc_3_0_6D2AF0(7, null, "Fv"))),
            Handling.achievementAwardPayload(achievementRow));
        Map<String, Long> achievementLevels = new HashMap<>();
        achievementLevels.put("ACH_", 2L);
        String expectedAchievementEntry = Crypto.Proc_3_0_6D2AF0(42, null, "");
        expectedAchievementEntry = Crypto.Proc_3_0_6D2AF0(2, null, expectedAchievementEntry);
        expectedAchievementEntry = Crypto.Proc_3_0_6D2AF0(20, null, expectedAchievementEntry);
        expectedAchievementEntry = Crypto.Proc_3_0_6D2AF0(10, null, expectedAchievementEntry);
        expectedAchievementEntry = Crypto.Proc_3_0_6D2AF0(5, null, expectedAchievementEntry);
        expectedAchievementEntry = Crypto.Proc_3_0_6D2AF0(7, null, expectedAchievementEntry);
        expectedAchievementEntry = Crypto.Proc_3_0_6D2AF0(2, null, expectedAchievementEntry);
        expectedAchievementEntry = Crypto.Proc_3_0_6D2AF0(3, null, expectedAchievementEntry) + "ACH_\2" + "2\2";
        assertEquals(Crypto.Proc_3_0_6D2AF0(1, null, "Ft") + expectedAchievementEntry,
            Handling.achievementListPayload(achievementRow, achievementLevels));
        Handling.AchievementProgressDecision achievementDecision = Handling.achievementProgressDecision(
            achievementRow, 42, achievementLevels, 30);
        assertEquals(0L, achievementDecision.achievementIndex);
        assertEquals(3L, achievementDecision.nextLevel);
        assertEquals(30L, achievementDecision.requiredProgress);
        assertEquals(true, achievementDecision.shouldReward);
        assertEquals("5;1;7;1;5;0;", Handling.wiredSpecialStatePayload(1507));
        assertEquals("", Handling.wiredSpecialStatePayload(1));
        String wiredRecord = Handling.wiredRecordText(502, 44, "100;101", "7;8", "txt", "9");
        assertEquals("\1" + "502\2" + "44\3" + "100;101\4" + "7;8\5" + "txt\6" + "9", wiredRecord);
        assertEquals("502", Handling.wiredRecordField(wiredRecord, 0));
        assertEquals("44", Handling.wiredRecordField(wiredRecord, 1));
        assertEquals("100;101", Handling.wiredRecordField(wiredRecord, 2));
        assertEquals("7;8", Handling.wiredRecordField(wiredRecord, 3));
        assertEquals("txt", Handling.wiredRecordField(wiredRecord, 4));
        assertEquals("9", Handling.wiredRecordField(wiredRecord, 5));
        String wiredWire = "ok" + Crypto.Proc_3_0_6D2AF0(44, null, "")
            + Crypto.Proc_3_0_6D2AF0(2, null, "")
            + Crypto.Proc_3_0_6D2AF0(7, null, "")
            + Crypto.Proc_3_0_6D2AF0(8, null, "")
            + "@Ctxt"
            + Crypto.Proc_3_0_6D2AF0(2, null, "")
            + Crypto.Proc_3_0_6D2AF0(100, null, "")
            + Crypto.Proc_3_0_6D2AF0(101, null, "")
            + Crypto.Proc_3_0_6D2AF0(9, null, "");
        assertEquals(wiredRecord, Handling.wiredEditRecordFromWire(wiredWire, "ok", 502, true));
        String replacementRecord = Handling.wiredRecordText(502, 44, "102", "5", "next", "3");
        String otherRecord = Handling.wiredRecordText(503, 45, "200", "1", "", "");
        assertEquals(replacementRecord + "\n" + otherRecord,
            Handling.wiredCacheWithRecord(wiredRecord + "\n" + otherRecord, replacementRecord));
        assertEquals(true, Handling.wiredSelectedItemsExist("100;101", "99,100,101"));
        assertEquals(false, Handling.wiredSelectedItemsExist("100;101", "99,100"));
        Handling.WiredApplyResult wiredApply = Handling.wiredApplySelected("100;101,102", "5;ignored", 0, "100;102");
        assertEquals(2L, wiredApply.appliedCount);
        assertEquals(Handling.furnitureStatePayload(100, 5) + Handling.furnitureStatePayload(102, 5), wiredApply.statePayloads);
        Handling.WiredApplyResult wiredOverride = Handling.wiredApplySelected("100", "7", 101, "100;101");
        assertEquals(1L, wiredOverride.appliedCount);
        assertEquals(Handling.furnitureStatePayload(101, 7), wiredOverride.statePayloads);
        String songInfoWire = "C]" + Crypto.Proc_3_0_6D2AF0(2, null, "")
            + Crypto.Proc_3_0_6D2AF0(50, null, "")
            + Crypto.Proc_3_0_6D2AF0(51, null, "");
        Handling.SongInfoRequest songInfoRequest = Handling.songInfoRequestFromWire(songInfoWire);
        assertEquals(2L, songInfoRequest.requestedCount);
        assertEquals("50,51", songInfoRequest.requestedIds);
        String expectedCdPayload = Crypto.Proc_3_0_6D2AF0(50, null, "");
        expectedCdPayload = Crypto.Proc_3_0_6D2AF0(3, null, expectedCdPayload) + "Song A\2Author A\2sound-a\2";
        expectedCdPayload += Crypto.Proc_3_0_6D2AF0(4, null, Crypto.Proc_3_0_6D2AF0(51, null, ""))
            + "Song B\2Author B\2sound-b\2";
        assertEquals(Crypto.Proc_3_0_6D2AF0(2, null, "Dl") + expectedCdPayload,
            Handling.songInfoPayload("Song A\t3\tAuthor A\tsound-a\t50\rSong B\t4\tAuthor B\tsound-b\t51"));
        assertEquals("\1" + "200\2" + "keep\1" + "300\2",
            Handling.removeSoundMachineMarkers("\1" + "100\2\1" + "200\2" + "keep\1" + "300\2", 100, 0));
        Handling.JukeboxAddRequest addRequest = Handling.jukeboxAddRequestFromWire("C" + '\177'
            + Crypto.Proc_3_0_6D2AF0(77, null, "")
            + Crypto.Proc_3_0_6D2AF0(2, null, ""));
        assertEquals(77L, addRequest.diskFurnitureId);
        assertEquals(2L, addRequest.playlistOrder);
        assertEquals(true, Handling.jukeboxCanAddDisk(2, "1", 1, 10));
        assertEquals(false, Handling.jukeboxCanAddDisk(3, "1", 1, 10));
        assertEquals(true, Handling.jukeboxCanAddDisk(0, "", 0, 0));
        assertEquals(3L, Handling.jukeboxRemoveOrderFromWire("D@" + Crypto.Proc_3_0_6D2AF0(3, null, "")));
        String expectedPlaylist = Crypto.Proc_3_0_6D2AF0(2, null, "");
        expectedPlaylist += Crypto.Proc_3_0_6D2AF0(40, null, "");
        expectedPlaylist += Crypto.Proc_3_0_6D2AF0(3, null, "");
        expectedPlaylist += Crypto.Proc_3_0_6D2AF0(41, null, "");
        assertEquals(Crypto.Proc_3_0_6D2AF0(5, null, Crypto.Proc_3_0_6D2AF0(2, null, "EN")) + expectedPlaylist,
            Handling.jukeboxPlaylistPayload(5, "2\t40\r3\t41"));
        assertEquals(Crypto.Proc_3_0_6D2AF0(2, null, "EM") + expectedPlaylist,
            Handling.songDiskInventoryPayload("2\t40\r3\t41"));
        String expectedPlayback = Crypto.Proc_3_0_6D2AF0(10, null, "EG");
        expectedPlayback = Crypto.Proc_3_0_6D2AF0(3, null, expectedPlayback);
        expectedPlayback = Crypto.Proc_3_0_6D2AF0(40, null, expectedPlayback);
        expectedPlayback = Crypto.Proc_3_0_6D2AF0(2, null, expectedPlayback);
        expectedPlayback = Crypto.Proc_3_0_6D2AF0(0, null, Crypto.Proc_3_0_6D2AF0(0, null, expectedPlayback));
        assertEquals(expectedPlayback, Handling.jukeboxPlaybackPayload(10, 3, 40, 2));
        Map<Long, String> staffNames = new HashMap<>();
        staffNames.put(6L, "Partner");
        staffNames.put(9L, "Picker");
        String expectedCallForHelp = Handling.Proc_6_29_70D800(0, 0, 8, 5, "Caller", 6, "Partner",
            "Need help", 7, "Room", 50, "Picker");
        assertEquals(expectedCallForHelp, Handling.callForHelpRowPayload("50\t2\t5\tCaller\t6\t7\t8\tNeed help\t7\tRoom\t9",
            staffNames));
        String staffWhereWire = Crypto.Proc_3_0_6D2AF0(2, null, "")
            + Crypto.Proc_3_0_6D2AF0(50, null, "")
            + Crypto.Proc_3_0_6D2AF0(51, null, "");
        assertEquals("id='50' OR id='51'", Handling.staffCallForHelpWhereClause(staffWhereWire));
        assertEquals("", Handling.staffCallForHelpWhereClause(Crypto.Proc_3_0_6D2AF0(0, null, "")));
        String expectedStaffSummary = Crypto.Proc_3_0_6D2AF0(5, null, "HU") + "Alice\2";
        expectedStaffSummary = Crypto.Proc_3_0_6D2AF0(60, null, expectedStaffSummary);
        expectedStaffSummary = Crypto.Proc_3_0_6D2AF0(10, null, expectedStaffSummary);
        expectedStaffSummary = Crypto.Proc_3_0_6D2AF0(1, null, expectedStaffSummary);
        expectedStaffSummary = Crypto.Proc_3_0_6D2AF0(2, null, expectedStaffSummary);
        expectedStaffSummary = Crypto.Proc_3_0_6D2AF0(1, null, expectedStaffSummary);
        expectedStaffSummary = Crypto.Proc_3_0_6D2AF0(4, null, expectedStaffSummary);
        expectedStaffSummary = Crypto.Proc_3_0_6D2AF0(0, null, expectedStaffSummary);
        assertEquals(expectedStaffSummary, Handling.staffUserSummaryPayload("5\tAlice\t60\t10\t3", 2, 1, 4, 0));
        String expectedVisit = Crypto.Proc_3_0_6D2AF0(1, null, "");
        expectedVisit = Crypto.Proc_3_0_6D2AF0(7, null, expectedVisit);
        expectedVisit = Crypto.Proc_3_0_6D2AF0(12, null, expectedVisit);
        expectedVisit = Crypto.Proc_3_0_6D2AF0(30, null, expectedVisit) + "Room\2";
        assertEquals(expectedVisit, Handling.staffRoomVisitPayload("1\t7\tRoom\t12\t30"));
        assertEquals(123L, Handling.staffNestedUserIdFromWire("@C123"));
        assertEquals(77L, Handling.staffNestedUserIdFromWire(Crypto.Proc_3_0_6D2AF0(77, null, "")));
        String staffChatRows = "10\t5\t7\tAlice\thello\r11\t6\t8\tBob\thi";
        String expectedStaffChatRows = Crypto.Proc_3_0_6D2AF0(10, null, "");
        expectedStaffChatRows += Crypto.Proc_3_0_6D2AF0(5, null, "");
        expectedStaffChatRows += Crypto.Proc_3_0_6D2AF0(7, null, "");
        expectedStaffChatRows += "Alice\2hello\2";
        expectedStaffChatRows += Crypto.Proc_3_0_6D2AF0(11, null, "");
        expectedStaffChatRows += Crypto.Proc_3_0_6D2AF0(6, null, "");
        expectedStaffChatRows += Crypto.Proc_3_0_6D2AF0(8, null, "");
        expectedStaffChatRows += "Bob\2hi\2";
        Handling.StaffChatRowsPayload staffChat = Handling.staffRoomChatRowsPayload(staffChatRows);
        assertEquals(2L, staffChat.chatCount);
        assertEquals(expectedStaffChatRows, staffChat.payload);
        String expectedStaffChatHistory = Crypto.Proc_3_0_6D2AF0(1, null, "");
        expectedStaffChatHistory = Crypto.Proc_3_0_6D2AF0(7, null, expectedStaffChatHistory);
        expectedStaffChatHistory = Crypto.Proc_3_0_6D2AF0(2, null, expectedStaffChatHistory) + "Room\2" + expectedStaffChatRows;
        assertEquals(expectedStaffChatHistory, Handling.staffRoomChatHistoryPayload("1\t7\tRoom\t100\t200", staffChatRows));
        assertEquals(true, Handling.containsUnsafeStaffAlert("cookie plus javascript:"));
        assertEquals(false, Handling.containsUnsafeStaffAlert("cookie only"));

        final List<String> handlingSends = new ArrayList<>();
        final List<String> handlingSql = new ArrayList<>();
        HandlingMUS.configureMusSink((socketIndex, payload) -> handlingSends.add(socketIndex + ":" + payload));
        Guardian.setSocketConnected(4, true);
        Guardian.setSocketConnected(8, true);
        Licence.global_00829268 = "[1:4\1" + "77\2" + "4][1:8\1" + "88\2" + "8]";
        Licence.global_0082934C = "";
        Licence.global_0082912C = "CACHE";
        Licence.global_00829204 = "IMPORTANTFAQ";
        Licence.global_00829208 = "FAQCATS";
        Licence.global_0082920C = new String[]{"", "CATFAQ"};
        Licence.global_00829210 = new String[]{"", "", "FAQDESC"};
        Licence.global_008292D8 = new String[][]{{}, {"STAFFMOD"}};
        Licence.global_00829094 = "WIREDSTATE";
        Licence.global_0082908C = "12\t1";
        Licence.global_00829090 = java.time.LocalDateTime.now().plusSeconds(90L);
        DataManager.global_008291AC = "\0" + "1\1events\2";
        Path originalApplicationPath = Path.of(Functions.applicationPath);
        Object originalProductCache = DataManager.global_008292BC;
        Object originalLicenceProductCache = Licence.global_008292BC;
        Object originalCatalogProductCache = Licence.global_008292C0;
        Object originalRoomCategoryPayloads = Licence.global_00829244;
        Object originalRecommendedRooms = Licence.global_0082911C;
        long originalRecommendedRoomCount = Licence.global_00829128;
        String originalHcGiftPayload = Licence.global_00829178;
        String originalHcGiftLookup = Licence.global_0082917C;
        String originalGiftWrapPayload = Licence.global_00829260;
        Object originalCatalogPagePayloads = Licence.global_00829308;
        String originalSettingsCache = Functions.global_0082928C;
        Path figureCachePath = Files.createTempDirectory("alphaseries-figuredata");
        Functions.applicationPath = figureCachePath.toString();
        Files.write(figureCachePath.resolve("figuredata.cache"),
            "<settype type=\"hd\"><set id=\"180\" gender=\"M\"/></settype><settype type=\"ch\"><set id=\"255\" gender=\"M\"/></settype>"
                .getBytes());
        Files.createDirectories(figureCachePath.resolve("cache").resolve("wired_trigger"));
        Files.createDirectories(figureCachePath.resolve("cache").resolve("rooms"));
        Files.createDirectories(figureCachePath.resolve("cache").resolve("items_charges"));
        Files.write(figureCachePath.resolve("cache").resolve("wired_trigger").resolve("9.cache"), "trigger-cache".getBytes());
        Files.write(figureCachePath.resolve("cache").resolve("rooms").resolve("9.cache"), "room-cache".getBytes());
        String[] stickyProducts = new String[511];
        stickyProducts[9] = productRow(9, "18", "wall_sprite");
        stickyProducts[500] = productRow(500, "18", "post.it.vd");
        stickyProducts[501] = productRow(501, "17", "present_wrap_basic");
        stickyProducts[502] = productRow(502, "0", "2", "24", "Opened Sofa");
        stickyProducts[503] = productRow(503, "10", "2");
        stickyProducts[504] = productRow(504, "17", "CF_10");
        stickyProducts[506] = productRow(506, "1", "1", "13", "RewardA", "14", "Trade Chair", "15", "Trade Desc", "18", "trade_sprite");
        stickyProducts[507] = productRow(507, "1", "2", "14", "Wallpaper", "15", "Wallpaper Desc", "18", "paper_sprite", "20", "paper1");
        stickyProducts[508] = productRow(508, "12", "1", "18", "charge_sprite", "34", "3", "35", "10", "36", "2", "37", "1");
        stickyProducts[509] = productRow(509, "7", "static", "18", "plain_sprite");
        stickyProducts[510] = productRow(510, "12", "99", "17", "bb_score_blue");
        DataManager.global_008292BC = stickyProducts;
        Licence.global_008292BC = stickyProducts;
        String[] catalogProducts = new String[82];
        catalogProducts[81] = productRow(81, "2", "506", "9", "1");
        Licence.global_008292C0 = catalogProducts;
        String[][] categoryPayloads = new String[21][3];
        categoryPayloads[2][1] = "CATEGORY_PAYLOAD";
        Licence.global_00829244 = categoryPayloads;
        Licence.global_0082911C = new String[]{"RECOMMENDED"};
        Licence.global_00829128 = 1L;
        Licence.global_00829178 = "GIFTS";
        Licence.global_0082917C = "[81\0" + "506\1" + "20]";
        Licence.global_00829260 = "WRAP_PAYLOAD";
        Licence.global_00829308 = new String[]{"", "", "PAGE_PAYLOAD"};
        Functions.global_0082928C = "[com.server.socket.game.rooms.own.max=5]"
            + "[com.client.navigator.staff_picked.category.id.default=2]"
            + "[com.client.navigator.staff_picked.style.default=3]"
            + "[com.client.navigator.staff_picked.category.icon.default=4]"
            + "[com.server.socket.game.rooms.favourites.max=3]"
            + "[com.client.navigator.list.limit=4]"
            + "[com.mysql.format.time=%H:%i]"
            + "[com.client.catalog.gifts.enabled=1]"
            + "[com.client.catalog.gifts.wrap.enabled=1]"
            + "[com.client.catalog.gifts.wrap.price=7]"
            + "[com.client.rooms.bots.pets.enabled=1]"
            + "[com.client.rooms.bots.guide.enabled=1]"
            + "[com.client.bot.guide.id=20]"
            + "[com.client.catalog.recycler.enabled=1]"
            + "[com.server.socket.game.default.songdisk=700]"
            + "[com.server.socket.game.jukebox.900.soundsets.max=5]"
            + "[com.server.socket.game.activitypoints_0.interval=60]"
            + "[com.server.socket.game.activitypoints_0.max=500]"
            + "[com.server.socket.game.activitypoints_0.amount=5]";
        Licence.global_008291E8 = new String[][]{{"2", "ACH_", "10", "5", "3", "7", "2"}};
        Functions.global_008292A8 = new String[][]{{}, {"\2fuse_mod\2fuse_alert\2fuse_kick\2fuse_receive_calls_for_help\2fuse_chatlog\2"
            + "fuse_use_wardrobe\2fuse_larger_wardrobe\2fuse_client_staff\2"}};
        MySQL.configureDatabaseConnection(new Database() {
            @Override
            public void execute(String sqlText) {
                handlingSql.add(sqlText);
            }

            @Override
            public List<List<Object>> query(String sqlText) {
                if (sqlText.contains("SELECT id FROM users WHERE id_socket='4'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(77));
                }
                if (sqlText.contains("SELECT id FROM users WHERE id_socket='8'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(88));
                }
                if (sqlText.contains("SELECT id FROM users WHERE name='Target'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(88));
                }
                if (sqlText.contains("SELECT id,name,motto,figure,level,id_socket,DATE_FORMAT(FROM_UNIXTIME(lastonline_time)")
                    && sqlText.contains("FROM users WHERE id='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(77, "User77", "Ready", "hd-180-1", 1, 4, "today"));
                }
                if (sqlText.contains("SELECT id,name,motto,figure,level,id_socket,DATE_FORMAT(FROM_UNIXTIME(lastonline_time)")
                    && sqlText.contains("FROM users WHERE id='88'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(88, "Target", "Motto", "fig", 1, 8, "now"));
                }
                if (sqlText.contains("SELECT users.id,users.name,users.motto,users.figure,users.level,users.id_socket")
                    && sqlText.contains("friendships.has_accept='0'")
                    && sqlText.contains("friendships.id_user='77'")
                    && sqlText.contains("friendships.id_friend='88'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(88, "Target", "Motto", "fig", 1, 8, "now"));
                }
                if (sqlText.contains("SELECT id,name,id_socket,figure,motto,nickname,DATE_FORMAT(FROM_UNIXTIME(lastonline_time)")
                    && sqlText.contains("LOWER(name) LIKE 'targ%'")) {
                    return Arrays.<List<Object>>asList(
                        Arrays.<Object>asList(88, "Target", 8, "fig", "Motto", "nick", "now"),
                        Arrays.<Object>asList(99, "Targus", 0, "fig2", "Other", "nick2", "later"));
                }
                if (sqlText.contains("SELECT activitypoints_0 FROM users WHERE id='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(70));
                }
                if (sqlText.contains("SELECT online_time FROM users WHERE id='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(60));
                }
                if (sqlText.contains("SELECT id,name,motto,gender,respect_amount,scratch_amount FROM users WHERE id='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(77, "Caller", "Motto", "M", 4, 2));
                }
                if (sqlText.contains("SELECT activitypoints_1,activitypoints_2,activitypoints_3,activitypoints_4 FROM users WHERE id='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(10, 20, 30, 40));
                }
                if (sqlText.contains("SELECT level_hc FROM users")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(0));
                }
                if (sqlText.contains("SELECT level FROM users")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(1));
                }
                if (sqlText.contains("SELECT id_pet,breed,min_rank,min_hcrank,name FROM settings_petraces WHERE product_pet='dog'")) {
                    return Arrays.<List<Object>>asList(
                        Arrays.<Object>asList(1, 1, 0, 0, "A"),
                        Arrays.<Object>asList(2, 2, 2, 0, "B"));
                }
                if (sqlText.contains("SELECT bots.id,bots.name,bots.figure,bots_petdata.scratches FROM bots,bots_petdata WHERE bots.id_user='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(10, "Rex", "1 2 FF00AA", 4));
                }
                if (sqlText.contains("SELECT bots.id,bots.name,bots.figure,bots_petdata.id_level,bots_petdata.experience")
                    && sqlText.contains("WHERE bots.id='10'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(10, "Rex", "1 2 ff", 2, 7, 100, 90, 4, 12, 5, "Owner"));
                }
                if (sqlText.contains("SELECT bots.name,bots.figure,bots_petdata.id_level,bots_petdata.experience")
                    && sqlText.contains("WHERE bots.id='10'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList("Rex", "1 2 ff", 2, 18, 100, 90, 4, 9));
                }
                if (sqlText.contains("SELECT id_level,max_exp FROM bots_petlevels ORDER BY id_level ASC")) {
                    return Arrays.<List<Object>>asList(
                        Arrays.<Object>asList(1, 10),
                        Arrays.<Object>asList(2, 20),
                        Arrays.<Object>asList(3, 30));
                }
                if (sqlText.contains("SELECT scratch_amount FROM users WHERE id='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(2));
                }
                if (sqlText.contains("SELECT tutorial_guide FROM users WHERE id='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(0));
                }
                if (sqlText.contains("SELECT bots.id,bots.name,bots.figure,bots_petdata.scratches FROM bots,bots_petdata WHERE bots.id='10'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(10, "Rex", "1 2 ff", 4));
                }
                if (sqlText.contains("SELECT id_slot,figure,gender FROM users_wardrobe")) {
                    return Arrays.<List<Object>>asList(
                        Arrays.<Object>asList(1, "hd-180-1", "M"),
                        Arrays.<Object>asList(6, "ch-255-66", "M"));
                }
                if (sqlText.contains("SELECT id_socket FROM users WHERE id='88'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(8));
                }
                if (sqlText.contains("SELECT id_socket FROM users WHERE id='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(4));
                }
                if (sqlText.contains("SELECT id_badge,id_slot,id FROM users_badges WHERE id_user='77' AND id_slot='0'")) {
                    return Arrays.<List<Object>>asList(
                        Arrays.<Object>asList("ACH1", 0, 201),
                        Arrays.<Object>asList("MOD", 0, 202));
                }
                if (sqlText.contains("SELECT id_badge,id_slot,id FROM users_badges WHERE id_slot != '0' AND id_user='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList("VIP", 1, 203));
                }
                if (sqlText.contains("SELECT REPLACE(id_badge,'ACH_','') FROM users_badges WHERE id_user='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(2));
                }
                if (sqlText.contains("SELECT id FROM users_badges WHERE id_user='77' AND id_badge='ACH_3'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(204));
                }
                if (sqlText.contains("SELECT respect_received FROM users WHERE id='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(30));
                }
                if (sqlText.contains("SELECT name FROM users_tags WHERE id_user='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList("alpha"), Arrays.<Object>asList("beta"));
                }
                if (sqlText.contains("SELECT name FROM users_tags WHERE id_user='88'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList("target"));
                }
                if (sqlText.contains("SELECT id,description_title,description_thanks FROM poll WHERE id='7' AND id_room='9'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(7, "Title", "Thanks"));
                }
                if (sqlText.contains("SELECT id,description_question,id_type FROM poll_questions WHERE id_poll='7'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(8, "Question?", 2));
                }
                if (sqlText.contains("SELECT id,id_question,caption FROM poll_answers WHERE id_question='8'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(1, 8, "Yes"), Arrays.<Object>asList(2, 8, "No"));
                }
                if (sqlText.contains("SELECT title,sequence,author,sound,id FROM soundmachine_cds WHERE id='50' OR id='51'")) {
                    return Arrays.<List<Object>>asList(
                        Arrays.<Object>asList("Song A", 3, "Author A", "sound-a", 50),
                        Arrays.<Object>asList("Song B", 4, "Author B", "sound-b", 51));
                }
                if (sqlText.contains("SELECT furnitures.id,furnitures.id_product FROM furnitures,soundmachine_jb_playlist WHERE furnitures.id_room='9'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(300, 900));
                }
                if (sqlText.contains("SELECT id_destination FROM soundmachine_jb_playlist WHERE id_jukebox='300' AND id_order='0'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(40));
                }
                if (sqlText.contains("SELECT id_cd,id_destination FROM soundmachine_jb_playlist WHERE id_jukebox='300'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(2, 40), Arrays.<Object>asList(3, 41));
                }
                if (sqlText.contains("SELECT id,id_destination FROM furnitures WHERE id_owner='77' AND id_product='700'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(4, 50), Arrays.<Object>asList(5, 51));
                }
                if (sqlText.contains("SELECT soundmachine_jb_playlist.id_destination,soundmachine_jb_playlist.id_cd,soundmachine_cds.sequence")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(40, 2, 3));
                }
                if (sqlText.contains("SELECT id_quest,id_numericquest,progress,id_level,time_next FROM users_quests WHERE id_user='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(10, 10, 1, 0, 0));
                }
                if (sqlText.contains("SELECT id_quest,id_numericquest,progress,id_level FROM users_quests WHERE id_user='77' AND id_quest='10'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(10, 10, 3, 0));
                }
                if (sqlText.contains("SELECT id_quest,id_level FROM users_quests WHERE id_user='77'")
                    && sqlText.contains("timestamp_accepted IS NOT NULL")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(10, 1));
                }
                if (sqlText.contains("SELECT id_quest,id_level,timestamp_done,timestamp_accepted,time_next,progress FROM users_quests WHERE id_user='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(10, 0, 0, 1, 0, 1));
                }
                if (sqlText.contains("SELECT id_level FROM users_quests WHERE id_user='77' AND id_quest='10'")) {
                    return new ArrayList<List<Object>>();
                }
                if (sqlText.contains("SELECT progress FROM users_quests WHERE id_user='77' AND id_quest='10'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(1));
                }
                if (sqlText.contains("SELECT time_next FROM users_quests WHERE id_user='77' AND id_quest='10'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(0));
                }
                if (sqlText.contains("SELECT activitypoints_2 FROM users WHERE id='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(20));
                }
                if (sqlText.contains("FROM quests ORDER BY id_campaign DESC,level ASC")) {
                    return Arrays.<List<Object>>asList(
                        Arrays.<Object>asList(10, 1, "First", "", 5, 2, "visit", 0, 7, 3, 30),
                        Arrays.<Object>asList(11, 2, "Second", "", 6, 2, "visit", 0, 7, 4, 0));
                }
                if (sqlText.contains("SELECT logs_visitedrooms.id,logs_visitedrooms.id_user,users.id_socket")
                    && sqlText.contains("logs_visitedrooms.id='61'")
                    && sqlText.contains("logs_visitedrooms.id_room='9'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(61, 88, 8));
                }
                if (sqlText.contains("logs_visitedrooms WHERE id_user='88'") && sqlText.contains("id_room")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(9));
                }
                if (sqlText.contains("SELECT id FROM logs_visitedrooms WHERE id_user='88'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(61));
                }
                if (sqlText.contains("logs_visitedrooms WHERE id_user='77'") && sqlText.contains("id_room")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(9));
                }
                if (sqlText.contains("SELECT logs_visitedrooms.id,logs_visitedrooms.id_room,rooms.id_slot")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(61, 9, 4));
                }
                if (sqlText.contains("SELECT users.id_socket FROM logs_visitedrooms,users WHERE logs_visitedrooms.id_room='9'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(4), Arrays.<Object>asList(8));
                }
                if (sqlText.contains("SELECT id_slot FROM rooms WHERE id='9'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(4));
                }
                if (sqlText.contains("SELECT heightmap FROM models,rooms WHERE rooms.id='9'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList("0"));
                }
                if (sqlText.contains("SELECT bots.id,bots.name,bots.motto,bots.speech,bots.responses,'2','3','0','4',bots.figure")
                    && sqlText.contains("WHERE bots_petdata.id_bot='10'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(
                        10, "Rex", "pet motto", "speech", "responses", 2, 3, "0", 4, "1 2 ff", "", 3, 0, "", "submit", 1, 6));
                }
                if (sqlText.contains("SELECT scratches FROM bots_petdata WHERE id_bot='10'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(5));
                }
                if (sqlText.contains("SELECT id,name,motto,speech,responses,position_x,position_y,position_z,position_r,figure")
                    && sqlText.contains("FROM bots WHERE id='20'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(
                        20, "Guide", "guide motto", "speech", "responses", 1, 2, "0", 3, "guide-fig", "", 0, 0, "", "submit", 1, 6));
                }
                if (sqlText.contains("SELECT logs_visitedrooms.id,users.name,users.motto,users.achievement_score,users.figure")
                    && sqlText.contains("logs_visitedrooms.id='61'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(61, "Target", "Motto", 123, "fig"));
                }
                if (sqlText.contains("SELECT id FROM rooms WHERE id_slot='4'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(9));
                }
                if (sqlText.contains("SELECT id FROM rooms WHERE id='9' AND id_owner='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(9));
                }
                if (sqlText.contains("SELECT id_owner FROM rooms WHERE id='9' AND id_owner='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(77));
                }
                if (sqlText.contains("SELECT id FROM rooms_categories WHERE id='1'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(1));
                }
                if (sqlText.contains("SELECT id_user FROM rooms_rates WHERE id_user='77'")) {
                    return new ArrayList<List<Object>>();
                }
                if (sqlText.contains("SELECT rate FROM rooms WHERE id='9'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(5));
                }
                if (sqlText.contains("SELECT COUNT(id) FROM rooms WHERE id_owner='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(1));
                }
                if (sqlText.contains("SELECT id,visitors_max FROM models WHERE create_min_level_hc")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(20, 25));
                }
                if (sqlText.equals("SELECT MAX(id) FROM rooms")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(90));
                }
                if (sqlText.contains("SELECT visitors_now,visitors_max,status_door,password,id_slot,id_owner FROM rooms WHERE rooms.id='9'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(0, 25, 0, "", 4, 77));
                }
                if (sqlText.contains("SELECT rooms.id,rooms.name,rooms.description,rooms.status_door")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(
                        9, "Room", "Description", 0, 1, 25, 50, "tag1", "tag2", "", 1, 0, 1, 0));
                }
                if (sqlText.contains("SELECT rooms.id,rooms_events.name")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(
                        9, "Event Room", "Owner", 0, 3, 25, "Event Desc", 1, "", 5, 2, "icon", "tag1", "tag2", "12:00"));
                }
                if (sqlText.contains("ORDER BY 27 ASC LIMIT 255")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(
                        1, 2, 3, "caption", "cap2", "cap3", 7, 8, 9, 10,
                        11, 12, 13, "description", 15, 16, 17, 18, "icon",
                        "tag1", "tag2", 22, "model", "files", 250, 5, 6, 7));
                }
                if (sqlText.contains("SELECT SUM(get_one) as get_one,get_two")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(5, "tag1"));
                }
                if (sqlText.contains("SELECT users.id,users.name FROM rooms_rights")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(88, "Target"));
                }
                if (sqlText.contains("SELECT users.id_socket FROM rooms_rights,users WHERE rooms_rights.id_room='9'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(8));
                }
                if (sqlText.contains("SELECT users.id_socket FROM friendships,users WHERE friendships.has_accept='1' AND friendships.id_user='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(8));
                }
                if (sqlText.contains("SELECT users.id,users.name,users.id_socket,users.figure,users.motto,users.level,DATE_FORMAT(FROM_UNIXTIME(users.lastonline_time)")
                    && sqlText.contains("FROM friendships,users WHERE friendships.has_accept='1' AND friendships.id_user='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(88, "Target", 8, "fig", "Motto", 1, "now"));
                }
                if (sqlText.contains("SELECT id_friend FROM friendships WHERE id_user='77' AND id_friend='88' AND has_accept='1'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(88));
                }
                if (sqlText.contains("SELECT id_user FROM friendships WHERE has_accept='1'") && sqlText.contains("id_friend='88'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(77));
                }
                if (sqlText.contains("SELECT id_user FROM friendships WHERE has_accept='1'")
                    && sqlText.contains("id_friend='88'")
                    && sqlText.contains("id_user='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(77));
                }
                if (sqlText.contains("SELECT id_user FROM friendships WHERE (id_user='77' AND id_friend='88')")) {
                    return new ArrayList<List<Object>>();
                }
                if (sqlText.contains("SELECT accept_friends FROM users WHERE id='88'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(1));
                }
                if (sqlText.contains("SELECT users.id,users.name FROM users,friendships WHERE friendships.has_accept='0' AND friendships.id_user='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(88, "Target"));
                }
                if (sqlText.contains("SELECT name FROM users WHERE id='88'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList("Target"));
                }
                if (sqlText.contains("SELECT id,id_product,sign,caption,position_wall FROM furnitures WHERE id='70'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(70, 500, "FFFF33", "old", "w=1"));
                }
                if (sqlText.contains("SELECT id,id_product,sign,caption FROM furnitures WHERE id='70'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(70, 500, "9CFF9C", "hello\u001fworld"));
                }
                if (sqlText.contains("SELECT id,id_product,id_destination,sign_extra FROM furnitures WHERE id='71'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(71, 501, 502, "gift sign"));
                }
                if (sqlText.contains("SELECT id,id_product,sign,position_wall FROM furnitures WHERE id='72'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(72, 503, 1, "w=1"));
                }
                if (sqlText.contains("SELECT id_product FROM furnitures WHERE id_room='9' AND id='73'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(504));
                }
                if (sqlText.contains("SELECT id,id_product,sign,id_secondary FROM furnitures WHERE id='76'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(76, 506, "trade-state", 4));
                }
                if (sqlText.contains("SELECT id,id_product,sign FROM furnitures WHERE id='76'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(76, 506, "trade-state"));
                }
                if (sqlText.contains("SELECT id,id_product,sign FROM furnitures WHERE id_owner='77' AND id='79'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(79, 507, "paper1"));
                }
                if (sqlText.contains("SELECT id,id_product,sign,id_secondary FROM furnitures WHERE id_owner='77' AND id_room IS NULL LIMIT 1000")) {
                    return Arrays.<List<Object>>asList(
                        Arrays.<Object>asList(76, 506, "trade-state", 4),
                        Arrays.<Object>asList(79, 507, "paper1", 0));
                }
                if (sqlText.contains("SELECT furnitures.id FROM furnitures,products WHERE furnitures.id_room='9'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(78));
                }
                if (sqlText.contains("SELECT id_product,position_wall FROM furnitures WHERE id='78'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(503, ":w=5"));
                }
                if (sqlText.contains("SELECT status_door FROM rooms WHERE id='9'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(0));
                }
                if (sqlText.contains("SELECT name FROM users WHERE id='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList("OldName"));
                }
                if (sqlText.contains("SELECT gender FROM users WHERE id='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList("M"));
                }
                if (sqlText.contains("SELECT COUNT(*) FROM users WHERE name='NewName'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(0));
                }
                if (sqlText.contains("SELECT rooms.id,rooms.name,users.name,rooms.status_door")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(
                        9, "NavRoom", "Owner", 0, 2, 25, "Description", 1, "", 5, 3, "icon", "tag1", "tag2", 1, 0));
                }
                if (sqlText.contains("users.id='88'") && sqlText.contains("ROUND")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(88, "Target", 60, 10, 8));
                }
                if (sqlText.contains("COUNT(id) FROM staff_cfh WHERE id_user='88' AND id_closed='2'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(1));
                }
                if (sqlText.contains("COUNT(id) FROM staff_cfh WHERE id_user='88'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(2));
                }
                if (sqlText.contains("COUNT(id) FROM users_cautions WHERE id_user='88'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(3));
                }
                if (sqlText.contains("COUNT(id) FROM users_bans WHERE id_user='88'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(4));
                }
                if (sqlText.contains("SELECT ip_last FROM users WHERE id='88'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList("127.0.0.1"));
                }
                if (sqlText.contains("SELECT id_slot,id_owner FROM rooms WHERE id='9'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(4, 88));
                }
                if (sqlText.contains("SELECT id_owner FROM rooms WHERE id='9' LIMIT 1")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(77));
                }
                if (sqlText.contains("SELECT is_staff_picked FROM rooms WHERE id='9'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(0));
                }
                if (sqlText.contains("staff_cfh.id='50'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(50, 88, "Target", 0, 9, 8, "Need help", 9, "Room"));
                }
                if (sqlText.contains("SELECT id FROM staff_cfh WHERE id_user='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(51));
                }
                if (sqlText.contains("SELECT id_closed FROM staff_cfh WHERE id_user='77'")) {
                    return new ArrayList<List<Object>>();
                }
                if (sqlText.contains("SELECT MAX(id) FROM staff_cfh")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(52));
                }
                if (sqlText.contains("SELECT staff_cfh.id,staff_cfh.id_tab")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(51, 0, 77, "Caller", 88, 9, 8, "Need help", 9, "Room", 0));
                }
                if (sqlText.contains("SELECT id_user FROM staff_cfh WHERE id='50'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(88));
                }
                if (sqlText.contains("SELECT id,name FROM faq WHERE name LIKE")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(2, "FAQ Two"), Arrays.<Object>asList(3, "FAQ Three"));
                }
                if (sqlText.contains("SELECT users.id,users.name,rooms_events.id_room")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(77, "Caller", 9, 8, "Party", "Desc", "12:00", "tag1", "tag2"));
                }
                if (sqlText.contains("SELECT motto FROM users WHERE id='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList("Motto"));
                }
                if (sqlText.contains("SELECT figure,gender FROM users WHERE id='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList("hd-180-1", "M"));
                }
                if (sqlText.contains("SELECT id_session FROM users WHERE id='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList("session-77"));
                }
                if (sqlText.contains("SELECT login_session FROM users WHERE id='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList("login-77"));
                }
                if (sqlText.contains("SELECT credits FROM users WHERE id='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(110));
                }
                if (sqlText.contains("SELECT rooms.id,rooms_official.id,models.required_files,rooms_official.caption")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(9, 1, "model.swf", "Official Room"));
                }
                if (sqlText.contains("SELECT users.id,users.name,users.figure,users.motto,users.gender,models.position_x,models.position_y,rooms.id_slot")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(77, "Caller", "hd-180-1", "Motto", "M", 2, 3, 4));
                }
                if (sqlText.contains("SELECT logs_visitedrooms.id,users_effects.id_effect")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(61, 12));
                }
                if (sqlText.contains("SELECT id_effect,time_rent,COUNT(id_effect),timestamp_expire,UNIX_TIMESTAMP()")) {
                    return Arrays.<List<Object>>asList(
                        Arrays.<Object>asList(12, 3600, 2, 1000, 900),
                        Arrays.<Object>asList(13, 120, 1, 0, 900));
                }
                if (sqlText.contains("SELECT id,time_rent,timestamp_expire FROM users_effects")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(901, 3600, 0));
                }
                if (sqlText.contains("SELECT users_effects.id_effect,users.id_socket,users_effects.id")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(12, 4, 901));
                }
                if (sqlText.contains("SELECT id_type,id_source,id_sprite,position_x,position_y,position_z,action,action_rotation,action_height FROM models_furnitures WHERE id_model='20'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(503, 901, "", 4, 5, 0, "state", 2, 0));
                }
                if (sqlText.contains("SELECT id,id_product,position_wall,sign,id_secondary FROM furnitures WHERE id_room='9'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(74, 503, ":w=1,2 l=3,4", "wall-state", 6));
                }
                if (sqlText.contains("SELECT id,id_room,id_product,sign FROM furnitures WHERE id_room='9' AND position_x='2' AND position_y='3'")) {
                    return Arrays.<List<Object>>asList(
                        Arrays.<Object>asList(88, 9, 506, 4),
                        Arrays.<Object>asList(89, 9, 509, 7));
                }
                if (sqlText.contains("SELECT id_product FROM furnitures WHERE id='75' AND id_room='9'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(505));
                }
                if (sqlText.contains("SELECT id_product,id_owner FROM furnitures WHERE id='80' AND id_room='9'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(506, 77));
                }
                if (sqlText.contains("SELECT id_room FROM furnitures WHERE id='82' LIMIT 1")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(9));
                }
                if (sqlText.contains("SELECT id_room FROM furnitures WHERE id='83' LIMIT 1")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(9));
                }
                if (sqlText.contains("SELECT id_room,id_product,sign FROM furnitures WHERE id='84' LIMIT 1")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(9, 506, 5));
                }
                if (sqlText.contains("SELECT id_product,id_owner FROM furnitures WHERE id='85' AND id_room='9'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(506, 77));
                }
                if (sqlText.contains("SELECT id_room FROM furnitures WHERE id='85' LIMIT 1")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(9));
                }
                if (sqlText.contains("SELECT id_product,sign FROM furnitures WHERE id='86' AND id_room='9' AND position_wall IS NULL")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(506, 0));
                }
                if (sqlText.contains("SELECT id_product FROM furnitures WHERE id='86' AND id_room='9' AND position_wall IS NULL")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(506));
                }
                if (sqlText.contains("SELECT id_product,id,sign,id_secondary,id_destination FROM furnitures WHERE id='90' AND id_owner='77' AND id_room IS NULL")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(9, 90, "wall-state", 6, 0));
                }
                if (sqlText.contains("SELECT id_room FROM furnitures WHERE id='91' LIMIT 1")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(9));
                }
                if (sqlText.contains("SELECT models.map,rooms.allow_walkthrough,rooms.id_slot FROM rooms,models WHERE rooms.id='9'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList("000\r0x0\r000", 1, 4));
                }
                if (sqlText.contains("SELECT COUNT(*) FROM furnitures WHERE id_room='9'")
                    && sqlText.contains("position_x='2'")
                    && sqlText.contains("position_y='2'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(1));
                }
                if (sqlText.contains("SELECT id_room,id_product,sign FROM furnitures WHERE id='92' LIMIT 1")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(9, 510, -2));
                }
                if (sqlText.contains("SELECT id_product,type_secondary,id_contain,type_check FROM packages WHERE id_product='505'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(505, "packages_pets", 12, ""));
                }
                if (sqlText.contains("SELECT id_pet,id_race,color FROM packages_pets WHERE id='12'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(1, 2, "3"));
                }
                if (sqlText.contains("SELECT id_socket FROM users WHERE name='Target'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(8));
                }
                if (sqlText.contains("SELECT id FROM users WHERE name='Target'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(88));
                }
                if (sqlText.contains("users.name='Target'") && sqlText.contains("logs_visitedrooms.id_room")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(88, 8, 9));
                }
                if (sqlText.contains("SELECT id,sprite_name,months,level,price_credits FROM products_club")) {
                    return Arrays.<List<Object>>asList(
                        Arrays.<Object>asList(1, "club_habbo", 1, 1, 25),
                        Arrays.<Object>asList(2, "club_vip", 3, 2, 60));
                }
                if (sqlText.contains("SELECT level_hc,hc_days,hc2_days,hc_periods,hc2_periods,hc_presents")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(2, 10, 70, 1, 3, 4, 8));
                }
                if (sqlText.contains("SELECT users.id_socket FROM logs_visitedrooms")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(4), Arrays.<Object>asList(8));
                }
                if (sqlText.contains("SELECT users.id,users.name FROM users WHERE users.id='88'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(88, "Target"));
                }
                if (sqlText.contains("SELECT id_room FROM rooms_favourites WHERE id_user='77'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(9), Arrays.<Object>asList(10));
                }
                if (sqlText.contains("SELECT contain_product,contain_credits,contain_shells FROM vouchers WHERE name='ABCD0000'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList("trade_sprite", 5, 7));
                }
                if (sqlText.contains("SELECT id_product FROM catalog_products WHERE sprite='trade_sprite'")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(506));
                }
                if (sqlText.contains("logs_visitedrooms.id_user='88'") && sqlText.contains("models.type")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(1, 9, "Room", 12, 30));
                }
                if (sqlText.contains("FROM logs_chat,users")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(10, 5, 88, "Target", "hello"));
                }
                if (sqlText.equals("SELECT id,id_socket FROM users WHERE id_socket IS NOT NULL")) {
                    return Arrays.<List<Object>>asList(Arrays.<Object>asList(77, 4), Arrays.<Object>asList(88, 8));
                }
                return Arrays.<List<Object>>asList(Arrays.<Object>asList(0));
            }
        });

        Handling.Proc_6_244_801E80(4, "PING");
        assertEquals("4:DATA\6" + "4\6PING\1\7", handlingSends.get(0));
        handlingSends.clear();
        assertEquals(2L, Handling.broadcastToRoomUsers(9, "ROOM"));
        assertEquals(Arrays.asList("4:DATA\6" + "4\6ROOM\1\7", "8:DATA\6" + "8\6ROOM\1\7"), handlingSends);
        handlingSends.clear();
        Handling.Proc_6_0_6D7FF0(4, "GF" + wireLong(88));
        assertEquals(true, handlingSends.get(0).contains("HU"));
        handlingSends.clear();
        Handling.Proc_6_1_6D8B70(4, "GM" + wireLong(88) + wireString("Careful"));
        assertEquals("8:DATA\6" + "8\6BaCareful\2\1\7", handlingSends.get(0));
        assertEquals(true, containsSql(handlingSql, "users_cautions"));
        handlingSends.clear();
        Handling.Proc_6_2_6D9880(4, "GO" + wireLong(88) + wireString("Leave"));
        assertEquals(true, handlingSends.contains("8:DATA\6" + "8\6BaLeave\2\1\7"));
        assertEquals(true, handlingSends.contains("8:DATA\6" + "8\6@R\7"));
        handlingSends.clear();
        Handling.Proc_6_3_6DA490(4, "GP" + wireLong(88) + wireString("Ban") + wireLong(2));
        assertEquals(true, containsSql(handlingSql, "users_bans"));
        assertEquals(true, containsSql(handlingSql, "UNIX_TIMESTAMP()+7200"));
        Guardian.setSocketConnected(8, true);
        handlingSends.clear();
        assertEquals(1L, Handling.Proc_6_4_6DAFB0(4, "CH" + wireLong(1) + wireString("Room alert")));
        assertEquals(true, handlingSends.contains("4:DATA\6" + "4\6BaRoom alert\2\1\7"));
        assertEquals(true, handlingSends.contains("8:DATA\6" + "8\6BaRoom alert\2\1\7"));
        handlingSends.clear();
        Handling.Proc_6_5_6DC340(50, 4);
        assertEquals(true, handlingSends.get(0).contains("HR"));
        handlingSql.clear();
        Handling.Proc_6_6_6DC9D0(4, "GB" + wireLong(1) + wireLong(50));
        assertEquals(true, containsSql(handlingSql, "id_tab='2'"));
        handlingSql.clear();
        Handling.Proc_6_8_6DD790(4, "GC" + wireLong(1) + wireLong(50));
        assertEquals(true, containsSql(handlingSql, "id_tab='1'"));
        handlingSends.clear();
        Handling.Proc_6_7_6DD0E0(4, "GD" + wireLong(2) + wireLong(50));
        assertEquals(true, handlingSends.get(0).contains("H\\"));
        handlingSql.clear();
        Handling.Proc_6_9_6DDD70(4, "GL" + wireLong(1) + wireLong(1));
        assertEquals(true, containsSql(handlingSql, "Inappropriate to hotel management"));
        handlingSends.clear();
        Handling.Proc_6_10_6DE1D0(4, "GG" + wireLong(88));
        assertEquals(true, handlingSends.get(0).contains("HX"));
        handlingSends.clear();
        Handling.Proc_6_11_6DF4A0(4, "GJB88");
        assertEquals(true, handlingSends.get(0).contains("HY"));
        handlingSends.clear();
        Handling.Proc_6_12_6DFE90(4, "GN" + wireLong(88) + wireString("Direct"));
        assertEquals("8:DATA\6" + "8\6BaDirect\2\1\7", handlingSends.get(0));
        handlingSends.clear();
        assertEquals(4L, Handling.Proc_6_13_6E0A80(4));
        assertEquals(true, handlingSends.get(0).contains("Ga"));
        handlingSends.clear();
        assertEquals(3L, Handling.Proc_6_14_6E10C0(4, "A]" + wireLong(3)));
        assertEquals(true, handlingSends.get(0).contains("G`"));
        handlingSends.clear();
        Handling.Proc_6_15_6E1900(4, "Ew");
        assertEquals(true, handlingSends.get(0).contains("DK"));
        assertEquals(true, handlingSends.get(0).contains("hd-180-1"));
        handlingSql.clear();
        handlingSends.clear();
        Handling.Proc_6_16_6E2320(4, "Ex" + wireLong(2) + wireString("hd-180-1.ch-255-66") + wireString("M"));
        assertEquals(true, containsSql(handlingSql, "DELETE FROM users_wardrobe"));
        assertEquals(true, containsSql(handlingSql, "INSERT INTO users_wardrobe"));
        assertEquals(true, handlingSends.get(0).contains("DK"));
        handlingSql.clear();
        handlingSends.clear();
        Handling.Proc_6_17_6E48D0(4, "@l" + wireString("M") + wireString("hd-180-1.ch-255-66"));
        assertEquals(true, containsSql(handlingSql, "tutorial_clothes='1'"));
        assertEquals(true, handlingSends.get(0).contains("DJ"));
        handlingSends.clear();
        Handling.Proc_6_18_6E7480(4);
        assertEquals(true, handlingSends.get(0).contains("Iq"));
        assertEquals(true, handlingSends.get(0).contains("club_vip"));
        handlingSends.clear();
        assertEquals("GzCACHE", Handling.Proc_6_19_6E8040(4));
        assertEquals("4:DATA\6" + "4\6GzCACHE\1\7", handlingSends.get(0));
        handlingSends.clear();
        Handling.Proc_6_20_6E88E0(4);
        assertEquals(true, handlingSends.get(0).contains("@B"));
        handlingSql.clear();
        handlingSends.clear();
        String chatPayload = Handling.Proc_6_26_7034C0(4, "@t" + wireString("hello"));
        assertEquals(true, chatPayload.contains("@X"));
        assertEquals(true, chatPayload.contains("hello"));
        assertEquals(true, containsSql(handlingSql, "logs_chat"));
        assertEquals(true, containsSql(handlingSql, "'0'"));
        assertEquals(2, handlingSends.size());
        handlingSql.clear();
        handlingSends.clear();
        String shoutPayload = Handling.Proc_6_27_706920(4, "@w" + wireString("loud"));
        assertEquals(true, shoutPayload.contains("@Y"));
        assertEquals(true, containsSql(handlingSql, "'1'"));
        handlingSends.clear();
        String whisperPayload = Handling.Proc_6_28_709DA0(4, "@x" + wireString("Target") + wireString("secret"));
        assertEquals(true, whisperPayload.contains("@X"));
        assertEquals(true, whisperPayload.contains("secret"));
        assertEquals(2, handlingSends.size());
        assertEquals(true, handlingSends.get(0).startsWith("8:DATA"));
        assertEquals(true, handlingSends.get(1).startsWith("4:DATA"));
        handlingSql.clear();
        handlingSends.clear();
        Handling.Proc_6_30_70DC90(4, "EG");
        assertEquals(true, containsSql(handlingSql, "DELETE FROM staff_cfh WHERE id='51'"));
        assertEquals("4:DATA\6" + "4\6E@\1\7", handlingSends.get(0));
        handlingSends.clear();
        Handling.Proc_6_31_70DE80(4);
        assertEquals(true, handlingSends.get(0).contains("HS"));
        assertEquals(true, handlingSends.get(0).contains("STAFFMOD"));
        assertEquals(true, handlingSends.get(1).contains("HR"));
        handlingSql.clear();
        handlingSends.clear();
        Handling.Proc_6_32_70EAB0(4, "GE" + wireString("This is a sufficiently long call for help description") + wireLong(8) + wireLong(88));
        assertEquals(true, containsSql(handlingSql, "INSERT INTO staff_cfh"));
        assertEquals(true, handlingSends.get(0).contains("EA"));
        handlingSends.clear();
        Handling.Proc_6_33_70F4F0(4);
        assertEquals("4:DATA\6" + "4\6HFIMPORTANTFAQ\1\7", handlingSends.get(0));
        handlingSends.clear();
        Handling.Proc_6_34_70F590(4);
        assertEquals("4:DATA\6" + "4\6HGFAQCATS\1\7", handlingSends.get(0));
        handlingSends.clear();
        Handling.Proc_6_35_70F630(4, "Fd" + wireLong(1));
        assertEquals(true, handlingSends.get(0).contains("HJ"));
        assertEquals(true, handlingSends.get(0).contains("CATFAQ"));
        handlingSends.clear();
        Handling.Proc_6_36_70F7B0(4, "Fc" + "hotel");
        assertEquals(true, handlingSends.get(0).contains("HI"));
        assertEquals(true, handlingSends.get(0).contains("FAQ Two"));
        handlingSends.clear();
        Handling.Proc_6_37_70FC20(4, "Fb" + wireLong(2));
        assertEquals("4:DATA\6" + "4\6HHFAQDESC\1\7", handlingSends.get(0));
        String eventInfoPayload = Handling.Proc_6_51_716AC0(9);
        assertEquals(true, eventInfoPayload.contains("Party"));
        assertEquals("-1\2", Handling.Proc_6_51_716AC0(0));
        handlingSends.clear();
        assertEquals(0L, Handling.Proc_6_39_711650(4, "GW" + wireString("NewName")));
        assertEquals(true, handlingSends.get(0).contains("H{"));
        assertEquals(true, handlingSends.get(0).contains("NewName"));
        handlingSql.clear();
        handlingSends.clear();
        assertEquals(0L, Handling.Proc_6_38_70FD10(4, "GV" + wireString("NewName")));
        assertEquals(true, containsSql(handlingSql, "UPDATE users SET name='NewName'"));
        assertEquals(true, containsSql(handlingSql, "INSERT INTO logs_identity"));
        assertEquals(true, containsSend(handlingSends, "H|"));
        assertEquals(true, containsSend(handlingSends, "GH"));
        assertEquals(true, Handling.Proc_6_112_74E0C0("users,rooms,rooms_categories WHERE rooms.id='9' LIMIT 1").contains("NavRoom"));
        handlingSends.clear();
        Handling.Proc_6_43_713680(4, "FF" + wireLong(9));
        assertEquals(true, handlingSends.get(0).contains("GQ"));
        assertEquals(true, handlingSends.get(0).contains("Room"));
        handlingSql.clear();
        handlingSends.clear();
        Handling.Proc_6_44_7145E0(4, "FB" + wireLong(1) + wireLong(2) + wireLong(0));
        assertEquals(true, containsSql(handlingSql, "UPDATE rooms SET icon="));
        assertEquals(true, containsSend(handlingSends, "GI"));
        assertEquals(true, containsSend(handlingSends, "GH"));
        handlingSql.clear();
        handlingSends.clear();
        Handling.Proc_6_45_714B60(4);
        assertEquals(true, containsSql(handlingSql, "DELETE FROM rooms_events WHERE id_room='9'"));
        assertEquals("4:DATA\6" + "4\6Er-1\2\1\7", handlingSends.get(0));
        handlingSends.clear();
        Handling.Proc_6_46_714D50(4);
        assertEquals("4:DATA\6" + "4\6EoIH\1\7", handlingSends.get(0));
        handlingSql.clear();
        handlingSends.clear();
        Handling.Proc_6_47_714F60(4, "XX" + wireLong(9));
        assertEquals(true, containsSql(handlingSql, "UPDATE users SET homeroom='9'"));
        assertEquals(true, handlingSends.get(0).contains("GG"));
        handlingSql.clear();
        handlingSends.clear();
        Handling.Proc_6_48_7151E0(4, "EZ" + wireLong(1) + wireString("Party") + wireString("Description")
            + wireLong(2) + wireString("TagOne") + wireString("TagTwo"));
        assertEquals(true, containsSql(handlingSql, "INSERT INTO rooms_events"));
        assertEquals(true, containsSend(handlingSends, "Er"));
        handlingSql.clear();
        handlingSends.clear();
        Handling.Proc_6_49_715D30(4, "E\\" + wireString("Edited") + wireString("Description")
            + wireLong(1) + wireString("TagOne"));
        assertEquals(true, containsSql(handlingSql, "UPDATE rooms_events SET"));
        assertEquals(true, containsSend(handlingSends, "Er"));
        handlingSql.clear();
        handlingSends.clear();
        Handling.Proc_6_50_7166B0(4, "AbTarget");
        assertEquals(true, containsSql(handlingSql, "INSERT INTO logs_visitedrooms"));
        assertEquals(true, containsSend(handlingSends, "@S"));
        assertEquals(true, containsSend(handlingSends, "@R"));
        handlingSql.clear();
        handlingSends.clear();
        Handling.Proc_6_52_7172B0(4, "FQ" + wireString("Updated Room") + wireString("secret")
            + wireLong(0) + wireString("Updated description") + wireLong(20) + wireLong(1)
            + wireLong(2) + wireString("TagOne") + wireString("TagTwo")
            + wireLong(1) + wireLong(0) + wireLong(1) + wireLong(0) + wireLong(0) + wireLong(0));
        assertEquals(true, containsSql(handlingSql, "UPDATE rooms SET"));
        assertEquals(true, containsSql(handlingSql, "name='Updated Room'"));
        assertEquals(true, containsSend(handlingSends, "GS"));
        assertEquals(true, containsSend(handlingSends, "GX"));
        handlingSql.clear();
        handlingSends.clear();
        assertEquals(4L, Handling.Proc_6_57_71E8F0(4, 9, ""));
        assertEquals(true, containsSql(handlingSql, "UPDATE rooms SET id_slot='4'"));
        assertEquals(true, containsSend(handlingSends, "@R"));
        handlingSql.clear();
        handlingSends.clear();
        assertEquals(9L, Handling.Proc_6_55_71A6E0(4));
        assertEquals(true, containsSql(handlingSql, "timestamp_left=UNIX_TIMESTAMP()"));
        assertEquals(true, containsSend(handlingSends, "J|H"));
        handlingSends.clear();
        Handling.Proc_6_56_71E730(4, 0);
        assertEquals(true, containsSend(handlingSends, "@S"));
        assertEquals(true, containsSend(handlingSends, "Bf/client.php"));
        assertEquals(true, containsSend(handlingSends, "@i"));
        handlingSends.clear();
        assertEquals(9L, Handling.Proc_6_58_71FCA0(4, "FG9"));
        assertEquals(true, containsSend(handlingSends, "@R"));
        handlingSends.clear();
        Handling.Proc_6_59_71FEE0(4);
        assertEquals("4:DATA\6" + "4\6DB\2\2\1\7", handlingSends.get(0));
        handlingSends.clear();
        Handling.Proc_6_60_720060(4, "FA" + wireLong(0) + wireLong(1) + wireLong(9));
        assertEquals(true, containsSend(handlingSends, "GF"));
        assertEquals(true, containsSend(handlingSends, "NavRoom"));
        handlingSends.clear();
        Handling.Proc_6_61_720490(4, "A_" + wireLong(88));
        assertEquals(true, containsSend(handlingSends, "@aXjO"));
        handlingSql.clear();
        handlingSends.clear();
        Handling.Proc_6_62_7209F0(4, "E@" + wireLong(88));
        assertEquals(true, containsSend(handlingSends, "@aXjO"));
        assertEquals(true, containsSql(handlingSql, "INSERT IGNORE INTO rooms_bans"));
        assertEquals(true, containsSql(handlingSql, "UNIX_TIMESTAMP()+900"));
        handlingSql.clear();
        handlingSends.clear();
        Handling.Proc_6_63_721050(4, "DE" + wireLong(1));
        assertEquals(true, containsSql(handlingSql, "INSERT INTO rooms_rates"));
        assertEquals(true, containsSql(handlingSql, "UPDATE rooms SET rate='6'"));
        assertEquals(true, containsSend(handlingSends, "EY"));
        handlingSql.clear();
        handlingSends.clear();
        Handling.Proc_6_64_721650(4, "D\u007f" + wireString("Target"));
        assertEquals(true, containsSql(handlingSql, "DELETE FROM rooms_rights WHERE id_user='88'"));
        assertEquals(true, containsSend(handlingSends, "Fc"));
        handlingSql.clear();
        handlingSends.clear();
        Handling.Proc_6_65_721A10(4, "A`" + wireLong(88));
        assertEquals(true, containsSql(handlingSql, "INSERT IGNORE INTO rooms_rights"));
        assertEquals(true, containsSend(handlingSends, "8:DATA"));
        assertEquals(true, containsSend(handlingSends, "@j"));
        handlingSql.clear();
        handlingSends.clear();
        Handling.Proc_6_66_721D60(4, "AT" + wireLong(70) + "9CFF9C\nhello\nworld");
        assertEquals(true, containsSql(handlingSql, "UPDATE furnitures SET sign='9CFF9C'"));
        assertEquals(true, containsSql(handlingSql, "caption='hello\u001fworld'"));
        assertEquals(true, containsSend(handlingSends, "AT70\u0001AS70"));
        assertEquals(true, containsSend(handlingSends, "9CFF9C"));
        handlingSends.clear();
        Handling.Proc_6_67_722940(4, "AS" + wireLong(70));
        assertEquals(true, containsSend(handlingSends, "@p70\2" + "9CFF9C\rhello\rworld\2"));
        handlingSql.clear();
        handlingSends.clear();
        Handling.Proc_6_68_723170(4, "AU" + wireLong(70));
        assertEquals(true, containsSql(handlingSql, "DELETE FROM furnitures WHERE id='70' LIMIT 1"));
        assertEquals(true, containsSend(handlingSends, "AT70"));
        handlingSql.clear();
        handlingSends.clear();
        Handling.Proc_6_69_723630(4, "AN" + wireLong(71));
        assertEquals(true, containsSend(handlingSends, "A^71\2H\2"));
        assertEquals(true, containsSql(handlingSql, "DELETE FROM furnitures WHERE id='71' LIMIT 1"));
        assertEquals(true, containsSql(handlingSql, "INSERT INTO furnitures(id_product,id_owner,sign,task_owner,task_time) VALUES('502','77','gift sign','77',UNIX_TIMESTAMP())"));
        assertEquals(true, containsSend(handlingSends, "BAs\2"));
        assertEquals(true, containsSend(handlingSends, "Opened Sofa"));
        handlingSql.clear();
        handlingSends.clear();
        Handling.Proc_6_70_724190(4, "FI" + wireLong(72));
        assertEquals(true, containsSql(handlingSql, "UPDATE furnitures SET sign='2' WHERE id='72'"));
        assertEquals(true, containsSend(handlingSends, "AU72\2"));
        assertEquals(true, containsSend(handlingSends, "2\2" + "0\2"));
        handlingSql.clear();
        handlingSends.clear();
        Handling.Proc_6_71_724CF0(4);
        assertEquals(true, containsSql(handlingSql, "DELETE FROM rooms_rights WHERE id_room='9'"));
        assertEquals(true, containsSend(handlingSends, "8:DATA"));
        assertEquals(true, containsSend(handlingSends, "@k"));
        handlingSql.clear();
        handlingSends.clear();
        Handling.Proc_6_72_7250D0(4, "@W" + wireLong(0));
        assertEquals(true, containsSql(handlingSql, "DELETE FROM rooms WHERE id='9' LIMIT 1"));
        assertEquals(true, containsSend(handlingSends, "@R"));
        handlingSql.clear();
        handlingSends.clear();
        Handling.Proc_6_73_725540(4, "AT" + wireLong(73));
        assertEquals(true, containsSql(handlingSql, "UPDATE users SET credits=credits+10 WHERE id='77'"));
        assertEquals(true, containsSql(handlingSql, "DELETE FROM furnitures WHERE id='73' LIMIT 1"));
        assertEquals(true, containsSend(handlingSends, "@F110.0\2"));
        assertEquals(true, containsSend(handlingSends, "A^73\2H\2"));
        handlingSql.clear();
        handlingSends.clear();
        Handling.Proc_6_74_7265B0(4, "Aa" + wireLong(1) + wireLong(88));
        assertEquals(true, containsSql(handlingSql, "DELETE FROM rooms_rights WHERE id_user='88' AND id_room='9'"));
        assertEquals(true, containsSend(handlingSends, "8:DATA"));
        assertEquals(true, containsSend(handlingSends, "@k"));
        handlingSql.clear();
        handlingSends.clear();
        Handling.Proc_6_75_7269D0(4, "EB" + wireString("Target"));
        assertEquals(true, containsSql(handlingSql, "DELETE FROM rooms_rights WHERE id_user='88' AND id_room='9'"));
        assertEquals(true, containsSend(handlingSends, "Fc"));
        handlingSends.clear();
        Handling.Proc_6_77_727590(4, "FD" + wireLong(9));
        assertEquals(true, containsSend(handlingSends, "GE"));
        assertEquals(true, containsSend(handlingSends, "model.swf"));
        assertEquals(true, containsSend(handlingSends, "Official Room"));
        handlingSends.clear();
        Handling.Proc_6_80_72EB60(4, 9);
        assertEquals(true, containsSend(handlingSends, "@\\"));
        assertEquals(true, containsSend(handlingSends, "Caller"));
        assertEquals(true, containsSend(handlingSends, "hd-180-1"));
        handlingSends.clear();
        Handling.Proc_6_82_731070(4, 9);
        assertEquals(true, containsSend(handlingSends, "Ge"));
        handlingSends.clear();
        String modelFurniturePayload = Handling.Proc_6_83_732640(4, 20);
        assertEquals(true, modelFurniturePayload.contains("@^"));
        assertEquals(true, modelFurniturePayload.contains("state"));
        assertEquals(true, containsSend(handlingSends, "@^"));
        handlingSends.clear();
        String cachePayload = Handling.Proc_6_84_733600(4, 9);
        assertEquals(true, cachePayload.contains("DiWIREDSTATE"));
        assertEquals(true, cachePayload.contains("trigger-cache"));
        assertEquals(true, cachePayload.contains("room-cache"));
        assertEquals(true, containsSend(handlingSends, "DiWIREDSTATE"));
        handlingSends.clear();
        String wallPayload = Handling.Proc_6_85_73A8E0(4, 9);
        assertEquals(true, wallPayload.contains("@m"));
        assertEquals(true, wallPayload.contains(":w=1,2 l=3,4"));
        assertEquals(true, wallPayload.contains("wall-state"));
        assertEquals(true, containsSend(handlingSends, "@m"));
        handlingSends.clear();
        String petPreviewPayload = Handling.Proc_6_86_73B0D0(4, "p`" + wireLong(75));
        assertEquals(true, petPreviewPayload.contains("Ly"));
        assertEquals(true, petPreviewPayload.endsWith("3\2"));
        assertEquals(true, containsSend(handlingSends, "Ly"));
        handlingSends.clear();
        Handling.Proc_6_88_73E4F0(4);
        assertEquals(true, containsSend(handlingSends, "L\u007f"));
        handlingSends.clear();
        handlingSql.clear();
        long dimmerId = Handling.Proc_6_100_748C80(4, "EV" + wireLong(2) + wireLong(1) + wireString("#82f349") + wireLong(100));
        assertEquals(78L, dimmerId);
        assertEquals(true, containsSql(handlingSql, "UPDATE furnitures_dimmerpresets SET id_state='1'"));
        assertEquals(true, containsSql(handlingSql, "colour='#82F349'"));
        assertEquals(true, containsSql(handlingSql, "sign='2,2,1,#82F349,100'"));
        assertEquals(true, containsSend(handlingSends, "AU78\2"));
        assertEquals(true, containsSend(handlingSends, "2,2,1,#82F349,100"));
        handlingSql.clear();
        handlingSends.clear();
        assertEquals(2L, Handling.Proc_6_101_749540(4));
        assertEquals(true, containsSend(handlingSends, "GL"));
        handlingSends.clear();
        assertEquals(12L, Handling.Proc_6_102_749C50(4, "Fx" + wireLong(12)));
        assertEquals(true, containsSql(handlingSql, "UPDATE users_effects SET timestamp_expire=UNIX_TIMESTAMP()+time_rent"));
        assertEquals(true, containsSend(handlingSends, "GN"));
        assertEquals(true, containsSend(handlingSends, "Ge"));
        handlingSql.clear();
        handlingSends.clear();
        assertEquals(1L, Handling.Proc_6_103_74A510());
        assertEquals(true, containsSql(handlingSql, "DELETE FROM users_effects WHERE users_effects.timestamp_expire IS NOT NULL"));
        assertEquals(true, containsSend(handlingSends, "GO"));
        assertEquals(true, containsSend(handlingSends, "Ge"));
        handlingSql.clear();
        handlingSends.clear();
        Handling.Proc_6_104_74AB60(4);
        assertEquals(true, containsSend(handlingSends, "H@"));
        handlingSends.clear();
        Handling.Proc_6_105_74AD50(4, "@]" + wireString("Created Room") + wireString("model_a"));
        assertEquals(true, containsSql(handlingSql, "INSERT INTO rooms(id_owner,name,visitors_max,id_model,timestamp_created)"));
        assertEquals(true, containsSend(handlingSends, "@{"));
        assertEquals(true, containsSend(handlingSends, "Created Room"));
        handlingSql.clear();
        handlingSends.clear();
        Handling.Proc_6_107_74B7E0(4);
        assertEquals(true, containsSql(handlingSql, "INSERT INTO rooms_official"));
        assertEquals(true, containsSql(handlingSql, "UPDATE rooms SET is_staff_picked='1'"));
        assertEquals(true, containsSend(handlingSends, "NavRoom"));
        assertEquals(true, containsSend(handlingSends, "GH"));
        handlingSql.clear();
        handlingSends.clear();
        Handling.Proc_6_108_74D800(4);
        assertEquals(true, containsSend(handlingSends, "GJ"));
        handlingSends.clear();
        Handling.Proc_6_110_74DDA0(4, "@S" + wireLong(9));
        assertEquals(true, containsSql(handlingSql, "INSERT INTO rooms_favourites"));
        assertEquals(true, containsSend(handlingSends, "GK"));
        handlingSql.clear();
        handlingSends.clear();
        Handling.Proc_6_109_74DBD0(4, "@T" + wireLong(9));
        assertEquals(true, containsSql(handlingSql, "DELETE FROM rooms_favourites WHERE id_room='9'"));
        assertEquals(true, containsSend(handlingSends, "GK"));
        handlingSql.clear();
        handlingSends.clear();
        Handling.Proc_6_111_74DF70(4, "", "", 2, 1);
        assertEquals(true, containsSend(handlingSends, "C]CATEGORY_PAYLOAD"));
        handlingSends.clear();
        assertEquals(true, Handling.Proc_6_114_750550("rooms_events,users,rooms,rooms_categories WHERE rooms.id=rooms_events.id_room").contains("Event Room"));
        assertEquals(true, Handling.Proc_6_113_74EE70(
            "rooms_events,users,rooms,rooms_categories WHERE rooms.id=rooms_events.id_room",
            "users,rooms,rooms_categories WHERE rooms.id='9'").contains("Event Room"));
        Handling.Proc_6_115_751220(4, "GC" + wireLong(2));
        assertEquals(true, containsSend(handlingSends, "GCPC2"));
        assertEquals(true, containsSend(handlingSends, "RECOMMENDED"));
        handlingSends.clear();
        Handling.Proc_6_116_751550(4, "GC" + wireLong(2));
        assertEquals(true, containsSend(handlingSends, "GC 2"));
        assertEquals(true, containsSend(handlingSends, "RECOMMENDED"));
        handlingSends.clear();
        Handling.Proc_6_117_751880(4);
        assertEquals(true, containsSend(handlingSends, "GCQA"));
        handlingSends.clear();
        Handling.Proc_6_118_751A80(4);
        assertEquals(true, containsSend(handlingSends, "GC\0"));
        handlingSends.clear();
        Handling.Proc_6_119_751C80(4);
        assertEquals(true, containsSend(handlingSends, "GCRA"));
        handlingSends.clear();
        Handling.Proc_6_120_751E80(4);
        assertEquals(true, containsSend(handlingSends, "GCSA"));
        handlingSends.clear();
        Handling.Proc_6_121_752080(4);
        assertEquals(true, containsSend(handlingSends, "GCQA"));
        handlingSends.clear();
        Handling.Proc_6_123_754020(4);
        assertEquals(true, containsSend(handlingSends, "GB"));
        assertEquals(true, containsSend(handlingSends, "caption"));
        handlingSends.clear();
        Handling.Proc_6_124_754D90(4);
        assertEquals(true, containsSend(handlingSends, "GD"));
        assertEquals(true, containsSend(handlingSends, "tag1"));
        handlingSends.clear();
        Handling.Proc_6_125_755650(4, "XXtag1");
        assertEquals(true, containsSend(handlingSends, "GCSAtag1"));
        assertEquals(true, containsSend(handlingSends, "Event Room"));
        handlingSends.clear();
        Handling.Proc_6_126_755B40(4);
        assertEquals(true, containsSend(handlingSends, "GC\b"));
        handlingSends.clear();
        Handling.Proc_6_127_755D30(4, "XXNav");
        assertEquals(true, containsSend(handlingSends, "GCSANav"));
        assertEquals(true, containsSend(handlingSends, "Event Room"));
        handlingSends.clear();
        Handling.Proc_6_131_75C700(4);
        assertEquals(true, containsSend(handlingSends, "IoM"));
        assertEquals(true, containsSend(handlingSends, "GIFTS"));
        handlingSends.clear();
        Handling.Proc_6_134_765B90(4, "oV" + wireLong(81));
        assertEquals(true, containsSend(handlingSends, "In"));
        handlingSends.clear();
        Handling.Proc_6_135_765D80(4);
        assertEquals(true, containsSend(handlingSends, "WRAP_PAYLOAD"));
        handlingSends.clear();
        Handling.Proc_6_136_765F10(4, "xx" + wireLong(2));
        assertEquals(true, containsSend(handlingSends, "A\u007f"));
        assertEquals(true, containsSend(handlingSends, "PAGE_PAYLOAD"));
        handlingSends.clear();
        Handling.Proc_6_140_769400(4);
        assertEquals(true, containsSend(handlingSends, "BLS"));
        assertEquals(true, containsSend(handlingSends, "Id"));
        handlingSends.clear();
        handlingSql.clear();
        Handling.Proc_6_137_766470(4, "BAABCD    ");
        assertEquals(true, containsSql(handlingSql, "UPDATE users SET credits=credits+5"));
        assertEquals(true, containsSql(handlingSql, "UPDATE users SET activitypoints_0=activitypoints_0+7"));
        assertEquals(true, containsSql(handlingSql, "DELETE FROM vouchers WHERE name='ABCD0000'"));
        assertEquals(true, containsSend(handlingSends, "CTRewardA"));
        handlingSql.clear();
        handlingSends.clear();
        Handling.Proc_6_139_768100(4, "AB" + wireLong(79));
        assertEquals(true, containsSend(handlingSends, "@nwallpaper\2paper1\2"));
        assertEquals(true, containsSend(handlingSends, "Ac"));
        assertEquals(true, containsSend(handlingSends, "BLS"));
        assertEquals(true, containsSql(handlingSql, "UPDATE rooms SET id_wallpaper='paper1'"));
        assertEquals(true, containsSql(handlingSql, "DELETE FROM furnitures WHERE id='79' LIMIT 1"));
        handlingSql.clear();
        handlingSends.clear();
        Handling.Proc_6_143_76BB80(4);
        assertEquals(true, containsSend(handlingSends, "M@"));
        handlingSends.clear();
        handlingSql.clear();
        Handling.Proc_6_144_76BE70(4, "AZ" + wireLong(80));
        assertEquals(true, containsSql(handlingSql, "UPDATE furnitures SET id_room=NULL"));
        assertEquals(true, containsSend(handlingSends, "A^80\2"));
        assertEquals(true, containsSend(handlingSends, "BLS"));
        handlingSql.clear();
        handlingSends.clear();
        Licence.global_008291F8 = "";
        Licence.global_008291FC = "";
        Licence.global_00829310 = "";
        Handling.Proc_6_145_76CA20(4, 9, 81);
        assertEquals(true, Licence.global_008291F8.contains("\1" + "9\2"));
        assertEquals(true, Licence.global_008291FC.contains("\1" + "81\2"));
        Handling.Proc_6_151_78AC20(9, 82, 3);
        assertEquals(true, Licence.global_00829310.contains("\1" + "9\t82\t3\2"));
        Handling.Proc_6_146_76D300(4, 82, 506);
        assertEquals(false, Licence.global_008291FC.contains("\1" + "82\2"));
        assertEquals(true, Licence.global_00829310.contains("\1" + "9\t82\t3\2"));
        Licence.global_008291F8 = "";
        Licence.global_008291FC = "";
        Licence.global_00829310 = "";
        Handling.Proc_6_152_78C2F0(9, 83, "on");
        assertEquals(true, Licence.global_00829310.contains("\1" + "83\t9\ton\2"));
        Handling.Proc_6_153_78D980(83, "off");
        assertEquals(true, Licence.global_00829310.contains("\1" + "83\t9\toff\2"));
        assertEquals(false, Licence.global_00829310.contains("\1" + "83\t9\ton\2"));
        handlingSends.clear();
        String refreshPayload = Handling.Proc_6_154_78F040(84);
        assertEquals("AX84\2" + "5\2", refreshPayload);
        assertEquals(true, Licence.global_00829310.contains("\1" + "9\t84\t5\2"));
        assertEquals(true, containsSend(handlingSends, "AX84\2" + "5\2"));
        handlingSql.clear();
        handlingSends.clear();
        Licence.global_008291FC = "\1" + "85\2";
        Handling.Proc_6_155_795C90(4, "AC" + wireLong(85));
        assertEquals(false, Licence.global_008291FC.contains("\1" + "85\2"));
        assertEquals(true, containsSql(handlingSql, "UPDATE furnitures SET id_room=NULL"));
        assertEquals(true, containsSql(handlingSql, "WHERE id='85' AND id_room='9' LIMIT 1"));
        assertEquals(true, containsSend(handlingSends, "Ac"));
        assertEquals(true, containsSend(handlingSends, "A^85\2"));
        assertEquals(true, containsSend(handlingSends, "BLS"));
        handlingSql.clear();
        handlingSends.clear();
        Handling.Proc_6_148_7756D0(4, 508, 87);
        assertEquals(true, containsSend(handlingSends, "Iu"));
        handlingSends.clear();
        Handling.Proc_6_149_775C10(4, "Ch" + wireLong(86));
        assertEquals(true, containsSql(handlingSql, "UPDATE furnitures SET sign='1'"));
        assertEquals(true, containsSend(handlingSends, "AX86\2" + "1\2"));
        handlingSql.clear();
        handlingSends.clear();
        assertEquals(Long.valueOf(86L), Handling.Proc_6_150_777FA0(4, "FH" + wireLong(86)));
        assertEquals(true, containsSend(handlingSends, "AX86\2" + "1\2"));
        handlingSql.clear();
        handlingSends.clear();
        Licence.global_00829310 = "";
        assertEquals(1L, Handling.Proc_6_147_76E910(9, 2, 3));
        assertEquals(true, Licence.global_00829310.contains("\1" + "9\t88\t4\2"));
        assertEquals(true, containsSend(handlingSends, "AX88\2" + "4\2"));
        assertEquals(false, containsSend(handlingSends, "AX89\2" + "7\2"));
        handlingSql.clear();
        handlingSends.clear();
        Handling.Proc_6_157_7974B0(4, "rv:w=1,2 l=3,4", "9\t90\twall-state\t6\t0");
        assertEquals(true, containsSql(handlingSql, "UPDATE furnitures SET position_wall=':w=1,2 l=3,4'"));
        assertEquals(true, containsSql(handlingSql, "WHERE id='90' AND id_owner='77' AND id_room IS NULL LIMIT 1"));
        assertEquals(true, containsSend(handlingSends, "Ac"));
        assertEquals(true, containsSend(handlingSends, "AS0"));
        assertEquals(true, containsSend(handlingSends, "BLS"));
        handlingSql.clear();
        handlingSends.clear();
        assertEquals(1L, Handling.Proc_6_158_7987C0(91, 0, 0, 1, 1));
        assertEquals(0L, Handling.Proc_6_158_7987C0(91, 1, 1, 1, 1));
        assertEquals(0L, Handling.Proc_6_158_7987C0(91, 2, 2, 1, 1));
        handlingSql.clear();
        handlingSends.clear();
        String scoreboardPayload = Handling.Proc_6_160_7A71A0(4, 510, 92);
        assertEquals("AX92\2" + "0\2", scoreboardPayload);
        assertEquals(true, containsSql(handlingSql, "UPDATE furnitures SET sign='0' WHERE id='92' LIMIT 1"));
        assertEquals(true, containsSend(handlingSends, "AX92\2" + "0\2"));
        handlingSql.clear();
        handlingSends.clear();
        Handling.Proc_6_162_7B3310(4);
        assertEquals(true, containsSend(handlingSends, "DAQBHHIIKHJHPAHQA"));
        assertEquals(true, containsSend(handlingSends, "http://www.alpha-series.com/"));
        handlingSends.clear();
        String friendNotifyPayload = Handling.Proc_6_165_7BE0B0(4);
        assertEquals(true, friendNotifyPayload.startsWith("@MHIH"));
        assertEquals(true, friendNotifyPayload.contains("User77"));
        assertEquals(true, containsSend(handlingSends, "@MHIH"));
        assertEquals(true, containsSend(handlingSends, "User77"));
        handlingSends.clear();
        handlingSql.clear();
        assertEquals(1L, Handling.Proc_6_170_7C1100(4, "@fA"));
        assertEquals(true, containsSql(handlingSql, "DELETE FROM friendships WHERE id_user='77' AND has_accept='0' LIMIT 75"));
        handlingSql.clear();
        assertEquals(1L, Handling.Proc_6_170_7C1100(4, "@fCABA"));
        assertEquals(true, containsSql(handlingSql, "id_friend IN (1,2) LIMIT 75"));
        handlingSql.clear();
        handlingSends.clear();
        String followPayload = Handling.Proc_6_169_7C0DC0(4, "DF" + wireLong(88));
        assertEquals(Crypto.Proc_3_0_6D2AF0(9, null, Crypto.Proc_3_0_6D2AF0(61, null, "D^")), followPayload);
        assertEquals(true, containsSend(handlingSends, "D^"));
        handlingSends.clear();
        handlingSql.clear();
        String inviteWire = wireLong(1) + wireLong(88) + wireString("Join me");
        String inviteText = Functions.Proc_10_7_80A190(inviteWire, 0, 0);
        String invitePayload = Handling.Proc_6_168_7C05F0(4, "@b" + inviteWire);
        assertEquals(Crypto.Proc_3_0_6D2AF0(77, null, "BG") + inviteText + "\2", invitePayload);
        assertEquals(true, containsSend(handlingSends, "BG"));
        assertEquals(true, containsSql(handlingSql, "(Invite To: Target) -- " + inviteText));
        handlingSql.clear();
        handlingSends.clear();
        String acceptPayload = Handling.Proc_6_167_7BECA0(4, "@e" + wireLong(1) + wireLong(88));
        assertEquals(true, acceptPayload.startsWith("@MH"));
        assertEquals(true, acceptPayload.contains("Target"));
        assertEquals(true, containsSql(handlingSql, "INSERT IGNORE INTO friendships(id_user,id_friend,has_accept) VALUES('88','77','0')"));
        assertEquals(true, containsSql(handlingSql, "UPDATE friendships SET has_accept='1'"));
        assertEquals(true, containsSend(handlingSends, "@MHIH"));
        assertEquals(true, containsSend(handlingSends, "User77"));
        assertEquals(true, containsSend(handlingSends, "@MH"));
        handlingSql.clear();
        handlingSends.clear();
        String removePayload = Handling.Proc_6_171_7C1520(4, "@h" + wireLong(1) + wireLong(88));
        assertEquals(Handling.messengerRemoveFriendsPayload(Handling.messengerRemovedIdPayload(88), 1), removePayload);
        assertEquals(true, containsSql(handlingSql, "DELETE FROM friendships WHERE has_accept='1'"));
        assertEquals(true, containsSql(handlingSql, "id_friend IN (88)"));
        assertEquals(true, containsSend(handlingSends, "@MMIM77"));
        assertEquals(true, containsSend(handlingSends, "@MM"));
        handlingSql.clear();
        handlingSends.clear();
        String searchPayload = Handling.Proc_6_172_7C25B0(4, "@i" + wireString("targ"));
        assertEquals(true, searchPayload.startsWith("Fs"));
        assertEquals(true, searchPayload.contains("Target"));
        assertEquals(true, searchPayload.contains("Targus"));
        assertEquals(true, containsSend(handlingSends, "Fs"));
        handlingSends.clear();
        handlingSql.clear();
        String chatWire = wireLong(88) + wireString("Private hello");
        String chatText = Functions.Proc_10_7_80A190(chatWire, 0, 0);
        String privateChatPayload = Handling.Proc_6_173_7C3430(4, "@a" + chatWire);
        assertEquals(Crypto.Proc_3_0_6D2AF0(77, null, "BF") + chatText + "\2", privateChatPayload);
        assertEquals(true, containsSql(handlingSql, "(Chat To:     Target) -- " + chatText));
        assertEquals(true, containsSend(handlingSends, "BF"));
        handlingSql.clear();
        handlingSends.clear();
        String requestPayload = Handling.Proc_6_174_7C3BC0(4, "@g" + wireString("Target"));
        assertEquals(Handling.messengerRequestAcceptedCallerPayload(88), requestPayload);
        assertEquals(true, containsSql(handlingSql, "INSERT IGNORE INTO friendships(id_user,id_friend) VALUES('88','77')"));
        assertEquals(true, containsSend(handlingSends, "BD"));
        assertEquals(true, containsSend(handlingSends, "DD"));
        handlingSql.clear();
        handlingSends.clear();
        String pendingPayload = Handling.Proc_6_175_7C4800(4);
        assertEquals(Handling.messengerPendingRequestsPayload("88\tTarget"), pendingPayload);
        assertEquals(true, containsSend(handlingSends, "Dz"));
        assertEquals(true, containsSend(handlingSends, "Target"));
        handlingSends.clear();
        String friendListPayload = Handling.Proc_6_176_7C4EE0(4);
        assertEquals(true, friendListPayload.startsWith("@L"));
        assertEquals(true, friendListPayload.contains("Target"));
        assertEquals(true, friendListPayload.endsWith("PYH"));
        assertEquals(true, containsSend(handlingSends, "@MHIH"));
        assertEquals(true, containsSend(handlingSends, "@L"));
        handlingSends.clear();
        String raceListPayload = Handling.Proc_6_177_7C6580(4, "n\u007f" + wireString("dog"));
        assertEquals(Handling.petRaceListPayload("dog", "1\t1\t0\t0\tA\r2\t2\t2\t0\tB", 1, 0), raceListPayload);
        assertEquals(true, containsSend(handlingSends, "L{dog\2"));
        handlingSends.clear();
        String petInventoryPayload = Handling.Proc_6_178_7C6E60(4);
        assertEquals(Handling.petInventoryListPayload("10\tRex\t1 2 FF00AA\t4"), petInventoryPayload);
        assertEquals(true, containsSend(handlingSends, "IX"));
        assertEquals(true, containsSend(handlingSends, "Rex"));
        handlingSends.clear();
        assertEquals(2L, Handling.Proc_6_181_7CA920("Rex1"));
        String nameCheckPayload = Handling.Proc_6_182_7CAAD0(4, "@c" + wireString("Rex"));
        assertEquals(Handling.petNameValidationPayload("Rex"), nameCheckPayload);
        assertEquals(true, containsSend(handlingSends, "@d"));
        handlingSends.clear();
        String petStatusPayload = Handling.Proc_6_183_7CABF0(4, "ny" + wireLong(10));
        assertEquals(Handling.representedPetStatusPayload(10,
            new String[]{"10", "Rex", "1 2 ff", "2", "7", "100", "90", "4", "12", "5", "Owner"}), petStatusPayload);
        assertEquals(true, containsSend(handlingSends, "IY"));
        assertEquals(true, containsSend(handlingSends, "Owner"));
        handlingSends.clear();
        Licence.global_008292CC = new String[]{"", "1\t0\tgst sit", "2\t3\tgst jump"};
        String petCommandPayload = Handling.Proc_6_184_7CBDA0(4, 2);
        assertEquals(Handling.petCommandListPayload(2, Licence.global_008292CC), petCommandPayload);
        assertEquals(true, containsSend(handlingSends, "I]"));
        handlingSends.clear();
        handlingSql.clear();
        assertEquals(3L, Handling.Proc_6_185_7CC2D0(10, 3));
        assertEquals(true, containsSql(handlingSql, "UPDATE bots_petdata SET id_level='3',experience='0' WHERE id_bot='10'"));
        assertEquals(true, containsSend(handlingSends, "@X"));
        assertEquals(true, containsSend(handlingSends, "IY"));
        assertEquals(true, containsSend(handlingSends, "Ia"));
        handlingSql.clear();
        handlingSends.clear();
        assertEquals(5L, Handling.Proc_6_186_7CD040(4, "n}" + wireLong(10)));
        assertEquals(true, containsSql(handlingSql, "UPDATE bots_petdata SET scratches='5' WHERE id_bot='10'"));
        assertEquals(true, containsSql(handlingSql, "UPDATE users SET scratch_amount=scratch_amount-1,scratch_given=scratch_given+1 WHERE id='77'"));
        assertEquals(true, containsSend(handlingSends, "I^"));
        assertEquals(true, containsSend(handlingSends, "Rex"));
        handlingSends.clear();
        handlingSql.clear();
        Licence.global_008292D4 = "";
        Licence.global_00829358 = "";
        long placedPetEntityId = Handling.Proc_6_179_7C7790(4, "nz" + wireLong(10) + wireLong(2) + wireLong(3) + wireLong(4));
        assertEquals(true, placedPetEntityId > 0L);
        assertEquals("10", Handling.representedBotRecordField(placedPetEntityId, 1));
        assertEquals(true, containsSql(handlingSql, "UPDATE bots SET id_room='9',position_x='2',position_y='3',position_z='0',position_r='4' WHERE id='10'"));
        assertEquals(true, containsSend(handlingSends, "@\\"));
        assertEquals(true, containsSend(handlingSends, "I\\"));
        handlingSql.clear();
        handlingSends.clear();
        assertEquals(10L, Handling.Proc_6_180_7C96F0(4, placedPetEntityId));
        assertEquals("", Handling.representedBotRecordText(placedPetEntityId));
        assertEquals(true, containsSql(handlingSql, "UPDATE bots SET id_room=null WHERE id='10'"));
        assertEquals(true, containsSql(handlingSql, "UPDATE bots_petdata SET id_level=id_level,energy=energy,experience=experience,nutrition=nutrition,scratches=scratches WHERE id_bot='10'"));
        assertEquals(true, containsSend(handlingSends, "@]"));
        assertEquals(true, containsSend(handlingSends, "I["));
        handlingSends.clear();
        handlingSql.clear();
        Licence.global_008292D4 = "";
        Licence.global_00829358 = "";
        long guideEntityId = Handling.Proc_6_188_7CF3C0(4);
        assertEquals(true, guideEntityId > 0L);
        assertEquals("20", Handling.representedBotRecordField(guideEntityId, 1));
        assertEquals(true, containsSql(handlingSql, "UPDATE users SET tutorial_guide='1' WHERE id='77'"));
        assertEquals(true, containsSend(handlingSends, "@aYjO"));
        handlingSends.clear();
        assertEquals(1L, Handling.Proc_6_189_7D0630(4, "Fy" + wireLong(0)));
        assertEquals("", Handling.representedBotRecordText(guideEntityId));
        assertEquals(true, containsSend(handlingSends, "@]"));
        handlingSends.clear();
        String profilePayload = Handling.Proc_6_190_7D11D0(4, "Cg" + wireLong(61));
        assertEquals(Handling.representedRoomUserProfilePayload(61, "Target", "Motto", 123, "fig"), profilePayload);
        assertEquals(true, containsSend(handlingSends, "Jf"));
        assertEquals(true, containsSend(handlingSends, "Target"));
        handlingSends.clear();
        handlingSql.clear();
        String badgeInventoryPayload = Handling.Proc_6_193_7D2BB0(4);
        assertEquals(Handling.badgeInventoryPayload("ACH1\t0\t201\rMOD\t0\t202", Handling.equippedBadgePayload("VIP\t1\t203")),
            badgeInventoryPayload);
        assertEquals(true, containsSend(handlingSends, "Ce"));
        assertEquals(true, containsSend(handlingSends, "Cd"));
        handlingSends.clear();
        String badgeUpdateWire = "B^" + wireLong(1) + wireString("VIP")
            + wireLong(0) + wireLong(0) + wireLong(0) + wireLong(0);
        String updatedBadgesPayload = Handling.Proc_6_194_7D3180(4, badgeUpdateWire);
        assertEquals(Handling.equippedBadgePayload("VIP\t1\t203"), updatedBadgesPayload);
        assertEquals(true, containsSql(handlingSql, "UPDATE users_badges SET id_slot='0' WHERE id_user='77'"));
        assertEquals(true, containsSql(handlingSql, "UPDATE users_badges SET id_slot='1' WHERE id_badge='VIP' AND id_user='77'"));
        assertEquals(true, containsSend(handlingSends, "Cd"));
        handlingSends.clear();
        assertEquals(Handling.equippedBadgePayload("VIP\t1\t203"), Handling.Proc_6_195_7D38D0("77"));
        assertEquals(Handling.tagListPayload("alpha\rbeta"), Handling.Proc_6_196_7D3ED0("77"));
        String tagDisplay = Handling.Proc_6_191_7D18B0(4, "DG" + wireLong(88));
        assertEquals(Handling.tagDisplayPayload(88, Handling.tagListPayload("target")), tagDisplay);
        assertEquals(true, containsSend(handlingSends, "E^"));
        assertEquals(true, containsSend(handlingSends, "target"));
        handlingSends.clear();
        handlingSql.clear();
        Handling.Proc_6_199_7D54E0(4, "Ck" + wireLong(7));
        assertEquals(true, containsSql(handlingSql, "INSERT INTO poll_exit(id_user,id_poll) VALUES('77','7')"));
        handlingSql.clear();
        Handling.Proc_6_200_7D5770(4, "Cl" + wireLong(7) + wireLong(8) + wireLong(4) + wireString("yes"));
        assertEquals(true, containsSql(handlingSql, "INSERT INTO poll_results(id_poll,id_question,message_answer,id_user,timestamp) VALUES('7','8','yes','77',UNIX_TIMESTAMP())"));
        handlingSends.clear();
        Map<Long, String> livePollAnswers = new HashMap<>();
        livePollAnswers.put(8L, "1\t8\tYes\r2\t8\tNo");
        String livePollPayload = Handling.Proc_6_201_7D5AC0(4, "Cj" + wireLong(7));
        assertEquals(Handling.pollPayloadFromRows("7\tTitle\tThanks", "8\tQuestion?\t2", livePollAnswers), livePollPayload);
        assertEquals(true, containsSend(handlingSends, "D}"));
        assertEquals(true, containsSend(handlingSends, "Question?"));
        handlingSends.clear();
        handlingSql.clear();
        String recyclerStatus = Handling.Proc_6_203_7D7F80(4);
        assertEquals(Handling.recyclerStatusPayload(1, 0), recyclerStatus);
        assertEquals(true, containsSend(handlingSends, "G{"));
        handlingSends.clear();
        String achievementReward = Handling.Proc_6_204_7D82E0(4, 0, 3);
        assertEquals(Handling.achievementRewardPayload(0, "2\tACH_\t10\t5\t3\t7\t2", 3, 204), achievementReward);
        assertEquals(true, containsSql(handlingSql, "DELETE FROM users_badges WHERE id_user='77' AND id_badge LIKE 'ACH_%' LIMIT 1"));
        assertEquals(true, containsSql(handlingSql, "INSERT INTO users_badges(id_user,id_badge) VALUES('77','ACH_3')"));
        assertEquals(true, containsSql(handlingSql, "UPDATE users SET activitypoints_2=activitypoints_2+5,achievement_score=achievement_score+7 WHERE id='77'"));
        assertEquals(true, containsSend(handlingSends, "Fu"));
        assertEquals(true, containsSend(handlingSends, "Fv"));
        handlingSql.clear();
        handlingSends.clear();
        Handling.Proc_6_205_7D9780(4, 2);
        assertEquals(true, containsSend(handlingSends, "Fu"));
        handlingSends.clear();
        String achievementListPayload = Handling.Proc_6_206_7DA450(4);
        Map<String, Long> liveAchievementLevels = new HashMap<>();
        liveAchievementLevels.put("ACH_", 2L);
        assertEquals(Handling.achievementListPayload("2\tACH_\t10\t5\t3\t7\t2", liveAchievementLevels), achievementListPayload);
        assertEquals(true, containsSend(handlingSends, "Ft"));
        handlingSends.clear();
        assertEquals("", Handling.Proc_6_210_7E1DC0(4));
        assertEquals("5;1;7;1;5;0;", Handling.Proc_6_218_7EA200(1507));
        String liveSongInfoWire = "C]" + wireLong(2) + wireLong(50) + wireLong(51);
        String liveSongInfoPayload = Handling.Proc_6_223_7EEDD0(4, liveSongInfoWire);
        assertEquals(Handling.songInfoPayload("Song A\t3\tAuthor A\tsound-a\t50\rSong B\t4\tAuthor B\tsound-b\t51"),
            liveSongInfoPayload);
        assertEquals(true, containsSend(handlingSends, "Dl"));
        assertEquals(true, containsSend(handlingSends, "Song A"));
        handlingSends.clear();
        Licence.global_008291FC = "\1" + "300\2\1" + "40\2\1" + "999\2";
        Handling.Proc_6_224_7EF5A0(4);
        assertEquals("\1" + "999\2", Licence.global_008291FC);
        String playlistPayload = Handling.Proc_6_227_7F2400(4);
        assertEquals(Handling.jukeboxPlaylistPayload(5, "2\t40\r3\t41"), playlistPayload);
        assertEquals(true, containsSend(handlingSends, "EN"));
        handlingSends.clear();
        String diskInventoryPayload = Handling.Proc_6_228_7F2AF0(4);
        assertEquals(Handling.songDiskInventoryPayload("4\t50\r5\t51"), diskInventoryPayload);
        assertEquals(true, containsSend(handlingSends, "EM"));
        handlingSends.clear();
        String playbackPayload = Handling.Proc_6_229_7F3070(4);
        assertEquals(true, playbackPayload.startsWith("EG"));
        assertEquals(true, containsSend(handlingSends, "EG"));
        handlingSends.clear();
        handlingSql.clear();
        String mottoPayload = Handling.Proc_6_230_7F3D20(4, "Gd" + wireString("New motto"));
        assertEquals(Handling.userIdentityPayload(77, "New motto", "M", "hd-180-1"), mottoPayload);
        assertEquals(true, containsSql(handlingSql, "UPDATE users SET motto='New motto' WHERE id='77'"));
        assertEquals(true, containsSend(handlingSends, "DJ"));
        assertEquals(true, containsSend(handlingSends, "New motto"));
        handlingSends.clear();
        Handling.Proc_6_231_7F4510(4);
        assertEquals(true, containsSend(handlingSends, "IcIQA"));
        handlingSends.clear();
        handlingSql.clear();
        String liveQuestRows = "10\t1\tFirst\t\t5\t2\tvisit\t0\t7\t3\t30\r11\t2\tSecond\t\t6\t2\tvisit\t0\t7\t4\t0";
        Licence.global_00829080 = liveQuestRows;
        String questListPayload = Handling.Proc_6_236_7F8540(4);
        assertEquals(Handling.questListPayload(liveQuestRows, "10\t0\t0\t1\t0\t1\t0"), questListPayload);
        assertEquals(true, containsSend(handlingSends, "L`"));
        handlingSends.clear();
        Handling.Proc_6_234_7F75C0(4);
        assertEquals(true, containsSql(handlingSql, "UPDATE users_quests SET timestamp_done=NULL,timestamp_accepted=NULL WHERE id_user='77' LIMIT 50"));
        assertEquals(true, containsSend(handlingSends, "Lc"));
        assertEquals(true, containsSend(handlingSends, "L`"));
        handlingSends.clear();
        handlingSql.clear();
        Handling.Proc_6_232_7F45A0(4, "p^" + wireLong(10));
        assertEquals(true, containsSql(handlingSql, "INSERT INTO users_quests(id_user,id_quest,id_level,id_numericquest,timestamp_accepted) VALUES('77','10','0','10',UNIX_TIMESTAMP())"));
        assertEquals(true, containsSql(handlingSql, "UPDATE users_quests SET time_next=DATE_ADD(NOW(),INTERVAL 30 SECOND) WHERE id_user='77' AND id_quest='10' LIMIT 1"));
        assertEquals(true, containsSend(handlingSends, "L`"));
        handlingSends.clear();
        handlingSql.clear();
        Handling.Proc_6_233_7F5D60(4);
        assertEquals(true, containsSql(handlingSql, "id_numericquest='11'"));
        assertEquals(true, containsSend(handlingSends, "L`"));
        handlingSends.clear();
        handlingSql.clear();
        Handling.Proc_6_235_7F77E0(4);
        assertEquals(true, containsSql(handlingSql, "UPDATE users_quests SET time_next=DATE_ADD(NOW(),INTERVAL 30 SECOND) WHERE id_user='77' AND id_quest='10' LIMIT 1"));
        assertEquals(true, containsSend(handlingSends, "L`"));
        handlingSends.clear();
        handlingSql.clear();
        Handling.Proc_6_164_7BC820(4, 10, 10);
        assertEquals(true, containsSend(handlingSends, "Lb"));
        assertEquals(true, containsSend(handlingSends, "La"));
        assertEquals(true, containsSend(handlingSends, "Fv"));
        assertEquals(true, containsSql(handlingSql, "UPDATE users SET activitypoints_2=activitypoints_2+5 WHERE id='77' LIMIT 1"));
        assertEquals(true, containsSql(handlingSql, "UPDATE users_quests SET id_level=id_level+1,progress='0',id_numericquest='0',timestamp_done=UNIX_TIMESTAMP() WHERE id_user='77' AND id_quest='10' LIMIT 1"));
        Licence.global_00829080 = "";
        handlingSends.clear();
        handlingSql.clear();
        String ownProfilePayload = Handling.Proc_6_237_7F9ED0(4);
        assertEquals(Handling.ownProfilePayload("77\tCaller\tMotto\tM\t4\t2"), ownProfilePayload);
        assertEquals(true, containsSend(handlingSends, "@E77"));
        handlingSends.clear();
        String pointAwardPayload = Handling.Proc_6_238_7FA670(4);
        assertEquals(Handling.representedActivityPointAwardPayload(0, 75), pointAwardPayload);
        assertEquals(true, containsSql(handlingSql, "UPDATE users SET activitypoints_0=activitypoints_0+5 WHERE id='77'"));
        assertEquals(true, containsSend(handlingSends, "Fv"));
        handlingSends.clear();
        Handling.Proc_6_93_745D90(4, "AG" + wireLong(61));
        assertEquals(8, Handling.representedInteractionPartner(4));
        assertEquals(4, Handling.representedInteractionPartner(8));
        assertEquals(true, containsSend(handlingSends, "Ah"));
        assertEquals(true, containsSend(handlingSends, "8:DATA"));
        handlingSends.clear();
        Handling.Proc_6_90_742E80(4);
        assertEquals(true, containsSend(handlingSends, "Am"));
        assertEquals(true, containsSend(handlingSends, "Ao"));
        handlingSends.clear();
        String carriedTradePayload = Handling.Proc_6_91_743480(4, "FU" + wireLong(76));
        assertEquals(true, carriedTradePayload.contains("Al"));
        assertEquals(true, carriedTradePayload.contains("Trade Chair"));
        assertEquals(true, containsSend(handlingSends, "Al"));
        assertEquals(true, containsSend(handlingSends, "8:DATA"));
        handlingSends.clear();
        String removedTradePayload = Handling.Proc_6_92_744870(4, "AH" + wireLong(76));
        assertEquals(true, removedTradePayload.contains("Al"));
        assertEquals(false, removedTradePayload.contains("Trade Chair"));
        assertEquals(true, containsSend(handlingSends, "Al"));
        handlingSends.clear();
        Handling.Proc_6_94_746990(4);
        assertEquals(true, containsSend(handlingSends, "An"));
        assertEquals(true, containsSend(handlingSends, "8:DATA"));
        assertEquals(0, Handling.representedInteractionPartner(4));
        MySQL.configureDatabaseConnection(null);
        HandlingMUS.configureMusSink(null);
        Guardian.setSocketConnected(4, false);
        Guardian.setSocketConnected(8, false);
        DataManager.global_008292BC = originalProductCache;
        Licence.global_008292BC = originalLicenceProductCache;
        Licence.global_008292C0 = originalCatalogProductCache;
        Licence.global_00829244 = originalRoomCategoryPayloads;
        Licence.global_0082911C = originalRecommendedRooms;
        Licence.global_00829128 = originalRecommendedRoomCount;
        Licence.global_00829178 = originalHcGiftPayload;
        Licence.global_0082917C = originalHcGiftLookup;
        Licence.global_00829260 = originalGiftWrapPayload;
        Licence.global_00829308 = originalCatalogPagePayloads;
        Functions.global_0082928C = originalSettingsCache;
        Functions.applicationPath = originalApplicationPath.toString();
    }

    private static String wireLong(long value) {
        return Crypto.Proc_3_0_6D2AF0(value, null, "");
    }

    private static String productRow(long productId, String... columnPairs) {
        int maxColumn = 0;
        for (int index = 0; index + 1 < columnPairs.length; index += 2) {
            maxColumn = Math.max(maxColumn, (int) Long.parseLong(columnPairs[index]));
        }
        String[] fields = new String[maxColumn + 1];
        Arrays.fill(fields, "");
        fields[0] = String.valueOf(productId);
        for (int index = 0; index + 1 < columnPairs.length; index += 2) {
            fields[(int) Long.parseLong(columnPairs[index])] = columnPairs[index + 1];
        }
        return String.join("\t", fields);
    }

    private static String wireString(String value) {
        String text = value == null ? "" : value;
        long length = text.length();
        return Character.toString((char) (64L + (length / 64L)))
            + Character.toString((char) (64L + (length % 64L)))
            + text;
    }

    private static boolean containsSql(List<String> statements, String needle) {
        for (String statement : statements) {
            if (statement.contains(needle)) {
                return true;
            }
        }
        return false;
    }

    private static boolean containsSend(List<String> sends, String needle) {
        for (String send : sends) {
            if (send.contains(needle)) {
                return true;
            }
        }
        return false;
    }

    private static void assertEquals(Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("expected <" + expected + "> but got <" + actual + ">");
        }
    }
}
