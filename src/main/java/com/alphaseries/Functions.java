package com.alphaseries;

import com.alphaseries.config.AppSettingsCache;
import com.alphaseries.config.PermissionMatrix;
import com.alphaseries.dao.mysql.RoomDao;
import com.alphaseries.dao.mysql.UserDao;
import com.alphaseries.db.Database;
import com.alphaseries.game.pet.RepresentedBotRegistry;
import com.alphaseries.messages.outgoing.UserPayloads;
import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;
import com.alphaseries.vb.Vb;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.concurrent.ThreadLocalRandom;

public final class Functions {
    public static String global_0082928C = "";
    public static Object global_008292A8 = null;
    public static String applicationPath = Paths.get("").toAbsolutePath().toString();

    private Functions() {
    }

    public static String Proc_10_0_809570(Object... args) {
        if (args == null || args.length == 0) {
            return "";
        }
        Object defaultValue = args.length >= 2 ? args[1] : "";
        return settingsCache().valueOrDefault(StringUtils.text(args[0]), defaultValue);
    }

    public static boolean Proc_10_1_809790(Object... args) {
        if (args == null || args.length < 3) {
            return false;
        }
        Object hcLevel = args.length >= 4 ? args[3] : 0;
        return permissionMatrix().allows(args[0], args[1], args[2], hcLevel);
    }

    public static String Proc_10_2_8099D0(Object... args) {
        if (args == null || args.length == 0) {
            return "";
        }
        long requestedLength = NumberUtils.parseLong(args[0]);
        if (requestedLength > 100L) {
            requestedLength = 100L;
        }
        if (requestedLength < 0L) {
            requestedLength = 0L;
        }

        StringBuilder outputValue = new StringBuilder();
        for (long index = 1L; index <= requestedLength; index++) {
            if (Proc_10_4_809CA0(0, 1) == 1L) {
                outputValue.append((char) Proc_10_4_809CA0(48, 57));
            } else {
                outputValue.append((char) Proc_10_4_809CA0(97, 122));
            }
        }
        return outputValue.toString();
    }

    public static String Proc_10_3_809B90(Object... args) {
        return String.valueOf(randomLongFromArgs(args));
    }

    public static long Proc_10_4_809CA0(Object... args) {
        return randomLongFromArgs(args);
    }

    public static String Proc_10_5_809D80(Object... args) {
        if (args == null || args.length < 2) {
            return "";
        }
        String sourceValue = StringUtils.text(args[0]);
        int startAt = NumberUtils.parseInt(args[1]);
        if (startAt < 1) {
            startAt = 1;
        }
        if (args.length >= 3 && !StringUtils.text(args[2]).isEmpty()) {
            int fieldLength = NumberUtils.parseInt(args[2]);
            if (fieldLength > 0) {
                return StringUtils.mid(sourceValue, startAt, fieldLength);
            }
        }
        return StringUtils.mid(sourceValue, startAt);
    }

    public static String Proc_10_6_809F10(Object... args) {
        if (args == null || args.length == 0) {
            return "";
        }
        String sourceValue = StringUtils.text(args[0]);
        long encodedLengthSize = Crypto.Proc_3_2_6D30A0(sourceValue);
        long fieldLength = Crypto.Proc_3_3_6D3240(sourceValue);
        if (encodedLengthSize <= 0L || fieldLength <= 0L) {
            return "";
        }
        return StringUtils.mid(sourceValue, (int) encodedLengthSize + 1, (int) fieldLength);
    }

    public static String Proc_10_7_80A190(Object... args) {
        if (args == null || args.length == 0) {
            return "";
        }
        String sourceValue = StringUtils.text(args[0]);
        long fieldLength = Crypto.Proc_3_4_6D3620(sourceValue);
        if (fieldLength <= 0L) {
            return "";
        }
        return StringUtils.mid(sourceValue, 3, (int) fieldLength);
    }

    public static String Proc_10_8_80A580(Object... args) {
        if (args == null || args.length == 0) {
            return "";
        }
        long firstValue = NumberUtils.parseLong(args[0]);
        long secondValue = args.length >= 2 ? NumberUtils.parseLong(args[1]) : 0L;
        return PacketBuilder.message("Dk").appendInt(firstValue).appendInt(secondValue).build();
    }

    public static String Proc_10_9_80A680(Object... args) {
        if (args == null || args.length == 0) {
            return "";
        }
        return StringUtils.text(args[0]).replace('\0', '\u00a0');
    }

    public static String Proc_10_10_80A7F0(Object... args) {
        if (args == null || args.length == 0) {
            return "";
        }
        return StringUtils.text(args[0]).replace('\n', ' ').replace('\r', ' ');
    }

    public static String Proc_10_11_80A9C0(Object... args) {
        if (args == null || args.length == 0) {
            return "";
        }
        return StringUtils.text(args[0])
            .replace("'", "''")
            .replace("\\r", " ")
            .replace("\\n", " ")
            .replace("\"", " ");
    }

    public static long Proc_10_12_80ADB0(Object... args) {
        if (args == null || args.length < 2) {
            return 0L;
        }
        return Filesystems.Proc_7_0_8034A0(UserPayloads.roomAlert(StringUtils.text(args[0]), StringUtils.text(args[1])));
    }

    public static long Proc_10_13_80AEC0(Object... args) {
        return Proc_10_12_80ADB0(args);
    }

    public static String inventoryCacheRecord(long furnitureId, long productId, String itemData, long secondaryValue) {
        return "\1" + furnitureId + '\t' + productId + '\t' + StringUtils.text(itemData) + '\t' + secondaryValue + '\2';
    }

    public static String trimInventoryCache(String cacheText) {
        String text = StringUtils.text(cacheText);
        while (!text.isEmpty() && (text.charAt(text.length() - 1) == '\r' || text.charAt(text.length() - 1) == '\n')) {
            text = text.substring(0, text.length() - 1);
        }
        return text;
    }

    public static String inventoryCacheAddRecord(String cacheText, long furnitureId, long productId, String itemData, long secondaryValue) {
        if (furnitureId <= 0L) {
            return trimInventoryCache(cacheText);
        }
        String cache = trimInventoryCache(cacheText);
        String marker = "\1" + furnitureId + '\t';
        if (cache.contains(marker)) {
            return cache;
        }
        return cache + inventoryCacheRecord(furnitureId, productId, itemData, secondaryValue);
    }

    public static String inventoryCacheRemoveRecord(String cacheText, long furnitureId) {
        String cache = trimInventoryCache(cacheText);
        if (furnitureId <= 0L || cache.isEmpty()) {
            return cache;
        }
        String marker = "\1" + furnitureId + '\t';
        int recordStart = cache.indexOf(marker);
        if (recordStart < 0) {
            return cache;
        }
        int recordEnd = cache.indexOf('\2', recordStart);
        if (recordEnd < 0) {
            return cache;
        }
        return cache.substring(0, recordStart) + cache.substring(recordEnd + 1);
    }

    public static String inventoryAddPayload(long furnitureId, long productId, String itemData, long secondaryValue) {
        return "Ab" + Handling.inventoryItemPayload(furnitureId, productId, itemData, secondaryValue) + '\1';
    }

    public static String inventoryRemovePayload(long furnitureId) {
        return PacketBuilder.message("Ac").appendInt(furnitureId).build();
    }

    public static long Proc_10_14_80B010(Object... args) {
        if (args == null || args.length == 0) {
            return 0L;
        }
        try {
            long furnitureId = NumberUtils.parseLong(args[0]);
            if (furnitureId <= 0L) {
                return 0L;
            }
            String rowText = MySQL.Proc_5_2_6D4690(
                "SELECT id_product,id_owner,sign,id_secondary FROM furnitures WHERE id ='" + furnitureId + "' LIMIT 1",
                0, 0);
            String[] fields = rowText.split("\t", -1);
            if (rowText.isEmpty() || fields.length < 2) {
                return 0L;
            }
            long productId = NumberUtils.parseLong(fields[0]);
            long ownerId = NumberUtils.parseLong(fields[1]);
            String itemData = fields.length >= 3 ? fields[2] : "";
            long secondaryValue = fields.length >= 4 ? NumberUtils.parseLong(fields[3]) : 0L;
            if (ownerId <= 0L) {
                return 0L;
            }

            String cachePath = userInventoryCachePath(ownerId);
            String cacheText = Handling.Proc_6_239_7FC170(cachePath, 0, 0);
            String updatedCache = inventoryCacheAddRecord(cacheText, furnitureId, productId, itemData, secondaryValue);
            if (!updatedCache.equals(trimInventoryCache(cacheText))) {
                Handling.Proc_6_240_7FC2B0(cachePath, updatedCache);
            }
            long socketIndex = Licence.Proc_9_9_808AC0(ownerId, 0, 0);
            if (socketIndex > 0L) {
                HandlingMUS.Proc_12_1_821AA0((int) socketIndex,
                    inventoryAddPayload(furnitureId, productId, itemData, secondaryValue));
            }
            return 1L;
        } catch (Exception ex) {
            return 0L;
        }
    }

    public static long Proc_10_15_80BA40(Object... args) {
        if (args == null || args.length == 0) {
            return 0L;
        }
        try {
            long furnitureId = NumberUtils.parseLong(args[0]);
            if (furnitureId <= 0L) {
                return 0L;
            }
            String rowText = MySQL.Proc_5_2_6D4690(
                "SELECT id_product,id_owner,sign FROM furnitures WHERE id ='" + furnitureId + "' LIMIT 1", 0, 0);
            String[] fields = rowText.split("\t", -1);
            if (rowText.isEmpty() || fields.length < 2) {
                return 0L;
            }
            long ownerId = NumberUtils.parseLong(fields[1]);
            if (ownerId <= 0L) {
                return 0L;
            }

            String cachePath = userInventoryCachePath(ownerId);
            String cacheText = Handling.Proc_6_239_7FC170(cachePath, 0, 0);
            String updatedCache = inventoryCacheRemoveRecord(cacheText, furnitureId);
            if (!updatedCache.equals(trimInventoryCache(cacheText))) {
                Handling.Proc_6_240_7FC2B0(cachePath, updatedCache);
            }
            long socketIndex = Licence.Proc_9_9_808AC0(ownerId, 0, 0);
            if (socketIndex > 0L) {
                HandlingMUS.Proc_12_1_821AA0((int) socketIndex, inventoryRemovePayload(furnitureId));
            }
            return 1L;
        } catch (Exception ex) {
            return 0L;
        }
    }

    public static String creditsRefreshPayload(long creditsValue) {
        return UserPayloads.creditsRefresh(creditsValue);
    }

    public static String activityPointRefreshPayload(long pointType, long pointsValue) {
        return UserPayloads.activityPointRefresh(pointType, pointsValue);
    }

    public static String activityPointRefreshPayloads(long... pointValues) {
        return UserPayloads.activityPointRefreshes(pointValues);
    }

    public static long Proc_10_16_80C480(Object... args) {
        if (args == null || args.length == 0) {
            return 0L;
        }
        try {
            String userId = StringUtils.text(args[0]);
            if (userId.isEmpty()) {
                return 0L;
            }
            long socketIndex = Licence.Proc_9_9_808AC0(userId);
            if (socketIndex == 0L) {
                return 0L;
            }
            long creditsValue = userDao().credits(NumberUtils.parseLong(userId));
            HandlingMUS.Proc_12_1_821AA0((int) socketIndex, creditsRefreshPayload(creditsValue));
            return 1L;
        } catch (Exception ex) {
            return 0L;
        }
    }

    public static long Proc_10_17_80C6B0(Object... args) {
        if (args == null || args.length == 0) {
            return 0L;
        }
        try {
            String userId = StringUtils.text(args[0]);
            if (userId.isEmpty()) {
                return 0L;
            }
            long socketIndex = Licence.Proc_9_9_808AC0(userId);
            if (socketIndex == 0L) {
                return 0L;
            }
            UserDao users = userDao();
            long numericUserId = NumberUtils.parseLong(userId);
            long sentCount = 0L;
            for (long pointType = 0L; pointType <= 4L; pointType++) {
                long pointsValue = users.activityPoints(numericUserId, pointType);
                HandlingMUS.Proc_12_1_821AA0((int) socketIndex, activityPointRefreshPayload(pointType, pointsValue));
                sentCount++;
            }
            return sentCount;
        } catch (Exception ex) {
            return 0L;
        }
    }

    public static long Proc_10_18_80C9E0(Object... args) {
        if (args == null || args.length == 0) {
            return 0L;
        }
        try {
            long roomId = Vb.val(args[0]);
            if (roomId <= 0L) {
                return 0L;
            }
            String rowText = MySQL.Proc_5_2_6D4690(
                "SELECT users.id_socket FROM logs_visitedrooms,users WHERE logs_visitedrooms.id_room='" + roomId
                    + "' AND logs_visitedrooms.timestamp_left IS NULL AND users.id=logs_visitedrooms.id_user"
                    + " AND users.id_socket IS NOT NULL", 0, 0);
            String sentMarkers = "";
            long readyCount = 0L;
            for (String row : Vb.cStr(rowText).split("\r", -1)) {
                if (row.isEmpty()) {
                    continue;
                }
                String[] fields = row.split("\t", -1);
                int socketIndex = (int) Vb.val(fields.length > 0 ? fields[0] : "");
                String marker = "[" + socketIndex + "]";
                if (socketIndex > 0 && !sentMarkers.contains(marker)) {
                    Handling.Proc_6_53_718E00(socketIndex, 0, 0);
                    sentMarkers += marker;
                    readyCount++;
                }
            }
            return readyCount;
        } catch (Exception ex) {
            return 0L;
        }
    }

    public static String roomAlertPayload(String alertType, String alertText) {
        return UserPayloads.roomAlert(alertType, alertText);
    }

    public static long Proc_10_20_80CF60(Object... args) {
        if (args == null || args.length < 3) {
            return 0L;
        }
        try {
            String userId = Vb.cStr(args[0]);
            if (userId.isEmpty()) {
                return 0L;
            }
            long socketIndex = Licence.Proc_9_9_808AC0(userId);
            if (socketIndex <= 0L) {
                return 0L;
            }
            HandlingMUS.Proc_12_1_821AA0((int) socketIndex, roomAlertPayload(Vb.cStr(args[1]), Vb.cStr(args[2])));
            return 1L;
        } catch (Exception ex) {
            return 0L;
        }
    }

    public static long Proc_10_21_80D0A0(Object... args) {
        if (args == null || args.length < 3) {
            return 0L;
        }
        try {
            long roomId = Vb.val(args[0]);
            if (roomId <= 0L) {
                return 0L;
            }
            String payload = roomAlertPayload(Vb.cStr(args[1]), Vb.cStr(args[2]));
            String rowText = MySQL.Proc_5_2_6D4690(
                "SELECT users.id_socket FROM logs_visitedrooms,users WHERE logs_visitedrooms.id_room='" + roomId
                    + "' AND logs_visitedrooms.timestamp_left IS NULL AND users.id=logs_visitedrooms.id_user"
                    + " AND users.id_socket IS NOT NULL", 0, 0);
            String sentMarkers = "";
            long sentCount = 0L;
            for (String row : Vb.cStr(rowText).split("\r", -1)) {
                if (row.isEmpty()) {
                    continue;
                }
                String[] fields = row.split("\t", -1);
                int socketIndex = (int) Vb.val(fields.length > 0 ? fields[0] : "");
                String marker = "[" + socketIndex + "]";
                if (socketIndex > 0 && !sentMarkers.contains(marker)) {
                    HandlingMUS.Proc_12_1_821AA0(socketIndex, payload);
                    sentMarkers += marker;
                    sentCount++;
                }
            }
            return sentCount;
        } catch (Exception ex) {
            return 0L;
        }
    }

    public static String emailValidatedPayload(long emailState) {
        return UserPayloads.emailValidated(emailState);
    }

    public static long Proc_10_19_80CCD0(Object... args) {
        if (args == null || args.length == 0) {
            return 0L;
        }
        try {
            String userId = String.valueOf(Vb.val(args[0]));
            if ("0".equals(userId)) {
                return 0L;
            }
            long numericUserId = Vb.val(userId);
            UserDao userDao = userDao();
            userDao.markEmailValidated(numericUserId);
            long socketIndex = Licence.Proc_9_9_808AC0(userId, 0, 0);
            if (socketIndex <= 0L) {
                return 0L;
            }
            long emailState = userDao.emailValidated(numericUserId);
            HandlingMUS.Proc_12_1_821AA0((int) socketIndex, emailValidatedPayload(emailState));
            return 1L;
        } catch (Exception ex) {
            return 0L;
        }
    }

    public static String userIdentityRefreshPayload(long userId, String mottoText, String figureText, String genderText) {
        return UserPayloads.identityRefresh(userId, mottoText, figureText, genderText);
    }

    public static long Proc_10_22_80D460(Object... args) {
        if (args == null || args.length == 0) {
            return 0L;
        }
        try {
            String requestedUserId = Vb.cStr(args[0]);
            if (requestedUserId.isEmpty() || "0".equals(requestedUserId)) {
                return 0L;
            }
            UserDao.UserIdentity identity = userDao().findIdentity(Vb.val(requestedUserId)).orElse(null);
            if (identity == null) {
                return 0L;
            }
            long userId = identity.userId();
            long socketIndex = identity.socketIndex();
            if (socketIndex <= 0L) {
                socketIndex = Licence.Proc_9_9_808AC0(String.valueOf(userId), 0, 0);
            }
            if (socketIndex <= 0L) {
                return 0L;
            }
            HandlingMUS.Proc_12_1_821AA0((int) socketIndex,
                userIdentityRefreshPayload(userId, identity.motto(), identity.figure(), identity.gender()));
            return 1L;
        } catch (Exception ex) {
            return 0L;
        }
    }

    private static UserDao userDao() throws SQLException {
        return new UserDao(configuredDatabase());
    }

    private static RoomDao roomDao() throws SQLException {
        return new RoomDao(configuredDatabase());
    }

    private static Database configuredDatabase() throws SQLException {
        if (MySQL.configuredDatabase() == null) {
            throw new SQLException("Database is not configured.");
        }
        return MySQL.configuredDatabase();
    }

    public static String clubPeriodUpdateQuery(long userId, long hcRank, long currentPeriods, long paidDays, long giftIncrementDefault) {
        if (userId <= 0L) {
            return "";
        }
        long periodIncrement;
        long giftIncrement;
        if (currentPeriods > 0L || paidDays > 0L) {
            long effectivePaidDays = paidDays <= 0L ? currentPeriods : paidDays;
            periodIncrement = effectivePaidDays / 31L;
            if (periodIncrement < 1L) {
                periodIncrement = 1L;
            }
            giftIncrement = 0L;
        } else {
            periodIncrement = 1L;
            giftIncrement = giftIncrementDefault;
        }
        String periodColumn = hcRank > 1L ? "hc2" : "hc";
        return "UPDATE users SET hc_startperiod=UNIX_TIMESTAMP(),"
            + periodColumn + "_periods=" + periodColumn + "_periods+" + periodIncrement
            + ",hc_presents=hc_presents+" + giftIncrement
            + " WHERE id='" + userId + "'";
    }

    public static long Proc_10_23_80E110(Object... args) {
        if (args == null || args.length < 3) {
            return 0L;
        }
        try {
            long userId = Vb.val(args[0]);
            long hcRank = Vb.val(args[1]);
            long currentPeriods = Vb.val(args[2]);
            long paidDays = args.length >= 4 ? Vb.val(args[3]) : 0L;
            if (userId <= 0L) {
                return 0L;
            }
            long giftIncrementDefault = Vb.val(Proc_10_0_809570(
                "com.server.socket.game.club.gifts.hcrank" + hcRank + ".amount", 0, 0));
            MySQL.Proc_5_0_6D3CD0(clubPeriodUpdateQuery(userId, hcRank, currentPeriods, paidDays, giftIncrementDefault), 0, 0);
            return 1L;
        } catch (Exception ex) {
            return 0L;
        }
    }

    public static String Proc_10_24_80E790(Object... args) {
        return movementStep(args);
    }

    public static long Proc_10_25_80F5D0(Object... args) {
        if (args == null || args.length < 3) {
            return 0L;
        }
        try {
            long roomId = Vb.val(args[0]);
            long positionX = Vb.val(args[1]);
            long positionY = Vb.val(args[2]);
            if (roomId <= 0L) {
                return 1L;
            }

            RoomDao roomDao = roomDao();
            long occupiedCount = roomDao.furnitureCountAt(roomId, positionX, positionY);
            if (occupiedCount > 0L) {
                return 0L;
            }

            long botCount = roomDao.botCountAt(roomId, positionX, positionY);
            return representedPositionAvailable(roomId, occupiedCount, botCount);
        } catch (Exception ex) {
            return 0L;
        }
    }

    public static String Proc_10_26_81E4E0(Object... args) {
        return movementStep(args);
    }

    public static long Proc_10_27_81F1A0(Object... args) {
        if (args == null || args.length < 3) {
            return 0L;
        }
        try {
            long botEntityId = Vb.val(args[0]);
            long positionX = Vb.val(args[1]);
            long positionY = Vb.val(args[2]);
            if (botEntityId <= 0L) {
                return 0L;
            }

            long roomSlot = Licence.representedBots().recordLong(botEntityId, 0);
            long botId = Licence.representedBots().recordLong(botEntityId, 1);
            long roomId = 0L;
            RoomDao roomDao = roomDao();
            if (roomSlot > 0L) {
                roomId = roomDao.roomIdBySlot(roomSlot);
            }
            if (roomId <= 0L) {
                if (botId <= 0L) {
                    botId = botEntityId;
                }
                roomId = roomDao.roomIdByBot(botId);
            }
            if (roomId <= 0L) {
                return 1L;
            }
            return Proc_10_25_80F5D0(roomId, positionX, positionY);
        } catch (Exception ex) {
            return 0L;
        }
    }

    public static boolean Proc_10_28_8210C0(Object... args) {
        if (args == null || args.length < 2) {
            return false;
        }
        try (InputStream input = new URL(Vb.cStr(args[0])).openStream()) {
            Files.copy(input, Paths.get(Vb.cStr(args[1])), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static long movementDirectionCode(long deltaX, long deltaY) {
        if (deltaX == 0L && deltaY < 0L) {
            return 0L;
        } else if (deltaX > 0L && deltaY < 0L) {
            return 1L;
        } else if (deltaX > 0L && deltaY == 0L) {
            return 2L;
        } else if (deltaX > 0L && deltaY > 0L) {
            return 3L;
        } else if (deltaX == 0L && deltaY > 0L) {
            return 4L;
        } else if (deltaX < 0L && deltaY > 0L) {
            return 5L;
        } else if (deltaX < 0L && deltaY == 0L) {
            return 6L;
        } else if (deltaX < 0L && deltaY < 0L) {
            return 7L;
        }
        return 0L;
    }

    public static String representedBotRecordField(String botCacheText, long botEntityId, long fieldIndex) {
        return RepresentedBotRegistry.fromLegacy("", botCacheText).recordField(botEntityId, fieldIndex);
    }

    public static long representedPositionAvailable(long roomId, long furnitureCount, long botCount) {
        if (roomId <= 0L) {
            return 1L;
        }
        if (furnitureCount > 0L) {
            return 0L;
        }
        return botCount == 0L ? 1L : 0L;
    }

    public static String readSettingsValue(String settingsText, String keyName) {
        return AppSettingsCache.fromLegacy(settingsText).value(keyName);
    }

    public static AppSettingsCache settingsCache() {
        return AppSettingsCache.fromLegacy(global_0082928C);
    }

    public static void setSettingsCache(String settingsCache) {
        global_0082928C = settingsCache == null ? "" : settingsCache;
    }

    public static PermissionMatrix permissionMatrix() {
        return PermissionMatrix.fromLegacy(global_008292A8);
    }

    public static void setPermissions(Object permissions) {
        global_008292A8 = permissions == null ? "" : permissions;
    }

    public static String userInventoryCachePath(long ownerId) {
        Path basePath = Paths.get(Vb.cStr(applicationPath));
        return basePath.resolve("cache").resolve("users").resolve(ownerId + ".cache").toString();
    }

    private static String movementStep(Object... args) {
        if (args == null) {
            return zeroMovement();
        }
        long currentX = 0L;
        long currentY = 0L;
        long targetX;
        long targetY;
        if (args.length >= 5) {
            currentX = Vb.val(args[1]);
            currentY = Vb.val(args[2]);
            targetX = Vb.val(args[3]);
            targetY = Vb.val(args[4]);
        } else if (args.length >= 3) {
            targetX = Vb.val(args[1]);
            targetY = Vb.val(args[2]);
        } else {
            return zeroMovement();
        }

        long deltaX = Long.compare(targetX - currentX, 0L);
        long deltaY = Long.compare(targetY - currentY, 0L);
        long nextX = currentX + deltaX;
        long nextY = currentY + deltaY;
        long isMoving = nextX != currentX || nextY != currentY ? 1L : 0L;
        long directionValue = movementDirectionCode(deltaX, deltaY);
        return nextX + "\0" + nextY + "\0" + directionValue + "\0" + isMoving + "\0";
    }

    private static String zeroMovement() {
        return "0\0" + "0\0" + "0\0" + "0\0";
    }

    private static long randomLongFromArgs(Object... args) {
        if (args == null || args.length < 2) {
            return 0L;
        }
        return randomInclusive(Vb.val(args[0]), Vb.val(args[1]));
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static long randomInclusive(long lower, long upper) {
        long min = Math.min(lower, upper);
        long max = Math.max(lower, upper);
        if (min == max) {
            return min;
        }
        return ThreadLocalRandom.current().nextLong(min, max + 1L);
    }
}
