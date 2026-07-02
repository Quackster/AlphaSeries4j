package com.alphaseries.game.help;

import com.alphaseries.dao.mysql.DaoProvider;
import com.alphaseries.dao.mysql.HelpDao;
import com.alphaseries.messages.outgoing.HelpPayloads;
import com.alphaseries.server.runtime.SocketDelivery;

import java.util.List;
import java.util.function.IntConsumer;

public final class HelpPacketHandlers {
    private HelpPacketHandlers() {
    }

    /**
     * Original function: Proc_6_33_70F4F0.
     */
    public static void sendImportantFaqs(int socketIndex) {
        try {
            SocketDelivery.sendToSocket(socketIndex, HelpPayloads.importantFaqs(helpCenterCache()));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_34_70F590.
     */
    public static void sendCategories(int socketIndex) {
        try {
            SocketDelivery.sendToSocket(socketIndex, HelpPayloads.categories(helpCenterCache()));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_35_70F630.
     */
    public static void sendCategoryFaqs(int socketIndex, HelpWire.CategoryFaqRequest request) {
        try {
            long categoryId = request.categoryId();
            SocketDelivery.sendToSocket(socketIndex, HelpPayloads.categoryFaqs(helpCenterCache(), categoryId));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_36_70F7B0.
     */
    public static void searchFaqs(int socketIndex, HelpWire.FaqSearchRequest request, IntConsumer disconnectSocket) {
        try {
            String searchText = request.searchText();
            if (searchText.length() < 3) {
                if (disconnectSocket != null) {
                    disconnectSocket.accept(socketIndex);
                }
                return;
            }
            HelpDao helpDao = helpDao();
            if (helpDao == null) {
                return;
            }
            List<HelpDao.FaqNameRow> rows = helpDao.searchFaqs(searchText);
            SocketDelivery.sendToSocket(socketIndex, HelpPayloads.searchResults(rows));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    /**
     * Original function: Proc_6_37_70FC20.
     */
    public static void sendDescription(int socketIndex, HelpWire.FaqIdRequest request) {
        try {
            long faqId = request.faqId();
            SocketDelivery.sendToSocket(socketIndex, HelpPayloads.description(helpCenterCache(), faqId));
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
        }
    }

    private static HelpCenterCache helpCenterCache() {
        return HelpCenterState.instance().cache();
    }

    private static HelpDao helpDao() {
        return DaoProvider.helpDao();
    }
}
