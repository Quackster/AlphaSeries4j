package com.alphaseries.dao.mysql;

import java.util.function.Function;

import com.alphaseries.db.Database;
import com.alphaseries.db.MySQL;

public final class DaoProvider {
    private DaoProvider() {
    }

    public static StaffModerationDao staffModerationDao() {
        return dao(StaffModerationDao::new);
    }

    public static HelpDao helpDao() {
        return dao(HelpDao::new);
    }

    public static UserDao userDao() {
        return dao(UserDao::new);
    }

    public static ClubDao clubDao() {
        return dao(ClubDao::new);
    }

    public static CatalogDao catalogDao() {
        return dao(CatalogDao::new);
    }

    public static RoomDao roomDao() {
        return dao(RoomDao::new);
    }

    public static FurnitureDao furnitureDao() {
        return dao(FurnitureDao::new);
    }

    public static PackageDao packageDao() {
        return dao(PackageDao::new);
    }

    public static VoucherDao voucherDao() {
        return dao(VoucherDao::new);
    }

    public static PollDao pollDao() {
        return dao(PollDao::new);
    }

    public static JukeboxDao jukeboxDao() {
        return dao(JukeboxDao::new);
    }

    public static QuestDao questDao() {
        return dao(QuestDao::new);
    }

    public static RecyclerDao recyclerDao() {
        return dao(RecyclerDao::new);
    }

    public static BotDao botDao() {
        return dao(BotDao::new);
    }

    public static TradeDao tradeDao() {
        return dao(TradeDao::new);
    }

    public static MessengerDao messengerDao() {
        return dao(MessengerDao::new);
    }

    private static <T> T dao(Function<Database, T> factory) {
        Database database = MySQL.configuredDatabase();
        return database == null ? null : factory.apply(database);
    }
}
