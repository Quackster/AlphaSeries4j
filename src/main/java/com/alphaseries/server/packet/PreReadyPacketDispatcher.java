package com.alphaseries.server.packet;

import com.alphaseries.config.AppConfigState;
import com.alphaseries.game.achievement.AchievementPacketHandlers;
import com.alphaseries.game.advertising.AdvertisingPacketHandlers;
import com.alphaseries.game.catalog.CatalogPacketHandlers;
import com.alphaseries.game.catalog.CatalogWire;
import com.alphaseries.game.catalog.ClubPacketHandlers;
import com.alphaseries.game.catalog.VoucherWire;
import com.alphaseries.game.help.HelpPacketHandlers;
import com.alphaseries.game.help.HelpWire;
import com.alphaseries.game.inventory.InventoryPacketHandlers;
import com.alphaseries.game.jukebox.JukeboxPacketHandlers;
import com.alphaseries.game.jukebox.JukeboxRequests;
import com.alphaseries.game.messenger.MessengerPacketHandlers;
import com.alphaseries.game.messenger.MessengerWire;
import com.alphaseries.game.moderation.StaffModerationPacketHandlers;
import com.alphaseries.game.moderation.StaffWire;
import com.alphaseries.game.navigator.NavigatorPacketHandlers;
import com.alphaseries.game.navigator.NavigatorWire;
import com.alphaseries.game.pet.PetPacketHandlers;
import com.alphaseries.game.pet.PetWire;
import com.alphaseries.game.poll.PollPacketHandlers;
import com.alphaseries.game.poll.PollWire;
import com.alphaseries.game.quest.QuestPacketHandlers;
import com.alphaseries.game.quest.QuestWire;
import com.alphaseries.game.recycler.RecyclerPacketHandlers;
import com.alphaseries.game.recycler.RecyclerWire;
import com.alphaseries.game.room.FurniturePacketHandlers;
import com.alphaseries.game.room.FurnitureWire;
import com.alphaseries.game.room.RoomPacketHandlers;
import com.alphaseries.game.room.RoomRefreshService;
import com.alphaseries.game.room.RoomUserPosition;
import com.alphaseries.game.room.RoomWire;
import com.alphaseries.game.session.SessionPacketHandlers;
import com.alphaseries.game.session.SessionWire;
import com.alphaseries.game.social.SocialPacketHandlers;
import com.alphaseries.game.social.SocialWire;
import com.alphaseries.game.trade.TradePacketHandlers;
import com.alphaseries.game.trade.TradeWire;
import com.alphaseries.game.user.UserPacketHandlers;
import com.alphaseries.game.user.UserWire;
import com.alphaseries.game.wired.WiredPacketHandlers;
import com.alphaseries.game.wired.WiredWire;
import com.alphaseries.protocol.WireEncoding;
import com.alphaseries.server.lifecycle.LifecycleState;
import com.alphaseries.server.logging.Console;
import com.alphaseries.server.runtime.Guardian;
import com.alphaseries.server.runtime.SessionLookups;
import com.alphaseries.server.runtime.SocketDelivery;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.function.IntConsumer;

public final class PreReadyPacketDispatcher {
    private PreReadyPacketDispatcher() {
    }

    /**
     * Original function: Proc_6_241_7FC380.
     */
    public static void processPreSessionPacketBuffer(
        long socketIndex,
        String packetBuffer,
        IntConsumer disconnectSocket,
        QuestPacketHandlers.CompletionHandler questCompletionHandler
    ) {
        try {
            if (socketIndex <= 0L || !Guardian.isSocketConnected(socketIndex)) {
                return;
            }
            long packetCount = 0L;
            while (packetBuffer.length() > 2 && packetCount < 10L) {
                packetBuffer = packetBuffer.substring(1);
                long packetLength = WireEncoding.decodeBase64Length(StringUtils.left(packetBuffer, 2));
                if (packetLength <= 0L || packetBuffer.length() < packetLength + 2L) {
                    break;
                }
                String payload = StringUtils.mid(packetBuffer, 3, (int) packetLength);
                String packetCode = StringUtils.left(payload, 2);
                if (LifecycleState.instance().runtimeState().shouldTracePackets()) {
                    Console.logSourceLine("[" + socketIndex + "] " + payload, "GAME", 16711680L);
                }
                dispatchPreReadyPacket((int) socketIndex, packetCode, payload, disconnectSocket, questCompletionHandler);
                packetCount++;
                packetBuffer = StringUtils.mid(packetBuffer, (int) packetLength + 3);
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    public static void dispatchPreReadyPacket(
        int socketIndex,
        String packetCode,
        String payload,
        IntConsumer disconnectSocket,
        QuestPacketHandlers.CompletionHandler questCompletionHandler
    ) {
        try {
            switch (StringUtils.text(packetCode)) {
                case "~\u00e4": System.exit(0); break;
                case "oD": UserPacketHandlers.sendGuideInvitation(socketIndex); break;
                case "Gd": UserPacketHandlers.updateMotto(socketIndex, UserWire.mottoRequest(payload)); break;
                case "C]": JukeboxPacketHandlers.sendSongInfo(socketIndex, JukeboxRequests.songInfoFromWire(payload)); break;
                case "C\u007f": JukeboxPacketHandlers.addDisk(socketIndex, JukeboxRequests.addRequestFromWire(payload)); break;
                case "D@": JukeboxPacketHandlers.removeDisk(socketIndex, JukeboxRequests.removeRequestFromWire(payload)); break;
                case "DC":
                    JukeboxPacketHandlers.sendPlaylist(socketIndex);
                    JukeboxPacketHandlers.sendDiskInventory(socketIndex);
                    break;
                case "AZ": FurniturePacketHandlers.returnRoomFurnitureToInventory(socketIndex, FurnitureWire.pickupFurnitureRequest(payload)); break;
                case "AC": FurniturePacketHandlers.pickUpRoomFurniture(socketIndex, FurnitureWire.stickyFurnitureRequest(payload)); break;
                case "A[": FurniturePacketHandlers.moveFloorFurnitureInRoom(socketIndex, FurnitureWire.floorPlacementRequest(payload)); break;
                case "AI": FurniturePacketHandlers.moveFloorFurnitureInRoom(socketIndex, FurnitureWire.floorPlacementRequest(payload)); break;
                case "Ch": FurniturePacketHandlers.toggleFloorFurnitureState(socketIndex, FurnitureWire.floorStateFurnitureRequest(payload)); break;
                case "FH": FurniturePacketHandlers.openFloorFurniturePackageOrToggleState(socketIndex, FurnitureWire.floorFurniturePackageRequest(payload)); break;
                case "@B": RoomPacketHandlers.loadCurrentRoomModel(socketIndex, questCompletionHandler); break;
                case "rv": FurniturePacketHandlers.placeFloorFurnitureFromInventory(socketIndex, FurnitureWire.floorPlacementRequest(payload)); break;
                case "pa": SocketDelivery.sendToSocket(socketIndex, "J|H"); break;
                case "Ce": UserPacketHandlers.updateSoundSetting(socketIndex, UserWire.soundSettingRequest(payload)); break;
                case "Cy": break;
                case "pb": QuestPacketHandlers.resetQuests(socketIndex); break;
                case "p^": QuestPacketHandlers.acceptQuest(socketIndex, QuestWire.questIdRequest(payload, "p^"), questCompletionHandler); break;
                case "pc": QuestPacketHandlers.autoAcceptNextQuest(socketIndex, questCompletionHandler); break;
                case "p]": QuestPacketHandlers.sendList(socketIndex); break;
                case "F_": SessionPacketHandlers.handleLoginTicket(
                    socketIndex,
                    SessionWire.loginTicketRequest(payload),
                    disconnectSocket); break;
                case "GV": UserPacketHandlers.changeAvatarName(socketIndex, UserWire.avatarNameRequest(payload, "GV")); break;
                case "GW": UserPacketHandlers.checkAvatarName(socketIndex, UserWire.avatarNameRequest(payload, "GW")); break;
                case "F]": RecyclerPacketHandlers.sendStatus(socketIndex); break;
                case "F^": RecyclerPacketHandlers.submitItems(socketIndex, RecyclerWire.selectionFromWire(payload)); break;
                case "Cj": PollPacketHandlers.sendLivePoll(socketIndex, PollWire.idRequestFromWire(payload, "Cj").pollId()); break;
                case "Ck": PollPacketHandlers.recordExit(socketIndex, PollWire.idRequestFromWire(payload, "Ck").pollId()); break;
                case "Cl": PollPacketHandlers.submitAnswer(socketIndex, PollWire.answerFromWire(payload, "Cl")); break;
                case "EW": FurniturePacketHandlers.toggleDimmerState(socketIndex); break;
                case "AL": FurniturePacketHandlers.useSimpleFloorItem(socketIndex, FurnitureWire.simpleFloorItemUseRequest(payload, "AL"), -1L, false, RoomUserPosition.absent()); break;
                case "AM": FurniturePacketHandlers.useSimpleFloorItem(socketIndex, FurnitureWire.simpleFloorItemUseRequest(payload, "AM"), 0L, true, RoomUserPosition.absent()); break;
                case "FU": TradePacketHandlers.addTradeFurniture(socketIndex, TradeWire.furnitureRequest(payload, "FU")); break;
                case "AH": TradePacketHandlers.removeTradeFurniture(socketIndex, TradeWire.furnitureRequest(payload, "AH")); break;
                case "FR": TradePacketHandlers.confirmTrade(socketIndex); break;
                case "EV": FurniturePacketHandlers.updateDimmerPreset(socketIndex, FurnitureWire.dimmerPresetRequest(payload)); break;
                case "EU": FurniturePacketHandlers.sendDimmerPresets(socketIndex); break;
                case "Er": AchievementPacketHandlers.sendList(socketIndex); break;
                case "CD": break;
                case "@G": UserPacketHandlers.sendOwnProfile(socketIndex); break;
                case "D{":
                case "Fe": break;
                case "@t": SocialPacketHandlers.chatInCurrentRoom(socketIndex, SocialWire.representedChatMessage(payload, 0L)); break;
                case "@w": SocialPacketHandlers.shoutInCurrentRoom(socketIndex, SocialWire.representedChatMessage(payload, 1L)); break;
                case "@x": SocialPacketHandlers.whisperInCurrentRoom(socketIndex, SocialWire.representedChatMessage(payload, 2L)); break;
                case "Cd": UserPacketHandlers.sendEffectList(socketIndex); break;
                case "Et":
                case "Eu": UserPacketHandlers.activateEffect(socketIndex, SocialWire.effectRequest(payload)); break;
                case "@Z": RecyclerPacketHandlers.sendCachedStatus(socketIndex, "", "Gz"); break;
                case "oW": ClubPacketHandlers.sendSubscriptionOffers(socketIndex); break;
                case "Cn": StaffModerationPacketHandlers.cancelLatestCallForHelp(socketIndex); break;
                case "A^": SocialPacketHandlers.waveCurrentRoomUser(socketIndex); break;
                case "A]": SocialPacketHandlers.danceCurrentRoomUser(socketIndex, SocialWire.danceRequest(payload)); break;
                case "GE": StaffModerationPacketHandlers.submitCallForHelp(socketIndex, StaffWire.submitCallForHelpRequest(payload)); break;
                case "F`": HelpPacketHandlers.sendImportantFaqs(socketIndex); break;
                case "Fa": HelpPacketHandlers.sendCategories(socketIndex); break;
                case "Fb": HelpPacketHandlers.sendDescription(socketIndex, HelpWire.faqIdRequest(payload, "Fb")); break;
                case "Fc": HelpPacketHandlers.searchFaqs(socketIndex, HelpWire.faqSearchRequest(payload, "Fc"), disconnectSocket); break;
                case "Fd": HelpPacketHandlers.sendCategoryFaqs(socketIndex, HelpWire.categoryFaqRequest(payload, "Fd")); break;
                case "Ae": CatalogPacketHandlers.sendDefaultIndex(socketIndex); break;
                case "FC": RoomPacketHandlers.sendCreatableRoomCount(socketIndex); break;
                case "@]": RoomPacketHandlers.createRoom(socketIndex, RoomWire.createRoomRequest(payload)); break;
                case "Af": CatalogPacketHandlers.sendPage(socketIndex, CatalogWire.pageRequest(payload)); break;
                case "Fv": NavigatorPacketHandlers.sendTagResults(socketIndex, NavigatorWire.queryRequest(payload)); break;
                case "FG": RoomPacketHandlers.enterRoomFromPayload(socketIndex, RoomWire.roomEntryRequest(payload)); break;
                case "Bv": AdvertisingPacketHandlers.sendVisitRoomAdvertisement(socketIndex); break;
                case "@{": RoomPacketHandlers.sendCurrentRoomDecoration(socketIndex, questCompletionHandler); break;
                case "Ew": UserPacketHandlers.sendWardrobeSlots(socketIndex); break;
                case "Ex": UserPacketHandlers.saveWardrobeSlot(socketIndex, UserWire.wardrobeSlotRequest(payload)); break;
                case "@l": UserPacketHandlers.updateTutorialClothes(socketIndex, UserWire.tutorialClothesRequest(payload)); break;
                case "oC": CatalogPacketHandlers.sendGiftWrapOptions(socketIndex); break;
                case "oV": CatalogPacketHandlers.sendGiftAvailability(socketIndex, CatalogWire.giftAvailabilityRequest(payload)); break;
                case "Ad": CatalogPacketHandlers.purchaseProduct(socketIndex, CatalogWire.productPurchaseRequest(payload)); break;
                case "GX": CatalogPacketHandlers.purchaseGift(socketIndex, CatalogWire.giftPurchaseRequest(payload)); break;
                case "GZ": ClubPacketHandlers.sendGiftStatus(socketIndex); break;
                case "G[": ClubPacketHandlers.claimGift(socketIndex, CatalogWire.clubGiftClaimRequest(payload)); break;
                case "Gc": RoomPacketHandlers.toggleStaffPickedRoom(socketIndex); break;
                case "GG": StaffModerationPacketHandlers.sendRoomChatHistory(socketIndex, StaffWire.historyRequest(payload, "GG", true)); break;
                case "F@": RoomPacketHandlers.setHomeRoom(socketIndex, RoomWire.roomIdRequest(payload, "F@")); break;
                case "FB": RoomPacketHandlers.updateRoomIcon(socketIndex, RoomWire.roomIconRequest(payload)); break;
                case "Ab": SocialPacketHandlers.followUserToRoom(
                    socketIndex,
                    SocialWire.followUserRequest(payload),
                    roomId -> RoomPacketHandlers.enterRoom(socketIndex, roomId, "")); break;
                case "EZ": RoomPacketHandlers.createRoomEvent(socketIndex, RoomWire.roomEventCreatePayloadFromWire(payload)); break;
                case "E\\": RoomPacketHandlers.editRoomEvent(socketIndex, RoomWire.roomEventEditPayloadFromWire(payload)); break;
                case "FP":
                case "FF": RoomPacketHandlers.sendRoomSettings(socketIndex, RoomWire.roomSettingsReadRequest(payload)); break;
                case "FQ": RoomPacketHandlers.updateRoomSettings(socketIndex, RoomWire.roomSettingsFromWire(payload)); break;
                case "@H": RoomPacketHandlers.sendFavouriteRoomIds(socketIndex); break;
                case "@S": RoomPacketHandlers.addFavouriteRoom(socketIndex, RoomWire.roomIdRequest(payload, "@S")); break;
                case "@T": RoomPacketHandlers.removeFavouriteRoom(socketIndex, RoomWire.roomIdRequest(payload, "@T")); break;
                case "BW": NavigatorPacketHandlers.sendRoomCategoryPayload(socketIndex); break;
                case "GI": StaffModerationPacketHandlers.sendCallForHelpChatLog(
                    socketIndex,
                    StaffWire.callForHelpChatLogRequest(payload)); break;
                case "oj": WiredPacketHandlers.editTrigger(socketIndex, WiredWire.editFurnitureRequest(payload, "oj")); break;
                case "ok": WiredPacketHandlers.editAction(socketIndex, WiredWire.editFurnitureRequest(payload, "ok")); break;
                case "ol": WiredPacketHandlers.editCondition(socketIndex, WiredWire.editFurnitureRequest(payload, "ol")); break;
                case "on": WiredPacketHandlers.createSnapshot(socketIndex, WiredWire.snapshotRequest(payload)); break;
                case "GH": StaffModerationPacketHandlers.sendRoomChatLog(
                    socketIndex,
                    StaffWire.roomChatLogRequest(payload)); break;
                case "GK": StaffModerationPacketHandlers.sendRoomInfo(
                    socketIndex,
                    StaffWire.roomInfoRequest(payload)); break;
                case "GF": StaffModerationPacketHandlers.sendUserSummary(socketIndex, StaffWire.userSummaryRequest(payload)); break;
                case "GJ": StaffModerationPacketHandlers.sendRoomVisitHistory(socketIndex, StaffWire.historyRequest(payload, "GJ", false)); break;
                case "GM": StaffModerationPacketHandlers.sendCaution(socketIndex, StaffWire.directMessageRequest(payload, "GM")); break;
                case "GN": StaffModerationPacketHandlers.sendAlert(socketIndex, StaffWire.directMessageRequest(payload, "GN")); break;
                case "GO": StaffModerationPacketHandlers.kickUser(socketIndex, StaffWire.directMessageRequest(payload, "GO")); break;
                case "GP": StaffModerationPacketHandlers.banUser(socketIndex, StaffWire.banRequest(payload), disconnectSocket); break;
                case "CH": StaffModerationPacketHandlers.moderateCurrentRoom(socketIndex, StaffWire.roomModerationRequest(payload)); break;
                case "GB": StaffModerationPacketHandlers.moveCallForHelpToPickedTab(socketIndex, StaffWire.callForHelpTabRequest(payload, "GB")); break;
                case "GC": StaffModerationPacketHandlers.moveCallForHelpToOpenTab(socketIndex, StaffWire.callForHelpTabRequest(payload, "GC")); break;
                case "GD": StaffModerationPacketHandlers.closeCallForHelp(socketIndex, StaffWire.closeCallForHelpRequest(payload)); break;
                case "GL": StaffModerationPacketHandlers.lockCurrentRoomForModeration(socketIndex, StaffWire.roomLockRequest(payload)); break;
                case "Fw": NavigatorPacketHandlers.sendEventCategoryRooms(socketIndex, NavigatorWire.categoryRequest(payload)); break;
                case "Fn": NavigatorPacketHandlers.sendPopularCategoryRooms(socketIndex, NavigatorWire.categoryRequest(payload)); break;
                case "Fr": NavigatorPacketHandlers.sendOwnedRooms(socketIndex); break;
                case "Fq": NavigatorPacketHandlers.sendFriendCurrentRooms(socketIndex); break;
                case "Fp": NavigatorPacketHandlers.sendFriendOwnedRooms(socketIndex); break;
                case "Fs": NavigatorPacketHandlers.sendFavouriteRooms(socketIndex); break;
                case "Fo": NavigatorPacketHandlers.sendTopRatedRooms(socketIndex); break;
                case "Ft": NavigatorPacketHandlers.sendRecentlyVisitedRooms(socketIndex); break;
                case "E|": NavigatorPacketHandlers.sendOfficialNavigator(socketIndex); break;
                case "Fu": NavigatorPacketHandlers.sendSearchResults(socketIndex, NavigatorWire.queryRequest(payload)); break;
                case "E~": NavigatorPacketHandlers.sendPopularTags(socketIndex); break;
                case "EY": RoomPacketHandlers.sendRoomDoorStatus(socketIndex); break;
                case "Gj": NavigatorPacketHandlers.sendNewFriendRoom(socketIndex); break;
                case "@L": MessengerPacketHandlers.sendFriendList(socketIndex); break;
                case "@u":
                case "Ao": RoomRefreshService.sendRoomReady(socketIndex); break;
                case "@j": PetPacketHandlers.validateName(socketIndex, PetWire.nameValidationRequest(payload)); break;
                case "@f": MessengerPacketHandlers.deleteFriendRequests(socketIndex, MessengerWire.friendDeleteTargetsFromPayload(payload)); break;
                case "DF": MessengerPacketHandlers.followFriend(socketIndex, MessengerWire.friendFollowRequest(payload)); break;
                case "@O": break;
                case "D}":
                case "D~": SocialPacketHandlers.broadcastPreReadyRoomUserState(socketIndex); break;
                case "@a": MessengerPacketHandlers.sendPrivateMessage(socketIndex, MessengerWire.privateMessageFromWire(payload)); break;
                case "Ci": MessengerPacketHandlers.sendPendingRequests(socketIndex); break;
                case "@b": MessengerPacketHandlers.sendRoomInvite(socketIndex, MessengerWire.roomInviteFromWire(payload)); break;
                case "@e": MessengerPacketHandlers.acceptFriendRequests(socketIndex, MessengerWire.acceptFriendRequests(payload)); break;
                case "@i": MessengerPacketHandlers.searchUsers(socketIndex, MessengerWire.searchRequest(payload, "@i")); break;
                case "@g": MessengerPacketHandlers.requestFriend(socketIndex, MessengerWire.friendRequest(payload, "@g")); break;
                case "@h": MessengerPacketHandlers.removeFriends(socketIndex, MessengerWire.friendRemoveTargetsFromPayload(
                    payload, NumberUtils.parseLong(SessionLookups.userIdTextFromSocket(socketIndex)))); break;
                case "Fy": PetPacketHandlers.removeTutorialGuides(socketIndex, PetWire.petIdRequest(payload, "Fy")); break;
                case "Fx": PetPacketHandlers.spawnTutorialGuide(socketIndex); break;
                case "Cg": SocialPacketHandlers.sendRoomUserProfile(socketIndex, SocialWire.roomUserIndexRequest(payload, "Cg")); break;
                case "DG": SocialPacketHandlers.sendUserTags(socketIndex, SocialWire.userIdRequest(payload, "DG")); break;
                case "B_": SocialPacketHandlers.lookAtRoomUserBadge(socketIndex, SocialWire.roomUserIndexRequest(payload, "B_")); break;
                case "B]": SocialPacketHandlers.sendBadgeInventory(socketIndex); break;
                case "B^": SocialPacketHandlers.updateEquippedBadges(socketIndex, SocialWire.badgeUpdateSelections(payload)); break;
                case "pg": break;
                case "AK": RoomPacketHandlers.lookTowardRoomPosition(socketIndex, RoomWire.positionRequest(payload, "AK")); break;
                case "AO": RoomPacketHandlers.walkTowardRoomPosition(socketIndex, RoomWire.positionRequest(payload, "AO")); break;
                case "AG": TradePacketHandlers.requestInteraction(socketIndex, SocialWire.roomUserIndexRequest(payload, "AG")); break;
                case "FT": InventoryPacketHandlers.sendInventoryToSocket(socketIndex); break;
                case "AB": FurniturePacketHandlers.applyRoomDecorationFurniture(socketIndex, FurnitureWire.stickyFurnitureRequest(payload)); break;
                case "BA": CatalogPacketHandlers.redeemVoucher(socketIndex, VoucherWire.redeemRequest(payload)); break;
                case "n\u007f": PetPacketHandlers.sendRaceList(socketIndex, PetWire.raceListRequest(payload)); break;
                case "ny": PetPacketHandlers.sendStatus(socketIndex, PetWire.petIdRequest(payload, "ny")); break;
                case "nx": PetPacketHandlers.sendInventory(socketIndex); break;
                case "nz": PetPacketHandlers.placeInRoom(socketIndex, PetWire.roomPlacementRequest(payload)); break;
                case "p`":
                case "rt": PetPacketHandlers.sendPackagePreview(socketIndex, PetWire.packagePreviewRequest(payload)); break;
                case "n~": PetPacketHandlers.placeFromPackage(socketIndex, PetWire.packagePlacementRequest(payload)); break;
                case "n|": PetPacketHandlers.sendCommandListForTarget(socketIndex, PetWire.petIdRequest(payload, "n|")); break;
                case "n{": PetPacketHandlers.performCommand(socketIndex, PetWire.commandRequest(payload)); break;
                case "n}": PetPacketHandlers.scratch(socketIndex, PetWire.petIdRequest(payload, "n}")); break;
                case "E[": RoomPacketHandlers.deleteRoomEvent(socketIndex); break;
                case "A_": RoomPacketHandlers.kickRoomUser(socketIndex, RoomWire.roomUserTargetRequest(payload, "A_")); break;
                case "E@": RoomPacketHandlers.banRoomUser(socketIndex, RoomWire.roomUserTargetRequest(payload, "E@")); break;
                case "DE": RoomPacketHandlers.rateCurrentRoom(socketIndex, RoomWire.roomRatingRequest(payload)); break;
                case "D\u007f": RoomPacketHandlers.revokeRoomRightByName(socketIndex, RoomWire.roomRightNameRequest(payload, "D\u007f")); break;
                case "EB": RoomPacketHandlers.revokeRoomRightByTargetName(socketIndex, RoomWire.roomRightNameRequest(payload, "EB")); break;
                case "Bw": FurniturePacketHandlers.redeemCreditFurniture(socketIndex, FurnitureWire.creditFurnitureRequest(payload)); break;
                case "Aa": RoomPacketHandlers.revokeRoomRights(socketIndex, RoomWire.roomRightRevokeRequest(payload)); break;
                case "B[": RoomPacketHandlers.revokeAllRoomRights(socketIndex); break;
                case "@W": RoomPacketHandlers.deleteCurrentRoom(socketIndex, RoomWire.deleteRoomRequest(payload)); break;
                case "Es": SocialPacketHandlers.giveRespect(socketIndex, SocialWire.userIdRequest(payload, "Es")); break;
                case "FD": RoomPacketHandlers.sendOfficialRoomModel(socketIndex, RoomWire.roomIdRequest(payload, "FD")); break;
                case "A`": RoomPacketHandlers.grantRoomRight(socketIndex, RoomWire.roomRightGrantRequest(payload)); break;
                case "AT": FurniturePacketHandlers.updateStickyNote(socketIndex, FurnitureWire.stickyNoteUpdate(payload)); break;
                case "AS": FurniturePacketHandlers.sendStickyNote(socketIndex, FurnitureWire.stickyFurnitureRequest(payload)); break;
                case "AU": FurniturePacketHandlers.deleteStickyNote(socketIndex, FurnitureWire.stickyFurnitureRequest(payload)); break;
                case "A~":
                case "CW":
                case "Cf": break;
                case "FA": NavigatorPacketHandlers.sendSingleRoomInfo(socketIndex, NavigatorWire.singleRoomRequest(payload)); break;
                case "FI": FurniturePacketHandlers.toggleWallFurnitureState(socketIndex, FurnitureWire.stickyFurnitureRequest(payload)); break;
                case "AN": FurniturePacketHandlers.openPresent(socketIndex, FurnitureWire.stickyFurnitureRequest(payload)); break;
                case "Aq":
                case "AE":
                case "FS":
                case "AF":
                    SocketDelivery.sendToSocket(socketIndex,
                        StringUtils.text(AppConfigState.instance().settingsCache().valueOrDefault("com.client.park.infobus.theme.title", "AQ")) + '\2');
                    break;
                case "oL": UserWire.guideInviteRequest(payload).userId(); break;
                default:
                    if (LifecycleState.instance().runtimeState().debugLoggingEnabled()) {
                        Console.logSourceLine(payload, "UNHANDLED -- index: " + socketIndex, 255L);
                    }
                    break;
            }
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }
}
