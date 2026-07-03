package com.alphaseries.game.room;

import com.alphaseries.dao.mysql.RoomDao;
import com.alphaseries.dao.mysql.UserDao;
import com.alphaseries.messages.outgoing.NavigatorPayloads;
import com.alphaseries.messages.outgoing.RoomPayloads;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class RoomLookups {
    private RoomLookups() {
    }

    public record RoomRightRevocation(List<Long> targetUserIds, String notificationPayload) {
        public RoomRightRevocation {
            targetUserIds = List.copyOf(targetUserIds == null ? List.of() : targetUserIds);
            notificationPayload = StringUtils.text(notificationPayload);
        }

        public static RoomRightRevocation empty() {
            return new RoomRightRevocation(List.of(), "");
        }

        public boolean hasNotifications() {
            return !targetUserIds.isEmpty() && !notificationPayload.isEmpty();
        }
    }

    public record RoomRightSocketRevocation(List<Long> socketIndexes, String notificationPayload) {
        public RoomRightSocketRevocation {
            socketIndexes = List.copyOf(socketIndexes == null ? List.of() : socketIndexes);
            notificationPayload = StringUtils.text(notificationPayload);
        }

        public static RoomRightSocketRevocation empty() {
            return new RoomRightSocketRevocation(List.of(), "");
        }

        public boolean hasNotifications() {
            return !socketIndexes.isEmpty() && !notificationPayload.isEmpty();
        }
    }

    public record RoomEventChange(String directPayload, String broadcastPayload) {
        public RoomEventChange {
            directPayload = StringUtils.text(directPayload);
            broadcastPayload = StringUtils.text(broadcastPayload);
        }

        public static RoomEventChange empty() {
            return new RoomEventChange("", "");
        }

        public boolean hasDirectPayload() {
            return !directPayload.isEmpty();
        }

        public boolean hasBroadcastPayload() {
            return !broadcastPayload.isEmpty();
        }
    }

    public record RoomIconUpdate(String iconUpdatedPayload, String entryUpdatedPayload) {
        public RoomIconUpdate {
            iconUpdatedPayload = StringUtils.text(iconUpdatedPayload);
            entryUpdatedPayload = StringUtils.text(entryUpdatedPayload);
        }

        public static RoomIconUpdate empty() {
            return new RoomIconUpdate("", "");
        }

        public boolean valid() {
            return !iconUpdatedPayload.isEmpty() && !entryUpdatedPayload.isEmpty();
        }
    }

    public record RoomSettingsUpdate(String settingsUpdatedPayload, String entryUpdatedPayload, String wallOptionsPayload) {
        public RoomSettingsUpdate {
            settingsUpdatedPayload = StringUtils.text(settingsUpdatedPayload);
            entryUpdatedPayload = StringUtils.text(entryUpdatedPayload);
            wallOptionsPayload = StringUtils.text(wallOptionsPayload);
        }

        public static RoomSettingsUpdate empty() {
            return new RoomSettingsUpdate("", "", "");
        }

        public boolean valid() {
            return !settingsUpdatedPayload.isEmpty()
                && !entryUpdatedPayload.isEmpty()
                && !wallOptionsPayload.isEmpty();
        }
    }

    public static final class RoomModelInitialPayloads implements Iterable<String> {
        private final List<String> payloads;

        private RoomModelInitialPayloads(List<String> payloads) {
            this.payloads = List.copyOf(payloads == null ? List.of() : payloads);
        }

        public static RoomModelInitialPayloads empty() {
            return new RoomModelInitialPayloads(List.of());
        }

        public boolean isEmpty() {
            return payloads.isEmpty();
        }

        public boolean contains(String payload) {
            return payloads.contains(payload);
        }

        @Override
        public java.util.Iterator<String> iterator() {
            return payloads.iterator();
        }
    }

    public record RoomModelLoad(long modelId, RoomModelInitialPayloads initialPayloads) {
        public RoomModelLoad {
            initialPayloads = initialPayloads == null ? RoomModelInitialPayloads.empty() : initialPayloads;
        }

        public static RoomModelLoad empty() {
            return new RoomModelLoad(0L, RoomModelInitialPayloads.empty());
        }

        public boolean valid() {
            return modelId > 0L && !initialPayloads.isEmpty();
        }
    }

    public static final class RoomPresentationInitialPayloads implements Iterable<String> {
        private final List<String> payloads;

        private RoomPresentationInitialPayloads(List<String> payloads) {
            this.payloads = List.copyOf(payloads == null ? List.of() : payloads);
        }

        public static RoomPresentationInitialPayloads empty() {
            return new RoomPresentationInitialPayloads(List.of());
        }

        public boolean isEmpty() {
            return payloads.isEmpty();
        }

        public boolean contains(String payload) {
            return payloads.contains(payload);
        }

        @Override
        public java.util.Iterator<String> iterator() {
            return payloads.iterator();
        }
    }

    public record RoomPresentationLoad(long modelId, RoomPresentationInitialPayloads initialPayloads) {
        public RoomPresentationLoad {
            initialPayloads = initialPayloads == null ? RoomPresentationInitialPayloads.empty() : initialPayloads;
        }

        public static RoomPresentationLoad empty() {
            return new RoomPresentationLoad(0L, RoomPresentationInitialPayloads.empty());
        }

        public boolean valid() {
            return modelId > 0L && !initialPayloads.isEmpty();
        }
    }

    public static Optional<RoomUserTargetRow> activeRoomUserTarget(
        long roomId,
        long requestedRoomUserIndex,
        RoomDao rooms
    )
        throws Exception {

        if (roomId <= 0L || requestedRoomUserIndex <= 0L || rooms == null) {
            return Optional.empty();
        }
        Optional<RoomUserTargetRow> target = rooms.activeRoomUserTargetByVisitId(roomId, requestedRoomUserIndex);
        if (target.isEmpty()) {
            target = rooms.activeRoomUserTargetByUserId(roomId, requestedRoomUserIndex);
        }
        return target;
    }

    public static boolean userOwnsRoom(long userId, long roomId, RoomDao rooms) {
        if (userId <= 0L || roomId <= 0L || rooms == null) {
            return false;
        }
        try {
            return rooms.userOwnsRoom(userId, roomId);
        } catch (Exception ignored) {
            return false;
        }
    }

    public static boolean userHasRoomRight(long userId, long roomId, RoomDao rooms) {
        if (userId <= 0L || roomId <= 0L || rooms == null) {
            return false;
        }
        try {
            return rooms.userHasRoomRight(userId, roomId);
        } catch (Exception ignored) {
            return false;
        }
    }

    public static long categoryForUser(long categoryId, long rankIndex, long hcLevel, RoomDao rooms) {
        if (rooms == null) {
            return 0L;
        }
        try {
            return rooms.visibleCategoryId(categoryId, rankIndex, hcLevel);
        } catch (Exception ignored) {
            return 0L;
        }
    }

    public static String eventInfoPayload(long roomId, String timeFormat, RoomDao rooms) {
        if (roomId <= 0L || rooms == null) {
            return RoomPayloads.eventInfo(null);
        }
        try {
            return RoomPayloads.eventInfo(rooms.eventInfo(roomId, timeFormat).orElse(null));
        } catch (Exception ignored) {
            return RoomPayloads.eventInfo(null);
        }
    }

    /**
     * Original function: Proc_6_43_713680.
     */
    public static String roomSettingsPayload(long roomId, RoomDao rooms) {
        try {
            if (roomId <= 0L || rooms == null) {
                return "";
            }
            Optional<RoomDao.RoomSettingsRead> roomSettings = rooms.roomSettings(roomId);
            if (roomSettings.isEmpty()) {
                return "";
            }
            return RoomPayloads.settingsRead(roomSettings.get(), rooms.rightsRows(roomId));
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_44_7145E0.
     */
    public static RoomIconUpdate updateRoomIcon(long roomId, RoomWire.RoomIconRequest request, RoomDao rooms) {
        try {
            if (roomId <= 0L) {
                return RoomIconUpdate.empty();
            }
            String iconPayload = request == null ? "" : request.iconPayload();
            if (iconPayload.isEmpty()) {
                return RoomIconUpdate.empty();
            }
            if (rooms != null) {
                rooms.updateIcon(roomId, iconPayload);
            }
            return new RoomIconUpdate(RoomPayloads.iconUpdated(roomId), RoomPayloads.entryUpdated(roomId));
        } catch (Exception ignored) {
            return RoomIconUpdate.empty();
        }
    }

    /**
     * Original function: Proc_6_52_7172B0.
     */
    public static RoomSettingsUpdate updateRoomSettings(
        long roomId,
        RoomSettingsPayload settingsPayload,
        long rankIndex,
        long hcLevel,
        boolean canHideRoomWalls,
        RoomDao rooms
    ) {
        try {
            if (roomId <= 0L || rooms == null) {
                return RoomSettingsUpdate.empty();
            }
            RoomSettingsPayload settings = settingsPayload;
            if (settings == null) {
                return RoomSettingsUpdate.empty();
            }
            settings = settings.withCategoryId(categoryForUser(settings.categoryId(), rankIndex, hcLevel, rooms));
            if (settings.categoryId() <= 0L) {
                return RoomSettingsUpdate.empty();
            }
            if (settings.disableWalls() != 0L && !canHideRoomWalls) {
                settings = settings.withDisableWalls(0L);
            }
            rooms.updateSettings(
                roomId,
                settings.thicknessFloor(),
                settings.thicknessWallpaper(),
                settings.roomName(),
                settings.roomPassword(),
                settings.roomDescription(),
                settings.doorStatus(),
                settings.categoryId(),
                settings.tagOne(),
                settings.tagTwo(),
                settings.allowOthersPets(),
                settings.allowFeedPets(),
                settings.allowWalkthrough(),
                settings.visitorsMax(),
                settings.disableWalls());
            return new RoomSettingsUpdate(
                RoomPayloads.settingsUpdated(roomId),
                RoomPayloads.entryUpdated(roomId),
                RoomPayloads.wallOptions(
                    settings.disableWalls(),
                    settings.thicknessFloor(),
                    settings.thicknessWallpaper()));
        } catch (Exception ignored) {
            return RoomSettingsUpdate.empty();
        }
    }

    /**
     * Original function: Proc_6_77_727590.
     */
    public static String officialRoomModelPayload(long roomId, RoomDao rooms) {
        try {
            if (roomId <= 0L || rooms == null) {
                return "";
            }
            RoomDao.OfficialRoomModel officialRoom = rooms.officialRoomModel(roomId).orElse(null);
            if (officialRoom == null) {
                return "";
            }
            return RoomPayloads.officialRoomModel(roomId, officialRoom);
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_78_7279A0.
     */
    public static RoomModelLoad roomModelLoad(long roomId, RoomDao rooms) {
        try {
            if (roomId <= 0L || rooms == null) {
                return RoomModelLoad.empty();
            }
            RoomDao.RoomModelEntry roomEntry = rooms.roomModelEntry(roomId).orElse(null);
            if (roomEntry == null || roomEntry.modelId() <= 0L) {
                return RoomModelLoad.empty();
            }
            String modelPayload = RoomWire.normalizeModelMap(roomEntry.modelMap());
            List<String> payloads = new ArrayList<>();
            payloads.add("Bf" + "/client.php" + '\2');
            payloads.add("AE" + roomId + '\2' + "H");
            if (!modelPayload.isEmpty()) {
                payloads.add("@_" + modelPayload + '\2');
                payloads.add("GV" + modelPayload + '\2');
                payloads.add("GWH" + modelPayload + '\2' + "H");
            }
            return new RoomModelLoad(roomEntry.modelId(), new RoomModelInitialPayloads(payloads));
        } catch (Exception ignored) {
            return RoomModelLoad.empty();
        }
    }

    /**
     * Original function: Proc_6_79_72A430.
     */
    public static RoomPresentationLoad roomPresentationLoad(
        long userId,
        long roomId,
        boolean hasControl,
        String eventInfoPayload,
        RoomDao rooms
    ) {
        try {
            if (userId <= 0L || roomId <= 0L || rooms == null) {
                return RoomPresentationLoad.empty();
            }
            RoomDao.RoomPresentationState roomState = rooms.roomPresentationState(roomId).orElse(null);
            if (roomState == null || roomState.modelId() <= 0L) {
                return RoomPresentationLoad.empty();
            }
            long roomRate = roomState.roomRate();
            if (roomRate < 0L) {
                roomRate = 0L;
            }
            boolean hasVoted = rooms.hasRatedRoom(userId, roomId);
            long ratingPayloadValue = hasVoted ? -1L : roomRate;
            String modelPayload = RoomWire.normalizeModelMap(roomState.modelMap());
            List<String> payloads = new ArrayList<>();
            payloads.add(RoomPayloads.currentRoom(roomId));
            payloads.add("@nfloor" + '\2' + StringUtils.text(roomState.floorPattern()) + '\2');
            payloads.add("@nwallpaper" + '\2' + StringUtils.text(roomState.wallpaperPattern()) + '\2');
            payloads.add("@nlandscape" + '\2' + StringUtils.text(roomState.landscapePattern()) + '\2');
            payloads.add(RoomPayloads.rating(ratingPayloadValue));
            payloads.add("Er" + eventInfoPayload);
            if (hasControl) {
                payloads.add("@j");
            }
            if (roomState.ownerUserId() == userId) {
                payloads.add("@o");
            }
            if (!modelPayload.isEmpty()) {
                payloads.add("@_" + modelPayload + '\2');
                payloads.add("GV" + modelPayload + '\2');
            }
            payloads.add(RoomPayloads.wallOptions(
                roomState.disableWalls(),
                roomState.thicknessFloor(),
                roomState.thicknessWallpaper()));
            return new RoomPresentationLoad(roomState.modelId(), new RoomPresentationInitialPayloads(payloads));
        } catch (Exception ignored) {
            return RoomPresentationLoad.empty();
        }
    }

    /**
     * Original function: Proc_6_63_721050.
     */
    public static String rateRoomPayload(long userId, long roomId, long voteValue, RoomDao rooms) {
        try {
            if (userId <= 0L || roomId <= 0L || voteValue != 1L || rooms == null) {
                return "";
            }
            if (rooms.userRatedRoom(userId, roomId)) {
                return "";
            }
            rooms.insertRoomRate(userId, roomId);
            long roomRate = rooms.roomRate(roomId);
            if (roomRate < 0L) {
                roomRate = 0L;
            }
            roomRate++;
            rooms.updateRoomRate(roomId, roomRate);
            return RoomPayloads.rating(roomRate);
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_64_721650.
     */
    public static String revokeRoomRightByNamePayload(String targetName, long roomId, UserDao users, RoomDao rooms) {
        try {
            if (StringUtils.text(targetName).isEmpty() || roomId <= 0L || users == null || rooms == null) {
                return "";
            }
            long targetUserId = users.userIdByName(targetName);
            if (targetUserId <= 0L) {
                return "";
            }
            rooms.deleteRoomRight(targetUserId, roomId);
            return RoomPayloads.roomRightRemoved();
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_65_721A10.
     */
    public static String grantRoomRightPayload(long targetUserId, long roomId, RoomDao rooms) {
        try {
            if (targetUserId <= 0L || roomId <= 0L || rooms == null) {
                return "";
            }
            rooms.insertRoomRight(targetUserId, roomId);
            return "@j";
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_74_7265B0.
     */
    public static RoomRightRevocation revokeRoomRights(List<Long> targetUserIds, long roomId, RoomDao rooms) {
        try {
            if (targetUserIds == null || targetUserIds.isEmpty() || roomId <= 0L || rooms == null) {
                return RoomRightRevocation.empty();
            }
            List<Long> revokedTargetIds = new ArrayList<>();
            for (long targetUserId : targetUserIds) {
                if (targetUserId <= 0L) {
                    continue;
                }
                rooms.deleteRoomRight(targetUserId, roomId);
                revokedTargetIds.add(targetUserId);
            }
            return new RoomRightRevocation(revokedTargetIds, "@k");
        } catch (Exception ignored) {
            return RoomRightRevocation.empty();
        }
    }

    /**
     * Original function: Proc_6_71_724CF0.
     */
    public static RoomRightSocketRevocation revokeAllRoomRights(long roomId, RoomDao rooms) {
        try {
            if (roomId <= 0L || rooms == null) {
                return RoomRightSocketRevocation.empty();
            }
            List<Long> socketIndexes = rooms.activeRightHolderSocketIndexes(roomId);
            rooms.deleteRoomRights(roomId);
            return new RoomRightSocketRevocation(socketIndexes, "@k");
        } catch (Exception ignored) {
            return RoomRightSocketRevocation.empty();
        }
    }

    /**
     * Original function: Proc_6_72_7250D0.
     */
    public static boolean deleteRoom(long roomId, RoomDao rooms) {
        try {
            if (roomId <= 0L || rooms == null) {
                return false;
            }
            rooms.deleteRoom(roomId);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    /**
     * Original function: Proc_6_46_714D50.
     */
    public static String doorStatusPayload(long roomId, RoomDao rooms) {
        try {
            long doorStatus = roomId <= 0L || rooms == null ? 0L : rooms.doorStatus(roomId);
            return RoomPayloads.doorStatus(doorStatus);
        } catch (Exception ignored) {
            return RoomPayloads.doorStatus(0L);
        }
    }

    /**
     * Original function: Proc_6_47_714F60.
     */
    public static String setHomeRoomPayload(long userId, long roomId, UserDao users) {
        try {
            if (userId <= 0L || roomId <= 0L) {
                return "";
            }
            if (users != null) {
                users.updateHomeRoom(userId, roomId);
            }
            return RoomPayloads.homeRoom(roomId);
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_45_714B60.
     */
    public static RoomEventChange deleteRoomEvent(long roomId, RoomDao rooms) {
        try {
            if (roomId <= 0L) {
                return RoomEventChange.empty();
            }
            if (rooms != null) {
                rooms.deleteRoomEvents(roomId);
            }
            return new RoomEventChange("Er-1" + '\2', "");
        } catch (Exception ignored) {
            return RoomEventChange.empty();
        }
    }

    /**
     * Original function: Proc_6_48_7151E0.
     */
    public static RoomEventChange createRoomEvent(
        long userId,
        long roomId,
        RoomEventPayload event,
        String timeFormat,
        RoomDao rooms
    ) {
        try {
            if (userId <= 0L || roomId <= 0L) {
                return RoomEventChange.empty();
            }
            long doorStatus = rooms == null ? 0L : rooms.doorStatus(roomId);
            if (doorStatus != 0L) {
                return new RoomEventChange(RoomPayloads.doorStatus(doorStatus), "");
            }
            if (event == null) {
                return RoomEventChange.empty();
            }
            if (rooms != null) {
                rooms.insertRoomEvent(
                    roomId,
                    userId,
                    event.eventName(),
                    event.eventDescription(),
                    event.categoryId(),
                    event.tagOne(),
                    event.tagTwo(),
                    event.categoryName());
            }
            return new RoomEventChange("", "Er" + eventInfoPayload(roomId, timeFormat, rooms));
        } catch (Exception ignored) {
            return RoomEventChange.empty();
        }
    }

    /**
     * Original function: Proc_6_49_715D30.
     */
    public static RoomEventChange editRoomEvent(
        long userId,
        long roomId,
        RoomEventPayload event,
        String timeFormat,
        RoomDao rooms
    ) {
        try {
            if (userId <= 0L || roomId <= 0L) {
                return RoomEventChange.empty();
            }
            if (event == null) {
                return RoomEventChange.empty();
            }
            if (rooms != null) {
                rooms.updateRoomEvent(
                    roomId,
                    userId,
                    event.eventName(),
                    event.eventDescription(),
                    event.tagOne(),
                    event.tagTwo());
            }
            return new RoomEventChange("", "Er" + eventInfoPayload(roomId, timeFormat, rooms));
        } catch (Exception ignored) {
            return RoomEventChange.empty();
        }
    }

    /**
     * Original function: Proc_6_104_74AB60.
     */
    public static String creatableRoomCountPayload(long userId, long maxOwnedRooms, RoomDao rooms) {
        try {
            long ownedRoomCount = userId <= 0L || rooms == null ? 0L : rooms.ownedRoomCount(userId);
            return RoomPayloads.creatableRoomCount(maxOwnedRooms, ownedRoomCount);
        } catch (Exception ignored) {
            return RoomPayloads.creatableRoomCount(maxOwnedRooms, 0L);
        }
    }

    /**
     * Original function: Proc_6_105_74AD50.
     */
    public static CreatedRoom createRoom(
        long userId,
        RoomWire.CreateRoomRequest request,
        long maxOwnedRooms,
        long hcLevel,
        RoomDao rooms
    ) {
        try {
            if (userId <= 0L || rooms == null) {
                return CreatedRoom.empty();
            }
            long ownedRoomCount = rooms.ownedRoomCount(userId);
            if (maxOwnedRooms > 0L && ownedRoomCount >= maxOwnedRooms) {
                return CreatedRoom.empty();
            }
            if (request == null || request.roomName().isEmpty() || request.modelName().isEmpty()) {
                return CreatedRoom.empty();
            }
            RoomDao.CreatableRoomModel model = rooms.creatableRoomModel(hcLevel, request.modelName()).orElse(null);
            if (model == null || model.modelId() <= 0L) {
                return CreatedRoom.empty();
            }
            long visitorsMax = model.visitorsMax() <= 0L ? 25L : model.visitorsMax();
            rooms.insertRoom(userId, request.roomName(), visitorsMax, model.modelId());
            long roomId = rooms.newestRoomId();
            if (roomId <= 0L) {
                return CreatedRoom.empty();
            }
            return new CreatedRoom(roomId, request.roomName(), RoomPayloads.createdRoom(roomId, request.roomName()));
        } catch (Exception ignored) {
            return CreatedRoom.empty();
        }
    }

    /**
     * Original function: Proc_6_107_74B7E0.
     */
    public static StaffPickedToggle toggleStaffPickedRoom(
        long roomId,
        long categoryId,
        long styleId,
        long iconId,
        RoomDao rooms,
        UserDao users
    ) {
        try {
            if (roomId <= 0L || rooms == null) {
                return StaffPickedToggle.empty();
            }
            long ownerUserId = rooms.roomOwnerId(roomId);
            if (ownerUserId <= 0L) {
                return StaffPickedToggle.empty();
            }
            long currentPicked = rooms.staffPickedState(roomId);
            long newPicked = currentPicked == 0L ? 1L : 0L;
            long resolvedCategoryId = categoryId <= 0L ? 1L : categoryId;
            rooms.deleteStaffPickedOfficialRoom(resolvedCategoryId, roomId);
            if (newPicked != 0L) {
                rooms.insertStaffPickedOfficialRoom(resolvedCategoryId, roomId, styleId, iconId);
                if (users != null) {
                    users.incrementStaffPickedCount(ownerUserId);
                }
            }
            rooms.updateStaffPickedState(roomId, newPicked);
            return new StaffPickedToggle(roomId, newPicked);
        } catch (Exception ignored) {
            return StaffPickedToggle.empty();
        }
    }

    /**
     * Original function: Proc_6_108_74D800.
     */
    public static String favouriteRoomIdsPayload(long userId, long maxFavorites, RoomDao rooms) {
        try {
            long resolvedMaxFavorites = maxFavorites <= 0L ? 30L : maxFavorites;
            if (userId <= 0L || rooms == null) {
                return "";
            }
            return NavigatorPayloads.favouriteRoomIds(
                rooms.favouriteRoomIds(userId, resolvedMaxFavorites),
                resolvedMaxFavorites).payload();
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_109_74DBD0.
     */
    public static String removeFavouriteRoomPayload(long userId, long roomId, RoomDao rooms) {
        try {
            if (userId <= 0L || roomId <= 0L) {
                return "";
            }
            if (rooms != null) {
                rooms.deleteFavouriteRoom(userId, roomId);
            }
            return RoomPayloads.favouriteRemoved(roomId);
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_110_74DDA0.
     */
    public static String addFavouriteRoomPayload(long userId, long roomId, RoomDao rooms) {
        try {
            if (userId <= 0L || roomId <= 0L) {
                return "";
            }
            if (rooms != null) {
                rooms.insertFavouriteRoom(userId, roomId);
            }
            return RoomPayloads.favouriteAdded(roomId);
        } catch (Exception ignored) {
            return "";
        }
    }
}
