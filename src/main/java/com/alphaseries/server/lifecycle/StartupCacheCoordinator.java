package com.alphaseries.server.lifecycle;

import com.alphaseries.config.AppSettingsBootCache;
import com.alphaseries.config.FiguredataBootCache;
import com.alphaseries.game.achievement.AchievementBootCache;
import com.alphaseries.game.advertising.AdvertisingBootCache;
import com.alphaseries.game.catalog.CatalogStartupBootCache;
import com.alphaseries.game.chat.ChatBootCache;
import com.alphaseries.game.help.HelpCenterBootCache;
import com.alphaseries.game.navigator.NavigatorBootCache;
import com.alphaseries.game.pet.PetBootCache;
import com.alphaseries.game.room.RoomEventBootCache;

public final class StartupCacheCoordinator {
    private StartupCacheCoordinator() {
    }

    /**
     * Original function: Proc_1_3_6BEBA0.
     */
    public static void initializeStartupCaches() {
        BootLog.initializeBootLogFiles();
        BootLog.runTimed("Empfohlene Räume im Cache gespeichert", NavigatorBootCache::loadRecommendedRoomsCache);
        BootLog.runTimed("Mögliche Badgevergabe im Cache gespeichert", AppSettingsBootCache::loadPermissionMatrixCache);
        BootLog.runTimed("Haustiere im Cache gespeichert", PetBootCache::loadPetLevelAndCommandCache);
        BootLog.runTimed("Figuredata im Cache gespeichert", FiguredataBootCache::writeFiguredataCache);
        BootLog.runTimed("Server Einstellungen im Cache gespeichert", AppSettingsBootCache::loadServerSettingsCache);
        BootLog.runTimed("Event Kategorien im Cache gespeichert", RoomEventBootCache::loadRoomEventLocalesCache);
        BootLog.runTimed("Navigator Kategorien im Cache gespeichert", () -> {
            NavigatorBootCache.loadRoomCategoryRowsCache();
            NavigatorBootCache.loadRoomCategoryPayloadCache();
        });
        BootLog.runTimed("Raumwerbung im Cache gespeichert", AdvertisingBootCache::loadVisitRoomAdsCache);
        BootLog.runTimed("Bonussystem im Cache gespeichert", AchievementBootCache::loadBonusSystemCache);
        BootLog.runTimed("Katalog im Cache gespeichert", CatalogStartupBootCache::loadCatalogStartupCache);
        BootLog.runTimed("Chat Einstellungen im Cache gespeichert", ChatBootCache::buildChatSettingsCache);
        BootLog.runTimed("Haustierrassen im Cache gespeichert", PetBootCache::loadPetRaceCache);
        BootLog.runTimed("FAQ im Cache gespeichert", () -> {
            HelpCenterBootCache.loadImportantFaqCache();
            HelpCenterBootCache.loadFaqCategoryCache();
            HelpCenterBootCache.loadFaqDescriptionCache();
        });
        BootLog.runTimed("Aktive Serverdaten zurückgesetzt", ServerStartupMaintenance::resetActiveServerData);
    }

    /**
     * Original function: Proc_1_4_6C4F00.
     */
    public static void refreshDynamicStartupCaches() {
        RoomEventBootCache.loadRoomEventLocalesCache();
        AppSettingsBootCache.loadServerSettingsCache();
        HelpCenterBootCache.loadImportantFaqCache();
        HelpCenterBootCache.loadFaqCategoryCache();
        HelpCenterBootCache.loadFaqDescriptionCache();
        AdvertisingBootCache.loadVisitRoomAdsCache();
        NavigatorBootCache.loadRoomCategoryRowsCache();
        NavigatorBootCache.loadRoomCategoryPayloadCache();
        NavigatorBootCache.loadRecommendedRoomsCache();
    }
}
