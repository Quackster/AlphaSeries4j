package com.alphaseries.game.social;

import java.util.ArrayList;
import java.util.List;

import com.alphaseries.dao.mysql.RoomDao;
import com.alphaseries.dao.mysql.UserDao;
import com.alphaseries.game.pet.PetLookups;
import com.alphaseries.game.room.RepresentedRoomCache;
import com.alphaseries.game.room.RoomOccupantRow;
import com.alphaseries.game.room.RoomUserEntryPayloadArgs;
import com.alphaseries.game.room.RoomUserEntryRow;
import com.alphaseries.game.room.RoomUserPosition;
import com.alphaseries.game.room.RoomUserProfileRow;
import com.alphaseries.game.room.RoomUserTargetRow;
import com.alphaseries.messages.outgoing.RoomPayloads;
import com.alphaseries.messages.outgoing.UserPayloads;
import com.alphaseries.messages.outgoing.SocialPayloads;
import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

public final class SocialLookups {
    private SocialLookups() {
    }

    public record RoomUserAction(long resultValue, String payload) {
        public RoomUserAction {
            payload = StringUtils.text(payload);
        }

        public static RoomUserAction empty() {
            return new RoomUserAction(0L, "");
        }

        public boolean valid() {
            return resultValue > 0L && !payload.isEmpty();
        }
    }

    public record FollowRoomAction(long roomId, String failurePayload) {
        public FollowRoomAction {
            failurePayload = StringUtils.text(failurePayload);
        }

        public static FollowRoomAction failure(String payload) {
            return new FollowRoomAction(0L, payload);
        }

        public boolean canEnterRoom() {
            return roomId > 0L;
        }

        public boolean hasFailurePayload() {
            return !failurePayload.isEmpty();
        }
    }

    public record DirectPayload(String payload) {
        public DirectPayload {
            payload = StringUtils.text(payload);
        }

        public static DirectPayload empty() {
            return new DirectPayload("");
        }

        public boolean hasPayload() {
            return !payload.isEmpty();
        }
    }

    public record BadgeUpdateResult(String equippedPayload, String displayPayload) {
        public BadgeUpdateResult {
            equippedPayload = StringUtils.text(equippedPayload);
            displayPayload = StringUtils.text(displayPayload);
        }

        public static BadgeUpdateResult empty() {
            return new BadgeUpdateResult("", "");
        }

        public boolean hasDisplayPayload() {
            return !displayPayload.isEmpty();
        }
    }

    public record RoomUserBadgeLook(String directPayload, RoomUserStatusPayloads statusPayloads) {
        public RoomUserBadgeLook {
            directPayload = StringUtils.text(directPayload);
            statusPayloads = statusPayloads == null ? new RoomUserStatusPayloads("", "") : statusPayloads;
        }

        public static RoomUserBadgeLook empty() {
            return new RoomUserBadgeLook("", new RoomUserStatusPayloads("", ""));
        }

        public boolean hasDirectPayload() {
            return !directPayload.isEmpty();
        }
    }

    /**
     * Original function: Proc_6_195_7D38D0.
     */
    public static String equippedBadgePayload(String userId, UserDao users) {
        return SocialPayloads.equippedBadges(equippedBadgeRows(userId, users));
    }

    private static List<BadgeRow> equippedBadgeRows(String userId, UserDao users) {
        try {
            String userIdText = StringUtils.text(userId);
            if (userIdText.isEmpty() || "0".equals(userIdText) || users == null) {
                return List.of();
            }
            return users.equippedBadges(NumberUtils.parseLong(userIdText));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return List.of();
        }
    }

    public static BadgeInventoryPayload badgeInventoryPayload(String userId, UserDao users) {
        try {
            String userIdText = StringUtils.text(userId);
            if (userIdText.isEmpty() || "0".equals(userIdText) || users == null) {
                return emptyBadgeInventoryPayload();
            }
            long numericUserId = NumberUtils.parseLong(userIdText);
            List<BadgeRow> equippedRows = equippedBadgeRows(userIdText, users);
            String equippedPayload = SocialPayloads.equippedBadges(equippedRows);
            return new BadgeInventoryPayload(
                SocialPayloads.badgeInventory(users.unequippedBadges(numericUserId), equippedRows),
                SocialPayloads.badgeDisplay(numericUserId, equippedRows),
                equippedPayload);
        } catch (Exception ignored) {
            return emptyBadgeInventoryPayload();
        }
    }

    /**
     * Original function: Proc_6_194_7D3180.
     */
    public static BadgeUpdateResult updateEquippedBadges(
        String userId,
        BadgeUpdateSelections selections,
        UserDao users
    ) {
        try {
            String userIdText = StringUtils.text(userId);
            long userIdValue = NumberUtils.parseLong(userIdText);
            if (userIdValue <= 0L || selections == null || users == null) {
                return BadgeUpdateResult.empty();
            }
            users.clearEquippedBadges(userIdValue);
            for (int slotIndex = 0; slotIndex < selections.size(); slotIndex++) {
                String badgeId = selections.slot(slotIndex);
                if (!badgeId.isEmpty()) {
                    users.equipBadge(userIdValue, badgeId, slotIndex + 1L);
                }
            }
            List<BadgeRow> equippedRows = equippedBadgeRows(userIdText, users);
            String equippedPayload = SocialPayloads.equippedBadges(equippedRows);
            return new BadgeUpdateResult(equippedPayload, SocialPayloads.badgeDisplay(userIdValue, equippedRows));
        } catch (Exception ignored) {
            return BadgeUpdateResult.empty();
        }
    }

    public static String badgeDisplayPayload(String userId, UserDao users) {
        return SocialPayloads.badgeDisplay(NumberUtils.parseLong(userId), equippedBadgeRows(userId, users));
    }

    /**
     * Original function: Proc_6_196_7D3ED0.
     */
    public static String tagPayload(String userId, UserDao users) {
        return SocialPayloads.tags(tagRows(userId, users));
    }

    private static List<UserDao.UserTagRow> tagRows(String userId, UserDao users) {
        try {
            String userIdText = StringUtils.text(userId);
            if (userIdText.isEmpty() || "0".equals(userIdText) || users == null) {
                return List.of();
            }
            return users.tagNames(NumberUtils.parseLong(userIdText));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return List.of();
        }
    }

    public static String tagDisplayPayload(String userId, UserDao users) {
        return SocialPayloads.tagDisplay(NumberUtils.parseLong(userId), tagRows(userId, users));
    }

    /**
     * Original function: Proc_6_191_7D18B0.
     */
    public static DirectPayload tagDisplayAction(
        String callerUserId,
        long callerRoomId,
        String packetPayload,
        UserDao users
    ) {
        try {
            String callerUserIdText = StringUtils.text(callerUserId);
            long requestedUserId = SocialWire.userIdRequest(packetPayload, "DG").userId();
            if (callerUserIdText.isEmpty() || "0".equals(callerUserIdText)
                || requestedUserId <= 0L || users == null) {
                return DirectPayload.empty();
            }
            long targetSocketIndex = users.socketByUserId(requestedUserId);
            if (targetSocketIndex <= 0L && callerRoomId <= 0L) {
                return DirectPayload.empty();
            }
            return new DirectPayload(tagDisplayPayload(String.valueOf(requestedUserId), users));
        } catch (Exception ignored) {
            return DirectPayload.empty();
        }
    }

    public static RoomUserStatusPayloads roomUserStatusPayloads(long callerRoomUserIndex, long targetRoomUserIndex) {
        if (callerRoomUserIndex <= 0L || callerRoomUserIndex == targetRoomUserIndex) {
            return new RoomUserStatusPayloads("", "");
        }
        return new RoomUserStatusPayloads(
            SocialPayloads.roomUserStatus(callerRoomUserIndex, 0L),
            SocialPayloads.roomUserStatus(targetRoomUserIndex, 0L));
    }

    /**
     * Original function: Proc_6_192_7D1B80.
     */
    public static RoomUserBadgeLook roomUserBadgeLookAction(
        long callerRoomUserIndex,
        RoomUserTargetRow target,
        UserDao users
    ) {
        try {
            if (callerRoomUserIndex <= 0L || target == null || users == null) {
                return RoomUserBadgeLook.empty();
            }
            long targetRoomUserIndex = target.roomUserIndex();
            long targetUserId = target.userId();
            if (targetRoomUserIndex <= 0L || targetUserId <= 0L) {
                return RoomUserBadgeLook.empty();
            }
            return new RoomUserBadgeLook(
                badgeDisplayPayload(String.valueOf(targetUserId), users),
                roomUserStatusPayloads(callerRoomUserIndex, targetRoomUserIndex));
        } catch (Exception ignored) {
            return RoomUserBadgeLook.empty();
        }
    }

    public static String roomUserWavePayload(long roomUserIndex) {
        return SocialPayloads.roomUserWave(roomUserIndex);
    }

    public static RoomUserAction roomUserWaveAction(String userId, long roomId, long roomUserIndex) {
        String userIdText = StringUtils.text(userId);
        if (userIdText.isEmpty() || "0".equals(userIdText) || roomId <= 0L || roomUserIndex <= 0L) {
            return RoomUserAction.empty();
        }
        return new RoomUserAction(roomUserIndex, roomUserWavePayload(roomUserIndex));
    }

    public static String roomUserDancePayload(long roomUserIndex, long danceId) {
        return SocialPayloads.roomUserDance(roomUserIndex, danceId);
    }

    public static RoomUserAction roomUserDanceAction(
        String userId,
        long roomId,
        long roomUserIndex,
        SocialWire.DanceRequest request
    ) {
        String userIdText = StringUtils.text(userId);
        if (userIdText.isEmpty() || "0".equals(userIdText) || roomId <= 0L || roomUserIndex <= 0L
            || request == null) {
            return RoomUserAction.empty();
        }
        long danceId = request.danceId();
        return new RoomUserAction(danceId, roomUserDancePayload(roomUserIndex, danceId));
    }

    public static String roomUserRemovedPayload(long roomUserIndex) {
        return SocialPayloads.roomUserRemoved(roomUserIndex);
    }

    /**
     * Original function: Proc_6_50_7166B0.
     */
    public static FollowRoomAction followRoomAction(SocialWire.FollowUserRequest request, UserDao users) {
        try {
            String targetName = request == null ? "" : request.targetName();
            if (targetName.isEmpty() || users == null) {
                return FollowRoomAction.failure("BC");
            }
            UserDao.ActiveUserLocation target = users.activeLocationByName(targetName).orElse(null);
            if (target == null || target.userId() <= 0L || target.socketIndex() <= 0L || target.roomId() <= 0L) {
                return FollowRoomAction.failure("BC");
            }
            return new FollowRoomAction(target.roomId(), "");
        } catch (Exception ignored) {
            return FollowRoomAction.failure("BC");
        }
    }

    /**
     * Original function: Proc_6_76_726CE0.
     */
    public static String giveRespectPayload(String giverUserId, String targetUserId, UserDao users) {
        try {
            long giverUserIdValue = NumberUtils.parseLong(giverUserId);
            long targetUserIdValue = NumberUtils.parseLong(targetUserId);
            if (giverUserIdValue <= 0L || targetUserIdValue <= 0L
                || giverUserIdValue == targetUserIdValue || users == null) {
                return "";
            }
            long respectAmount = users.respectAmount(giverUserIdValue);
            if (respectAmount <= 0L) {
                return "";
            }
            users.spendRespect(giverUserIdValue);
            users.receiveRespect(targetUserIdValue);
            long respectReceived = users.respectReceived(targetUserIdValue);
            return UserPayloads.respectReceived(targetUserIdValue, respectReceived);
        } catch (Exception ignored) {
            return "";
        }
    }

    public static String roomUserEntryPayload(RoomUserEntryRow row, long roomUserIndex) {
        if (row == null) {
            return "";
        }
        return SocialPayloads.roomUserEntry(new RoomUserEntryPayloadArgs(
            String.valueOf(row.userId()),
            row.name(),
            row.figure(),
            row.motto(),
            row.gender(),
            String.valueOf(roomUserIndex),
            String.valueOf(row.positionX()),
            String.valueOf(row.positionY()),
            "0.0",
            "0",
            "0"));
    }

    /**
     * Original function: Proc_6_80_72EB60.
     */
    public static String roomUserEntryBroadcastPayload(
        String userId,
        long roomId,
        long roomUserIndex,
        RoomDao rooms
    ) {
        try {
            long numericUserId = NumberUtils.parseLong(userId);
            if (numericUserId <= 0L || roomId <= 0L || roomUserIndex <= 0L || rooms == null) {
                return "";
            }
            RoomUserEntryRow entry = rooms.roomUserEntry(numericUserId, roomId).orElse(null);
            String entryPayload = roomUserEntryPayload(entry, roomUserIndex);
            if (entryPayload.isEmpty()) {
                return "";
            }
            return RoomPayloads.occupantEntries(new SocialRoomOccupants(1L, 0L, entryPayload, ""));
        } catch (Exception ignored) {
            return "";
        }
    }

    public static SocialRoomOccupants roomOccupantsPayloads(
        List<RoomOccupantRow> occupants,
        long roomSlot,
        RepresentedRoomCache representedRooms
    ) {
        PacketBuilder occupantPayload = PacketBuilder.create();
        PacketBuilder statusPayload = PacketBuilder.create();
        long occupantCount = 0L;
        long statusCount = 0L;
        if (occupants != null) {
            for (RoomOccupantRow occupant : occupants) {
                if (occupant != null) {
                    long roomUserIndex = occupant.roomUserIndex();
                    long positionX = occupant.positionX();
                    long positionY = occupant.positionY();
                    if (roomSlot > 0L && representedRooms != null) {
                        RoomUserPosition movementPosition = RoomUserPosition.from(
                            representedRooms.movementPosition(roomSlot, roomUserIndex));
                        if (movementPosition.found()) {
                            positionX = movementPosition.positionX();
                            positionY = movementPosition.positionY();
                        }
                    }
                    String positionZ = "0.0";
                    long directionValue = 0L;
                    occupantPayload.appendRaw(SocialPayloads.roomUserEntry(new RoomUserEntryPayloadArgs(
                        String.valueOf(occupant.userId()),
                        occupant.name(),
                        occupant.figure(),
                        occupant.motto(),
                        normalizedGender(occupant.gender()),
                        String.valueOf(roomUserIndex),
                        String.valueOf(positionX),
                        String.valueOf(positionY),
                        positionZ,
                        "0",
                        "0")));
                    statusPayload.appendRaw(SocialPayloads.roomOccupantStatus(
                        roomUserIndex, positionX, positionY, positionZ, directionValue));
                    occupantCount++;
                    statusCount++;
                }
            }
        }
        return new SocialRoomOccupants(
            occupantCount,
            statusCount,
            occupantPayload.build(),
            statusPayload.build());
    }

    /**
     * Original function: Proc_6_81_730010.
     */
    public static List<String> roomOccupantListPayloads(
        long roomId,
        RepresentedRoomCache representedRooms,
        RoomDao rooms
    ) {
        try {
            if (roomId <= 0L || rooms == null) {
                return List.of();
            }
            long roomSlot = rooms.roomSlot(roomId);
            SocialRoomOccupants occupants = roomOccupantsPayloads(
                rooms.activeRoomOccupants(roomId), roomSlot, representedRooms);
            if (roomSlot > 0L) {
                occupants = occupants.withPetOccupants(PetLookups.roomOccupants(roomSlot));
            }
            return List.of(
                RoomPayloads.occupantEntries(occupants),
                RoomPayloads.occupantStatuses(occupants));
        } catch (Exception ignored) {
            return List.of();
        }
    }

    public static String roomUserEffectPayload(long roomUserIndex, long effectId) {
        return SocialPayloads.roomUserEffect(roomUserIndex, effectId);
    }

    /**
     * Original function: Proc_6_82_731070.
     */
    public static List<String> activeRoomEffectPayloads(long roomId, RoomDao rooms) {
        try {
            if (roomId <= 0L || rooms == null) {
                return List.of();
            }
            List<String> payloads = new ArrayList<>();
            for (RoomDao.ActiveRoomEffect activeEffect : rooms.activeRoomEffects(roomId)) {
                long roomUserIndex = activeEffect.roomUserIndex();
                long effectId = activeEffect.effectId();
                if (roomUserIndex > 0L && effectId > 0L) {
                    payloads.add(roomUserEffectPayload(roomUserIndex, effectId));
                }
            }
            return payloads;
        } catch (Exception ignored) {
            return List.of();
        }
    }

    public static String roomUserEffectClearedPayload(long roomUserIndex) {
        return SocialPayloads.roomUserEffectCleared(roomUserIndex);
    }

    public static String roomUserPreReadyPayload(long roomUserIndex) {
        return SocialPayloads.roomUserPreReadyState(roomUserIndex);
    }

    public static InteractionStatePayloads interactionStatePayloads(
        long sourceRoomUserIndex,
        long interactionState
    ) {
        return new InteractionStatePayloads(
            SocialPayloads.interactionStateForSource(sourceRoomUserIndex, interactionState),
            SocialPayloads.interactionStateForTarget(sourceRoomUserIndex, interactionState));
    }

    public static String interactionRequestPayload(long sourceUserId, long targetUserId) {
        return SocialPayloads.interactionRequest(sourceUserId, targetUserId);
    }

    public static String interactionClosedPayload(long sourceRoomUserIndex) {
        return SocialPayloads.interactionClosed(sourceRoomUserIndex);
    }

    public static String roomUserProfilePayload(RoomUserProfileRow row) {
        if (row == null) {
            return "";
        }
        return SocialPayloads.roomUserProfile(
            row.roomUserIndex(),
            row.userName(),
            row.motto(),
            row.achievementScore(),
            row.figure());
    }

    /**
     * Original function: Proc_6_190_7D11D0.
     */
    public static DirectPayload roomUserProfileAction(long roomId, long requestedRoomUserIndex, RoomDao rooms) {
        try {
            if (roomId <= 0L || requestedRoomUserIndex <= 0L || rooms == null) {
                return DirectPayload.empty();
            }
            java.util.Optional<RoomUserProfileRow> row =
                rooms.activeRoomUserProfileByVisitId(roomId, requestedRoomUserIndex);
            if (row.isEmpty()) {
                row = rooms.activeRoomUserProfileByUserId(roomId, requestedRoomUserIndex);
            }
            return row.map(value -> new DirectPayload(roomUserProfilePayload(value))).orElseGet(DirectPayload::empty);
        } catch (Exception ignored) {
            return DirectPayload.empty();
        }
    }

    private static BadgeInventoryPayload emptyBadgeInventoryPayload() {
        String equippedPayload = SocialPayloads.equippedBadges(List.of());
        return new BadgeInventoryPayload(
            SocialPayloads.badgeInventory(List.of(), List.of()),
            SocialPayloads.badgeDisplay(0L, List.of()),
            equippedPayload);
    }

    private static String normalizedGender(String gender) {
        String genderText = StringUtils.text(gender).toUpperCase();
        genderText = genderText.isEmpty() ? "M" : genderText.substring(0, 1);
        if (!"M".equals(genderText) && !"F".equals(genderText)) {
            return "M";
        }
        return genderText;
    }
}
