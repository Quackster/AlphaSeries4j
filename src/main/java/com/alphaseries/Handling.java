package com.alphaseries;

import com.alphaseries.game.pet.PetPayloads;
import com.alphaseries.game.inventory.InventoryMessagePayloads;
import com.alphaseries.game.catalog.GiftSettings;
import com.alphaseries.game.chat.ChatSettings;
import com.alphaseries.game.help.HelpCenterCache;
import com.alphaseries.game.moderation.StaffPayloads;
import com.alphaseries.game.recycler.RecyclerSettings;
import com.alphaseries.game.wired.WiredPayloads;
import com.alphaseries.messages.outgoing.AchievementPayloads;
import com.alphaseries.messages.outgoing.JukeboxPayloads;
import com.alphaseries.messages.outgoing.MessengerPayloads;
import com.alphaseries.messages.outgoing.PollPayloads;
import com.alphaseries.messages.outgoing.RecyclerPayloads;
import com.alphaseries.messages.outgoing.SocialPayloads;
import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.vb.Vb;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public final class Handling {
    private static String representedInteractionPairs = "";
    private static String representedTradeOffers = "";
    private static String representedActivityPointTicks = "";

    private Handling() {
    }

    public static void Proc_6_0_6D7FF0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "GF");
            long targetUserId = Vb.val(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
            if (targetUserId <= 0L) {
                targetUserId = readWireLong(requestPayload, new LongRef(1));
            }
            if (targetUserId <= 0L) {
                return;
            }
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId) || !handlingUserHasPermission(callerUserId, "fuse_mod")) {
                return;
            }
            String escapedTarget = Functions.Proc_10_11_80A9C0(targetUserId, 0, 0);
            String userRow = MySQL.Proc_5_2_6D4690("SELECT users.id,users.name,ROUND((UNIX_TIMESTAMP()-users.create_time)/60,0),"
                + "ROUND((UNIX_TIMESTAMP()-users.lastonline_time)/60,0),users.id_socket FROM users WHERE users.id='"
                + escapedTarget + "' LIMIT 1", 0, 0);
            if (userRow.isEmpty()) {
                return;
            }
            String payload = staffUserSummaryPayload(userRow,
                Vb.val(MySQL.Proc_5_2_6D4690("SELECT COUNT(id) FROM staff_cfh WHERE id_user='" + targetUserId + "'", 0, 0)),
                Vb.val(MySQL.Proc_5_2_6D4690("SELECT COUNT(id) FROM staff_cfh WHERE id_user='" + targetUserId + "' AND id_closed='2'", 0, 0)),
                Vb.val(MySQL.Proc_5_2_6D4690("SELECT COUNT(id) FROM users_cautions WHERE id_user='" + targetUserId + "'", 0, 0)),
                Vb.val(MySQL.Proc_5_2_6D4690("SELECT COUNT(id) FROM users_bans WHERE id_user='" + targetUserId + "'", 0, 0)));
            if (!payload.isEmpty()) {
                Proc_6_244_801E80(socketIndex, payload, 0);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_1_6D8B70(Object... args) {
        staffDirectMessage(args, "GM", "fuse_alert", "4", false, false);
    }

    public static void Proc_6_2_6D9880(Object... args) {
        staffDirectMessage(args, "GO", "fuse_kick", "5", true, false);
    }

    public static void Proc_6_3_6DA490(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "GP");
            LongRef offset = new LongRef(1);
            long targetUserId = readWireLong(requestPayload, offset);
            if (targetUserId <= 0L) {
                targetUserId = Vb.val(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
            }
            String banMessage = Functions.Proc_10_10_80A7F0(readWireString(requestPayload, offset), 0, 0);
            if (banMessage.isEmpty()) {
                banMessage = Functions.Proc_10_10_80A7F0(Functions.Proc_10_7_80A190(requestPayload, 0, 0), 0, 0);
            }
            long banHours = readWireLong(requestPayload, offset);
            if (banHours <= 0L) {
                banHours = Vb.val(Functions.Proc_10_6_809F10(Vb.mid(requestPayload, (int) offset.value), 0, 0));
            }
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (targetUserId <= 0L || banMessage.isEmpty() || banHours <= 0L
                || callerUserId.isEmpty() || "0".equals(callerUserId)
                || !handlingUserHasPermission(callerUserId, "fuse_mod")
                || !handlingUserHasPermission(callerUserId, "fuse_alert")
                || containsUnsafeStaffAlert(banMessage)) {
                return;
            }
            long currentRoomId = handlingCurrentRoomId(socketIndex, callerUserId);
            MySQL.Proc_5_1_6D4110("INSERT INTO logs_moderation(id_type,id_user,id_target,id_target_2,timestamp,message,id_session) VALUES('6','"
                + Functions.Proc_10_11_80A9C0(callerUserId, 0, 0) + "','" + Functions.Proc_10_11_80A9C0(targetUserId, 0, 0)
                + "','" + currentRoomId + "',UNIX_TIMESTAMP(),'" + Functions.Proc_10_11_80A9C0(banMessage, 0, 0)
                + "','" + socketIndex + "')", 0, 0);
            String targetIpAddress = MySQL.Proc_5_2_6D4690("SELECT ip_last FROM users WHERE id='"
                + Functions.Proc_10_11_80A9C0(targetUserId, 0, 0) + "' LIMIT 1", 0, 0);
            long banSeconds = banHours * 60L * 60L;
            if (!targetIpAddress.isEmpty()) {
                MySQL.Proc_5_0_6D3CD0("INSERT INTO users_bans(id_user,id_partner,message,timestamp_expire,timestamp_submit,ipaddress) VALUES('"
                    + Functions.Proc_10_11_80A9C0(targetUserId, 0, 0) + "','" + Functions.Proc_10_11_80A9C0(callerUserId, 0, 0)
                    + "','" + Functions.Proc_10_11_80A9C0(banMessage, 0, 0) + "',UNIX_TIMESTAMP()+" + banSeconds
                    + ",UNIX_TIMESTAMP(),'" + Functions.Proc_10_11_80A9C0(targetIpAddress, 0, 0) + "')", 0, 0);
            } else {
                MySQL.Proc_5_0_6D3CD0("INSERT INTO users_bans(id_user,id_partner,message,timestamp_expire,timestamp_submit) VALUES('"
                    + Functions.Proc_10_11_80A9C0(targetUserId, 0, 0) + "','" + Functions.Proc_10_11_80A9C0(callerUserId, 0, 0)
                    + "','" + Functions.Proc_10_11_80A9C0(banMessage, 0, 0) + "',UNIX_TIMESTAMP()+" + banSeconds
                    + ",UNIX_TIMESTAMP())", 0, 0);
            }
            MySQL.Proc_5_0_6D3CD0("UPDATE users SET login_session=NULL WHERE id='" + Functions.Proc_10_11_80A9C0(targetUserId, 0, 0) + "'", 0, 0);
            int targetSocketIndex = handlingSocketFromUserId(String.valueOf(targetUserId));
            if (targetSocketIndex > 0) {
                Proc_6_244_801E80(targetSocketIndex, "@c" + banMessage + '\2', 0);
                Proc_6_243_7FFEB0(targetSocketIndex, 0, 0);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static long Proc_6_4_6DAFB0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "CH");
            LongRef offset = new LongRef(1);
            long actionType = readWireLong(requestPayload, offset);
            if (actionType <= 0L) {
                actionType = Vb.val(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
            }
            String messageText = readWireString(requestPayload, offset);
            if (messageText.isEmpty()) {
                messageText = Functions.Proc_10_7_80A190(requestPayload, 0, 0);
            }
            messageText = Functions.Proc_10_10_80A7F0(messageText, 0, 0);
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId) || !handlingUserHasPermission(callerUserId, "fuse_mod")) {
                return 0L;
            }
            long roomId = handlingCurrentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L || actionType <= 0L || messageText.isEmpty() || containsUnsafeStaffAlert(messageText)) {
                return 0L;
            }
            String roomText = MySQL.Proc_5_2_6D4690("SELECT id_slot,id_owner FROM rooms WHERE id='" + roomId + "' LIMIT 1", 0, 0);
            String[] roomFields = roomText.split("\t", -1);
            String roomOwnerId = String.valueOf(Vb.val(handlingField(roomFields, 1)));
            if (roomOwnerId.isEmpty() || "0".equals(roomOwnerId)) {
                return 0L;
            }
            long logType = actionType == 1L ? 1L : 2L;
            MySQL.Proc_5_1_6D4110("INSERT INTO logs_moderation(id_type,id_user,id_target,timestamp,message,id_session) VALUES('"
                + logType + "','" + Functions.Proc_10_11_80A9C0(callerUserId, 0, 0) + "','" + roomId
                + "',UNIX_TIMESTAMP(),'" + Functions.Proc_10_11_80A9C0(messageText, 0, 0) + "','" + socketIndex + "')", 0, 0);
            broadcastToRoomUsers(roomId, "Ba" + messageText + '\2');
            if (actionType == 1L || actionType == 4L) {
                MySQL.Proc_5_0_6D3CD0("DELETE FROM rooms_events WHERE id_room='" + roomId + "' LIMIT 1", 0, 0);
                Functions.Proc_10_18_80C9E0(roomId, 0, 0);
            }
            if (actionType == 1L) {
                MySQL.Proc_5_0_6D3CD0("INSERT INTO users_cautions(id_user,id_partner,message,timestamp_submit) VALUES('"
                    + Functions.Proc_10_11_80A9C0(roomOwnerId, 0, 0) + "','" + Functions.Proc_10_11_80A9C0(callerUserId, 0, 0)
                    + "','" + Functions.Proc_10_11_80A9C0(messageText + " (Room caution of room id: " + roomId + ")", 0, 0)
                    + "',UNIX_TIMESTAMP())", 0, 0);
            }
            return actionType;
        } catch (Exception ignored) {
            return 0L;
        }
    }

    public static void Proc_6_5_6DC340(Object... args) {
        try {
            long callForHelpId = args != null && args.length >= 1 ? Vb.val(args[0]) : 0L;
            int socketIndex = args != null && args.length >= 2 ? (int) Vb.val(args[1]) : 0;
            if (callForHelpId <= 0L) {
                return;
            }
            String rowText = MySQL.Proc_5_2_6D4690("SELECT staff_cfh.id,users.id,users.name,staff_cfh.id_partner,staff_cfh.id_room,"
                + "staff_cfh.id_category,staff_cfh.description,rooms.id,rooms.name FROM staff_cfh,users,rooms WHERE staff_cfh.id='"
                + callForHelpId + "' AND staff_cfh.id_closed='0' AND users.id=staff_cfh.id_user AND rooms.id=staff_cfh.id_room LIMIT 1", 0, 0);
            if (rowText.isEmpty()) {
                return;
            }
            String[] fields = rowText.split("\t", -1);
            String representedRow = handlingField(fields, 0) + "\t\t" + handlingField(fields, 1) + "\t" + handlingField(fields, 2)
                + "\t" + handlingField(fields, 3) + "\t" + handlingField(fields, 4) + "\t" + handlingField(fields, 5)
                + "\t" + handlingField(fields, 6) + "\t" + handlingField(fields, 7) + "\t" + handlingField(fields, 8) + "\t";
            String payload = "HR" + callForHelpRowPayload(representedRow, null);
            if (socketIndex > 0) {
                Proc_6_244_801E80(socketIndex, payload, 0);
            } else {
                Proc_6_249_802F10(payload, 0, 0);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_6_6DC9D0(Object... args) {
        updateCallForHelpTab(args, "GB", "2");
    }

    public static void Proc_6_7_6DD0E0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "GD");
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)
                || !handlingUserHasPermission(callerUserId, "fuse_mod")
                || !handlingUserHasPermission(callerUserId, "fuse_receive_calls_for_help")) {
                return;
            }
            LongRef offset = new LongRef(1);
            long closeState = readWireLong(requestPayload, offset);
            if (closeState < 1L || closeState > 3L) {
                return;
            }
            long callForHelpId = readWireLong(requestPayload, offset);
            if (callForHelpId <= 0L) {
                callForHelpId = readWireLong(requestPayload, offset);
            }
            if (callForHelpId <= 0L) {
                return;
            }
            String reporterUserId = String.valueOf(Vb.val(MySQL.Proc_5_2_6D4690("SELECT id_user FROM staff_cfh WHERE id='"
                + callForHelpId + "' LIMIT 1", 0, 0)));
            int reporterSocketIndex = handlingSocketFromUserId(reporterUserId);
            if (reporterSocketIndex > 0) {
                Proc_6_244_801E80(reporterSocketIndex, Crypto.Proc_3_0_6D2AF0(closeState, null, "H\\"), 0);
            }
            MySQL.Proc_5_0_6D3CD0("UPDATE staff_cfh SET id_closed='" + closeState + "',id_tab='0' WHERE id='" + callForHelpId + "'", 0, 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_8_6DD790(Object... args) {
        updateCallForHelpTab(args, "GC", "1");
    }

    public static void Proc_6_9_6DDD70(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "GL");
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId) || !handlingUserHasPermission(callerUserId, "fuse_mod")) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, callerUserId);
            LongRef offset = new LongRef(1);
            readWireLong(requestPayload, offset);
            long lockFlag = readWireLong(requestPayload, offset);
            if (roomId > 0L && lockFlag == 1L) {
                MySQL.Proc_5_0_6D3CD0("UPDATE rooms SET status_door='1', name='Inappropriate to hotel management' WHERE id='" + roomId + "'", 0, 0);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_10_6DE1D0(Object... args) {
        staffRoomHistory(args, "GG", true);
    }

    public static void Proc_6_11_6DF4A0(Object... args) {
        staffRoomHistory(args, "GJ", false);
    }

    public static void Proc_6_12_6DFE90(Object... args) {
        staffDirectMessage(args, "GN", "fuse_alert", "3", false, true);
    }

    public static long Proc_6_13_6E0A80(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return 0L;
            }
            long roomUserIndex = representedRoomUserIndex(socketIndex, userId);
            Proc_6_247_8027E0(socketIndex, Crypto.Proc_3_0_6D2AF0(roomUserIndex, null, "Ga"), 0);
            return roomUserIndex;
        } catch (Exception ignored) {
            return 0L;
        }
    }

    public static long Proc_6_14_6E10C0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "A]");
            long danceId = Vb.val(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
            if (danceId <= 0L) {
                danceId = readWireLong(requestPayload, new LongRef(1));
            }
            if (danceId < 0L) {
                danceId = 0L;
            }
            if (danceId > 4L) {
                danceId = 4L;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return 0L;
            }
            long roomUserIndex = representedRoomUserIndex(socketIndex, userId);
            String payload = Crypto.Proc_3_0_6D2AF0(danceId, null,
                Crypto.Proc_3_0_6D2AF0(roomUserIndex, null, "G`"));
            Proc_6_247_8027E0(socketIndex, payload, 0);
            return danceId;
        } catch (Exception ignored) {
            return 0L;
        }
    }

    public static void Proc_6_15_6E1900(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId) || !handlingUserHasPermission(userId, "fuse_use_wardrobe")) {
                return;
            }
            long maxSlots = handlingUserHasPermission(userId, "fuse_larger_wardrobe") ? 10L : 5L;
            String rowText = MySQL.Proc_5_2_6D4690("SELECT id_slot,figure,gender FROM users_wardrobe WHERE id_user='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "' ORDER BY id_slot", 0, 0);
            long slotCount = 0L;
            StringBuilder wardrobePayload = new StringBuilder();
            for (String row : rowText.split("\r", -1)) {
                if (!row.isEmpty()) {
                    String[] fields = row.split("\t", -1);
                    if (fields.length >= 3) {
                        long slotId = Vb.val(handlingField(fields, 0));
                        if (slotId >= 1L && slotId <= maxSlots) {
                            String genderText = Vb.left(handlingField(fields, 2).toUpperCase(), 1);
                            if (!"M".equals(genderText) && !"F".equals(genderText)) {
                                genderText = "M";
                            }
                            wardrobePayload.append(wardrobeSlotPayload(slotId, handlingField(fields, 1), genderText));
                            slotCount++;
                        }
                    }
                }
            }
            Proc_6_244_801E80(socketIndex, Crypto.Proc_3_0_6D2AF0(slotCount, null, "DK") + wardrobePayload, 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_16_6E2320(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "Ex");
            LongRef offset = new LongRef(1);
            long slotId = readWireLong(requestPayload, offset);
            String figureText = Functions.Proc_10_10_80A7F0(readWireString(requestPayload, offset), 0, 0);
            String genderText = Vb.left(readWireString(requestPayload, offset).toUpperCase(), 1);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId) || !handlingUserHasPermission(userId, "fuse_use_wardrobe")) {
                return;
            }
            long maxSlots = handlingUserHasPermission(userId, "fuse_larger_wardrobe") ? 10L : 5L;
            if (slotId < 1L || slotId > maxSlots || (!"M".equals(genderText) && !"F".equals(genderText))) {
                return;
            }
            String figureData = Proc_6_239_7FC170(Functions.applicationPath + "/figuredata.cache", 0, 0);
            if (!isValidWardrobeFigure(figureText, genderText, figureData)) {
                return;
            }
            MySQL.Proc_5_0_6D3CD0("DELETE FROM users_wardrobe WHERE id_user='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "' AND id_slot='" + slotId + "' LIMIT 1", 0, 0);
            MySQL.Proc_5_0_6D3CD0("INSERT INTO users_wardrobe(id_user,id_slot,figure,gender) VALUES('"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "','" + slotId + "','"
                + Functions.Proc_10_11_80A9C0(figureText, 0, 0) + "','"
                + Functions.Proc_10_11_80A9C0(genderText, 0, 0) + "')", 0, 0);
            Proc_6_15_6E1900(socketIndex, "Ew", "Ew");
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_17_6E48D0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "@l");
            LongRef offset = new LongRef(1);
            String genderText = Vb.left(readWireString(requestPayload, offset).toUpperCase(), 1);
            String figureText = Functions.Proc_10_10_80A7F0(readWireString(requestPayload, offset), 0, 0);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId) || (!"M".equals(genderText) && !"F".equals(genderText))) {
                return;
            }
            String figureData = Proc_6_239_7FC170(Functions.applicationPath + "/figuredata.cache", 0, 0);
            if (!isValidWardrobeFigure(figureText, genderText, figureData)) {
                return;
            }
            MySQL.Proc_5_0_6D3CD0("UPDATE users SET tutorial_clothes='1',gender='"
                + Functions.Proc_10_11_80A9C0(genderText, 0, 0) + "',figure='"
                + Functions.Proc_10_11_80A9C0(figureText, 0, 0) + "' WHERE id='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "'", 0, 0);
            String mottoText = MySQL.Proc_5_2_6D4690("SELECT motto FROM users WHERE id='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "' LIMIT 1", 0, 0);
            String payload = userIdentityPayload(Vb.val(userId), mottoText, genderText, figureText);
            Proc_6_244_801E80(socketIndex, payload, 0);
            Proc_6_247_8027E0(socketIndex, payload, 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_18_6E7480(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (socketIndex <= 0 || userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long offerCount = 0L;
            StringBuilder offerPayload = new StringBuilder();
            String offerRows = MySQL.Proc_5_2_6D4690("SELECT id,sprite_name,months,level,price_credits FROM products_club ORDER BY id ASC", 0, 0);
            for (String row : offerRows.split("\r", -1)) {
                if (!row.isEmpty()) {
                    String[] fields = row.split("\t", -1);
                    if (fields.length >= 5) {
                        long months = Vb.val(handlingField(fields, 2));
                        offerPayload.append(Crypto.Proc_3_0_6D2AF0(Vb.val(handlingField(fields, 0)), null, ""));
                        offerPayload.append(handlingField(fields, 1)).append('\2');
                        offerPayload.append(Crypto.Proc_3_0_6D2AF0(months, null, ""));
                        offerPayload.append(Crypto.Proc_3_0_6D2AF0(months * 31L, null, ""));
                        offerPayload.append(Crypto.Proc_3_0_6D2AF0(Vb.val(handlingField(fields, 3)), null, ""));
                        offerPayload.append(Crypto.Proc_3_0_6D2AF0(Vb.val(handlingField(fields, 4)), null, ""));
                        offerPayload.append(Crypto.Proc_3_0_6D2AF0(0, null, ""));
                        offerCount++;
                    }
                }
            }
            String rowText = MySQL.Proc_5_2_6D4690("SELECT level_hc,hc_days,hc2_days,hc_periods,hc2_periods,hc_presents,"
                + "ROUND((UNIX_TIMESTAMP()-hc_startperiod)/60/60/24,0) FROM users WHERE id='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "' LIMIT 1", 0, 0);
            String[] userFields = rowText.split("\t", -1);
            long hcLevel = Vb.val(handlingField(userFields, 0));
            long hcDays = Vb.val(handlingField(userFields, 1));
            long vipDays = Vb.val(handlingField(userFields, 2));
            long hcPeriods = Vb.val(handlingField(userFields, 3));
            long vipPeriods = Vb.val(handlingField(userFields, 4));
            long presentsAvailable = Vb.val(handlingField(userFields, 5));
            long daysSinceStart = Vb.val(handlingField(userFields, 6));
            long activeDays = hcLevel > 1L ? vipDays : hcDays;
            long periodsLeft = hcLevel > 1L ? vipPeriods : hcPeriods;
            long daysLeft = activeDays - daysSinceStart;
            if (daysLeft < 0L) {
                daysLeft = 0L;
            }
            if (periodsLeft < 1L && daysLeft > 0L) {
                periodsLeft = (daysLeft + 30L) / 31L;
            }
            String payload = Crypto.Proc_3_0_6D2AF0(offerCount, null, "Iq") + offerPayload;
            payload += Crypto.Proc_3_0_6D2AF0(hcLevel, null, "");
            payload += Crypto.Proc_3_0_6D2AF0(daysLeft, null, "");
            payload += Crypto.Proc_3_0_6D2AF0(periodsLeft, null, "");
            payload += Crypto.Proc_3_0_6D2AF0(presentsAvailable, null, "");
            Proc_6_244_801E80(socketIndex, payload, 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static String Proc_6_19_6E8040(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            if (socketIndex <= 0) {
                return "";
            }
            String cachedPayload = args != null && args.length >= 2 ? Vb.cStr(args[1]) : "";
            String packetPrefix = args != null && args.length >= 3 ? Vb.cStr(args[2]) : "";
            if (packetPrefix.isEmpty()) {
                packetPrefix = "Gz";
            }
            if (cachedPayload.isEmpty()) {
                cachedPayload = Licence.recyclerSettings().statusPayload();
            }
            String payload = packetPrefix + cachedPayload;
            Proc_6_244_801E80(socketIndex, payload, 0);
            return payload;
        } catch (Exception ignored) {
            return "";
        }
    }

    public static void Proc_6_20_6E88E0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long rankIndex = handlingUserRank(userId);
            long staffFlag = handlingUserHasPermission(userId, "fuse_client_staff") ? 1L : 0L;
            String payload = Crypto.Proc_3_0_6D2AF0(rankIndex, null, "@B");
            payload += Crypto.Proc_3_0_6D2AF0(rankIndex, null, "");
            payload = "0" + Crypto.Proc_3_0_6D2AF0(staffFlag, null, payload);
            Proc_6_244_801E80(socketIndex, payload, 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static String Proc_6_24_6EA010(Object... args) {
        return handlingRepresentedChatRoute(args, 0L);
    }

    public static String Proc_6_25_6EEAC0(Object... args) {
        return handlingRepresentedChatRoute(args, 0L);
    }

    public static String Proc_6_26_7034C0(Object... args) {
        return handlingRepresentedChatRoute(args, 0L);
    }

    public static String Proc_6_27_706920(Object... args) {
        return handlingRepresentedChatRoute(args, 1L);
    }

    public static String Proc_6_28_709DA0(Object... args) {
        return handlingRepresentedChatRoute(args, 2L);
    }

    public static void Proc_6_53_718E00(Object... args) {
        int socketIndex = args != null && args.length >= 1 ? (int) Vb.val(args[0]) : 0;
        if (socketIndex <= 0) {
            return;
        }
        HandlingMUS.Proc_12_1_821AA0(socketIndex, "@R", 0);
    }

    public static final class LongRef {
        public long value;

        public LongRef(long value) {
            this.value = value;
        }
    }

    public static final class StickyNoteUpdate {
        public long furnitureId;
        public String noteColor = "";
        public String noteCaption = "";
    }

    public static final class WallPlacement {
        public long wallX;
        public long wallY;
        public long localX;
        public long localY;
    }

    public static final class RoomEventPayload {
        public long categoryId;
        public String categoryName = "";
        public String eventName = "";
        public String eventDescription = "";
        public String tagOne = "";
        public String tagTwo = "";
    }

    public static final class RoomSettingsPayload {
        public String roomName = "";
        public String roomPassword = "";
        public long doorStatus;
        public String roomDescription = "";
        public long visitorsMax;
        public long categoryId;
        public String tagOne = "";
        public String tagTwo = "";
        public long allowOthersPets;
        public long allowFeedPets;
        public long allowWalkthrough;
        public long disableWalls;
        public long thicknessFloor;
        public long thicknessWallpaper;
    }

    public static final class InventoryPayloads {
        public long regularCount;
        public long iconCount;
        public String regularPayload = "";
        public String iconPayload = "";
    }

    public static final class FurnitureMoveRequest {
        public long furnitureId;
        public long positionX;
        public long positionY;
        public long rotation;
    }

    public static final class FloorFurniturePlacement {
        public long furnitureId;
        public long positionX;
        public long positionY;
        public long rotation;
    }

    public static final class FurnitureCacheState {
        public String pendingRoomCache = "";
        public String pendingFurnitureCache = "";
        public String representedRoomCache = "";
    }

    public static final class FurnitureStateCache {
        public String pendingRoomCache = "";
        public String pendingFurnitureCache = "";
        public String representedRoomCache = "";
    }

    public static final class FriendTargetList {
        public boolean deleteAllPending;
        public String targetList = "";
        public long targetCount;
    }

    public static final class PetCommandAction {
        public boolean found;
        public long requiredLevel;
        public String action = "";
    }

    public static final class PetExperienceUpdate {
        public long petLevel;
        public long petExperience;
        public boolean leveledUp;
        public String statusPayload = "";
        public String experiencePayload = "";
    }

    public static final class PollAnswerSubmission {
        public long pollId;
        public long questionId;
        public long answerValue;
        public String answerText = "";
        public boolean valid;
    }

    public static final class RecyclerSelection {
        public long requestedCount;
        public String selectedItems = "";
        public boolean valid;
    }

    public static final class AchievementProgressDecision {
        public long achievementIndex = -1L;
        public long nextLevel;
        public long requiredProgress;
        public boolean shouldReward;
    }

    public static final class WiredApplyResult {
        public long appliedCount;
        public String statePayloads = "";
    }

    public static final class SongInfoRequest {
        public long requestedCount;
        public String requestedIds = "";
    }

    public static final class JukeboxAddRequest {
        public long diskFurnitureId;
        public long playlistOrder;
    }

    public static final class QuestProgressDecision {
        public long questId;
        public long numericQuestId;
        public long progressValue;
        public long amountRequired;
        public long waitAmount;
        public long remainingWait;
        public boolean shouldComplete;
        public boolean shouldScheduleWait;
        public boolean shouldSendList;
    }

    public static final class ActivityPointAward {
        public long pointType;
        public long awardAmount;
        public long newPoints;
        public boolean shouldAward;
        public String payload = "";
    }

    public static final class TradeOfferItemPayload {
        public long itemCount;
        public String payload = "";
    }

    public static final class MovementPosition {
        public long positionX;
        public long positionY;
        public boolean found;
    }

    public static final class StaffChatRowsPayload {
        public long chatCount;
        public String payload = "";
    }

    public static String wardrobeSlotPayload(long slotId, String figureText, String genderText) {
        return Crypto.Proc_3_0_6D2AF0(slotId, null, "") + Vb.cStr(figureText) + '\2' + Vb.cStr(genderText) + '\2';
    }

    public static boolean isValidWardrobeFigure(String figureText, String genderText) {
        return isValidWardrobeFigure(figureText, genderText, "");
    }

    public static boolean isValidWardrobeFigure(String figureText, String genderText, String figureData) {
        String figure = Vb.cStr(figureText);
        String gender = Vb.cStr(genderText).toUpperCase();
        if (figure.isEmpty() || figure.length() > 255 || figure.indexOf('\'') >= 0 || figure.indexOf('"') >= 0) {
            return false;
        }

        String allowedTypes = ";lg;ha;wa;hr;ch;sh;cc;ea;he;ca;hd;fa;cp;";
        for (String part : figure.split("\\.", -1)) {
            if (!part.isEmpty()) {
                String[] piece = part.split("-", -1);
                if (piece.length < 2) {
                    return false;
                }
                String figureType = piece[0].toLowerCase();
                String setId = piece[1];
                if (!allowedTypes.contains(";" + figureType + ";") || Vb.val(setId) <= 0L) {
                    return false;
                }

                if (!Vb.cStr(figureData).isEmpty()) {
                    String lowerFigureData = figureData.toLowerCase();
                    String setTypeMarker = "<settype type=\"" + figureType + "\"";
                    int setTypeStart = lowerFigureData.indexOf(setTypeMarker.toLowerCase());
                    if (setTypeStart < 0) {
                        return false;
                    }
                    int setTypeEnd = lowerFigureData.indexOf("</settype>", setTypeStart);
                    if (setTypeEnd < 0) {
                        return false;
                    }
                    String setTypeXml = figureData.substring(setTypeStart, setTypeEnd);
                    String setMarker = "<set id=\"" + Vb.val(setId) + "\"";
                    if (!setTypeXml.toLowerCase().contains(setMarker.toLowerCase())) {
                        return false;
                    }
                    if (!figureSetAllowsGender(setTypeXml, setMarker, gender)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static boolean figureSetAllowsGender(String setTypeXml, String setMarker, String genderText) {
        String setType = Vb.cStr(setTypeXml);
        String marker = Vb.cStr(setMarker);
        int setStart = setType.toLowerCase().indexOf(marker.toLowerCase());
        if (setStart < 0) {
            return false;
        }
        String lowerSetType = setType.toLowerCase();
        int setEnd = lowerSetType.indexOf("</set>", setStart);
        if (setEnd < 0) {
            setEnd = lowerSetType.indexOf("/>", setStart);
        }
        if (setEnd < 0) {
            setEnd = setType.length();
        }
        String setXml = setType.substring(setStart, setEnd);
        int genderStart = setXml.toLowerCase().indexOf("gender=\"");
        if (genderStart < 0) {
            return true;
        }
        if (genderStart + 8 >= setXml.length()) {
            return false;
        }
        String genderValue = setXml.substring(genderStart + 8, genderStart + 9).toUpperCase();
        String gender = Vb.cStr(genderText).toUpperCase();
        return "U".equals(genderValue) || genderValue.equals(gender);
    }

    public static String userIdentityPayload(long userId, String mottoText, String genderText, String figureText) {
        return Crypto.Proc_3_0_6D2AF0(userId, null, "DJ")
            + Vb.cStr(mottoText) + '\2'
            + Vb.cStr(genderText) + '\2'
            + Vb.cStr(figureText) + '\2';
    }

    public static String representedChatPayload(long roomUserIndex, String filteredText, long gestureId, long chatType) {
        String prefix = chatType == 1L ? "@Y" : "@X";
        String payload = Crypto.Proc_3_0_6D2AF0(roomUserIndex, null, prefix) + Vb.cStr(filteredText) + '\2';
        return Crypto.Proc_3_0_6D2AF0(gestureId, null, payload);
    }

    public static String Proc_6_21_6E8BA0(Object... args) {
        if (args == null || args.length == 0) {
            return "";
        }
        return extractUrlList(Vb.cStr(args[0]));
    }

    public static String extractUrlList(String messageText) {
        StringBuilder urlList = new StringBuilder();
        for (String word : Vb.cStr(messageText).split(" ", -1)) {
            String candidate = word.trim();
            String lowered = candidate.toLowerCase();
            if (!candidate.isEmpty()) {
                if (lowered.startsWith("www.") && lowered.indexOf('.', 4) > 0) {
                    urlList.append(candidate).append(';');
                } else if (lowered.startsWith("http://")) {
                    urlList.append(candidate).append(';');
                } else if (lowered.startsWith("https://")) {
                    urlList.append(candidate).append(';');
                }
            }
        }
        return urlList.toString();
    }

    public static String Proc_6_22_6E9300(Object... args) {
        if (args == null || args.length == 0) {
            return "";
        }
        boolean enabled = Vb.val(Functions.Proc_10_0_809570("com.client.chat.filter.enabled", 0)) != 0L;
        String replacement = Functions.Proc_10_0_809570("com.client.chat.filter.replacement", "");
        return Licence.chatSettings().filterText(Vb.cStr(args[0]), enabled, replacement);
    }

    public static String filterChatText(String messageText, boolean filterEnabled, String replacementText, String filterRows) {
        return ChatSettings.fromLegacy(filterRows, "").filterText(messageText, filterEnabled, replacementText);
    }

    public static long Proc_6_23_6E9A90(Object... args) {
        if (args == null || args.length == 0) {
            return 0L;
        }
        boolean enabled = Vb.val(Functions.Proc_10_0_809570("com.client.chat.gesture.enabled", 0)) != 0L;
        return Licence.chatSettings().gestureId(Vb.cStr(args[0]), enabled);
    }

    public static long findGestureId(String messageText, boolean gestureEnabled, String gestureRows) {
        return ChatSettings.fromLegacy("", gestureRows).gestureId(messageText, gestureEnabled);
    }

    public static String Proc_6_29_70D800(Object... args) {
        if (args == null || args.length < 12) {
            return "";
        }
        return StaffPayloads.callForHelp(
            Vb.val(args[0]),
            Vb.val(args[1]),
            Vb.val(args[2]),
            Vb.val(args[3]),
            Vb.cStr(args[4]),
            Vb.val(args[5]),
            Vb.cStr(args[6]),
            Vb.cStr(args[7]),
            Vb.val(args[8]),
            Vb.cStr(args[9]),
            Vb.val(args[10]),
            Vb.cStr(args[11]));
    }

    public static void Proc_6_30_70DC90(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty()) {
                return;
            }
            long callForHelpId = Vb.val(MySQL.Proc_5_2_6D4690("SELECT id FROM staff_cfh WHERE id_user='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0)
                + "' AND id_closed='0' AND timestamp_sent > UNIX_TIMESTAMP()-600 ORDER BY id DESC LIMIT 1", 0, 0));
            if (callForHelpId > 0L) {
                MySQL.Proc_5_0_6D3CD0("DELETE FROM staff_cfh WHERE id='" + callForHelpId + "'", 0, 0);
                Proc_6_244_801E80(socketIndex, "E@", 0);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_31_70DE80(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long rankIndex = handlingUserRank(userId);
            long hcLevel = handlingUserHcLevel(userId);
            if (!Functions.Proc_10_1_809790(rankIndex, "", "fuse_mod", hcLevel)) {
                return;
            }
            String payload = Crypto.Proc_3_0_6D2AF0(0, null, "HS")
                + Crypto.Proc_3_0_6D2AF0(0, null, "")
                + staffModerationPayload(rankIndex, hcLevel);
            Proc_6_244_801E80(socketIndex, payload, 0);

            String rowText = MySQL.Proc_5_2_6D4690("SELECT staff_cfh.id,staff_cfh.id_tab,users.id,users.name,"
                + "staff_cfh.id_partner,staff_cfh.id_room,staff_cfh.id_category,staff_cfh.description,rooms.id,rooms.name,"
                + "staff_cfh.id_picker FROM staff_cfh,users,rooms WHERE staff_cfh.id_closed!='3' "
                + "AND staff_cfh.timestamp_sent > UNIX_TIMESTAMP()-43200 AND users.id=staff_cfh.id_user "
                + "AND users.id_socket IS NOT NULL AND rooms.id=staff_cfh.id_room LIMIT 1000", 0, 0);
            for (String row : rowText.split("\r", -1)) {
                if (!row.isEmpty()) {
                    Proc_6_244_801E80(socketIndex, "HR" + callForHelpRowPayload(row, null), 0);
                }
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_32_70EAB0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String packetPayload = handlingPacketPayload(args);
            String requestPayload = packetPayload.length() >= 3 ? packetPayload.substring(2) : packetPayload;
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long lastClosedState = Vb.val(MySQL.Proc_5_2_6D4690("SELECT id_closed FROM staff_cfh WHERE id_user='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0)
                + "' AND timestamp_sent > UNIX_TIMESTAMP()-600 ORDER BY id DESC LIMIT 1", 0, 0));
            if (lastClosedState == 0L && !MySQL.Proc_5_2_6D4690("SELECT id_closed FROM staff_cfh WHERE id_user='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0)
                + "' AND timestamp_sent > UNIX_TIMESTAMP()-600 ORDER BY id DESC LIMIT 1", 0, 0).isEmpty()) {
                return;
            }
            LongRef offset = new LongRef(1);
            String descriptionText = Functions.Proc_10_10_80A7F0(readWireString(requestPayload, offset), 0, 0);
            if (descriptionText.length() < 30) {
                return;
            }
            long categoryId = readWireLong(requestPayload, offset);
            if (categoryId <= 0L) {
                categoryId = readWireLong(requestPayload, offset);
            }
            long partnerUserId = readWireLong(requestPayload, offset);
            if (partnerUserId == Vb.val(userId)) {
                partnerUserId = 0L;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return;
            }
            MySQL.Proc_5_0_6D3CD0("INSERT INTO staff_cfh(id_user,id_room,id_category,id_partner,description,timestamp_sent) VALUES('"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "','" + roomId + "','" + categoryId + "','"
                + partnerUserId + "','" + Functions.Proc_10_11_80A9C0(descriptionText, 0, 0) + "',UNIX_TIMESTAMP())", 0, 0);
            long callForHelpId = Vb.val(MySQL.Proc_5_2_6D4690("SELECT MAX(id) FROM staff_cfh", 0, 0));
            Proc_6_244_801E80(socketIndex, Crypto.Proc_3_0_6D2AF0(callForHelpId, null, "EA"), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_33_70F4F0(Object... args) {
        try {
            Proc_6_244_801E80(handlingSocketIndex(args), "HF" + Licence.helpCenterCache().importantFaqPayload(), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_34_70F590(Object... args) {
        try {
            Proc_6_244_801E80(handlingSocketIndex(args), "HG" + Licence.helpCenterCache().categoryPayload(), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_35_70F630(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String packetPayload = handlingPacketPayload(args);
            long categoryId = packetPayload.length() >= 3 ? readWireLong(packetPayload.substring(2), new LongRef(1)) : 0L;
            HelpCenterCache helpCenterCache = Licence.helpCenterCache();
            String categoryPayload = helpCenterCache.categoryFaqPayload(categoryId);
            Proc_6_244_801E80(socketIndex,
                Crypto.Proc_3_0_6D2AF0(categoryId, null, "HJ") + '\2' + categoryPayload, 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_36_70F7B0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String packetPayload = handlingPacketPayload(args);
            String searchText = packetPayload.length() >= 3
                ? Functions.Proc_10_11_80A9C0(Functions.Proc_10_7_80A190(packetPayload.substring(2), 0, 0), 0, 0)
                : "";
            if (searchText.length() < 3) {
                Proc_6_243_7FFEB0(socketIndex, 0, 0);
                return;
            }
            String rows = MySQL.Proc_5_2_6D4690("SELECT id,name FROM faq WHERE name LIKE '%" + searchText + "%' LIMIT 25", 0, 0);
            long resultCount = 0L;
            String resultPayload = "";
            for (String row : rows.split("\r", -1)) {
                if (!row.isEmpty()) {
                    String[] fields = row.split("\t", -1);
                    if (fields.length >= 2) {
                        resultPayload = Crypto.Proc_3_0_6D2AF0(Vb.val(handlingField(fields, 0)), null, resultPayload)
                            + handlingField(fields, 1) + '\2';
                        resultCount++;
                    }
                }
            }
            Proc_6_244_801E80(socketIndex, Crypto.Proc_3_0_6D2AF0(resultCount, null, "HI") + resultPayload, 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_37_70FC20(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String packetPayload = handlingPacketPayload(args);
            long faqId = 0L;
            if (packetPayload.length() >= 3) {
                String requestPayload = packetPayload.substring(2);
                faqId = Vb.val(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
                if (faqId <= 0L) {
                    faqId = readWireLong(requestPayload, new LongRef(1));
                }
            }
            Proc_6_244_801E80(socketIndex, "HH" + Licence.helpCenterCache().descriptionPayload(faqId), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static long Proc_6_38_70FD10(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "GV");
            LongRef offset = new LongRef(1);
            String candidateName = Functions.Proc_10_10_80A7F0(readWireString(requestPayload, offset), 0, 0);
            if (candidateName.isEmpty()) {
                candidateName = Functions.Proc_10_10_80A7F0(Functions.Proc_10_7_80A190(requestPayload, 0, 0), 0, 0);
            }
            return Proc_6_40_711770(socketIndex, 0, candidateName);
        } catch (Exception ignored) {
            return 0L;
        }
    }

    public static long Proc_6_39_711650(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "GW");
            LongRef offset = new LongRef(1);
            String candidateName = Functions.Proc_10_10_80A7F0(readWireString(requestPayload, offset), 0, 0);
            if (candidateName.isEmpty()) {
                candidateName = Functions.Proc_10_10_80A7F0(Functions.Proc_10_7_80A190(requestPayload, 0, 0), 0, 0);
            }
            return Proc_6_40_711770(socketIndex, -1, candidateName);
        } catch (Exception ignored) {
            return 0L;
        }
    }

    public static long Proc_6_40_711770(Object... args) {
        try {
            if (args == null || args.length < 3) {
                return 0L;
            }
            int socketIndex = handlingSocketIndex(args);
            boolean checkOnly = Vb.val(args[1]) < 0L;
            String candidateName = Vb.cStr(args[2]).trim();
            if (socketIndex <= 0) {
                return 0L;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            String oldName = MySQL.Proc_5_2_6D4690("SELECT name FROM users WHERE id='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "' LIMIT 1", 0, 0);
            MySQL.Proc_5_2_6D4690("SELECT gender FROM users WHERE id='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "' LIMIT 1", 0, 0);
            long existingCount = Vb.val(MySQL.Proc_5_2_6D4690("SELECT COUNT(*) FROM users WHERE name='"
                + Functions.Proc_10_11_80A9C0(candidateName, 0, 0) + "'", 0, 0));
            long validationCode = avatarNameValidationCode(candidateName, oldName, existingCount);
            Proc_6_244_801E80(socketIndex, Crypto.Proc_3_0_6D2AF0(validationCode, null, "H{") + candidateName + '\2', 0);
            if (checkOnly || validationCode != 0L) {
                return validationCode;
            }
            MySQL.Proc_5_0_6D3CD0("UPDATE users SET name='" + Functions.Proc_10_11_80A9C0(candidateName, 0, 0)
                + "',tutorial_name='1',merge_name='0' WHERE id='" + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "'", 0, 0);
            MySQL.Proc_5_1_6D4110("INSERT INTO logs_identity(previous_identity,new_identity,timestamp,id_session) VALUES('"
                + Functions.Proc_10_11_80A9C0(oldName, 0, 0) + "','" + Functions.Proc_10_11_80A9C0(candidateName, 0, 0)
                + "',UNIX_TIMESTAMP(),'" + socketIndex + "')", 0, 0);
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId > 0L) {
                long roomUserIndex = representedRoomUserIndex(socketIndex, userId);
                Proc_6_247_8027E0(socketIndex,
                    Crypto.Proc_3_0_6D2AF0(roomUserIndex, null, Crypto.Proc_3_0_6D2AF0(Vb.val(userId), null, "H|"))
                        + candidateName + '\2', 0);
                String queryTail = "users,rooms,rooms_categories WHERE rooms.id='" + roomId
                    + "' AND users.id=rooms.id_owner AND rooms_categories.id=rooms.id_category LIMIT 1";
                Proc_6_247_8027E0(socketIndex, Proc_6_112_74E0C0(queryTail, "GF", 0), 0);
                Proc_6_247_8027E0(socketIndex, Crypto.Proc_3_0_6D2AF0(roomId, null, "GH"), 0);
            }
            return 0L;
        } catch (Exception ignored) {
            return 0L;
        }
    }

    public static String Proc_6_41_712730(Object... args) {
        if (args == null || args.length == 0) {
            return "";
        }
        String[] values = normalizeUserEntryArgs(args);
        long userId = Vb.val(values[0]);
        String userName = values[1];
        String figureText = values[2];
        String mottoText = values[3];
        String genderText = values[4];
        long roomUserIndex = Vb.val(values[5]);
        long xValue = Vb.val(values[6]);
        long yValue = Vb.val(values[7]);
        String zValue = values[8];
        long firstState = Vb.val(values[9]);
        long secondState = Vb.val(values[10]);
        if (roomUserIndex <= 0L) {
            roomUserIndex = userId;
        }

        String payload = Crypto.Proc_3_0_6D2AF0(userId, null, "") + userName + '\2' + figureText;
        payload = Crypto.Proc_3_0_6D2AF0(roomUserIndex, null, payload + '\2' + mottoText + '\2');
        payload = Crypto.Proc_3_0_6D2AF0(xValue, null, "0" + payload);
        payload = Crypto.Proc_3_0_6D2AF0(yValue, null, "0" + payload) + zValue + '\2' + "JI";
        payload = payload + genderText + '\2' + "M";
        payload = Crypto.Proc_3_0_6D2AF0(firstState, null, payload);
        return Crypto.Proc_3_0_6D2AF0(secondState, null, payload + "M" + '\2');
    }

    public static String Proc_6_42_712FB0(Object... args) {
        if (args == null || args.length == 0) {
            return "";
        }
        String[] values = normalizeObjectEntryArgs(args);
        long entityId = Vb.val(values[0]);
        String displayName = values[1];
        String figureText = values[2];
        String genderText = values[3];
        long roomUserIndex = Vb.val(values[4]);
        long xValue = Vb.val(values[5]);
        long yValue = Vb.val(values[6]);
        String zValue = values[7];
        long objectType = Vb.val(values[8]);
        if (roomUserIndex <= 0L) {
            roomUserIndex = entityId;
        }

        String payload;
        String tailMarker;
        if (objectType == 3L) {
            payload = Crypto.Proc_3_0_6D2AF0(entityId, null, "");
            tailMarker = "PAJJ";
        } else {
            payload = "M";
            tailMarker = "HK";
        }

        payload = payload + displayName + '\2';
        payload = payload + figureText + '\2';
        payload = payload + genderText + '\2';
        payload = payload + Crypto.Proc_3_0_6D2AF0(roomUserIndex, null, "");
        payload = payload + Crypto.Proc_3_0_6D2AF0(xValue, null, "");
        payload = payload + Crypto.Proc_3_0_6D2AF0(yValue, null, "");
        payload = payload + zValue + '\2';
        return payload + tailMarker;
    }

    public static void Proc_6_43_713680(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "FF");
            long requestedRoomId = Vb.val(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
            if (requestedRoomId <= 0L) {
                requestedRoomId = readWireLong(requestPayload, new LongRef(1));
            }
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long roomId = requestedRoomId > 0L ? requestedRoomId : handlingCurrentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L) {
                return;
            }
            if (!handlingUserOwnsRoom(callerUserId, roomId) && !handlingUserHasPermission(callerUserId, "fuse_any_room_controller")) {
                return;
            }
            String roomRow = MySQL.Proc_5_2_6D4690("SELECT rooms.id,rooms.name,rooms.description,rooms.status_door,"
                + "rooms.id_category,rooms.visitors_max,models.visitors_max,rooms.tag_1,rooms.tag_2,NULL,"
                + "rooms.allow_otherspets,rooms.allow_feedpets,rooms.allow_walkthrough,rooms.disable_walls "
                + "FROM rooms,models WHERE rooms.id='" + roomId + "' AND models.id=rooms.id_model LIMIT 1", 0, 0);
            if (roomRow.isEmpty()) {
                return;
            }
            String[] roomFields = roomRow.split("\t", -1);
            if (roomFields.length < 14) {
                return;
            }
            String rightsRow = MySQL.Proc_5_2_6D4690("SELECT users.id,users.name FROM rooms_rights,users WHERE rooms_rights.id_room='"
                + roomId + "' AND users.id=rooms_rights.id_user LIMIT 250", 0, 0);
            String payload = roomSettingsReadPayload(roomFields, rightsRow);
            if (!payload.isEmpty()) {
                Proc_6_244_801E80(socketIndex, payload, 0);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_44_7145E0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String packetPayload = handlingRequestPayload(args, "FB");
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return;
            }
            String iconPayload = roomIconPayloadFromWire(packetPayload);
            if (iconPayload.isEmpty()) {
                return;
            }
            MySQL.Proc_5_0_6D3CD0("UPDATE rooms SET icon='" + Functions.Proc_10_11_80A9C0(iconPayload, 0, 0)
                + "' WHERE id='" + roomId + "'", 0, 0);
            String queryTail = "users,rooms,rooms_categories WHERE rooms.id='" + roomId
                + "' AND users.id=rooms.id_owner AND rooms_categories.id=rooms.id_category LIMIT 1";
            Proc_6_247_8027E0(socketIndex, Proc_6_112_74E0C0(queryTail, "GF", 0), 0);
            Proc_6_244_801E80(socketIndex, Crypto.Proc_3_0_6D2AF0(roomId, null, "GI") + '\2', 0);
            Proc_6_244_801E80(socketIndex, Crypto.Proc_3_0_6D2AF0(roomId, null, "GH"), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_45_714B60(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return;
            }
            MySQL.Proc_5_0_6D3CD0("DELETE FROM rooms_events WHERE id_room='" + roomId + "'", 0, 0);
            Proc_6_244_801E80(socketIndex, "Er-1" + '\2', 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_46_714D50(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return;
            }
            long doorStatus = Vb.val(MySQL.Proc_5_2_6D4690("SELECT status_door FROM rooms WHERE id='" + roomId + "' LIMIT 1", 0, 0));
            Proc_6_244_801E80(socketIndex, doorStatus != 0L ? "EoHK" : "EoIH", 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_47_714F60(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String packetPayload = handlingPacketPayload(args);
            if (packetPayload.length() > 2) {
                packetPayload = packetPayload.substring(2);
            }
            long roomId = readWireLong(packetPayload, new LongRef(1));
            if (roomId <= 0L) {
                return;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            MySQL.Proc_5_0_6D3CD0("UPDATE users SET homeroom='" + roomId + "' WHERE id='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "'", 0, 0);
            Proc_6_244_801E80(socketIndex, Crypto.Proc_3_0_6D2AF0(roomId, null, "GG"), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_48_7151E0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String packetPayload = handlingRequestPayload(args, "EZ");
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return;
            }
            long doorStatus = Vb.val(MySQL.Proc_5_2_6D4690("SELECT status_door FROM rooms WHERE id='" + roomId + "' LIMIT 1", 0, 0));
            if (doorStatus != 0L) {
                Proc_6_244_801E80(socketIndex, "EoHK", 0);
                return;
            }
            RoomEventPayload event = new RoomEventPayload();
            if (!roomEventCreatePayloadFromWire(packetPayload, event)) {
                return;
            }
            MySQL.Proc_5_0_6D3CD0("INSERT INTO rooms_events(id_room,id_user,name,description,id_category,tag_1,tag_2,timestamp,name_category) VALUES('"
                + roomId + "','" + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "','"
                + Functions.Proc_10_11_80A9C0(event.eventName, 0, 0) + "','"
                + Functions.Proc_10_11_80A9C0(event.eventDescription, 0, 0) + "','" + event.categoryId + "',"
                + nullableSqlText(event.tagOne) + "," + nullableSqlText(event.tagTwo)
                + ",UNIX_TIMESTAMP(),'" + Functions.Proc_10_11_80A9C0(event.categoryName, 0, 0) + "')", 0, 0);
            Proc_6_247_8027E0(socketIndex, "Er" + Proc_6_51_716AC0(roomId), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_49_715D30(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String packetPayload = handlingRequestPayload(args, "E\\");
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return;
            }
            RoomEventPayload event = new RoomEventPayload();
            if (!roomEventEditPayloadFromWire(packetPayload, event)) {
                return;
            }
            MySQL.Proc_5_0_6D3CD0("UPDATE rooms_events SET id_user='" + Functions.Proc_10_11_80A9C0(userId, 0, 0)
                + "',name='" + Functions.Proc_10_11_80A9C0(event.eventName, 0, 0)
                + "',description='" + Functions.Proc_10_11_80A9C0(event.eventDescription, 0, 0)
                + "',tag_1=" + nullableSqlText(event.tagOne)
                + ",tag_2=" + nullableSqlText(event.tagTwo)
                + " WHERE id_room='" + roomId + "'", 0, 0);
            Proc_6_247_8027E0(socketIndex, "Er" + Proc_6_51_716AC0(roomId), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_50_7166B0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "Ab");
            String targetName = Functions.Proc_10_7_80A190(requestPayload, 0, 0).trim();
            if (targetName.isEmpty() || !requestPayload.startsWith("@")) {
                targetName = requestPayload.trim();
            }
            if (targetName.isEmpty()) {
                Proc_6_244_801E80(socketIndex, "BC", 0);
                return;
            }
            String targetRow = MySQL.Proc_5_2_6D4690("SELECT users.id,users.id_socket,logs_visitedrooms.id_room "
                + "FROM users,logs_visitedrooms WHERE users.name='" + Functions.Proc_10_11_80A9C0(targetName, 0, 0)
                + "' AND users.id=logs_visitedrooms.id_user AND logs_visitedrooms.timestamp_left IS NULL LIMIT 1", 0, 0);
            String[] targetFields = targetRow.split("\t", -1);
            long targetUserId = Vb.val(handlingField(targetFields, 0));
            long targetSocketIndex = Vb.val(handlingField(targetFields, 1));
            long targetRoomId = Vb.val(handlingField(targetFields, 2));
            if (targetUserId <= 0L || targetSocketIndex <= 0L || targetRoomId <= 0L) {
                Proc_6_244_801E80(socketIndex, "BC", 0);
                return;
            }
            Proc_6_57_71E8F0(socketIndex, targetRoomId, "");
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_52_7172B0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String packetPayload = handlingRequestPayload(args, "FQ");
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return;
            }
            if (!handlingUserOwnsRoom(userId, roomId) && !handlingUserHasPermission(userId, "fuse_any_room_controller")) {
                return;
            }
            RoomSettingsPayload settings = new RoomSettingsPayload();
            if (!roomSettingsFromWire(packetPayload, settings)) {
                return;
            }
            settings.categoryId = roomCategoryForUser(settings.categoryId, userId);
            if (settings.categoryId <= 0L) {
                return;
            }
            if (settings.disableWalls != 0L && !handlingUserHasPermission(userId, "fuse_hide_room_walls")) {
                settings.disableWalls = 0L;
            }
            MySQL.Proc_5_0_6D3CD0("UPDATE rooms SET thickness_floor='" + settings.thicknessFloor
                + "',thickness_wallpaper='" + settings.thicknessWallpaper
                + "',name='" + Functions.Proc_10_11_80A9C0(settings.roomName, 0, 0)
                + "',password='" + Functions.Proc_10_11_80A9C0(settings.roomPassword, 0, 0)
                + "',description='" + Functions.Proc_10_11_80A9C0(settings.roomDescription, 0, 0)
                + "',status_door='" + settings.doorStatus
                + "',id_category='" + settings.categoryId
                + "',tag_1=" + nullableSqlText(settings.tagOne)
                + ",tag_2=" + nullableSqlText(settings.tagTwo)
                + ",allow_otherspets='" + settings.allowOthersPets
                + "',allow_feedpets='" + settings.allowFeedPets
                + "',allow_walkthrough='" + settings.allowWalkthrough
                + "',visitors_max='" + settings.visitorsMax
                + "',disable_walls='" + settings.disableWalls
                + "' WHERE id='" + roomId + "'", 0, 0);
            String queryTail = "users,rooms,rooms_categories WHERE rooms.id='" + roomId
                + "' AND users.id=rooms.id_owner AND rooms_categories.id=rooms.id_category LIMIT 1";
            Proc_6_247_8027E0(socketIndex, Proc_6_112_74E0C0(queryTail, "GF", 0), 0);
            Proc_6_244_801E80(socketIndex, Crypto.Proc_3_0_6D2AF0(roomId, null, "GS"), 0);
            Proc_6_244_801E80(socketIndex, Crypto.Proc_3_0_6D2AF0(roomId, null, "GH"), 0);
            String optionPayload = Crypto.Proc_3_0_6D2AF0(settings.disableWalls, null, "GX");
            optionPayload = Crypto.Proc_3_0_6D2AF0(settings.thicknessFloor, null, optionPayload);
            optionPayload = Crypto.Proc_3_0_6D2AF0(settings.thicknessWallpaper, null, optionPayload);
            Proc_6_244_801E80(socketIndex, optionPayload, 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static String Proc_6_51_716AC0(Object... args) {
        try {
            if (args == null || args.length == 0) {
                return "-1" + '\2';
            }
            long roomId = Vb.val(args[0]);
            if (roomId <= 0L) {
                return "-1" + '\2';
            }
            String timeFormat = Functions.Proc_10_0_809570("com.mysql.format.time", "%H:%i", 0);
            String rowText = MySQL.Proc_5_2_6D4690("SELECT users.id,users.name,rooms_events.id_room,rooms_events.id_category,"
                + "rooms_events.name,rooms_events.description,DATE_FORMAT(FROM_UNIXTIME(rooms_events.timestamp), '"
                + timeFormat + "'),rooms_events.tag_1,rooms_events.tag_2 FROM rooms_events,users WHERE rooms_events.id_room='"
                + roomId + "' AND users.id=rooms_events.id_user LIMIT 1", 0, 0);
            if (rowText.isEmpty()) {
                return "-1" + '\2';
            }
            String[] fields = rowText.split("\t", -1);
            if (fields.length < 9) {
                return "-1" + '\2';
            }
            StringBuilder payload = new StringBuilder();
            for (int fieldIndex = 4; fieldIndex <= 8; fieldIndex++) {
                payload.append(handlingField(fields, fieldIndex)).append('\2');
            }
            String result = Crypto.Proc_3_0_6D2AF0(Vb.val(handlingField(fields, 0)), null, payload.toString());
            result = Crypto.Proc_3_0_6D2AF0(Vb.val(handlingField(fields, 2)), null, result);
            return Crypto.Proc_3_0_6D2AF0(Vb.val(handlingField(fields, 3)), null, result);
        } catch (Exception ignored) {
            return "-1" + '\2';
        }
    }

    public static long Proc_6_54_719050(Object... args) {
        long reservedSlot = 0L;
        try {
            int socketIndex = handlingSocketIndex(args);
            long roomId = args != null && args.length >= 2 ? Vb.val(args[1]) : 0L;
            long preferredSlot = args != null && args.length >= 3 ? Vb.val(args[2]) : 0L;
            if (socketIndex <= 0 || roomId <= 0L) {
                return 0L;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            if (handlingCurrentRoomId(socketIndex, userId) > 0L) {
                Proc_6_55_71A6E0(socketIndex, 0, 0);
            }
            reservedSlot = reserveRepresentedRoomSlot(preferredSlot);
            if (reservedSlot <= 0L) {
                Proc_6_244_801E80(socketIndex, Functions.Proc_10_8_80A580(1, 0, 0), 0);
                return 0L;
            }
            loadRepresentedRoomBots(reservedSlot, roomId);
            String sessionId = MySQL.Proc_5_2_6D4690("SELECT login_session FROM users WHERE id='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "' LIMIT 1", 0, 0);
            MySQL.Proc_5_1_6D4110("INSERT INTO logs_visitedrooms(id_user,id_room,timestamp_enter,id_session) VALUES('"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "','" + roomId + "',UNIX_TIMESTAMP(),'"
                + Functions.Proc_10_11_80A9C0(sessionId, 0, 0) + "')", 0, 0);
            MySQL.Proc_5_0_6D3CD0("UPDATE rooms SET id_slot='" + reservedSlot
                + "',visitors_now=visitors_now+1 WHERE id='" + roomId + "'", 0, 0);
            Proc_6_56_71E730(socketIndex, 0, 0);
            Proc_6_53_718E00(socketIndex, 0, 0);
            return reservedSlot;
        } catch (Exception ignored) {
            if (reservedSlot > 0L) {
                releaseRepresentedRoomSlot(reservedSlot);
            }
            return 0L;
        }
    }

    public static long Proc_6_55_71A6E0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            if (socketIndex <= 0) {
                return 0L;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            String roomRow = MySQL.Proc_5_2_6D4690("SELECT logs_visitedrooms.id,logs_visitedrooms.id_room,rooms.id_slot "
                + "FROM logs_visitedrooms,rooms WHERE logs_visitedrooms.id_user='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0)
                + "' AND logs_visitedrooms.timestamp_left IS NULL AND rooms.id=logs_visitedrooms.id_room "
                + "ORDER BY logs_visitedrooms.timestamp_enter DESC LIMIT 1", 0, 0);
            if (roomRow.isEmpty()) {
                Proc_6_244_801E80(socketIndex, "J|H", 0);
                return 0L;
            }
            String[] fields = roomRow.split("\t", -1);
            long visitId = Vb.val(handlingField(fields, 0));
            long roomId = Vb.val(handlingField(fields, 1));
            long slotId = Vb.val(handlingField(fields, 2));
            if (roomId <= 0L) {
                return 0L;
            }
            long roomUserIndex = representedRoomUserIndex(socketIndex, userId);
            if (roomUserIndex > 0L) {
                Proc_6_247_8027E0(socketIndex, Crypto.Proc_3_0_6D2AF0(roomUserIndex, null, "@\\"), 0);
            }
            if (visitId > 0L) {
                MySQL.Proc_5_0_6D3CD0("UPDATE logs_visitedrooms SET timestamp_left=UNIX_TIMESTAMP() WHERE id='"
                    + visitId + "' AND timestamp_left IS NULL", 0, 0);
            } else {
                MySQL.Proc_5_0_6D3CD0("UPDATE logs_visitedrooms SET timestamp_left=UNIX_TIMESTAMP() WHERE id_user='"
                    + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "' AND id_room='" + roomId
                    + "' AND timestamp_left IS NULL", 0, 0);
            }
            MySQL.Proc_5_0_6D3CD0("UPDATE rooms SET visitors_now=IF(visitors_now>0,visitors_now-1,0) WHERE id='" + roomId + "'", 0, 0);
            if (slotId > 0L) {
                releaseRepresentedRoomSlot(slotId);
                MySQL.Proc_5_0_6D3CD0("UPDATE rooms SET id_slot=null WHERE id='" + roomId + "' AND id_slot='" + slotId + "'", 0, 0);
            }
            Proc_6_244_801E80(socketIndex, "J|H", 0);
            return roomId;
        } catch (Exception ignored) {
            return 0L;
        }
    }

    public static void Proc_6_56_71E730(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            long roomMode = args != null && args.length >= 2 ? Vb.val(args[1]) : 0L;
            Proc_6_244_801E80(socketIndex, "@S", 0);
            Proc_6_244_801E80(socketIndex, "Bf/client.php" + '\2', 0);
            Proc_6_244_801E80(socketIndex, roomMode == 0L ? "@i" : "@{", 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static long Proc_6_57_71E8F0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            long roomId = args != null && args.length >= 2 ? Vb.val(args[1]) : 0L;
            String suppliedPassword = args != null && args.length >= 3 ? Vb.cStr(args[2]) : "";
            if (roomId <= 0L) {
                Proc_6_244_801E80(socketIndex, "C`H", 0);
                return 0L;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            String roomRow = MySQL.Proc_5_2_6D4690("SELECT visitors_now,visitors_max,status_door,password,id_slot,id_owner "
                + "FROM rooms WHERE rooms.id='" + roomId + "' LIMIT 1", 0, 0);
            if (roomRow.isEmpty()) {
                Proc_6_244_801E80(socketIndex, "C`H", 0);
                return 0L;
            }
            String[] fields = roomRow.split("\t", -1);
            long visitorsNow = Vb.val(handlingField(fields, 0));
            long visitorsMax = Vb.val(handlingField(fields, 1));
            long doorStatus = Vb.val(handlingField(fields, 2));
            String roomPassword = handlingField(fields, 3);
            long roomSlot = Vb.val(handlingField(fields, 4));
            String ownerUserId = String.valueOf(Vb.val(handlingField(fields, 5)));
            boolean isOwner = ownerUserId.equals(String.valueOf(Vb.val(userId)));
            if (!isOwner) {
                boolean isBanned = Vb.val(MySQL.Proc_5_2_6D4690("SELECT id_user FROM rooms_bans WHERE id_user='"
                    + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "' AND id_room='" + roomId + "' LIMIT 1", 0, 0)) > 0L;
                if (isBanned) {
                    Proc_6_53_718E00(socketIndex, 0, 0);
                    Proc_6_244_801E80(socketIndex, "C`PA", 0);
                    return 0L;
                }
                if (visitorsMax > 0L && visitorsNow >= visitorsMax && !handlingUserHasPermission(userId, "fuse_enter_full_rooms")) {
                    Proc_6_53_718E00(socketIndex, 0, 0);
                    Proc_6_244_801E80(socketIndex, "C`I", 0);
                    return 0L;
                }
                if (doorStatus == 1L && !handlingUserHasPermission(userId, "fuse_enter_locked_rooms")) {
                    Proc_6_53_718E00(socketIndex, 0, 0);
                    Proc_6_244_801E80(socketIndex, "C`H", 0);
                    return 0L;
                }
                if (doorStatus == 2L && !roomPassword.equals(suppliedPassword)) {
                    Proc_6_53_718E00(socketIndex, 0, 0);
                    Proc_6_244_801E80(socketIndex, "@afhFF", 0);
                    return 0L;
                }
            }
            return Proc_6_54_719050(socketIndex, roomId, roomSlot);
        } catch (Exception ignored) {
            return 0L;
        }
    }

    public static long Proc_6_58_71FCA0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String packetPayload = handlingRequestPayload(args, "FG");
            ensureRepresentedRoomSlotPool();
            if (Licence.global_0082930C.isEmpty()) {
                Proc_6_244_801E80(socketIndex, Functions.Proc_10_8_80A580(1, 0, 0), 0);
                return 0L;
            }
            String roomIdText = Functions.Proc_10_7_80A190(packetPayload, 0, 0);
            if (roomIdText.isEmpty()) {
                roomIdText = packetPayload;
            }
            long roomId = Vb.val(roomIdText);
            int passwordStart = 2 + roomIdText.length();
            String roomPassword = "";
            if (passwordStart < packetPayload.length()) {
                roomPassword = Functions.Proc_10_6_809F10(packetPayload.substring(passwordStart), 0, 0);
            }
            Proc_6_57_71E8F0(socketIndex, roomId, roomPassword);
            return roomId;
        } catch (Exception ignored) {
            return 0L;
        }
    }

    public static void Proc_6_59_71FEE0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String advertisementPayload = "\2\2";
            if (Licence.visitRoomAds().count() > 0L) {
                String candidate = Licence.visitRoomAds().randomPayload();
                if (!candidate.isEmpty()) {
                    advertisementPayload = candidate;
                }
            }
            Proc_6_244_801E80(socketIndex, "DB" + advertisementPayload, 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_60_720060(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "FA");
            LongRef offset = new LongRef(1);
            long requestMode = readWireLong(requestPayload, offset);
            long detailFlag = readWireLong(requestPayload, offset);
            if (detailFlag == 1L) {
                long roomId = readWireLong(requestPayload, offset);
                if (roomId <= 0L) {
                    return;
                }
                String queryTail = "users,rooms,rooms_categories WHERE rooms.id='" + roomId
                    + "' AND users.id=rooms.id_owner AND rooms_categories.id=rooms.id_category LIMIT 1";
                String rowText = MySQL.Proc_5_2_6D4690("SELECT rooms.id,rooms.name,users.name,rooms.status_door,"
                    + "rooms.visitors_now,rooms.visitors_max,rooms.description,rooms_categories.has_trading,NULL,"
                    + "rooms.rate,rooms.id_category,rooms.icon,rooms.tag_1,rooms.tag_2,rooms.allow_otherspets,"
                    + "rooms.is_staff_picked FROM " + queryTail, 0, 0);
                Proc_6_244_801E80(socketIndex,
                    Crypto.Proc_3_0_6D2AF0(0, null, "GF") + singleNavigatorRoomPayloadFromRows(rowText), 0);
            } else if (requestMode > 0L) {
                // VB6 reads a room id from packed session offsets here; those offsets are not represented yet.
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_61_720490(Object... args) {
        roomKickOrBanUser(args, false);
    }

    public static void Proc_6_62_7209F0(Object... args) {
        roomKickOrBanUser(args, true);
    }

    public static void Proc_6_63_721050(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "DE");
            LongRef offset = new LongRef(1);
            long voteValue = readWireLong(requestPayload, offset);
            if (voteValue != 1L) {
                return;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return;
            }
            String escapedUserId = Functions.Proc_10_11_80A9C0(userId, 0, 0);
            String existingVoteUserId = MySQL.Proc_5_2_6D4690("SELECT id_user FROM rooms_rates WHERE id_user='"
                + escapedUserId + "' AND id_room='" + roomId + "' LIMIT 1", 0, 0);
            if (!existingVoteUserId.isEmpty()) {
                return;
            }
            MySQL.Proc_5_0_6D3CD0("INSERT INTO rooms_rates(id_user,id_room,timestamp) VALUES('"
                + escapedUserId + "','" + roomId + "',UNIX_TIMESTAMP())", 0, 0);
            long roomRate = Vb.val(MySQL.Proc_5_2_6D4690("SELECT rate FROM rooms WHERE id='" + roomId + "' LIMIT 1", 0, 0));
            if (roomRate < 0L) {
                roomRate = 0L;
            }
            roomRate++;
            MySQL.Proc_5_0_6D3CD0("UPDATE rooms SET rate='" + roomRate + "' WHERE id='" + roomId + "'", 0, 0);
            Proc_6_244_801E80(socketIndex, Crypto.Proc_3_0_6D2AF0(roomRate, null, "EY"), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_64_721650(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "D\u007f");
            LongRef offset = new LongRef(1);
            String targetName = Functions.Proc_10_11_80A9C0(readWireString(requestPayload, offset), 0, 0);
            if (targetName.isEmpty()) {
                return;
            }
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L || !handlingUserHasRoomRight(callerUserId, roomId)) {
                return;
            }
            String targetUserId = String.valueOf(Vb.val(MySQL.Proc_5_2_6D4690("SELECT id FROM users WHERE name='"
                + targetName + "' LIMIT 1", 0, 0)));
            if (targetUserId.isEmpty() || "0".equals(targetUserId)) {
                return;
            }
            MySQL.Proc_5_0_6D3CD0("DELETE FROM rooms_rights WHERE id_user='"
                + Functions.Proc_10_11_80A9C0(targetUserId, 0, 0) + "' AND id_room='" + roomId + "'", 0, 0);
            Proc_6_244_801E80(socketIndex, Crypto.Proc_3_0_6D2AF0(0, null, "Fc"), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_65_721A10(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "A`");
            LongRef offset = new LongRef(1);
            String targetUserId = String.valueOf(readWireLong(requestPayload, offset));
            if (targetUserId.isEmpty() || "0".equals(targetUserId)) {
                return;
            }
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L || !handlingUserHasRoomRight(callerUserId, roomId)) {
                return;
            }
            int targetSocketIndex = handlingSocketFromUserId(targetUserId);
            if (targetSocketIndex <= 0) {
                return;
            }
            MySQL.Proc_5_0_6D3CD0("INSERT IGNORE INTO rooms_rights(id_user,id_room) VALUES('"
                + Functions.Proc_10_11_80A9C0(targetUserId, 0, 0) + "','" + roomId + "')", 0, 0);
            Proc_6_244_801E80(targetSocketIndex, "@j", 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_66_721D60(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "AT");
            StickyNoteUpdate note = new StickyNoteUpdate();
            if (!stickyNoteUpdateFromWire(requestPayload, note) || note.furnitureId <= 0L) {
                return;
            }
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L || !handlingUserHasRoomRight(callerUserId, roomId)) {
                return;
            }
            String rowText = MySQL.Proc_5_2_6D4690("SELECT id,id_product,sign,caption,position_wall FROM furnitures WHERE id='"
                + note.furnitureId + "' AND id_room='" + roomId + "' LIMIT 1", 0, 0);
            String[] fields = rowText.split("\t", -1);
            if (rowText.isEmpty() || fields.length < 2) {
                return;
            }
            long productId = Vb.val(handlingField(fields, 1));
            if (!isPostItProduct(productId)) {
                return;
            }
            MySQL.Proc_5_0_6D3CD0("UPDATE furnitures SET sign='"
                + Functions.Proc_10_11_80A9C0(note.noteColor, 0, 0) + "',caption='"
                + Functions.Proc_10_11_80A9C0(note.noteCaption, 0, 0) + "' WHERE id='" + note.furnitureId + "'", 0, 0);
            String broadcastPayload = "AT" + note.furnitureId + '\1' + "AS" + note.furnitureId + '\2';
            broadcastPayload = Crypto.Proc_3_0_6D2AF0(productId, null, broadcastPayload)
                + productId + '\2' + note.noteColor + '\2';
            Proc_6_247_8027E0(socketIndex, broadcastPayload, 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_67_722940(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "AS");
            long furnitureId = stickyFurnitureIdFromPayload(requestPayload);
            if (furnitureId <= 0L) {
                return;
            }
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L) {
                return;
            }
            String rowText = MySQL.Proc_5_2_6D4690("SELECT id,id_product,sign,caption FROM furnitures WHERE id='"
                + furnitureId + "' AND id_room='" + roomId + "' LIMIT 1", 0, 0);
            String[] fields = rowText.split("\t", -1);
            if (rowText.isEmpty() || fields.length < 4) {
                return;
            }
            long productId = Vb.val(handlingField(fields, 1));
            if (!isPostItProduct(productId)) {
                return;
            }
            String noteColor = Vb.left(handlingField(fields, 2), 6);
            if (noteColor.isEmpty()) {
                noteColor = "FFFF33";
            }
            String noteCaption = handlingField(fields, 3).replace('\u001f', '\r');
            Proc_6_244_801E80(socketIndex, "@p" + furnitureId + '\2' + noteColor + '\r' + noteCaption + '\2', 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_68_723170(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "AU");
            long furnitureId = stickyFurnitureIdFromPayload(requestPayload);
            if (furnitureId <= 0L) {
                return;
            }
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L || !handlingUserOwnsRoom(callerUserId, roomId)) {
                return;
            }
            String rowText = MySQL.Proc_5_2_6D4690("SELECT id,id_product,sign,caption FROM furnitures WHERE id='"
                + furnitureId + "' AND id_room='" + roomId + "' LIMIT 1", 0, 0);
            String[] fields = rowText.split("\t", -1);
            if (rowText.isEmpty() || fields.length < 2) {
                return;
            }
            long productId = Vb.val(handlingField(fields, 1));
            if (!isPostItProduct(productId)) {
                return;
            }
            MySQL.Proc_5_0_6D3CD0("DELETE FROM furnitures WHERE id='" + furnitureId + "' LIMIT 1", 0, 0);
            Proc_6_247_8027E0(socketIndex, "AT" + furnitureId, 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_69_723630(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "AN");
            long furnitureId = stickyFurnitureIdFromPayload(requestPayload);
            if (furnitureId <= 0L) {
                return;
            }
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L || !handlingUserHasRoomRight(callerUserId, roomId)) {
                return;
            }
            String rowText = MySQL.Proc_5_2_6D4690("SELECT id,id_product,id_destination,sign_extra FROM furnitures WHERE id='"
                + furnitureId + "' AND id_room='" + roomId + "' LIMIT 1", 0, 0);
            String[] fields = rowText.split("\t", -1);
            if (rowText.isEmpty() || fields.length < 3) {
                return;
            }
            long boxProductId = Vb.val(handlingField(fields, 1));
            long openedProductId = Vb.val(handlingField(fields, 2));
            String openedSign = handlingField(fields, 3);
            if (boxProductId <= 0L || openedProductId <= 0L) {
                return;
            }
            String boxAction = DataManager.Proc_8_12_806C30(boxProductId, 17, 0).toLowerCase();
            if (!boxAction.contains("present_") || "ecotron_box".equals(boxAction)) {
                return;
            }
            Proc_6_247_8027E0(socketIndex, "A^" + furnitureId + '\2' + "H" + '\2', 0);
            MySQL.Proc_5_0_6D3CD0("DELETE FROM furnitures WHERE id='" + furnitureId + "' LIMIT 1", 0, 0);
            MySQL.Proc_5_0_6D3CD0("INSERT INTO furnitures(id_product,id_owner,sign,task_owner,task_time) VALUES('"
                + openedProductId + "','" + Functions.Proc_10_11_80A9C0(callerUserId, 0, 0) + "','"
                + Functions.Proc_10_11_80A9C0(openedSign, 0, 0) + "','"
                + Functions.Proc_10_11_80A9C0(callerUserId, 0, 0) + "',UNIX_TIMESTAMP())", 0, 0);
            long openedProductType = Vb.val(DataManager.Proc_8_12_806C30(openedProductId, 0, 0));
            String responseClass = "i";
            if (openedProductType == 2L) {
                responseClass = "s";
            }
            if (openedProductType == 3L) {
                responseClass = "e";
            }
            String responsePayload = Crypto.Proc_3_0_6D2AF0(openedProductId, null, "BA" + responseClass + '\2')
                + DataManager.Proc_8_12_806C30(openedProductId, 24, 0) + '\2';
            Proc_6_244_801E80(socketIndex, responsePayload, 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_70_724190(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "FI");
            long furnitureId = stickyFurnitureIdFromPayload(requestPayload);
            if (furnitureId <= 0L) {
                return;
            }
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L || !handlingUserHasRoomRight(callerUserId, roomId)) {
                return;
            }
            String rowText = MySQL.Proc_5_2_6D4690("SELECT id,id_product,sign,position_wall FROM furnitures WHERE id='"
                + furnitureId + "' AND id_room='" + roomId + "' AND position_wall IS NOT NULL LIMIT 1", 0, 0);
            String[] fields = rowText.split("\t", -1);
            if (rowText.isEmpty() || fields.length < 3) {
                return;
            }
            long productId = Vb.val(handlingField(fields, 1));
            if (productId <= 0L) {
                return;
            }
            long currentState = Vb.val(handlingField(fields, 2));
            long stateCount = Licence.Proc_9_0_806F70(productId, 5, 0);
            if (stateCount <= 0L) {
                stateCount = Vb.val(DataManager.Proc_8_12_806C30(productId, 10, 0));
            }
            if (stateCount <= 0L) {
                stateCount = 1L;
            }
            long nextState = currentState + 1L;
            if (nextState > stateCount) {
                nextState = 0L;
            }
            if (nextState < 0L) {
                nextState = 0L;
            }
            MySQL.Proc_5_0_6D3CD0("UPDATE furnitures SET sign='" + nextState + "' WHERE id='" + furnitureId + "'", 0, 0);
            String payload = Crypto.Proc_3_0_6D2AF0(productId, null, "AU" + furnitureId + '\2')
                + nextState + '\2' + "0" + '\2';
            Proc_6_247_8027E0(socketIndex, payload, 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_71_724CF0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L || !handlingUserOwnsRoom(callerUserId, roomId)) {
                return;
            }
            String socketRows = MySQL.Proc_5_2_6D4690("SELECT users.id_socket FROM rooms_rights,users WHERE rooms_rights.id_room='"
                + roomId + "' AND users.id=rooms_rights.id_user AND users.id_socket IS NOT NULL", 0, 0);
            MySQL.Proc_5_0_6D3CD0("DELETE FROM rooms_rights WHERE id_room='" + roomId + "'", 0, 0);
            for (String row : Vb.cStr(socketRows).split("\r", -1)) {
                int targetSocketIndex = (int) Vb.val(row);
                if (targetSocketIndex > 0) {
                    Proc_6_244_801E80(targetSocketIndex, "@k", 0);
                }
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_72_7250D0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "@W");
            long requestFlag = Vb.val(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
            if (requestFlag == 0L && !requestPayload.isEmpty()) {
                requestFlag = readWireLong(requestPayload, new LongRef(1));
            }
            if (requestFlag != 0L) {
                return;
            }
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L || !handlingUserOwnsRoom(callerUserId, roomId)) {
                return;
            }
            Functions.Proc_10_18_80C9E0(roomId, 0, 0);
            MySQL.Proc_5_0_6D3CD0("DELETE FROM rooms WHERE id='" + roomId + "' LIMIT 1", 0, 0);
            Proc_6_53_718E00(socketIndex, 0, 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_73_725540(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "AT");
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L || (!handlingUserHasRoomRight(userId, roomId)
                && !handlingUserHasPermission(userId, "fuse_pick_up_any_furni"))) {
                return;
            }
            long furnitureId = stickyFurnitureIdFromPayload(requestPayload);
            if (furnitureId <= 0L) {
                return;
            }
            long productId = Vb.val(MySQL.Proc_5_2_6D4690("SELECT id_product FROM furnitures WHERE id_room='"
                + roomId + "' AND id='" + furnitureId + "' LIMIT 1", 0, 0));
            if (productId <= 0L) {
                return;
            }
            String productSprite = DataManager.Proc_8_12_806C30(productId, 17, 0);
            if (productSprite.isEmpty()) {
                productSprite = DataManager.Proc_8_12_806C30(productId, 18, 0);
            }
            if (!productSprite.startsWith("CF_") && !productSprite.startsWith("CFC_")) {
                return;
            }
            String[] productParts = productSprite.split("_", -1);
            if (productParts.length < 2) {
                return;
            }
            long creditValue = Vb.val(productParts[1]);
            if (creditValue <= 0L) {
                return;
            }
            String escapedUserId = Functions.Proc_10_11_80A9C0(userId, 0, 0);
            MySQL.Proc_5_0_6D3CD0("UPDATE users SET credits=credits+" + creditValue + " WHERE id='" + escapedUserId + "'", 0, 0);
            long updatedCredits = Vb.val(MySQL.Proc_5_2_6D4690("SELECT credits FROM users WHERE id='" + escapedUserId + "' LIMIT 1", 0, 0));
            Proc_6_244_801E80(socketIndex, "@F" + updatedCredits + ".0" + '\2', 0);
            Proc_6_247_8027E0(socketIndex, "A^" + furnitureId + '\2' + "H" + '\2', 0);
            MySQL.Proc_5_0_6D3CD0("DELETE FROM furnitures WHERE id='" + furnitureId + "' LIMIT 1", 0, 0);
            Proc_6_106_74B750(Path.of(Functions.applicationPath, "CACHE", "ROOMS", roomId + ".cache").toString(), 0, 0);
            Proc_6_106_74B750(Path.of(Functions.applicationPath, "CACHE", "PATHFINDER", roomId + ".cache").toString(), 0, 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_74_7265B0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "Aa");
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L || !handlingUserHasRoomRight(callerUserId, roomId)) {
                return;
            }
            LongRef offset = new LongRef(1);
            long revokeCount = readWireLong(requestPayload, offset);
            if (revokeCount < 1L || revokeCount > 150L) {
                return;
            }
            for (long revokeIndex = 1L; revokeIndex <= revokeCount; revokeIndex++) {
                String targetUserId = String.valueOf(readWireLong(requestPayload, offset));
                if (!targetUserId.isEmpty() && !"0".equals(targetUserId)) {
                    MySQL.Proc_5_0_6D3CD0("DELETE FROM rooms_rights WHERE id_user='"
                        + Functions.Proc_10_11_80A9C0(targetUserId, 0, 0) + "' AND id_room='" + roomId + "'", 0, 0);
                    int targetSocketIndex = handlingSocketFromUserId(targetUserId);
                    if (targetSocketIndex > 0) {
                        Proc_6_244_801E80(targetSocketIndex, "@k", 0);
                    }
                }
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_75_7269D0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "EB");
            LongRef offset = new LongRef(1);
            String targetName = Functions.Proc_10_11_80A9C0(readWireString(requestPayload, offset), 0, 0);
            if (targetName.isEmpty()) {
                return;
            }
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L || !handlingUserHasRoomRight(callerUserId, roomId)) {
                return;
            }
            String targetUserId = String.valueOf(Vb.val(MySQL.Proc_5_2_6D4690("SELECT id FROM users WHERE name='"
                + targetName + "' LIMIT 1", 0, 0)));
            if (targetUserId.isEmpty() || "0".equals(targetUserId)) {
                return;
            }
            MySQL.Proc_5_0_6D3CD0("DELETE FROM rooms_rights WHERE id_user='"
                + Functions.Proc_10_11_80A9C0(targetUserId, 0, 0) + "' AND id_room='" + roomId + "'", 0, 0);
            Proc_6_244_801E80(socketIndex, Crypto.Proc_3_0_6D2AF0(0, null, "Fc"), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static String Proc_6_76_726CE0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "Es");
            LongRef offset = new LongRef(1);
            String targetUserId = String.valueOf(readWireLong(requestPayload, offset));
            if (targetUserId.isEmpty() || "0".equals(targetUserId)) {
                return "";
            }
            String giverUserId = handlingUserIdFromSocket(socketIndex);
            if (giverUserId.isEmpty() || "0".equals(giverUserId) || giverUserId.equals(targetUserId)) {
                return "";
            }
            int targetSocketIndex = handlingSocketFromUserId(targetUserId);
            if (targetSocketIndex <= 0) {
                return "";
            }
            long respectAmount = Vb.val(MySQL.Proc_5_2_6D4690("SELECT respect_amount FROM users WHERE id='"
                + Functions.Proc_10_11_80A9C0(giverUserId, 0, 0) + "' LIMIT 1", 0, 0));
            if (respectAmount <= 0L) {
                return "";
            }
            MySQL.Proc_5_0_6D3CD0("UPDATE users SET respect_amount=respect_amount-1,respect_given=respect_given+1 WHERE id='"
                + Functions.Proc_10_11_80A9C0(giverUserId, 0, 0) + "'", 0, 0);
            MySQL.Proc_5_0_6D3CD0("UPDATE users SET respect_received=respect_received+1 WHERE id='"
                + Functions.Proc_10_11_80A9C0(targetUserId, 0, 0) + "'", 0, 0);
            Proc_6_205_7D9780(socketIndex, 3);
            Proc_6_205_7D9780(targetSocketIndex, 2);
            long respectReceived = Vb.val(MySQL.Proc_5_2_6D4690("SELECT respect_received FROM users WHERE id='"
                + Functions.Proc_10_11_80A9C0(targetUserId, 0, 0) + "' LIMIT 1", 0, 0));
            String payload = Crypto.Proc_3_0_6D2AF0(Vb.val(targetUserId), null, "Fx")
                + Crypto.Proc_3_0_6D2AF0(respectReceived, null, "");
            Proc_6_247_8027E0(socketIndex, payload, 0);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static void Proc_6_77_727590(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "FD");
            LongRef offset = new LongRef(1);
            long roomId = readWireLong(requestPayload, offset);
            if (roomId <= 0L) {
                return;
            }
            String rowText = MySQL.Proc_5_2_6D4690("SELECT rooms.id,rooms_official.id,models.required_files,rooms_official.caption "
                + "FROM rooms_official,rooms,models WHERE rooms.id='" + roomId
                + "' AND rooms_official.id_room=rooms.id AND models.id=rooms.id_model AND models.type='1' LIMIT 1", 0, 0);
            if (rowText.isEmpty()) {
                return;
            }
            String[] fields = rowText.split("\t", -1);
            String requiredFiles = handlingField(fields, 2);
            String roomCaption = handlingField(fields, 3);
            String payload = Crypto.Proc_3_0_6D2AF0(roomId, null, "GE") + requiredFiles + '\2';
            payload = Crypto.Proc_3_0_6D2AF0(roomId, null, payload) + roomCaption + '\2';
            Proc_6_244_801E80(socketIndex, payload, 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_78_7279A0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (socketIndex <= 0 || userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return;
            }
            String roomRow = MySQL.Proc_5_2_6D4690("SELECT rooms.id,rooms.id_slot,NULL,models.name,models.id,rooms.id_floor,"
                + "rooms.id_wallpaper,rooms.id_landscape,rooms.rate,models.map,models.position_x,models.position_y,NULL,"
                + "rooms.name,rooms.disable_walls,rooms.allow_otherspets,rooms.allow_walkthrough,rooms.allow_feedpets,models.type,"
                + "rooms.visitors_primaryid FROM rooms,models WHERE rooms.id='" + roomId
                + "' AND models.id=rooms.id_model LIMIT 1", 0, 0);
            if (roomRow.isEmpty()) {
                return;
            }
            String[] fields = roomRow.split("\t", -1);
            long modelId = Vb.val(handlingField(fields, 4));
            String modelPayload = normalizeRoomModelMap(handlingField(fields, 9));
            Proc_6_244_801E80(socketIndex, "Bf" + "/client.php" + '\2', 0);
            Proc_6_244_801E80(socketIndex, "AE" + roomId + '\2' + "H", 0);
            if (!modelPayload.isEmpty()) {
                Proc_6_244_801E80(socketIndex, "@_" + modelPayload + '\2', 0);
                Proc_6_244_801E80(socketIndex, "GV" + modelPayload + '\2', 0);
                Proc_6_244_801E80(socketIndex, "GWH" + modelPayload + '\2' + "H", 0);
            }
            Proc_6_81_730010(socketIndex, roomId, -1);
            Proc_6_82_731070(socketIndex, roomId, 0);
            Proc_6_84_733600(socketIndex, roomId);
            Proc_6_83_732640(socketIndex, modelId);
            Proc_6_85_73A8E0(socketIndex, roomId);
            Proc_6_235_7F77E0(socketIndex, 0, 0);
            Proc_6_80_72EB60(socketIndex, roomId);
            Proc_6_244_801E80(socketIndex, "CP" + '\2' + '\2', 0);
            sendRoomPollPrompt(socketIndex, userId, roomId);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_79_72A430(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (socketIndex <= 0 || userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return;
            }
            String roomRow = MySQL.Proc_5_2_6D4690("SELECT rooms.id,rooms.id_slot,users.id,models.name,models.id,rooms.id_floor,"
                + "rooms.id_wallpaper,rooms.id_landscape,rooms.rate,models.map,models.position_x,models.position_y,NULL,"
                + "rooms.name,rooms.disable_walls,rooms.allow_otherspets,rooms.allow_walkthrough,rooms.allow_feedpets,models.type,"
                + "rooms.visitors_primaryid,rooms.is_staff_picked,thickness_floor,thickness_wallpaper FROM rooms,models,users "
                + "WHERE rooms.id='" + roomId + "' AND users.id=rooms.id_owner AND models.id=rooms.id_model LIMIT 1", 0, 0);
            if (roomRow.isEmpty()) {
                return;
            }
            String[] fields = roomRow.split("\t", -1);
            long modelId = Vb.val(handlingField(fields, 4));
            String floorPattern = handlingField(fields, 5);
            String wallpaperPattern = handlingField(fields, 6);
            String landscapePattern = handlingField(fields, 7);
            long roomRate = Vb.val(handlingField(fields, 8));
            if (roomRate < 0L) {
                roomRate = 0L;
            }
            String modelPayload = normalizeRoomModelMap(handlingField(fields, 9));
            String ownerUserId = String.valueOf((long) Vb.val(handlingField(fields, 2)));
            long disableWalls = Vb.val(handlingField(fields, 14));
            long thicknessFloor = Vb.val(handlingField(fields, 21));
            long thicknessWallpaper = Vb.val(handlingField(fields, 22));
            boolean hasControl = handlingUserHasRoomRight(userId, roomId)
                || handlingUserHasPermission(userId, "fuse_any_room_controller");
            boolean hasVoted = !MySQL.Proc_5_2_6D4690("SELECT id_user FROM rooms_rates WHERE id_user='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "' AND id_room='" + roomId + "' LIMIT 1", 0, 0).isEmpty();
            long ratingPayloadValue = hasVoted ? -1L : roomRate;
            Proc_6_244_801E80(socketIndex, Crypto.Proc_3_0_6D2AF0(roomId, null, "AE") + '\2', 0);
            Proc_6_244_801E80(socketIndex, "@nfloor" + '\2' + floorPattern + '\2', 0);
            Proc_6_244_801E80(socketIndex, "@nwallpaper" + '\2' + wallpaperPattern + '\2', 0);
            Proc_6_244_801E80(socketIndex, "@nlandscape" + '\2' + landscapePattern + '\2', 0);
            Proc_6_244_801E80(socketIndex, Crypto.Proc_3_0_6D2AF0(ratingPayloadValue, null, "EY"), 0);
            Proc_6_244_801E80(socketIndex, "Er" + Proc_6_51_716AC0(roomId, 0, 0), 0);
            if (hasControl) {
                Proc_6_244_801E80(socketIndex, "@j", 0);
            }
            if (ownerUserId.equals(String.valueOf((long) Vb.val(userId)))) {
                Proc_6_244_801E80(socketIndex, "@o", 0);
            }
            if (!modelPayload.isEmpty()) {
                Proc_6_244_801E80(socketIndex, "@_" + modelPayload + '\2', 0);
                Proc_6_244_801E80(socketIndex, "GV" + modelPayload + '\2', 0);
            }
            String wallPayload = Crypto.Proc_3_0_6D2AF0(disableWalls, null, "GX");
            wallPayload = Crypto.Proc_3_0_6D2AF0(thicknessFloor, null, wallPayload);
            wallPayload = Crypto.Proc_3_0_6D2AF0(thicknessWallpaper, null, wallPayload);
            Proc_6_244_801E80(socketIndex, wallPayload, 0);
            Proc_6_81_730010(socketIndex, roomId, -1);
            Proc_6_82_731070(socketIndex, roomId, 0);
            Proc_6_83_732640(socketIndex, modelId);
            Proc_6_84_733600(socketIndex, roomId);
            Proc_6_85_73A8E0(socketIndex, roomId);
            Proc_6_235_7F77E0(socketIndex, 0, 0);
            Proc_6_80_72EB60(socketIndex, roomId);
            Proc_6_244_801E80(socketIndex, "CP" + '\2' + '\2', 0);
            sendRoomPollPrompt(socketIndex, userId, roomId);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_80_72EB60(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            if (socketIndex <= 0) {
                return;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long roomId = args != null && args.length >= 2 ? Vb.val(args[1]) : 0L;
            if (roomId <= 0L) {
                roomId = handlingCurrentRoomId(socketIndex, userId);
            }
            if (roomId <= 0L) {
                return;
            }
            String rowText = MySQL.Proc_5_2_6D4690("SELECT users.id,users.name,users.figure,users.motto,users.gender,"
                + "models.position_x,models.position_y,rooms.id_slot FROM users,rooms,models WHERE users.id='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "' AND rooms.id='" + roomId
                + "' AND models.id=rooms.id_model LIMIT 1", 0, 0);
            if (rowText.isEmpty()) {
                return;
            }
            String[] fields = rowText.split("\t", -1);
            long roomUserIndex = representedRoomUserIndex(socketIndex, userId);
            long positionX = Vb.val(handlingField(fields, 5));
            long positionY = Vb.val(handlingField(fields, 6));
            String positionZ = "0.0";
            long directionValue = 0L;
            String entryPayload = Proc_6_41_712730(
                Vb.val(handlingField(fields, 0)),
                handlingField(fields, 1),
                handlingField(fields, 2),
                handlingField(fields, 3),
                handlingField(fields, 4),
                roomUserIndex,
                positionX,
                positionY,
                positionZ,
                directionValue,
                0);
            if (!entryPayload.isEmpty()) {
                Proc_6_247_8027E0(socketIndex, Crypto.Proc_3_0_6D2AF0(1, null, "@\\") + entryPayload, 0);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_81_730010(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            long roomId = args != null && args.length >= 2 ? Vb.val(args[1]) : 0L;
            if (roomId <= 0L && socketIndex > 0) {
                String userId = handlingUserIdFromSocket(socketIndex);
                if (!userId.isEmpty() && !"0".equals(userId)) {
                    roomId = handlingCurrentRoomId(socketIndex, userId);
                }
            }
            if (socketIndex <= 0 || roomId <= 0L) {
                return;
            }
            long roomSlot = Vb.val(MySQL.Proc_5_2_6D4690("SELECT rooms.id_slot FROM rooms WHERE rooms.id='"
                + roomId + "' LIMIT 1", 0, 0));
            StringBuilder occupantPayload = new StringBuilder();
            StringBuilder statusPayload = new StringBuilder();
            long occupantCount = 0L;
            long statusCount = 0L;
            String rowText = MySQL.Proc_5_2_6D4690("SELECT logs_visitedrooms.id,users.id,users.name,users.figure,users.motto,"
                + "users.gender,models.position_x,models.position_y,users.id_socket FROM logs_visitedrooms,users,rooms,models "
                + "WHERE logs_visitedrooms.id_room='" + roomId + "' AND logs_visitedrooms.timestamp_left IS NULL "
                + "AND users.id=logs_visitedrooms.id_user AND rooms.id=logs_visitedrooms.id_room AND models.id=rooms.id_model "
                + "ORDER BY logs_visitedrooms.timestamp_enter ASC LIMIT 250", 0, 0);
            for (String row : rowText.split("\r", -1)) {
                if (!row.trim().isEmpty()) {
                    String[] fields = row.split("\t", -1);
                    long roomUserIndex = Vb.val(handlingField(fields, 0));
                    long occupantUserId = Vb.val(handlingField(fields, 1));
                    String genderText = handlingField(fields, 5).toUpperCase();
                    genderText = genderText.isEmpty() ? "M" : genderText.substring(0, 1);
                    if (!"M".equals(genderText) && !"F".equals(genderText)) {
                        genderText = "M";
                    }
                    long positionX = Vb.val(handlingField(fields, 6));
                    long positionY = Vb.val(handlingField(fields, 7));
                    if (roomSlot > 0L) {
                        MovementPosition movementPosition = representedMovementPosition(Licence.global_00829310, roomSlot, roomUserIndex);
                        if (movementPosition.found) {
                            positionX = movementPosition.positionX;
                            positionY = movementPosition.positionY;
                        }
                    }
                    String positionZ = "0.0";
                    long directionValue = 0L;
                    occupantPayload.append(Proc_6_41_712730(occupantUserId, handlingField(fields, 2), handlingField(fields, 3),
                        handlingField(fields, 4), genderText, roomUserIndex, positionX, positionY, positionZ, 0, 0));
                    statusPayload.append(Crypto.Proc_3_0_6D2AF0(roomUserIndex, null, ""))
                        .append(' ').append(positionX).append(' ').append(positionY).append(' ').append(positionZ)
                        .append(' ').append(directionValue).append(' ').append(directionValue).append('/').append('\r');
                    occupantCount++;
                    statusCount++;
                }
            }
            if (roomSlot > 0L) {
                String botEntities = representedBotEntitiesForRoom(roomSlot, 0);
                for (String botRow : botEntities.split("\r", -1)) {
                    long botEntityId = Vb.val(botRow);
                    if (botEntityId > 0L) {
                        String botName = representedBotRecordField(botEntityId, 2);
                        String botFigure = representedBotRecordField(botEntityId, 10);
                        long positionX = representedBotRecordLong(botEntityId, 6);
                        long positionY = representedBotRecordLong(botEntityId, 7);
                        String positionZ = representedBotRecordField(botEntityId, 8);
                        long directionValue = representedBotRecordLong(botEntityId, 9);
                        if (positionZ.isEmpty()) {
                            positionZ = "0.0";
                        }
                        String botEntry = Proc_6_42_712FB0(botEntityId, botName, botFigure, "M", botEntityId,
                            positionX, positionY, positionZ, 2);
                        if (!botEntry.isEmpty()) {
                            occupantPayload.append(botEntry);
                            statusPayload.append(Crypto.Proc_3_0_6D2AF0(botEntityId, null, ""))
                                .append(' ').append(positionX).append(' ').append(positionY).append(' ').append(positionZ)
                                .append(' ').append(directionValue).append(' ').append(directionValue).append('/').append('\r');
                            occupantCount++;
                            statusCount++;
                        }
                    }
                }
            }
            Proc_6_244_801E80(socketIndex, Crypto.Proc_3_0_6D2AF0(occupantCount, null, "@\\") + occupantPayload, -1);
            Proc_6_244_801E80(socketIndex, Crypto.Proc_3_0_6D2AF0(statusCount, null, "Du") + statusPayload, 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_82_731070(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            long roomId = args != null && args.length >= 2 ? Vb.val(args[1]) : 0L;
            if (roomId <= 0L && socketIndex > 0) {
                String userId = handlingUserIdFromSocket(socketIndex);
                if (!userId.isEmpty() && !"0".equals(userId)) {
                    roomId = handlingCurrentRoomId(socketIndex, userId);
                }
            }
            if (socketIndex <= 0 || roomId <= 0L) {
                return;
            }
            String rowText = MySQL.Proc_5_2_6D4690("SELECT logs_visitedrooms.id,users_effects.id_effect "
                + "FROM logs_visitedrooms,users_effects WHERE logs_visitedrooms.id_room='" + roomId
                + "' AND logs_visitedrooms.timestamp_left IS NULL AND users_effects.id_user=logs_visitedrooms.id_user "
                + "AND users_effects.timestamp_expire IS NOT NULL AND users_effects.timestamp_expire>UNIX_TIMESTAMP() "
                + "ORDER BY logs_visitedrooms.timestamp_enter ASC LIMIT 250", 0, 0);
            for (String row : Vb.cStr(rowText).split("\r", -1)) {
                if (!row.trim().isEmpty()) {
                    String[] fields = row.split("\t", -1);
                    long roomUserIndex = Vb.val(handlingField(fields, 0));
                    long effectId = Vb.val(handlingField(fields, 1));
                    if (roomUserIndex > 0L && effectId > 0L) {
                        String effectPayload = Crypto.Proc_3_0_6D2AF0(effectId, null,
                            Crypto.Proc_3_0_6D2AF0(roomUserIndex, null, "Ge"));
                        Proc_6_244_801E80(socketIndex, effectPayload, 0);
                    }
                }
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static String Proc_6_83_732640(Object... args) {
        try {
            int socketIndex = 0;
            long modelId = 0L;
            if (args != null && args.length >= 2) {
                socketIndex = handlingSocketIndex(args);
                modelId = Vb.val(args[1]);
            } else if (args != null && args.length >= 1) {
                modelId = Vb.val(args[0]);
            }
            if (modelId <= 0L && socketIndex > 0) {
                String userId = handlingUserIdFromSocket(socketIndex);
                if (!userId.isEmpty() && !"0".equals(userId)) {
                    long roomId = handlingCurrentRoomId(socketIndex, userId);
                    if (roomId > 0L) {
                        modelId = Vb.val(MySQL.Proc_5_2_6D4690("SELECT id_model FROM rooms WHERE id='" + roomId + "' LIMIT 1", 0, 0));
                    }
                }
            }
            if (modelId <= 0L) {
                return "";
            }
            String rowText = MySQL.Proc_5_2_6D4690("SELECT id_type,id_source,id_sprite,position_x,position_y,position_z,"
                + "action,action_rotation,action_height FROM models_furnitures WHERE id_model='" + modelId + "' LIMIT 500", 0, 0);
            long itemCount = 0L;
            StringBuilder itemPayload = new StringBuilder();
            for (String row : Vb.cStr(rowText).split("\r", -1)) {
                String trimmedRow = row.trim();
                if (!trimmedRow.isEmpty()) {
                    String[] fields = trimmedRow.split("\t", -1);
                    long productId = Vb.val(handlingField(fields, 0));
                    long sourceId = Vb.val(handlingField(fields, 1));
                    if (sourceId <= 0L) {
                        sourceId = itemCount + 1L;
                    }
                    if (productId <= 0L) {
                        productId = sourceId;
                    }
                    long positionX = Vb.val(handlingField(fields, 3));
                    long positionY = Vb.val(handlingField(fields, 4));
                    long positionZ = Vb.val(handlingField(fields, 5));
                    String itemData = handlingField(fields, 6);
                    long rotation = Vb.val(handlingField(fields, 7));
                    itemPayload.append(Proc_6_161_7B2EE0(sourceId, positionX, positionY, rotation, positionZ, "", itemData, 0, productId));
                    itemCount++;
                }
            }
            String payload = Crypto.Proc_3_0_6D2AF0(itemCount, null, "@^") + itemPayload;
            if (socketIndex > 0) {
                Proc_6_244_801E80(socketIndex, payload, 0);
            }
            return payload;
        } catch (Exception ignored) {
            return "";
        }
    }

    public static String Proc_6_84_733600(Object... args) {
        String payload = "Di" + Licence.global_00829094;
        try {
            int socketIndex = args != null && args.length >= 1 ? handlingSocketIndex(args) : 0;
            long roomId = args != null && args.length >= 2 ? Vb.val(args[1]) : 0L;
            if (roomId <= 0L && socketIndex > 0) {
                String userId = handlingUserIdFromSocket(socketIndex);
                if (!userId.isEmpty() && !"0".equals(userId)) {
                    roomId = handlingCurrentRoomId(socketIndex, userId);
                }
            }
            if (socketIndex > 0) {
                Proc_6_244_801E80(socketIndex, payload, 0);
            }
            if (roomId <= 0L) {
                return payload;
            }
            Path cacheRoot = Path.of(Functions.applicationPath, "cache");
            String triggerCache = handlingEnsureRoomCacheFile(cacheRoot.resolve("wired_trigger").resolve(roomId + ".cache").toString());
            String actionCache = handlingEnsureRoomCacheFile(cacheRoot.resolve("wired_action").resolve(roomId + ".cache").toString());
            String conditionCache = handlingEnsureRoomCacheFile(cacheRoot.resolve("wired_condition").resolve(roomId + ".cache").toString());
            String pathfinderCache = handlingEnsureRoomCacheFile(cacheRoot.resolve("pathfinder").resolve(roomId + ".cache").toString());
            String destinationCache = handlingEnsureRoomCacheFile(cacheRoot.resolve("rooms").resolve("destination_" + roomId + ".cache").toString());
            String roomCache = handlingEnsureRoomCacheFile(cacheRoot.resolve("rooms").resolve(roomId + ".cache").toString());
            return payload + '\t' + triggerCache + '\t' + actionCache + '\t' + conditionCache
                + '\t' + pathfinderCache + '\t' + destinationCache + '\t' + roomCache;
        } catch (Exception ignored) {
            return payload;
        }
    }

    public static String Proc_6_85_73A8E0(Object... args) {
        try {
            int socketIndex = args != null && args.length >= 1 ? handlingSocketIndex(args) : 0;
            long roomId = args != null && args.length >= 2 ? Vb.val(args[1]) : 0L;
            if (roomId <= 0L && socketIndex > 0) {
                String userId = handlingUserIdFromSocket(socketIndex);
                if (!userId.isEmpty() && !"0".equals(userId)) {
                    roomId = handlingCurrentRoomId(socketIndex, userId);
                }
            }
            if (roomId <= 0L) {
                return "";
            }
            String rowText = MySQL.Proc_5_2_6D4690("SELECT id,id_product,position_wall,sign,id_secondary FROM furnitures WHERE id_room='"
                + roomId + "' AND id_owner IS NULL AND position_wall IS NOT NULL LIMIT 100", 0, 0);
            long itemCount = 0L;
            StringBuilder itemPayload = new StringBuilder();
            for (String row : Vb.cStr(rowText).split("\r", -1)) {
                String trimmedRow = row.trim();
                if (!trimmedRow.isEmpty()) {
                    String[] fields = trimmedRow.split("\t", -1);
                    long furnitureId = Vb.val(handlingField(fields, 0));
                    long productId = Vb.val(handlingField(fields, 1));
                    String wallPosition = handlingField(fields, 2);
                    String signText = handlingField(fields, 3);
                    long secondaryValue = Vb.val(handlingField(fields, 4));
                    if (furnitureId > 0L && productId > 0L && !wallPosition.isEmpty()) {
                        itemPayload.append(Proc_6_156_7972B0(furnitureId, productId, wallPosition, signText, secondaryValue));
                        itemCount++;
                    }
                }
            }
            String payload = Crypto.Proc_3_0_6D2AF0(itemCount, null, "@m") + itemPayload;
            if (socketIndex > 0) {
                Proc_6_244_801E80(socketIndex, payload, 0);
            }
            return payload;
        } catch (Exception ignored) {
            return "";
        }
    }

    public static String Proc_6_86_73B0D0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingPacketPayload(args);
            if (requestPayload.startsWith("p`") || requestPayload.startsWith("rt")) {
                requestPayload = requestPayload.substring(2);
            }
            LongRef offset = new LongRef(1);
            long furnitureId = readWireLong(requestPayload, offset);
            if (furnitureId <= 0L) {
                furnitureId = Vb.val(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
            }
            if (socketIndex <= 0 || furnitureId <= 0L) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return "";
            }
            String rowText = MySQL.Proc_5_2_6D4690("SELECT id_product FROM furnitures WHERE id='" + furnitureId
                + "' AND id_room='" + roomId + "' LIMIT 1", 0, 0);
            if (rowText.isEmpty()) {
                return "";
            }
            long productId = Vb.val(handlingField(rowText.split("\t", -1), 0));
            if (productId <= 0L) {
                return "";
            }
            String packageRow = MySQL.Proc_5_2_6D4690("SELECT id_product,type_secondary,id_contain,type_check FROM packages WHERE id_product='"
                + productId + "' LIMIT 1", 0, 0);
            if (packageRow.isEmpty()) {
                return "";
            }
            String[] packageFields = packageRow.split("\t", -1);
            String packageType = handlingField(packageFields, 1).toLowerCase();
            long containedPetId = Vb.val(handlingField(packageFields, 2));
            if (!"packages_pets".equals(packageType) || containedPetId <= 0L) {
                return "";
            }
            String petRow = MySQL.Proc_5_2_6D4690("SELECT id_pet,id_race,color FROM packages_pets WHERE id='"
                + containedPetId + "' LIMIT 1", 0, 0);
            if (petRow.isEmpty()) {
                return "";
            }
            String[] petFields = petRow.split("\t", -1);
            long petType = Vb.val(handlingField(petFields, 0));
            long petRace = Vb.val(handlingField(petFields, 1));
            String petColor = handlingField(petFields, 2);
            String payload = Crypto.Proc_3_0_6D2AF0(furnitureId, null, "Ly");
            payload = Crypto.Proc_3_0_6D2AF0(petType, null, payload);
            payload = Crypto.Proc_3_0_6D2AF0(petRace, null, payload);
            payload = Crypto.Proc_3_0_6D2AF0(Vb.val(petColor), null, payload) + petColor + '\2';
            Proc_6_244_801E80(socketIndex, payload, 0);
            return payload;
        } catch (Exception ignored) {
            return "";
        }
    }

    public static String Proc_6_87_73C120(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "n~");
            LongRef offset = new LongRef(1);
            long furnitureId = readWireLong(requestPayload, offset);
            if (furnitureId <= 0L) {
                furnitureId = Vb.val(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
            }
            String petName = Functions.Proc_10_10_80A7F0(readWireString(requestPayload, offset), 0, 0);
            if (petName.isEmpty()) {
                petName = Functions.Proc_10_10_80A7F0(Functions.Proc_10_7_80A190(requestPayload, 0, 0), 0, 0);
            }
            long validationCode = Proc_6_181_7CA920(petName, 0, 0);
            if (validationCode > 0L) {
                Proc_6_244_801E80(socketIndex, Crypto.Proc_3_0_6D2AF0(validationCode, null,
                    Crypto.Proc_3_0_6D2AF0(furnitureId, null, "Lz")) + petName + '\2', 0);
                return "";
            }
            if (socketIndex <= 0 || furnitureId <= 0L) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return "";
            }
            String rowText = MySQL.Proc_5_2_6D4690("SELECT id_product,id_owner FROM furnitures WHERE id='"
                + furnitureId + "' AND id_room='" + roomId + "' LIMIT 1", 0, 0);
            if (rowText.isEmpty()) {
                return "";
            }
            String[] fields = rowText.split("\t", -1);
            long productId = Vb.val(handlingField(fields, 0));
            String ownerId = String.valueOf((long) Vb.val(handlingField(fields, 1)));
            if (productId <= 0L || !ownerId.equals(userId)
                && !handlingUserOwnsRoom(userId, roomId)
                && !handlingUserHasRoomRight(userId, roomId)) {
                return "";
            }
            String packageRow = MySQL.Proc_5_2_6D4690("SELECT id_product,type_secondary,id_contain,type_check FROM packages WHERE id_product='"
                + productId + "' LIMIT 1", 0, 0);
            if (packageRow.isEmpty()) {
                return "";
            }
            String[] packageFields = packageRow.split("\t", -1);
            String packageType = handlingField(packageFields, 1).toLowerCase();
            long containedPetId = Vb.val(handlingField(packageFields, 2));
            if (!"packages_pets".equals(packageType) || containedPetId <= 0L) {
                return "";
            }
            String petRow = MySQL.Proc_5_2_6D4690("SELECT id_pet,id_race,color FROM packages_pets WHERE id='"
                + containedPetId + "' LIMIT 1", 0, 0);
            if (petRow.isEmpty()) {
                return "";
            }
            String[] petFields = petRow.split("\t", -1);
            String petFigure = String.valueOf((long) Vb.val(handlingField(petFields, 0))) + ' '
                + String.valueOf((long) Vb.val(handlingField(petFields, 1))) + ' '
                + handlingField(petFields, 2);
            String escapedUserId = Functions.Proc_10_11_80A9C0(userId, 0, 0);
            MySQL.Proc_5_0_6D3CD0("INSERT INTO bots(id_user,figure,name,id_handle) VALUES('" + escapedUserId
                + "','" + Functions.Proc_10_11_80A9C0(petFigure.toLowerCase(), 0, 0) + "','"
                + Functions.Proc_10_11_80A9C0(petName, 0, 0) + "','3')", 0, 0);
            long botId = Vb.val(MySQL.Proc_5_2_6D4690("SELECT id FROM bots WHERE id_user='" + escapedUserId
                + "' AND id_handle='3' ORDER BY id DESC LIMIT 1", 0, 0));
            if (botId <= 0L) {
                return "";
            }
            MySQL.Proc_5_0_6D3CD0("INSERT INTO bots_petdata(id_bot,timestamp_buy,id_owner,energy,nutrition,scratches) VALUES('"
                + botId + "',UNIX_TIMESTAMP(),'" + escapedUserId + "','100','100','0')", 0, 0);
            String inventoryRow = petInventoryRowPayload(new String[]{String.valueOf(botId), petName, petFigure, "0"});
            if (!inventoryRow.isEmpty()) {
                Proc_6_244_801E80(socketIndex, "I[" + inventoryRow, 0);
            }
            Proc_6_146_76D300(socketIndex, furnitureId, productId);
            Proc_6_247_8027E0(socketIndex, "A^" + furnitureId + '\2' + "H" + '\2', 0);
            MySQL.Proc_5_0_6D3CD0("DELETE FROM furnitures WHERE id='" + furnitureId + "' LIMIT 1", 0, 0);
            Proc_6_244_801E80(socketIndex, Crypto.Proc_3_0_6D2AF0(validationCode, null,
                Crypto.Proc_3_0_6D2AF0(furnitureId, null, "Lz")) + petName + '\2', 0);
            return String.valueOf(botId);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static void Proc_6_88_73E4F0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            LocalDateTime now = LocalDateTime.now();
            if (Licence.global_0082908C.isEmpty()
                || Licence.global_00829090 == null
                || !Licence.global_00829090.isAfter(now)) {
                Licence.global_0082908C = MySQL.Proc_5_2_6D4690("SELECT rooms.id,models.type FROM rooms_categories,rooms,models "
                    + "WHERE rooms_categories.is_newfriends='1' AND rooms.id_category=rooms_categories.id "
                    + "AND models.id=rooms.id_model ORDER BY rooms.visitors_now DESC LIMIT 15", 0, 0);
                Licence.global_00829090 = now.plusSeconds(90L);
            }
            long roomId = 0L;
            long modelType = 0L;
            if (!Licence.global_0082908C.isEmpty()) {
                String[] rows = Licence.global_0082908C.split("\r", -1);
                int rowIndex = (int) Vb.val(Functions.Proc_10_4_809CA0(0, rows.length - 1L, 0));
                if (rowIndex < 0) {
                    rowIndex = 0;
                }
                if (rowIndex >= rows.length) {
                    rowIndex = rows.length - 1;
                }
                if (rowIndex >= 0 && !rows[rowIndex].isEmpty()) {
                    String[] fields = rows[rowIndex].split("\t", -1);
                    if (fields.length >= 2) {
                        roomId = Vb.val(fields[0]);
                        modelType = Vb.val(fields[1]);
                    }
                }
            }
            String payload = Crypto.Proc_3_0_6D2AF0(roomId, null, "L\u007f");
            payload = Crypto.Proc_3_0_6D2AF0(modelType, null, payload);
            Proc_6_244_801E80(socketIndex, payload, 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static String Proc_6_89_73EA10(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            if (socketIndex <= 0) {
                return "";
            }
            int targetSocketIndex = representedInteractionPartner(socketIndex);
            if (targetSocketIndex <= 0) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            String targetUserId = handlingUserIdFromSocket(targetSocketIndex);
            if (userId.isEmpty() || "0".equals(userId) || targetUserId.isEmpty() || "0".equals(targetUserId)) {
                return "";
            }
            String sourceSqlIds = representedTradeOfferSqlIds(representedTradeOffers, socketIndex);
            String targetSqlIds = representedTradeOfferSqlIds(representedTradeOffers, targetSocketIndex);
            if (sourceSqlIds.isEmpty() && targetSqlIds.isEmpty()) {
                return "";
            }
            String sourceLogItems = representedTradeOfferLogItems(representedTradeOffers, socketIndex);
            String targetLogItems = representedTradeOfferLogItems(representedTradeOffers, targetSocketIndex);
            String escapedUserId = Functions.Proc_10_11_80A9C0(userId, 0, 0);
            String escapedTargetUserId = Functions.Proc_10_11_80A9C0(targetUserId, 0, 0);
            if (!sourceSqlIds.isEmpty()) {
                MySQL.Proc_5_0_6D3CD0("UPDATE furnitures SET id_owner='" + escapedTargetUserId
                    + "' WHERE id IN (" + sourceSqlIds + ") AND id_owner='" + escapedUserId
                    + "' AND id_room IS NULL", 0, 0);
            }
            if (!targetSqlIds.isEmpty()) {
                MySQL.Proc_5_0_6D3CD0("UPDATE furnitures SET id_owner='" + escapedUserId
                    + "' WHERE id IN (" + targetSqlIds + ") AND id_owner='" + escapedTargetUserId
                    + "' AND id_room IS NULL", 0, 0);
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            String sessionId = handlingUserSessionId(userId);
            MySQL.Proc_5_1_6D4110("INSERT INTO logs_trading(id_user,id_partner,items_user,items_partner,id_room,timestamp,id_session) VALUES('"
                + escapedUserId + "','" + escapedTargetUserId + "','"
                + Functions.Proc_10_11_80A9C0(sourceLogItems, 0, 0) + "','"
                + Functions.Proc_10_11_80A9C0(targetLogItems, 0, 0) + "','" + roomId
                + "',UNIX_TIMESTAMP(),'" + Functions.Proc_10_11_80A9C0(sessionId, 0, 0) + "')", 0, 0);
            Proc_6_244_801E80(socketIndex, "Ap", 0);
            Proc_6_244_801E80(targetSocketIndex, "Ap", 0);
            Proc_6_140_769400(socketIndex, "FT", "");
            Proc_6_140_769400(targetSocketIndex, "FT", "");
            removeRepresentedInteractionPair(socketIndex);
            removeRepresentedInteractionPair(targetSocketIndex);
            return "Ap";
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static void Proc_6_90_742E80(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            if (socketIndex <= 0) {
                return;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long sourceRoomUserIndex = representedRoomUserIndex(socketIndex, userId);
            if (sourceRoomUserIndex <= 0L) {
                return;
            }
            int targetSocketIndex = args != null && args.length >= 2 ? (int) Vb.val(args[1]) : 0;
            if (targetSocketIndex <= 0) {
                targetSocketIndex = representedInteractionPartner(socketIndex);
            }
            long interactionState = args != null && args.length >= 3
                ? Vb.val(args[2])
                : representedInteractionState(socketIndex);
            if (targetSocketIndex <= 0) {
                return;
            }
            String targetUserId = handlingUserIdFromSocket(targetSocketIndex);
            if (targetUserId.isEmpty() || "0".equals(targetUserId)) {
                return;
            }
            String sourcePayload = "0" + Crypto.Proc_3_0_6D2AF0(interactionState, null,
                "0" + Crypto.Proc_3_0_6D2AF0(sourceRoomUserIndex, null, "Am"));
            String targetPayload = Crypto.Proc_3_0_6D2AF0(interactionState, null,
                Crypto.Proc_3_0_6D2AF0(sourceRoomUserIndex, null, "Am"));
            Proc_6_244_801E80(socketIndex, sourcePayload, 0);
            Proc_6_244_801E80(targetSocketIndex, targetPayload, 0);
            if (interactionState == 1L) {
                Proc_6_244_801E80(socketIndex, "Ao", 0);
                Proc_6_244_801E80(targetSocketIndex, "Ao", 0);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static String Proc_6_91_743480(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            if (socketIndex <= 0) {
                return "";
            }
            String requestPayload = handlingRequestPayload(args, "FU");
            int targetSocketIndex = representedInteractionPartner(socketIndex);
            if (targetSocketIndex <= 0) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            String targetUserId = handlingUserIdFromSocket(targetSocketIndex);
            if (userId.isEmpty() || "0".equals(userId) || targetUserId.isEmpty() || "0".equals(targetUserId)) {
                return "";
            }
            long furnitureId = stickyFurnitureIdFromPayload(requestPayload);
            if (furnitureId <= 0L) {
                return "";
            }
            String rowText = MySQL.Proc_5_2_6D4690("SELECT id,id_product,sign,id_secondary FROM furnitures WHERE id='"
                + furnitureId + "' AND id_owner='" + Functions.Proc_10_11_80A9C0(userId, 0, 0)
                + "' AND id_room IS NULL LIMIT 1", 0, 0);
            if (rowText.isEmpty()) {
                return "";
            }
            String[] fields = rowText.split("\t", -1);
            long productId = Vb.val(handlingField(fields, 1));
            String signText = handlingField(fields, 2);
            long secondaryValue = Vb.val(handlingField(fields, 3));
            if (productId <= 0L) {
                return "";
            }
            storeRepresentedTradeOffer(socketIndex, furnitureId, productId, signText, secondaryValue);
            String sourcePayload = representedTradeOfferPayload(representedTradeOffers, socketIndex, targetSocketIndex, userId, targetUserId);
            String targetPayload = representedTradeOfferPayload(representedTradeOffers, targetSocketIndex, socketIndex, targetUserId, userId);
            if (!sourcePayload.isEmpty()) {
                Proc_6_244_801E80(socketIndex, sourcePayload, 0);
            }
            if (!targetPayload.isEmpty()) {
                Proc_6_244_801E80(targetSocketIndex, targetPayload, 0);
            }
            return sourcePayload;
        } catch (Exception ignored) {
            return "";
        }
    }

    public static String Proc_6_92_744870(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            if (socketIndex <= 0) {
                return "";
            }
            String requestPayload = handlingRequestPayload(args, "AH");
            int targetSocketIndex = representedInteractionPartner(socketIndex);
            if (targetSocketIndex <= 0) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            String targetUserId = handlingUserIdFromSocket(targetSocketIndex);
            if (userId.isEmpty() || "0".equals(userId) || targetUserId.isEmpty() || "0".equals(targetUserId)) {
                return "";
            }
            long furnitureId = stickyFurnitureIdFromPayload(requestPayload);
            if (furnitureId <= 0L) {
                return "";
            }
            String rowText = MySQL.Proc_5_2_6D4690("SELECT id,id_product,sign FROM furnitures WHERE id='"
                + furnitureId + "' AND id_owner='" + Functions.Proc_10_11_80A9C0(userId, 0, 0)
                + "' AND id_room IS NULL LIMIT 1", 0, 0);
            if (rowText.isEmpty()) {
                return "";
            }
            removeRepresentedTradeOffer(socketIndex, furnitureId);
            String sourcePayload = representedTradeOfferPayload(representedTradeOffers, socketIndex, targetSocketIndex, userId, targetUserId);
            String targetPayload = representedTradeOfferPayload(representedTradeOffers, targetSocketIndex, socketIndex, targetUserId, userId);
            if (!sourcePayload.isEmpty()) {
                Proc_6_244_801E80(socketIndex, sourcePayload, 0);
            }
            if (!targetPayload.isEmpty()) {
                Proc_6_244_801E80(targetSocketIndex, targetPayload, 0);
            }
            return sourcePayload;
        } catch (Exception ignored) {
            return "";
        }
    }

    public static void Proc_6_93_745D90(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            if (socketIndex <= 0) {
                return;
            }
            String requestPayload = handlingRequestPayload(args, "AG");
            long requestedRoomUserIndex = readWireLong(requestPayload, new LongRef(1));
            if (requestedRoomUserIndex <= 0L) {
                requestedRoomUserIndex = Vb.val(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
            }
            if (requestedRoomUserIndex <= 0L) {
                return;
            }
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long callerRoomId = handlingCurrentRoomId(socketIndex, callerUserId);
            if (callerRoomId <= 0L) {
                return;
            }
            String targetRow = MySQL.Proc_5_2_6D4690("SELECT logs_visitedrooms.id,logs_visitedrooms.id_user,users.id_socket "
                + "FROM logs_visitedrooms,users WHERE logs_visitedrooms.id='" + requestedRoomUserIndex
                + "' AND logs_visitedrooms.id_room='" + callerRoomId
                + "' AND logs_visitedrooms.timestamp_left IS NULL AND users.id=logs_visitedrooms.id_user LIMIT 1", 0, 0);
            if (targetRow.isEmpty()) {
                targetRow = MySQL.Proc_5_2_6D4690("SELECT logs_visitedrooms.id,logs_visitedrooms.id_user,users.id_socket "
                    + "FROM logs_visitedrooms,users WHERE logs_visitedrooms.id_user='" + requestedRoomUserIndex
                    + "' AND logs_visitedrooms.id_room='" + callerRoomId
                    + "' AND logs_visitedrooms.timestamp_left IS NULL AND users.id=logs_visitedrooms.id_user LIMIT 1", 0, 0);
            }
            if (targetRow.isEmpty()) {
                return;
            }
            String[] targetFields = targetRow.split("\t", -1);
            long targetRoomUserIndex = Vb.val(handlingField(targetFields, 0));
            String targetUserId = String.valueOf(Vb.val(handlingField(targetFields, 1)));
            int targetSocketIndex = (int) Vb.val(handlingField(targetFields, 2));
            if (targetRoomUserIndex <= 0L || targetUserId.isEmpty() || "0".equals(targetUserId)) {
                return;
            }
            if (targetSocketIndex <= 0) {
                targetSocketIndex = handlingSocketFromUserId(targetUserId);
            }
            if (targetSocketIndex <= 0 || targetSocketIndex == socketIndex || representedInteractionPartner(targetSocketIndex) > 0) {
                return;
            }
            storeRepresentedInteractionPair(socketIndex, targetSocketIndex, 1L);
            String callerPayload = Crypto.Proc_3_0_6D2AF0(Vb.val(targetUserId), null,
                Crypto.Proc_3_0_6D2AF0(Vb.val(callerUserId), null, "Ah"));
            String targetPayload = Crypto.Proc_3_0_6D2AF0(Vb.val(callerUserId), null,
                Crypto.Proc_3_0_6D2AF0(Vb.val(targetUserId), null, "Ah"));
            Proc_6_244_801E80(socketIndex, callerPayload, 0);
            Proc_6_244_801E80(targetSocketIndex, targetPayload, 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_94_746990(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            if (socketIndex <= 0) {
                return;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long sourceRoomUserIndex = representedRoomUserIndex(socketIndex, userId);
            if (sourceRoomUserIndex <= 0L) {
                return;
            }
            int targetSocketIndex = args != null && args.length >= 2 ? (int) Vb.val(args[1]) : 0;
            if (targetSocketIndex <= 0) {
                targetSocketIndex = representedInteractionPartner(socketIndex);
            }
            if (targetSocketIndex <= 0) {
                return;
            }
            String targetUserId = handlingUserIdFromSocket(targetSocketIndex);
            if (targetUserId.isEmpty() || "0".equals(targetUserId)) {
                return;
            }
            long targetRoomUserIndex = representedRoomUserIndex(targetSocketIndex, targetUserId);
            if (targetRoomUserIndex <= 0L) {
                return;
            }
            String payload = "0" + Crypto.Proc_3_0_6D2AF0(sourceRoomUserIndex, null, "An");
            Proc_6_244_801E80(socketIndex, payload, 0);
            Proc_6_244_801E80(targetSocketIndex, payload, 0);
            removeRepresentedInteractionPair(socketIndex);
            removeRepresentedInteractionPair(targetSocketIndex);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static String Proc_6_95_746CD0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "Cw");
            String userId = handlingUserIdFromSocket(socketIndex);
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (userId.isEmpty() || "0".equals(userId) || roomId <= 0L) {
                return "";
            }
            long furnitureId = idRequestFromWire(requestPayload, "");
            if (furnitureId <= 0L) {
                return "";
            }
            String rowText = MySQL.Proc_5_2_6D4690("SELECT id,position_x,position_y,id_product FROM furnitures WHERE id='"
                + furnitureId + "' AND id_room='" + roomId + "' LIMIT 1", 0, 0);
            if (rowText.isEmpty()) {
                return "";
            }
            long productId = Vb.val(handlingField(rowText.split("\t", -1), 3));
            String productAction = DataManager.Proc_8_12_806C30(productId, 17, 0);
            return "habbowheel".equals(productAction) ? String.valueOf(furnitureId) : "";
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_96_747000(Object... args) {
        return handlingSimpleFloorItemUse(args, "AM", 0L, true);
    }

    public static String Proc_6_97_747640(Object... args) {
        return handlingSimpleFloorItemUse(args, "AL", -1L, false);
    }

    public static long Proc_6_98_747D80(Object... args) {
        long currentPresetId = 0L;
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L || (!handlingUserOwnsRoom(userId, roomId) && !handlingUserHasRoomRight(userId, roomId))) {
                return 0L;
            }
            long dimmerFurnitureId = representedDimmerFurnitureId(roomId);
            if (dimmerFurnitureId <= 0L) {
                return 0L;
            }
            String rowText = MySQL.Proc_5_2_6D4690("SELECT id_light,id_preset,id_background,colour,id_state "
                + "FROM furnitures_dimmerpresets WHERE id_furni='" + dimmerFurnitureId + "' LIMIT 3", 0, 0);
            StringBuilder presetPayload = new StringBuilder();
            for (String row : rowText.split("\r", -1)) {
                if (!row.isEmpty()) {
                    String[] fields = row.split("\t", -1);
                    if (fields.length >= 5) {
                        long lightLevel = Vb.val(handlingField(fields, 0));
                        long presetId = Vb.val(handlingField(fields, 1));
                        long backgroundId = Vb.val(handlingField(fields, 2));
                        String colourText = handlingField(fields, 3);
                        long stateId = Vb.val(handlingField(fields, 4));
                        if (stateId == 2L || currentPresetId == 0L) {
                            currentPresetId = presetId;
                        }
                        presetPayload.append(Crypto.Proc_3_0_6D2AF0(presetId, null, ""));
                        presetPayload.append(Crypto.Proc_3_0_6D2AF0(backgroundId, null, ""));
                        presetPayload.append(Crypto.Proc_3_0_6D2AF0(lightLevel, null, ""));
                        presetPayload.append(colourText).append('\2');
                    }
                }
            }
            String payload = Crypto.Proc_3_0_6D2AF0(currentPresetId, null,
                Crypto.Proc_3_0_6D2AF0(0, null, "Em")) + presetPayload;
            Proc_6_244_801E80(socketIndex, payload, 0);
            return currentPresetId;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return currentPresetId;
        }
    }

    public static long Proc_6_99_748460(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L || (!handlingUserOwnsRoom(userId, roomId) && !handlingUserHasRoomRight(userId, roomId))) {
                return 0L;
            }
            long dimmerFurnitureId = representedDimmerFurnitureId(roomId);
            if (dimmerFurnitureId <= 0L) {
                return 0L;
            }
            String rowText = MySQL.Proc_5_2_6D4690("SELECT furnitures_dimmerpresets.id_light,"
                + "furnitures_dimmerpresets.id_preset,furnitures_dimmerpresets.id_background,"
                + "furnitures_dimmerpresets.colour,furnitures.id_product,furnitures.position_wall,furnitures.sign "
                + "FROM furnitures_dimmerpresets,furnitures WHERE furnitures_dimmerpresets.id_furni='"
                + dimmerFurnitureId + "' AND furnitures_dimmerpresets.id_state='2' "
                + "AND furnitures.id=furnitures_dimmerpresets.id_furni LIMIT 1", 0, 0);
            if (rowText.isEmpty()) {
                return 0L;
            }
            String[] fields = rowText.split("\t", -1);
            if (fields.length < 7) {
                return 0L;
            }
            long lightLevel = Vb.val(handlingField(fields, 0));
            long presetId = Vb.val(handlingField(fields, 1));
            long backgroundId = Vb.val(handlingField(fields, 2));
            String colourText = handlingField(fields, 3);
            long productId = Vb.val(handlingField(fields, 4));
            String wallPosition = handlingField(fields, 5);
            String currentSign = handlingField(fields, 6);
            long currentState = currentSign.isEmpty() ? 0L : Vb.val(currentSign.substring(0, 1));
            if (currentState <= 0L) {
                currentState = 2L;
            }
            long nextState = currentState - 1L;
            if (nextState < 1L) {
                nextState = 2L;
            }
            String signText = nextState + "," + presetId + "," + backgroundId + "," + colourText + "," + lightLevel;
            MySQL.Proc_5_0_6D3CD0("UPDATE furnitures SET sign='"
                + Functions.Proc_10_11_80A9C0(signText, 0, 0) + "' WHERE id='" + dimmerFurnitureId + "'", 0, 0);
            Proc_6_247_8027E0(socketIndex, "AU" + dimmerFurnitureId + '\2'
                + Crypto.Proc_3_0_6D2AF0(productId, null, "") + wallPosition + '\2' + signText + '\2', 0);
            return nextState;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return 0L;
        }
    }

    public static long Proc_6_100_748C80(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "EV");
            LongRef offset = new LongRef(1);
            long presetId = readWireLong(requestPayload, offset);
            long backgroundId = readWireLong(requestPayload, offset);
            String colourText = readWireString(requestPayload, offset).toUpperCase();
            long lightLevel = readWireLong(requestPayload, offset);
            if (presetId < 1L || presetId > 3L || backgroundId < 1L || backgroundId > 2L
                || !isDimmerColour(colourText) || lightLevel < 76L || lightLevel > 225L) {
                return 0L;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L || (!handlingUserOwnsRoom(userId, roomId) && !handlingUserHasRoomRight(userId, roomId))) {
                return 0L;
            }
            long dimmerFurnitureId = representedDimmerFurnitureId(roomId);
            if (dimmerFurnitureId <= 0L) {
                return 0L;
            }
            String signText = "2," + presetId + "," + backgroundId + "," + colourText + "," + lightLevel;
            MySQL.Proc_5_0_6D3CD0("UPDATE furnitures_dimmerpresets SET id_state='1' WHERE id_furni='"
                + dimmerFurnitureId + "'", 0, 0);
            MySQL.Proc_5_0_6D3CD0("UPDATE furnitures_dimmerpresets SET id_state='2',id_light='" + lightLevel
                + "',id_background='" + backgroundId + "',colour='" + Functions.Proc_10_11_80A9C0(colourText, 0, 0)
                + "' WHERE id_furni='" + dimmerFurnitureId + "' AND id_preset='" + presetId + "'", 0, 0);
            MySQL.Proc_5_0_6D3CD0("UPDATE furnitures SET sign='" + Functions.Proc_10_11_80A9C0(signText, 0, 0)
                + "' WHERE id='" + dimmerFurnitureId + "'", 0, 0);
            String rowText = MySQL.Proc_5_2_6D4690("SELECT id_product,position_wall FROM furnitures WHERE id='"
                + dimmerFurnitureId + "' LIMIT 1", 0, 0);
            String[] fields = rowText.split("\t", -1);
            if (fields.length >= 2) {
                long productId = Vb.val(handlingField(fields, 0));
                String wallPosition = handlingField(fields, 1);
                Proc_6_247_8027E0(socketIndex, "AU" + dimmerFurnitureId + '\2'
                    + Crypto.Proc_3_0_6D2AF0(productId, null, "") + wallPosition + '\2' + signText + '\2', 0);
            }
            return dimmerFurnitureId;
        } catch (Exception ignored) {
            return 0L;
        }
    }

    public static long Proc_6_101_749540(Object... args) {
        long listedEffects = 0L;
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            String rowText = MySQL.Proc_5_2_6D4690("SELECT id_effect,time_rent,COUNT(id_effect),timestamp_expire,UNIX_TIMESTAMP() "
                + "FROM users_effects WHERE id_user='" + Functions.Proc_10_11_80A9C0(userId, 0, 0)
                + "' GROUP BY users_effects.id_effect LIMIT 50", 0, 0);
            StringBuilder effectsPayload = new StringBuilder();
            for (String row : Vb.cStr(rowText).split("\r", -1)) {
                if (!row.isEmpty()) {
                    String[] fields = row.split("\t", -1);
                    if (fields.length >= 5) {
                        long effectId = Vb.val(handlingField(fields, 0));
                        long rentSeconds = Vb.val(handlingField(fields, 1));
                        long effectCount = Vb.val(handlingField(fields, 2));
                        long expireTimestamp = Vb.val(handlingField(fields, 3));
                        long currentTimestamp = Vb.val(handlingField(fields, 4));
                        if (effectId > 0L) {
                            effectsPayload.append(Crypto.Proc_3_0_6D2AF0(effectId, null, ""));
                            effectsPayload.append(Crypto.Proc_3_0_6D2AF0(rentSeconds, null, ""));
                            effectsPayload.append(Crypto.Proc_3_0_6D2AF0(effectCount, null, ""));
                            long remainingSeconds = expireTimestamp - currentTimestamp;
                            effectsPayload.append(expireTimestamp > 0L && remainingSeconds > 0L
                                ? Crypto.Proc_3_0_6D2AF0(remainingSeconds, null, "")
                                : "M");
                            listedEffects++;
                        }
                    }
                }
            }
            Proc_6_244_801E80(socketIndex, Crypto.Proc_3_0_6D2AF0(listedEffects, null, "GL") + effectsPayload, 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
        return listedEffects;
    }

    public static long Proc_6_102_749C50(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "");
            if (requestPayload.length() >= 3) {
                requestPayload = requestPayload.substring(2);
            }
            long effectId = Vb.val(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
            if (effectId <= 0L) {
                effectId = readWireLong(requestPayload, new LongRef(1));
            }
            if (effectId <= 0L) {
                return 0L;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            String effectRow = MySQL.Proc_5_2_6D4690("SELECT id,time_rent,timestamp_expire FROM users_effects WHERE id_user='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "' AND id_effect='" + effectId
                + "' ORDER BY timestamp_expire DESC LIMIT 1", 0, 0);
            String[] fields = effectRow.split("\t", -1);
            if (fields.length < 2) {
                return 0L;
            }
            long effectRowId = Vb.val(handlingField(fields, 0));
            long rentSeconds = Vb.val(handlingField(fields, 1));
            if (effectRowId <= 0L || rentSeconds <= 0L) {
                return 0L;
            }
            MySQL.Proc_5_0_6D3CD0("UPDATE users_effects SET timestamp_expire=UNIX_TIMESTAMP()+time_rent WHERE id='"
                + effectRowId + "' LIMIT 1", 0, 0);
            Proc_6_244_801E80(socketIndex, Crypto.Proc_3_0_6D2AF0(rentSeconds, null,
                Crypto.Proc_3_0_6D2AF0(effectId, null, "GN")), 0);
            String broadcastPayload = Crypto.Proc_3_0_6D2AF0(socketIndex, null, "Ge");
            broadcastPayload = Crypto.Proc_3_0_6D2AF0(effectId, null, broadcastPayload) + "H";
            Proc_6_247_8027E0(socketIndex, broadcastPayload, 0);
            return effectId;
        } catch (Exception ignored) {
            return 0L;
        }
    }

    public static long Proc_6_103_74A510(Object... args) {
        long expiredCount = 0L;
        try {
            String queryText = "SELECT users_effects.id_effect,users.id_socket,users_effects.id "
                + "FROM users_effects,users WHERE users_effects.timestamp_expire IS NOT NULL "
                + "AND users_effects.timestamp_expire<UNIX_TIMESTAMP() AND users.id=users_effects.id_user "
                + "AND users.id_socket IS NOT NULL LIMIT 500";
            String rowText = MySQL.Proc_5_2_6D4690(queryText, 1, 0);
            for (String row : Vb.cStr(rowText).split("\r", -1)) {
                if (!row.isEmpty()) {
                    String[] fields = row.split("\t", -1);
                    if (fields.length >= 3) {
                        long effectId = Vb.val(handlingField(fields, 0));
                        int socketIndex = (int) Vb.val(handlingField(fields, 1));
                        if (socketIndex > 0 && effectId > 0L) {
                            Proc_6_247_8027E0(socketIndex, Crypto.Proc_3_0_6D2AF0(socketIndex, null, "Ge") + "H", 0);
                            Proc_6_244_801E80(socketIndex, Crypto.Proc_3_0_6D2AF0(effectId, null, "GO"), 0);
                            expiredCount++;
                        }
                    }
                }
            }
            MySQL.Proc_5_0_6D3CD0("DELETE FROM users_effects WHERE users_effects.timestamp_expire IS NOT NULL "
                + "AND users_effects.timestamp_expire<UNIX_TIMESTAMP() LIMIT 500", 0, 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
        return expiredCount;
    }

    public static void Proc_6_104_74AB60(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            long maxOwnedRooms = Vb.val(Functions.Proc_10_0_809570("com.server.socket.game.rooms.own.max", 0, 0));
            long ownedRoomCount = Vb.val(MySQL.Proc_5_2_6D4690("SELECT COUNT(id) FROM rooms WHERE id_owner='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "'", 0, 0));
            String payload = Crypto.Proc_3_0_6D2AF0(maxOwnedRooms, null, "H@");
            payload = Crypto.Proc_3_0_6D2AF0(ownedRoomCount, null, payload);
            Proc_6_244_801E80(socketIndex, payload, 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_105_74AD50(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "@]");
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long maxOwnedRooms = Vb.val(Functions.Proc_10_0_809570("com.server.socket.game.rooms.own.max", 0, 0));
            long ownedRoomCount = Vb.val(MySQL.Proc_5_2_6D4690("SELECT COUNT(id) FROM rooms WHERE id_owner='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "'", 0, 0));
            if (maxOwnedRooms > 0L && ownedRoomCount >= maxOwnedRooms) {
                return;
            }
            LongRef offset = new LongRef(1);
            String roomName = left(Functions.Proc_10_10_80A7F0(readWireString(requestPayload, offset), 0, 0), 25);
            String modelName = left(Functions.Proc_10_11_80A9C0(readWireString(requestPayload, offset), 0, 0), 10);
            if (roomName.isEmpty() || modelName.isEmpty()) {
                return;
            }
            String modelRow = MySQL.Proc_5_2_6D4690("SELECT id,visitors_max FROM models WHERE create_min_level_hc <= '"
                + handlingUserHcLevel(userId) + "' AND type='0' AND name='" + modelName + "' LIMIT 1", 0, 0);
            String[] modelFields = modelRow.split("\t", -1);
            long modelId = Vb.val(handlingField(modelFields, 0));
            long visitorsMax = Vb.val(handlingField(modelFields, 1));
            if (modelId <= 0L) {
                return;
            }
            if (visitorsMax <= 0L) {
                visitorsMax = 25L;
            }
            String escapedUserId = Functions.Proc_10_11_80A9C0(userId, 0, 0);
            MySQL.Proc_5_0_6D3CD0("INSERT INTO rooms(id_owner,name,visitors_max,id_model,timestamp_created) VALUES('"
                + escapedUserId + "','" + Functions.Proc_10_11_80A9C0(roomName, 0, 0) + "','" + visitorsMax
                + "','" + modelId + "',UNIX_TIMESTAMP())", 0, 0);
            long roomId = Vb.val(MySQL.Proc_5_2_6D4690("SELECT MAX(id) FROM rooms", 0, 0));
            if (roomId <= 0L) {
                return;
            }
            Proc_6_106_74B750(Path.of(Functions.applicationPath, "CACHE", "ROOMS", roomId + ".cache").toString(), 0, 0);
            Proc_6_106_74B750(Path.of(Functions.applicationPath, "CACHE", "PATHFINDER", roomId + ".cache").toString(), 0, 0);
            Proc_6_244_801E80(socketIndex, Crypto.Proc_3_0_6D2AF0(roomId, null, "@{") + roomName + '\2', 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_106_74B750(Object... args) {
        try {
            if (args != null && args.length >= 1) {
                Files.deleteIfExists(Path.of(Vb.cStr(args[0])));
            }
        } catch (Exception ignored) {
            // VB6 source suppresses file delete failures.
        }
    }

    public static void Proc_6_107_74B7E0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId) || !handlingUserHasPermission(userId, "fuse_client_staff")) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return;
            }
            String ownerUserId = MySQL.Proc_5_2_6D4690("SELECT id_owner FROM rooms WHERE id='" + roomId + "' LIMIT 1", 0, 0);
            if (ownerUserId.isEmpty()) {
                return;
            }
            long currentPicked = Vb.val(MySQL.Proc_5_2_6D4690(
                "SELECT is_staff_picked FROM rooms WHERE id='" + roomId + "' LIMIT 1", 0, 0));
            long newPicked = currentPicked == 0L ? 1L : 0L;
            long categoryId = Vb.val(Functions.Proc_10_0_809570("com.client.navigator.staff_picked.category.id.default", 0, 0));
            if (categoryId <= 0L) {
                categoryId = 1L;
            }
            MySQL.Proc_5_0_6D3CD0("DELETE FROM rooms_official WHERE id_parent='" + categoryId
                + "' AND id_room='" + roomId + "' LIMIT 1", 0, 0);
            if (newPicked != 0L) {
                long styleId = Vb.val(Functions.Proc_10_0_809570("com.client.navigator.staff_picked.style.default", 0, 0));
                long iconId = Vb.val(Functions.Proc_10_0_809570("com.client.navigator.staff_picked.category.icon.default", 0, 0));
                MySQL.Proc_5_0_6D3CD0("INSERT INTO rooms_official(id_parent,id_room,id_style,id_type,icon) VALUES('"
                    + categoryId + "','" + roomId + "','" + styleId + "','2','" + iconId + "')", 0, 0);
                MySQL.Proc_5_0_6D3CD0("UPDATE users SET amount_staffpicked=amount_staffpicked+1 WHERE id='"
                    + Functions.Proc_10_11_80A9C0(ownerUserId, 0, 0) + "'", 0, 0);
            }
            MySQL.Proc_5_0_6D3CD0("UPDATE rooms SET is_staff_picked='" + newPicked + "' WHERE id='" + roomId + "'", 0, 0);
            String queryTail = "users,rooms,rooms_categories WHERE rooms.id='" + roomId
                + "' AND users.id=rooms.id_owner AND rooms_categories.id=rooms.id_category LIMIT 1";
            Proc_6_247_8027E0(socketIndex, Proc_6_112_74E0C0(queryTail, "GF", 0), 0);
            Proc_6_247_8027E0(socketIndex, Crypto.Proc_3_0_6D2AF0(roomId, null, "GH"), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_108_74D800(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            long maxFavorites = Vb.val(Functions.Proc_10_0_809570("com.server.socket.game.rooms.favourites.max", 30, 0));
            if (maxFavorites <= 0L) {
                maxFavorites = 30L;
            }
            String rowText = MySQL.Proc_5_2_6D4690("SELECT id_room FROM rooms_favourites WHERE id_user='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "' LIMIT " + maxFavorites, 0, 0);
            StringBuilder roomIds = new StringBuilder();
            long roomCount = 0L;
            for (String row : Vb.cStr(rowText).split("\r", -1)) {
                if (!row.isEmpty()) {
                    roomIds.append(Crypto.Proc_3_0_6D2AF0(Vb.val(row), null, ""));
                    roomCount++;
                }
            }
            Proc_6_244_801E80(socketIndex, Crypto.Proc_3_0_6D2AF0(roomCount, null,
                Crypto.Proc_3_0_6D2AF0(maxFavorites, null, "GJ")) + roomIds, 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_109_74DBD0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "@T");
            long roomId = readWireLong(requestPayload, new LongRef(1));
            if (roomId <= 0L) {
                roomId = Vb.val(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            MySQL.Proc_5_0_6D3CD0("DELETE FROM rooms_favourites WHERE id_room='" + roomId + "' AND id_user='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "'", 0, 0);
            Proc_6_244_801E80(socketIndex, Crypto.Proc_3_0_6D2AF0(roomId, null, "GK") + "H", 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_110_74DDA0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "@S");
            long roomId = readWireLong(requestPayload, new LongRef(1));
            if (roomId <= 0L) {
                roomId = Vb.val(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            MySQL.Proc_5_0_6D3CD0("INSERT INTO rooms_favourites(id_user,id_room,timestamp) VALUES('"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "','" + roomId + "',UNIX_TIMESTAMP())", 0, 0);
            Proc_6_244_801E80(socketIndex, Crypto.Proc_3_0_6D2AF0(roomId, null, "GK") + " ", 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_111_74DF70(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            long rankIndex = args != null && args.length >= 4 ? Vb.val(args[3]) : 0L;
            long hcLevel = args != null && args.length >= 5 ? Vb.val(args[4]) : 0L;
            if (rankIndex < 0L) {
                rankIndex = 0L;
            }
            if (rankIndex > 20L) {
                rankIndex = 20L;
            }
            if (hcLevel < 0L) {
                hcLevel = 0L;
            }
            if (hcLevel > 2L) {
                hcLevel = 2L;
            }
            String responsePayload = Licence.roomCategoryCache().payload(rankIndex, hcLevel);
            Proc_6_244_801E80(socketIndex, "C]" + responsePayload, 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static String Proc_6_113_74EE70(Object... args) {
        try {
            if (args == null || args.length < 2) {
                return "";
            }
            String eventQueryTail = Vb.cStr(args[0]);
            String roomQueryTail = Vb.cStr(args[1]);
            String eventRows = "";
            String roomRows = "";
            if (!eventQueryTail.isEmpty()) {
                String timeFormat = Functions.Proc_10_0_809570("com.mysql.format.time", "%H:%i", 0);
                eventRows = MySQL.Proc_5_2_6D4690("SELECT rooms.id,rooms_events.name,users.name,rooms.status_door,"
                    + "rooms.visitors_now,rooms.visitors_max,rooms_events.description,rooms_categories.has_trading,"
                    + "rooms.allow_otherspets,rooms.rate,rooms_events.id_category,rooms.icon,rooms_events.tag_1,"
                    + "rooms_events.tag_2,DATE_FORMAT(FROM_UNIXTIME(rooms_events.timestamp), '" + timeFormat
                    + "') FROM " + eventQueryTail, 0, 0);
            }
            if (!roomQueryTail.isEmpty()) {
                roomRows = MySQL.Proc_5_2_6D4690("SELECT rooms.id,rooms.name,users.name,rooms.status_door,"
                    + "rooms.visitors_now,rooms.visitors_max,rooms.description,rooms_categories.has_trading,"
                    + "rooms.allow_otherspets,rooms.rate,rooms.id_category,rooms.icon,rooms.tag_1,rooms.tag_2 FROM "
                    + roomQueryTail, 0, 0);
            }
            return navigatorCombinedRoomListPayloadFromRows(eventRows, roomRows);
        } catch (Exception ignored) {
            return "";
        }
    }

    public static String Proc_6_114_750550(Object... args) {
        try {
            if (args == null || args.length == 0) {
                return "";
            }
            String queryTail = Vb.cStr(args[0]);
            if (queryTail.isEmpty()) {
                return Crypto.Proc_3_0_6D2AF0(0, null, "");
            }
            String timeFormat = Functions.Proc_10_0_809570("com.mysql.format.time", "%H:%i", 0);
            String rowText = MySQL.Proc_5_2_6D4690("SELECT rooms.id,rooms_events.name,users.name,rooms.status_door,"
                + "rooms.visitors_now,rooms.visitors_max,rooms_events.description,rooms_categories.has_trading,NULL,"
                + "rooms.rate,rooms_events.id_category,rooms.icon,rooms_events.tag_1,rooms_events.tag_2,"
                + "DATE_FORMAT(FROM_UNIXTIME(rooms_events.timestamp), '" + timeFormat + "') FROM " + queryTail, 0, 0);
            return navigatorEventListPayloadFromRows(rowText);
        } catch (Exception ignored) {
            return "";
        }
    }

    public static void Proc_6_115_751220(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            long categoryId = navigatorCategoryIdFromPacket(args, "GC");
            String categoryFilter = categoryId > 1L ? " rooms_events.id_category='" + categoryId + "' AND" : "";
            long limitValue = navigatorListLimit();
            String queryTail = "rooms_events,users,rooms,rooms_categories WHERE" + categoryFilter
                + " rooms.id=rooms_events.id_room AND rooms_categories.id=rooms.id_category AND users.id=rooms.id_owner "
                + "GROUP BY rooms_events.id ORDER BY rooms_events.id ASC LIMIT " + limitValue;
            long randomTree = Functions.Proc_10_4_809CA0(1, Licence.recommendedRooms().count(), 0);
            Proc_6_244_801E80(socketIndex, "GCPC" + categoryId + '\2'
                + Crypto.Proc_3_0_6D2AF0(limitValue, null, "") + Proc_6_112_74E0C0(queryTail, 0, 0)
                + recommendedRoomPayload(randomTree), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_116_751550(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            long categoryId = navigatorCategoryIdFromPacket(args, "GC");
            String categoryFilter = categoryId > 1L ? " rooms.id_category='" + categoryId + "' AND" : "";
            long limitValue = navigatorListLimit();
            String queryTail = "users,rooms,rooms_categories WHERE" + categoryFilter
                + " rooms.visitors_now > 0 AND users.id=rooms.id_owner AND rooms_categories.id=rooms.id_category "
                + "GROUP BY rooms.id ORDER BY rooms.visitors_now DESC LIMIT " + limitValue;
            long randomTree = Functions.Proc_10_4_809CA0(1, Licence.recommendedRooms().count(), 0);
            Proc_6_244_801E80(socketIndex, "GC " + categoryId + '\2'
                + Crypto.Proc_3_0_6D2AF0(limitValue, null, "") + Proc_6_112_74E0C0(queryTail, 0, 0)
                + recommendedRoomPayload(randomTree), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_117_751880(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            long limitValue = navigatorListLimit();
            String queryTail = "friendships,logs_visitedrooms,users,rooms,rooms_categories WHERE friendships.id_user='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0)
                + "' AND logs_visitedrooms.id_user=friendships.id_friend AND logs_visitedrooms.timestamp_left IS NULL "
                + "AND rooms.id=logs_visitedrooms.id_room AND rooms_categories.id=rooms.id_category "
                + "AND users.id=rooms.id_owner GROUP BY rooms.id ORDER BY rooms.id DESC LIMIT " + limitValue;
            Proc_6_244_801E80(socketIndex, "GCQA" + '\2' + Crypto.Proc_3_0_6D2AF0(limitValue, null, "")
                + Proc_6_112_74E0C0(queryTail, 0, 0), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_118_751A80(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            long limitValue = navigatorListLimit();
            String queryTail = "friendships,users,rooms,rooms_categories WHERE friendships.id_user='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0)
                + "' AND users.id=friendships.id_friend AND rooms_categories.id=rooms.id_category "
                + "AND users.id=rooms.id_owner GROUP BY rooms.id ORDER BY rooms.visitors_now DESC LIMIT " + limitValue;
            Proc_6_244_801E80(socketIndex, "GC" + '\0' + '\2' + Crypto.Proc_3_0_6D2AF0(limitValue, null, "")
                + Proc_6_112_74E0C0(queryTail, 0, 0), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_119_751C80(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            long limitValue = navigatorListLimit();
            String queryTail = "rooms_favourites,users,rooms,rooms_categories WHERE rooms_favourites.id_user='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0)
                + "' AND rooms.id=rooms_favourites.id_room AND rooms_categories.id=rooms.id_category "
                + "AND users.id=rooms.id_owner GROUP BY rooms.id ORDER BY rooms.visitors_now DESC LIMIT " + limitValue;
            Proc_6_244_801E80(socketIndex, "GCRA" + '\2' + Crypto.Proc_3_0_6D2AF0(limitValue, null, "")
                + Proc_6_112_74E0C0(queryTail, 0, 0), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_120_751E80(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            long limitValue = navigatorListLimit();
            String queryTail = "logs_visitedrooms,users,rooms,rooms_categories WHERE logs_visitedrooms.id_user='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0)
                + "' AND rooms.id=logs_visitedrooms.id_room AND rooms_categories.id=rooms.id_category "
                + "AND users.id=rooms.id_owner GROUP BY rooms.id ORDER BY rooms.id DESC LIMIT " + limitValue;
            Proc_6_244_801E80(socketIndex, "GCSA" + '\2' + Crypto.Proc_3_0_6D2AF0(limitValue, null, "")
                + Proc_6_112_74E0C0(queryTail, 0, 0), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_121_752080(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            long limitValue = navigatorListLimit();
            String queryTail = "users,rooms,rooms_categories WHERE rooms.id_owner='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0)
                + "' AND rooms_categories.id=rooms.id_category AND users.id=rooms.id_owner "
                + "GROUP BY rooms.id ORDER BY rooms.visitors_now DESC LIMIT " + limitValue;
            Proc_6_244_801E80(socketIndex, "GCQA" + '\2' + Crypto.Proc_3_0_6D2AF0(limitValue, null, "")
                + Proc_6_112_74E0C0(queryTail, 0, 0), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_123_754020(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String rowText = MySQL.Proc_5_2_6D4690(officialNavigatorQuery(), 0, 0);
            Proc_6_244_801E80(socketIndex, "GB" + officialNavigatorRowsPayload(rowText, true), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_124_754D90(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            long limitValue = navigatorListLimit();
            String queryText = "SELECT SUM(get_one) as get_one,get_two FROM (SELECT SUM(rooms.visitors_now) as get_one,"
                + "rooms.tag_1 as get_two FROM rooms,users WHERE rooms.tag_1 != '' AND rooms.visitors_max > 0 "
                + "AND users.id=rooms.id_owner GROUP BY 2 UNION ALL SELECT SUM(rooms.visitors_now) as get_one,"
                + "rooms.tag_2 as get_two FROM rooms,users WHERE rooms.tag_2 != '' AND rooms.visitors_max > 0 "
                + "AND users.id=rooms.id_owner GROUP BY 2) as a GROUP BY get_two ORDER BY 1 DESC LIMIT " + limitValue;
            String rowText = MySQL.Proc_5_2_6D4690(queryText, 0, 0);
            StringBuilder payload = new StringBuilder();
            for (String row : Vb.cStr(rowText).split("\r", -1)) {
                if (!row.isEmpty()) {
                    String[] fields = row.split("\t", -1);
                    payload.append(Crypto.Proc_3_0_6D2AF0(Vb.val(navigatorField(fields, 0)), null, ""));
                    payload.append(navigatorField(fields, 1)).append('\2');
                }
            }
            Proc_6_244_801E80(socketIndex, "GD" + payload, 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_125_755650(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String tagText = Functions.Proc_10_11_80A9C0(navigatorTextFromPacket(args), 0, 0);
            long limitValue = navigatorListLimit();
            String eventQueryTail = "rooms_events,users,rooms,rooms_categories WHERE (rooms_events.name_category='" + tagText
                + "' OR rooms_events.tag_1='" + tagText + "' OR rooms_events.tag_2='" + tagText
                + "') AND rooms.id=rooms_events.id_room AND rooms_categories.id=rooms.id_category "
                + "AND users.id=rooms.id_owner GROUP BY rooms_events.id ORDER BY rooms_events.id ASC LIMIT " + limitValue;
            String roomQueryTail = "users,rooms,rooms_categories WHERE (rooms.tag_1 = '" + tagText + "' OR rooms.tag_2 = '"
                + tagText + "') AND users.id=rooms.id_owner AND rooms_categories.id=rooms.id_category "
                + "GROUP BY rooms.id ORDER BY rooms.visitors_now DESC LIMIT " + limitValue;
            Proc_6_244_801E80(socketIndex, "GCSA" + tagText + '\2' + Crypto.Proc_3_0_6D2AF0(limitValue, null, "")
                + Proc_6_113_74EE70(eventQueryTail, roomQueryTail, 0), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_126_755B40(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            long limitValue = navigatorListLimit();
            String queryTail = "users,rooms,rooms_categories WHERE rooms.rate > 0 AND users.id=rooms.id_owner "
                + "AND rooms_categories.id=rooms.id_category GROUP BY rooms.id ORDER BY rooms.rate DESC LIMIT " + limitValue;
            Proc_6_244_801E80(socketIndex, "GC" + '\b' + '\2' + Crypto.Proc_3_0_6D2AF0(limitValue, null, "")
                + Proc_6_112_74E0C0(queryTail, 0, 0), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_127_755D30(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String searchText = navigatorSearchTerm(navigatorTextFromPacket(args));
            String roomPredicate = searchText.length() > 2
                ? "(users.name LIKE '" + searchText + "%' OR rooms.name LIKE '" + searchText + "%')"
                : "(users.name = '" + searchText + "' OR rooms.name = '" + searchText + "')";
            long limitValue = navigatorListLimit();
            String roomQueryTail = "users,rooms,rooms_categories WHERE " + roomPredicate
                + " AND users.id=rooms.id_owner AND rooms_categories.id=rooms.id_category "
                + "GROUP BY rooms.id ORDER BY rooms.visitors_now DESC LIMIT " + limitValue;
            String eventQueryTail = "rooms_events,users,rooms,rooms_categories WHERE (users.name='" + searchText
                + "' AND rooms_events.id_user=users.id OR rooms_events.name LIKE '" + searchText
                + "%' AND users.id=rooms.id_owner) AND rooms.id=rooms_events.id_room "
                + "AND rooms_categories.id=rooms.id_category GROUP BY rooms_events.id ORDER BY rooms_events.id ASC LIMIT " + limitValue;
            Proc_6_244_801E80(socketIndex, "GCSA" + searchText + '\2' + Crypto.Proc_3_0_6D2AF0(limitValue, null, "")
                + Proc_6_113_74EE70(eventQueryTail, roomQueryTail, 0), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static String Proc_6_128_756190(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "Ad");
            LongRef offset = new LongRef(1);
            long catalogProductId = readWireLong(requestPayload, offset);
            String signText = Functions.Proc_10_10_80A7F0(readWireString(requestPayload, offset), 1, 1);
            if (catalogProductId <= 0L) {
                catalogProductId = Vb.val(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
            }
            if (signText.isEmpty()) {
                signText = Functions.Proc_10_10_80A7F0(Functions.Proc_10_7_80A190(requestPayload, 0, 0), 1, 1);
            }
            if (catalogProductId <= 0L) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (socketIndex <= 0 || userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            String catalogRow = Licence.Proc_9_4_807B90(catalogProductId, 0, 0);
            if (catalogRow.isEmpty()) {
                return "";
            }
            String[] catalogFields = catalogRow.split("\t", -1);
            long productId = Vb.val(handlingField(catalogFields, 2));
            String typeSecondary = handlingField(catalogFields, 4).toLowerCase();
            long creditPrice = Vb.val(handlingField(catalogFields, 7));
            long activityPrice = Vb.val(handlingField(catalogFields, 8));
            long activityType = Vb.val(handlingField(catalogFields, 9));
            long minClubLevel = Vb.val(handlingField(catalogFields, 11));
            if (productId <= 0L) {
                return "";
            }
            if (activityType < 0L || activityType > 4L) {
                activityType = 0L;
            }
            String escapedUserId = Functions.Proc_10_11_80A9C0(userId, 0, 0);
            String userRow = MySQL.Proc_5_2_6D4690("SELECT credits,activitypoints_" + activityType
                + ",level_hc FROM users WHERE id='" + escapedUserId + "' LIMIT 1", 0, 0);
            if (userRow.isEmpty()) {
                return "";
            }
            String[] userFields = userRow.split("\t", -1);
            long userCredits = Vb.val(handlingField(userFields, 0));
            long userActivityPoints = Vb.val(handlingField(userFields, 1));
            long userClubLevel = Vb.val(handlingField(userFields, 2));
            if (minClubLevel > 0L && userClubLevel < minClubLevel) {
                Proc_6_244_801E80(socketIndex, "AD" + Crypto.Proc_3_0_6D2AF0(3, null, ""), 0);
                return "";
            }
            if (userCredits < creditPrice) {
                Proc_6_244_801E80(socketIndex, "AD" + Crypto.Proc_3_0_6D2AF0(1, null, ""), 0);
                return "";
            }
            if (userActivityPoints < activityPrice) {
                Proc_6_244_801E80(socketIndex, "AD" + Crypto.Proc_3_0_6D2AF0(2, null, ""), 0);
                return "";
            }
            long grantedFurnitureId = Vb.val(Proc_6_129_7583C0(socketIndex, catalogProductId, signText));
            if (grantedFurnitureId <= 0L) {
                return "";
            }
            if (creditPrice > 0L || activityPrice > 0L) {
                MySQL.Proc_5_0_6D3CD0("UPDATE users SET credits=credits-" + creditPrice + ",activitypoints_"
                    + activityType + "=activitypoints_" + activityType + "-" + activityPrice + " WHERE id='"
                    + escapedUserId + "'", 0, 0);
                if (creditPrice > 0L) {
                    Functions.Proc_10_16_80C480(userId, 0, 0);
                }
                if (activityPrice > 0L) {
                    Functions.Proc_10_17_80C6B0(userId, activityType, 0);
                }
            }
            String itemClass = "i";
            if (!"products_deals".equals(typeSecondary)
                && Vb.val(DataManager.Proc_8_12_806C30(productId, 0, 0)) == 8L) {
                itemClass = "I";
            }
            String purchasePayload = Crypto.Proc_3_0_6D2AF0(catalogProductId, null, "AC");
            purchasePayload = Crypto.Proc_3_0_6D2AF0(creditPrice, null, purchasePayload);
            purchasePayload = Crypto.Proc_3_0_6D2AF0(activityPrice, null, purchasePayload);
            purchasePayload = Crypto.Proc_3_0_6D2AF0(activityType, null, purchasePayload);
            purchasePayload = Crypto.Proc_3_0_6D2AF0(grantedFurnitureId, null, purchasePayload) + '\2'
                + itemClass + '\2' + "IHH";
            Proc_6_244_801E80(socketIndex, purchasePayload, 0);
            Proc_6_140_769400(socketIndex, "FT", "");
            return purchasePayload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_129_7583C0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            long catalogProductId = args != null && args.length >= 2 ? Vb.val(args[1]) : 0L;
            String signText = args != null && args.length >= 3 ? Vb.cStr(args[2]) : "";
            if (socketIndex <= 0 || catalogProductId <= 0L) {
                return "";
            }
            String catalogRow = Licence.Proc_9_4_807B90(catalogProductId, 0, 0);
            if (catalogRow.isEmpty()) {
                return "";
            }
            String[] catalogFields = catalogRow.split("\t", -1);
            long productId = Vb.val(handlingField(catalogFields, 2));
            String typeSecondary = handlingField(catalogFields, 4).toLowerCase();
            if (productId <= 0L) {
                return "";
            }
            String grantResult = Proc_6_133_760400(socketIndex, catalogProductId, signText);
            if (grantResult.isEmpty()) {
                return "";
            }
            String[] grantedIds = grantResult.split("[,\r]", -1);
            long[] productIds;
            int itemCount = 0;
            if ("products_deals".equals(typeSecondary)) {
                String dealRow = Licence.Proc_9_5_807DF0(productId, 0, 0);
                String[] dealFields = dealRow.split("\t", -1);
                if (dealFields.length >= 2) {
                    dealRow = dealFields[1];
                }
                String[] dealItems = dealRow.replace(',', ';').split(";", -1);
                productIds = new long[dealItems.length];
                for (String dealItem : dealItems) {
                    long dealProductId = Vb.val(dealItem);
                    if (dealProductId > 0L) {
                        productIds[itemCount++] = dealProductId;
                    }
                }
            } else {
                itemCount = Math.max(1, grantedIds.length);
                productIds = new long[itemCount];
                for (int index = 0; index < itemCount; index++) {
                    productIds[index] = productId;
                }
            }
            long firstFurnitureId = 0L;
            for (int index = 0; index < itemCount; index++) {
                long furnitureId = index < grantedIds.length ? Vb.val(grantedIds[index]) : 0L;
                long itemProductId = productIds[index];
                if (furnitureId > 0L && itemProductId > 0L) {
                    if (firstFurnitureId == 0L) {
                        firstFurnitureId = furnitureId;
                    }
                    String itemData = DataManager.Proc_8_12_806C30(itemProductId, 24, 0);
                    if (itemData.isEmpty()) {
                        itemData = DataManager.Proc_8_12_806C30(itemProductId, 4, 0);
                    }
                    long productType = Vb.val(DataManager.Proc_8_12_806C30(itemProductId, 0, 0));
                    Proc_6_244_801E80(socketIndex, "Ab" + Proc_6_138_7678A0(furnitureId, itemProductId, itemData, 0)
                        + '\2', 0);
                    if ("TROPHY_VAR".equalsIgnoreCase(DataManager.Proc_8_12_806C30(itemProductId, 4, 0))) {
                        String trophySign = handlingUserName(handlingUserIdFromSocket(socketIndex)) + '\b'
                            + recyclerRewardSign() + '\b' + signText;
                        MySQL.Proc_5_0_6D3CD0("UPDATE furnitures SET sign='"
                            + Functions.Proc_10_11_80A9C0(Functions.Proc_10_10_80A7F0(trophySign, 1, 1), 0, 0)
                            + "' WHERE id='" + furnitureId + "'", 0, 0);
                    }
                    if (productType == 8L) {
                        Proc_6_244_801E80(socketIndex, "GM" + Crypto.Proc_3_0_6D2AF0(furnitureId, null, "")
                            + Crypto.Proc_3_0_6D2AF0(Vb.val(DataManager.Proc_8_12_806C30(itemProductId, 20, 0)), null, ""), 0);
                    }
                }
            }
            return String.valueOf(firstFurnitureId);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_133_760400(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            long catalogProductId = args != null && args.length >= 2 ? Vb.val(args[1]) : 0L;
            String signText = args != null && args.length >= 3 ? Vb.cStr(args[2]) : "";
            if (socketIndex <= 0 || catalogProductId <= 0L) {
                catalogProductId = args != null && args.length >= 1 ? Vb.val(args[0]) : 0L;
                signText = args != null && args.length >= 2 ? Vb.cStr(args[1]) : "";
                socketIndex = 0;
            }
            String userId = socketIndex > 0 ? handlingUserIdFromSocket(socketIndex) : "";
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            String catalogRow = Licence.Proc_9_4_807B90(catalogProductId, 0, 0);
            if (catalogRow.isEmpty()) {
                return "";
            }
            String[] catalogFields = catalogRow.split("\t", -1);
            long productId = Vb.val(handlingField(catalogFields, 2));
            String typeSecondary = handlingField(catalogFields, 4).toLowerCase();
            long amount = Vb.val(handlingField(catalogFields, 5));
            if (amount <= 0L) {
                amount = 1L;
            }
            String escapedUserId = Functions.Proc_10_11_80A9C0(userId, 0, 0);
            long grantedCount = 0L;
            if ("products_deals".equals(typeSecondary)) {
                String dealRow = Licence.Proc_9_5_807DF0(productId, 0, 0);
                if (dealRow.isEmpty()) {
                    return "";
                }
                String[] dealFields = dealRow.split("\t", -1);
                if (dealFields.length >= 2) {
                    dealRow = dealFields[1];
                }
                for (String dealItem : dealRow.replace(',', ';').split(";", -1)) {
                    long dealProductId = Vb.val(dealItem);
                    if (dealProductId > 0L) {
                        String defaultSign = Functions.Proc_10_10_80A7F0(DataManager.Proc_8_12_806C30(dealProductId, 4, 0), 0, 0);
                        if (defaultSign.isEmpty()) {
                            defaultSign = Functions.Proc_10_10_80A7F0(DataManager.Proc_8_12_806C30(dealProductId, 5, 0), 0, 0);
                        }
                        MySQL.Proc_5_0_6D3CD0("INSERT INTO furnitures(id_product,id_owner,sign,task_owner,task_time,id_ctlgproduct) VALUES('"
                            + dealProductId + "','" + escapedUserId + "','"
                            + Functions.Proc_10_11_80A9C0(defaultSign, 0, 0) + "','" + escapedUserId
                            + "',UNIX_TIMESTAMP(),'" + catalogProductId + "')", 0, 0);
                        grantedCount++;
                    }
                }
            } else {
                String containsClubRow = MySQL.Proc_5_2_6D4690("SELECT months,level FROM products_containshc WHERE id_product='"
                    + catalogProductId + "' LIMIT 1", 0, 0);
                if (containsClubRow.isEmpty()) {
                    containsClubRow = MySQL.Proc_5_2_6D4690("SELECT months,level FROM products_containshc WHERE id_product='"
                        + productId + "' LIMIT 1", 0, 0);
                }
                if (!containsClubRow.isEmpty()) {
                    String[] containsClubFields = containsClubRow.split("\t", -1);
                    long hcMonths = Vb.val(handlingField(containsClubFields, 0));
                    long hcLevel = Vb.val(handlingField(containsClubFields, 1));
                    if (hcLevel <= 0L) {
                        hcLevel = 1L;
                    }
                    Functions.Proc_10_23_80E110(userId, hcLevel, hcMonths, hcMonths * 31L);
                }
                String badgeId = DataManager.Proc_8_12_806C30(productId, 26, 0).toUpperCase();
                if (badgeId.isEmpty()) {
                    badgeId = DataManager.Proc_8_12_806C30(productId, 27, 0).toUpperCase();
                }
                if (badgeId.length() > 2) {
                    String existingBadge = MySQL.Proc_5_2_6D4690("SELECT id_badge FROM users_badges WHERE id_user='"
                        + escapedUserId + "' AND id_badge='" + Functions.Proc_10_11_80A9C0(badgeId, 0, 0)
                        + "' LIMIT 1", 0, 0).toUpperCase();
                    if (!badgeId.equals(existingBadge)) {
                        MySQL.Proc_5_0_6D3CD0("INSERT INTO users_badges(id_user,id_slot,id_badge) VALUES('"
                            + escapedUserId + "','0','" + Functions.Proc_10_11_80A9C0(badgeId, 0, 0) + "')", 0, 0);
                        long badgeRowId = Vb.val(MySQL.Proc_5_2_6D4690("SELECT id FROM users_badges WHERE id_user='"
                            + escapedUserId + "' AND id_badge='" + Functions.Proc_10_11_80A9C0(badgeId, 0, 0)
                            + "' ORDER BY id DESC LIMIT 1", 0, 0));
                        Proc_6_195_7D38D0(userId, 0, 0);
                        Proc_6_193_7D2BB0(socketIndex, "Ce", "");
                        if (badgeRowId > 0L) {
                            Proc_6_143_76BB80(socketIndex, 0, 0);
                        }
                    }
                }
                String defaultSign = signText;
                if (defaultSign.isEmpty()) {
                    defaultSign = Functions.Proc_10_10_80A7F0(DataManager.Proc_8_12_806C30(productId, 4, 0), 0, 0);
                }
                if (defaultSign.isEmpty()) {
                    defaultSign = Functions.Proc_10_10_80A7F0(DataManager.Proc_8_12_806C30(productId, 5, 0), 0, 0);
                }
                for (long itemIndex = 1L; itemIndex <= amount; itemIndex++) {
                    MySQL.Proc_5_0_6D3CD0("INSERT INTO furnitures(id_product,id_owner,sign,task_owner,task_time,id_ctlgproduct) VALUES('"
                        + productId + "','" + escapedUserId + "','"
                        + Functions.Proc_10_11_80A9C0(defaultSign, 0, 0) + "','" + escapedUserId
                        + "',UNIX_TIMESTAMP(),'" + catalogProductId + "')", 0, 0);
                    grantedCount++;
                }
            }
            if (grantedCount <= 0L) {
                return "";
            }
            String newestIds = MySQL.Proc_5_2_6D4690("SELECT id FROM furnitures WHERE id_owner='"
                + escapedUserId + "' ORDER BY id DESC LIMIT " + grantedCount, 0, 0);
            String grantedIds = newestIds.replace('\r', ',');
            long firstGrantedId = Vb.val(grantedIds);
            if (!"products_deals".equals(typeSecondary)
                && Vb.val(DataManager.Proc_8_12_806C30(productId, 0, 0)) == 9L && firstGrantedId > 0L) {
                MySQL.Proc_5_0_6D3CD0("INSERT INTO furnitures_dimmerpresets(id_furni,id_preset,id_state) VALUES('"
                    + firstGrantedId + "','1','2'),('" + firstGrantedId + "','2','1'),('"
                    + firstGrantedId + "','3','1')", 0, 0);
                MySQL.Proc_5_0_6D3CD0("UPDATE furnitures SET sign='1,1,1,#000000,166' WHERE id='"
                    + firstGrantedId + "'", 0, 0);
            }
            return grantedIds;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_130_75B770(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "G[");
            String requestedSprite = Functions.Proc_10_7_80A190(requestPayload, 0, 0);
            if (requestedSprite.isEmpty()) {
                requestedSprite = Functions.Proc_10_6_809F10(requestPayload, 0, 0);
            }
            if (requestedSprite.isEmpty()) {
                requestedSprite = requestPayload.replace("\2", "").replace("\0", "").trim();
            }
            if (requestedSprite.isEmpty()) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (socketIndex <= 0 || userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long catalogProductId = Vb.val(MySQL.Proc_5_2_6D4690("SELECT id FROM catalog_products WHERE sprite='"
                + Functions.Proc_10_11_80A9C0(requestedSprite, 0, 0) + "' LIMIT 1", 0, 0));
            if (catalogProductId <= 0L) {
                return "";
            }
            GiftSettings.ClubGift gift = Licence.giftSettings().clubGiftByCatalogProductId(catalogProductId);
            long productId = gift.productId();
            long requiredDays = gift.requiredDays();
            if (productId <= 0L) {
                return "";
            }
            String rowText = MySQL.Proc_5_2_6D4690("SELECT level_hc,hc_days,hc2_days,hc_presents,"
                + "ROUND((UNIX_TIMESTAMP()-hc_startperiod)/60/60/24,0) FROM users WHERE id='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "' LIMIT 1", 0, 0);
            if (rowText.isEmpty()) {
                return "";
            }
            String[] userFields = rowText.split("\t", -1);
            long hcLevel = Vb.val(handlingField(userFields, 0));
            long hcDays = Vb.val(handlingField(userFields, 1));
            long vipDays = Vb.val(handlingField(userFields, 2));
            long presentsAvailable = Vb.val(handlingField(userFields, 3));
            long daysSinceStart = Vb.val(handlingField(userFields, 4));
            long activeDays = (hcLevel > 1L ? vipDays : hcDays) - daysSinceStart;
            if (activeDays < 0L) {
                activeDays = 0L;
            }
            if (presentsAvailable <= 0L || activeDays < requiredDays) {
                return "";
            }
            String itemData = DataManager.Proc_8_12_806C30(productId, 24, 0);
            String escapedUserId = Functions.Proc_10_11_80A9C0(userId, 0, 0);
            MySQL.Proc_5_0_6D3CD0("INSERT INTO furnitures(id_product,id_ctlgproduct,id_owner,task_owner,task_time,position_r,sign) VALUES('"
                + productId + "','" + catalogProductId + "','" + escapedUserId + "','" + escapedUserId
                + "',UNIX_TIMESTAMP(),'0','" + Functions.Proc_10_11_80A9C0(itemData, 0, 0) + "')", 0, 0);
            long insertedFurnitureId = Vb.val(MySQL.Proc_5_2_6D4690("SELECT id FROM furnitures WHERE id_owner='"
                + escapedUserId + "' AND id_product='" + productId + "' ORDER BY id DESC LIMIT 1", 0, 0));
            String itemClass = Vb.val(DataManager.Proc_8_12_806C30(productId, 0, 0)) == 9L ? "I" : "i";
            String responsePayload = Crypto.Proc_3_0_6D2AF0(productId, null, "AC")
                + DataManager.Proc_8_12_806C30(productId, 24, 0) + '\2'
                + "HHHI" + itemClass + '\2';
            responsePayload = Crypto.Proc_3_0_6D2AF0(insertedFurnitureId, null, responsePayload) + '\2' + "IH";
            Proc_6_244_801E80(socketIndex, responsePayload, 0);
            MySQL.Proc_5_0_6D3CD0("UPDATE users SET hc_presents=hc_presents-1 WHERE id='" + escapedUserId + "'", 0, 0);
            Proc_6_140_769400(socketIndex, "FT", "");
            return responsePayload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static void Proc_6_131_75C700(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (socketIndex <= 0 || userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            String rowText = MySQL.Proc_5_2_6D4690("SELECT level_hc,hc_days,hc2_days,hc_presents,"
                + "ROUND((UNIX_TIMESTAMP()-hc_startperiod)/60/60/24,0) FROM users WHERE id='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "' LIMIT 1", 0, 0);
            String[] userFields = rowText.split("\t", -1);
            long hcLevel = Vb.val(handlingField(userFields, 0));
            long hcDays = Vb.val(handlingField(userFields, 1));
            long vipDays = Vb.val(handlingField(userFields, 2));
            long presentsAvailable = Vb.val(handlingField(userFields, 3));
            long daysSinceStart = Vb.val(handlingField(userFields, 4));
            long activeDays = (hcLevel > 1L ? vipDays : hcDays) - daysSinceStart;
            if (activeDays < 0L) {
                activeDays = 0L;
            }
            long statusCount = 0L;
            StringBuilder statusPayload = new StringBuilder();
            GiftSettings giftSettings = Licence.giftSettings();
            for (GiftSettings.ClubGift gift : giftSettings.clubGifts()) {
                long canClaim = presentsAvailable > 0L && activeDays >= gift.requiredDays() ? 1L : 0L;
                statusPayload.append(Crypto.Proc_3_0_6D2AF0(gift.catalogProductId(), null, ""));
                statusPayload.append(Crypto.Proc_3_0_6D2AF0(gift.productId(), null, ""));
                statusPayload.append(Crypto.Proc_3_0_6D2AF0(gift.requiredDays(), null, ""));
                statusPayload.append(Crypto.Proc_3_0_6D2AF0(canClaim, null, ""));
                statusPayload.append('H');
                statusCount++;
            }
            String payload = Crypto.Proc_3_0_6D2AF0(presentsAvailable, null, "IoM")
                + giftSettings.clubGiftPayload()
                + Crypto.Proc_3_0_6D2AF0(statusCount, null, "") + statusPayload;
            Proc_6_244_801E80(socketIndex, payload, 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static String Proc_6_132_75D4A0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "GX");
            LongRef offset = new LongRef(1);
            long catalogProductId = readWireLong(requestPayload, offset);
            long expectedProductId = readWireLong(requestPayload, offset);
            String recipientName = Functions.Proc_10_10_80A7F0(readWireString(requestPayload, offset), 1, 1);
            String giftMessage = Functions.Proc_10_10_80A7F0(readWireString(requestPayload, offset), 1, 1);
            if (giftMessage.length() > 142) {
                giftMessage = giftMessage.substring(0, 142);
            }
            long wrapProductId = readWireLong(requestPayload, offset);
            long ribbonId = readWireLong(requestPayload, offset);
            long colorId = readWireLong(requestPayload, offset);
            if (catalogProductId <= 0L) {
                catalogProductId = Vb.val(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
            }
            if (recipientName.isEmpty()) {
                recipientName = Functions.Proc_10_10_80A7F0(Functions.Proc_10_7_80A190(requestPayload, 0, 0), 1, 1);
            }
            if (catalogProductId <= 0L || recipientName.isEmpty()) {
                return "";
            }
            String senderUserId = handlingUserIdFromSocket(socketIndex);
            if (socketIndex <= 0 || senderUserId.isEmpty() || "0".equals(senderUserId)) {
                return "";
            }
            String catalogRow = Licence.Proc_9_4_807B90(catalogProductId, 0, 0);
            if (catalogRow.isEmpty()) {
                return "";
            }
            String[] catalogFields = catalogRow.split("\t", -1);
            long productId = Vb.val(handlingField(catalogFields, 2));
            long creditPrice = Vb.val(handlingField(catalogFields, 7));
            long activityPrice = Vb.val(handlingField(catalogFields, 8));
            long activityType = Vb.val(handlingField(catalogFields, 9));
            long allowGifts = Vb.val(handlingField(catalogFields, 10));
            long minClubLevel = Vb.val(handlingField(catalogFields, 11));
            if (productId <= 0L || allowGifts == 0L || expectedProductId > 0L && expectedProductId != productId) {
                return "";
            }
            if (activityType < 0L || activityType > 4L) {
                activityType = 0L;
            }
            if (Vb.val(Functions.Proc_10_0_809570("com.client.catalog.gifts.wrap.enabled", 0, 0)) != 0L) {
                long wrapPrice = Vb.val(Functions.Proc_10_0_809570("com.client.catalog.gifts.wrap.price", 0, 0));
                if (wrapProductId <= 0L) {
                    wrapProductId = Vb.val(MySQL.Proc_5_2_6D4690(
                        "SELECT id FROM products WHERE sprite LIKE 'present_wrap%' ORDER BY id ASC LIMIT 1", 0, 0));
                }
                if (wrapProductId > 0L && !Licence.giftSettings().containsGiftWrapProduct(wrapProductId)) {
                    return "";
                }
                creditPrice += wrapPrice;
            }
            String escapedSenderId = Functions.Proc_10_11_80A9C0(senderUserId, 0, 0);
            String userRow = MySQL.Proc_5_2_6D4690("SELECT credits,activitypoints_" + activityType
                + ",level_hc FROM users WHERE id='" + escapedSenderId + "' LIMIT 1", 0, 0);
            if (userRow.isEmpty()) {
                return "";
            }
            String[] userFields = userRow.split("\t", -1);
            long userCredits = Vb.val(handlingField(userFields, 0));
            long userActivityPoints = Vb.val(handlingField(userFields, 1));
            long userClubLevel = Vb.val(handlingField(userFields, 2));
            if (minClubLevel > 0L && userClubLevel < minClubLevel) {
                Proc_6_244_801E80(socketIndex, "AD" + Crypto.Proc_3_0_6D2AF0(3, null, ""), 0);
                return "";
            }
            if (userCredits < creditPrice) {
                Proc_6_244_801E80(socketIndex, "AD" + Crypto.Proc_3_0_6D2AF0(1, null, ""), 0);
                return "";
            }
            if (userActivityPoints < activityPrice) {
                Proc_6_244_801E80(socketIndex, "AD" + Crypto.Proc_3_0_6D2AF0(2, null, ""), 0);
                return "";
            }
            String recipientUserId = String.valueOf(Vb.val(MySQL.Proc_5_2_6D4690("SELECT id FROM users WHERE name='"
                + Functions.Proc_10_11_80A9C0(recipientName, 0, 0) + "' LIMIT 1", 0, 0)));
            if (recipientUserId.isEmpty() || "0".equals(recipientUserId)) {
                recipientUserId = senderUserId;
            }
            long grantedFurnitureId = Vb.val(Proc_6_133_760400(socketIndex, catalogProductId, giftMessage));
            if (grantedFurnitureId <= 0L) {
                return "";
            }
            String productSign = DataManager.Proc_8_12_806C30(productId, 4, 0);
            if ("TROPHY_VAR".equalsIgnoreCase(productSign)) {
                productSign = handlingUserName(senderUserId) + '\b' + recyclerRewardSign() + '\b' + giftMessage;
            }
            long giftSecondary = colorId * 1000L + ribbonId;
            String escapedRecipientId = Functions.Proc_10_11_80A9C0(recipientUserId, 0, 0);
            MySQL.Proc_5_0_6D3CD0("UPDATE furnitures SET sign_extra='"
                + Functions.Proc_10_11_80A9C0(Functions.Proc_10_10_80A7F0(giftMessage, 0, 0), 0, 0)
                + "',sign='" + Functions.Proc_10_11_80A9C0(Functions.Proc_10_10_80A7F0(productSign, 0, 0), 0, 0)
                + "',id_owner='" + escapedRecipientId + "',id_destination='" + catalogProductId + "',id_secondary='"
                + giftSecondary + "' WHERE id='" + grantedFurnitureId + "'", 0, 0);
            if (creditPrice > 0L || activityPrice > 0L) {
                MySQL.Proc_5_0_6D3CD0("UPDATE users SET credits=credits-" + creditPrice + ",activitypoints_"
                    + activityType + "=activitypoints_" + activityType + "-" + activityPrice + " WHERE id='"
                    + escapedSenderId + "'", 0, 0);
                if (creditPrice > 0L) {
                    Functions.Proc_10_16_80C480(senderUserId, 0, 0);
                }
                if (activityPrice > 0L) {
                    Functions.Proc_10_17_80C6B0(senderUserId, activityType, 0);
                }
            }
            MySQL.Proc_5_0_6D3CD0("UPDATE users SET gifts_given=gifts_given+1 WHERE id='" + escapedSenderId + "'", 0, 0);
            if (!recipientUserId.equals(senderUserId)) {
                MySQL.Proc_5_0_6D3CD0("UPDATE users SET gifts_received=gifts_received+1 WHERE id='" + escapedRecipientId + "'", 0, 0);
                Proc_6_205_7D9780(socketIndex, 6);
            }
            String purchasePayload = Crypto.Proc_3_0_6D2AF0(catalogProductId, null, "AC")
                + Licence.Proc_9_1_8072B0(catalogProductId, 0, 0) + '\2';
            purchasePayload = Crypto.Proc_3_0_6D2AF0(creditPrice, null, purchasePayload);
            purchasePayload = Crypto.Proc_3_0_6D2AF0(activityPrice, null, purchasePayload);
            purchasePayload = Crypto.Proc_3_0_6D2AF0(activityType, null, purchasePayload);
            purchasePayload = Crypto.Proc_3_0_6D2AF0(grantedFurnitureId, null, purchasePayload) + '\2' + "i" + '\2' + "IH";
            Proc_6_244_801E80(socketIndex, purchasePayload, 0);
            long recipientSocket = handlingSocketFromUserId(recipientUserId);
            if (recipientSocket > 0L) {
                Proc_6_244_801E80((int) recipientSocket,
                    "Ab" + Proc_6_138_7678A0(grantedFurnitureId, productId, productSign, giftSecondary) + '\2', 0);
                Proc_6_205_7D9780((int) recipientSocket, 7);
            }
            return purchasePayload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static void Proc_6_134_765B90(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "oV");
            long itemId = Vb.val(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
            if (itemId <= 0L) {
                itemId = readWireLong(requestPayload, new LongRef(1));
            }
            long itemType = Vb.val(Licence.Proc_9_1_8072B0(itemId, 9, 0));
            long giftEnabled = itemType == 1L ? Vb.val(Functions.Proc_10_0_809570("com.client.catalog.gifts.enabled", 0, 0)) : 0L;
            String responsePayload = Crypto.Proc_3_0_6D2AF0(itemId, null, "In");
            responsePayload = Crypto.Proc_3_0_6D2AF0(giftEnabled, null, responsePayload) + '\2';
            Proc_6_244_801E80(socketIndex, responsePayload, 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_135_765D80(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String defaultPayload = "0" + Crypto.Proc_3_0_6D2AF0(
                Vb.val(Functions.Proc_10_0_809570("com.client.catalog.gifts.wrap.enabled", 0, 0)), null, "Il");
            long giftWrapPrice = Vb.val(Functions.Proc_10_0_809570("com.client.catalog.gifts.wrap.price", defaultPayload, 0));
            Proc_6_244_801E80(socketIndex,
                Crypto.Proc_3_0_6D2AF0(giftWrapPrice, null, "") + Licence.giftSettings().giftWrapPayload(), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_136_765F10(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingPacketPayload(args);
            if (requestPayload.length() >= 3) {
                requestPayload = requestPayload.substring(2);
            }
            long pageId = Vb.val(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
            if (pageId <= 0L) {
                pageId = readWireLong(requestPayload, new LongRef(1));
            }
            String pagePayload = Licence.catalogPages().pagePayload(pageId);
            if (!pagePayload.isEmpty()) {
                Proc_6_244_801E80(socketIndex, "A\u007f" + Crypto.Proc_3_0_6D2AF0(pageId, null, "") + pagePayload, 0);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_137_766470(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "BA");
            String voucherCode = requestPayload.startsWith("@")
                ? readWireString(requestPayload, new LongRef(1))
                : requestPayload;
            voucherCode = voucherCode.replace(' ', '0');
            if (voucherCode.length() != 8) {
                Proc_6_244_801E80(socketIndex, "CU" + voucherCode + '\2', 0);
                return;
            }
            String voucherRows = MySQL.Proc_5_2_6D4690("SELECT contain_product,contain_credits,contain_shells FROM vouchers WHERE name='"
                + Functions.Proc_10_11_80A9C0(voucherCode, 0, 0) + "' LIMIT 1", 0, 0);
            String[] fields = voucherRows.split("\t", -1);
            if (voucherRows.isEmpty() || fields.length < 3) {
                Proc_6_244_801E80(socketIndex, "CU" + voucherCode + '\2', 0);
                return;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty()) {
                Proc_6_244_801E80(socketIndex, "CU" + voucherCode + '\2', 0);
                return;
            }
            String productSprite = handlingField(fields, 0);
            long creditsValue = Vb.val(handlingField(fields, 1));
            long shellsValue = Vb.val(handlingField(fields, 2));
            String rewardPayload = "";
            if (productSprite.length() > 2) {
                long productId = Vb.val(MySQL.Proc_5_2_6D4690("SELECT id_product FROM catalog_products WHERE sprite='"
                    + Functions.Proc_10_11_80A9C0(productSprite, 0, 0) + "' LIMIT 1", 0, 0));
                if (productId != 0L) {
                    rewardPayload = DataManager.Proc_8_12_806C30(productId, 13, 0) + '\2'
                        + DataManager.Proc_8_12_806C30(productId, 14, 0) + '\2';
                }
            }
            String escapedUserId = Functions.Proc_10_11_80A9C0(userId, 0, 0);
            if (creditsValue != 0L) {
                MySQL.Proc_5_0_6D3CD0("UPDATE users SET credits=credits+" + creditsValue + " WHERE id='" + escapedUserId + "'", 0, 0);
                Functions.Proc_10_16_80C480(userId, 0, 0);
            }
            if (shellsValue != 0L) {
                MySQL.Proc_5_0_6D3CD0("UPDATE users SET activitypoints_0=activitypoints_0+" + shellsValue
                    + " WHERE id='" + escapedUserId + "'", 0, 0);
                Functions.Proc_10_17_80C6B0(userId, 0, 0);
            }
            MySQL.Proc_5_0_6D3CD0("DELETE FROM vouchers WHERE name='"
                + Functions.Proc_10_11_80A9C0(voucherCode, 0, 0) + "' LIMIT 1", 0, 0);
            Proc_6_244_801E80(socketIndex, "CT" + rewardPayload, 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_139_768100(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "AB");
            String userId = handlingUserIdFromSocket(socketIndex);
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (userId.isEmpty() || roomId <= 0L) {
                return;
            }
            long furnitureId = stickyFurnitureIdFromPayload(requestPayload);
            if (furnitureId <= 0L) {
                return;
            }
            String rowText = MySQL.Proc_5_2_6D4690("SELECT id,id_product,sign FROM furnitures WHERE id_owner='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "' AND id='" + furnitureId
                + "' AND id_room IS NULL LIMIT 1", 0, 0);
            if (rowText.isEmpty()) {
                return;
            }
            String[] fields = rowText.split("\t", -1);
            long productId = Vb.val(navigatorField(fields, 1));
            String decoValue = navigatorField(fields, 2);
            String[] productFields = Licence.Proc_9_3_807930(productId, 0, 0).split("\t", -1);
            long productType = Vb.val(navigatorField(productFields, 1));
            String decoName;
            String decoColumn;
            if (productType == 2L) {
                decoName = "wallpaper";
                decoColumn = "id_wallpaper";
            } else if (productType == 3L) {
                decoName = "floor";
                decoColumn = "id_floor";
            } else if (productType == 4L) {
                decoName = "landscape";
                decoColumn = "id_landscape";
            } else {
                return;
            }
            if (decoValue.isEmpty() || "0".equals(decoValue)) {
                decoValue = navigatorField(productFields, 20);
            }
            if (decoValue.isEmpty()) {
                decoValue = navigatorField(productFields, 18);
            }
            if (decoValue.isEmpty()) {
                return;
            }
            Proc_6_247_8027E0(socketIndex, "@n" + decoName + '\2' + decoValue + '\2', 0);
            MySQL.Proc_5_0_6D3CD0("UPDATE rooms SET " + decoColumn + "='"
                + Functions.Proc_10_11_80A9C0(decoValue, 0, 0) + "' WHERE id='" + roomId + "'", 0, 0);
            Proc_6_244_801E80(socketIndex, Crypto.Proc_3_0_6D2AF0(furnitureId, null, "Ac"), 0);
            MySQL.Proc_5_0_6D3CD0("DELETE FROM furnitures WHERE id='" + furnitureId + "' LIMIT 1", 0, 0);
            Proc_6_140_769400(socketIndex, "FT", "");
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_140_769400(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty()) {
                return;
            }
            String rowText = MySQL.Proc_5_2_6D4690("SELECT id,id_product,sign,id_secondary FROM furnitures WHERE id_owner='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "' AND id_room IS NULL LIMIT 1000", 0, 0);
            InventoryPayloads payloads = inventoryPayloadsFromRows(rowText);
            Proc_6_244_801E80(socketIndex, '\2' + Crypto.Proc_3_0_6D2AF0(payloads.regularCount, null, "BLS" + '\2' + "II")
                + payloads.regularPayload, 0);
            Proc_6_244_801E80(socketIndex, Crypto.Proc_3_0_6D2AF0(payloads.iconCount, null, "BL" + '\2' + "II")
                + payloads.iconPayload, 0);
            String payload = Crypto.Proc_3_0_6D2AF0(0, null,
                Crypto.Proc_3_0_6D2AF0(0, null,
                    Crypto.Proc_3_0_6D2AF0(0, null,
                        Crypto.Proc_3_0_6D2AF0(0, null, "Id") + "HHH"))) + "H";
            Proc_6_244_801E80(socketIndex, payload, 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static String Proc_6_141_76A670(Object... args) {
        return handlingFloorFurnitureMove(args, "A[", false);
    }

    public static String Proc_6_142_76B310(Object... args) {
        return handlingFloorFurnitureMove(args, "rv", true);
    }

    public static void Proc_6_143_76BB80(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty()) {
                return;
            }
            String rowText = MySQL.Proc_5_2_6D4690(
                "SELECT activitypoints_1,activitypoints_2,activitypoints_3,activitypoints_4 FROM users WHERE id='"
                    + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "' LIMIT 1",
                0,
                0);
            if (rowText.isEmpty()) {
                return;
            }
            Proc_6_244_801E80(socketIndex, activityPointBalancePayload(rowText), 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_144_76BE70(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String packetPayload = handlingPacketPayload(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (userId.isEmpty() || "0".equals(userId) || roomId <= 0L) {
                return;
            }
            long furnitureId = pickupFurnitureIdFromPayload(packetPayload);
            if (furnitureId <= 0L) {
                return;
            }
            String rowText = MySQL.Proc_5_2_6D4690("SELECT id_product,id_owner FROM furnitures WHERE id='"
                + furnitureId + "' AND id_room='" + roomId + "' LIMIT 1", 0, 0);
            if (rowText.isEmpty()) {
                return;
            }
            String[] fields = rowText.split("\t", -1);
            long productId = Vb.val(handlingField(fields, 0));
            String ownerId = String.valueOf((long) Vb.val(handlingField(fields, 1)));
            if (productId <= 0L || ownerId.isEmpty() || "0".equals(ownerId)) {
                return;
            }
            boolean canPickUpAny = handlingUserHasPermission(userId, "fuse_pick_up_any_furni");
            if (!ownerId.equals(userId) && !handlingUserOwnsRoom(userId, roomId) && !canPickUpAny) {
                return;
            }
            if (!handlingUserHasRoomRight(userId, roomId) && !ownerId.equals(userId) && !canPickUpAny) {
                return;
            }
            MySQL.Proc_5_0_6D3CD0("UPDATE furnitures SET id_room=NULL,position_x=NULL,position_y=NULL,position_z=NULL,"
                + "position_r='0',position_wall=NULL,id_owner='" + Functions.Proc_10_11_80A9C0(userId, 0, 0)
                + "',task_owner='" + Functions.Proc_10_11_80A9C0(userId, 0, 0)
                + "',task_time=UNIX_TIMESTAMP() WHERE id='" + furnitureId + "' LIMIT 1", 0, 0);
            Proc_6_247_8027E0(socketIndex, "A^" + furnitureId + '\2', 0);
            Proc_6_106_74B750(Path.of(Functions.applicationPath, "CACHE", "ROOMS", roomId + ".cache").toString(), 0, 0);
            Proc_6_106_74B750(Path.of(Functions.applicationPath, "CACHE", "PATHFINDER", roomId + ".cache").toString(), 0, 0);
            Proc_6_140_769400(socketIndex, "FT", "");
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_145_76CA20(Object... args) {
        try {
            int socketIndex = 0;
            long roomId = 0L;
            long furnitureId = 0L;
            if (args != null && args.length >= 3) {
                socketIndex = (int) Vb.val(args[0]);
                roomId = Vb.val(args[1]);
                furnitureId = Vb.val(args[2]);
            } else if (args != null && args.length >= 2) {
                roomId = Vb.val(args[0]);
                furnitureId = Vb.val(args[1]);
            } else if (args != null && args.length >= 1) {
                furnitureId = Vb.val(args[0]);
            }
            if (roomId <= 0L && socketIndex > 0) {
                String userId = handlingUserIdFromSocket(socketIndex);
                if (!userId.isEmpty() && !"0".equals(userId)) {
                    roomId = handlingCurrentRoomId(socketIndex, userId);
                }
            }
            if (furnitureId <= 0L) {
                return;
            }
            FurnitureCacheState state = trackFurnitureCacheMarker(
                Licence.global_008291F8,
                Licence.global_008291FC,
                Licence.global_00829310,
                roomId,
                furnitureId);
            Licence.global_008291F8 = state.pendingRoomCache;
            Licence.global_008291FC = state.pendingFurnitureCache;
            Licence.global_00829310 = state.representedRoomCache;
            if (roomId > 0L) {
                Proc_6_106_74B750(Path.of(Functions.applicationPath, "CACHE", "ROOMS", roomId + ".cache").toString(), 0, 0);
                Proc_6_106_74B750(Path.of(Functions.applicationPath, "CACHE", "PATHFINDER", roomId + ".cache").toString(), 0, 0);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_146_76D300(Object... args) {
        try {
            int socketIndex = 0;
            long furnitureId = 0L;
            long productId = 0L;
            if (args != null && args.length >= 3) {
                socketIndex = (int) Vb.val(args[0]);
                furnitureId = Vb.val(args[1]);
                productId = Vb.val(args[2]);
            } else if (args != null && args.length >= 2) {
                furnitureId = Vb.val(args[0]);
                productId = Vb.val(args[1]);
            } else if (args != null && args.length >= 1) {
                furnitureId = Vb.val(args[0]);
            }
            if (furnitureId <= 0L) {
                return;
            }
            if (productId <= 0L) {
                productId = Vb.val(MySQL.Proc_5_2_6D4690("SELECT id_product FROM furnitures WHERE id='"
                    + furnitureId + "' LIMIT 1", 0, 0));
            }
            String rowText = MySQL.Proc_5_2_6D4690("SELECT id_room FROM furnitures WHERE id='"
                + furnitureId + "' LIMIT 1", 0, 0);
            long roomId = 0L;
            if (!rowText.isEmpty()) {
                roomId = Vb.val(handlingField(rowText.split("\t", -1), 0));
            }
            FurnitureCacheState state = removeFurnitureCacheMarker(
                Licence.global_008291F8,
                Licence.global_008291FC,
                Licence.global_00829310,
                furnitureId);
            Licence.global_008291F8 = state.pendingRoomCache;
            Licence.global_008291FC = state.pendingFurnitureCache;
            Licence.global_00829310 = state.representedRoomCache;
            if (roomId <= 0L && socketIndex > 0) {
                String userId = handlingUserIdFromSocket(socketIndex);
                if (!userId.isEmpty() && !"0".equals(userId)) {
                    roomId = handlingCurrentRoomId(socketIndex, userId);
                }
            }
            if (roomId > 0L) {
                Proc_6_106_74B750(Path.of(Functions.applicationPath, "CACHE", "ROOMS", roomId + ".cache").toString(), 0, 0);
                Proc_6_106_74B750(Path.of(Functions.applicationPath, "CACHE", "PATHFINDER", roomId + ".cache").toString(), 0, 0);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static long Proc_6_147_76E910(Object... args) {
        try {
            long roomId = 0L;
            long positionX;
            long positionY;
            if (args != null && args.length >= 3) {
                roomId = Vb.val(args[0]);
                positionX = Vb.val(args[1]);
                positionY = Vb.val(args[2]);
            } else if (args != null && args.length >= 2) {
                positionX = Vb.val(args[0]);
                positionY = Vb.val(args[1]);
            } else {
                return 0L;
            }
            String whereText = "position_x='" + positionX + "' AND position_y='" + positionY
                + "' AND position_wall IS NULL";
            if (roomId > 0L) {
                whereText = "id_room='" + roomId + "' AND " + whereText;
            }
            String rowText = MySQL.Proc_5_2_6D4690("SELECT id,id_room,id_product,sign FROM furnitures WHERE "
                + whereText + " LIMIT 250", 0, 0);
            if (rowText.isEmpty()) {
                return 0L;
            }
            long refreshCount = 0L;
            for (String row : rowText.split("\r", -1)) {
                if (!row.isEmpty()) {
                    String[] fields = row.split("\t", -1);
                    long furnitureId = Vb.val(handlingField(fields, 0));
                    long rowRoomId = roomId > 0L ? roomId : Vb.val(handlingField(fields, 1));
                    long productId = Vb.val(handlingField(fields, 2));
                    String stateText = handlingField(fields, 3);
                    if (furnitureId > 0L && rowRoomId > 0L && productId > 0L) {
                        String productAction = DataManager.Proc_8_12_806C30(productId, 7, 0).toLowerCase();
                        String productSprite = DataManager.Proc_8_12_806C30(productId, 17, 0).toLowerCase();
                        if (productSprite.isEmpty()) {
                            productSprite = DataManager.Proc_8_12_806C30(productId, 18, 0).toLowerCase();
                        }
                        if (productAction.isEmpty() || productAction.contains("switch") || productAction.contains("click")
                            || productAction.contains("score") || productSprite.contains("score") || productSprite.contains("dice")) {
                            long stateValue = Vb.val(stateText);
                            Proc_6_151_78AC20(rowRoomId, furnitureId, stateValue);
                            Proc_6_246_8024C0(rowRoomId, furnitureStatePayload(furnitureId, stateValue), 0);
                            refreshCount++;
                        }
                    }
                }
            }
            return refreshCount;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return 0L;
        }
    }

    public static void Proc_6_148_7756D0(Object... args) {
        try {
            if (args == null || args.length < 3) {
                return;
            }
            int socketIndex = handlingSocketIndex(args);
            long productId = Vb.val(args[1]);
            long furnitureId = Vb.val(args[2]);
            if (socketIndex <= 0 || productId <= 0L || furnitureId <= 0L) {
                return;
            }
            String[] productFields = Licence.Proc_9_3_807930(productId, 0, 0).split("\t", -1);
            long hasCharge = Vb.val(navigatorField(productFields, 34));
            if (hasCharge == 0L) {
                return;
            }
            long chargeSize = Vb.val(navigatorField(productFields, 34));
            long chargePriceCredits = Vb.val(navigatorField(productFields, 35));
            long chargePricePoints = Vb.val(navigatorField(productFields, 36));
            long chargePointType = Vb.val(navigatorField(productFields, 37));
            Path chargePath = Path.of(Functions.applicationPath, "cache", "items_charges", furnitureId + ".cache");
            long currentCharges = Vb.val(Proc_6_239_7FC170(chargePath.toString(), 0, 0));
            if (currentCharges < 1L) {
                String payload = Crypto.Proc_3_0_6D2AF0(furnitureId, null, "Iu")
                    + Crypto.Proc_3_0_6D2AF0(currentCharges, null, "")
                    + Crypto.Proc_3_0_6D2AF0(chargeSize, null, "")
                    + Crypto.Proc_3_0_6D2AF0(chargePriceCredits, null, "")
                    + Crypto.Proc_3_0_6D2AF0(chargePricePoints, null, "")
                    + Crypto.Proc_3_0_6D2AF0(chargePointType, null, "");
                Proc_6_244_801E80(socketIndex, payload, 0);
            } else {
                DataManager.Proc_8_10_8068E0(chargePath.toString(), String.valueOf(currentCharges - 1L), 0);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_149_775C10(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            if (socketIndex <= 0) {
                return;
            }
            String requestPayload = handlingRequestPayload(args, "Ch");
            long furnitureId = stickyFurnitureIdFromPayload(requestPayload);
            if (furnitureId <= 0L) {
                return;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return;
            }
            String rowText = MySQL.Proc_5_2_6D4690("SELECT id_product,sign FROM furnitures WHERE id='"
                + furnitureId + "' AND id_room='" + roomId + "' AND position_wall IS NULL LIMIT 1", 0, 0);
            if (rowText.isEmpty()) {
                return;
            }
            String[] fields = rowText.split("\t", -1);
            long productId = Vb.val(handlingField(fields, 0));
            String signText = handlingField(fields, 1);
            if (productId <= 0L) {
                return;
            }
            long productType = Vb.val(DataManager.Proc_8_12_806C30(productId, 0, 0));
            if (productType == 9L) {
                return;
            }
            String productSprite = DataManager.Proc_8_12_806C30(productId, 17, 0).toLowerCase();
            if (productSprite.isEmpty()) {
                productSprite = DataManager.Proc_8_12_806C30(productId, 18, 0).toLowerCase();
            }
            long currentState = Vb.val(signText);
            long maxState = Vb.val(DataManager.Proc_8_12_806C30(productId, 12, 0));
            long nextState = nextFurnitureState(productSprite, currentState, maxState);
            MySQL.Proc_5_0_6D3CD0("UPDATE furnitures SET sign='" + nextState + "',task_owner='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "',task_time=UNIX_TIMESTAMP() WHERE id='"
                + furnitureId + "' AND id_room='" + roomId + "' LIMIT 1", 0, 0);
            Proc_6_151_78AC20(roomId, furnitureId, nextState);
            String payload = furnitureStatePayload(furnitureId, nextState);
            Proc_6_247_8027E0(socketIndex, payload, 0);
            if (Vb.val(DataManager.Proc_8_12_806C30(productId, 34, 0)) != 0L) {
                Proc_6_148_7756D0(socketIndex, productId, furnitureId);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static Object Proc_6_150_777FA0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            if (socketIndex <= 0) {
                return "";
            }
            String requestPayload = handlingRequestPayload(args, "FH");
            LongRef offset = new LongRef(1);
            long furnitureId = readWireLong(requestPayload, offset);
            if (furnitureId <= 0L) {
                furnitureId = Vb.val(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
            }
            if (furnitureId <= 0L) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return "";
            }
            String rowText = MySQL.Proc_5_2_6D4690("SELECT id_product FROM furnitures WHERE id='"
                + furnitureId + "' AND id_room='" + roomId + "' AND position_wall IS NULL LIMIT 1", 0, 0);
            if (rowText.isEmpty()) {
                return "";
            }
            long productId = Vb.val(handlingField(rowText.split("\t", -1), 0));
            if (productId <= 0L) {
                return "";
            }
            String packageRow = MySQL.Proc_5_2_6D4690("SELECT id_product,type_secondary,id_contain,type_check FROM packages WHERE id_product='"
                + productId + "' LIMIT 1", 0, 0);
            if (!packageRow.isEmpty()) {
                String[] packageFields = packageRow.split("\t", -1);
                String packageType = handlingField(packageFields, 1).toLowerCase();
                long containedId = Vb.val(handlingField(packageFields, 2));
                if ("packages_pets".equals(packageType) && containedId > 0L) {
                    return Proc_6_86_73B0D0(socketIndex, "FH", requestPayload);
                } else if (!packageType.isEmpty()) {
                    Proc_6_244_801E80(socketIndex, Crypto.Proc_3_0_6D2AF0(furnitureId, null,
                        Crypto.Proc_3_0_6D2AF0(productId, null, "L}" + packageType + '\2')) + "H", 0);
                    return furnitureId;
                }
            }
            Proc_6_149_775C10(socketIndex, "Ch", requestPayload);
            return furnitureId;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static void Proc_6_151_78AC20(Object... args) {
        try {
            long roomId = 0L;
            long furnitureId = 0L;
            long stateValue = 0L;
            if (args != null && args.length >= 3) {
                roomId = Vb.val(args[0]);
                furnitureId = Vb.val(args[1]);
                stateValue = Vb.val(args[2]);
            } else if (args != null && args.length >= 2) {
                roomId = Vb.val(args[0]);
                furnitureId = Vb.val(args[1]);
            } else if (args != null && args.length >= 1) {
                furnitureId = Vb.val(args[0]);
            }
            if (roomId <= 0L || furnitureId <= 0L) {
                return;
            }
            FurnitureStateCache state = representedFurnitureStateCache(
                Licence.global_008291F8,
                Licence.global_008291FC,
                Licence.global_00829310,
                roomId,
                furnitureId,
                stateValue);
            Licence.global_008291F8 = state.pendingRoomCache;
            Licence.global_008291FC = state.pendingFurnitureCache;
            Licence.global_00829310 = state.representedRoomCache;
            Proc_6_106_74B750(Path.of(Functions.applicationPath, "CACHE", "ROOMS", roomId + ".cache").toString(), 0, 0);
            Proc_6_106_74B750(Path.of(Functions.applicationPath, "CACHE", "PATHFINDER", roomId + ".cache").toString(), 0, 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_152_78C2F0(Object... args) {
        handlingRepresentedFurnitureStateWrite(args);
    }

    public static void Proc_6_153_78D980(Object... args) {
        handlingRepresentedFurnitureStateWrite(args);
    }

    public static String Proc_6_154_78F040(Object... args) {
        try {
            if (args == null || args.length == 0) {
                return "";
            }
            long furnitureId = Vb.val(args[0]);
            long productId = args.length >= 2 ? Vb.val(args[1]) : 0L;
            if (furnitureId <= 0L) {
                return "";
            }
            String rowText = MySQL.Proc_5_2_6D4690("SELECT id_room,id_product,sign FROM furnitures WHERE id='"
                + furnitureId + "' LIMIT 1", 0, 0);
            if (rowText.isEmpty()) {
                return "";
            }
            String[] fields = rowText.split("\t", -1);
            long roomId = Vb.val(handlingField(fields, 0));
            if (productId <= 0L) {
                productId = Vb.val(handlingField(fields, 1));
            }
            String signText = handlingField(fields, 2);
            if (roomId <= 0L || productId <= 0L) {
                return "";
            }
            long productType = Vb.val(DataManager.Proc_8_12_806C30(productId, 0, 0));
            String productSprite = DataManager.Proc_8_12_806C30(productId, 17, 0);
            if (productSprite.isEmpty()) {
                productSprite = DataManager.Proc_8_12_806C30(productId, 18, 0);
            }
            long stateValue = Vb.val(signText);
            String lowerSprite = productSprite.toLowerCase();
            if ((lowerSprite.startsWith("bb_score_") || lowerSprite.startsWith("es_score_")) && stateValue < 0L) {
                stateValue = 0L;
            }
            Proc_6_151_78AC20(roomId, furnitureId, stateValue);
            String payload = furnitureStatePayload(furnitureId, stateValue);
            Proc_6_246_8024C0(roomId, payload, 0);
            if (productType == 11L || lowerSprite.contains("soundmachine") || lowerSprite.contains("jukebox")) {
                Proc_6_224_7EF5A0(0, roomId, furnitureId);
            }
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static void Proc_6_155_795C90(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "AC");
            long furnitureId = stickyFurnitureIdFromPayload(requestPayload);
            if (socketIndex <= 0 || furnitureId <= 0L) {
                return;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (userId.isEmpty() || "0".equals(userId) || roomId <= 0L) {
                return;
            }
            String rowText = MySQL.Proc_5_2_6D4690("SELECT id_product,id_owner FROM furnitures WHERE id='"
                + furnitureId + "' AND id_room='" + roomId + "' LIMIT 1", 0, 0);
            if (rowText.isEmpty()) {
                return;
            }
            String[] fields = rowText.split("\t", -1);
            long productId = Vb.val(handlingField(fields, 0));
            String ownerId = String.valueOf((long) Vb.val(handlingField(fields, 1)));
            if (productId <= 0L) {
                return;
            }
            boolean canPickUpAny = handlingUserHasPermission(userId, "fuse_pick_up_any_furni");
            if (!ownerId.equals(userId) && !handlingUserOwnsRoom(userId, roomId) && !canPickUpAny) {
                return;
            }
            if (!handlingUserHasRoomRight(userId, roomId) && !ownerId.equals(userId) && !canPickUpAny) {
                return;
            }
            if (!ownerId.equals(userId)) {
                String sessionId = handlingUserSessionId(userId);
                MySQL.Proc_5_1_6D4110("INSERT INTO logs_moderation(id_type,id_user,id_target,id_target_2,timestamp,message,id_session) VALUES('8','"
                    + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "','" + roomId + "','" + furnitureId
                    + "',UNIX_TIMESTAMP(),'','" + Functions.Proc_10_11_80A9C0(sessionId, 0, 0) + "')", 0, 0);
            }
            Proc_6_146_76D300(socketIndex, furnitureId, productId);
            MySQL.Proc_5_0_6D3CD0("UPDATE furnitures SET id_room=NULL,position_x=NULL,position_y=NULL,position_z=NULL,"
                + "position_r='0',position_wall=NULL,id_owner='" + Functions.Proc_10_11_80A9C0(userId, 0, 0)
                + "',task_owner='" + Functions.Proc_10_11_80A9C0(userId, 0, 0)
                + "',task_time=UNIX_TIMESTAMP() WHERE id='" + furnitureId + "' AND id_room='" + roomId + "' LIMIT 1", 0, 0);
            Proc_6_244_801E80(socketIndex, Crypto.Proc_3_0_6D2AF0(furnitureId, null, "Ac"), 0);
            Proc_6_247_8027E0(socketIndex, "A^" + furnitureId + '\2', 0);
            Proc_6_106_74B750(Path.of(Functions.applicationPath, "CACHE", "ROOMS", roomId + ".cache").toString(), 0, 0);
            Proc_6_106_74B750(Path.of(Functions.applicationPath, "CACHE", "PATHFINDER", roomId + ".cache").toString(), 0, 0);
            Proc_6_140_769400(socketIndex, "FT", "");
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void Proc_6_157_7974B0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            if (socketIndex <= 0) {
                return;
            }
            String wallPayload = args != null && args.length >= 2 ? Vb.cStr(args[1]) : "";
            if (wallPayload.startsWith("rv")) {
                wallPayload = wallPayload.substring(2);
            }
            String[] itemFields = new String[0];
            if (args != null && args.length >= 3) {
                Object itemArg = args[2];
                if (itemArg instanceof String[]) {
                    itemFields = (String[]) itemArg;
                } else if (itemArg instanceof Object[]) {
                    Object[] values = (Object[]) itemArg;
                    itemFields = new String[values.length];
                    for (int index = 0; index < values.length; index++) {
                        itemFields[index] = Vb.cStr(values[index]);
                    }
                } else {
                    itemFields = Vb.cStr(itemArg).split("\t", -1);
                }
            }
            long furnitureId = Vb.val(handlingField(itemFields, 1));
            long productId = Vb.val(handlingField(itemFields, 0));
            if (furnitureId <= 0L) {
                furnitureId = Vb.val(Functions.Proc_10_6_809F10(wallPayload, 0, 0));
            }
            if (furnitureId <= 0L) {
                return;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L || (!handlingUserHasRoomRight(userId, roomId)
                && !handlingUserHasPermission(userId, "fuse_pick_up_any_furni"))) {
                return;
            }
            if (productId <= 0L) {
                String itemRow = MySQL.Proc_5_2_6D4690("SELECT id_product,id,sign,id_secondary,id_destination FROM furnitures WHERE id='"
                    + furnitureId + "' AND id_owner='" + Functions.Proc_10_11_80A9C0(userId, 0, 0)
                    + "' AND id_room IS NULL LIMIT 1", 0, 0);
                if (!itemRow.isEmpty()) {
                    itemFields = itemRow.split("\t", -1);
                }
                productId = Vb.val(handlingField(itemFields, 0));
            }
            if (productId <= 0L || Vb.val(DataManager.Proc_8_12_806C30(productId, 0, 0)) != 9L) {
                return;
            }
            WallPlacement placement = new WallPlacement();
            if (!wallPlacementFromPayload(wallPayload, placement)) {
                return;
            }
            String wallPosition = Functions.Proc_10_11_80A9C0((":w=" + placement.wallX + "," + placement.wallY
                + " l=" + placement.localX + "," + placement.localY).toLowerCase(), 0, 0);
            MySQL.Proc_5_0_6D3CD0("UPDATE furnitures SET position_wall='" + wallPosition + "',id_room='" + roomId
                + "',id_owner=NULL,task_owner='" + Functions.Proc_10_11_80A9C0(userId, 0, 0)
                + "',task_time=UNIX_TIMESTAMP() WHERE id='" + furnitureId + "' AND id_owner='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "' AND id_room IS NULL LIMIT 1", 0, 0);
            Proc_6_244_801E80(socketIndex, Crypto.Proc_3_0_6D2AF0(furnitureId, null, "Ac"), 0);
            String payload = Proc_6_156_7972B0(furnitureId, productId, wallPosition,
                handlingField(itemFields, 2), Vb.val(handlingField(itemFields, 3)));
            if (!payload.isEmpty()) {
                Proc_6_247_8027E0(socketIndex, "AS" + payload, 0);
            }
            Proc_6_106_74B750(Path.of(Functions.applicationPath, "CACHE", "ROOMS", roomId + ".cache").toString(), 0, 0);
            Proc_6_106_74B750(Path.of(Functions.applicationPath, "CACHE", "PATHFINDER", roomId + ".cache").toString(), 0, 0);
            Proc_6_140_769400(socketIndex, "FT", "");
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static long Proc_6_158_7987C0(Object... args) {
        try {
            if (args == null || args.length < 3) {
                return 0L;
            }
            long furnitureId = Vb.val(args[0]);
            long positionX = Vb.val(args[1]);
            long positionY = Vb.val(args[2]);
            long footprintX = args.length >= 4 ? Vb.val(args[3]) : 1L;
            long footprintY = args.length >= 5 ? Vb.val(args[4]) : 1L;
            if (footprintX <= 0L) {
                footprintX = 1L;
            }
            if (footprintY <= 0L) {
                footprintY = 1L;
            }
            String rowText = MySQL.Proc_5_2_6D4690("SELECT id_room FROM furnitures WHERE id='"
                + furnitureId + "' LIMIT 1", 0, 0);
            long roomId;
            if (!rowText.isEmpty()) {
                roomId = Vb.val(rowText);
            } else {
                roomId = furnitureId;
                furnitureId = 0L;
            }
            if (roomId <= 0L || positionX < 0L || positionY < 0L) {
                return 0L;
            }
            rowText = MySQL.Proc_5_2_6D4690("SELECT models.map,rooms.allow_walkthrough,rooms.id_slot FROM rooms,models WHERE rooms.id='"
                + roomId + "' AND models.id=rooms.id_model LIMIT 1", 0, 0);
            if (rowText.isEmpty()) {
                return 0L;
            }
            String[] fields = rowText.split("\t", -1);
            String modelMap = handlingField(fields, 0).replace('\n', '\r');
            while (modelMap.contains("\r\r")) {
                modelMap = modelMap.replace("\r\r", "\r");
            }
            if (modelMap.endsWith("\r")) {
                modelMap = modelMap.substring(0, modelMap.length() - 1);
            }
            String[] mapRows = modelMap.split("\r", -1);
            long allowWalkthrough = Vb.val(handlingField(fields, 1));
            long roomSlot = Vb.val(handlingField(fields, 2));
            for (long tileY = positionY; tileY <= positionY + footprintY - 1L; tileY++) {
                if (tileY < 0L || tileY >= mapRows.length) {
                    return 0L;
                }
                String mapRow = mapRows[(int) tileY];
                for (long tileX = positionX; tileX <= positionX + footprintX - 1L; tileX++) {
                    if (tileX < 0L || tileX + 1L > mapRow.length()) {
                        return 0L;
                    }
                    String mapCell = mapRow.substring((int) tileX, (int) tileX + 1).toLowerCase();
                    if (mapCell.isEmpty() || "x".equals(mapCell)) {
                        return 0L;
                    }
                    long occupiedCount = Vb.val(MySQL.Proc_5_2_6D4690("SELECT COUNT(*) FROM furnitures WHERE id_room='"
                        + roomId + "' AND position_wall IS NULL AND position_x='" + tileX + "' AND position_y='"
                        + tileY + "' AND id<>'" + furnitureId + "' LIMIT 1", 0, 0));
                    if (occupiedCount > 0L) {
                        return 0L;
                    }
                    occupiedCount = Vb.val(MySQL.Proc_5_2_6D4690("SELECT COUNT(*) FROM bots WHERE id_room='"
                        + roomId + "' AND position_x='" + tileX + "' AND position_y='" + tileY + "' LIMIT 1", 0, 0));
                    if (occupiedCount > 0L) {
                        return 0L;
                    }
                    if (allowWalkthrough == 0L && roomSlot > 0L) {
                        String occupantText = MySQL.Proc_5_2_6D4690("SELECT id FROM logs_visitedrooms WHERE id_room='"
                            + roomId + "' AND timestamp_left IS NULL LIMIT 250", 0, 0);
                        for (String occupantRow : occupantText.split("\r", -1)) {
                            long occupantRoomUserIndex = Vb.val(occupantRow);
                            if (occupantRoomUserIndex > 0L) {
                                MovementPosition movementPosition = representedMovementPosition(
                                    Licence.global_00829310, roomSlot, occupantRoomUserIndex);
                                if (movementPosition.found && movementPosition.positionX == tileX
                                    && movementPosition.positionY == tileY) {
                                    return 0L;
                                }
                            }
                        }
                    }
                }
            }
            return 1L;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return 0L;
        }
    }

    public static String Proc_6_159_79FCD0(Object... args) {
        try {
            String requestPayload = handlingRequestPayload(args, "AI");
            if (requestPayload.isEmpty()) {
                return "";
            }
            return Proc_6_141_76A670(handlingSocketIndex(args), "A[", "A[" + requestPayload);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_160_7A71A0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            long firstId = args != null && args.length >= 2 ? Vb.val(args[1]) : 0L;
            long secondId = args != null && args.length >= 3 ? Vb.val(args[2]) : 0L;
            long furnitureId = 0L;
            long productId = 0L;
            String rowText = "";
            if (secondId > 0L) {
                rowText = MySQL.Proc_5_2_6D4690("SELECT id_room,id_product,sign FROM furnitures WHERE id='"
                    + secondId + "' LIMIT 1", 0, 0);
                if (!rowText.isEmpty()) {
                    furnitureId = secondId;
                    productId = firstId;
                }
            }
            if (rowText.isEmpty() && firstId > 0L) {
                rowText = MySQL.Proc_5_2_6D4690("SELECT id_room,id_product,sign FROM furnitures WHERE id='"
                    + firstId + "' LIMIT 1", 0, 0);
                if (!rowText.isEmpty()) {
                    furnitureId = firstId;
                }
            }
            if (rowText.isEmpty() && secondId > 0L) {
                rowText = MySQL.Proc_5_2_6D4690("SELECT id_room,id_product,sign FROM furnitures WHERE id_product='"
                    + secondId + "' ORDER BY id DESC LIMIT 1", 0, 0);
                if (!rowText.isEmpty()) {
                    productId = secondId;
                }
            }
            if (rowText.isEmpty()) {
                return "";
            }
            String[] fields = rowText.split("\t", -1);
            long roomId = Vb.val(handlingField(fields, 0));
            if (productId <= 0L) {
                productId = Vb.val(handlingField(fields, 1));
            }
            String signText = handlingField(fields, 2);
            if (roomId <= 0L || productId <= 0L) {
                return "";
            }
            if (furnitureId <= 0L) {
                furnitureId = Vb.val(MySQL.Proc_5_2_6D4690("SELECT id FROM furnitures WHERE id_room='"
                    + roomId + "' AND id_product='" + productId + "' ORDER BY id DESC LIMIT 1", 0, 0));
            }
            if (furnitureId <= 0L) {
                return "";
            }
            String productSprite = DataManager.Proc_8_12_806C30(productId, 17, 0).toLowerCase();
            if (productSprite.isEmpty()) {
                productSprite = DataManager.Proc_8_12_806C30(productId, 18, 0).toLowerCase();
            }
            if (productSprite.startsWith("bb_score_") || productSprite.startsWith("es_score_")) {
                long stateValue = Vb.val(signText);
                long maxState = Vb.val(DataManager.Proc_8_12_806C30(productId, 12, 0));
                if (maxState <= 0L) {
                    maxState = 99L;
                }
                if (stateValue < 0L) {
                    stateValue = 0L;
                }
                if (stateValue > maxState) {
                    stateValue = maxState;
                }
                if (!String.valueOf(stateValue).equals(signText)) {
                    MySQL.Proc_5_0_6D3CD0("UPDATE furnitures SET sign='" + stateValue + "' WHERE id='"
                        + furnitureId + "' LIMIT 1", 0, 0);
                }
            }
            return Proc_6_154_78F040(furnitureId, productId);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static void Proc_6_162_7B3310(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String dateFormat = Functions.Proc_10_0_809570("com.system.format.date", "DAQBHHIIKHJHPAHQA", 0);
            if (dateFormat.isEmpty()) {
                dateFormat = "DAQBHHIIKHJHPAHQA";
            }
            Proc_6_244_801E80(socketIndex,
                "0" + dateFormat + '\2' + "SAHPB" + "http://www.alpha-series.com/" + '\2' + "QBH", 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static String Proc_6_163_7B3480(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String packetPayload = args != null && args.length >= 2 ? Vb.cStr(args[1]) : "";
            String loginTicket = handlingLoginTicketFromPayload(packetPayload);
            if (loginTicket.isEmpty() || "NULL".equalsIgnoreCase(loginTicket)) {
                if (socketIndex > 0) {
                    Proc_6_243_7FFEB0(socketIndex, 0, 0);
                }
                return "";
            }
            String escapedTicket = Functions.Proc_10_11_80A9C0(loginTicket, 0, 0);
            String userRow = MySQL.Proc_5_2_6D4690("SELECT id,name,level,figure,motto,gender,activitypoints_0,credits,level_hc,"
                + "hc_days,hc2_days,hc_presents,id_socket,nickname,homeroom,respect_amount,scratch_amount,language,hc_periods,"
                + "hc2_periods,respect_received,respect_given,ROUND(online_time/60,0),ROUND((UNIX_TIMESTAMP()-create_time)/60/60/24,0),"
                + "gifts_given,gifts_received,ROUND((UNIX_TIMESTAMP()-update_time)/60/60/24,0),hc_startperiod,"
                + "ROUND((UNIX_TIMESTAMP()-hc_startperiod)/60/60/24,0),merge_name,tutorial_name,tutorial_clothes,tutorial_guide,"
                + "login_session,achievement_score,activitypoints_1,id_favgroup,privileges_extra,accept_friends,activitypoints_2,"
                + "amount_staffpicked,email_validated,email,settings_sound,online_time,activitypoints_3,activitypoints_4,ip_last "
                + "FROM users WHERE login_ticket = '" + escapedTicket + "' LIMIT 1", 0, 0);
            if (userRow.isEmpty()) {
                if (socketIndex > 0) {
                    Proc_6_243_7FFEB0(socketIndex, 0, 0);
                }
                return "";
            }
            String[] fields = userRow.split("\t", -1);
            String userId = String.valueOf((long) Vb.val(handlingField(fields, 0)));
            if (userId.isEmpty() || "0".equals(userId)) {
                if (socketIndex > 0) {
                    Proc_6_243_7FFEB0(socketIndex, 0, 0);
                }
                return "";
            }
            int oldSocketIndex = (int) Vb.val(handlingField(fields, 12));
            if (oldSocketIndex > 0 && oldSocketIndex != socketIndex) {
                Proc_6_243_7FFEB0(oldSocketIndex, 0, 0);
            }
            String userName = handlingField(fields, 1);
            long rankIndex = Vb.val(handlingField(fields, 2));
            long creditsValue = Vb.val(handlingField(fields, 7));
            long homeRoomId = Vb.val(handlingField(fields, 14));
            long updateAgeDays = Vb.val(handlingField(fields, 26));
            long emailValidated = Vb.val(handlingField(fields, 41));
            long[] pointValues = new long[]{
                Vb.val(handlingField(fields, 6)),
                Vb.val(handlingField(fields, 35)),
                Vb.val(handlingField(fields, 39)),
                Vb.val(handlingField(fields, 45)),
                Vb.val(handlingField(fields, 46))
            };
            String escapedUserId = Functions.Proc_10_11_80A9C0(userId, 0, 0);
            MySQL.Proc_5_0_6D3CD0("UPDATE users SET login_ticket=null,id_socket = '" + socketIndex
                + "' WHERE id = '" + escapedUserId + "'", 0, 0);
            if (updateAgeDays > 0L) {
                MySQL.Proc_5_0_6D3CD0("UPDATE users SET respect_amount='5',scratch_amount='5',update_time=UNIX_TIMESTAMP() WHERE id='"
                    + escapedUserId + "' LIMIT 1", 0, 0);
            }
            handlingStoreSocketSession(socketIndex, userId + '\2' + socketIndex + '\2' + userName + '\2'
                + rankIndex + '\2' + loginTicket + '\2');
            Proc_6_244_801E80(socketIndex, "@C", 0);
            Proc_6_20_6E88E0(socketIndex, 0, 0);
            Proc_6_244_801E80(socketIndex, "@F" + creditsValue + ".0" + '\2', 0);
            for (int pointIndex = 0; pointIndex <= 4; pointIndex++) {
                Proc_6_244_801E80(socketIndex, handlingLoginActivityPointPayload(pointIndex, pointValues[pointIndex]), 0);
            }
            if (homeRoomId > 0L) {
                Proc_6_244_801E80(socketIndex, Crypto.Proc_3_0_6D2AF0(homeRoomId, null, "GG"), 0);
            }
            if (emailValidated > 0L) {
                Proc_6_244_801E80(socketIndex, Crypto.Proc_3_0_6D2AF0(emailValidated, null, "DX"), 0);
            }
            Proc_6_244_801E80(socketIndex, "@a" + "com.server.socket.location" + '\2' + "invalid.location" + '\2', 0);
            if (Vb.val(Functions.Proc_10_0_809570("com.client.motd.message.enabled", 0, 0)) != 0L) {
                String motdMessage = Functions.Proc_10_0_809570("com.client.motd.message", "", 0).replace("\\n", "\n");
                if (!motdMessage.isEmpty()) {
                    Proc_6_244_801E80(socketIndex, Console.Proc_2_4_6D28B0(motdMessage.length(), 0, 0)
                        + " " + motdMessage + '\2', 0);
                }
            }
            Proc_6_244_801E80(socketIndex, "Cd" + Crypto.Proc_3_0_6D2AF0(Vb.val(userId), null, "")
                + Proc_6_195_7D38D0(userId, 0, 0), 0);
            Proc_6_244_801E80(socketIndex, "E^" + Crypto.Proc_3_0_6D2AF0(Vb.val(userId), null, "")
                + Proc_6_196_7D3ED0(userId, 0, 0), 0);
            long favouriteGroupId = Vb.val(handlingField(fields, 36));
            if (favouriteGroupId > 0L) {
                String groupRow = MySQL.Proc_5_3_6D4CF0("SELECT group_name,group_description,id_badge,id_room FROM users_groups WHERE id='"
                    + favouriteGroupId + "' LIMIT 1", 0, 0);
                if (!groupRow.isEmpty()) {
                    String[] groupFields = groupRow.split("\t", -1);
                    String groupPayload = Crypto.Proc_3_0_6D2AF0(favouriteGroupId, null, "Dt")
                        + handlingField(groupFields, 0) + '\2'
                        + handlingField(groupFields, 1) + '\2'
                        + handlingField(groupFields, 2) + '\2'
                        + Crypto.Proc_3_0_6D2AF0(Vb.val(handlingField(groupFields, 3)), null, "") + "H";
                    Proc_6_244_801E80(socketIndex, groupPayload, 0);
                }
            }
            return userId;
        } catch (Exception ignored) {
            int socketIndex = handlingSocketIndex(args);
            if (socketIndex > 0) {
                Proc_6_243_7FFEB0(socketIndex, 0, 0);
            }
            return "";
        }
    }

    public static String Proc_6_165_7BE0B0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            int targetSocketIndex = args != null && args.length >= 2 ? (int) Vb.val(args[1]) : 0;
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            String summaryPayload = messengerFriendSummaryPayload(userId, 1L);
            if (summaryPayload.isEmpty()) {
                return "";
            }
            String notifyPayload = "@MHIH" + summaryPayload;
            if (targetSocketIndex > 0) {
                if (Guardian.Proc_11_2_821390(targetSocketIndex, 0, 0) == 1L) {
                    Proc_6_244_801E80(targetSocketIndex, notifyPayload, 0);
                }
            } else {
                String rowText = MySQL.Proc_5_2_6D4690("SELECT users.id_socket FROM friendships,users WHERE friendships.has_accept='1' AND friendships.id_user='"
                    + Functions.Proc_10_11_80A9C0(userId, 0, 0)
                    + "' AND users.id=friendships.id_friend AND users.id_socket>'0'", 0, 0);
                for (String row : rowText.split("\r", -1)) {
                    if (!row.isEmpty()) {
                        String[] fields = row.split("\t", -1);
                        targetSocketIndex = (int) Vb.val(handlingField(fields, 0));
                        if (targetSocketIndex > 0 && Guardian.Proc_11_2_821390(targetSocketIndex, 0, 0) == 1L) {
                            Proc_6_244_801E80(targetSocketIndex, notifyPayload, 0);
                        }
                    }
                }
            }
            return notifyPayload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static long Proc_6_170_7C1100(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            FriendTargetList targets = friendDeleteTargetsFromPayload(handlingPacketPayload(args));
            if (targets.deleteAllPending) {
                MySQL.Proc_5_0_6D3CD0("DELETE FROM friendships WHERE id_user='"
                    + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "' AND has_accept='0' LIMIT 75", 0, 0);
                return 1L;
            }
            if (targets.targetList.isEmpty()) {
                return 0L;
            }
            MySQL.Proc_5_0_6D3CD0("DELETE FROM friendships WHERE id_user='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0)
                + "' AND has_accept='0' AND id_friend IN (" + targets.targetList + ") LIMIT 75", 0, 0);
            return 1L;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return 0L;
        }
    }

    public static String Proc_6_171_7C1520(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "@h");
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            LongRef offset = new LongRef(1);
            long removeCount = readWireLong(requestPayload, offset);
            if (removeCount <= 0L) {
                return "";
            }
            removeCount = Math.min(removeCount, 75L);
            StringBuilder targetList = new StringBuilder();
            StringBuilder removedIdsPayload = new StringBuilder();
            long removedCount = 0L;
            for (long removeIndex = 1L; removeIndex <= removeCount; removeIndex++) {
                long targetUserId = readWireLong(requestPayload, offset);
                String targetId = String.valueOf(targetUserId);
                if (targetUserId > 0L && !targetId.equals(userId)
                    && !("," + targetList + ",").contains("," + targetId + ",")) {
                    String friendshipRow = MySQL.Proc_5_2_6D4690("SELECT id_user FROM friendships WHERE has_accept='1' AND ((id_friend='"
                        + Functions.Proc_10_11_80A9C0(targetId, 0, 0) + "' AND id_user='"
                        + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "') OR (id_user='"
                        + Functions.Proc_10_11_80A9C0(targetId, 0, 0) + "' AND id_friend='"
                        + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "')) LIMIT 1", 0, 0);
                    if (!friendshipRow.isEmpty()) {
                        if (targetList.length() > 0) {
                            targetList.append(',');
                        }
                        targetList.append(targetId);
                        removedIdsPayload.append(messengerRemovedIdPayload(targetUserId));
                        removedCount++;
                        int targetSocketIndex = handlingSocketFromUserId(targetId);
                        if (targetSocketIndex > 0) {
                            Proc_6_244_801E80(targetSocketIndex, "@MMIM" + userId, 0);
                        }
                    }
                }
            }
            if (targetList.length() == 0) {
                return "";
            }
            MySQL.Proc_5_0_6D3CD0("DELETE FROM friendships WHERE has_accept='1' AND ((id_user='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "' AND id_friend IN (" + targetList
                + ")) OR (id_friend='" + Functions.Proc_10_11_80A9C0(userId, 0, 0)
                + "' AND id_user IN (" + targetList + "))) LIMIT 150", 0, 0);
            String callerPayload = messengerRemoveFriendsPayload(removedIdsPayload.toString(), removedCount);
            Proc_6_244_801E80(socketIndex, callerPayload, 0);
            return callerPayload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_172_7C25B0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "@i");
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            String searchText = Functions.Proc_10_7_80A190(requestPayload, 0, 0);
            if (searchText.isEmpty()) {
                LongRef offset = new LongRef(1);
                searchText = readWireString(requestPayload, offset);
            }
            searchText = searchText.trim().toLowerCase();
            if (searchText.isEmpty()) {
                return "";
            }
            String escapedSearch = Functions.Proc_10_11_80A9C0(searchText, 0, 0).toLowerCase();
            String whereClause = searchText.length() > 3
                ? "LOWER(name) LIKE '" + escapedSearch + "%'"
                : "LOWER(name)='" + escapedSearch + "'";
            String dateFormat = Functions.Proc_10_0_809570("com.mysql.format.date", "%d-%m-%Y", 0);
            String timeFormat = Functions.Proc_10_0_809570("com.mysql.format.time", "%H:%i", 0);
            String rowText = MySQL.Proc_5_2_6D4690("SELECT id,name,id_socket,figure,motto,nickname,DATE_FORMAT(FROM_UNIXTIME(lastonline_time), '"
                + Functions.Proc_10_11_80A9C0(dateFormat + " " + timeFormat, 0, 0)
                + "') FROM users WHERE " + whereClause + " LIMIT 50", 0, 0);
            long friendCount = 0L;
            long otherCount = 0L;
            StringBuilder friendPayload = new StringBuilder();
            StringBuilder otherPayload = new StringBuilder();
            for (String row : rowText.split("\r", -1)) {
                if (!row.isEmpty()) {
                    String[] fields = row.split("\t", -1);
                    if (fields.length >= 7) {
                        String targetUserId = handlingField(fields, 0);
                        if (!targetUserId.equals(userId)) {
                            long isOnline = Vb.val(handlingField(fields, 2)) > 0L ? 1L : 0L;
                            String resultPayload = messengerSearchResultPayload(
                                targetUserId,
                                handlingField(fields, 1),
                                handlingField(fields, 3),
                                handlingField(fields, 4),
                                handlingField(fields, 5),
                                handlingField(fields, 6),
                                isOnline);
                            boolean isFriend = !MySQL.Proc_5_2_6D4690("SELECT id_user FROM friendships WHERE has_accept='1' AND ((id_user='"
                                + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "' AND id_friend='"
                                + Functions.Proc_10_11_80A9C0(targetUserId, 0, 0) + "') OR (id_user='"
                                + Functions.Proc_10_11_80A9C0(targetUserId, 0, 0) + "' AND id_friend='"
                                + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "')) LIMIT 1", 0, 0).isEmpty();
                            if (isFriend) {
                                friendPayload.append(resultPayload);
                                friendCount++;
                            } else {
                                otherPayload.append(resultPayload);
                                otherCount++;
                            }
                        }
                    }
                }
            }
            String resultPayload = Crypto.Proc_3_0_6D2AF0(friendCount, null, "Fs") + friendPayload
                + Crypto.Proc_3_0_6D2AF0(otherCount, null, "") + otherPayload;
            Proc_6_244_801E80(socketIndex, resultPayload, 0);
            return resultPayload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_173_7C3430(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "@a");
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            LongRef offset = new LongRef(1);
            String targetUserId = String.valueOf(readWireLong(requestPayload, offset));
            if (targetUserId.isEmpty() || "0".equals(targetUserId)) {
                targetUserId = String.valueOf((long) Vb.val(Functions.Proc_10_6_809F10(requestPayload, 0, 0)));
            }
            if (targetUserId.isEmpty() || "0".equals(targetUserId)) {
                return "";
            }
            String messageText = Functions.Proc_10_7_80A190(requestPayload, 0, 0);
            if (messageText.length() > 122) {
                messageText = messageText.substring(0, 122);
            }
            if (messageText.isEmpty()) {
                messageText = readWireString(requestPayload, offset);
                if (messageText.length() > 122) {
                    messageText = messageText.substring(0, 122);
                }
            }
            if (messageText.isEmpty() || messageText.length() > 255) {
                return "";
            }
            int targetSocketIndex = handlingSocketFromUserId(targetUserId);
            if (targetSocketIndex <= 0) {
                return "";
            }
            long currentRoomId = handlingCurrentRoomId(socketIndex, userId);
            MySQL.Proc_5_1_6D4110("INSERT INTO logs_chat(id_user,id_room,timestamp,description,id_type,id_session) VALUES('"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "','" + currentRoomId + "',UNIX_TIMESTAMP(),'"
                + Functions.Proc_10_11_80A9C0("(Chat To:     " + handlingUserName(targetUserId) + ") -- " + messageText, 0, 0)
                + "','3','" + socketIndex + "')", 0, 0);
            String filteredText = Proc_6_22_6E9300(messageText, 0, 0);
            String payload = Crypto.Proc_3_0_6D2AF0(Vb.val(userId), null, "BF") + filteredText + '\2';
            Proc_6_244_801E80(targetSocketIndex, payload, 0);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_174_7C3BC0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "@g");
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            String targetName = Functions.Proc_10_7_80A190(requestPayload, 0, 0);
            if (targetName.isEmpty()) {
                LongRef offset = new LongRef(1);
                targetName = readWireString(requestPayload, offset);
            }
            targetName = targetName.trim();
            if (targetName.isEmpty()) {
                return "";
            }
            String targetUserId = String.valueOf((long) Vb.val(MySQL.Proc_5_2_6D4690("SELECT id FROM users WHERE name='"
                + Functions.Proc_10_11_80A9C0(targetName, 0, 0) + "' LIMIT 1", 0, 0)));
            if (targetUserId.isEmpty() || "0".equals(targetUserId) || targetUserId.equals(userId)) {
                String callerPayload = messengerRequestDeniedPayload();
                Proc_6_244_801E80(socketIndex, callerPayload, 0);
                return callerPayload;
            }
            String friendshipRow = MySQL.Proc_5_2_6D4690("SELECT id_user FROM friendships WHERE (id_user='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "' AND id_friend='"
                + Functions.Proc_10_11_80A9C0(targetUserId, 0, 0) + "') OR (id_user='"
                + Functions.Proc_10_11_80A9C0(targetUserId, 0, 0) + "' AND id_friend='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "') LIMIT 1", 0, 0);
            long acceptFriends = Vb.val(MySQL.Proc_5_2_6D4690("SELECT accept_friends FROM users WHERE id='"
                + Functions.Proc_10_11_80A9C0(targetUserId, 0, 0) + "' LIMIT 1", 0, 0));
            if (!friendshipRow.isEmpty() || acceptFriends != 1L) {
                String callerPayload = messengerRequestDeniedPayload();
                Proc_6_244_801E80(socketIndex, callerPayload, 0);
                return callerPayload;
            }
            MySQL.Proc_5_0_6D3CD0("INSERT IGNORE INTO friendships(id_user,id_friend) VALUES('"
                + Functions.Proc_10_11_80A9C0(targetUserId, 0, 0) + "','"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "')", 0, 0);
            String userName = handlingUserName(userId);
            int targetSocketIndex = handlingSocketFromUserId(targetUserId);
            if (targetSocketIndex > 0) {
                Proc_6_244_801E80(targetSocketIndex, messengerRequestNotifyPayload(Vb.val(userId), userName), 0);
            }
            String callerPayload = messengerRequestAcceptedCallerPayload(Vb.val(targetUserId));
            Proc_6_244_801E80(socketIndex, callerPayload, 0);
            return callerPayload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_175_7C4800(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            String rowText = MySQL.Proc_5_2_6D4690("SELECT users.id,users.name FROM users,friendships WHERE friendships.has_accept='0' AND friendships.id_user='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "' AND users.id=friendships.id_friend LIMIT 50", 0, 0);
            String payload = messengerPendingRequestsPayload(rowText);
            Proc_6_244_801E80(socketIndex, payload, 0);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_176_7C4EE0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long maxFriends0 = messengerMaxFriends(0L);
            long maxFriends1 = messengerMaxFriends(2L);
            long maxFriends2 = messengerMaxFriends(4L);
            long queryLimit = maxFriends2 > 0L ? maxFriends2 : 200L;
            String dateFormat = Functions.Proc_10_0_809570("com.mysql.format.date", "%d-%m-%Y", 0);
            String timeFormat = Functions.Proc_10_0_809570("com.mysql.format.time", "%H:%i", 0);
            String rowText = MySQL.Proc_5_2_6D4690("SELECT users.id,users.name,users.id_socket,users.figure,users.motto,users.level,"
                + "DATE_FORMAT(FROM_UNIXTIME(users.lastonline_time), '"
                + Functions.Proc_10_11_80A9C0(dateFormat + " " + timeFormat, 0, 0)
                + "') FROM friendships,users WHERE friendships.has_accept='1' AND friendships.id_user='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "' AND users.id=friendships.id_friend LIMIT " + queryLimit, 0, 0);
            String callerSummary = messengerFriendSummaryPayload(userId, 1L);
            long friendCount = 0L;
            StringBuilder friendPayload = new StringBuilder();
            for (String row : rowText.split("\r", -1)) {
                if (!row.isEmpty()) {
                    String[] fields = row.split("\t", -1);
                    if (fields.length >= 7) {
                        String friendUserId = handlingField(fields, 0);
                        int friendSocketIndex = (int) Vb.val(handlingField(fields, 2));
                        long friendOnline = friendSocketIndex > 0
                            && Guardian.Proc_11_2_821390(friendSocketIndex, 0, 0) == 1L ? 1L : 0L;
                        friendPayload.append(messengerFriendPayload(
                            Vb.val(friendUserId),
                            handlingField(fields, 1),
                            handlingField(fields, 4),
                            handlingField(fields, 3),
                            Vb.val(handlingField(fields, 5)),
                            friendOnline == 1L ? 2L : 0L,
                            friendOnline,
                            handlingField(fields, 6),
                            1L));
                        friendCount++;
                        if (friendOnline == 1L && !callerSummary.isEmpty()) {
                            Proc_6_244_801E80(friendSocketIndex, "@MHIH" + callerSummary, 0);
                        }
                    }
                }
            }
            String payload = Crypto.Proc_3_0_6D2AF0(maxFriends0, null, "@L")
                + Crypto.Proc_3_0_6D2AF0(maxFriends1, null, "")
                + Crypto.Proc_3_0_6D2AF0(maxFriends2, null, "")
                + Crypto.Proc_3_0_6D2AF0(friendCount, null, "") + friendPayload + "PYH";
            Proc_6_244_801E80(socketIndex, payload, 0);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_177_7C6580(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "n" + '\177');
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            String productPet = Functions.Proc_10_7_80A190(requestPayload, 0, 0);
            if (productPet.isEmpty()) {
                productPet = readWireString(requestPayload, new LongRef(1));
            }
            if (productPet.isEmpty()) {
                return "";
            }
            long rankIndex = handlingUserRank(userId);
            long hcLevel = handlingUserHcLevel(userId);
            String rowText = MySQL.Proc_5_2_6D4690("SELECT id_pet,breed,min_rank,min_hcrank,name FROM settings_petraces WHERE product_pet='"
                + Functions.Proc_10_11_80A9C0(productPet, 0, 0) + "' ORDER BY breed ASC", 0, 0);
            String payload = petRaceListPayload(productPet, rowText, rankIndex, hcLevel);
            Proc_6_244_801E80(socketIndex, payload, 0);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_178_7C6E60(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            String rowText = MySQL.Proc_5_2_6D4690("SELECT bots.id,bots.name,bots.figure,bots_petdata.scratches FROM bots,bots_petdata WHERE bots.id_user='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0)
                + "' AND bots.id_handle='3' AND bots.id_room IS NULL AND bots_petdata.id_bot=bots.id", 0, 0);
            String payload = petInventoryListPayload(rowText);
            Proc_6_244_801E80(socketIndex, payload, 0);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static long Proc_6_179_7C7790(Object... args) {
        try {
            if (Vb.val(Functions.Proc_10_0_809570("com.client.rooms.bots.pets.enabled", "0", 0)) == 0L) {
                return 0L;
            }
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "nz");
            LongRef offset = new LongRef(1);
            long petId = readWireLong(requestPayload, offset);
            long positionX = readWireLong(requestPayload, offset);
            long positionY = readWireLong(requestPayload, offset);
            long positionR = readWireLong(requestPayload, offset);
            if (socketIndex <= 0 || petId <= 0L) {
                return 0L;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return 0L;
            }
            long roomSlot = Vb.val(MySQL.Proc_5_2_6D4690("SELECT id_slot FROM rooms WHERE id='" + roomId + "' LIMIT 1", 0, 0));
            if (roomSlot <= 0L) {
                return 0L;
            }
            String positionZ = String.valueOf(Vb.val(MySQL.Proc_5_2_6D4690("SELECT heightmap FROM models,rooms WHERE rooms.id='"
                + roomId + "' AND models.id=rooms.id_model LIMIT 1", 0, 0)));
            String rowText = MySQL.Proc_5_2_6D4690("SELECT bots.id,bots.name,bots.motto,bots.speech,bots.responses,'"
                + positionX + "','" + positionY + "','" + Functions.Proc_10_11_80A9C0(positionZ, 0, 0) + "','"
                + positionR + "',bots.figure,NULL,bots.id_handle,bots.id_handleaction,NULL,bots.speech_submit,bots.allow_walk,bots.max_fields_away "
                + "FROM bots,bots_petdata WHERE bots_petdata.id_bot='" + petId
                + "' AND bots.id=bots_petdata.id_bot AND bots.id_user='" + Functions.Proc_10_11_80A9C0(userId, 0, 0)
                + "' AND bots.id_room IS NULL LIMIT 1", 0, 0);
            if (rowText.isEmpty()) {
                return 0L;
            }
            long botEntityId = Proc_6_187_7CD700(roomSlot, rowText.split("\t", -1), 0);
            if (botEntityId <= 0L) {
                return 0L;
            }
            storeRepresentedBotPosition(botEntityId, positionX, positionY, positionZ, positionR);
            MySQL.Proc_5_0_6D3CD0("UPDATE bots SET id_room='" + roomId + "',position_x='" + positionX
                + "',position_y='" + positionY + "',position_z='" + Functions.Proc_10_11_80A9C0(positionZ, 0, 0)
                + "',position_r='" + positionR + "' WHERE id='" + petId + "'", 0, 0);
            String placementPayload = representedBotRoomEntryPayload(botEntityId);
            if (!placementPayload.isEmpty()) {
                Proc_6_247_8027E0(socketIndex, placementPayload, 0);
            }
            Proc_6_244_801E80(socketIndex, Crypto.Proc_3_0_6D2AF0(petId, null, "I\\"), 0);
            return botEntityId;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return 0L;
        }
    }

    public static long Proc_6_180_7C96F0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            long botEntityId = args != null && args.length >= 2 ? Vb.val(args[1]) : 0L;
            if (botEntityId <= 0L) {
                return 0L;
            }
            long botId = representedBotRecordLong(botEntityId, 1);
            if (botId <= 0L) {
                return 0L;
            }
            MySQL.Proc_5_0_6D3CD0("UPDATE bots SET id_room=null WHERE id='" + botId + "'", 0, 0);
            MySQL.Proc_5_0_6D3CD0("UPDATE bots_petdata SET id_level=id_level,energy=energy,experience=experience,nutrition=nutrition,scratches=scratches WHERE id_bot='"
                + botId + "'", 0, 0);
            Proc_6_247_8027E0(socketIndex, "@]" + botEntityId + '\2', 0);
            String petName = representedBotRecordField(botEntityId, 2);
            String petFigure = representedBotRecordField(botEntityId, 10).toLowerCase();
            long scratches = Vb.val(MySQL.Proc_5_2_6D4690("SELECT scratches FROM bots_petdata WHERE id_bot='" + botId + "' LIMIT 1", 0, 0));
            String pickupPayload = petInventoryRowPayload(new String[]{String.valueOf(botId), petName, petFigure, String.valueOf(scratches)});
            if (!pickupPayload.isEmpty()) {
                Proc_6_244_801E80(socketIndex, "I[" + pickupPayload, 0);
            }
            removeRepresentedBotRecord(botEntityId);
            return botId;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return 0L;
        }
    }

    public static long Proc_6_181_7CA920(Object... args) {
        try {
            if (args == null || args.length == 0) {
                return 2L;
            }
            return petNameValidationCode(Vb.cStr(args[0]));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return 2L;
        }
    }

    public static String Proc_6_182_7CAAD0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "@c");
            String requestedName = Functions.Proc_10_7_80A190(requestPayload, 0, 0);
            if (requestedName.isEmpty()) {
                requestedName = readWireString(requestPayload, new LongRef(1));
            }
            requestedName = Functions.Proc_10_11_80A9C0(Functions.Proc_10_10_80A7F0(requestedName), 0, 0);
            String payload = Crypto.Proc_3_0_6D2AF0(Proc_6_181_7CA920(requestedName, 0, 0), null, "@d");
            Proc_6_244_801E80(socketIndex, payload, 0);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_183_7CABF0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "ny");
            LongRef offset = new LongRef(1);
            long requestedId = readWireLong(requestPayload, offset);
            if (socketIndex <= 0 || requestedId <= 0L) {
                return "";
            }
            long botEntityId = requestedId;
            long botId = representedBotRecordLong(botEntityId, 1);
            if (botId <= 0L) {
                botId = requestedId;
                botEntityId = representedBotEntityFromBotId(botId);
            }
            if (botId <= 0L) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            String rowText = MySQL.Proc_5_2_6D4690("SELECT bots.id,bots.name,bots.figure,bots_petdata.id_level,bots_petdata.experience,"
                + "bots_petdata.energy,bots_petdata.nutrition,bots_petdata.scratches,"
                + "ROUND((UNIX_TIMESTAMP()-bots_petdata.timestamp_buy)/60/60/24,0),bots_petdata.id_owner,users.name "
                + "FROM bots,bots_petdata,users WHERE bots.id='" + botId
                + "' AND bots.id_handle='3' AND bots_petdata.id_bot=bots.id AND users.id=bots_petdata.id_owner LIMIT 1", 0, 0);
            if (rowText.isEmpty()) {
                return "";
            }
            String[] fields = rowText.split("\t", -1);
            if (fields.length < 11) {
                return "";
            }
            if (botEntityId <= 0L) {
                botEntityId = botId;
            }
            String payload = representedPetStatusPayload(botEntityId, fields);
            if (!payload.isEmpty()) {
                Proc_6_244_801E80(socketIndex, payload, 0);
            }
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_184_7CBDA0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            long petLevel = 0L;
            if (args != null && args.length >= 2) {
                petLevel = Vb.val(args[1]);
            } else if (args != null && args.length >= 1) {
                petLevel = Vb.val(args[0]);
            }
            String payload = Licence.petSettings().commandListPayload(petLevel);
            if (socketIndex > 0) {
                Proc_6_244_801E80(socketIndex, payload, 0);
            }
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_7CC190(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "n|");
            long requestedId = readWireLong(requestPayload, new LongRef(1));
            long botId = representedBotRecordLong(requestedId, 1);
            if (botId <= 0L) {
                botId = requestedId;
            }
            long petLevel = 0L;
            String userId = handlingUserIdFromSocket(socketIndex);
            if (!userId.isEmpty() && !"0".equals(userId) && botId > 0L) {
                petLevel = Vb.val(MySQL.Proc_5_2_6D4690("SELECT bots_petdata.id_level FROM bots,bots_petdata WHERE bots.id='"
                    + botId + "' AND bots.id_user='" + Functions.Proc_10_11_80A9C0(userId, 0, 0)
                    + "' AND bots_petdata.id_bot=bots.id LIMIT 1", 0, 0));
            }
            return Proc_6_184_7CBDA0(socketIndex, petLevel, 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static long Proc_7CA730(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            if (socketIndex <= 0) {
                return 0L;
            }
            String requestPayload = handlingRequestPayload(args, "n{");
            LongRef offset = new LongRef(1);
            long requestedId = readWireLong(requestPayload, offset);
            long commandId = readWireLong(requestPayload, offset);
            if (requestedId <= 0L || commandId <= 0L) {
                return 0L;
            }
            long botEntityId = requestedId;
            long botId = representedBotRecordLong(botEntityId, 1);
            if (botId <= 0L) {
                botId = requestedId;
                botEntityId = representedBotEntityFromBotId(botId);
            }
            if (botId <= 0L) {
                return 0L;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return 0L;
            }
            String petRow = MySQL.Proc_5_2_6D4690("SELECT bots.id,bots.id_room,bots_petdata.id_level,bots_petdata.energy,"
                + "bots_petdata.nutrition FROM bots,bots_petdata WHERE bots.id='" + botId
                + "' AND bots.id_handle='3' AND bots.id_room='" + roomId
                + "' AND bots_petdata.id_bot=bots.id LIMIT 1", 0, 0);
            if (petRow.isEmpty()) {
                return 0L;
            }
            String[] petFields = petRow.split("\t", -1);
            if (petFields.length < 5) {
                return 0L;
            }
            long petLevel = Vb.val(handlingField(petFields, 2));
            long petEnergy = Vb.val(handlingField(petFields, 3));
            long petNutrition = Vb.val(handlingField(petFields, 4));
            PetCommandAction commandAction = petCommandAction(commandId, Licence.petSettings().commandRows());
            if (!commandAction.found || commandAction.requiredLevel > petLevel) {
                return 0L;
            }
            if (!commandAction.action.isEmpty()) {
                String payload = "IZ" + Crypto.Proc_3_0_6D2AF0(botEntityId, null, "")
                    + commandAction.action + '\2' + Crypto.Proc_3_0_6D2AF0(commandId, null, "");
                Proc_6_248_802B80(roomId, payload, 0);
            }
            if (petEnergy < 250L || petNutrition < 250L) {
                String commandSpeech = Functions.Proc_10_4_809CA0(0, 2, -1) == 0L
                    ? Functions.Proc_10_0_809570("com.client.bot.pet.sad.speech", "gst thr", 0)
                    : Functions.Proc_10_0_809570("com.client.bot.pet.angry.speech", "gst grr", 0);
                if (!commandSpeech.isEmpty()) {
                    Proc_6_248_802B80(roomId, "@X" + Crypto.Proc_3_0_6D2AF0(botEntityId, null, "")
                        + commandSpeech + '\2' + "H", 0);
                }
            } else {
                Proc_6_185_7CC2D0(botEntityId, commandId * 10L, 0);
            }
            return commandId;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return 0L;
        }
    }

    public static long Proc_6_185_7CC2D0(Object... args) {
        try {
            if (args == null || args.length == 0) {
                return 0L;
            }
            long botEntityId = Vb.val(args[0]);
            long experienceDelta = args.length >= 2 ? Vb.val(args[1]) : 0L;
            if (botEntityId <= 0L) {
                return 0L;
            }
            long botId = representedBotRecordLong(botEntityId, 1);
            if (botId <= 0L) {
                botId = botEntityId;
                botEntityId = representedBotEntityFromBotId(botId);
            }
            if (botId <= 0L) {
                return 0L;
            }
            String rowText = MySQL.Proc_5_2_6D4690("SELECT bots.name,bots.figure,bots_petdata.id_level,bots_petdata.experience,"
                + "bots_petdata.energy,bots_petdata.nutrition,bots_petdata.scratches,bots.id_room FROM bots,bots_petdata WHERE bots.id='"
                + botId + "' AND bots_petdata.id_bot=bots.id LIMIT 1", 0, 0);
            if (rowText.isEmpty()) {
                return 0L;
            }
            String[] fields = rowText.split("\t", -1);
            if (fields.length < 8) {
                return 0L;
            }
            String petName = handlingField(fields, 0);
            String petFigure = handlingField(fields, 1);
            long petLevel = Vb.val(handlingField(fields, 2));
            long petExperience = Vb.val(handlingField(fields, 3));
            long petEnergy = Vb.val(handlingField(fields, 4));
            long petNutrition = Vb.val(handlingField(fields, 5));
            long petScratches = Vb.val(handlingField(fields, 6));
            long roomId = Vb.val(handlingField(fields, 7));
            if (botEntityId <= 0L) {
                botEntityId = botId;
            }
            String levelRows = MySQL.Proc_5_2_6D4690("SELECT id_level,max_exp FROM bots_petlevels ORDER BY id_level ASC", 0, 0);
            PetExperienceUpdate update = petExperienceUpdate(
                botEntityId,
                petName,
                petFigure,
                petLevel,
                petExperience,
                petEnergy,
                petNutrition,
                petScratches,
                experienceDelta,
                levelRows);
            if (update.leveledUp && roomId > 0L) {
                String levelSpeech = Functions.Proc_10_0_809570("com.client.bot.pet.level_up.speech", "gst sml", 0);
                if (!levelSpeech.isEmpty()) {
                    Proc_6_248_802B80(roomId, "@X" + Crypto.Proc_3_0_6D2AF0(botEntityId, null, "") + levelSpeech + '\2' + "H", 0);
                }
            }
            MySQL.Proc_5_0_6D3CD0("UPDATE bots_petdata SET id_level='" + update.petLevel + "',experience='"
                + update.petExperience + "' WHERE id_bot='" + botId + "'", 0, 0);
            if (roomId > 0L) {
                Proc_6_248_802B80(roomId, update.statusPayload, 0);
                Proc_6_248_802B80(roomId, update.experiencePayload, 0);
            }
            return update.petLevel;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return 0L;
        }
    }

    public static long Proc_6_186_7CD040(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "n}");
            LongRef offset = new LongRef(1);
            long requestedId = readWireLong(requestPayload, offset);
            if (socketIndex <= 0 || requestedId <= 0L) {
                return 0L;
            }
            long botEntityId = requestedId;
            long botId = representedBotRecordLong(botEntityId, 1);
            if (botId <= 0L) {
                botId = requestedId;
                botEntityId = representedBotEntityFromBotId(botId);
            }
            if (botId <= 0L) {
                return 0L;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            long scratchAmount = Vb.val(MySQL.Proc_5_2_6D4690("SELECT scratch_amount FROM users WHERE id='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "' LIMIT 1", 0, 0));
            if (scratchAmount <= 0L) {
                return 0L;
            }
            String rowText = MySQL.Proc_5_2_6D4690("SELECT bots.id,bots.name,bots.figure,bots_petdata.scratches FROM bots,bots_petdata WHERE bots.id='"
                + botId + "' AND bots.id_handle='3' AND bots.id_room IS NOT NULL AND bots_petdata.id_bot=bots.id LIMIT 1", 0, 0);
            if (rowText.isEmpty()) {
                return 0L;
            }
            String[] fields = rowText.split("\t", -1);
            if (fields.length < 4) {
                return 0L;
            }
            long scratches = Vb.val(handlingField(fields, 3)) + 1L;
            MySQL.Proc_5_0_6D3CD0("UPDATE bots_petdata SET scratches='" + scratches + "' WHERE id_bot='" + botId + "'", 0, 0);
            MySQL.Proc_5_0_6D3CD0("UPDATE users SET scratch_amount=scratch_amount-1,scratch_given=scratch_given+1 WHERE id='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "'", 0, 0);
            if (botEntityId <= 0L) {
                botEntityId = botId;
            }
            Proc_6_247_8027E0(socketIndex, petScratchPayload(botEntityId, Vb.val(userId), scratches,
                handlingField(fields, 1), handlingField(fields, 2)), 0);
            return scratches;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return 0L;
        }
    }

    public static long Proc_6_187_7CD700(Object... args) {
        try {
            if (args == null || args.length < 2) {
                return 0L;
            }
            long roomSlot = Vb.val(args[0]);
            Object fieldSource = args[1];
            String[] botFields;
            if (fieldSource instanceof String[]) {
                botFields = (String[]) fieldSource;
            } else {
                botFields = Vb.cStr(fieldSource).split("\t", -1);
            }
            return allocateRepresentedBot(roomSlot, botFields);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return 0L;
        }
    }

    public static long Proc_6_188_7CF3C0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            if (socketIndex <= 0) {
                return 0L;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            long tutorialGuide = Vb.val(MySQL.Proc_5_2_6D4690("SELECT tutorial_guide FROM users WHERE id='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "' LIMIT 1", 0, 0));
            if (tutorialGuide == 0L) {
                MySQL.Proc_5_0_6D3CD0("UPDATE users SET tutorial_guide='1' WHERE id='"
                    + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "'", 0, 0);
            }
            if (Vb.val(Functions.Proc_10_0_809570("com.client.rooms.bots.guide.enabled", "0", 0)) == 0L) {
                return 0L;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return 0L;
            }
            long roomSlot = Vb.val(MySQL.Proc_5_2_6D4690("SELECT id_slot FROM rooms WHERE id='" + roomId + "' LIMIT 1", 0, 0));
            if (roomSlot <= 0L) {
                return 0L;
            }
            long guideBotId = Vb.val(Functions.Proc_10_0_809570("com.client.bot.guide.id", "0", 0));
            if (guideBotId <= 0L || isRepresentedBotAllocated(roomSlot, guideBotId)) {
                return 0L;
            }
            String rowText = MySQL.Proc_5_2_6D4690("SELECT id,name,motto,speech,responses,position_x,position_y,position_z,position_r,figure,NULL,"
                + "id_handle,id_handleaction,cache_action,speech_submit,allow_walk,max_fields_away FROM bots WHERE id='"
                + guideBotId + "' LIMIT 1", 0, 0);
            if (rowText.isEmpty()) {
                return 0L;
            }
            long botEntityId = Proc_6_187_7CD700(roomSlot, rowText.split("\t", -1), 0);
            if (botEntityId > 0L) {
                Proc_6_244_801E80(socketIndex, "@a" + "YjO", 0);
            }
            return botEntityId;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return 0L;
        }
    }

    public static long Proc_6_189_7D0630(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "Fy");
            LongRef offset = new LongRef(1);
            long requestedEntityId = readWireLong(requestPayload, offset);
            if (socketIndex <= 0) {
                return 0L;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return 0L;
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return 0L;
            }
            long roomSlot = Vb.val(MySQL.Proc_5_2_6D4690("SELECT id_slot FROM rooms WHERE id='" + roomId + "' LIMIT 1", 0, 0));
            if (roomSlot <= 0L) {
                return 0L;
            }
            String entityList = "";
            if (requestedEntityId > 0L) {
                if (representedBotRecordLong(requestedEntityId, 0) == roomSlot) {
                    entityList = String.valueOf(requestedEntityId);
                }
            } else {
                long guideBotId = Vb.val(Functions.Proc_10_0_809570("com.client.bot.guide.id", "0", 0));
                entityList = representedBotEntitiesForRoom(roomSlot, guideBotId);
            }
            if (entityList.isEmpty()) {
                return 0L;
            }
            long removedCount = 0L;
            for (String entityIdText : entityList.split("\r", -1)) {
                long botEntityId = Vb.val(entityIdText);
                if (botEntityId > 0L) {
                    Proc_6_248_802B80(roomId, "@]" + botEntityId + '\2', 0);
                    removeRepresentedBotRecord(botEntityId);
                    removedCount++;
                }
            }
            return removedCount;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return 0L;
        }
    }

    public static String Proc_6_190_7D11D0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "Cg");
            LongRef offset = new LongRef(1);
            long requestedRoomUserIndex = readWireLong(requestPayload, offset);
            if (socketIndex <= 0 || requestedRoomUserIndex <= 0L) {
                return "";
            }
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return "";
            }
            long roomId = handlingCurrentRoomId(socketIndex, callerUserId);
            if (roomId <= 0L) {
                return "";
            }
            String rowText = MySQL.Proc_5_2_6D4690("SELECT logs_visitedrooms.id,users.name,users.motto,users.achievement_score,users.figure "
                + "FROM logs_visitedrooms,users WHERE logs_visitedrooms.id='" + requestedRoomUserIndex
                + "' AND logs_visitedrooms.id_room='" + roomId
                + "' AND logs_visitedrooms.timestamp_left IS NULL AND users.id=logs_visitedrooms.id_user LIMIT 1", 0, 0);
            if (rowText.isEmpty()) {
                rowText = MySQL.Proc_5_2_6D4690("SELECT logs_visitedrooms.id,users.name,users.motto,users.achievement_score,users.figure "
                    + "FROM logs_visitedrooms,users WHERE logs_visitedrooms.id_user='" + requestedRoomUserIndex
                    + "' AND logs_visitedrooms.id_room='" + roomId
                    + "' AND logs_visitedrooms.timestamp_left IS NULL AND users.id=logs_visitedrooms.id_user LIMIT 1", 0, 0);
            }
            if (rowText.isEmpty()) {
                return "";
            }
            String[] fields = rowText.split("\t", -1);
            if (fields.length < 5) {
                return "";
            }
            String payload = representedRoomUserProfilePayload(
                Vb.val(handlingField(fields, 0)),
                handlingField(fields, 1),
                handlingField(fields, 2),
                Vb.val(handlingField(fields, 3)),
                handlingField(fields, 4));
            if (!payload.isEmpty()) {
                Proc_6_244_801E80(socketIndex, payload, 0);
            }
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_191_7D18B0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "DG");
            LongRef offset = new LongRef(1);
            long requestedUserId = readWireLong(requestPayload, offset);
            if (socketIndex <= 0 || requestedUserId <= 0L) {
                return "";
            }
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return "";
            }
            int targetSocketIndex = (int) Vb.val(MySQL.Proc_5_2_6D4690("SELECT id_socket FROM users WHERE id='"
                + requestedUserId + "' LIMIT 1", 0, 0));
            if (targetSocketIndex <= 0 && handlingCurrentRoomId(socketIndex, callerUserId) <= 0L) {
                return "";
            }
            String payload = tagDisplayPayload(requestedUserId, Proc_6_196_7D3ED0(requestedUserId, 0, 0));
            Proc_6_244_801E80(socketIndex, payload, 0);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_192_7D1B80(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "B_");
            long requestedRoomUserIndex = readWireLong(requestPayload, new LongRef(1));
            if (socketIndex <= 0 || requestedRoomUserIndex <= 0L) {
                return "";
            }
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return "";
            }
            long callerRoomId = handlingCurrentRoomId(socketIndex, callerUserId);
            if (callerRoomId <= 0L) {
                return "";
            }
            long callerRoomUserIndex = representedRoomUserIndex(socketIndex, callerUserId);
            String targetRow = MySQL.Proc_5_2_6D4690("SELECT logs_visitedrooms.id,logs_visitedrooms.id_user,users.id_socket FROM logs_visitedrooms,users WHERE logs_visitedrooms.id='"
                + requestedRoomUserIndex + "' AND logs_visitedrooms.id_room='" + callerRoomId
                + "' AND logs_visitedrooms.timestamp_left IS NULL AND users.id=logs_visitedrooms.id_user LIMIT 1", 0, 0);
            if (targetRow.isEmpty()) {
                targetRow = MySQL.Proc_5_2_6D4690("SELECT logs_visitedrooms.id,logs_visitedrooms.id_user,users.id_socket FROM logs_visitedrooms,users WHERE logs_visitedrooms.id_user='"
                    + requestedRoomUserIndex + "' AND logs_visitedrooms.id_room='" + callerRoomId
                    + "' AND logs_visitedrooms.timestamp_left IS NULL AND users.id=logs_visitedrooms.id_user LIMIT 1", 0, 0);
            }
            if (targetRow.isEmpty()) {
                return "";
            }
            String[] targetFields = targetRow.split("\t", -1);
            long targetRoomUserIndex = Vb.val(handlingField(targetFields, 0));
            String targetUserId = String.valueOf((long) Vb.val(handlingField(targetFields, 1)));
            if (targetRoomUserIndex <= 0L || targetUserId.isEmpty() || "0".equals(targetUserId)) {
                return "";
            }
            String targetBadgePayload = badgeDisplayPayload(Vb.val(targetUserId), Proc_6_195_7D38D0(targetUserId, 0, 0));
            Proc_6_244_801E80(socketIndex, targetBadgePayload, 0);
            if (callerRoomUserIndex > 0L && callerRoomUserIndex != targetRoomUserIndex) {
                String callerStatusPayload = representedRoomUserStatusPayload(callerRoomUserIndex, 0L);
                String targetStatusPayload = representedRoomUserStatusPayload(targetRoomUserIndex, 0L);
                if (!callerStatusPayload.isEmpty()) {
                    Proc_6_247_8027E0(socketIndex, callerStatusPayload, 0);
                }
                if (!targetStatusPayload.isEmpty()) {
                    Proc_6_247_8027E0(socketIndex, targetStatusPayload, 0);
                }
            }
            return targetBadgePayload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_193_7D2BB0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            if (socketIndex <= 0) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            String inventoryRows = MySQL.Proc_5_2_6D4690("SELECT id_badge,id_slot,id FROM users_badges WHERE id_user='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "' AND id_slot='0' LIMIT 1000", 0, 0);
            String equippedPayload = Proc_6_195_7D38D0(userId, 0, 0);
            String payload = badgeInventoryPayload(inventoryRows, equippedPayload);
            Proc_6_244_801E80(socketIndex, payload, 0);
            Proc_6_244_801E80(socketIndex, badgeDisplayPayload(Vb.val(userId), equippedPayload), 0);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_194_7D3180(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            if (socketIndex <= 0) {
                return "";
            }
            String packetPayload = handlingPacketPayload(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            String escapedUserId = Functions.Proc_10_11_80A9C0(userId, 0, 0);
            MySQL.Proc_5_0_6D3CD0("UPDATE users_badges SET id_slot='0' WHERE id_user='" + escapedUserId + "'", 0, 0);
            String[] slots = badgeUpdateSelectionsFromWire(packetPayload);
            for (int slotIndex = 0; slotIndex < slots.length; slotIndex++) {
                String badgeId = slots[slotIndex];
                if (!badgeId.isEmpty()) {
                    MySQL.Proc_5_0_6D3CD0("UPDATE users_badges SET id_slot='" + (slotIndex + 1)
                        + "' WHERE id_badge='" + badgeId + "' AND id_user='" + escapedUserId + "'", 0, 0);
                }
            }
            String equippedPayload = Proc_6_195_7D38D0(userId, 0, 0);
            String displayPayload = badgeDisplayPayload(Vb.val(userId), equippedPayload);
            Proc_6_244_801E80(socketIndex, displayPayload, 0);
            if (handlingCurrentRoomId(socketIndex, userId) > 0L) {
                Proc_6_247_8027E0(socketIndex, displayPayload, 0);
            }
            return equippedPayload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_195_7D38D0(Object... args) {
        try {
            String userId = "";
            if (args != null && args.length >= 1 && Vb.val(args[0]) > 0L) {
                userId = String.valueOf((long) Vb.val(args[0]));
            }
            if (userId.isEmpty() || "0".equals(userId)) {
                int socketIndex = handlingSocketIndex(args);
                if (socketIndex > 0) {
                    userId = handlingUserIdFromSocket(socketIndex);
                }
            }
            if (userId.isEmpty() || "0".equals(userId)) {
                return Crypto.Proc_3_0_6D2AF0(0, null, "");
            }
            String rows = MySQL.Proc_5_2_6D4690("SELECT id_badge,id_slot,id FROM users_badges WHERE id_slot != '0' AND id_user='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "' LIMIT 5", 0, 0);
            return equippedBadgePayload(rows);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return Crypto.Proc_3_0_6D2AF0(0, null, "");
        }
    }

    public static String Proc_6_196_7D3ED0(Object... args) {
        try {
            String userId = "";
            if (args != null && args.length >= 1 && Vb.val(args[0]) > 0L) {
                userId = String.valueOf((long) Vb.val(args[0]));
            }
            if (userId.isEmpty() || "0".equals(userId)) {
                int socketIndex = handlingSocketIndex(args);
                if (socketIndex > 0) {
                    userId = handlingUserIdFromSocket(socketIndex);
                }
            }
            if (userId.isEmpty() || "0".equals(userId)) {
                return Crypto.Proc_3_0_6D2AF0(0, null, "");
            }
            String rows = MySQL.Proc_5_2_6D4690("SELECT name FROM users_tags WHERE id_user='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "' LIMIT 30", 0, 0);
            return tagListPayload(rows);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return Crypto.Proc_3_0_6D2AF0(0, null, "");
        }
    }

    public static String Proc_6_197_7D43C0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            if (socketIndex <= 0) {
                return "";
            }
            String requestPayload = handlingRequestPayload(args, "AK");
            LongRef offset = new LongRef(1);
            long lookX = readWireLong(requestPayload, offset);
            long lookY = readWireLong(requestPayload, offset);
            if (lookX == 0L && lookY == 0L) {
                lookX = Vb.val(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
                lookY = Vb.val(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
            }
            if (lookX < 0L || lookY < 0L) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return "";
            }
            long roomSlot = socketIndex;
            MovementPosition current = representedMovementPosition(Licence.global_00829310, roomSlot, socketIndex);
            long currentX = current.found ? current.positionX : 0L;
            long currentY = current.found ? current.positionY : 0L;
            long directionValue = handlingDirectionCode(Long.compare(lookX, currentX), Long.compare(lookY, currentY));
            Licence.global_00829310 = representedRoomOccupantMove(
                Licence.global_00829310, roomSlot, socketIndex, currentX, currentY, directionValue, 0L);
            Proc_6_106_74B750(Path.of(Functions.applicationPath, "CACHE", "ROOMS", roomId + ".cache").toString(), 0, 0);
            return "";
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_198_7D4B70(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            if (socketIndex <= 0) {
                return "";
            }
            String requestPayload = handlingRequestPayload(args, "AO");
            LongRef offset = new LongRef(1);
            long targetX = readWireLong(requestPayload, offset);
            long targetY = readWireLong(requestPayload, offset);
            if (targetX == 0L && targetY == 0L) {
                targetX = Vb.val(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
                targetY = Vb.val(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
            }
            if (targetX < 0L || targetY < 0L) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L || Functions.Proc_10_25_80F5D0(roomId, targetX, targetY) == 0L) {
                return "";
            }
            long roomSlot = socketIndex;
            MovementPosition current = representedMovementPosition(Licence.global_00829310, roomSlot, socketIndex);
            long currentX = current.found ? current.positionX : 0L;
            long currentY = current.found ? current.positionY : 0L;
            String movementText = Functions.Proc_10_24_80E790(socketIndex, currentX, currentY, targetX, targetY);
            long nextX = handlingMovementField(movementText, 0);
            long nextY = handlingMovementField(movementText, 1);
            long directionValue = handlingMovementField(movementText, 2);
            long movingValue = handlingMovementField(movementText, 3);
            if (movingValue == 0L && (currentX != targetX || currentY != targetY)) {
                movingValue = 1L;
            }
            Licence.global_00829310 = representedRoomOccupantMove(
                Licence.global_00829310, roomSlot, socketIndex, nextX, nextY, directionValue, movingValue);
            Proc_6_106_74B750(Path.of(Functions.applicationPath, "CACHE", "ROOMS", roomId + ".cache").toString(), 0, 0);
            return "";
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_199_7D54E0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            long pollId = pollIdFromWire(handlingPacketPayload(args), "Ck");
            if (pollId <= 0L) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return "";
            }
            String pollRow = MySQL.Proc_5_2_6D4690("SELECT id,description_title,description_thanks FROM poll WHERE id='"
                + pollId + "' AND id_room='" + roomId + "' LIMIT 1", 0, 0);
            if (pollRow.isEmpty()) {
                return "";
            }
            MySQL.Proc_5_0_6D3CD0("INSERT INTO poll_exit(id_user,id_poll) VALUES('"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "','" + pollId + "')", 0, 0);
            return "";
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_200_7D5770(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            PollAnswerSubmission submission = pollAnswerFromWire(handlingPacketPayload(args), "Cl");
            if (!submission.valid) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return "";
            }
            String pollRow = MySQL.Proc_5_2_6D4690("SELECT id,description_title,description_thanks FROM poll WHERE id='"
                + submission.pollId + "' AND id_room='" + roomId + "' LIMIT 1", 0, 0);
            if (pollRow.isEmpty()) {
                return "";
            }
            MySQL.Proc_5_0_6D3CD0("INSERT INTO poll_results(id_poll,id_question,message_answer,id_user,timestamp) VALUES('"
                + submission.pollId + "','" + submission.questionId + "','"
                + Functions.Proc_10_11_80A9C0(submission.answerText, 0, 0) + "','"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "',UNIX_TIMESTAMP())", 0, 0);
            return "";
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_201_7D5AC0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            long pollId = pollIdFromWire(handlingPacketPayload(args), "Cj");
            if (pollId <= 0L) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return "";
            }
            String pollRow = MySQL.Proc_5_2_6D4690("SELECT id,description_title,description_thanks FROM poll WHERE id='"
                + pollId + "' AND id_room='" + roomId + "' LIMIT 1", 0, 0);
            if (pollRow.isEmpty()) {
                return "";
            }
            String questionRows = MySQL.Proc_5_2_6D4690("SELECT id,description_question,id_type FROM poll_questions WHERE id_poll='"
                + pollId + "' LIMIT 50", 0, 0);
            Map<Long, String> answerRowsByQuestionId = new HashMap<>();
            for (String questionRow : questionRows.split("\r", -1)) {
                if (!questionRow.isEmpty()) {
                    String[] questionFields = questionRow.split("\t", -1);
                    if (questionFields.length >= 3) {
                        long questionId = Vb.val(handlingField(questionFields, 0));
                        String answerRows = MySQL.Proc_5_2_6D4690("SELECT id,id_question,caption FROM poll_answers WHERE id_question='"
                            + questionId + "' LIMIT 5", 0, 0);
                        answerRowsByQuestionId.put(questionId, answerRows);
                    }
                }
            }
            String payload = pollPayloadFromRows(pollRow, questionRows, answerRowsByQuestionId);
            if (!payload.isEmpty()) {
                Proc_6_244_801E80(socketIndex, payload, 0);
            }
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_202_7D6760(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            if (socketIndex <= 0) {
                return "";
            }
            long enabledValue = Vb.val(Functions.Proc_10_0_809570("com.client.catalog.recycler.enabled", 0, 0));
            if (enabledValue == 0L) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            RecyclerSelection selection = recyclerSelectionFromWire(handlingPacketPayload(args));
            if (!selection.valid) {
                return "";
            }
            String escapedUserId = Functions.Proc_10_11_80A9C0(userId, 0, 0);
            String itemWhere = recyclerSelectionWhereClause(selection.selectedItems, escapedUserId);
            if (itemWhere.isEmpty()) {
                return "";
            }
            long validCount = Vb.val(MySQL.Proc_5_2_6D4690("SELECT COUNT(*) FROM furnitures,products WHERE "
                + itemWhere, 0, 0));
            if (validCount != selection.requestedCount) {
                return "";
            }
            long rewardProductId = representedRecyclerRewardProduct();
            if (rewardProductId <= 0L) {
                return "";
            }
            long rewardDestinationId = Vb.val(MySQL.Proc_5_2_6D4690(
                "SELECT id_destination FROM catalog_products WHERE id_product='" + rewardProductId
                    + "' ORDER BY id DESC LIMIT 1", 0, 0));
            if (rewardDestinationId <= 0L) {
                rewardDestinationId = rewardProductId;
            }
            String rewardSign = recyclerRewardSign();
            MySQL.Proc_5_0_6D3CD0("UPDATE furnitures SET sign='"
                + Functions.Proc_10_11_80A9C0(rewardSign, 0, 0) + "',id_owner='" + escapedUserId
                + "',id_destination='" + rewardDestinationId + "' WHERE id_owner='" + escapedUserId
                + "' AND id_product='" + Licence.recyclerSettings().boxProductId() + "' ORDER BY id DESC LIMIT 1", 1, 0);
            MySQL.Proc_5_0_6D3CD0("UPDATE furnitures SET id_owner=NULL WHERE id_owner='" + escapedUserId
                + "' AND id_room IS NULL AND id IN (" + selection.selectedItems + ")", 0, 0);
            MySQL.Proc_5_1_6D4110("INSERT INTO logs_recycler(id_user,timestamp,items,id_reward,id_session) VALUES('"
                + escapedUserId + "',UNIX_TIMESTAMP(),'"
                + Functions.Proc_10_11_80A9C0(selection.selectedItems, 0, 0) + "','"
                + rewardProductId + "','0')", 0, 0);
            for (String furnitureId : selection.selectedItems.split(",", -1)) {
                long selectedFurnitureId = Vb.val(furnitureId);
                if (selectedFurnitureId > 0L) {
                    Proc_6_244_801E80(socketIndex, Crypto.Proc_3_0_6D2AF0(selectedFurnitureId, null, "Ac"), 0);
                }
            }
            String payload = Crypto.Proc_3_0_6D2AF0(rewardProductId, null, "G|");
            Proc_6_244_801E80(socketIndex, payload, 0);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_203_7D7F80(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            long enabledValue = Vb.val(Functions.Proc_10_0_809570("com.client.catalog.recycler.enabled", 0, 0));
            String payload = recyclerStatusPayload(enabledValue, 0L);
            Proc_6_244_801E80(socketIndex, payload, 0);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_204_7D82E0(Object... args) {
        try {
            if (args == null || args.length < 2) {
                return "";
            }
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            long achievementIndex = Vb.val(args[1]);
            long badgeLevel = args.length >= 3 ? Vb.val(args[2]) : 1L;
            if (badgeLevel <= 0L) {
                badgeLevel = 1L;
            }
            String achievementRow = achievementRowByIndex(achievementIndex);
            String[] fields = achievementRow.split("\t", -1);
            if (userId.isEmpty() || achievementRow.isEmpty() || fields.length < 7) {
                return "";
            }
            String badgePrefix = handlingField(fields, 1);
            String badgeId = badgePrefix + badgeLevel;
            if (Vb.val(handlingField(fields, 0)) == 0L || badgePrefix.isEmpty()) {
                return "";
            }
            String escapedUserId = Functions.Proc_10_11_80A9C0(userId, 0, 0);
            String escapedBadgePrefix = Functions.Proc_10_11_80A9C0(badgePrefix, 0, 0);
            String escapedBadgeId = Functions.Proc_10_11_80A9C0(badgeId, 0, 0);
            MySQL.Proc_5_0_6D3CD0("DELETE FROM users_badges WHERE id_user='" + escapedUserId
                + "' AND id_badge LIKE '" + escapedBadgePrefix + "%' LIMIT 1", 0, 0);
            MySQL.Proc_5_0_6D3CD0("INSERT INTO users_badges(id_user,id_badge) VALUES('"
                + escapedUserId + "','" + escapedBadgeId + "')", 0, 0);
            long badgeRowId = Vb.val(MySQL.Proc_5_2_6D4690("SELECT id FROM users_badges WHERE id_user='"
                + escapedUserId + "' AND id_badge='" + escapedBadgeId + "' ORDER BY id DESC LIMIT 1", 0, 0));
            String payload = achievementRewardPayload(achievementIndex, achievementRow, badgeLevel, badgeRowId);
            Proc_6_244_801E80(socketIndex, payload, 0);
            String awardPayload = achievementAwardPayload(achievementRow);
            if (!awardPayload.isEmpty()) {
                long rewardIncrease = Vb.val(handlingField(fields, 3));
                long scoreIncrease = Vb.val(handlingField(fields, 5));
                long rewardType = Vb.val(handlingField(fields, 6));
                MySQL.Proc_5_0_6D3CD0("UPDATE users SET activitypoints_" + rewardType + "=activitypoints_"
                    + rewardType + "+" + rewardIncrease + ",achievement_score=achievement_score+" + scoreIncrease
                    + " WHERE id='" + escapedUserId + "'", 0, 0);
                Proc_6_244_801E80(socketIndex, awardPayload, 0);
            }
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_205_7D9780(Object... args) {
        try {
            if (args == null || args.length < 2) {
                return "";
            }
            int socketIndex = handlingSocketIndex(args);
            if (socketIndex <= 0 && args.length >= 2) {
                socketIndex = (int) Vb.val(args[1]);
            }
            long achievementQuestId = Vb.val(args[args.length - 1]);
            if (socketIndex <= 0 || achievementQuestId <= 0L) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            String achievementRows = achievementRowsFromGlobal();
            if (achievementRows.isEmpty()) {
                return "";
            }
            AchievementProgressDecision decision = achievementProgressDecision(
                achievementRows,
                achievementQuestId,
                achievementCurrentLevels(userId, achievementRows),
                representedAchievementProgress(userId, achievementQuestId));
            if (decision.shouldReward) {
                Proc_6_204_7D82E0(socketIndex, decision.achievementIndex, decision.nextLevel);
            }
            return "";
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_206_7DA450(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            String achievementRows = achievementRowsFromGlobal();
            if (achievementRows.isEmpty()) {
                return "";
            }
            String payload = achievementListPayload(achievementRows, achievementCurrentLevels(userId, achievementRows));
            Proc_6_244_801E80(socketIndex, payload, 0);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static long Proc_6_207_7DB0D0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            long roomId = 0L;
            if (socketIndex > 0) {
                String userId = handlingUserIdFromSocket(socketIndex);
                if (!userId.isEmpty() && !"0".equals(userId)) {
                    roomId = handlingCurrentRoomId(socketIndex, userId);
                }
            }
            if (roomId <= 0L && args != null && args.length >= 2) {
                roomId = Vb.val(args[1]);
            }
            long triggerCode = args != null && args.length >= 3 ? Vb.val(args[2]) : 0L;
            return handlingRepresentedWiredTrigger(roomId, triggerCode, socketIndex);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return 0L;
        }
    }

    public static long Proc_6_208_7DC030(Object... args) {
        return handlingRepresentedWiredActionCall(args, 506);
    }

    public static long Proc_6_209_7DE480(Object... args) {
        return handlingRepresentedWiredActionCall(args, 505);
    }

    public static String Proc_6_210_7E1DC0(Object... args) {
        return "";
    }

    public static long Proc_6_211_7E1E40(Object... args) {
        return handlingRepresentedWiredTriggerCall(args, 1004);
    }

    public static long Proc_6_212_7E36C0(Object... args) {
        return handlingRepresentedWiredTriggerCall(args, 1001);
    }

    public static long Proc_6_213_7E3FA0(Object... args) {
        return handlingRepresentedWiredTriggerCall(args, 1003);
    }

    public static long Proc_6_214_7E60C0(Object... args) {
        return handlingRepresentedWiredTriggerCall(args, 1002);
    }

    public static long Proc_6_215_7E6770(Object... args) {
        return handlingRepresentedWiredActionCall(args, 503);
    }

    public static long Proc_6_216_7E8120(Object... args) {
        return handlingRepresentedWiredActionCall(args, 502);
    }

    public static long Proc_6_217_7E9780(Object... args) {
        return handlingRepresentedWiredActionCall(args, 501);
    }

    public static String Proc_6_218_7EA200(Object... args) {
        try {
            if (args == null || args.length == 0) {
                return "";
            }
            return wiredSpecialStatePayload(Vb.val(args[0]));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_219_7EA390(Object... args) {
        return handlingRepresentedWiredEdit(args, "oj", 1, 500, "wired_trigger", false);
    }

    public static String Proc_6_220_7EBA50(Object... args) {
        return handlingRepresentedWiredEdit(args, "ok", 501, 1000, "wired_action", true);
    }

    public static String Proc_6_221_7ED1E0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "on");
            LongRef offset = new LongRef(1);
            long furnitureId = readWireLong(requestPayload, offset);
            if (furnitureId <= 0L) {
                furnitureId = Vb.val(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
            }
            if (socketIndex <= 0 || furnitureId <= 0L) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L || (!handlingUserHasRoomRight(userId, roomId) && !handlingUserOwnsRoom(userId, roomId))) {
                return "";
            }
            String rowText = MySQL.Proc_5_2_6D4690("SELECT id_product FROM furnitures WHERE id='"
                + furnitureId + "' AND id_room='" + roomId + "' LIMIT 1", 0, 0);
            if (rowText.isEmpty()) {
                return "";
            }
            String[] fields = rowText.split("\t", -1);
            long productId = Vb.val(handlingField(fields, 0));
            long wiredCode = Vb.val(DataManager.Proc_8_12_806C30(productId, 27, 0));
            if (productId <= 0L || wiredCode <= 0L) {
                return "";
            }
            Path snapshotPath = Path.of(Functions.applicationPath, "cache", "wired_snapshots", furnitureId + ".cache");
            Files.createDirectories(snapshotPath.getParent());
            DataManager.Proc_8_10_8068E0(snapshotPath.toString(), Licence.global_00829310, wiredCode);
            return snapshotPath.toString();
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_222_7ED710(Object... args) {
        return handlingRepresentedWiredEdit(args, "ol", 1001, 1500, "wired_condition", false);
    }

    public static String Proc_6_223_7EEDD0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            if (socketIndex <= 0) {
                return "";
            }
            SongInfoRequest request = songInfoRequestFromWire(handlingPacketPayload(args));
            String rowText = "";
            if (!request.requestedIds.isEmpty()) {
                StringBuilder whereClause = new StringBuilder();
                for (String cdId : request.requestedIds.split(",", -1)) {
                    if (!cdId.isEmpty()) {
                        if (whereClause.length() > 0) {
                            whereClause.append(" OR ");
                        }
                        whereClause.append("id='").append(cdId).append("'");
                    }
                }
                if (whereClause.length() > 0) {
                    rowText = MySQL.Proc_5_2_6D4690("SELECT title,sequence,author,sound,id FROM soundmachine_cds WHERE "
                        + whereClause + " LIMIT " + request.requestedCount, 0, 0);
                }
            }
            String payload = songInfoPayload(rowText);
            Proc_6_244_801E80(socketIndex, payload, 0);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_224_7EF5A0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            long roomId = args != null && args.length >= 2 ? Vb.val(args[1]) : 0L;
            long jukeboxId = args != null && args.length >= 3 ? Vb.val(args[2]) : 0L;
            if (socketIndex > 0) {
                String userId = handlingUserIdFromSocket(socketIndex);
                if (!userId.isEmpty() && !"0".equals(userId) && roomId <= 0L) {
                    roomId = handlingCurrentRoomId(socketIndex, userId);
                }
            }
            if (jukeboxId <= 0L && roomId > 0L) {
                jukeboxId = Vb.val(handlingField(jukeboxRow(roomId), 0));
            }
            if (jukeboxId > 0L) {
                long activeDestinationId = Vb.val(MySQL.Proc_5_2_6D4690("SELECT id_destination FROM soundmachine_jb_playlist WHERE id_jukebox='"
                    + jukeboxId + "' AND id_order='0' LIMIT 1", 0, 0));
                Licence.global_008291FC = removeSoundMachineMarkers(Licence.global_008291FC, jukeboxId, activeDestinationId);
            }
            return "";
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_225_7EFBD0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            if (socketIndex <= 0) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            JukeboxAddRequest request = jukeboxAddRequestFromWire(handlingPacketPayload(args));
            if (request.diskFurnitureId <= 0L) {
                return "";
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            String[] jukeboxFields = jukeboxRow(roomId);
            long jukeboxId = Vb.val(handlingField(jukeboxFields, 0));
            long jukeboxProductId = Vb.val(handlingField(jukeboxFields, 1));
            if (jukeboxId <= 0L) {
                return "";
            }
            String maxOrderText = MySQL.Proc_5_2_6D4690("SELECT MAX(id_order) FROM soundmachine_jb_playlist WHERE id_jukebox='"
                + jukeboxId + "'", 0, 0);
            long playlistCount = Vb.val(MySQL.Proc_5_2_6D4690("SELECT COUNT(*) FROM soundmachine_jb_playlist WHERE id_jukebox='"
                + jukeboxId + "'", 0, 0));
            long playlistLimit = Vb.val(Functions.Proc_10_0_809570(
                "com.server.socket.game.jukebox." + jukeboxProductId + ".soundsets.max", 0, 0));
            if (!jukeboxCanAddDisk(request.playlistOrder, maxOrderText, playlistCount, playlistLimit)) {
                return "";
            }
            long songDiskProductId = Vb.val(Functions.Proc_10_0_809570("com.server.socket.game.default.songdisk", 0, 0));
            if (songDiskProductId <= 0L) {
                return "";
            }
            String escapedUserId = Functions.Proc_10_11_80A9C0(userId, 0, 0);
            long destinationId = Vb.val(MySQL.Proc_5_2_6D4690("SELECT id_destination FROM furnitures WHERE id_owner='"
                + escapedUserId + "' AND id='" + request.diskFurnitureId + "' AND id_product='"
                + songDiskProductId + "' LIMIT 1", 0, 0));
            if (destinationId <= 0L) {
                return "";
            }
            MySQL.Proc_5_0_6D3CD0("UPDATE furnitures SET id_owner=NULL WHERE id_owner='" + escapedUserId
                + "' AND id='" + request.diskFurnitureId + "' AND id_product='" + songDiskProductId + "' LIMIT 1", 0, 0);
            MySQL.Proc_5_0_6D3CD0("INSERT INTO soundmachine_jb_playlist(id_jukebox,id_cd,id_order,id_destination) VALUES('"
                + jukeboxId + "','" + request.diskFurnitureId + "','" + request.playlistOrder + "','" + destinationId + "')", 0, 0);
            String payload = Crypto.Proc_3_0_6D2AF0(request.diskFurnitureId, null, "Ac");
            Proc_6_244_801E80(socketIndex, payload, 0);
            Proc_6_227_7F2400(socketIndex);
            Proc_6_228_7F2AF0(socketIndex);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_226_7F0B20(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            if (socketIndex <= 0) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long playlistOrder = jukeboxRemoveOrderFromWire(handlingPacketPayload(args));
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            String[] jukeboxFields = jukeboxRow(roomId);
            long jukeboxId = Vb.val(handlingField(jukeboxFields, 0));
            if (jukeboxId <= 0L) {
                return "";
            }
            long cdFurnitureId = Vb.val(MySQL.Proc_5_2_6D4690("SELECT id_cd FROM soundmachine_jb_playlist WHERE id_jukebox='"
                + jukeboxId + "' AND id_order='" + playlistOrder + "' LIMIT 1", 0, 0));
            if (cdFurnitureId <= 0L) {
                return "";
            }
            String escapedUserId = Functions.Proc_10_11_80A9C0(userId, 0, 0);
            long songDiskProductId = Vb.val(Functions.Proc_10_0_809570("com.server.socket.game.default.songdisk", 0, 0));
            if (songDiskProductId > 0L) {
                MySQL.Proc_5_0_6D3CD0("UPDATE furnitures SET id_owner='" + escapedUserId + "' WHERE id='"
                    + cdFurnitureId + "' AND id_product='" + songDiskProductId + "' LIMIT 1", 0, 0);
            } else {
                MySQL.Proc_5_0_6D3CD0("UPDATE furnitures SET id_owner='" + escapedUserId + "' WHERE id='"
                    + cdFurnitureId + "' LIMIT 1", 0, 0);
            }
            MySQL.Proc_5_0_6D3CD0("DELETE FROM soundmachine_jb_playlist WHERE id_jukebox='" + jukeboxId
                + "' AND id_cd='" + cdFurnitureId + "' LIMIT 1", 0, 0);
            MySQL.Proc_5_0_6D3CD0("UPDATE soundmachine_jb_playlist SET id_order=id_order-1 WHERE id_jukebox='"
                + jukeboxId + "' AND id_order>'" + playlistOrder + "'", 0, 0);
            Proc_6_227_7F2400(socketIndex);
            Proc_6_228_7F2AF0(socketIndex);
            return "";
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_227_7F2400(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            if (socketIndex <= 0) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return "";
            }
            String[] jukeboxFields = jukeboxRow(roomId);
            long jukeboxId = Vb.val(handlingField(jukeboxFields, 0));
            long jukeboxProductId = Vb.val(handlingField(jukeboxFields, 1));
            if (jukeboxId <= 0L) {
                return "";
            }
            long playlistLimit = Vb.val(Functions.Proc_10_0_809570("com.server.socket.game.jukebox." + jukeboxProductId + ".soundsets.max", 0, 0));
            if (playlistLimit <= 0L) {
                playlistLimit = Vb.val(MySQL.Proc_5_2_6D4690("SELECT MAX(id_order)+1 FROM soundmachine_jb_playlist WHERE id_jukebox='"
                    + jukeboxId + "'", 0, 0));
            }
            if (playlistLimit <= 0L) {
                playlistLimit = 100L;
            }
            String rows = MySQL.Proc_5_2_6D4690("SELECT id_cd,id_destination FROM soundmachine_jb_playlist WHERE id_jukebox='"
                + jukeboxId + "' ORDER BY id_order ASC LIMIT " + playlistLimit, 0, 0);
            String payload = jukeboxPlaylistPayload(playlistLimit, rows);
            Proc_6_244_801E80(socketIndex, payload, 0);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_228_7F2AF0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long songDiskProductId = Vb.val(Functions.Proc_10_0_809570("com.server.socket.game.default.songdisk", 0, 0));
            if (songDiskProductId <= 0L) {
                return "";
            }
            String rows = MySQL.Proc_5_2_6D4690("SELECT id,id_destination FROM furnitures WHERE id_owner='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "' AND id_product='" + songDiskProductId + "' LIMIT 250", 0, 0);
            String payload = songDiskInventoryPayload(rows);
            Proc_6_244_801E80(socketIndex, payload, 0);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_229_7F3070(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            if (socketIndex <= 0) {
                return "";
            }
            long roomId = args != null && args.length >= 2 ? Vb.val(args[1]) : 0L;
            long jukeboxId = args != null && args.length >= 3 ? Vb.val(args[2]) : 0L;
            String userId = handlingUserIdFromSocket(socketIndex);
            if (!userId.isEmpty() && !"0".equals(userId) && roomId <= 0L) {
                roomId = handlingCurrentRoomId(socketIndex, userId);
            }
            if (jukeboxId <= 0L && roomId > 0L) {
                jukeboxId = Vb.val(handlingField(jukeboxRow(roomId), 0));
            }
            if (jukeboxId <= 0L) {
                return "";
            }
            String rowText = MySQL.Proc_5_2_6D4690("SELECT soundmachine_jb_playlist.id_destination,soundmachine_jb_playlist.id_cd,soundmachine_cds.sequence "
                + "FROM soundmachine_jb_playlist,soundmachine_cds WHERE soundmachine_jb_playlist.id_jukebox='"
                + jukeboxId + "' AND soundmachine_jb_playlist.id_order='0' AND soundmachine_cds.id=soundmachine_jb_playlist.id_destination "
                + "GROUP BY soundmachine_cds.id LIMIT 1", 0, 0);
            if (rowText.isEmpty()) {
                return "";
            }
            String[] fields = rowText.split("\t", -1);
            long destinationId = Vb.val(handlingField(fields, 0));
            long diskFurnitureId = Vb.val(handlingField(fields, 1));
            long sequenceId = Vb.val(handlingField(fields, 2));
            String payload = jukeboxPlaybackPayload(System.currentTimeMillis() / 1000L, sequenceId, destinationId, diskFurnitureId);
            if (payload.isEmpty()) {
                return "";
            }
            if (roomId > 0L) {
                Proc_6_246_8024C0(roomId, payload, 0);
            } else {
                Proc_6_247_8027E0(socketIndex, payload, 0);
            }
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_230_7F3D20(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "Gd");
            String rawMotto = readWireString(requestPayload, new LongRef(1));
            if (rawMotto.isEmpty()) {
                rawMotto = Functions.Proc_10_7_80A190(requestPayload, 0, 0);
            }
            if (rawMotto.isEmpty()) {
                rawMotto = requestPayload;
            }
            String mottoText = left(Functions.Proc_10_10_80A7F0(rawMotto, 0, 0), 255);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            String escapedUserId = Functions.Proc_10_11_80A9C0(userId, 0, 0);
            MySQL.Proc_5_0_6D3CD0("UPDATE users SET motto='" + Functions.Proc_10_11_80A9C0(mottoText, 0, 0)
                + "' WHERE id='" + escapedUserId + "'", 0, 0);
            String rowText = MySQL.Proc_5_2_6D4690("SELECT figure,gender FROM users WHERE id='" + escapedUserId + "' LIMIT 1", 0, 0);
            String[] fields = rowText.split("\t", -1);
            String figureText = handlingField(fields, 0);
            String genderText = left(handlingField(fields, 1).toUpperCase(), 1);
            if (!"M".equals(genderText) && !"F".equals(genderText)) {
                genderText = "M";
            }
            String payload = userIdentityPayload(Vb.val(userId), mottoText, genderText, figureText);
            Proc_6_244_801E80(socketIndex, payload, 0);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static long Proc_7F44D0(Object... args) {
        try {
            String packetPayload = handlingPacketPayload(args);
            String requestPayload = packetPayload.startsWith("oL") ? packetPayload.substring(2) : packetPayload;
            if (packetPayload.length() >= 3) {
                Functions.Proc_10_5_809D80(packetPayload, 3, 0);
            }
            String valueText = Functions.Proc_10_6_809F10(requestPayload, 0, 0);
            if (valueText.isEmpty()) {
                valueText = readWireString(requestPayload, new LongRef(1));
            }
            return Vb.val(valueText);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return 0L;
        }
    }

    public static String Proc_6_231_7F4510(Object... args) {
        try {
            Proc_6_244_801E80(handlingSocketIndex(args), "Ic" + "IQA", 0);
            return "";
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_232_7F45A0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long requestedQuestId = questRequestIdFromWire(handlingPacketPayload(args), "p^");
            if (requestedQuestId <= 0L) {
                requestedQuestId = Vb.val(Functions.Proc_10_6_809F10(handlingRequestPayload(args, "p^"), 0, 0));
            }
            if (requestedQuestId <= 0L) {
                return "";
            }
            String[] questFields = questFieldsById(questRowsFromSource(), requestedQuestId);
            if (questFields.length < 11) {
                return "";
            }
            long questId = Vb.val(handlingField(questFields, 0));
            long activityCount = Vb.val(handlingField(questFields, 9));
            long waitAmount = Vb.val(handlingField(questFields, 10));
            if (activityCount <= 0L) {
                activityCount = 1L;
            }
            String escapedUserId = Functions.Proc_10_11_80A9C0(userId, 0, 0);
            MySQL.Proc_5_0_6D3CD0("UPDATE users_quests SET timestamp_accepted=NULL WHERE id_user='"
                + escapedUserId + "' AND timestamp_accepted IS NOT NULL AND timestamp_done IS NULL LIMIT 1", 0, 0);
            String existingLevelText = MySQL.Proc_5_2_6D4690("SELECT id_level FROM users_quests WHERE id_user='"
                + escapedUserId + "' AND id_quest='" + questId + "' LIMIT 1", 0, 0);
            if (!existingLevelText.isEmpty()) {
                MySQL.Proc_5_0_6D3CD0("UPDATE users_quests SET timestamp_done=NULL,timestamp_accepted=UNIX_TIMESTAMP(),id_numericquest='"
                    + requestedQuestId + "',time_next=NULL WHERE id_user='" + escapedUserId
                    + "' AND id_quest='" + questId + "' LIMIT 1", 0, 0);
            } else {
                MySQL.Proc_5_0_6D3CD0("INSERT INTO users_quests(id_user,id_quest,id_level,id_numericquest,timestamp_accepted) VALUES('"
                    + escapedUserId + "','" + questId + "','0','" + requestedQuestId + "',UNIX_TIMESTAMP())", 0, 0);
            }
            long progressValue = Vb.val(MySQL.Proc_5_2_6D4690("SELECT progress FROM users_quests WHERE id_user='"
                + escapedUserId + "' AND id_quest='" + questId + "' LIMIT 1", 0, 0));
            if (waitAmount > 0L && progressValue > 0L && progressValue < activityCount) {
                String timeNextText = MySQL.Proc_5_2_6D4690("SELECT time_next FROM users_quests WHERE id_user='"
                    + escapedUserId + "' AND id_quest='" + questId + "' LIMIT 1", 0, 0);
                if (timeNextText.isEmpty() || "0".equals(timeNextText)) {
                    MySQL.Proc_5_0_6D3CD0("UPDATE users_quests SET time_next=DATE_ADD(NOW(),INTERVAL " + waitAmount
                        + " SECOND) WHERE id_user='" + escapedUserId + "' AND id_quest='" + questId + "' LIMIT 1", 0, 0);
                }
            }
            if (progressValue >= activityCount) {
                Proc_6_164_7BC820(socketIndex, questId, requestedQuestId);
            } else {
                Proc_6_236_7F8540(socketIndex, "", "");
            }
            return "";
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_233_7F5D60(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            String escapedUserId = Functions.Proc_10_11_80A9C0(userId, 0, 0);
            String activeRow = MySQL.Proc_5_2_6D4690("SELECT id_quest,id_level FROM users_quests WHERE id_user='"
                + escapedUserId + "' AND timestamp_accepted IS NOT NULL AND timestamp_done IS NULL LIMIT 1", 0, 0);
            if (activeRow.isEmpty()) {
                activeRow = MySQL.Proc_5_2_6D4690("SELECT id_quest,id_level FROM users_quests WHERE id_user='"
                    + escapedUserId + "' ORDER BY timestamp_done DESC,timestamp_accepted DESC,id_level DESC LIMIT 1", 0, 0);
            }
            long requestedQuestId = nextQuestId(questRowsFromSource(), activeRow);
            if (requestedQuestId > 0L) {
                Proc_6_232_7F45A0(socketIndex, "p^" + Crypto.Proc_3_0_6D2AF0(requestedQuestId, null, ""));
            }
            return "";
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_234_7F75C0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            String escapedUserId = Functions.Proc_10_11_80A9C0(userId, 0, 0);
            MySQL.Proc_5_0_6D3CD0("UPDATE users_quests SET timestamp_done=NULL,timestamp_accepted=NULL WHERE id_user='"
                + escapedUserId + "' LIMIT 50", 0, 0);
            Proc_6_244_801E80(socketIndex, "Lc", 0);
            Proc_6_236_7F8540(socketIndex, "", "");
            return "";
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_235_7F77E0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            String escapedUserId = Functions.Proc_10_11_80A9C0(userId, 0, 0);
            String activeRow = MySQL.Proc_5_2_6D4690("SELECT id_quest,id_numericquest,progress,id_level,time_next FROM users_quests WHERE id_user='"
                + escapedUserId + "' AND timestamp_accepted IS NOT NULL AND timestamp_done IS NULL LIMIT 1", 0, 0);
            if (activeRow.isEmpty()) {
                return "";
            }
            String[] fields = activeRow.split("\t", -1);
            long questId = Vb.val(handlingField(fields, 0));
            long numericQuestId = Vb.val(handlingField(fields, 1));
            String timeNextText = handlingField(fields, 4);
            long remainingWait = 0L;
            if (!timeNextText.isEmpty() && !"0".equals(timeNextText)) {
                remainingWait = Vb.val(MySQL.Proc_5_2_6D4690("SELECT GREATEST(0,UNIX_TIMESTAMP('"
                    + Functions.Proc_10_11_80A9C0(timeNextText, 0, 0) + "')-UNIX_TIMESTAMP())", 0, 0));
            }
            QuestProgressDecision decision = questProgressDecision(activeRow, questRowsFromSource(), remainingWait);
            if (decision.shouldScheduleWait) {
                MySQL.Proc_5_0_6D3CD0("UPDATE users_quests SET time_next=DATE_ADD(NOW(),INTERVAL " + decision.waitAmount
                    + " SECOND) WHERE id_user='" + escapedUserId + "' AND id_quest='" + questId + "' LIMIT 1", 0, 0);
            }
            if (decision.shouldComplete) {
                Proc_6_164_7BC820(socketIndex, questId, numericQuestId);
            } else if (decision.shouldSendList) {
                Proc_6_236_7F8540(socketIndex, "", "");
            }
            return "";
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_236_7F8540(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            String escapedUserId = Functions.Proc_10_11_80A9C0(userId, 0, 0);
            String userQuestRows = MySQL.Proc_5_2_6D4690(
                "SELECT id_quest,id_level,timestamp_done,timestamp_accepted,time_next,progress FROM users_quests WHERE id_user='"
                    + escapedUserId + "' LIMIT 250", "\r", 0);
            String payload = questListPayload(questRowsFromSource(), questRowsWithRemainingWait(userQuestRows));
            Proc_6_244_801E80(socketIndex, payload, 0);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_237_7F9ED0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            String rowText = MySQL.Proc_5_2_6D4690("SELECT id,name,motto,gender,respect_amount,scratch_amount FROM users WHERE id='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "' LIMIT 1", 0, 0);
            String payload = ownProfilePayload(rowText);
            if (!payload.isEmpty()) {
                Proc_6_244_801E80(socketIndex, payload, 0);
            }
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_7FA5A0(Object... args) {
        return "";
    }

    public static String Proc_6_238_7FA670(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long sessionSeconds = representedActivityPointSessionSeconds(socketIndex, userId);
            if (sessionSeconds <= 0L) {
                return "";
            }
            StringBuilder sentPayloads = new StringBuilder();
            String escapedUserId = Functions.Proc_10_11_80A9C0(userId, 0, 0);
            for (long pointType = 0L; pointType <= 4L; pointType++) {
                long intervalSeconds = Vb.val(Functions.Proc_10_0_809570(
                    "com.server.socket.game.activitypoints_" + pointType + ".interval", 0, 0));
                if (intervalSeconds > 0L && sessionSeconds % intervalSeconds == 0L) {
                    String columnName = "activitypoints_" + pointType;
                    long maxPoints = Vb.val(Functions.Proc_10_0_809570(
                        "com.server.socket.game.activitypoints_" + pointType + ".max", 1, 0));
                    long currentPoints = Vb.val(MySQL.Proc_5_2_6D4690("SELECT " + columnName + " FROM users WHERE id='"
                        + escapedUserId + "' LIMIT 1", 0, 0));
                    long awardAmount = Vb.val(Functions.Proc_10_0_809570(
                        "com.server.socket.game.activitypoints_" + pointType + ".amount", 0, 0));
                    ActivityPointAward award = activityPointAwardDecision(
                        sessionSeconds, pointType, intervalSeconds, maxPoints, awardAmount, currentPoints);
                    if (award.shouldAward) {
                        MySQL.Proc_5_0_6D3CD0("UPDATE users SET " + columnName + "=" + columnName + "+"
                            + awardAmount + " WHERE id='" + escapedUserId + "'", 0, 0);
                        Proc_6_244_801E80(socketIndex, award.payload, 0);
                        sentPayloads.append(award.payload);
                    }
                }
            }
            return sentPayloads.toString();
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_168_7C05F0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "@b");
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L) {
                return "";
            }
            LongRef offset = new LongRef(1);
            long targetCount = readWireLong(requestPayload, offset);
            if (targetCount <= 0L) {
                return "";
            }
            targetCount = Math.min(targetCount, 150L);
            StringBuilder targetList = new StringBuilder();
            for (long targetIndex = 1L; targetIndex <= targetCount; targetIndex++) {
                String targetUserId = String.valueOf(readWireLong(requestPayload, offset));
                if (!targetUserId.isEmpty() && !"0".equals(targetUserId)
                    && !("," + targetList + ",").contains("," + targetUserId + ",")) {
                    String friendshipRow = MySQL.Proc_5_2_6D4690("SELECT id_user FROM friendships WHERE has_accept='1' AND ((id_user='"
                        + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "' AND id_friend='"
                        + Functions.Proc_10_11_80A9C0(targetUserId, 0, 0) + "') OR (id_user='"
                        + Functions.Proc_10_11_80A9C0(targetUserId, 0, 0) + "' AND id_friend='"
                        + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "')) LIMIT 1", 0, 0);
                    if (!friendshipRow.isEmpty() && handlingSocketFromUserId(targetUserId) > 0) {
                        if (targetList.length() > 0) {
                            targetList.append(',');
                        }
                        targetList.append(targetUserId);
                    }
                }
            }
            String inviteText = Functions.Proc_10_7_80A190(requestPayload, 0, 0);
            if (inviteText.length() > 122) {
                inviteText = inviteText.substring(0, 122);
            }
            if (inviteText.isEmpty()) {
                inviteText = readWireString(requestPayload, offset);
                if (inviteText.length() > 122) {
                    inviteText = inviteText.substring(0, 122);
                }
            }
            String filteredText = Proc_6_22_6E9300(inviteText, 0, 0);
            String payload = Crypto.Proc_3_0_6D2AF0(Vb.val(userId), null, "BG") + filteredText + '\2';
            if (targetList.length() > 0) {
                for (String targetUserId : targetList.toString().split(",", -1)) {
                    int targetSocketIndex = handlingSocketFromUserId(targetUserId);
                    if (targetSocketIndex > 0) {
                        Proc_6_244_801E80(targetSocketIndex, payload, 0);
                        MySQL.Proc_5_1_6D4110("INSERT INTO logs_chat(id_user,id_room,timestamp,description,id_type,id_session) VALUES('"
                            + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "','" + roomId + "',UNIX_TIMESTAMP(),'"
                            + Functions.Proc_10_11_80A9C0("(Invite To: " + handlingUserName(targetUserId) + ") -- " + inviteText, 0, 0)
                            + "','4','" + socketIndex + "')", 0, 0);
                    }
                }
            }
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_167_7BECA0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "@e");
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            LongRef offset = new LongRef(1);
            long acceptCount = readWireLong(requestPayload, offset);
            if (acceptCount <= 0L) {
                return "";
            }
            acceptCount = Math.min(acceptCount, 75L);
            String dateFormat = Functions.Proc_10_0_809570("com.mysql.format.date", "%d-%m-%Y", 0);
            String timeFormat = Functions.Proc_10_0_809570("com.mysql.format.time", "%H:%i", 0);
            String dateTimeFormat = Functions.Proc_10_11_80A9C0(dateFormat + " " + timeFormat, 0, 0);
            StringBuilder targetIds = new StringBuilder();
            for (long acceptIndex = 1L; acceptIndex <= acceptCount; acceptIndex++) {
                String targetUserId = String.valueOf(readWireLong(requestPayload, offset));
                if (!targetUserId.isEmpty() && !"0".equals(targetUserId)
                    && !("," + targetIds + ",").contains("," + targetUserId + ",")) {
                    String rowText = MySQL.Proc_5_2_6D4690("SELECT users.id,users.name,users.motto,users.figure,users.level,users.id_socket,"
                        + "DATE_FORMAT(FROM_UNIXTIME(users.lastonline_time), '" + dateTimeFormat
                        + "') FROM users,friendships WHERE friendships.has_accept='0' AND friendships.id_user='"
                        + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "' AND friendships.id_friend='"
                        + Functions.Proc_10_11_80A9C0(targetUserId, 0, 0)
                        + "' AND users.id=friendships.id_friend LIMIT 1", 0, 0);
                    if (!rowText.isEmpty()) {
                        if (targetIds.length() > 0) {
                            targetIds.append(',');
                        }
                        targetIds.append(targetUserId);
                    }
                }
            }
            if (targetIds.length() == 0) {
                return "";
            }
            long acceptedCount = 0L;
            StringBuilder payloadRows = new StringBuilder();
            for (String targetUserId : targetIds.toString().split(",", -1)) {
                String rowText = MySQL.Proc_5_2_6D4690("SELECT id,name,motto,figure,level,id_socket,DATE_FORMAT(FROM_UNIXTIME(lastonline_time), '"
                    + dateTimeFormat + "') FROM users WHERE id='" + Functions.Proc_10_11_80A9C0(targetUserId, 0, 0)
                    + "' LIMIT 1", 0, 0);
                if (!rowText.isEmpty()) {
                    String[] fields = rowText.split("\t", -1);
                    if (fields.length >= 7) {
                        int targetSocketIndex = (int) Vb.val(handlingField(fields, 5));
                        payloadRows.append('H').append(messengerFriendPayload(
                            Vb.val(handlingField(fields, 0)),
                            handlingField(fields, 1),
                            handlingField(fields, 2),
                            handlingField(fields, 3),
                            Vb.val(handlingField(fields, 4)),
                            targetSocketIndex > 0 ? 2L : 0L,
                            targetSocketIndex > 0 ? 1L : 0L,
                            handlingField(fields, 6),
                            0L));
                        MySQL.Proc_5_0_6D3CD0("INSERT IGNORE INTO friendships(id_user,id_friend,has_accept) VALUES('"
                            + Functions.Proc_10_11_80A9C0(targetUserId, 0, 0) + "','"
                            + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "','0')", 0, 0);
                        MySQL.Proc_5_0_6D3CD0("UPDATE friendships SET has_accept='1' WHERE ((id_user='"
                            + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "' AND id_friend='"
                            + Functions.Proc_10_11_80A9C0(targetUserId, 0, 0) + "') OR (id_user='"
                            + Functions.Proc_10_11_80A9C0(targetUserId, 0, 0) + "' AND id_friend='"
                            + Functions.Proc_10_11_80A9C0(userId, 0, 0)
                            + "')) AND has_accept='0' LIMIT 2", 0, 0);
                        if (targetSocketIndex > 0) {
                            String notifyPayload = "@MHIH" + messengerFriendSummaryPayload(userId, 1L);
                            Proc_6_244_801E80(targetSocketIndex, notifyPayload, 0);
                        }
                        acceptedCount++;
                    }
                }
            }
            if (acceptedCount > 0L) {
                String callerPayload = messengerAcceptedFriendsPayload(payloadRows.toString(), acceptedCount);
                Proc_6_244_801E80(socketIndex, callerPayload, 0);
                return callerPayload;
            }
            return "";
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String Proc_6_169_7C0DC0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, "DF");
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            LongRef offset = new LongRef(1);
            String targetUserId = String.valueOf(readWireLong(requestPayload, offset));
            if (targetUserId.isEmpty() || "0".equals(targetUserId)) {
                targetUserId = String.valueOf((long) Vb.val(Functions.Proc_10_6_809F10(requestPayload, 0, 0)));
            }
            if (targetUserId.isEmpty() || "0".equals(targetUserId)) {
                return "";
            }
            String friendshipRow = MySQL.Proc_5_2_6D4690("SELECT id_friend FROM friendships WHERE id_user='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "' AND id_friend='"
                + Functions.Proc_10_11_80A9C0(targetUserId, 0, 0) + "' AND has_accept='1' LIMIT 1", 0, 0);
            if (friendshipRow.isEmpty()) {
                return "";
            }
            int targetSocketIndex = handlingSocketFromUserId(targetUserId);
            if (targetSocketIndex <= 0) {
                return "";
            }
            long targetRoomId = handlingCurrentRoomId(targetSocketIndex, targetUserId);
            if (targetRoomId <= 0L) {
                return "";
            }
            long targetRoomUserIndex = representedRoomUserIndex(targetSocketIndex, targetUserId);
            String payload = Crypto.Proc_3_0_6D2AF0(targetRoomId, null,
                Crypto.Proc_3_0_6D2AF0(targetRoomUserIndex, null, "D^"));
            Proc_6_244_801E80(socketIndex, payload, 0);
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String handlingField(String[] fields, long fieldIndex) {
        return fields != null && fieldIndex >= 0 && fieldIndex < fields.length ? Vb.cStr(fields[(int) fieldIndex]) : "";
    }

    public static String handlingUserName(String userId) {
        try {
            if (Vb.cStr(userId).isEmpty() || "0".equals(Vb.cStr(userId))) {
                return "";
            }
            return MySQL.Proc_5_2_6D4690("SELECT name FROM users WHERE id='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "' LIMIT 1", 0, 0);
        } catch (Exception ignored) {
            return "";
        }
    }

    public static void Proc_6_241_7FC380(Object... args) {
        try {
            if (args == null || args.length < 2) {
                return;
            }
            long socketIndex = Vb.val(args[0]);
            String packetBuffer = Functions.Proc_10_9_80A680(Vb.cStr(args[1]), 0, 0);
            if (socketIndex <= 0L || Guardian.Proc_11_2_821390(socketIndex, 0, 0) != 1) {
                return;
            }
            long packetCount = 0L;
            while (packetBuffer.length() > 2 && packetCount < 10L) {
                packetBuffer = packetBuffer.substring(1);
                long packetLength = Crypto.Proc_3_4_6D3620(Vb.left(packetBuffer, 2));
                if (packetLength <= 0L || packetBuffer.length() < packetLength + 2L) {
                    break;
                }
                String packetPayload = Vb.mid(packetBuffer, 3, (int) packetLength);
                String packetCode = Vb.left(packetPayload, 2);
                if (Licence.global_00829190 && !Licence.global_00829034) {
                    Console.Proc_2_0_6D1510("[" + socketIndex + "] " + packetPayload, "GAME", "16711680");
                }
                dispatchPreReadyPacket((int) socketIndex, packetCode, packetPayload);
                packetCount++;
                packetBuffer = Vb.mid(packetBuffer, (int) packetLength + 3);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void dispatchPreReadyPacket(int socketIndex, String packetCode, String packetPayload) {
        try {
            switch (Vb.cStr(packetCode)) {
                case "~\u00e4": System.exit(0); break;
                case "oD": Proc_6_231_7F4510(socketIndex, packetPayload, 0); break;
                case "Gd": Proc_6_230_7F3D20(socketIndex, packetPayload, 0); break;
                case "C]": Proc_6_223_7EEDD0(socketIndex, packetPayload, 0); break;
                case "C\u007f": Proc_6_225_7EFBD0(socketIndex, packetPayload, 0); break;
                case "D@": Proc_6_226_7F0B20(socketIndex, packetPayload, 0); break;
                case "DC":
                    Proc_6_227_7F2400(socketIndex, packetPayload, 0);
                    Proc_6_228_7F2AF0(socketIndex, packetPayload, 0);
                    break;
                case "AZ": Proc_6_144_76BE70(socketIndex, "AZ", packetPayload); break;
                case "AC": Proc_6_155_795C90(socketIndex, "AC", packetPayload); break;
                case "A[": Proc_6_141_76A670(socketIndex, "A[", packetPayload); break;
                case "AI": Proc_6_159_79FCD0(socketIndex, "AI", packetPayload); break;
                case "Ch": Proc_6_149_775C10(socketIndex, "Ch", packetPayload); break;
                case "FH": Proc_6_150_777FA0(socketIndex, "FH", packetPayload); break;
                case "@B": Proc_6_78_7279A0(socketIndex, "@B", packetPayload); break;
                case "rv": Proc_6_142_76B310(socketIndex, "rv", packetPayload); break;
                case "pa": Proc_6_244_801E80(socketIndex, "J|H", 0); break;
                case "Ce": dispatchPreReadySoundSetting(socketIndex, packetPayload); break;
                case "Cy": break;
                case "pb": Proc_6_234_7F75C0(socketIndex, "pb", packetPayload); break;
                case "p^": Proc_6_232_7F45A0(socketIndex, "p^", packetPayload); break;
                case "pc": Proc_6_233_7F5D60(socketIndex, "pc", packetPayload); break;
                case "p]": Proc_6_236_7F8540(socketIndex, "p]", packetPayload); break;
                case "GV": Proc_6_38_70FD10(socketIndex, "GV", packetPayload); break;
                case "GW": Proc_6_39_711650(socketIndex, "GW", packetPayload); break;
                case "F]": Proc_6_203_7D7F80(socketIndex, "F]", packetPayload); break;
                case "F^": Proc_6_202_7D6760(socketIndex, "F^", packetPayload); break;
                case "Cj": Proc_6_201_7D5AC0(socketIndex, "Cj", packetPayload); break;
                case "Ck": Proc_6_199_7D54E0(socketIndex, "Ck", packetPayload); break;
                case "Cl": Proc_6_200_7D5770(socketIndex, "Cl", packetPayload); break;
                case "EW": Proc_6_99_748460(socketIndex, "EW", packetPayload); break;
                case "Cw": Proc_6_95_746CD0(socketIndex, "Cw", packetPayload); break;
                case "AL": Proc_6_97_747640(socketIndex, "AL", packetPayload); break;
                case "AM": Proc_6_96_747000(socketIndex, "AM", packetPayload); break;
                case "FU": Proc_6_91_743480(socketIndex, "FU", packetPayload); break;
                case "AH": Proc_6_92_744870(socketIndex, "AH", packetPayload); break;
                case "FR": Proc_6_89_73EA10(socketIndex, "FR", packetPayload); break;
                case "EV": Proc_6_100_748C80(socketIndex, "EV", packetPayload); break;
                case "EU": Proc_6_98_747D80(socketIndex, "EU", packetPayload); break;
                case "Er": Proc_6_206_7DA450(socketIndex, "Er", packetPayload); break;
                case "CD": Proc_7FA5A0(socketIndex, "CD", packetPayload); break;
                case "@G": Proc_6_237_7F9ED0(socketIndex, "@G", packetPayload); break;
                case "D{":
                case "Fe": break;
                case "@t": Proc_6_26_7034C0(socketIndex, "@t", packetPayload); break;
                case "@w": Proc_6_27_706920(socketIndex, "@w", packetPayload); break;
                case "@x": Proc_6_28_709DA0(socketIndex, "@x", packetPayload); break;
                case "Cd": Proc_6_101_749540(socketIndex, "EA", packetPayload); break;
                case "Et":
                case "Eu": Proc_6_102_749C50(socketIndex, packetCode, packetPayload); break;
                case "@Z": Proc_6_19_6E8040(socketIndex, Licence.recyclerSettings().statusPayload(), "Gz"); break;
                case "oW": Proc_6_18_6E7480(socketIndex, "GY", packetPayload); break;
                case "Cn": Proc_6_30_70DC90(socketIndex, packetPayload, "EG"); break;
                case "A^": Proc_6_13_6E0A80(socketIndex, "A^", packetPayload); break;
                case "A]": Proc_6_14_6E10C0(socketIndex, "A]", packetPayload); break;
                case "GE": Proc_6_32_70EAB0(socketIndex, "GE", packetPayload); break;
                case "F`": Proc_6_33_70F4F0(socketIndex); break;
                case "Fa": Proc_6_34_70F590(socketIndex); break;
                case "Fb": Proc_6_37_70FC20(socketIndex, "Fb", packetPayload); break;
                case "Fc": Proc_6_36_70F7B0(socketIndex, "Fc", packetPayload); break;
                case "Fd": Proc_6_35_70F630(socketIndex, "Fd", packetPayload); break;
                case "Ae": dispatchPreReadyCatalogIndex(socketIndex); break;
                case "FC": Proc_6_104_74AB60(socketIndex, "FC", packetPayload); break;
                case "@]": Proc_6_105_74AD50(socketIndex, "@]", packetPayload); break;
                case "Af": Proc_6_136_765F10(socketIndex, "Af", packetPayload); break;
                case "Fv": Proc_6_125_755650(socketIndex, "Fv", packetPayload); break;
                case "FG": Proc_6_58_71FCA0(socketIndex, "FG", packetPayload); break;
                case "Bv": Proc_6_59_71FEE0(socketIndex, "Bv", packetPayload); break;
                case "@{": Proc_6_79_72A430(socketIndex, "@{", packetPayload); break;
                case "Ew": Proc_6_15_6E1900(socketIndex, "Ew", packetPayload); break;
                case "Ex": Proc_6_16_6E2320(socketIndex, "Ex", packetPayload); break;
                case "@l": Proc_6_17_6E48D0(socketIndex, "@l", packetPayload); break;
                case "oC": Proc_6_135_765D80(socketIndex, "oC", packetPayload); break;
                case "oV": Proc_6_134_765B90(socketIndex, "oV", packetPayload); break;
                case "Ad": Proc_6_128_756190(socketIndex, "Ad", packetPayload); break;
                case "GX": Proc_6_132_75D4A0(socketIndex, "GX", packetPayload); break;
                case "GZ": Proc_6_131_75C700(socketIndex, "GZ", packetPayload); break;
                case "G[": Proc_6_130_75B770(socketIndex, "G[", packetPayload); break;
                case "Gc": Proc_6_107_74B7E0(socketIndex, "Gc", packetPayload); break;
                case "GG": Proc_6_10_6DE1D0(socketIndex, "GG", packetPayload); break;
                case "F@": Proc_6_47_714F60(socketIndex, "F@", packetPayload); break;
                case "FB": Proc_6_44_7145E0(socketIndex, "FB", packetPayload); break;
                case "Ab": Proc_6_50_7166B0(socketIndex, "Ab", packetPayload); break;
                case "EZ": Proc_6_48_7151E0(socketIndex, "EZ", packetPayload); break;
                case "E\\": Proc_6_49_715D30(socketIndex, "E\\", packetPayload); break;
                case "FP":
                case "FF": Proc_6_43_713680(socketIndex, "FF", packetPayload); break;
                case "FQ": Proc_6_52_7172B0(socketIndex, "FQ", packetPayload); break;
                case "@H": Proc_6_108_74D800(socketIndex, "@H", packetPayload); break;
                case "@S": Proc_6_110_74DDA0(socketIndex, "@S", packetPayload); break;
                case "@T": Proc_6_109_74DBD0(socketIndex, "@T", packetPayload); break;
                case "BW": Proc_6_111_74DF70(socketIndex, "BW", packetPayload); break;
                case "GI": MySQL.Proc_5_4_6D55E0(socketIndex, "GI", packetPayload); break;
                case "oj": Proc_6_219_7EA390(socketIndex, "oj", packetPayload); break;
                case "ok": Proc_6_220_7EBA50(socketIndex, "ok", packetPayload); break;
                case "ol": Proc_6_222_7ED710(socketIndex, "ol", packetPayload); break;
                case "on": Proc_6_221_7ED1E0(socketIndex, "on", packetPayload); break;
                case "GH": MySQL.Proc_5_5_6D64D0(socketIndex, "GH", packetPayload); break;
                case "GK": MySQL.Proc_5_6_6D7090(socketIndex, "GK", packetPayload); break;
                case "GF": Proc_6_0_6D7FF0(socketIndex, "GF", packetPayload); break;
                case "GJ": Proc_6_11_6DF4A0(socketIndex, "GJ", packetPayload); break;
                case "GM": Proc_6_1_6D8B70(socketIndex, "GM", packetPayload); break;
                case "GN": Proc_6_12_6DFE90(socketIndex, "GN", packetPayload); break;
                case "GO": Proc_6_2_6D9880(socketIndex, "GO", packetPayload); break;
                case "GP": Proc_6_3_6DA490(socketIndex, "GP", packetPayload); break;
                case "CH": Proc_6_4_6DAFB0(socketIndex, "CH", packetPayload); break;
                case "GB": Proc_6_6_6DC9D0(socketIndex, "GB", packetPayload); break;
                case "GC": Proc_6_8_6DD790(socketIndex, "GC", packetPayload); break;
                case "GD": Proc_6_7_6DD0E0(socketIndex, "GD", packetPayload); break;
                case "GL": Proc_6_9_6DDD70(socketIndex, "GL", packetPayload); break;
                case "Fw": Proc_6_115_751220(socketIndex, "Fw", packetPayload); break;
                case "Fn": Proc_6_116_751550(socketIndex, "Fn", packetPayload); break;
                case "Fr": Proc_6_121_752080(socketIndex, "Fr", packetPayload); break;
                case "Fq": Proc_6_117_751880(socketIndex, "Fq", packetPayload); break;
                case "Fp": Proc_6_118_751A80(socketIndex, "Fp", packetPayload); break;
                case "Fs": Proc_6_119_751C80(socketIndex, "Fs", packetPayload); break;
                case "Fo": Proc_6_126_755B40(socketIndex, "Fo", packetPayload); break;
                case "Ft": Proc_6_120_751E80(socketIndex, "Ft", packetPayload); break;
                case "E|": Proc_6_123_754020(socketIndex, "E|", packetPayload); break;
                case "Fu": Proc_6_127_755D30(socketIndex, "Fu", packetPayload); break;
                case "E~": Proc_6_124_754D90(socketIndex, "E~", packetPayload); break;
                case "EY": Proc_6_46_714D50(socketIndex, "EY", packetPayload); break;
                case "Gj": Proc_6_88_73E4F0(socketIndex, "Gj", packetPayload); break;
                case "@L": Proc_6_176_7C4EE0(socketIndex, "@L", packetPayload); break;
                case "@u":
                case "Ao": Proc_6_53_718E00(socketIndex, packetCode, packetPayload); break;
                case "@j": Proc_6_182_7CAAD0(socketIndex, "@j", packetPayload); break;
                case "@f": Proc_6_170_7C1100(socketIndex, "@f", packetPayload); break;
                case "DF": Proc_6_169_7C0DC0(socketIndex, "DF", packetPayload); break;
                case "@O": break;
                case "D}":
                case "D~": dispatchPreReadyRoomUserState(socketIndex); break;
                case "@a": Proc_6_173_7C3430(socketIndex, "@a", packetPayload); break;
                case "Ci": Proc_6_175_7C4800(socketIndex, "Ci", packetPayload); break;
                case "@b": Proc_6_168_7C05F0(socketIndex, "@b", packetPayload); break;
                case "@e": Proc_6_167_7BECA0(socketIndex, "@e", packetPayload); break;
                case "@i": Proc_6_172_7C25B0(socketIndex, "@i", packetPayload); break;
                case "@g": Proc_6_174_7C3BC0(socketIndex, "@g", packetPayload); break;
                case "@h": Proc_6_171_7C1520(socketIndex, "@h", packetPayload); break;
                case "Fy": Proc_6_189_7D0630(socketIndex, "Fy", packetPayload); break;
                case "Fx": Proc_6_188_7CF3C0(socketIndex, "Fx", packetPayload); break;
                case "Cg": Proc_6_190_7D11D0(socketIndex, "Cg", packetPayload); break;
                case "DG": Proc_6_191_7D18B0(socketIndex, "DG", packetPayload); break;
                case "B_": Proc_6_192_7D1B80(socketIndex, "B_", packetPayload); break;
                case "B]": Proc_6_193_7D2BB0(socketIndex, "B]", packetPayload); break;
                case "B^": Proc_6_194_7D3180(socketIndex, "B^", packetPayload); break;
                case "pg": break;
                case "AK": Proc_6_197_7D43C0(socketIndex, "AK", packetPayload); break;
                case "AO": Proc_6_198_7D4B70(socketIndex, "AO", packetPayload); break;
                case "AG": Proc_6_93_745D90(socketIndex, "AG", packetPayload); break;
                case "FT": Proc_6_140_769400(socketIndex, "FT", packetPayload); break;
                case "AB": Proc_6_139_768100(socketIndex, "AB", packetPayload); break;
                case "BA": Proc_6_137_766470(socketIndex, "BA", packetPayload); break;
                case "n\u007f": Proc_6_177_7C6580(socketIndex, "n\u007f", packetPayload); break;
                case "ny": Proc_6_183_7CABF0(socketIndex, "ny", packetPayload); break;
                case "nx": Proc_6_178_7C6E60(socketIndex, "nx", packetPayload); break;
                case "nz": Proc_6_179_7C7790(socketIndex, "nz", packetPayload); break;
                case "p`":
                case "rt": Proc_6_86_73B0D0(socketIndex, packetCode, packetPayload); break;
                case "n~": Proc_6_87_73C120(socketIndex, "n~", packetPayload); break;
                case "n|": Proc_7CC190(socketIndex, "n|", packetPayload); break;
                case "n{": Proc_7CA730(socketIndex, "n{", packetPayload); break;
                case "n}": Proc_6_186_7CD040(socketIndex, "n}", packetPayload); break;
                case "E[": Proc_6_45_714B60(socketIndex, "E[", packetPayload); break;
                case "A_": Proc_6_61_720490(socketIndex, "A_", packetPayload); break;
                case "E@": Proc_6_62_7209F0(socketIndex, "E@", packetPayload); break;
                case "DE": Proc_6_63_721050(socketIndex, "DE", packetPayload); break;
                case "D\u007f": Proc_6_64_721650(socketIndex, "D\u007f", packetPayload); break;
                case "EB": Proc_6_75_7269D0(socketIndex, "EB", packetPayload); break;
                case "Bw": Proc_6_73_725540(socketIndex, "Bw", packetPayload); break;
                case "Aa": Proc_6_74_7265B0(socketIndex, "Aa", packetPayload); break;
                case "B[": Proc_6_71_724CF0(socketIndex, "B[", packetPayload); break;
                case "@W": Proc_6_72_7250D0(socketIndex, "@W", packetPayload); break;
                case "Es": Proc_6_76_726CE0(socketIndex, "Es", packetPayload); break;
                case "FD": Proc_6_77_727590(socketIndex, "FD", packetPayload); break;
                case "A`": Proc_6_65_721A10(socketIndex, "A`", packetPayload); break;
                case "AT": Proc_6_66_721D60(socketIndex, "AT", packetPayload); break;
                case "AS": Proc_6_67_722940(socketIndex, "AS", packetPayload); break;
                case "AU": Proc_6_68_723170(socketIndex, "AU", packetPayload); break;
                case "A~":
                case "CW":
                case "Cf": break;
                case "FA": Proc_6_60_720060(socketIndex, "FA", packetPayload); break;
                case "FI": Proc_6_70_724190(socketIndex, "FI", packetPayload); break;
                case "AN": Proc_6_69_723630(socketIndex, "AN", packetPayload); break;
                case "Aq":
                case "AE":
                case "FS":
                case "AF":
                    Proc_6_244_801E80(socketIndex,
                        Vb.cStr(Functions.Proc_10_0_809570("com.client.park.infobus.theme.title", "AQ")) + '\2',
                        0);
                    break;
                case "oL": Proc_7F44D0(socketIndex, "oL", packetPayload); break;
                default:
                    if (Licence.global_00829034) {
                        Console.Proc_2_0_6D1510(packetPayload, "UNHANDLED -- index: " + socketIndex, "255");
                    }
                    break;
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    private static void dispatchPreReadySoundSetting(int socketIndex, String packetPayload) {
        try {
            long soundSetting = soundSettingFromWire(packetPayload);
            if (soundSetting <= 0L) {
                return;
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            MySQL.Proc_5_0_6D3CD0("UPDATE users SET settings_sound='" + soundSetting + "' WHERE id='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "' LIMIT 1", 0, 0);
        } catch (Exception ignored) {
            // VB6 source suppresses dispatcher helper failures.
        }
    }

    private static void dispatchPreReadyCatalogIndex(int socketIndex) {
        try {
            String pageTree = Licence.catalogPages().defaultPageTree();
            Proc_6_244_801E80(socketIndex, "A~IHHM" + '\2' + pageTree, 0);
        } catch (Exception ignored) {
            // VB6 source suppresses dispatcher helper failures.
        }
    }

    private static void dispatchPreReadyRoomUserState(int socketIndex) {
        try {
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return;
            }
            long roomUserIndex = representedRoomUserIndex(socketIndex, userId);
            if (roomUserIndex <= 0L) {
                return;
            }
            Proc_6_247_8027E0(socketIndex, Crypto.Proc_3_0_6D2AF0(roomUserIndex, null, "Ei") + '\r', 0);
        } catch (Exception ignored) {
            // VB6 source suppresses dispatcher helper failures.
        }
    }

    public static String Proc_6_242_7FF0D0(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (!userId.isEmpty() && !"0".equals(userId)) {
                MySQL.Proc_5_0_6D3CD0("UPDATE users SET id_socket=null WHERE id = '"
                    + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "'", 0, 0);
            }
            return "";
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static void Proc_6_243_7FFEB0(Object... args) {
        int socketIndex = handlingSocketIndex(args);
        if (socketIndex <= 0) {
            return;
        }
        Proc_6_242_7FF0D0(socketIndex, 0, 0);
        String marker = "[" + socketIndex + "]";
        Guardian.setSocketConnected(socketIndex, false);
        Guardian.global_008291A0 = Vb.cStr(Guardian.global_008291A0).replace(marker, "");
        Licence.global_008291A0 = Vb.cStr(Licence.global_008291A0).replace(marker, "");
        Licence.global_00829350 = Vb.cStr(Licence.global_00829350).replace(marker, "");
        Licence.global_00829354 = Vb.cStr(Licence.global_00829354).replace(marker, "");
    }

    public static void Proc_6_244_801E80(Object... args) {
        if (args == null || args.length < 2) {
            return;
        }
        int socketIndex = (int) Vb.val(args[0]);
        if (socketIndex <= 0 || Guardian.Proc_11_2_821390(socketIndex, 0, 0) != 1
            || isSocketMarkedBusy(Vb.cStr(Licence.global_0082934C), socketIndex)) {
            return;
        }
        HandlingMUS.Proc_12_1_821AA0(socketIndex, Vb.cStr(args[1]) + '\1', 0);
    }

    public static long Proc_6_245_801FA0(Object... args) {
        if (args == null || args.length < 2) {
            return 0L;
        }
        int socketIndex = (int) Vb.val(args[0]);
        String userId = handlingUserIdFromSocket(socketIndex);
        long roomId = handlingCurrentRoomId(socketIndex, userId);
        return broadcastToRoomUsers(roomId, Vb.cStr(args[1]));
    }

    public static long Proc_6_246_8024C0(Object... args) {
        if (args == null || args.length < 2) {
            return 0L;
        }
        return broadcastToRoomUsers(Vb.val(args[0]), Vb.cStr(args[1]));
    }

    public static long Proc_6_247_8027E0(Object... args) {
        if (args == null || args.length < 2) {
            return 0L;
        }
        int socketIndex = handlingSocketIndex(args);
        String userId = handlingUserIdFromSocket(socketIndex);
        long roomId = handlingCurrentRoomId(socketIndex, userId);
        return broadcastToRoomUsers(roomId, Vb.cStr(args[1]));
    }

    public static long Proc_6_248_802B80(Object... args) {
        if (args == null || args.length < 2) {
            return 0L;
        }
        return broadcastToRoomUsers(Vb.val(args[0]), Vb.cStr(args[1]));
    }

    public static long Proc_6_249_802F10(Object... args) {
        if (args == null || args.length == 0) {
            return 0L;
        }
        return broadcastToStaffModerators(Vb.cStr(args[0]));
    }

    public static int handlingSocketIndex(Object... args) {
        return args != null && args.length >= 1 ? (int) Vb.val(args[0]) : 0;
    }

    public static String handlingPacketPayload(Object... args) {
        if (args == null) {
            return "";
        }
        String payload = args.length >= 3 ? Vb.cStr(args[2]) : "";
        if (payload.isEmpty() && args.length >= 2) {
            payload = Vb.cStr(args[1]);
        }
        return payload;
    }

    public static String handlingRequestPayload(Object[] args, String prefix) {
        String payload = handlingPacketPayload(args);
        String expectedPrefix = Vb.cStr(prefix);
        if (!expectedPrefix.isEmpty() && payload.startsWith(expectedPrefix)) {
            return payload.substring(expectedPrefix.length());
        }
        return payload;
    }

    public static String handlingUserIdFromSocket(int socketIndex) {
        if (socketIndex <= 0) {
            return "";
        }
        String recordPayload = Licence.getSessionRecordPayload("1:", String.valueOf(socketIndex));
        if (!recordPayload.isEmpty()) {
            String[] fields = recordPayload.split("\2", -1);
            String userId = String.valueOf(Vb.val(handlingField(fields, 0)));
            if (!userId.isEmpty() && !"0".equals(userId)) {
                return userId;
            }
        }
        return String.valueOf(Vb.val(MySQL.Proc_5_2_6D4690("SELECT id FROM users WHERE id_socket='" + socketIndex + "' LIMIT 1", 0, 0)));
    }

    public static int handlingSocketFromUserId(String userId) {
        String idText = String.valueOf(Vb.val(userId));
        if (idText.isEmpty() || "0".equals(idText)) {
            return 0;
        }
        long socketIndex = Licence.Proc_9_8_8086A0(idText, 0, 0);
        if (socketIndex <= 0L) {
            socketIndex = Vb.val(MySQL.Proc_5_2_6D4690("SELECT id_socket FROM users WHERE id='"
                + Functions.Proc_10_11_80A9C0(idText, 0, 0) + "' LIMIT 1", 0, 0));
        }
        return (int) socketIndex;
    }

    public static long handlingCurrentRoomId(int socketIndex, String userId) {
        long roomId = Licence.Proc_9_10_808F30(String.valueOf(socketIndex), 1, 0);
        if (roomId > 0L) {
            return roomId;
        }
        String escapedUserId = Functions.Proc_10_11_80A9C0(userId, 0, 0);
        if (!Vb.cStr(userId).isEmpty() && !"0".equals(Vb.cStr(userId))) {
            roomId = Vb.val(MySQL.Proc_5_2_6D4690("SELECT id_room FROM logs_visitedrooms WHERE id_user='"
                + escapedUserId + "' AND timestamp_left IS NULL ORDER BY timestamp_enter DESC LIMIT 1", 0, 0));
        }
        if (roomId <= 0L) {
            roomId = Vb.val(MySQL.Proc_5_2_6D4690("SELECT id FROM rooms WHERE id_slot='" + socketIndex + "' LIMIT 1", 0, 0));
        }
        return roomId;
    }

    public static long representedRoomUserIndex(int socketIndex, String userId) {
        long roomUserIndex = Vb.val(MySQL.Proc_5_2_6D4690("SELECT id FROM logs_visitedrooms WHERE id_user='"
            + Functions.Proc_10_11_80A9C0(userId, 0, 0)
            + "' AND timestamp_left IS NULL ORDER BY timestamp_enter DESC LIMIT 1", 0, 0));
        return roomUserIndex > 0L ? roomUserIndex : socketIndex;
    }

    public static boolean handlingUserHasPermission(String userId, String permissionName) {
        long rankIndex = Vb.val(MySQL.Proc_5_2_6D4690("SELECT level FROM users WHERE id='"
            + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "' LIMIT 1", 0, 0));
        long hcLevel = Vb.val(MySQL.Proc_5_2_6D4690("SELECT level_hc FROM users WHERE id='"
            + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "' LIMIT 1", 0, 0));
        return Functions.Proc_10_1_809790(rankIndex, "", permissionName, hcLevel);
    }

    private static void roomKickOrBanUser(Object[] args, boolean addRoomBan) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingPacketPayload(args);
            if (requestPayload.startsWith("A_") || requestPayload.startsWith("E@")) {
                requestPayload = requestPayload.substring(2);
            }
            LongRef offset = new LongRef(1);
            String targetUserId = String.valueOf(readWireLong(requestPayload, offset));
            if (targetUserId.isEmpty() || "0".equals(targetUserId)) {
                return;
            }
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)) {
                return;
            }
            long callerRoomId = handlingCurrentRoomId(socketIndex, callerUserId);
            if (callerRoomId <= 0L) {
                return;
            }
            int targetSocketIndex = handlingSocketFromUserId(targetUserId);
            if (targetSocketIndex <= 0) {
                return;
            }
            long targetRoomId = handlingCurrentRoomId(targetSocketIndex, targetUserId);
            if (targetRoomId != callerRoomId) {
                return;
            }
            if (!handlingUserHasPermission(callerUserId, "fuse_kick")
                || handlingUserHasPermission(targetUserId, "fuse_unkickable")) {
                return;
            }
            Proc_6_244_801E80(targetSocketIndex, "@aXjO", 0);
            Proc_6_53_718E00(targetSocketIndex, "@aXjO", 0);
            if (addRoomBan) {
                MySQL.Proc_5_0_6D3CD0("INSERT IGNORE INTO rooms_bans(id_room,id_user,timestamp_expire) VALUES('"
                    + callerRoomId + "','" + Functions.Proc_10_11_80A9C0(targetUserId, 0, 0)
                    + "',UNIX_TIMESTAMP()+900)", 0, 0);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static long handlingUserRank(String userId) {
        return Vb.val(MySQL.Proc_5_2_6D4690("SELECT level FROM users WHERE id='"
            + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "' LIMIT 1", 0, 0));
    }

    public static long handlingUserHcLevel(String userId) {
        long hcLevel = Vb.val(MySQL.Proc_5_2_6D4690("SELECT level_hc FROM users WHERE id='"
            + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "' LIMIT 1", 0, 0));
        if (hcLevel < 0L) {
            return 0L;
        }
        return Math.min(hcLevel, 2L);
    }

    public static String handlingUserSessionId(String userId) {
        if (Vb.cStr(userId).isEmpty() || "0".equals(Vb.cStr(userId))) {
            return "";
        }
        return MySQL.Proc_5_2_6D4690("SELECT id_session FROM users WHERE id='"
            + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "' LIMIT 1", 0, 0);
    }

    public static boolean handlingUserOwnsRoom(String userId, long roomId) {
        if (Vb.cStr(userId).isEmpty() || "0".equals(Vb.cStr(userId)) || roomId <= 0L) {
            return false;
        }
        return !MySQL.Proc_5_2_6D4690("SELECT id FROM rooms WHERE id='" + roomId + "' AND id_owner='"
            + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "' LIMIT 1", 0, 0).isEmpty();
    }

    public static boolean handlingUserHasRoomRight(String userId, long roomId) {
        if (Vb.cStr(userId).isEmpty() || "0".equals(Vb.cStr(userId)) || roomId <= 0L) {
            return false;
        }
        if (!MySQL.Proc_5_2_6D4690("SELECT id_owner FROM rooms WHERE id='" + roomId + "' AND id_owner='"
            + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "' LIMIT 1", 0, 0).isEmpty()) {
            return true;
        }
        return !MySQL.Proc_5_2_6D4690("SELECT id_user FROM rooms_rights WHERE id_user='"
            + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "' AND id_room='" + roomId + "' LIMIT 1", 0, 0).isEmpty();
    }

    public static long roomCategoryForUser(long categoryId, String userId) {
        long rankIndex = handlingUserRank(userId);
        long hcLevel = handlingUserHcLevel(userId);
        return Vb.val(MySQL.Proc_5_2_6D4690("SELECT id FROM rooms_categories WHERE id='" + categoryId
            + "' AND level_minrequired <= '" + rankIndex + "' AND hclevel_minrequired <= '" + hcLevel + "' LIMIT 1", 0, 0));
    }

    public static int handlingSocketIndexForUserName(String userName) {
        if (Vb.cStr(userName).isEmpty()) {
            return 0;
        }
        return (int) Vb.val(MySQL.Proc_5_2_6D4690("SELECT id_socket FROM users WHERE name='"
            + Functions.Proc_10_11_80A9C0(userName, 0, 0) + "' AND id_socket IS NOT NULL LIMIT 1", 0, 0));
    }

    public static String staffModerationPayload(long rankIndex, long hcLevel) {
        Object cache = Licence.global_008292D8;
        int rank = (int) Math.max(0L, Math.min(rankIndex, 20L));
        int hc = (int) Math.max(0L, Math.min(hcLevel, 2L));
        if (cache instanceof String[][]) {
            String[][] values = (String[][]) cache;
            if (rank < values.length && values[rank] != null && hc < values[rank].length) {
                return Vb.cStr(values[rank][hc]);
            }
        }
        if (cache instanceof Object[][]) {
            Object[][] values = (Object[][]) cache;
            if (rank < values.length && values[rank] != null && hc < values[rank].length) {
                return Vb.cStr(values[rank][hc]);
            }
        }
        return "";
    }

    public static String indexedPayload(Object cache, long index) {
        int idx = (int) index;
        if (idx < 0) {
            return "";
        }
        if (cache instanceof String[]) {
            String[] values = (String[]) cache;
            return idx < values.length ? Vb.cStr(values[idx]) : "";
        }
        if (cache instanceof Object[]) {
            Object[] values = (Object[]) cache;
            return idx < values.length ? Vb.cStr(values[idx]) : "";
        }
        return "";
    }

    public static void ensureRepresentedRoomSlotPool() {
        if (!Licence.global_0082930C.isEmpty()) {
            return;
        }
        StringBuilder slots = new StringBuilder();
        for (long slotIndex = 1L; slotIndex <= 500L; slotIndex++) {
            slots.append('[').append(slotIndex).append(']');
        }
        Licence.global_0082930C = slots.toString();
    }

    public static long reserveRepresentedRoomSlot(long preferredSlot) {
        ensureRepresentedRoomSlotPool();
        if (preferredSlot > 0L) {
            String marker = "[" + preferredSlot + "]";
            if (Licence.global_0082930C.contains(marker)) {
                Licence.global_0082930C = Licence.global_0082930C.replaceFirst(Pattern.quote(marker), "");
                return preferredSlot;
            }
        }
        for (String part : Licence.global_0082930C.split("\\]", -1)) {
            long candidateSlot = Vb.val(part.replace("[", ""));
            if (candidateSlot > 0L) {
                String marker = "[" + candidateSlot + "]";
                Licence.global_0082930C = Licence.global_0082930C.replaceFirst(Pattern.quote(marker), "");
                return candidateSlot;
            }
        }
        return 0L;
    }

    public static void releaseRepresentedRoomSlot(long slotId) {
        if (slotId <= 0L) {
            return;
        }
        String marker = "[" + slotId + "]";
        if (!Licence.global_0082930C.contains(marker)) {
            Licence.global_0082930C += marker;
        }
    }

    public static void loadRepresentedRoomBots(long roomSlot, long roomId) {
        if (roomSlot <= 0L || roomId <= 0L
            || Vb.val(Functions.Proc_10_0_809570("com.client.rooms.bots.enabled", "-1", 0)) == 0L) {
            return;
        }
        String rowText = MySQL.Proc_5_2_6D4690("SELECT id,name,motto,speech,responses,position_x,position_y,position_z,"
            + "position_r,figure,NULL,id_handle,id_handleaction,cache_action,speech_submit,allow_walk,max_fields_away "
            + "FROM bots WHERE id_room='" + roomId + "' LIMIT 255", 0, 0);
        for (String row : rowText.split("\r", -1)) {
            if (!row.isEmpty()) {
                allocateRepresentedBot(roomSlot, row.split("\t", -1));
            }
        }
    }

    public static long broadcastToRoomUsers(long roomId, String payload) {
        if (roomId <= 0L || Vb.cStr(payload).isEmpty()) {
            return 0L;
        }
        String rowText = MySQL.Proc_5_2_6D4690("SELECT users.id_socket FROM logs_visitedrooms,users WHERE logs_visitedrooms.id_room='"
            + roomId + "' AND logs_visitedrooms.timestamp_left IS NULL AND users.id=logs_visitedrooms.id_user AND users.id_socket IS NOT NULL", 0, 0);
        if (rowText.isEmpty()) {
            rowText = MySQL.Proc_5_2_6D4690("SELECT id_socket FROM users WHERE id_socket IS NOT NULL AND id IN "
                + "(SELECT id_user FROM logs_visitedrooms WHERE id_room='" + roomId + "' AND timestamp_left IS NULL)", 0, 0);
        }
        String sentMarkers = "";
        long sentCount = 0L;
        for (String row : rowText.split("\r", -1)) {
            int socketIndex = (int) Vb.val(row);
            String marker = "[" + socketIndex + "]";
            if (socketIndex > 0 && !sentMarkers.contains(marker)) {
                Proc_6_244_801E80(socketIndex, payload, 0);
                sentMarkers += marker;
                sentCount++;
            }
        }
        return sentCount;
    }

    public static long broadcastToStaffModerators(String payload) {
        if (Vb.cStr(payload).isEmpty()) {
            return 0L;
        }
        String sentMarkers = "";
        long sentCount = 0L;
        for (String recordText : Vb.cStr(Licence.global_00829268).split("\\[", -1)) {
            if (recordText.startsWith("1:")) {
                int payloadStart = recordText.indexOf('\1');
                int payloadEnd = recordText.indexOf(']', payloadStart + 1);
                if (payloadStart > 0) {
                    if (payloadEnd < 0) {
                        payloadEnd = recordText.length();
                    }
                    String[] fields = recordText.substring(payloadStart + 1, payloadEnd).split("\2", -1);
                    String candidateUserId = String.valueOf(Vb.val(handlingField(fields, 0)));
                    int candidateSocket = (int) Vb.val(handlingField(fields, 1));
                    if ("0".equals(candidateUserId)) {
                        candidateUserId = handlingUserIdFromSocket(candidateSocket);
                    }
                    String marker = "[" + candidateSocket + "]";
                    if (candidateSocket > 0 && !sentMarkers.contains(marker) && handlingUserHasPermission(candidateUserId, "fuse_mod")) {
                        Proc_6_244_801E80(candidateSocket, payload, 0);
                        sentMarkers += marker;
                        sentCount++;
                    }
                }
            }
        }
        String rowText = MySQL.Proc_5_2_6D4690("SELECT id,id_socket FROM users WHERE id_socket IS NOT NULL", 0, 0);
        for (String row : rowText.split("\r", -1)) {
            String[] fields = row.split("\t", -1);
            String candidateUserId = String.valueOf(Vb.val(handlingField(fields, 0)));
            int candidateSocket = (int) Vb.val(handlingField(fields, 1));
            String marker = "[" + candidateSocket + "]";
            if (candidateSocket > 0 && !sentMarkers.contains(marker) && handlingUserHasPermission(candidateUserId, "fuse_mod")) {
                Proc_6_244_801E80(candidateSocket, payload, 0);
                sentMarkers += marker;
                sentCount++;
            }
        }
        return sentCount;
    }

    private static String handlingRepresentedChatRoute(Object[] args, long chatType) {
        try {
            int socketIndex = handlingSocketIndex(args);
            if (socketIndex <= 0) {
                return "";
            }
            String requestPayload = handlingPacketPayload(args);
            if (requestPayload.startsWith("@t") || requestPayload.startsWith("@w") || requestPayload.startsWith("@x")) {
                requestPayload = requestPayload.substring(2);
            }
            if (requestPayload.startsWith("H") || requestPayload.startsWith("I")) {
                requestPayload = requestPayload.substring(1);
            }

            String targetName = "";
            String messageText;
            LongRef offset = new LongRef(1);
            if (chatType == 2L) {
                targetName = readWireString(requestPayload, offset).trim();
                messageText = readWireString(requestPayload, offset);
                if (messageText.isEmpty()) {
                    messageText = requestPayload;
                    int spaceAt = messageText.indexOf(' ');
                    if (spaceAt >= 0) {
                        targetName = messageText.substring(0, spaceAt).trim();
                        messageText = messageText.substring(spaceAt + 1);
                    }
                }
            } else {
                messageText = readWireString(requestPayload, offset);
                if (messageText.isEmpty()) {
                    messageText = requestPayload;
                }
            }

            messageText = left(Functions.Proc_10_10_80A7F0(messageText, 0, 0).trim(), 122);
            if (messageText.isEmpty()) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            long roomUserIndex = representedRoomUserIndex(socketIndex, userId);
            if (roomId <= 0L || roomUserIndex <= 0L) {
                return "";
            }
            long userRank = handlingUserRank(userId);
            long hcLevel = handlingUserHcLevel(userId);
            if (!Proc_6_21_6E8BA0(messageText, 0, 0).isEmpty()
                && !Functions.Proc_10_1_809790(userRank, "", "fuse_can_chat_links", hcLevel)) {
                return "";
            }
            String filteredText = Proc_6_22_6E9300(messageText, 0, 0);
            if (filteredText.isEmpty()) {
                filteredText = messageText;
            }
            long gestureId = Proc_6_23_6E9A90(filteredText, 0, 0);
            MySQL.Proc_5_1_6D4110("INSERT INTO logs_chat(id_user,id_room,timestamp,description,id_type,id_session) VALUES('"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "','" + roomId + "',UNIX_TIMESTAMP(),'"
                + Functions.Proc_10_11_80A9C0(filteredText, 0, 0) + "','" + chatType + "','"
                + Functions.Proc_10_11_80A9C0(handlingUserSessionId(userId), 0, 0) + "')", 0, 0);
            String payload = representedChatPayload(roomUserIndex, filteredText, gestureId, chatType);
            if (chatType == 2L) {
                int targetSocketIndex = handlingSocketIndexForUserName(targetName);
                if (targetSocketIndex > 0) {
                    Proc_6_244_801E80(targetSocketIndex, payload, 0);
                    Proc_6_244_801E80(socketIndex, payload, 0);
                } else {
                    Proc_6_244_801E80(socketIndex, payload, 0);
                }
            } else {
                Proc_6_245_801FA0(socketIndex, payload, 0);
            }
            return payload;
        } catch (Exception ignored) {
            return "";
        }
    }

    private static void staffDirectMessage(
        Object[] args,
        String prefix,
        String requiredPermission,
        String logType,
        boolean kickAfterSend,
        boolean requireOnlineTarget
    ) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, prefix);
            LongRef offset = new LongRef(1);
            long targetUserId = readWireLong(requestPayload, offset);
            if (targetUserId <= 0L) {
                targetUserId = Vb.val(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
            }
            String messageText = readWireString(requestPayload, offset);
            if (messageText.isEmpty()) {
                messageText = Functions.Proc_10_7_80A190(requestPayload, 0, 0);
            }
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (targetUserId <= 0L || messageText.isEmpty()
                || callerUserId.isEmpty() || "0".equals(callerUserId)
                || !handlingUserHasPermission(callerUserId, "fuse_mod")
                || !handlingUserHasPermission(callerUserId, requiredPermission)
                || containsUnsafeStaffAlert(messageText)) {
                return;
            }
            int targetSocketIndex = handlingSocketFromUserId(String.valueOf(targetUserId));
            if (requireOnlineTarget && targetSocketIndex <= 0) {
                return;
            }
            long currentRoomId = handlingCurrentRoomId(socketIndex, callerUserId);
            MySQL.Proc_5_1_6D4110("INSERT INTO logs_moderation(id_type,id_user,id_target,id_target_2,timestamp,message,id_session) VALUES('"
                + logType + "','" + Functions.Proc_10_11_80A9C0(callerUserId, 0, 0) + "','"
                + Functions.Proc_10_11_80A9C0(targetUserId, 0, 0) + "','" + currentRoomId
                + "',UNIX_TIMESTAMP(),'" + Functions.Proc_10_11_80A9C0(messageText, 0, 0) + "','" + socketIndex + "')", 0, 0);
            if (targetSocketIndex > 0) {
                Proc_6_244_801E80(targetSocketIndex, "Ba" + messageText + '\2', 0);
                if (kickAfterSend) {
                    Proc_6_53_718E00(targetSocketIndex, 0, 0);
                }
            }
            if ("4".equals(logType)) {
                MySQL.Proc_5_0_6D3CD0("INSERT INTO users_cautions(id_user,id_partner,message,timestamp_submit) VALUES('"
                    + Functions.Proc_10_11_80A9C0(targetUserId, 0, 0) + "','" + Functions.Proc_10_11_80A9C0(callerUserId, 0, 0)
                    + "','" + Functions.Proc_10_11_80A9C0(messageText, 0, 0) + "',UNIX_TIMESTAMP())", 0, 0);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    private static void updateCallForHelpTab(Object[] args, String prefix, String tabId) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, prefix);
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId)
                || !handlingUserHasPermission(callerUserId, "fuse_mod")
                || !handlingUserHasPermission(callerUserId, "fuse_receive_calls_for_help")) {
                return;
            }
            String whereClause = staffCallForHelpWhereClause(requestPayload);
            if (whereClause.isEmpty()) {
                return;
            }
            if ("2".equals(tabId)) {
                MySQL.Proc_5_0_6D3CD0("UPDATE staff_cfh SET id_tab='2',id_picker='"
                    + Functions.Proc_10_11_80A9C0(callerUserId, 0, 0)
                    + "',timestamp_picked=UNIX_TIMESTAMP() WHERE " + whereClause, 0, 0);
            } else {
                MySQL.Proc_5_0_6D3CD0("UPDATE staff_cfh SET id_tab='1',id_picker=0,timestamp_picked=NULL WHERE "
                    + whereClause, 0, 0);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    private static void staffRoomHistory(Object[] args, String prefix, boolean includeChatRows) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, prefix);
            String callerUserId = handlingUserIdFromSocket(socketIndex);
            if (callerUserId.isEmpty() || "0".equals(callerUserId) || !handlingUserHasPermission(callerUserId, "fuse_mod")) {
                return;
            }
            if (includeChatRows && !handlingUserHasPermission(callerUserId, "fuse_chatlog")) {
                return;
            }
            long targetUserId = includeChatRows ? staffNestedUserIdFromWire(requestPayload)
                : Vb.val(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
            if (targetUserId <= 0L) {
                return;
            }
            String targetRow = MySQL.Proc_5_2_6D4690("SELECT users.id,users.name FROM users WHERE users.id='"
                + Functions.Proc_10_11_80A9C0(targetUserId, 0, 0) + "' LIMIT 1", 0, 0);
            if (targetRow.isEmpty()) {
                return;
            }
            String[] targetFields = targetRow.split("\t", -1);
            targetUserId = Vb.val(handlingField(targetFields, 0));
            if (targetUserId <= 0L) {
                return;
            }
            String visitRows;
            if (includeChatRows) {
                visitRows = MySQL.Proc_5_2_6D4690("SELECT models.type,rooms.id,rooms.name,logs_visitedrooms.timestamp_enter,"
                    + "logs_visitedrooms.timestamp_left FROM rooms,logs_visitedrooms,models WHERE logs_visitedrooms.id_user='"
                    + Functions.Proc_10_11_80A9C0(targetUserId, 0, 0)
                    + "' AND rooms.id=logs_visitedrooms.id_room AND logs_visitedrooms.timestamp_enter > UNIX_TIMESTAMP()-21600 "
                    + "AND models.id=rooms.id_model GROUP BY logs_visitedrooms.id ORDER BY logs_visitedrooms.id DESC LIMIT 10", 0, 0);
            } else {
                visitRows = MySQL.Proc_5_2_6D4690("SELECT models.type,rooms.id,rooms.name,DATE_FORMAT(FROM_UNIXTIME(logs_visitedrooms.timestamp_enter), '%H'),"
                    + "DATE_FORMAT(FROM_UNIXTIME(logs_visitedrooms.timestamp_enter), '%i') FROM rooms,logs_visitedrooms,models "
                    + "WHERE logs_visitedrooms.timestamp_enter > UNIX_TIMESTAMP()-21600 AND logs_visitedrooms.id_user='"
                    + Functions.Proc_10_11_80A9C0(targetUserId, 0, 0)
                    + "' AND rooms.id=logs_visitedrooms.id_room AND models.id=rooms.id_model GROUP BY logs_visitedrooms.id "
                    + "ORDER BY logs_visitedrooms.id DESC LIMIT 50", 0, 0);
            }
            long rowCount = 0L;
            StringBuilder rowPayload = new StringBuilder();
            for (String row : visitRows.split("\r", -1)) {
                if (!row.isEmpty()) {
                    if (includeChatRows) {
                        String[] fields = row.split("\t", -1);
                        long roomId = Vb.val(handlingField(fields, 1));
                        long timestampEnter = Vb.val(handlingField(fields, 3));
                        long timestampLeft = Vb.val(handlingField(fields, 4));
                        String chatRows = MySQL.Proc_5_2_6D4690("SELECT DATE_FORMAT(FROM_UNIXTIME(logs_chat.timestamp), '%H'),"
                            + "DATE_FORMAT(FROM_UNIXTIME(logs_chat.timestamp), '%i'),users.id,users.name,logs_chat.description "
                            + "FROM logs_chat,users WHERE logs_chat.id_room='" + roomId + "' AND logs_chat.id_user='"
                            + targetUserId + "' AND logs_chat.timestamp >= " + timestampEnter
                            + (timestampLeft > 0L ? " AND logs_chat.timestamp <= " + timestampLeft : "")
                            + " AND users.id=logs_chat.id_user ORDER BY logs_chat.id ASC", 0, 0);
                        rowPayload.append(staffRoomChatHistoryPayload(row, chatRows));
                    } else {
                        rowPayload.append(staffRoomVisitPayload(row));
                    }
                    rowCount++;
                }
            }
            String responsePrefix = includeChatRows ? "HX" : "HY";
            String responsePayload = Crypto.Proc_3_0_6D2AF0(targetUserId, null, responsePrefix)
                + handlingField(targetFields, 1) + '\2';
            responsePayload = Crypto.Proc_3_0_6D2AF0(rowCount, null, responsePayload) + rowPayload;
            Proc_6_244_801E80(socketIndex, responsePayload, 0);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static long avatarNameValidationCode(String candidateName, String currentName, long existingCount) {
        String candidate = Vb.cStr(candidateName).trim();
        if (candidate.length() < 3) {
            return 2L;
        }
        if (candidate.length() > 14) {
            return 1L;
        }
        String upper = candidate.toUpperCase();
        if (upper.startsWith("MOD-") || upper.startsWith("VIP-")) {
            return 2L;
        }
        for (int index = 0; index < candidate.length(); index++) {
            char ch = candidate.charAt(index);
            boolean allowed = (ch >= 'A' && ch <= 'Z')
                || (ch >= 'a' && ch <= 'z')
                || (ch >= '0' && ch <= '9')
                || ch == '-'
                || ch == '_';
            if (!allowed) {
                return 2L;
            }
        }
        if (candidate.equalsIgnoreCase(Vb.cStr(currentName))) {
            return 0L;
        }
        return existingCount > 0L ? 3L : 0L;
    }

    public static long handlingMovementField(String movementText, long fieldIndex) {
        String[] fields = Vb.cStr(movementText).split("\0", -1);
        return fieldIndex >= 0 && fieldIndex < fields.length ? Vb.val(fields[(int) fieldIndex]) : 0L;
    }

    public static long handlingDirectionCode(long deltaX, long deltaY) {
        return Functions.movementDirectionCode(deltaX, deltaY);
    }

    public static String representedRoomRecord(String roomCacheText, long roomSlot) {
        if (roomSlot <= 0L) {
            return "";
        }
        String cacheText = Vb.cStr(roomCacheText);
        String markerText = "\1" + roomSlot + '\t';
        int startAt = cacheText.indexOf(markerText);
        if (startAt < 0) {
            markerText = "\1" + roomSlot + '\2';
            startAt = cacheText.indexOf(markerText);
        }
        if (startAt < 0) {
            return "";
        }
        int recordStart = startAt + 1;
        int recordEnd = cacheText.indexOf('\2', recordStart);
        if (recordEnd < 0) {
            recordEnd = cacheText.length();
        }
        return cacheText.substring(recordStart, recordEnd);
    }

    public static String representedRoomRecordSet(String roomCacheText, long roomSlot, String roomRecord) {
        if (roomSlot <= 0L) {
            return Vb.cStr(roomCacheText);
        }
        String cacheText = removeRepresentedCacheRecord(roomCacheText, "\1" + roomSlot + '\t');
        cacheText = removeRepresentedCacheRecord(cacheText, "\1" + roomSlot + '\2');
        while (cacheText.startsWith("\2")) {
            cacheText = cacheText.substring(1);
        }
        return cacheText + '\1' + Vb.cStr(roomRecord) + '\2';
    }

    public static MovementPosition representedMovementPosition(String roomCacheText, long roomSlot, long entityIndex) {
        MovementPosition result = new MovementPosition();
        String roomRecord = representedRoomRecord(roomCacheText, roomSlot);
        if (roomRecord.isEmpty()) {
            return result;
        }
        String[] fields = roomRecord.split("\t", -1);
        if (fields.length < 5) {
            return result;
        }
        StringBuilder movementText = new StringBuilder();
        for (int fieldIndex = 4; fieldIndex < fields.length; fieldIndex++) {
            if (fieldIndex > 4) {
                movementText.append('\t');
            }
            movementText.append(fields[fieldIndex]);
        }
        for (String part : movementText.toString().split("\1", -1)) {
            String movementRecord = part;
            if (!movementRecord.isEmpty()) {
                if (movementRecord.endsWith("\2")) {
                    movementRecord = movementRecord.substring(0, movementRecord.length() - 1);
                }
                String[] movementFields = movementRecord.split("\t", -1);
                if (movementFields.length >= 3 && Vb.val(handlingField(movementFields, 0)) == entityIndex) {
                    result.positionX = Vb.val(handlingField(movementFields, 1));
                    result.positionY = Vb.val(handlingField(movementFields, 2));
                    result.found = true;
                    return result;
                }
            }
        }
        return result;
    }

    public static MovementPosition representedUserPosition(Object[] args) {
        MovementPosition result = new MovementPosition();
        if (args != null && args.length >= 5) {
            result.positionX = Vb.val(args[3]);
            result.positionY = Vb.val(args[4]);
            result.found = true;
        }
        return result;
    }

    public static String representedRoomOccupantMove(
        String roomCacheText,
        long roomSlot,
        long entityIndex,
        long positionX,
        long positionY,
        long directionValue,
        long movingValue
    ) {
        if (roomSlot <= 0L || entityIndex <= 0L) {
            return Vb.cStr(roomCacheText);
        }
        String roomRecord = representedRoomRecord(roomCacheText, roomSlot);
        if (roomRecord.isEmpty()) {
            roomRecord = roomSlot + "\t\t\t0";
        }
        String[] rawFields = ensureHandlingFieldCount(roomRecord.split("\t", -1), 4);
        String[] fields = new String[5];
        for (int fieldIndex = 0; fieldIndex < 4; fieldIndex++) {
            fields[fieldIndex] = rawFields[fieldIndex];
        }
        StringBuilder movementText = new StringBuilder();
        for (int fieldIndex = 4; fieldIndex < rawFields.length; fieldIndex++) {
            if (fieldIndex > 4) {
                movementText.append('\t');
            }
            movementText.append(rawFields[fieldIndex]);
        }
        fields[4] = movementText.toString();
        String movementRecord = entityIndex + "\t" + positionX + "\t" + positionY + "\t" + directionValue + "\t" + movingValue;
        fields[4] = removeMovementRecord(handlingField(fields, 4), "\1" + entityIndex + '\t');
        fields[4] = fields[4] + '\1' + movementRecord + '\2';
        return representedRoomRecordSet(roomCacheText, roomSlot, joinTab(fields));
    }

    public static String Proc_6_239_7FC170(Object... args) {
        if (args == null || args.length == 0) {
            return "";
        }
        return readFile(Vb.cStr(args[0]));
    }

    public static void Proc_6_240_7FC2B0(Object... args) {
        if (args == null || args.length < 2) {
            return;
        }
        writeFile(Vb.cStr(args[0]), Vb.cStr(args[1]));
    }

    public static String readFile(String filePath) {
        if (Vb.cStr(filePath).isEmpty()) {
            return "";
        }
        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            return "";
        }
        try {
            byte[] bytes = Files.readAllBytes(path);
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            return "";
        }
    }

    public static void writeFile(String filePath, String fileText) {
        if (Vb.cStr(filePath).isEmpty()) {
            return;
        }
        try {
            Path path = Path.of(filePath);
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.write(path, (Vb.cStr(fileText) + System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
        } catch (IOException ignored) {
            // VB6 source suppresses write failures.
        }
    }

    public static String removeRepresentedLineRecord(String cacheText, String markerText) {
        String cache = Vb.cStr(cacheText);
        String marker = Vb.cStr(markerText);
        if (cache.isEmpty() || marker.isEmpty()) {
            return cache;
        }
        cache = cache.replace("\r", "");
        StringBuilder rebuilt = new StringBuilder();
        for (String rowText : cache.split("\n", -1)) {
            if (!rowText.isEmpty() && !rowText.contains(marker)) {
                if (rebuilt.length() > 0) {
                    rebuilt.append('\n');
                }
                rebuilt.append(rowText);
            }
        }
        return rebuilt.toString();
    }

    public static String handlingEnsureRoomCacheFile(String cachePath) {
        if (Vb.cStr(cachePath).isEmpty()) {
            return "";
        }
        Path path = Path.of(cachePath);
        if (!Files.exists(path)) {
            DataManager.Proc_8_10_8068E0(cachePath, "");
        }
        return readFile(cachePath);
    }

    public static String removeRepresentedCacheRecord(String cacheText, String markerText) {
        String cache = Vb.cStr(cacheText);
        String marker = Vb.cStr(markerText);
        if (cache.isEmpty() || marker.isEmpty()) {
            return cache;
        }
        int markerAt = cache.indexOf(marker);
        while (markerAt >= 0) {
            int recordStart = cache.lastIndexOf('\1', markerAt);
            if (recordStart < 0) {
                recordStart = markerAt;
            }
            int recordEnd = cache.indexOf('\2', markerAt + marker.length());
            if (recordEnd < 0) {
                recordEnd = markerAt + marker.length() - 1;
            }
            cache = cache.substring(0, recordStart) + cache.substring(recordEnd + 1);
            markerAt = cache.indexOf(marker);
        }
        return cache;
    }

    public static String readWireString(String packetPayload, LongRef offset) {
        if (offset == null) {
            return "";
        }
        String payload = Vb.cStr(packetPayload);
        if (offset.value < 1L) {
            offset.value = 1L;
        }
        if (offset.value + 1L > payload.length()) {
            return "";
        }
        int start = (int) offset.value - 1;
        long fieldLength = Crypto.Proc_3_4_6D3620(payload.substring(start));
        if (fieldLength <= 0L) {
            return "";
        }
        int valueStart = start + 2;
        int valueEnd = Math.min(payload.length(), valueStart + (int) fieldLength);
        if (valueEnd - valueStart < fieldLength) {
            return "";
        }
        offset.value = offset.value + 2L + fieldLength;
        return payload.substring(valueStart, valueEnd);
    }

    public static long readWireLong(String packetPayload, LongRef offset) {
        if (offset == null) {
            return 0L;
        }
        String payload = Vb.cStr(packetPayload);
        if (offset.value < 1L) {
            offset.value = 1L;
        }
        if (offset.value > payload.length()) {
            return 0L;
        }
        String remainingPayload = payload.substring((int) offset.value - 1);
        long encodedLengthSize = wireLongFieldLength(remainingPayload);
        if (encodedLengthSize <= 0L) {
            return 0L;
        }
        long value = Crypto.Proc_3_3_6D3240(remainingPayload);
        offset.value += encodedLengthSize;
        return value;
    }

    private static long wireLongFieldLength(String encodedValue) {
        String value = Vb.cStr(encodedValue);
        if (value.isEmpty()) {
            return 0L;
        }
        long firstByte = value.charAt(0) - 64L;
        long tailLength = (firstByte & 0x38L) / 8L;
        if (tailLength <= 0L) {
            return 1L;
        }
        return Math.min(value.length(), tailLength + 1L);
    }

    public static boolean stickyNoteUpdateFromWire(String packetPayload, StickyNoteUpdate update) {
        if (update == null) {
            return false;
        }
        String payload = Vb.cStr(packetPayload);
        String idText = Functions.Proc_10_6_809F10(payload);
        long furnitureId = Vb.val(idText);
        String notePayload = "";
        if (furnitureId <= 0L) {
            LongRef offset = new LongRef(1);
            furnitureId = readWireLong(payload, offset);
            notePayload = Vb.mid(payload, (int) offset.value);
        } else {
            long idLengthSize = Crypto.Proc_3_2_6D30A0(payload);
            if (idLengthSize > 0L) {
                notePayload = Vb.mid(payload, (int) idLengthSize + idText.length() + 1);
            }
        }
        if (notePayload.isEmpty()) {
            notePayload = Functions.Proc_10_7_80A190(payload);
        }
        if (notePayload.isEmpty()) {
            return false;
        }
        if (notePayload.length() > 510) {
            notePayload = notePayload.substring(0, 510);
        }

        int separatorAt = firstPositiveIndex(notePayload, '\r', '\n', '\2');
        String noteColor;
        String noteCaption;
        if (separatorAt >= 0) {
            noteColor = notePayload.substring(0, separatorAt).toUpperCase();
            noteCaption = notePayload.substring(separatorAt + 1);
        } else {
            noteColor = notePayload.substring(0, Math.min(6, notePayload.length())).toUpperCase();
            noteCaption = notePayload.length() > 6 ? notePayload.substring(6) : "";
        }
        if (noteColor.length() > 6) {
            noteColor = noteColor.substring(0, 6);
        }
        if (!isStickyNoteColor(noteColor)) {
            return false;
        }
        if (noteCaption.length() > 510) {
            noteCaption = noteCaption.substring(0, 510);
        }
        noteCaption = noteCaption.replace('\u00a0', '\u001f').replace('\r', '\u001f').replace('\n', '\u001f');
        update.furnitureId = furnitureId;
        update.noteColor = noteColor;
        update.noteCaption = noteCaption;
        return true;
    }

    public static boolean isStickyNoteColor(String noteColor) {
        String color = Vb.cStr(noteColor).toUpperCase();
        return "9CFF9C".equals(color) || "FFFF33".equals(color) || "FF9CFF".equals(color) || "9CCEFF".equals(color);
    }

    public static long stickyFurnitureIdFromPayload(String requestPayload) {
        long furnitureId = Vb.val(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
        if (furnitureId <= 0L) {
            furnitureId = readWireLong(requestPayload, new LongRef(1));
        }
        return furnitureId;
    }

    public static String handlingSimpleFloorItemUse(Object[] args, String packetPrefix, long stateValue, boolean storeState) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, packetPrefix);
            String userId = handlingUserIdFromSocket(socketIndex);
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (userId.isEmpty() || "0".equals(userId) || roomId <= 0L) {
                return "";
            }
            long furnitureId = idRequestFromWire(requestPayload, "");
            if (furnitureId <= 0L) {
                return "";
            }
            String rowText = MySQL.Proc_5_2_6D4690("SELECT id,position_x,position_y,id_product FROM furnitures WHERE id='"
                + furnitureId + "' AND id_room='" + roomId + "' LIMIT 1", 0, 0);
            if (rowText.isEmpty()) {
                return "";
            }
            String[] fields = rowText.split("\t", -1);
            long furnitureX = Vb.val(handlingField(fields, 1));
            long furnitureY = Vb.val(handlingField(fields, 2));
            long productId = Vb.val(handlingField(fields, 3));
            if (productId <= 0L || Vb.val(DataManager.Proc_8_12_806C30(productId, 0, 0)) != 0L) {
                return "";
            }
            long roomSlot = Vb.val(MySQL.Proc_5_2_6D4690("SELECT id_slot FROM rooms WHERE id='" + roomId + "' LIMIT 1", 0, 0));
            MovementPosition userPosition = representedUserPosition(args);
            if (!userPosition.found) {
                userPosition = representedMovementPosition(
                    Licence.global_00829310, roomSlot, representedRoomUserIndex(socketIndex, userId));
            }
            if (userPosition.found
                && (Math.abs(userPosition.positionX - furnitureX) > 2L || Math.abs(userPosition.positionY - furnitureY) > 2L)) {
                return "";
            }
            String payload = "0" + Crypto.Proc_3_0_6D2AF0(stateValue, null,
                Crypto.Proc_3_0_6D2AF0(furnitureId, null, "AZ"));
            Proc_6_247_8027E0(socketIndex, payload, 0);
            if (storeState) {
                Proc_6_151_78AC20(roomId, furnitureId, stateValue);
            } else {
                Proc_6_145_76CA20(socketIndex, roomId, furnitureId);
            }
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String handlingFloorFurnitureMove(Object[] args, String packetPrefix, boolean fromInventory) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String requestPayload = handlingRequestPayload(args, packetPrefix);
            FloorFurniturePlacement placement = floorFurniturePlacementFromPayload(requestPayload);
            if (placement.furnitureId <= 0L) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L || (!handlingUserHasRoomRight(userId, roomId)
                && !handlingUserHasPermission(userId, "fuse_pick_up_any_furni"))) {
                return "";
            }
            String escapedUserId = Functions.Proc_10_11_80A9C0(userId, 0, 0);
            String itemRow;
            if (fromInventory) {
                itemRow = MySQL.Proc_5_2_6D4690("SELECT id_product,id,sign,id_secondary,id_destination FROM furnitures WHERE id='"
                    + placement.furnitureId + "' AND id_owner='" + escapedUserId
                    + "' AND id_room IS NULL LIMIT 1", 0, 0);
            } else {
                itemRow = MySQL.Proc_5_2_6D4690("SELECT id_product,id,sign,id_secondary,id_destination FROM furnitures WHERE id='"
                    + placement.furnitureId + "' AND id_room='" + roomId + "' LIMIT 1", 0, 0);
            }
            if (itemRow.isEmpty()) {
                return "";
            }
            String[] itemFields = itemRow.split("\t", -1);
            long productId = Vb.val(navigatorField(itemFields, 0));
            String itemData = navigatorField(itemFields, 2);
            long secondaryValue = Vb.val(navigatorField(itemFields, 3));
            if (productId <= 0L) {
                return "";
            }
            String[] productFields = Licence.Proc_9_3_807930(productId, 0, 0).split("\t", -1);
            long productType = Vb.val(navigatorField(productFields, 1));
            if (productType == 9L) {
                if (fromInventory) {
                    Proc_6_157_7974B0(socketIndex, requestPayload, itemFields);
                }
                return "";
            }
            String positionZ = String.valueOf((long) Vb.val(navigatorField(productFields, 24)));
            if (fromInventory) {
                MySQL.Proc_5_0_6D3CD0("UPDATE furnitures SET id_owner=NULL,id_room='" + roomId
                    + "',position_x='" + placement.positionX + "',position_y='" + placement.positionY
                    + "',position_z='" + Functions.Proc_10_11_80A9C0(positionZ, 0, 0) + "',position_r='"
                    + placement.rotation + "',position_wall=NULL,task_owner='" + escapedUserId
                    + "',task_time=UNIX_TIMESTAMP() WHERE id='" + placement.furnitureId
                    + "' AND id_owner='" + escapedUserId + "' AND id_room IS NULL LIMIT 1", 0, 0);
                Proc_6_244_801E80(socketIndex, Crypto.Proc_3_0_6D2AF0(placement.furnitureId, null, "Ac"), 0);
            } else {
                MySQL.Proc_5_0_6D3CD0("UPDATE furnitures SET position_x='" + placement.positionX
                    + "',position_y='" + placement.positionY + "',position_z='"
                    + Functions.Proc_10_11_80A9C0(positionZ, 0, 0) + "',position_r='" + placement.rotation
                    + "',position_wall=NULL,task_owner='" + escapedUserId
                    + "',task_time=UNIX_TIMESTAMP() WHERE id='" + placement.furnitureId
                    + "' AND id_room='" + roomId + "' LIMIT 1", 0, 0);
            }
            String placementPayload = Proc_6_161_7B2EE0(
                placement.furnitureId, placement.positionX, placement.positionY, placement.rotation,
                Vb.val(positionZ), "", itemData, secondaryValue, productId);
            String payload = (fromInventory ? "A]" : "A_") + placementPayload;
            if (!placementPayload.isEmpty()) {
                Proc_6_247_8027E0(socketIndex, payload, 0);
            }
            Proc_6_106_74B750(Path.of(Functions.applicationPath, "CACHE", "ROOMS", roomId + ".cache").toString(), 0, 0);
            Proc_6_106_74B750(Path.of(Functions.applicationPath, "CACHE", "PATHFINDER", roomId + ".cache").toString(), 0, 0);
            if (fromInventory) {
                Proc_6_140_769400(socketIndex, "FT", "");
            }
            return payload;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static FloorFurniturePlacement floorFurniturePlacementFromPayload(String packetPayload) {
        FloorFurniturePlacement placement = new FloorFurniturePlacement();
        String normalizedPayload = Vb.cStr(packetPayload)
            .replace('\1', ' ')
            .replace('\2', ' ')
            .replace('\t', ' ')
            .replace('\r', ' ')
            .replace('\n', ' ')
            .trim();
        while (normalizedPayload.contains("  ")) {
            normalizedPayload = normalizedPayload.replace("  ", " ");
        }
        if (!normalizedPayload.isEmpty()) {
            String[] tokens = normalizedPayload.split(" ", -1);
            if (tokens.length >= 1) {
                placement.furnitureId = Vb.val(tokens[0]);
            }
            if (tokens.length >= 2) {
                placement.positionX = Vb.val(tokens[1]);
            }
            if (tokens.length >= 3) {
                placement.positionY = Vb.val(tokens[2]);
            }
            if (tokens.length >= 4) {
                placement.rotation = Vb.val(tokens[3]);
            }
        }
        if (placement.furnitureId <= 0L) {
            placement.furnitureId = readWireLong(Vb.cStr(packetPayload), new LongRef(1));
        }
        return placement;
    }

    public static boolean isPostItProduct(long productId) {
        return DataManager.Proc_8_12_806C30(productId, 18, 0).toLowerCase().startsWith("post.it");
    }

    public static long representedDimmerFurnitureId(long roomId) {
        if (roomId <= 0L) {
            return 0L;
        }
        return Vb.val(MySQL.Proc_5_2_6D4690(
            "SELECT furnitures.id FROM furnitures,products WHERE furnitures.id_room='" + roomId
                + "' AND products.id_type='9' AND furnitures.id_product=products.id LIMIT 1", 0, 0));
    }

    public static boolean isDimmerColour(String colourText) {
        String color = Vb.cStr(colourText).toUpperCase();
        return "#0053F7".equals(color)
            || "#74F5F5".equals(color)
            || "#E759DE".equals(color)
            || "#EA4532".equals(color)
            || "#F2F851".equals(color)
            || "#82F349".equals(color)
            || "#000000".equals(color);
    }

    public static boolean wallPlacementFromPayload(String packetPayload, WallPlacement placement) {
        if (placement == null) {
            return false;
        }
        String normalizedPayload = Vb.cStr(packetPayload)
            .replace('\1', ' ')
            .replace('\2', ' ')
            .replace('\t', ' ')
            .replace('\r', ' ')
            .replace('\n', ' ');
        while (normalizedPayload.contains("  ")) {
            normalizedPayload = normalizedPayload.replace("  ", " ");
        }
        normalizedPayload = normalizedPayload.trim();
        String lower = normalizedPayload.toLowerCase();
        int wallAt = lower.indexOf(":w=");
        int localAt = lower.indexOf("l=");
        if (wallAt < 0 || localAt <= wallAt) {
            return false;
        }
        String wallText = normalizedPayload.substring(wallAt + 3, localAt).trim().replace(" ", "");
        String localText = normalizedPayload.substring(localAt + 2).trim().replace(" ", "");
        String[] wallParts = wallText.split(",", -1);
        String[] localParts = localText.split(",", -1);
        if (wallParts.length < 2 || localParts.length < 2) {
            return false;
        }
        placement.wallX = Vb.val(wallParts[0]);
        placement.wallY = Vb.val(wallParts[1]);
        placement.localX = Vb.val(localParts[0]);
        placement.localY = Vb.val(localParts[1]);
        return true;
    }

    public static String roomIconPayloadFromWire(String packetPayload) {
        LongRef offset = new LongRef(1);
        long previousOffset = offset.value;
        long backgroundId = readWireLong(packetPayload, offset);
        if (offset.value <= previousOffset || backgroundId < 0L || backgroundId > 24L) {
            return "";
        }

        previousOffset = offset.value;
        long foregroundId = readWireLong(packetPayload, offset);
        if (offset.value <= previousOffset || foregroundId < 0L || foregroundId > 11L) {
            return "";
        }

        previousOffset = offset.value;
        long itemCount = readWireLong(packetPayload, offset);
        if (offset.value <= previousOffset || itemCount < 0L || itemCount > 12L) {
            return "";
        }

        String payload = Crypto.Proc_3_0_6D2AF0(backgroundId, null, "");
        payload = Crypto.Proc_3_0_6D2AF0(foregroundId, null, payload);
        payload = Crypto.Proc_3_0_6D2AF0(itemCount, null, payload);

        for (long itemIndex = 1L; itemIndex <= itemCount; itemIndex++) {
            previousOffset = offset.value;
            long itemType = readWireLong(packetPayload, offset);
            if (offset.value <= previousOffset || itemType < 0L) {
                return "";
            }

            previousOffset = offset.value;
            long itemPosition = readWireLong(packetPayload, offset);
            if (offset.value <= previousOffset || itemPosition < 0L) {
                return "";
            }

            payload = Crypto.Proc_3_0_6D2AF0(itemType, null, payload);
            payload = Crypto.Proc_3_0_6D2AF0(itemPosition, null, payload);
        }
        return payload;
    }

    public static boolean roomEventCreatePayloadFromWire(String packetPayload, RoomEventPayload result) {
        if (result == null) {
            return false;
        }
        LongRef offset = new LongRef(1);
        long categoryId = readWireLong(packetPayload, offset);
        if (categoryId < 1L) {
            return false;
        }
        String categoryName = DataManager.Proc_8_11_8069B0(categoryId, 0);
        if (categoryName.isEmpty()) {
            return false;
        }
        result.categoryId = categoryId;
        result.categoryName = categoryName;
        return readRoomEventCommon(packetPayload, offset, result, true);
    }

    public static boolean roomEventEditPayloadFromWire(String packetPayload, RoomEventPayload result) {
        if (result == null) {
            return false;
        }
        LongRef offset = new LongRef(1);
        return readRoomEventCommon(packetPayload, offset, result, true);
    }

    public static boolean roomSettingsFromWire(String packetPayload, RoomSettingsPayload result) {
        if (result == null) {
            return false;
        }
        LongRef offset = new LongRef(1);
        String roomName = Functions.Proc_10_10_80A7F0(readWireString(packetPayload, offset));
        if (roomName.length() < 3) {
            return false;
        }
        result.roomName = left(roomName, 60);
        result.roomPassword = left(Functions.Proc_10_10_80A7F0(readWireString(packetPayload, offset)), 60);
        result.doorStatus = readWireLong(packetPayload, offset);
        if (result.doorStatus < 0L || result.doorStatus > 2L) {
            return false;
        }
        result.roomDescription = left(Functions.Proc_10_10_80A7F0(readWireString(packetPayload, offset)), 255);
        result.visitorsMax = readWireLong(packetPayload, offset);
        if (result.visitorsMax < 1L) {
            result.visitorsMax = 1L;
        }
        if (result.visitorsMax > 250L) {
            result.visitorsMax = 250L;
        }
        result.categoryId = readWireLong(packetPayload, offset);
        if (result.categoryId <= 0L) {
            return false;
        }
        long tagCount = readWireLong(packetPayload, offset);
        if (tagCount < 0L || tagCount > 2L) {
            return false;
        }
        for (long tagIndex = 1L; tagIndex <= tagCount; tagIndex++) {
            String tagText = left(Functions.Proc_10_10_80A7F0(readWireString(packetPayload, offset)), 60).toLowerCase();
            if (tagIndex == 1L) {
                result.tagOne = tagText;
            } else if (tagIndex == 2L) {
                result.tagTwo = tagText;
            }
        }

        result.allowOthersPets = optionalWireLong(packetPayload, offset, 0L);
        result.allowFeedPets = optionalWireLong(packetPayload, offset, 0L);
        result.allowWalkthrough = optionalWireLong(packetPayload, offset, 0L);
        result.disableWalls = optionalWireLong(packetPayload, offset, 0L);
        result.thicknessFloor = optionalWireLong(packetPayload, offset, 0L);
        result.thicknessWallpaper = optionalWireLong(packetPayload, offset, 0L);

        result.allowOthersPets = roomSettingsFlag(result.allowOthersPets);
        result.allowFeedPets = roomSettingsFlag(result.allowFeedPets);
        result.allowWalkthrough = roomSettingsFlag(result.allowWalkthrough);
        result.disableWalls = roomSettingsFlag(result.disableWalls);
        result.thicknessFloor = roomSettingsThickness(result.thicknessFloor);
        result.thicknessWallpaper = roomSettingsThickness(result.thicknessWallpaper);
        return true;
    }

    public static String roomSettingsReadPayload(String[] roomFields, String rightsRow) {
        StringBuilder tagPayload = new StringBuilder();
        long tagCount = 0L;
        if (!handlingField(roomFields, 7).isEmpty()) {
            tagPayload.append(handlingField(roomFields, 7)).append('\2');
            tagCount++;
        }
        if (!handlingField(roomFields, 8).isEmpty()) {
            tagPayload.append(handlingField(roomFields, 8)).append('\2');
            tagCount++;
        }

        StringBuilder rightsPayload = new StringBuilder();
        long rightsCount = 0L;
        if (!Vb.cStr(rightsRow).isEmpty()) {
            for (String row : Vb.cStr(rightsRow).split("\r", -1)) {
                if (!row.isEmpty()) {
                    String[] rightsFields = row.split("\t", -1);
                    if (rightsFields.length >= 2) {
                        rightsPayload = new StringBuilder(Crypto.Proc_3_0_6D2AF0(Vb.val(handlingField(rightsFields, 0)), null, rightsPayload.toString()));
                        rightsPayload.append(handlingField(rightsFields, 1)).append('\2');
                        rightsCount++;
                    }
                }
            }
        }

        String payload = Crypto.Proc_3_0_6D2AF0(Vb.val(handlingField(roomFields, 0)), null, "GQ");
        payload = payload + handlingField(roomFields, 1) + '\2';
        payload = payload + handlingField(roomFields, 2) + '\2';
        payload = Crypto.Proc_3_0_6D2AF0(Vb.val(handlingField(roomFields, 3)), null, payload);
        payload = Crypto.Proc_3_0_6D2AF0(Vb.val(handlingField(roomFields, 4)), null, payload);
        payload = Crypto.Proc_3_0_6D2AF0(Vb.val(handlingField(roomFields, 5)), null, payload);
        payload = Crypto.Proc_3_0_6D2AF0(Vb.val(handlingField(roomFields, 6)), null, payload);
        payload = Crypto.Proc_3_0_6D2AF0(tagCount, null, payload) + tagPayload;
        payload = Crypto.Proc_3_0_6D2AF0(rightsCount, null, payload) + rightsPayload + "H";
        payload = Crypto.Proc_3_0_6D2AF0(Vb.val(handlingField(roomFields, 10)), null, payload);
        payload = Crypto.Proc_3_0_6D2AF0(Vb.val(handlingField(roomFields, 11)), null, payload);
        payload = Crypto.Proc_3_0_6D2AF0(Vb.val(handlingField(roomFields, 12)), null, payload);
        return Crypto.Proc_3_0_6D2AF0(Vb.val(handlingField(roomFields, 13)), null, payload);
    }

    public static long roomSettingsFlag(long flagValue) {
        return flagValue != 0L ? 1L : 0L;
    }

    public static long roomSettingsThickness(long thicknessValue) {
        if (thicknessValue < -2L) {
            return -2L;
        }
        if (thicknessValue > 1L) {
            return 1L;
        }
        return thicknessValue;
    }

    public static String nullableSqlText(String valueText) {
        return Vb.cStr(valueText).isEmpty() ? "null" : "'" + Functions.Proc_10_11_80A9C0(valueText) + "'";
    }

    public static long navigatorListLimit() {
        long limit = Vb.val(Functions.Proc_10_0_809570("com.client.navigator.list.limit", 50));
        return limit <= 0L ? 50L : limit;
    }

    public static String navigatorSearchTerm(String rawText) {
        return Functions.Proc_10_11_80A9C0(rawText).replace("%", "");
    }

    public static long navigatorCategoryIdFromPacket(Object[] args, String packetPrefix) {
        String requestPayload = handlingRequestPayload(args, packetPrefix);
        long categoryId = Vb.val(Functions.Proc_10_7_80A190(requestPayload, 0, 0));
        if (categoryId <= 0L) {
            categoryId = readWireLong(requestPayload, new LongRef(1));
        }
        return categoryId;
    }

    public static String navigatorTextFromPacket(Object[] args) {
        String requestPayload = handlingPacketPayload(args);
        if (requestPayload.length() >= 3) {
            requestPayload = requestPayload.substring(2);
        }
        if (requestPayload.startsWith("@")) {
            String value = readWireString(requestPayload, new LongRef(1));
            if (!value.isEmpty()) {
                return value;
            }
        }
        return requestPayload;
    }

    public static String recommendedRoomPayload(long treeIndex) {
        try {
            return Licence.recommendedRooms().payload(treeIndex);
        } catch (Exception ignored) {
            return "";
        }
    }

    public static String officialNavigatorQuery() {
        String separator = " UNION ALL ";
        StringBuilder queryText = new StringBuilder();
        queryText.append("SELECT rooms_official.id_type,rooms_official.id_style,rooms_official.icon,");
        queryText.append("rooms_official.caption,rooms_official.caption_2,rooms_official.caption_3,");
        queryText.append("NULL,rooms.id,rooms.name,users.name,rooms.status_door,rooms.visitors_now,");
        queryText.append("rooms.visitors_max,rooms.description,rooms_categories.has_trading,NULL,");
        queryText.append("rooms.rate,rooms.id_category,rooms.icon,rooms.tag_1,rooms.tag_2,");
        queryText.append("rooms.allow_otherspets,NULL,NULL,NULL,rooms_official.id_parent,");
        queryText.append("rooms_official.id,rooms_official.requires_level_in FROM users,rooms,");
        queryText.append("rooms_categories,rooms_official WHERE rooms_official.id_type='2' ");
        queryText.append("AND rooms_official.id_room IS NOT NULL AND rooms.id=rooms_official.id_room ");
        queryText.append("AND users.id=rooms.id_owner AND rooms_categories.id=rooms.id_category ");
        queryText.append("GROUP BY rooms_official.id");

        queryText.append(separator).append("SELECT rooms_official.id_type,rooms_official.id_style,");
        queryText.append("rooms_official.icon,rooms_official.caption,rooms_official.caption_2,");
        queryText.append("rooms_official.caption_3,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,");
        queryText.append("NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,rooms_official.id_parent,");
        queryText.append("rooms_official.id,rooms_official.requires_level_in FROM rooms_official ");
        queryText.append("WHERE rooms_official.id_type='1' GROUP BY rooms_official.id");

        queryText.append(separator).append("SELECT rooms_official.id_type,rooms_official.id_style,");
        queryText.append("rooms_official.icon,rooms_official.caption,rooms_official.caption_2,");
        queryText.append("rooms_official.caption_3,NULL,rooms.id,rooms.name,NULL,rooms.status_door,");
        queryText.append("rooms.visitors_now,rooms.visitors_max,rooms.description,");
        queryText.append("rooms_categories.has_trading,NULL,rooms.rate,rooms.id_category,rooms.icon,");
        queryText.append("rooms.tag_1,rooms.tag_2,rooms.allow_otherspets,models.name,");
        queryText.append("models.required_files,models.visitors_max,rooms_official.id_parent,");
        queryText.append("rooms_official.id,rooms_official.requires_level_in FROM models,rooms,");
        queryText.append("rooms_categories,rooms_official WHERE rooms_official.id_type='3' ");
        queryText.append("AND rooms_official.id_room IS NOT NULL AND rooms.id=rooms_official.id_room ");
        queryText.append("AND models.id=rooms.id_model AND rooms_categories.id=rooms.id_category ");
        queryText.append("GROUP BY rooms_official.id");

        queryText.append(separator).append("SELECT rooms_official.id_type,rooms_official.id_style,");
        queryText.append("rooms_official.icon,rooms_official.caption,rooms_official.caption_2,");
        queryText.append("rooms_official.caption_3,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,");
        queryText.append("NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,rooms_official.id_parent,");
        queryText.append("rooms_official.id,rooms_official.requires_level_in FROM rooms_official ");
        queryText.append("WHERE rooms_official.id_type='4' GROUP BY rooms_official.id ");
        queryText.append("ORDER BY 27 ASC LIMIT 255");
        return queryText.toString();
    }

    public static String navigatorField(String[] fields, long fieldIndex) {
        return fields != null && fieldIndex >= 0 && fieldIndex < fields.length ? Vb.cStr(fields[(int) fieldIndex]) : "";
    }

    public static String navigatorEventFragment(String[] fields) {
        String payload = Crypto.Proc_3_0_6D2AF0(Vb.val(navigatorField(fields, 0)), null, "");
        payload = Crypto.Proc_3_0_6D2AF0(Vb.val(navigatorField(fields, 4)), null, payload);
        payload = Crypto.Proc_3_0_6D2AF0(Vb.val(navigatorField(fields, 5)), null, payload);
        payload = Crypto.Proc_3_0_6D2AF0(Vb.val(navigatorField(fields, 9)), null, payload);
        payload = Crypto.Proc_3_0_6D2AF0(Vb.val(navigatorField(fields, 10)), null, payload);
        payload = Crypto.Proc_3_0_6D2AF0(Vb.val(navigatorField(fields, 7)), null, payload);
        return payload + " "
            + navigatorField(fields, 1) + '\2'
            + navigatorField(fields, 2) + '\2'
            + navigatorField(fields, 3) + '\2'
            + navigatorField(fields, 6) + '\2'
            + navigatorField(fields, 11) + '\2'
            + navigatorField(fields, 12) + '\2'
            + navigatorField(fields, 13) + '\2'
            + navigatorField(fields, 14) + '\2'
            + "H";
    }

    public static String navigatorRoomFragment(String[] fields) {
        String payload = Crypto.Proc_3_0_6D2AF0(Vb.val(navigatorField(fields, 0)), null, "");
        payload = Crypto.Proc_3_0_6D2AF0(Vb.val(navigatorField(fields, 4)), null, payload);
        payload = Crypto.Proc_3_0_6D2AF0(Vb.val(navigatorField(fields, 5)), null, payload);
        payload = Crypto.Proc_3_0_6D2AF0(Vb.val(navigatorField(fields, 9)), null, payload);
        payload = Crypto.Proc_3_0_6D2AF0(Vb.val(navigatorField(fields, 10)), null, payload);
        payload = Crypto.Proc_3_0_6D2AF0(Vb.val(navigatorField(fields, 7)), null, payload);
        payload = Crypto.Proc_3_0_6D2AF0(Vb.val(navigatorField(fields, 14)), null, payload);
        payload = Crypto.Proc_3_0_6D2AF0(Vb.val(navigatorField(fields, 15)), null, payload);
        return payload
            + navigatorField(fields, 1) + '\2'
            + navigatorField(fields, 2) + '\2'
            + navigatorField(fields, 3) + '\2'
            + navigatorField(fields, 6) + '\2'
            + navigatorField(fields, 11) + '\2'
            + navigatorField(fields, 12) + '\2'
            + navigatorField(fields, 13) + '\2'
            + "H";
    }

    public static String navigatorRoomListPayloadFromRows(String rowText) {
        long roomCount = 0L;
        StringBuilder payload = new StringBuilder();
        for (String row : Vb.cStr(rowText).split("\r", -1)) {
            if (!row.isEmpty()) {
                payload.append(navigatorRoomFragment(row.split("\t", -1)));
                roomCount++;
            }
        }
        return Crypto.Proc_3_0_6D2AF0(roomCount, null, payload.toString());
    }

    public static String singleNavigatorRoomPayloadFromRows(String rowText) {
        String listPayload = navigatorRoomListPayloadFromRows(rowText);
        String singleCountPrefix = Crypto.Proc_3_0_6D2AF0(1, null, "");
        if (listPayload.endsWith(singleCountPrefix)) {
            return listPayload.substring(0, listPayload.length() - singleCountPrefix.length());
        }
        return "";
    }

    public static String Proc_6_112_74E0C0(Object... args) {
        try {
            if (args == null || args.length == 0) {
                return Crypto.Proc_3_0_6D2AF0(0, null, "");
            }
            String queryTail = Vb.cStr(args[0]);
            if (queryTail.isEmpty()) {
                return Crypto.Proc_3_0_6D2AF0(0, null, "");
            }
            String rowText = MySQL.Proc_5_2_6D4690("SELECT rooms.id,rooms.name,users.name,rooms.status_door,"
                + "rooms.visitors_now,rooms.visitors_max,rooms.description,rooms_categories.has_trading,NULL,"
                + "rooms.rate,rooms.id_category,rooms.icon,rooms.tag_1,rooms.tag_2,rooms.allow_otherspets,"
                + "rooms.is_staff_picked FROM " + queryTail, 0, 0);
            return navigatorRoomListPayloadFromRows(rowText);
        } catch (Exception ignored) {
            return Crypto.Proc_3_0_6D2AF0(0, null, "");
        }
    }

    public static String navigatorEventListPayloadFromRows(String rowText) {
        long eventCount = 0L;
        StringBuilder payload = new StringBuilder();
        for (String row : Vb.cStr(rowText).split("\r", -1)) {
            if (!row.isEmpty()) {
                payload.append(navigatorEventFragment(row.split("\t", -1)));
                eventCount++;
            }
        }
        return Crypto.Proc_3_0_6D2AF0(eventCount, null, payload.toString());
    }

    public static String navigatorCombinedRoomListPayloadFromRows(String eventRows, String roomRows) {
        long itemCount = 0L;
        StringBuilder payload = new StringBuilder();
        for (String row : Vb.cStr(eventRows).split("\r", -1)) {
            if (!row.isEmpty()) {
                payload.append(navigatorEventFragment(row.split("\t", -1)));
                itemCount++;
            }
        }
        for (String row : Vb.cStr(roomRows).split("\r", -1)) {
            if (!row.isEmpty()) {
                payload.append(navigatorRoomFragment(row.split("\t", -1)));
                itemCount++;
            }
        }
        return Crypto.Proc_3_0_6D2AF0(itemCount, null, payload.toString());
    }

    public static String Proc_6_122_752280(Object... args) {
        if (args == null || args.length == 0) {
            return "";
        }
        boolean includeCountPrefix = args.length >= 2
            && (args[1] instanceof Boolean ? (Boolean) args[1] : Vb.val(args[1]) != 0L);
        return officialNavigatorRowsPayload(Vb.cStr(args[0]), includeCountPrefix);
    }

    public static String officialNavigatorRowsPayload(String rowText, boolean includeCountPrefix) {
        long itemCount = 0L;
        StringBuilder payload = new StringBuilder();
        for (String row : Vb.cStr(rowText).split("\r", -1)) {
            if (!row.isEmpty()) {
                String[] fields = row.split("\t", -1);
                if (fields.length >= 27) {
                    payload.append(officialNavigatorRowPayload(fields));
                    itemCount++;
                }
            }
        }
        if (includeCountPrefix) {
            return Crypto.Proc_3_0_6D2AF0(itemCount, null, "") + payload;
        }
        return payload.toString();
    }

    public static String officialNavigatorRowPayload(String[] fields) {
        if (fields == null || fields.length < 27) {
            return "";
        }
        StringBuilder payload = new StringBuilder();
        payload.append(Crypto.Proc_3_0_6D2AF0(Vb.val(navigatorField(fields, 0)), null, ""));
        payload.append(Crypto.Proc_3_0_6D2AF0(Vb.val(navigatorField(fields, 1)), null, ""));
        payload.append(Crypto.Proc_3_0_6D2AF0(Vb.val(navigatorField(fields, 2)), null, ""));
        for (int fieldIndex = 3; fieldIndex <= 24; fieldIndex++) {
            payload.append(navigatorField(fields, fieldIndex)).append('\2');
        }
        payload.append(Crypto.Proc_3_0_6D2AF0(Vb.val(navigatorField(fields, 25)), null, ""));
        payload.append(Crypto.Proc_3_0_6D2AF0(Vb.val(navigatorField(fields, 26)), null, ""));
        if (fields.length >= 28) {
            payload.append(Crypto.Proc_3_0_6D2AF0(Vb.val(navigatorField(fields, 27)), null, ""));
        }
        return payload.toString();
    }

    public static String Proc_6_138_7678A0(Object... args) {
        if (args == null || args.length < 2) {
            return "";
        }
        return inventoryItemPayload(Vb.val(args[0]), Vb.val(args[1]),
            args.length >= 3 ? Vb.cStr(args[2]) : "",
            args.length >= 4 ? Vb.val(args[3]) : 0L);
    }

    public static String inventoryItemPayload(long itemId, long productId, String itemData, long extraValue) {
        return InventoryMessagePayloads.item(itemId, productId, itemData, extraValue);
    }

    public static String representedTradeOfferStore(
        String tradeOffersText,
        long socketIndex,
        long furnitureId,
        long productId,
        String signText,
        long secondaryValue
    ) {
        if (socketIndex <= 0L || furnitureId <= 0L || productId <= 0L) {
            return Vb.cStr(tradeOffersText);
        }
        String rowText = socketIndex + "\t" + furnitureId + "\t" + productId + "\t"
            + Vb.cStr(signText).replace("\r", "") + "\t" + secondaryValue;
        StringBuilder rebuiltText = new StringBuilder();
        boolean replacedExisting = false;
        for (String row : Vb.cStr(tradeOffersText).split("\r", -1)) {
            if (!row.isEmpty()) {
                String[] fields = row.split("\t", -1);
                if (fields.length >= 2) {
                    if (Vb.val(handlingField(fields, 0)) == socketIndex && Vb.val(handlingField(fields, 1)) == furnitureId) {
                        appendRow(rebuiltText, rowText);
                        replacedExisting = true;
                    } else {
                        appendRow(rebuiltText, row);
                    }
                }
            }
        }
        if (!replacedExisting) {
            appendRow(rebuiltText, rowText);
        }
        return rebuiltText.toString();
    }

    public static String representedTradeOfferRemove(String tradeOffersText, long socketIndex, long furnitureId) {
        if (socketIndex <= 0L || Vb.cStr(tradeOffersText).isEmpty()) {
            return Vb.cStr(tradeOffersText);
        }
        StringBuilder rebuiltText = new StringBuilder();
        for (String row : Vb.cStr(tradeOffersText).split("\r", -1)) {
            if (!row.isEmpty()) {
                String[] fields = row.split("\t", -1);
                if (fields.length >= 2) {
                    long rowSocketIndex = Vb.val(handlingField(fields, 0));
                    long rowFurnitureId = Vb.val(handlingField(fields, 1));
                    if (rowSocketIndex != socketIndex || (furnitureId > 0L && rowFurnitureId != furnitureId)) {
                        appendRow(rebuiltText, row);
                    }
                }
            }
        }
        return rebuiltText.toString();
    }

    public static String representedTradeOfferSqlIds(String tradeOffersText, long socketIndex) {
        if (socketIndex <= 0L || Vb.cStr(tradeOffersText).isEmpty()) {
            return "";
        }
        StringBuilder sqlIds = new StringBuilder();
        for (String row : Vb.cStr(tradeOffersText).split("\r", -1)) {
            if (!row.isEmpty()) {
                String[] fields = row.split("\t", -1);
                if (fields.length >= 2 && Vb.val(handlingField(fields, 0)) == socketIndex) {
                    long furnitureId = Vb.val(handlingField(fields, 1));
                    if (furnitureId > 0L) {
                        if (sqlIds.length() > 0) {
                            sqlIds.append(',');
                        }
                        sqlIds.append('\'').append(furnitureId).append('\'');
                    }
                }
            }
        }
        return sqlIds.toString();
    }

    public static String representedTradeOfferLogItems(String tradeOffersText, long socketIndex) {
        if (socketIndex <= 0L || Vb.cStr(tradeOffersText).isEmpty()) {
            return "";
        }
        StringBuilder logItems = new StringBuilder();
        for (String row : Vb.cStr(tradeOffersText).split("\r", -1)) {
            if (!row.isEmpty()) {
                String[] fields = row.split("\t", -1);
                if (fields.length >= 3 && Vb.val(handlingField(fields, 0)) == socketIndex) {
                    long furnitureId = Vb.val(handlingField(fields, 1));
                    long productId = Vb.val(handlingField(fields, 2));
                    if (furnitureId > 0L) {
                        if (logItems.length() > 0) {
                            logItems.append('\1');
                        }
                        logItems.append(furnitureId).append(':').append(productId);
                    }
                }
            }
        }
        return logItems.toString();
    }

    public static TradeOfferItemPayload representedTradeOfferItemPayload(String tradeOffersText, long socketIndex) {
        TradeOfferItemPayload result = new TradeOfferItemPayload();
        if (socketIndex <= 0L || Vb.cStr(tradeOffersText).isEmpty()) {
            return result;
        }
        StringBuilder payload = new StringBuilder();
        for (String row : Vb.cStr(tradeOffersText).split("\r", -1)) {
            if (!row.isEmpty()) {
                String[] fields = row.split("\t", -1);
                if (fields.length >= 5 && Vb.val(handlingField(fields, 0)) == socketIndex) {
                    payload.append(inventoryItemPayload(
                        Vb.val(handlingField(fields, 1)),
                        Vb.val(handlingField(fields, 2)),
                        handlingField(fields, 3),
                        Vb.val(handlingField(fields, 4))));
                    result.itemCount++;
                }
            }
        }
        result.payload = payload.toString();
        return result;
    }

    public static String representedTradeOfferPayload(
        String tradeOffersText,
        long sourceSocketIndex,
        long targetSocketIndex,
        String sourceUserId,
        String targetUserId
    ) {
        if (sourceSocketIndex <= 0L || targetSocketIndex <= 0L) {
            return "";
        }
        TradeOfferItemPayload sourceItems = representedTradeOfferItemPayload(tradeOffersText, sourceSocketIndex);
        TradeOfferItemPayload targetItems = representedTradeOfferItemPayload(tradeOffersText, targetSocketIndex);
        String payload = Crypto.Proc_3_0_6D2AF0(Vb.val(sourceUserId), null, "Al");
        payload = Crypto.Proc_3_0_6D2AF0(Vb.val(targetUserId), null, payload);
        payload = Crypto.Proc_3_0_6D2AF0(sourceItems.itemCount, null, payload) + sourceItems.payload;
        return Crypto.Proc_3_0_6D2AF0(targetItems.itemCount, null, payload) + targetItems.payload;
    }

    public static InventoryPayloads inventoryPayloadsFromRows(String rowText) {
        InventoryPayloads result = new InventoryPayloads();
        InventoryMessagePayloads.InventoryList inventory = InventoryMessagePayloads.listFromRows(rowText);
        result.regularCount = inventory.regularCount;
        result.regularPayload = inventory.regularPayload;
        result.iconCount = inventory.iconCount;
        result.iconPayload = inventory.iconPayload;
        return result;
    }

    public static FurnitureMoveRequest furnitureMoveRequestFromPayload(String packetPayload) {
        FurnitureMoveRequest request = new FurnitureMoveRequest();
        String requestPayload = Vb.cStr(packetPayload);
        if (requestPayload.startsWith("A[")) {
            requestPayload = requestPayload.substring(2);
        }

        String normalizedPayload = requestPayload
            .replace('\1', ' ')
            .replace('\2', ' ')
            .replace('\t', ' ')
            .replace('\r', ' ')
            .replace('\n', ' ');
        while (normalizedPayload.contains("  ")) {
            normalizedPayload = normalizedPayload.replace("  ", " ");
        }
        normalizedPayload = normalizedPayload.trim();

        if (!normalizedPayload.isEmpty()) {
            String[] tokens = normalizedPayload.split(" ", -1);
            request.furnitureId = tokens.length >= 1 ? Vb.val(tokens[0]) : 0L;
            request.positionX = tokens.length >= 2 ? Vb.val(tokens[1]) : 0L;
            request.positionY = tokens.length >= 3 ? Vb.val(tokens[2]) : 0L;
            request.rotation = tokens.length >= 4 ? Vb.val(tokens[3]) : 0L;
        }

        if (request.furnitureId <= 0L) {
            LongRef offset = new LongRef(1);
            request.furnitureId = readWireLong(requestPayload, offset);
        }
        return request;
    }

    public static String activityPointBalancePayload(String rowText) {
        String[] fields = Vb.cStr(rowText).split("\t", -1);
        long itemCount = 0L;
        StringBuilder itemPayload = new StringBuilder();
        for (long pointType = 1L; pointType <= 4L; pointType++) {
            long pointValue = Vb.val(navigatorField(fields, pointType - 1L));
            itemPayload.append(Crypto.Proc_3_0_6D2AF0(pointType, null, ""));
            itemPayload.append(Crypto.Proc_3_0_6D2AF0(pointValue, null, ""));
            itemCount++;
        }
        return Crypto.Proc_3_0_6D2AF0(itemCount, null, "M@") + itemPayload;
    }

    public static long pickupFurnitureIdFromPayload(String packetPayload) {
        String requestPayload = Vb.cStr(packetPayload);
        if (requestPayload.startsWith("AZ")) {
            requestPayload = requestPayload.substring(2);
        }
        long furnitureId = Vb.val(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
        if (furnitureId <= 0L) {
            LongRef offset = new LongRef(1);
            furnitureId = readWireLong(requestPayload, offset);
        }
        return furnitureId;
    }

    public static FurnitureCacheState trackFurnitureCacheMarker(
        String pendingRoomCache,
        String pendingFurnitureCache,
        String representedRoomCache,
        long roomId,
        long furnitureId
    ) {
        FurnitureCacheState state = new FurnitureCacheState();
        state.pendingRoomCache = Vb.cStr(pendingRoomCache);
        state.pendingFurnitureCache = Vb.cStr(pendingFurnitureCache);
        state.representedRoomCache = Vb.cStr(representedRoomCache);
        if (furnitureId <= 0L) {
            return state;
        }

        String exactMarker = "\1" + furnitureId + '\2';
        state.pendingFurnitureCache = state.pendingFurnitureCache.replace(exactMarker, "") + exactMarker;
        state.pendingFurnitureCache = removeRepresentedCacheRecord(state.pendingFurnitureCache, "\1" + furnitureId + '\t');

        if (!state.representedRoomCache.isEmpty()) {
            state.representedRoomCache = removeRepresentedCacheRecord(state.representedRoomCache, exactMarker);
            state.representedRoomCache = removeRepresentedCacheRecord(state.representedRoomCache, "\1" + furnitureId + '\t');
        }

        if (roomId > 0L) {
            String roomMarker = "\1" + roomId + '\2';
            state.pendingRoomCache = state.pendingRoomCache.replace(roomMarker, "");
            state.pendingRoomCache = removeRepresentedCacheRecord(state.pendingRoomCache, "\1" + roomId + '\t');
            state.pendingRoomCache += roomMarker;
        }
        return state;
    }

    public static FurnitureCacheState removeFurnitureCacheMarker(
        String pendingRoomCache,
        String pendingFurnitureCache,
        String representedRoomCache,
        long furnitureId
    ) {
        FurnitureCacheState state = new FurnitureCacheState();
        state.pendingRoomCache = Vb.cStr(pendingRoomCache);
        state.pendingFurnitureCache = Vb.cStr(pendingFurnitureCache);
        state.representedRoomCache = Vb.cStr(representedRoomCache);
        if (furnitureId <= 0L) {
            return state;
        }

        String exactMarker = "\1" + furnitureId + '\2';
        String recordMarker = "\1" + furnitureId + '\t';
        state.pendingRoomCache = state.pendingRoomCache.replace(exactMarker, "");
        state.pendingFurnitureCache = state.pendingFurnitureCache.replace(exactMarker, "");
        state.pendingRoomCache = removeRepresentedCacheRecord(state.pendingRoomCache, recordMarker);
        state.pendingFurnitureCache = removeRepresentedCacheRecord(state.pendingFurnitureCache, recordMarker);
        if (!state.representedRoomCache.isEmpty()) {
            state.representedRoomCache = removeRepresentedCacheRecord(state.representedRoomCache, exactMarker);
            state.representedRoomCache = removeRepresentedCacheRecord(state.representedRoomCache, recordMarker);
        }
        return state;
    }

    public static long nextFurnitureState(String productSprite, long currentState, long maxState) {
        String sprite = Vb.cStr(productSprite).toLowerCase();
        if (sprite.contains("dice")) {
            return Functions.Proc_10_4_809CA0(1, 6);
        }
        if (sprite.startsWith("bb_score_") || sprite.startsWith("es_score_") || sprite.contains("score")) {
            long resolvedMaxState = maxState <= 0L ? 99L : maxState;
            long nextState = currentState + 1L;
            return nextState > resolvedMaxState ? 0L : nextState;
        }
        long resolvedMaxState = maxState <= 0L ? 1L : maxState;
        long nextState = currentState + 1L;
        return nextState > resolvedMaxState ? 0L : nextState;
    }

    public static String furnitureStatePayload(long furnitureId, long stateValue) {
        return "AX" + furnitureId + '\2' + stateValue + '\2';
    }

    public static FurnitureStateCache representedFurnitureStateCache(
        String pendingRoomCache,
        String pendingFurnitureCache,
        String representedRoomCache,
        long roomId,
        long furnitureId,
        long stateValue
    ) {
        FurnitureStateCache state = new FurnitureStateCache();
        state.pendingRoomCache = Vb.cStr(pendingRoomCache);
        state.pendingFurnitureCache = Vb.cStr(pendingFurnitureCache);
        state.representedRoomCache = Vb.cStr(representedRoomCache);
        if (roomId <= 0L || furnitureId <= 0L) {
            return state;
        }

        String roomMarker = "\1" + roomId + '\2';
        state.pendingRoomCache = state.pendingRoomCache.replace(roomMarker, "");
        state.pendingRoomCache = removeRepresentedCacheRecord(state.pendingRoomCache, "\1" + roomId + '\t');
        state.pendingRoomCache += roomMarker;

        String furnitureMarker = "\1" + furnitureId + '\2';
        state.pendingFurnitureCache = state.pendingFurnitureCache.replace(furnitureMarker, "");
        state.pendingFurnitureCache += furnitureMarker;

        state.representedRoomCache = removeRepresentedCacheRecord(state.representedRoomCache, "\1" + roomId + '\t');
        state.representedRoomCache = removeRepresentedCacheRecord(state.representedRoomCache, furnitureMarker);
        state.representedRoomCache = removeRepresentedCacheRecord(state.representedRoomCache, "\1" + furnitureId + '\t');
        state.representedRoomCache += "\1" + roomId + '\t' + furnitureId + '\t' + stateValue + '\2';
        return state;
    }

    public static FurnitureStateCache representedFurnitureStateWrite(
        String pendingRoomCache,
        String pendingFurnitureCache,
        String representedRoomCache,
        long roomId,
        long furnitureId,
        String stateText
    ) {
        FurnitureStateCache state = new FurnitureStateCache();
        state.pendingRoomCache = Vb.cStr(pendingRoomCache);
        state.pendingFurnitureCache = Vb.cStr(pendingFurnitureCache);
        state.representedRoomCache = Vb.cStr(representedRoomCache);
        if (furnitureId <= 0L) {
            return state;
        }

        String furnitureMarker = "\1" + furnitureId + '\2';
        state.pendingFurnitureCache = state.pendingFurnitureCache.replace(furnitureMarker, "");
        state.pendingFurnitureCache += furnitureMarker;

        if (roomId > 0L) {
            String roomMarker = "\1" + roomId + '\2';
            state.pendingRoomCache = state.pendingRoomCache.replace(roomMarker, "");
            state.pendingRoomCache += roomMarker;
        }

        state.representedRoomCache = removeRepresentedCacheRecord(state.representedRoomCache, furnitureMarker);
        state.representedRoomCache = removeRepresentedCacheRecord(state.representedRoomCache, "\1" + furnitureId + '\t');
        state.representedRoomCache += "\1" + furnitureId + '\t' + roomId + '\t' + Vb.cStr(stateText) + '\2';
        return state;
    }

    public static void handlingRepresentedFurnitureStateWrite(Object... args) {
        try {
            long roomId = 0L;
            long furnitureId = 0L;
            String stateText = "";
            if (args != null && args.length >= 3) {
                roomId = Vb.val(args[0]);
                furnitureId = Vb.val(args[1]);
                stateText = Vb.cStr(args[2]);
            } else if (args != null && args.length >= 2) {
                furnitureId = Vb.val(args[0]);
                stateText = Vb.cStr(args[1]);
            }
            if (furnitureId <= 0L) {
                return;
            }
            if (roomId <= 0L) {
                roomId = Vb.val(MySQL.Proc_5_2_6D4690("SELECT id_room FROM furnitures WHERE id='"
                    + furnitureId + "' LIMIT 1", 0, 0));
            }
            FurnitureStateCache state = representedFurnitureStateWrite(
                Licence.global_008291F8,
                Licence.global_008291FC,
                Licence.global_00829310,
                roomId,
                furnitureId,
                stateText);
            Licence.global_008291F8 = state.pendingRoomCache;
            Licence.global_008291FC = state.pendingFurnitureCache;
            Licence.global_00829310 = state.representedRoomCache;
            if (roomId > 0L) {
                Proc_6_106_74B750(Path.of(Functions.applicationPath, "CACHE", "ROOMS", roomId + ".cache").toString(), 0, 0);
                Proc_6_106_74B750(Path.of(Functions.applicationPath, "CACHE", "PATHFINDER", roomId + ".cache").toString(), 0, 0);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static String Proc_6_156_7972B0(Object... args) {
        if (args == null || args.length == 0) {
            return "";
        }
        long baseValue = Vb.val(args[0]);
        long firstValue = args.length >= 2 ? Vb.val(args[1]) : 0L;
        String secondValue = args.length >= 3 ? Vb.cStr(args[2]) : "";
        String thirdValue = args.length >= 4 ? Vb.cStr(args[3]) : "";
        long fourthValue = args.length >= 5 ? Vb.val(args[4]) : 0L;
        return wallInventoryPlacementPayload(baseValue, firstValue, secondValue, thirdValue, fourthValue);
    }

    public static String wallInventoryPlacementPayload(
        long furnitureId,
        long productId,
        String wallPosition,
        String itemData,
        long secondaryValue
    ) {
        String payload = Crypto.Proc_3_0_6D2AF0(productId, null, furnitureId + "\2")
            + Vb.cStr(wallPosition) + '\2'
            + Vb.cStr(itemData) + '\2';
        return "0" + Crypto.Proc_3_0_6D2AF0(secondaryValue, null, payload);
    }

    public static String Proc_6_161_7B2EE0(Object... args) {
        if (args == null || args.length == 0) {
            return "";
        }
        return floorItemPlacementPayload(
            Vb.val(args[0]),
            args.length >= 2 ? Vb.val(args[1]) : 0L,
            args.length >= 3 ? Vb.val(args[2]) : 0L,
            args.length >= 4 ? Vb.val(args[3]) : 0L,
            args.length >= 5 ? Vb.val(args[4]) : 0L,
            args.length >= 6 ? Vb.cStr(args[5]) : "",
            args.length >= 7 ? Vb.cStr(args[6]) : "",
            args.length >= 8 ? Vb.val(args[7]) : 0L,
            args.length >= 9 ? Vb.val(args[8]) : 0L);
    }

    public static String floorItemPlacementPayload(
        long furnitureId,
        long positionX,
        long positionY,
        long rotation,
        long positionZ,
        String stateText,
        String itemData,
        long secondaryValue,
        long productId
    ) {
        String normalizedItemData = Vb.cStr(itemData).replace('\b', '\t').replace("{{9}}", "\t");
        String payload = Crypto.Proc_3_0_6D2AF0(furnitureId, null, "0");
        payload = Crypto.Proc_3_0_6D2AF0(positionX, null, payload);
        payload = "0" + Crypto.Proc_3_0_6D2AF0(positionY, null, payload);
        payload = "0" + Crypto.Proc_3_0_6D2AF0(rotation, null, payload);
        payload = Crypto.Proc_3_0_6D2AF0(positionZ, null, payload) + Vb.cStr(stateText) + '\2';
        payload = Crypto.Proc_3_0_6D2AF0(secondaryValue, null, payload) + normalizedItemData + '\2' + "M";
        return Crypto.Proc_3_0_6D2AF0(productId, null, payload);
    }

    public static String systemHandshakePayload(String configuredDateFormat) {
        String dateFormat = Vb.cStr(configuredDateFormat);
        if (dateFormat.isEmpty()) {
            dateFormat = "DAQBHHIIKHJHPAHQA";
        }
        return "0" + dateFormat + '\2' + "SAHPB" + "http://www.alpha-series.com/" + '\2' + "QBH";
    }

    public static String handlingLoginTicketFromPayload(String packetPayload) {
        String requestPayload = Vb.cStr(packetPayload);
        if (requestPayload.startsWith("F_")) {
            requestPayload = requestPayload.substring(2);
        }
        requestPayload = Functions.Proc_10_10_80A7F0(requestPayload, 0, 0).trim();
        if (requestPayload.startsWith("F_")) {
            requestPayload = requestPayload.substring(2);
        }
        return requestPayload;
    }

    public static void handlingStoreSocketSession(int socketIndex, String sessionRecord) {
        Licence.storeSocketSession(socketIndex, sessionRecord);
    }

    public static String normalizeRoomModelMap(String modelMap) {
        String modelPayload = Vb.cStr(modelMap).replace('\n', '\r');
        while (modelPayload.contains("\r\r")) {
            modelPayload = modelPayload.replace("\r\r", "\r");
        }
        return modelPayload;
    }

    public static void sendRoomPollPrompt(int socketIndex, String userId, long roomId) {
        if (socketIndex <= 0 || Vb.cStr(userId).isEmpty() || roomId <= 0L) {
            return;
        }
        String pollRow = MySQL.Proc_5_2_6D4690("SELECT id,description_title FROM poll WHERE id_room='"
            + roomId + "' AND timestamp_hide>UNIX_TIMESTAMP() LIMIT 1", 0, 0);
        if (pollRow.isEmpty()) {
            return;
        }
        String[] pollFields = pollRow.split("\t", -1);
        long pollId = Vb.val(handlingField(pollFields, 0));
        if (pollId <= 0L) {
            return;
        }
        String escapedUserId = Functions.Proc_10_11_80A9C0(userId, 0, 0);
        if (!MySQL.Proc_5_2_6D4690("SELECT id_user FROM poll_exit WHERE id_user='" + escapedUserId
            + "' AND id_poll='" + pollId + "' LIMIT 1", 0, 0).isEmpty()) {
            return;
        }
        if (!MySQL.Proc_5_2_6D4690("SELECT id FROM poll_results WHERE id_user='" + escapedUserId
            + "' AND id_poll='" + pollId + "' LIMIT 1", 0, 0).isEmpty()) {
            return;
        }
        Proc_6_244_801E80(socketIndex, Crypto.Proc_3_0_6D2AF0(pollId, null, "D|") + handlingField(pollFields, 1) + '\2', 0);
    }

    public static String handlingLoginActivityPointPayload(long pointType, long pointsValue) {
        return Crypto.Proc_3_0_6D2AF0(pointType, null,
            Crypto.Proc_3_0_6D2AF0(pointsValue, null, "Fv") + "H");
    }

    public static String representedActivityPointAwardPayload(long pointType, long pointsValue) {
        return Crypto.Proc_3_0_6D2AF0(pointType, null,
            Crypto.Proc_3_0_6D2AF0(pointsValue, null, "Fv")) + "H";
    }

    public static long representedActivityPointSessionSeconds(long socketIndex, String userId) {
        if (socketIndex <= 0L || Vb.cStr(userId).isEmpty()) {
            return 0L;
        }
        String marker = "[" + socketIndex + "]";
        long tickValue = 0L;
        int startAt = representedActivityPointTicks.indexOf(marker);
        if (startAt >= 0) {
            int valueStart = startAt + marker.length();
            int endAt = representedActivityPointTicks.indexOf('[', valueStart);
            if (endAt < 0) {
                endAt = representedActivityPointTicks.length();
            }
            tickValue = Vb.val(representedActivityPointTicks.substring(valueStart, endAt));
            representedActivityPointTicks = representedActivityPointTicks.substring(0, startAt)
                + representedActivityPointTicks.substring(endAt);
        } else {
            tickValue = Vb.val(MySQL.Proc_5_2_6D4690("SELECT online_time FROM users WHERE id='"
                + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "' LIMIT 1", 0, 0));
        }
        tickValue += 60L;
        representedActivityPointTicks += marker + tickValue;
        return tickValue;
    }

    public static ActivityPointAward activityPointAwardDecision(
        long sessionSeconds,
        long pointType,
        long intervalSeconds,
        long maxPoints,
        long awardAmount,
        long currentPoints
    ) {
        ActivityPointAward result = new ActivityPointAward();
        result.pointType = pointType;
        result.awardAmount = awardAmount;
        if (sessionSeconds <= 0L || intervalSeconds <= 0L || sessionSeconds % intervalSeconds != 0L) {
            return result;
        }
        long effectiveMaxPoints = maxPoints <= 0L ? 1L : maxPoints;
        if (currentPoints >= effectiveMaxPoints || awardAmount == 0L) {
            return result;
        }
        result.newPoints = currentPoints + awardAmount;
        result.payload = representedActivityPointAwardPayload(pointType, result.newPoints);
        result.shouldAward = true;
        return result;
    }

    public static boolean isSocketMarkedBusy(String representedSocketCache, long socketIndex) {
        String cache = Vb.cStr(representedSocketCache);
        if (socketIndex <= 0L || cache.isEmpty()) {
            return false;
        }
        String marker = "[" + socketIndex + "]";
        int startAt = cache.indexOf(marker);
        if (startAt < 0) {
            return false;
        }
        int recordStart = startAt + marker.length();
        int recordEnd = cache.indexOf('[', recordStart);
        if (recordEnd < 0) {
            recordEnd = cache.length();
        }
        String recordText = cache.substring(recordStart, recordEnd);
        String[] fields = recordText.split("\2", -1);
        return fields.length >= 6 && Vb.val(fields[5]) != 0L;
    }

    public static String ownProfilePayload(String userRow) {
        String[] fields = Vb.cStr(userRow).split("\t", -1);
        if (fields.length < 6) {
            return "";
        }
        long userId = Vb.val(handlingField(fields, 0));
        if (userId <= 0L) {
            return "";
        }
        String userName = handlingField(fields, 1);
        String mottoText = handlingField(fields, 2);
        String genderText = handlingField(fields, 3).toUpperCase();
        genderText = genderText.isEmpty() ? "M" : genderText.substring(0, 1);
        if (!"M".equals(genderText) && !"F".equals(genderText)) {
            genderText = "M";
        }
        long respectAmount = Vb.val(handlingField(fields, 4));
        long scratchAmount = Vb.val(handlingField(fields, 5));
        String payload = "@E" + userId + '\2' + userName + '\2' + mottoText + '\2';
        payload += genderText + "\2\2\2H\2HIH";
        payload = Crypto.Proc_3_0_6D2AF0(respectAmount, null, payload);
        return Crypto.Proc_3_0_6D2AF0(scratchAmount, null, payload);
    }

    public static long soundSettingFromWire(String packetPayload) {
        String requestPayload = Vb.cStr(packetPayload);
        if (requestPayload.startsWith("Ce")) {
            requestPayload = requestPayload.substring(2);
        }
        long soundSetting = Vb.val(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
        if (soundSetting <= 0L) {
            soundSetting = Vb.val(requestPayload);
        }
        return soundSetting > 0L && soundSetting < 101L ? soundSetting : 0L;
    }

    public static String loginGroupPayload(long groupId, String groupRow) {
        if (groupId <= 0L || Vb.cStr(groupRow).isEmpty()) {
            return "";
        }
        String[] fields = Vb.cStr(groupRow).split("\t", -1);
        String payload = Crypto.Proc_3_0_6D2AF0(groupId, null, "Dt");
        payload += handlingField(fields, 0) + '\2';
        payload += handlingField(fields, 1) + '\2';
        payload += handlingField(fields, 2) + '\2';
        payload += Crypto.Proc_3_0_6D2AF0(Vb.val(handlingField(fields, 3)), null, "") + "H";
        return payload;
    }

    public static void storeRepresentedInteractionPair(long sourceSocketIndex, long targetSocketIndex, long interactionState) {
        if (sourceSocketIndex <= 0L || targetSocketIndex <= 0L) {
            return;
        }
        removeRepresentedInteractionPair(sourceSocketIndex);
        removeRepresentedInteractionPair(targetSocketIndex);
        String sourceRow = sourceSocketIndex + "\t" + targetSocketIndex + "\t" + interactionState;
        String targetRow = targetSocketIndex + "\t" + sourceSocketIndex + "\t" + interactionState;
        representedInteractionPairs = representedInteractionPairs.isEmpty()
            ? sourceRow + "\r" + targetRow
            : representedInteractionPairs + "\r" + sourceRow + "\r" + targetRow;
    }

    public static void removeRepresentedInteractionPair(long socketIndex) {
        if (socketIndex <= 0L || representedInteractionPairs.isEmpty()) {
            return;
        }
        StringBuilder rebuilt = new StringBuilder();
        for (String row : representedInteractionPairs.split("\r", -1)) {
            if (!row.isEmpty()) {
                String[] fields = row.split("\t", -1);
                if (fields.length >= 1 && Vb.val(handlingField(fields, 0)) != socketIndex) {
                    appendRow(rebuilt, row);
                }
            }
        }
        representedInteractionPairs = rebuilt.toString();
        removeRepresentedTradeOffer(socketIndex, 0L);
    }

    public static int representedInteractionPartner(long socketIndex) {
        if (socketIndex <= 0L || representedInteractionPairs.isEmpty()) {
            return 0;
        }
        for (String row : representedInteractionPairs.split("\r", -1)) {
            if (!row.isEmpty()) {
                String[] fields = row.split("\t", -1);
                if (fields.length >= 2 && Vb.val(handlingField(fields, 0)) == socketIndex) {
                    return (int) Vb.val(handlingField(fields, 1));
                }
            }
        }
        return 0;
    }

    public static long representedInteractionState(long socketIndex) {
        if (socketIndex <= 0L || representedInteractionPairs.isEmpty()) {
            return 0L;
        }
        for (String row : representedInteractionPairs.split("\r", -1)) {
            if (!row.isEmpty()) {
                String[] fields = row.split("\t", -1);
                if (fields.length >= 3 && Vb.val(handlingField(fields, 0)) == socketIndex) {
                    return Vb.val(handlingField(fields, 2));
                }
            }
        }
        return 0L;
    }

    public static void storeRepresentedTradeOffer(long socketIndex, long furnitureId, long productId, String signText, long secondaryValue) {
        representedTradeOffers = representedTradeOfferStore(representedTradeOffers, socketIndex, furnitureId, productId, signText, secondaryValue);
    }

    public static void removeRepresentedTradeOffer(long socketIndex, long furnitureId) {
        representedTradeOffers = representedTradeOfferRemove(representedTradeOffers, socketIndex, furnitureId);
    }

    public static String questCompletionPayload(
        long campaignId,
        String questName,
        long campaignLevelCount,
        long questId,
        long userQuestLevel,
        long progressValue,
        long activityCount
    ) {
        long resolvedActivityCount = activityCount <= 0L ? 1L : activityCount;
        String payload = Crypto.Proc_3_0_6D2AF0(campaignId, null, "") + Vb.cStr(questName) + '\2';
        payload += Crypto.Proc_3_0_6D2AF0(campaignLevelCount, null, "");
        payload += Crypto.Proc_3_0_6D2AF0(questId, null, "");
        payload += Crypto.Proc_3_0_6D2AF0(userQuestLevel, null, "");
        payload += Crypto.Proc_3_0_6D2AF0(progressValue, null, "");
        payload += Crypto.Proc_3_0_6D2AF0(resolvedActivityCount, null, "");
        payload += Crypto.Proc_3_0_6D2AF0(0, null, "");
        return payload;
    }

    public static long questRequestIdFromWire(String packetPayload, String prefix) {
        return idRequestFromWire(packetPayload, prefix);
    }

    public static long nextQuestId(String questRows, String activeRow) {
        long currentQuestId = 0L;
        long currentLevel = 0L;
        if (!Vb.cStr(activeRow).isEmpty()) {
            String[] activeFields = Vb.cStr(activeRow).split("\t", -1);
            currentQuestId = Vb.val(handlingField(activeFields, 0));
            currentLevel = Vb.val(handlingField(activeFields, 1));
        }

        long currentCampaignId = 0L;
        long fallbackQuestId = 0L;
        long fallbackCampaignId = 0L;
        long fallbackLevel = Integer.MAX_VALUE;
        boolean foundCurrent = false;
        for (String row : Vb.cStr(questRows).split("\r", -1)) {
            String rowValue = row.trim();
            if (!rowValue.isEmpty()) {
                String[] fields = rowValue.split("\t", -1);
                if (fields.length >= 9) {
                    long rowQuestId = Vb.val(handlingField(fields, 0));
                    long rowLevel = Vb.val(handlingField(fields, 1));
                    long rowCampaignId = Vb.val(handlingField(fields, 8));
                    if (fallbackQuestId <= 0L || rowLevel < fallbackLevel) {
                        fallbackQuestId = rowQuestId;
                        fallbackCampaignId = rowCampaignId;
                        fallbackLevel = rowLevel;
                    }
                    if (rowQuestId == currentQuestId) {
                        currentCampaignId = rowCampaignId;
                        currentLevel = rowLevel;
                        foundCurrent = true;
                    }
                }
            }
        }
        if (!foundCurrent) {
            currentCampaignId = fallbackCampaignId;
            currentLevel = fallbackLevel - 1L;
        }

        long requestedQuestId = 0L;
        long bestLevel = Integer.MAX_VALUE;
        for (String row : Vb.cStr(questRows).split("\r", -1)) {
            String rowValue = row.trim();
            if (!rowValue.isEmpty()) {
                String[] fields = rowValue.split("\t", -1);
                if (fields.length >= 9) {
                    long rowQuestId = Vb.val(handlingField(fields, 0));
                    long rowLevel = Vb.val(handlingField(fields, 1));
                    long rowCampaignId = Vb.val(handlingField(fields, 8));
                    if (rowCampaignId == currentCampaignId && rowLevel > currentLevel && rowLevel < bestLevel) {
                        requestedQuestId = rowQuestId;
                        bestLevel = rowLevel;
                    }
                }
            }
        }
        return requestedQuestId > 0L ? requestedQuestId : fallbackQuestId;
    }

    public static QuestProgressDecision questProgressDecision(String activeRow, String questRows, long remainingWait) {
        QuestProgressDecision decision = new QuestProgressDecision();
        if (Vb.cStr(activeRow).isEmpty()) {
            return decision;
        }
        String[] activeFields = Vb.cStr(activeRow).split("\t", -1);
        decision.questId = Vb.val(handlingField(activeFields, 0));
        decision.numericQuestId = Vb.val(handlingField(activeFields, 1));
        decision.progressValue = Vb.val(handlingField(activeFields, 2));
        String timeNextText = handlingField(activeFields, 4);
        if (decision.questId <= 0L) {
            return decision;
        }

        boolean matchedQuest = false;
        for (String row : Vb.cStr(questRows).split("\r", -1)) {
            String rowValue = row.trim();
            if (!rowValue.isEmpty()) {
                String[] fields = rowValue.split("\t", -1);
                if (fields.length >= 11 && Vb.val(handlingField(fields, 0)) == decision.questId) {
                    decision.amountRequired = Vb.val(handlingField(fields, 9));
                    decision.waitAmount = Vb.val(handlingField(fields, 10));
                    matchedQuest = true;
                    break;
                }
            }
        }
        if (!matchedQuest) {
            return decision;
        }

        if (!timeNextText.isEmpty() && !"0".equals(timeNextText)) {
            decision.remainingWait = Math.max(0L, remainingWait);
            if (decision.remainingWait > 0L) {
                decision.shouldSendList = true;
                return decision;
            }
        } else if (decision.waitAmount > 0L && decision.progressValue > 0L && decision.progressValue < decision.amountRequired) {
            decision.shouldScheduleWait = true;
            decision.shouldSendList = true;
            return decision;
        }

        if (decision.amountRequired <= 0L) {
            decision.amountRequired = 1L;
        }
        if (decision.progressValue >= decision.amountRequired) {
            decision.shouldComplete = true;
        } else {
            decision.shouldSendList = true;
        }
        return decision;
    }

    public static String questListPayload(String questRows, String userQuestRows) {
        String userQuestText = "\r" + Vb.cStr(userQuestRows) + "\r";
        long lastCampaignId = -1L;
        long campaignLevelCount = 0L;
        long questCount = 0L;
        StringBuilder questPayload = new StringBuilder();
        for (String row : Vb.cStr(questRows).split("\r", -1)) {
            String questRow = row.trim();
            if (!questRow.isEmpty()) {
                String[] questFields = questRow.split("\t", -1);
                if (questFields.length >= 11) {
                    long questId = Vb.val(handlingField(questFields, 0));
                    long questLevel = Vb.val(handlingField(questFields, 1));
                    String questName = handlingField(questFields, 2);
                    long rewardAmount = Vb.val(handlingField(questFields, 4));
                    long rewardType = Vb.val(handlingField(questFields, 5));
                    long campaignId = Vb.val(handlingField(questFields, 8));
                    long activityCount = Vb.val(handlingField(questFields, 9));
                    long waitSeconds = Vb.val(handlingField(questFields, 10));

                    if (campaignId != lastCampaignId) {
                        lastCampaignId = campaignId;
                        campaignLevelCount = 0L;
                    }
                    campaignLevelCount++;

                    String[] userQuestFields = userQuestFields(userQuestText, questId);
                    long userLevel = Vb.val(handlingField(userQuestFields, 1));
                    String timestampDone = handlingField(userQuestFields, 2);
                    String timestampAccepted = handlingField(userQuestFields, 3);
                    String timeNextText = handlingField(userQuestFields, 4);
                    long progressValue = Vb.val(handlingField(userQuestFields, 5));
                    long remainingWait = Vb.val(handlingField(userQuestFields, 6));

                    long stateCode = 0L;
                    if (!timestampDone.isEmpty() && !"0".equals(timestampDone)) {
                        stateCode = 2L;
                    } else if (!timestampAccepted.isEmpty() && !"0".equals(timestampAccepted)) {
                        stateCode = 1L;
                    }
                    if (!timeNextText.isEmpty() && !"0".equals(timeNextText)) {
                        waitSeconds = remainingWait;
                    }

                    String rowPayload = Crypto.Proc_3_0_6D2AF0(campaignId, null, "") + questName + '\2';
                    rowPayload += Crypto.Proc_3_0_6D2AF0(questId, null, "");
                    rowPayload += Crypto.Proc_3_0_6D2AF0(questLevel, null, "");
                    rowPayload += Crypto.Proc_3_0_6D2AF0(campaignLevelCount, null, "");
                    rowPayload += Crypto.Proc_3_0_6D2AF0(stateCode, null, "");
                    rowPayload += Crypto.Proc_3_0_6D2AF0(userLevel, null, "");
                    rowPayload += Crypto.Proc_3_0_6D2AF0(progressValue, null, "");
                    rowPayload += Crypto.Proc_3_0_6D2AF0(activityCount, null, "");
                    rowPayload += Crypto.Proc_3_0_6D2AF0(rewardType, null, "");
                    rowPayload += Crypto.Proc_3_0_6D2AF0(rewardAmount, null, "");
                    rowPayload += "HHH\2\2H\2HHH";
                    rowPayload += Crypto.Proc_3_0_6D2AF0(waitSeconds, null, "");

                    questPayload.append(rowPayload);
                    questCount++;
                }
            }
        }
        return Crypto.Proc_3_0_6D2AF0(0, null, Crypto.Proc_3_0_6D2AF0(questCount, null, "L`"))
            + questPayload;
    }

    public static String Proc_6_166_7BE940(Object... args) {
        long userId = args != null && args.length >= 1 ? Vb.val(args[0]) : 0L;
        String userName = args != null && args.length >= 2 ? Vb.cStr(args[1]) : "";
        String motto = args != null && args.length >= 3 ? Vb.cStr(args[2]) : "";
        String figure = args != null && args.length >= 4 ? Vb.cStr(args[3]) : "";
        long rankValue = args != null && args.length >= 5 ? Vb.val(args[4]) : 0L;
        long followCount = args != null && args.length >= 6 ? Vb.val(args[5]) : 0L;
        long isOnline = args != null && args.length >= 7 ? Vb.val(args[6]) : 0L;
        String lastOnlineText = args != null && args.length >= 8 ? Vb.cStr(args[7]) : "";
        long relationshipState = args != null && args.length >= 9 ? Vb.val(args[8]) : 0L;
        return messengerFriendPayload(userId, userName, motto, figure, rankValue, followCount, isOnline, lastOnlineText, relationshipState);
    }

    public static String Proc_6_164_7BC820(Object... args) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long questId = args != null && args.length >= 2 ? Vb.val(args[1]) : 0L;
            long numericQuestId = args != null && args.length >= 3 ? Vb.val(args[2]) : 0L;
            String escapedUserId = Functions.Proc_10_11_80A9C0(userId, 0, 0);
            String activeRow;
            if (questId <= 0L) {
                activeRow = MySQL.Proc_5_2_6D4690("SELECT id_quest,id_numericquest,progress,id_level FROM users_quests WHERE id_user='"
                    + escapedUserId + "' AND timestamp_accepted IS NOT NULL AND timestamp_done IS NULL LIMIT 1", 0, 0);
            } else {
                activeRow = MySQL.Proc_5_2_6D4690("SELECT id_quest,id_numericquest,progress,id_level FROM users_quests WHERE id_user='"
                    + escapedUserId + "' AND id_quest='" + questId + "' LIMIT 1", 0, 0);
            }
            if (activeRow.isEmpty()) {
                return "";
            }
            String[] activeFields = activeRow.split("\t", -1);
            questId = Vb.val(handlingField(activeFields, 0));
            if (numericQuestId <= 0L) {
                numericQuestId = Vb.val(handlingField(activeFields, 1));
            }
            long progressValue = Vb.val(handlingField(activeFields, 2));
            long userQuestLevel = Vb.val(handlingField(activeFields, 3));
            if (questId <= 0L) {
                return "";
            }
            String questRows = questRowsFromSource();
            String[] questFields = questFieldsById(questRows, questId);
            if (questFields.length < 11) {
                return "";
            }
            String questName = handlingField(questFields, 2);
            long rewardAmount = Vb.val(handlingField(questFields, 4));
            long rewardType = Vb.val(handlingField(questFields, 5));
            long campaignId = Vb.val(handlingField(questFields, 8));
            long activityCount = Vb.val(handlingField(questFields, 9));
            if (activityCount <= 0L) {
                activityCount = 1L;
            }
            long campaignLevelCount = 0L;
            for (String row : Vb.cStr(questRows).split("\r", -1)) {
                String rowText = row.trim();
                if (!rowText.isEmpty()) {
                    String[] fields = rowText.split("\t", -1);
                    if (fields.length >= 9 && Vb.val(handlingField(fields, 8)) == campaignId) {
                        campaignLevelCount++;
                    }
                }
            }
            String completionPayload = questCompletionPayload(campaignId, questName, campaignLevelCount, questId,
                userQuestLevel, progressValue, activityCount);
            Proc_6_244_801E80(socketIndex, "Lb" + completionPayload, 0);
            if (progressValue < activityCount) {
                return "";
            }
            if (rewardAmount != 0L && rewardType >= 0L && rewardType <= 20L) {
                String pointColumn = "activitypoints_" + rewardType;
                long currentPoints = Vb.val(MySQL.Proc_5_2_6D4690("SELECT " + pointColumn + " FROM users WHERE id='"
                    + escapedUserId + "' LIMIT 1", 0, 0));
                MySQL.Proc_5_0_6D3CD0("UPDATE users SET " + pointColumn + "=" + pointColumn + "+" + rewardAmount
                    + " WHERE id='" + escapedUserId + "' LIMIT 1", 0, 0);
                Proc_6_244_801E80(socketIndex, representedActivityPointAwardPayload(rewardType, currentPoints + rewardAmount), 0);
            }
            MySQL.Proc_5_0_6D3CD0("UPDATE users_quests SET id_level=id_level+1,progress='0',id_numericquest='0',timestamp_done=UNIX_TIMESTAMP() WHERE id_user='"
                + escapedUserId + "' AND id_quest='" + questId + "' LIMIT 1", 0, 0);
            Proc_6_244_801E80(socketIndex, "La" + completionPayload, 0);
            Proc_6_236_7F8540(socketIndex, "", "");
            return "";
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static String messengerFriendPayload(
        long userId,
        String userName,
        String motto,
        String figure,
        long rankValue,
        long followCount,
        long isOnline,
        String lastOnlineText,
        long relationshipState
    ) {
        return MessengerPayloads.friend(
            userId,
            userName,
            motto,
            figure,
            rankValue,
            followCount,
            isOnline,
            lastOnlineText,
            relationshipState,
            messengerFollowEnabled());
    }

    public static String messengerFriendSummaryPayloadFromRow(String rowText, long relationshipState) {
        return MessengerPayloads.friendSummaryFromRow(rowText, relationshipState, messengerFollowEnabled());
    }

    public static String messengerFriendSummaryPayload(String userId, long relationshipState) {
        try {
            if (Vb.cStr(userId).isEmpty() || "0".equals(Vb.cStr(userId))) {
                return "";
            }
            String dateFormat = Functions.Proc_10_0_809570("com.mysql.format.date", "%d-%m-%Y", 0);
            String timeFormat = Functions.Proc_10_0_809570("com.mysql.format.time", "%H:%i", 0);
            String rowText = MySQL.Proc_5_2_6D4690("SELECT id,name,motto,figure,level,id_socket,DATE_FORMAT(FROM_UNIXTIME(lastonline_time), '"
                + Functions.Proc_10_11_80A9C0(dateFormat + " " + timeFormat, 0, 0)
                + "') FROM users WHERE id='" + Functions.Proc_10_11_80A9C0(userId, 0, 0) + "' LIMIT 1", 0, 0);
            if (rowText.isEmpty()) {
                return "";
            }
            return messengerFriendSummaryPayloadFromRow(rowText, relationshipState);
        } catch (Exception ignored) {
            return "";
        }
    }

    public static String messengerSearchResultPayload(
        String userId,
        String userName,
        String figureText,
        String mottoText,
        String nicknameText,
        String lastOnlineText,
        long isOnline
    ) {
        return MessengerPayloads.searchResult(userId, userName, figureText, mottoText, nicknameText, lastOnlineText, isOnline);
    }

    public static boolean messengerFollowEnabled() {
        return Vb.val(Functions.Proc_10_0_809570("com.client.messenger.follow.enabled", 0)) != 0L;
    }

    public static long messengerMaxFriends(long configIndex) {
        return Licence.messengerSettings().maxFriends(configIndex);
    }

    public static String requestTextFromWirePayload(String packetPayload, String prefix, int maxLength) {
        String requestPayload = Vb.cStr(packetPayload);
        if (!Vb.cStr(prefix).isEmpty() && requestPayload.startsWith(prefix)) {
            requestPayload = requestPayload.substring(prefix.length());
        }
        String value = Functions.Proc_10_7_80A190(requestPayload, 0, 0);
        if (value.isEmpty()) {
            LongRef offset = new LongRef(1);
            value = readWireString(requestPayload, offset);
        }
        if (maxLength >= 0 && value.length() > maxLength) {
            return value.substring(0, maxLength);
        }
        return value;
    }

    public static FriendTargetList friendDeleteTargetsFromPayload(String packetPayload) {
        String requestPayload = Vb.cStr(packetPayload);
        if (requestPayload.startsWith("@f")) {
            requestPayload = requestPayload.substring(2);
        }
        FriendTargetList result = new FriendTargetList();
        LongRef offset = new LongRef(1);
        long firstValue = readWireLong(requestPayload, offset);
        if (firstValue == 1L) {
            result.deleteAllPending = true;
            return result;
        }

        long maxTargets = firstValue <= 0L ? 75L : Math.min(firstValue, 75L);
        StringBuilder targetList = new StringBuilder();
        while (offset.value <= requestPayload.length() && result.targetCount < maxTargets) {
            long previousOffset = offset.value;
            long targetUserId = readWireLong(requestPayload, offset);
            if (targetUserId <= 0L || offset.value == previousOffset) {
                break;
            }
            String token = String.valueOf(targetUserId);
            if (!("," + targetList + ",").contains("," + token + ",")) {
                if (targetList.length() > 0) {
                    targetList.append(',');
                }
                targetList.append(token);
                result.targetCount++;
            }
        }
        if (targetList.length() == 0 && firstValue > 1L) {
            targetList.append(firstValue);
            result.targetCount = 1L;
        }
        result.targetList = targetList.toString();
        return result;
    }

    public static String messengerAcceptedFriendsPayload(String payloadRows, long acceptedCount) {
        return MessengerPayloads.acceptedFriends(payloadRows, acceptedCount);
    }

    public static String messengerRemoveFriendsPayload(String targetIdsPayload, long removedCount) {
        return MessengerPayloads.removeFriends(targetIdsPayload, removedCount);
    }

    public static String messengerRemovedIdPayload(long targetUserId) {
        return MessengerPayloads.removedId(targetUserId);
    }

    public static String messengerRequestAcceptedCallerPayload(long targetUserId) {
        return MessengerPayloads.requestAcceptedCaller(targetUserId);
    }

    public static String messengerRequestDeniedPayload() {
        return MessengerPayloads.requestDenied();
    }

    public static String messengerRequestNotifyPayload(long userId, String userName) {
        return MessengerPayloads.requestNotify(userId, userName);
    }

    public static String messengerPendingRequestsPayload(String rowText) {
        return MessengerPayloads.pendingRequests(rowText);
    }

    public static String messengerFriendListPayload(String rowText, long maxFriends0, long maxFriends1, long maxFriends2) {
        return MessengerPayloads.friendList(rowText, maxFriends0, maxFriends1, maxFriends2, messengerFollowEnabled());
    }

    public static FriendTargetList friendRemoveTargetsFromPayload(String packetPayload, String callerUserId) {
        String requestPayload = Vb.cStr(packetPayload);
        if (requestPayload.startsWith("@h")) {
            requestPayload = requestPayload.substring(2);
        }
        FriendTargetList result = new FriendTargetList();
        LongRef offset = new LongRef(1);
        long removeCount = readWireLong(requestPayload, offset);
        if (removeCount <= 0L) {
            return result;
        }
        if (removeCount > 75L) {
            removeCount = 75L;
        }
        StringBuilder targetList = new StringBuilder();
        for (long removeIndex = 1L; removeIndex <= removeCount; removeIndex++) {
            long targetUserId = readWireLong(requestPayload, offset);
            String token = String.valueOf(targetUserId);
            if (targetUserId > 0L && !token.equals(Vb.cStr(callerUserId)) && !("," + targetList + ",").contains("," + token + ",")) {
                if (targetList.length() > 0) {
                    targetList.append(',');
                }
                targetList.append(token);
                result.targetCount++;
            }
        }
        result.targetList = targetList.toString();
        return result;
    }

    public static String petRaceListPayload(String productPet, String rowText, long rankIndex, long hcLevel) {
        return PetPayloads.raceList(productPet, rowText, rankIndex, hcLevel);
    }

    public static String petInventoryListPayload(String rowText) {
        return PetPayloads.inventoryList(rowText);
    }

    public static String petInventoryRowPayload(String[] fields) {
        return PetPayloads.inventoryRow(fields);
    }

    public static long petNameValidationCode(String candidateName) {
        return PetPayloads.nameValidationCode(candidateName);
    }

    public static String petNameValidationPayload(String candidateName) {
        return PetPayloads.nameValidation(candidateName);
    }

    public static String petCommandListPayload(long petLevel, Object commandRows) {
        return PetPayloads.commandList(petLevel, commandRows);
    }

    public static PetCommandAction petCommandAction(long commandId, Object commandRows) {
        PetCommandAction result = new PetCommandAction();
        if (commandId <= 0L) {
            return result;
        }
        for (String row : normalizeRows(commandRows)) {
            if (!row.isEmpty()) {
                String[] fields = row.split("\t", -1);
                if (Vb.val(handlingField(fields, 0)) == commandId) {
                    result.requiredLevel = Vb.val(handlingField(fields, 1));
                    result.action = handlingField(fields, 3);
                    result.found = true;
                    return result;
                }
            }
        }
        String rowText = MySQL.Proc_5_2_6D4690("SELECT petlevel_required,command_action FROM bots_petcommands WHERE id_command='"
            + commandId + "' LIMIT 1", 0, 0);
        if (!rowText.isEmpty()) {
            String[] fields = rowText.split("\t", -1);
            result.requiredLevel = Vb.val(handlingField(fields, 0));
            result.action = handlingField(fields, 1);
            result.found = true;
        }
        return result;
    }

    public static String representedPetStatusPayload(long botEntityId, String[] petFields) {
        return PetPayloads.status(botEntityId, petFields);
    }

    public static PetExperienceUpdate petExperienceUpdate(
        long botEntityId,
        String petName,
        String petFigure,
        long petLevel,
        long petExperience,
        long petEnergy,
        long petNutrition,
        long petScratches,
        long experienceDelta,
        Object levelRows
    ) {
        PetExperienceUpdate result = new PetExperienceUpdate();
        long nextExperience = petExperience + experienceDelta;
        if (nextExperience < 0L) {
            nextExperience = 0L;
        }
        long nextLevel = petLevel;
        long maxExperience = petLevelMaxExperience(petLevel, levelRows);
        if (maxExperience > 0L && nextExperience >= maxExperience && petLevelMaxExperience(petLevel + 1L, levelRows) > 0L) {
            nextLevel = petLevel + 1L;
            nextExperience = 0L;
            result.leveledUp = true;
        }
        result.petLevel = nextLevel;
        result.petExperience = nextExperience;
        result.statusPayload = petExperienceStatusPayload(botEntityId, petName, petFigure, nextLevel, nextExperience, petEnergy, petNutrition, petScratches);
        result.experiencePayload = PetPayloads.experience(botEntityId, experienceDelta, nextExperience);
        return result;
    }

    public static long petLevelMaxExperience(long petLevel, Object levelRows) {
        for (String row : normalizeRows(levelRows)) {
            if (!row.isEmpty()) {
                String[] fields = row.split("\t", -1);
                if (Vb.val(handlingField(fields, 0)) == petLevel) {
                    return Vb.val(handlingField(fields, 1));
                }
            }
        }
        return Vb.val(MySQL.Proc_5_2_6D4690("SELECT max_exp FROM bots_petlevels WHERE id_level='" + petLevel + "' LIMIT 1", 0, 0));
    }

    public static String petExperienceStatusPayload(
        long botEntityId,
        String petName,
        String petFigure,
        long petLevel,
        long petExperience,
        long petEnergy,
        long petNutrition,
        long petScratches
    ) {
        return PetPayloads.experienceStatus(botEntityId, petName, petFigure, petLevel, petExperience, petEnergy, petNutrition, petScratches);
    }

    public static String petScratchPayload(long botEntityId, long userId, long scratches, String petName, String petFigure) {
        return PetPayloads.scratch(botEntityId, userId, scratches, petName, petFigure);
    }

    public static String petCommandActionPayload(long botEntityId, String commandAction, long commandId) {
        return PetPayloads.commandAction(botEntityId, commandAction, commandId);
    }

    public static long reserveRepresentedBotSlot() {
        for (long slotIndex = 1L; slotIndex <= 5000L; slotIndex++) {
            String marker = "[" + slotIndex + "]";
            if (!Licence.global_008292D4.contains(marker)) {
                Licence.global_008292D4 += marker;
                return slotIndex;
            }
        }
        return 0L;
    }

    public static String representedBotField(String[] botFields, long fieldIndex) {
        return botFields != null && fieldIndex >= 0 && fieldIndex < botFields.length ? Vb.cStr(botFields[(int) fieldIndex]) : "";
    }

    public static long allocateRepresentedBot(long roomSlot, String[] botFields) {
        if (roomSlot <= 0L) {
            return 0L;
        }
        long botEntityId = reserveRepresentedBotSlot();
        if (botEntityId <= 0L) {
            return 0L;
        }
        storeRepresentedBotRecord(botEntityId, representedBotRecordFromFields(roomSlot, botFields));
        return botEntityId;
    }

    public static String representedBotRecordFromFields(long roomSlot, String[] botFields) {
        long botId = Vb.val(representedBotField(botFields, 0));
        String botName = representedBotField(botFields, 1);
        String botMotto = representedBotField(botFields, 2);
        String botSpeech = representedBotField(botFields, 3);
        String botResponses = representedBotField(botFields, 4);
        long positionX = Vb.val(representedBotField(botFields, 5));
        long positionY = Vb.val(representedBotField(botFields, 6));
        String positionZ = representedBotField(botFields, 7);
        long positionR = Vb.val(representedBotField(botFields, 8));
        String botFigure = representedBotField(botFields, 9);
        long handleId = Vb.val(representedBotField(botFields, 11));
        long handleActionId = Vb.val(representedBotField(botFields, 12));
        String cacheAction = representedBotField(botFields, 13);
        String speechSubmit = representedBotField(botFields, 14);
        long allowWalk = Vb.val(representedBotField(botFields, 15));
        long maxFieldsAway = Vb.val(representedBotField(botFields, 16));

        return roomSlot + "\2" + botId + "\2"
            + botName + '\2' + botMotto + '\2'
            + botSpeech + '\2' + botResponses + '\2'
            + positionX + "\2" + positionY + "\2"
            + positionZ + '\2' + positionR + '\2'
            + botFigure + '\2' + handleId + '\2'
            + handleActionId + '\2' + cacheAction + '\2'
            + speechSubmit + '\2' + allowWalk + '\2'
            + maxFieldsAway;
    }

    public static void storeRepresentedBotRecord(long botEntityId, String recordText) {
        if (botEntityId <= 0L) {
            return;
        }
        String startMarker = "[" + botEntityId + ":";
        int startAt = Licence.global_00829358.indexOf(startMarker);
        if (startAt >= 0) {
            int endAt = Licence.global_00829358.indexOf(']', startAt + startMarker.length());
            if (endAt >= 0) {
                Licence.global_00829358 = Licence.global_00829358.substring(0, startAt)
                    + Licence.global_00829358.substring(endAt + 1);
            }
        }
        Licence.global_00829358 += startMarker + Vb.cStr(recordText) + "]";
    }

    public static void removeRepresentedBotRecord(long botEntityId) {
        if (botEntityId <= 0L) {
            return;
        }
        String startMarker = "[" + botEntityId + ":";
        int startAt = Licence.global_00829358.indexOf(startMarker);
        if (startAt >= 0) {
            int endAt = Licence.global_00829358.indexOf(']', startAt + startMarker.length());
            if (endAt >= 0) {
                Licence.global_00829358 = Licence.global_00829358.substring(0, startAt)
                    + Licence.global_00829358.substring(endAt + 1);
            }
        }
        Licence.global_008292D4 = Licence.global_008292D4.replace("[" + botEntityId + "]", "");
    }

    public static String representedBotRecordText(long botEntityId) {
        if (botEntityId <= 0L || Licence.global_00829358.isEmpty()) {
            return "";
        }
        String startMarker = "[" + botEntityId + ":";
        int startAt = Licence.global_00829358.indexOf(startMarker);
        if (startAt < 0) {
            return "";
        }
        startAt += startMarker.length();
        int endAt = Licence.global_00829358.indexOf(']', startAt);
        if (endAt <= startAt) {
            return "";
        }
        return Licence.global_00829358.substring(startAt, endAt);
    }

    public static String representedBotRecordField(long botEntityId, long fieldIndex) {
        String[] fields = representedBotRecordText(botEntityId).split("\2", -1);
        return fieldIndex >= 0 && fieldIndex < fields.length ? fields[(int) fieldIndex] : "";
    }

    public static long representedBotRecordLong(long botEntityId, long fieldIndex) {
        return Vb.val(representedBotRecordField(botEntityId, fieldIndex));
    }

    public static long representedBotEntityFromBotId(long botId) {
        if (botId <= 0L || Licence.global_00829358.isEmpty()) {
            return 0L;
        }
        for (String record : Licence.global_00829358.split("\\[", -1)) {
            int payloadAt = record.indexOf(':');
            int endAt = record.indexOf(']');
            if (payloadAt > 0 && endAt > payloadAt) {
                long entityId = Vb.val(record.substring(0, payloadAt));
                String[] fields = record.substring(payloadAt + 1, endAt).split("\2", -1);
                if (fields.length >= 2 && Vb.val(fields[1]) == botId) {
                    return entityId;
                }
            }
        }
        return 0L;
    }

    public static String representedBotEntitiesForRoom(long roomSlot, long onlyBotId) {
        if (roomSlot <= 0L || Licence.global_00829358.isEmpty()) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        for (String record : Licence.global_00829358.split("\\[", -1)) {
            int payloadAt = record.indexOf(':');
            int endAt = record.indexOf(']');
            if (payloadAt > 0 && endAt > payloadAt) {
                long entityId = Vb.val(record.substring(0, payloadAt));
                String[] fields = record.substring(payloadAt + 1, endAt).split("\2", -1);
                if (fields.length >= 2 && Vb.val(fields[0]) == roomSlot && (onlyBotId <= 0L || Vb.val(fields[1]) == onlyBotId)) {
                    if (result.length() > 0) {
                        result.append('\r');
                    }
                    result.append(entityId);
                }
            }
        }
        return result.toString();
    }

    public static boolean isRepresentedBotAllocated(long roomSlot, long botId) {
        return !representedBotEntitiesForRoom(roomSlot, botId).isEmpty();
    }

    public static void storeRepresentedBotPosition(long botEntityId, long positionX, long positionY, String positionZ, long positionR) {
        String[] fields = representedBotRecordText(botEntityId).split("\2", -1);
        if (fields.length < 10) {
            return;
        }
        fields[6] = String.valueOf(positionX);
        fields[7] = String.valueOf(positionY);
        fields[8] = Vb.cStr(positionZ);
        fields[9] = String.valueOf(positionR);
        storeRepresentedBotRecord(botEntityId, String.join("\2", fields));
    }

    public static String representedBotRoomEntryPayload(long botEntityId) {
        long botId = representedBotRecordLong(botEntityId, 1);
        if (botEntityId <= 0L || botId <= 0L) {
            return "";
        }
        String botName = representedBotRecordField(botEntityId, 2);
        long positionX = representedBotRecordLong(botEntityId, 6);
        long positionY = representedBotRecordLong(botEntityId, 7);
        String positionZ = representedBotRecordField(botEntityId, 8);
        long positionR = representedBotRecordLong(botEntityId, 9);
        String botFigure = representedBotRecordField(botEntityId, 10);
        return "@\\" + Crypto.Proc_3_0_6D2AF0(botEntityId, null, "")
            + botName + '\2'
            + positionX + " " + positionY + " " + positionZ + '\2'
            + positionR + "\2" + botFigure + '\2';
    }

    public static String representedRoomUserProfilePayload(
        long roomUserIndex,
        String userName,
        String mottoText,
        long achievementScore,
        String figureText
    ) {
        return SocialPayloads.roomUserProfile(roomUserIndex, userName, mottoText, achievementScore, figureText);
    }

    public static String badgeInventoryPayload(String inventoryRows, String equippedPayload) {
        return SocialPayloads.badgeInventory(inventoryRows, equippedPayload);
    }

    public static String equippedBadgePayload(String badgeRows) {
        return SocialPayloads.equippedBadges(badgeRows);
    }

    public static String badgeDisplayPayload(long userId, String equippedPayload) {
        return SocialPayloads.badgeDisplay(userId, equippedPayload);
    }

    public static String tagListPayload(String tagRows) {
        return SocialPayloads.tags(tagRows);
    }

    public static String tagDisplayPayload(long userId, String tagPayload) {
        return SocialPayloads.tagDisplay(userId, tagPayload);
    }

    public static String representedRoomUserStatusPayload(long roomUserIndex, long statusCode) {
        if (roomUserIndex <= 0L) {
            return "";
        }
        return "0" + Crypto.Proc_3_0_6D2AF0(Math.max(0L, statusCode), null,
            Crypto.Proc_3_0_6D2AF0(roomUserIndex, null, "Ge"));
    }

    public static long pollIdFromWire(String packetPayload, String prefix) {
        return idRequestFromWire(packetPayload, prefix);
    }

    public static PollAnswerSubmission pollAnswerFromWire(String packetPayload, String prefix) {
        PollAnswerSubmission submission = new PollAnswerSubmission();
        String requestPayload = Vb.cStr(packetPayload);
        if (!Vb.cStr(prefix).isEmpty() && requestPayload.startsWith(prefix)) {
            requestPayload = requestPayload.substring(prefix.length());
        }
        LongRef offset = new LongRef(1);
        submission.pollId = readWireLong(requestPayload, offset);
        submission.questionId = readWireLong(requestPayload, offset);
        submission.answerValue = readWireLong(requestPayload, offset);
        submission.answerText = Functions.Proc_10_10_80A7F0(readWireString(requestPayload, offset), 0, 0);
        if (submission.pollId <= 0L) {
            submission.pollId = Vb.val(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
        }
        if (submission.answerText.isEmpty() && submission.answerValue > 0L) {
            submission.answerText = String.valueOf(submission.answerValue);
        }
        submission.valid = submission.pollId > 0L && submission.questionId > 0L;
        return submission;
    }

    public static String pollPayloadFromRows(String pollRow, String questionRows, Map<Long, String> answerRowsByQuestionId) {
        return PollPayloads.poll(pollRow, questionRows, answerRowsByQuestionId);
    }

    public static String achievementRowsFromGlobal() {
        return Licence.achievementSettings().rowsAsText();
    }

    public static String achievementRowByIndex(long achievementIndex) {
        return Licence.achievementSettings().rowByIndex(achievementIndex);
    }

    public static Map<String, Long> achievementCurrentLevels(String userId, String achievementRows) {
        Map<String, Long> result = new HashMap<>();
        String escapedUserId = Functions.Proc_10_11_80A9C0(userId, 0, 0);
        for (String row : Vb.cStr(achievementRows).split("\r", -1)) {
            if (!row.isEmpty()) {
                String[] fields = row.split("\t", -1);
                String badgePrefix = handlingField(fields, 1);
                if (!badgePrefix.isEmpty() && !result.containsKey(badgePrefix)) {
                    String escapedBadgePrefix = Functions.Proc_10_11_80A9C0(badgePrefix, 0, 0);
                    long currentLevel = Vb.val(MySQL.Proc_5_2_6D4690("SELECT REPLACE(id_badge,'"
                        + escapedBadgePrefix + "','') FROM users_badges WHERE id_user='" + escapedUserId
                        + "' AND id_badge LIKE '" + escapedBadgePrefix + "%' ORDER BY id DESC LIMIT 1", 0, 0));
                    result.put(badgePrefix, Math.max(0L, currentLevel));
                }
            }
        }
        return result;
    }

    public static long representedAchievementProgress(String userId, long achievementQuestId) {
        String escapedUserId = Functions.Proc_10_11_80A9C0(userId, 0, 0);
        long progress;
        if (achievementQuestId == 1L) {
            progress = Vb.val(MySQL.Proc_5_2_6D4690("SELECT COUNT(DISTINCT id_room) FROM logs_visitedrooms WHERE id_user='"
                + escapedUserId + "'", 0, 0));
        } else if (achievementQuestId == 2L) {
            progress = Vb.val(MySQL.Proc_5_2_6D4690("SELECT respect_received FROM users WHERE id='"
                + escapedUserId + "' LIMIT 1", 0, 0));
        } else if (achievementQuestId == 3L) {
            progress = Vb.val(MySQL.Proc_5_2_6D4690("SELECT respect_given FROM users WHERE id='"
                + escapedUserId + "' LIMIT 1", 0, 0));
        } else if (achievementQuestId == 4L) {
            progress = Vb.val(MySQL.Proc_5_2_6D4690("SELECT online_time FROM users WHERE id='"
                + escapedUserId + "' LIMIT 1", 0, 0)) / 60L;
        } else if (achievementQuestId == 6L) {
            progress = Vb.val(MySQL.Proc_5_2_6D4690("SELECT gifts_given FROM users WHERE id='"
                + escapedUserId + "' LIMIT 1", 0, 0));
        } else if (achievementQuestId == 7L) {
            progress = Vb.val(MySQL.Proc_5_2_6D4690("SELECT gifts_received FROM users WHERE id='"
                + escapedUserId + "' LIMIT 1", 0, 0));
        } else if (achievementQuestId == 8L) {
            progress = Vb.val(MySQL.Proc_5_2_6D4690("SELECT hc_periods FROM users WHERE id='"
                + escapedUserId + "' LIMIT 1", 0, 0));
        } else if (achievementQuestId == 9L) {
            progress = Vb.val(MySQL.Proc_5_2_6D4690("SELECT hc2_periods FROM users WHERE id='"
                + escapedUserId + "' LIMIT 1", 0, 0));
        } else if (achievementQuestId == 11L) {
            progress = Vb.val(MySQL.Proc_5_2_6D4690("SELECT amount_staffpicked FROM users WHERE id='"
                + escapedUserId + "' LIMIT 1", 0, 0));
        } else {
            String rowText = MySQL.Proc_5_2_6D4690("SELECT respect_received,respect_given,gifts_given,gifts_received FROM users WHERE id='"
                + escapedUserId + "' LIMIT 1", 0, 0);
            progress = Vb.val(handlingField(rowText.split("\t", -1), 0));
        }
        return Math.max(0L, progress);
    }

    public static String achievementRewardPayload(long achievementIndex, String achievementRow, long badgeLevel, long badgeRowId) {
        return AchievementPayloads.reward(achievementIndex, achievementRow, badgeLevel, badgeRowId);
    }

    public static String achievementAwardPayload(String achievementRow) {
        return AchievementPayloads.award(achievementRow);
    }

    public static AchievementProgressDecision achievementProgressDecision(
        String achievementRows,
        long achievementQuestId,
        Map<String, Long> currentLevelsByBadgePrefix,
        long currentProgress
    ) {
        AchievementProgressDecision decision = new AchievementProgressDecision();
        long achievementIndex = 0L;
        for (String row : Vb.cStr(achievementRows).split("\r", -1)) {
            if (!row.isEmpty()) {
                String[] fields = row.split("\t", -1);
                if (fields.length >= 7) {
                    long achievementId = Vb.val(fields[0]);
                    if (achievementId == achievementQuestId) {
                        String badgePrefix = fields[1];
                        long progressStep = Vb.val(fields[2]);
                        long levelTotal = Vb.val(fields[4]);
                        if (levelTotal <= 0L) {
                            levelTotal = 1L;
                        }
                        long currentLevel = currentLevelsByBadgePrefix != null && currentLevelsByBadgePrefix.containsKey(badgePrefix)
                            ? currentLevelsByBadgePrefix.get(badgePrefix) : 0L;
                        if (!badgePrefix.isEmpty() && progressStep > 0L && currentLevel >= 0L && currentLevel < levelTotal) {
                            decision.achievementIndex = achievementIndex;
                            decision.nextLevel = currentLevel + 1L;
                            decision.requiredProgress = progressStep * decision.nextLevel;
                            decision.shouldReward = currentProgress >= decision.requiredProgress;
                        }
                        return decision;
                    }
                }
                achievementIndex++;
            }
        }
        return decision;
    }

    public static String achievementListPayload(String achievementRows, Map<String, Long> currentLevelsByBadgePrefix) {
        return AchievementPayloads.list(achievementRows, currentLevelsByBadgePrefix);
    }

    public static String wiredSpecialStatePayload(long itemState) {
        return WiredPayloads.specialState(itemState);
    }

    public static String handlingRepresentedWiredEdit(
        Object[] args,
        String packetCode,
        long minimumCode,
        long maximumCode,
        String cacheFolder,
        boolean includeExtraValue
    ) {
        try {
            int socketIndex = handlingSocketIndex(args);
            String packetPayload = handlingPacketPayload(args);
            long furnitureId = firstWireLong(packetPayload, packetCode);
            if (socketIndex <= 0 || furnitureId <= 0L) {
                return "";
            }
            String userId = handlingUserIdFromSocket(socketIndex);
            if (userId.isEmpty() || "0".equals(userId)) {
                return "";
            }
            long roomId = handlingCurrentRoomId(socketIndex, userId);
            if (roomId <= 0L || (!handlingUserHasRoomRight(userId, roomId) && !handlingUserOwnsRoom(userId, roomId))) {
                return "";
            }
            String rowText = MySQL.Proc_5_2_6D4690("SELECT id_product FROM furnitures WHERE id='" + furnitureId
                + "' AND id_room='" + roomId + "' LIMIT 1", 0, 0);
            long productId = Vb.val(handlingField(rowText.split("\t", -1), 0));
            long wiredCode = Vb.val(DataManager.Proc_8_12_806C30(productId, 27, 0));
            if (wiredCode < minimumCode || wiredCode > maximumCode) {
                return "";
            }
            String recordText = wiredEditRecordFromWire(packetPayload, packetCode, wiredCode, includeExtraValue);
            if (recordText.isEmpty()) {
                return "";
            }
            String selectedIds = wiredRecordField(recordText, 2);
            if (!selectedIds.isEmpty() && !handlingRepresentedWiredSelectedItemsExist(roomId, selectedIds)) {
                return "";
            }
            String cachePath = wiredCachePath(cacheFolder, roomId);
            String cacheText = Proc_6_239_7FC170(cachePath, 0, 0);
            Proc_6_240_7FC2B0(cachePath, wiredCacheWithRecord(cacheText, recordText));
            return recordText;
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    public static long handlingRepresentedWiredTriggerCall(Object[] args, long triggerCode) {
        try {
            int socketIndex = handlingSocketIndex(args);
            long roomId = 0L;
            if (socketIndex > 0) {
                String userId = handlingUserIdFromSocket(socketIndex);
                if (!userId.isEmpty() && !"0".equals(userId)) {
                    roomId = handlingCurrentRoomId(socketIndex, userId);
                }
            }
            if (roomId <= 0L && args != null && args.length >= 2) {
                roomId = Vb.val(args[1]);
            }
            return handlingRepresentedWiredTrigger(roomId, triggerCode, socketIndex);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return 0L;
        }
    }

    public static long handlingRepresentedWiredActionCall(Object[] args, long actionCode) {
        try {
            int socketIndex = handlingSocketIndex(args);
            long roomId = 0L;
            if (socketIndex > 0) {
                String userId = handlingUserIdFromSocket(socketIndex);
                if (!userId.isEmpty() && !"0".equals(userId)) {
                    roomId = handlingCurrentRoomId(socketIndex, userId);
                }
            }
            if (roomId <= 0L && args != null && args.length >= 2) {
                roomId = Vb.val(args[1]);
            }
            long selectedFurnitureId = args != null && args.length >= 3 ? Vb.val(args[2]) : 0L;
            return handlingRepresentedWiredAction(roomId, actionCode, selectedFurnitureId, socketIndex);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return 0L;
        }
    }

    public static long handlingRepresentedWiredTrigger(long roomId, long triggerCode, int socketIndex) {
        if (roomId <= 0L) {
            return 0L;
        }
        long executedCount = 0L;
        for (String row : readWiredCache("wired_trigger", roomId).replace("\r", "").split("\n", -1)) {
            String recordText = row.trim();
            if (!recordText.isEmpty()) {
                long recordCode = Vb.val(wiredRecordField(recordText, 0));
                if ((triggerCode <= 0L || recordCode == triggerCode) && handlingRepresentedWiredConditionsPass(roomId)) {
                    executedCount += handlingRepresentedWiredAction(roomId, 0L, 0L, socketIndex);
                }
            }
        }
        return executedCount;
    }

    public static boolean handlingRepresentedWiredConditionsPass(long roomId) {
        if (roomId <= 0L) {
            return false;
        }
        for (String row : readWiredCache("wired_condition", roomId).replace("\r", "").split("\n", -1)) {
            String recordText = row.trim();
            if (!recordText.isEmpty()) {
                String selectedIds = wiredRecordField(recordText, 2);
                if (!selectedIds.isEmpty() && !handlingRepresentedWiredSelectedItemsExist(roomId, selectedIds)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static long handlingRepresentedWiredAction(long roomId, long actionCode, long selectedFurnitureId, int socketIndex) {
        if (roomId <= 0L) {
            return 0L;
        }
        long actionCount = 0L;
        for (String row : readWiredCache("wired_action", roomId).replace("\r", "").split("\n", -1)) {
            String recordText = row.trim();
            if (!recordText.isEmpty()) {
                long recordCode = Vb.val(wiredRecordField(recordText, 0));
                if (actionCode <= 0L || recordCode == actionCode) {
                    actionCount += handlingRepresentedWiredApplySelected(
                        roomId, wiredRecordField(recordText, 2), wiredRecordField(recordText, 3), selectedFurnitureId);
                }
            }
        }
        return actionCount;
    }

    public static long handlingRepresentedWiredApplySelected(long roomId, String selectedIds, String parameterText, long selectedFurnitureId) {
        if (roomId <= 0L) {
            return 0L;
        }
        String effectiveSelectedIds = selectedFurnitureId > 0L ? String.valueOf(selectedFurnitureId) : Vb.cStr(selectedIds);
        if (effectiveSelectedIds.isEmpty()) {
            return 0L;
        }
        long stateValue = Vb.val((Vb.cStr(parameterText) + ";").split(";", -1)[0]);
        long appliedCount = 0L;
        for (String idPart : effectiveSelectedIds.replace(',', ';').split(";", -1)) {
            long furnitureId = Vb.val(idPart);
            if (furnitureId > 0L && handlingFurnitureExistsInRoom(roomId, furnitureId)) {
                MySQL.Proc_5_0_6D3CD0("UPDATE furnitures SET sign='" + stateValue + "' WHERE id='" + furnitureId + "' LIMIT 1", 0, 0);
                Proc_6_151_78AC20(roomId, furnitureId, stateValue);
                Proc_6_246_8024C0(roomId, furnitureStatePayload(furnitureId, stateValue), 0);
                appliedCount++;
            }
        }
        return appliedCount;
    }

    public static boolean handlingRepresentedWiredSelectedItemsExist(long roomId, String selectedIds) {
        if (roomId <= 0L) {
            return false;
        }
        for (String idPart : Vb.cStr(selectedIds).replace(',', ';').split(";", -1)) {
            long furnitureId = Vb.val(idPart);
            if (furnitureId > 0L && !handlingFurnitureExistsInRoom(roomId, furnitureId)) {
                return false;
            }
        }
        return true;
    }

    public static long firstWireLong(String packetPayload, String packetCode) {
        String requestPayload = Vb.cStr(packetPayload);
        if (!Vb.cStr(packetCode).isEmpty() && requestPayload.startsWith(packetCode)) {
            requestPayload = requestPayload.substring(Vb.cStr(packetCode).length());
        }
        LongRef offset = new LongRef(1);
        long value = readWireLong(requestPayload, offset);
        return value > 0L ? value : Vb.val(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
    }

    public static String wiredCachePath(String cacheFolder, long roomId) {
        return Path.of(Functions.applicationPath, "cache", Vb.cStr(cacheFolder), roomId + ".cache").toString();
    }

    public static String readWiredCache(String cacheFolder, long roomId) {
        return roomId <= 0L ? "" : Proc_6_239_7FC170(wiredCachePath(cacheFolder, roomId), 0, 0);
    }

    public static boolean handlingFurnitureExistsInRoom(long roomId, long furnitureId) {
        if (roomId <= 0L || furnitureId <= 0L) {
            return false;
        }
        return Vb.val(MySQL.Proc_5_2_6D4690("SELECT COUNT(*) FROM furnitures WHERE id='" + furnitureId
            + "' AND id_room='" + roomId + "' LIMIT 1", 0, 0)) > 0L;
    }

    public static String wiredEditRecordFromWire(String packetPayload, String packetCode, long wiredCode, boolean includeExtraValue) {
        String requestPayload = Vb.cStr(packetPayload);
        if (!Vb.cStr(packetCode).isEmpty() && requestPayload.startsWith(packetCode)) {
            requestPayload = requestPayload.substring(packetCode.length());
        }
        LongRef offset = new LongRef(1);
        long furnitureId = readWireLong(requestPayload, offset);
        if (furnitureId <= 0L) {
            furnitureId = Vb.val(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
        }
        if (furnitureId <= 0L || wiredCode <= 0L) {
            return "";
        }
        long parameterCount = readWireLong(requestPayload, offset);
        if (parameterCount < 0L || parameterCount > 100L) {
            return "";
        }
        String parameterValues = "";
        for (long parameterIndex = 0L; parameterIndex < parameterCount; parameterIndex++) {
            long parameterValue = readWireLong(requestPayload, offset);
            if (!parameterValues.isEmpty()) {
                parameterValues += ";";
            }
            parameterValues += parameterValue;
        }
        String textValue = left(Functions.Proc_10_10_80A7F0(readWireString(requestPayload, offset), 0, 0), 125);
        if (textValue.isEmpty()) {
            textValue = left(Functions.Proc_10_10_80A7F0(Functions.Proc_10_7_80A190(requestPayload, 0, 0), 0, 0), 125);
        }
        long selectedCount = readWireLong(requestPayload, offset);
        if (selectedCount < 0L || selectedCount > 100L) {
            return "";
        }
        String selectedIdsText = "";
        for (long selectedIndex = 0L; selectedIndex < selectedCount; selectedIndex++) {
            long selectedFurnitureId = readWireLong(requestPayload, offset);
            if (selectedFurnitureId <= 0L) {
                return "";
            }
            if (!selectedIdsText.isEmpty()) {
                selectedIdsText += ";";
            }
            selectedIdsText += selectedFurnitureId;
        }
        long extraValue = includeExtraValue ? readWireLong(requestPayload, offset) : 0L;
        return wiredRecordText(wiredCode, furnitureId, selectedIdsText, parameterValues, textValue,
            includeExtraValue ? String.valueOf(extraValue) : "");
    }

    public static String wiredRecordText(
        long wiredCode,
        long furnitureId,
        String selectedIdsText,
        String parameterValues,
        String textValue,
        String extraValue
    ) {
        return WiredPayloads.recordText(wiredCode, furnitureId, selectedIdsText, parameterValues, textValue, extraValue);
    }

    public static String wiredRecordMarker(String recordText) {
        return WiredPayloads.recordMarker(recordText);
    }

    public static String wiredCacheWithRecord(String cacheText, String recordText) {
        return WiredPayloads.cacheWithRecord(cacheText, recordText);
    }

    public static String wiredRecordField(String recordText, long fieldIndex) {
        return WiredPayloads.recordField(recordText, fieldIndex);
    }

    public static boolean wiredSelectedItemsExist(String selectedIds, String existingIds) {
        return WiredPayloads.selectedItemsExist(selectedIds, existingIds);
    }

    public static WiredApplyResult wiredApplySelected(
        String selectedIds,
        String parameterText,
        long selectedFurnitureId,
        String existingIds
    ) {
        WiredPayloads.ApplyResult applied = WiredPayloads.applySelected(
            selectedIds,
            parameterText,
            selectedFurnitureId,
            existingIds,
            Handling::furnitureStatePayload);
        WiredApplyResult result = new WiredApplyResult();
        result.appliedCount = applied.appliedCount;
        result.statePayloads = applied.statePayloads;
        return result;
    }

    public static SongInfoRequest songInfoRequestFromWire(String packetPayload) {
        SongInfoRequest request = new SongInfoRequest();
        String requestPayload = Vb.cStr(packetPayload);
        if (requestPayload.startsWith("C]")) {
            requestPayload = requestPayload.substring(2);
        }
        LongRef offset = new LongRef(1);
        long requestedCount = readWireLong(requestPayload, offset);
        if (requestedCount <= 0L) {
            requestedCount = Vb.val(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
        }
        if (requestedCount > 60L) {
            requestedCount = 60L;
        }
        request.requestedCount = requestedCount;
        String requestedIds = "";
        for (long requestIndex = 0L; requestIndex < requestedCount; requestIndex++) {
            long cdId = readWireLong(requestPayload, offset);
            if (cdId > 0L) {
                if (!requestedIds.isEmpty()) {
                    requestedIds += ",";
                }
                requestedIds += cdId;
            }
        }
        request.requestedIds = requestedIds;
        return request;
    }

    public static String songInfoPayload(String cdRows) {
        return JukeboxPayloads.songInfo(cdRows);
    }

    public static String removeSoundMachineMarkers(String representedRoomCache, long jukeboxId, long activeDestinationId) {
        String cache = Vb.cStr(representedRoomCache);
        if (activeDestinationId > 0L) {
            cache = cache.replaceFirst(Pattern.quote("\1" + activeDestinationId + '\2'), "");
        }
        if (jukeboxId > 0L) {
            cache = cache.replaceFirst(Pattern.quote("\1" + jukeboxId + '\2'), "");
        }
        return cache;
    }

    public static JukeboxAddRequest jukeboxAddRequestFromWire(String packetPayload) {
        JukeboxAddRequest request = new JukeboxAddRequest();
        String requestPayload = Vb.cStr(packetPayload);
        if (requestPayload.startsWith("C" + '\177')) {
            requestPayload = requestPayload.substring(2);
        }
        LongRef offset = new LongRef(1);
        request.diskFurnitureId = readWireLong(requestPayload, offset);
        request.playlistOrder = readWireLong(requestPayload, offset);
        if (request.playlistOrder < 0L) {
            request.playlistOrder = 0L;
        }
        return request;
    }

    public static String[] jukeboxRow(long roomId) {
        if (roomId <= 0L) {
            return new String[0];
        }
        String rowText = MySQL.Proc_5_2_6D4690("SELECT furnitures.id,furnitures.id_product FROM furnitures,soundmachine_jb_playlist WHERE furnitures.id_room='"
            + roomId + "' AND soundmachine_jb_playlist.id_jukebox=furnitures.id GROUP BY furnitures.id ORDER BY furnitures.id DESC LIMIT 1", 0, 0);
        if (rowText.isEmpty()) {
            rowText = MySQL.Proc_5_2_6D4690("SELECT furnitures.id,furnitures.id_product FROM furnitures,products WHERE furnitures.id_room='"
                + roomId + "' AND furnitures.id_product=products.id AND (products.action LIKE '%soundmachine%' OR products.action LIKE '%jukebox%' "
                + "OR products.name LIKE '%jukebox%' OR products.sprite LIKE '%jukebox%') ORDER BY furnitures.id DESC LIMIT 1", 0, 0);
        }
        return rowText.isEmpty() ? new String[0] : rowText.split("\t", -1);
    }

    public static boolean jukeboxCanAddDisk(long playlistOrder, String maxOrderText, long playlistCount, long playlistLimit) {
        long effectiveLimit = playlistLimit <= 0L ? 100L : playlistLimit;
        if (playlistCount >= effectiveLimit) {
            return false;
        }
        String maxText = Vb.cStr(maxOrderText);
        long maxOrder = Vb.val(maxText);
        if (!maxText.isEmpty()) {
            return playlistOrder == maxOrder || playlistOrder == maxOrder + 1L;
        }
        return playlistOrder == 0L;
    }

    public static long jukeboxRemoveOrderFromWire(String packetPayload) {
        String requestPayload = Vb.cStr(packetPayload);
        if (requestPayload.startsWith("D@")) {
            requestPayload = requestPayload.substring(2);
        }
        LongRef offset = new LongRef(1);
        long playlistOrder = readWireLong(requestPayload, offset);
        return Math.max(0L, playlistOrder);
    }

    public static String jukeboxPlaylistPayload(long playlistLimit, String playlistRows) {
        return JukeboxPayloads.playlist(playlistLimit, playlistRows);
    }

    public static String songDiskInventoryPayload(String diskRows) {
        return JukeboxPayloads.diskInventory(diskRows);
    }

    public static String jukeboxPlaybackPayload(long startedAt, long sequenceId, long destinationId, long diskFurnitureId) {
        return JukeboxPayloads.playback(startedAt, sequenceId, destinationId, diskFurnitureId);
    }

    public static String[] badgeUpdateSelectionsFromWire(String packetPayload) {
        String requestPayload = Vb.cStr(packetPayload);
        if (requestPayload.startsWith("B^")) {
            requestPayload = requestPayload.substring(2);
        }
        String[] slots = new String[5];
        LongRef offset = new LongRef(1);
        for (int slotIndex = 0; slotIndex < slots.length; slotIndex++) {
            long hasBadge = readWireLong(requestPayload, offset);
            if (hasBadge == 1L) {
                slots[slotIndex] = Functions.Proc_10_11_80A9C0(readWireString(requestPayload, offset), 0, 0);
            } else {
                slots[slotIndex] = "";
            }
        }
        return slots;
    }

    public static long idRequestFromWire(String packetPayload, String prefix) {
        String requestPayload = Vb.cStr(packetPayload);
        if (!Vb.cStr(prefix).isEmpty() && requestPayload.startsWith(prefix)) {
            requestPayload = requestPayload.substring(prefix.length());
        }
        LongRef offset = new LongRef(1);
        long value = readWireLong(requestPayload, offset);
        if (value <= 0L) {
            value = Vb.val(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
        }
        return value;
    }

    public static String recyclerStatusPayload(long enabledValue, long remainingBlockTime) {
        return RecyclerPayloads.status(enabledValue, remainingBlockTime);
    }

    public static RecyclerSelection recyclerSelectionFromWire(String packetPayload) {
        RecyclerSelection selection = new RecyclerSelection();
        String requestPayload = Vb.cStr(packetPayload);
        if (requestPayload.startsWith("F^")) {
            requestPayload = requestPayload.substring(2);
        }
        LongRef offset = new LongRef(1);
        selection.requestedCount = readWireLong(requestPayload, offset);
        if (selection.requestedCount != 5L) {
            return selection;
        }
        String selectedItems = "";
        for (long itemIndex = 0L; itemIndex < selection.requestedCount; itemIndex++) {
            long furnitureId = readWireLong(requestPayload, offset);
            if (furnitureId <= 0L) {
                return selection;
            }
            String token = String.valueOf(furnitureId);
            if (("," + selectedItems + ",").contains("," + token + ",")) {
                return selection;
            }
            if (!selectedItems.isEmpty()) {
                selectedItems += ",";
            }
            selectedItems += token;
        }
        selection.selectedItems = selectedItems;
        selection.valid = true;
        return selection;
    }

    public static String recyclerSelectionWhereClause(String selectedItems, String escapedUserId) {
        String whereClause = "";
        for (String item : Vb.cStr(selectedItems).split(",", -1)) {
            long furnitureId = Vb.val(item);
            if (furnitureId <= 0L) {
                continue;
            }
            if (!whereClause.isEmpty()) {
                whereClause += " OR ";
            }
            whereClause += "furnitures.id_owner='" + escapedUserId + "' AND furnitures.id_room IS NULL"
                + " AND furnitures.id='" + furnitureId + "' AND products.id=furnitures.id_product"
                + " AND products.is_recycleable='1'";
        }
        return whereClause;
    }

    public static long representedRecyclerRewardProduct() {
        try {
            RecyclerSettings recyclerSettings = Licence.recyclerSettings();
            if (recyclerSettings.hasRewardGroups()) {
                for (RecyclerSettings.RewardGroup rewardGroup : recyclerSettings.rewardGroups()) {
                    long chance = rewardGroup.chance();
                    if (chance > 0L && Functions.Proc_10_4_809CA0(1, chance, 0) == 1L) {
                        long productId = representedRandomProductFromList(rewardGroup.productIds());
                        if (productId > 0L) {
                            return productId;
                        }
                    }
                }
                for (RecyclerSettings.RewardGroup rewardGroup : recyclerSettings.rewardGroups()) {
                    long productId = representedRandomProductFromList(rewardGroup.productIds());
                    if (productId > 0L) {
                        return productId;
                    }
                }
            }
            String rewardRows = MySQL.Proc_5_2_6D4690(
                "SELECT id_product FROM settings_recycler ORDER BY chance DESC LIMIT 100", 0, 0);
            String[] rows = Vb.cStr(rewardRows).split("\r", -1);
            if (rows.length > 0 && !Vb.cStr(rewardRows).isEmpty()) {
                int rowIndex = (int) Vb.val(Functions.Proc_10_4_809CA0(0, rows.length - 1L, 0));
                rowIndex = Math.max(0, Math.min(rowIndex, rows.length - 1));
                return Vb.val(rows[rowIndex]);
            }
            return 0L;
        } catch (Exception ignored) {
            // VB6 source suppresses helper failures.
            return 0L;
        }
    }

    public static long representedRandomProductFromList(String productList) {
        return representedRandomProductFromList(RecyclerSettings.productIds(productList));
    }

    public static long representedRandomProductFromList(List<Long> productIds) {
        try {
            if (productIds == null || productIds.isEmpty()) {
                return 0L;
            }
            int selectedIndex = (int) Vb.val(Functions.Proc_10_4_809CA0(0, productIds.size() - 1L, 0));
            selectedIndex = Math.max(0, Math.min(selectedIndex, productIds.size() - 1));
            return productIds.get(selectedIndex);
        } catch (Exception ignored) {
            // VB6 source suppresses helper failures.
            return 0L;
        }
    }

    public static String recyclerRewardSign() {
        return LocalDateTime.now().toString().replace('T', ' ');
    }

    public static String callForHelpRowPayload(String rowText, Map<Long, String> userNamesById) {
        return StaffPayloads.callForHelpRow(rowText, userNamesById);
    }

    public static String staffCallForHelpWhereClause(String packetPayload) {
        return StaffPayloads.callForHelpWhereClause(packetPayload);
    }

    public static String staffUserSummaryPayload(
        String rowText,
        long callForHelpCount,
        long pickedCallForHelpCount,
        long cautionCount,
        long banCount
    ) {
        return StaffPayloads.userSummary(rowText, callForHelpCount, pickedCallForHelpCount, cautionCount, banCount);
    }

    public static String staffRoomVisitPayload(String rowText) {
        return StaffPayloads.roomVisit(rowText);
    }

    public static long staffNestedUserIdFromWire(String packetPayload) {
        String requestPayload = Vb.cStr(packetPayload);
        long directValue = Vb.val(Functions.Proc_10_6_809F10(requestPayload, 0, 0));
        if (directValue > 0L) {
            return directValue;
        }
        LongRef offset = new LongRef(1);
        String nestedPayload = readWireString(requestPayload, offset);
        long nestedValue = Vb.val(Functions.Proc_10_6_809F10(nestedPayload, 0, 0));
        if (nestedValue <= 0L) {
            nestedValue = Vb.val(nestedPayload);
        }
        if (nestedValue > 0L) {
            return nestedValue;
        }
        offset.value = 1L;
        return readWireLong(requestPayload, offset);
    }

    public static StaffChatRowsPayload staffRoomChatRowsPayload(String chatRows) {
        StaffPayloads.ChatRows chatRowsPayload = StaffPayloads.roomChatRows(chatRows);
        StaffChatRowsPayload result = new StaffChatRowsPayload();
        result.chatCount = chatRowsPayload.chatCount;
        result.payload = chatRowsPayload.payload;
        return result;
    }

    public static String staffRoomChatHistoryPayload(String visitRowText, String chatRows) {
        return StaffPayloads.roomChatHistory(visitRowText, chatRows);
    }

    public static boolean containsUnsafeStaffAlert(String messageText) {
        return StaffPayloads.containsUnsafeAlert(messageText);
    }

    private static String[] normalizeRows(Object rowSource) {
        if (rowSource instanceof String[]) {
            return (String[]) rowSource;
        }
        return Vb.cStr(rowSource).split("\r", -1);
    }

    private static boolean readRoomEventCommon(String packetPayload, LongRef offset, RoomEventPayload result, boolean requireText) {
        result.eventName = Functions.Proc_10_10_80A7F0(readWireString(packetPayload, offset));
        if (requireText && result.eventName.length() < 3) {
            return false;
        }
        result.eventDescription = Functions.Proc_10_10_80A7F0(readWireString(packetPayload, offset));
        if (requireText && result.eventDescription.length() < 3) {
            return false;
        }
        long tagCount = readWireLong(packetPayload, offset);
        if (tagCount < 0L || tagCount > 2L) {
            return false;
        }
        for (long tagIndex = 1L; tagIndex <= tagCount; tagIndex++) {
            String tagText = left(Functions.Proc_10_10_80A7F0(readWireString(packetPayload, offset)), 30).toLowerCase();
            if (tagIndex == 1L) {
                result.tagOne = tagText;
            } else if (tagIndex == 2L) {
                result.tagTwo = tagText;
            }
        }
        return true;
    }

    private static long optionalWireLong(String packetPayload, LongRef offset, long defaultValue) {
        long previousOffset = offset.value;
        long value = readWireLong(packetPayload, offset);
        return offset.value <= previousOffset ? defaultValue : value;
    }

    private static boolean containsDelimitedId(String idText, long wantedId) {
        String wanted = String.valueOf(wantedId);
        for (String idPart : Vb.cStr(idText).replace(',', ';').split(";", -1)) {
            if (wanted.equals(String.valueOf(Vb.val(idPart)))) {
                return true;
            }
        }
        return false;
    }

    private static void appendRow(StringBuilder rows, String rowText) {
        if (rows.length() > 0) {
            rows.append('\r');
        }
        rows.append(rowText);
    }

    private static String[] ensureHandlingFieldCount(String[] fields, int lastIndex) {
        if (fields.length > lastIndex) {
            return fields;
        }
        String[] expanded = new String[lastIndex + 1];
        for (int index = 0; index < expanded.length; index++) {
            expanded[index] = index < fields.length ? fields[index] : "";
        }
        return expanded;
    }

    private static String joinTab(String[] fields) {
        return String.join("\t", fields);
    }

    private static String removeMovementRecord(String movementText, String markerText) {
        String result = Vb.cStr(movementText);
        String marker = Vb.cStr(markerText);
        int markerAt = result.indexOf(marker);
        while (markerAt >= 0) {
            int endAt = result.indexOf('\2', markerAt + marker.length());
            if (endAt < 0) {
                result = result.substring(0, markerAt);
            } else {
                result = result.substring(0, markerAt) + result.substring(endAt + 1);
            }
            markerAt = result.indexOf(marker);
        }
        return result;
    }

    private static String[] userQuestFields(String userQuestText, long questId) {
        if (questId <= 0L) {
            return new String[0];
        }
        String marker = "\r" + questId + '\t';
        String text = Vb.cStr(userQuestText);
        int start = text.indexOf(marker);
        if (start < 0) {
            return new String[0];
        }
        int rowStart = start + marker.length();
        int rowEnd = text.indexOf('\r', rowStart);
        if (rowEnd < 0) {
            rowEnd = text.length();
        }
        return (questId + "\t" + text.substring(rowStart, rowEnd)).split("\t", -1);
    }

    private static String questRowsFromSource() {
        String cachedRows = Vb.cStr(Licence.global_00829080);
        if (!cachedRows.isEmpty()) {
            return cachedRows;
        }
        return MySQL.Proc_5_2_6D4690(
            "SELECT id,level,name,NULL,reward,reward_type,require_action,id_additional,id_campaign,amount_activities,waitamount "
                + "FROM quests ORDER BY id_campaign DESC,level ASC", 0, 0);
    }

    private static String[] questFieldsById(String questRows, long questId) {
        if (questId <= 0L) {
            return new String[0];
        }
        for (String row : Vb.cStr(questRows).split("\r", -1)) {
            String rowText = row.trim();
            if (!rowText.isEmpty()) {
                String[] fields = rowText.split("\t", -1);
                if (fields.length >= 1 && Vb.val(handlingField(fields, 0)) == questId) {
                    return fields;
                }
            }
        }
        return new String[0];
    }

    private static String questRowsWithRemainingWait(String userQuestRows) {
        StringBuilder rows = new StringBuilder();
        for (String row : Vb.cStr(userQuestRows).split("\r", -1)) {
            String rowText = row.trim();
            if (!rowText.isEmpty()) {
                String[] fields = rowText.split("\t", -1);
                String timeNextText = handlingField(fields, 4);
                long remainingWait = 0L;
                if (!timeNextText.isEmpty() && !"0".equals(timeNextText)) {
                    remainingWait = Vb.val(MySQL.Proc_5_2_6D4690("SELECT GREATEST(0,UNIX_TIMESTAMP('"
                        + Functions.Proc_10_11_80A9C0(timeNextText, 0, 0)
                        + "')-UNIX_TIMESTAMP())", 0, 0));
                } else if (fields.length >= 7) {
                    remainingWait = Vb.val(handlingField(fields, 6));
                }
                appendRow(rows, rowText + '\t' + remainingWait);
            }
        }
        return rows.toString();
    }

    private static String left(String value, int maxLength) {
        String text = Vb.cStr(value);
        return text.length() <= maxLength ? text : text.substring(0, maxLength);
    }

    private static int firstPositiveIndex(String text, char... chars) {
        int result = -1;
        for (char ch : chars) {
            int at = text.indexOf(ch);
            if (at >= 0 && (result < 0 || at < result)) {
                result = at;
            }
        }
        return result;
    }

    private static String[] normalizeUserEntryArgs(Object... args) {
        String[] values = new String[11];
        for (int i = 0; i < values.length; i++) {
            values[i] = "";
        }
        if (args.length == 1) {
            String recordText = Vb.cStr(args[0]);
            if (recordText.indexOf('\t') >= 0) {
                String[] fields = recordText.split("\t", -1);
                for (int i = 0; i < values.length; i++) {
                    values[i] = handlingField(fields, i);
                }
            } else {
                values[0] = recordText;
                values[5] = recordText;
            }
        } else {
            for (int i = 0; i < values.length && i < args.length; i++) {
                values[i] = Vb.cStr(args[i]);
            }
        }
        return values;
    }

    private static String[] normalizeObjectEntryArgs(Object... args) {
        String[] values = new String[9];
        for (int i = 0; i < values.length; i++) {
            values[i] = "";
        }
        if (args.length == 1) {
            String recordText = Vb.cStr(args[0]);
            if (recordText.indexOf('\t') >= 0) {
                String[] fields = recordText.split("\t", -1);
                for (int i = 0; i < values.length; i++) {
                    values[i] = handlingField(fields, i);
                }
            } else {
                values[0] = recordText;
                values[4] = recordText;
            }
        } else {
            for (int i = 0; i < values.length && i < args.length; i++) {
                values[i] = Vb.cStr(args[i]);
            }
        }
        return values;
    }
}
