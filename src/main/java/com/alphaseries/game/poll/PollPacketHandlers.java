package com.alphaseries.game.poll;

import com.alphaseries.dao.mysql.DaoProvider;
import com.alphaseries.dao.mysql.PollDao;
import com.alphaseries.dao.mysql.RoomDao;
import com.alphaseries.dao.mysql.UserDao;
import com.alphaseries.server.runtime.SocketDelivery;

public final class PollPacketHandlers {
    private PollPacketHandlers() {
    }

    /**
     * Original function: Proc_6_199_7D54E0.
     */
    public static String recordExit(int socketIndex, long pollId) {
        try {
            PollLookups.RoomRequest request = PollLookups.roomRequest(socketIndex, userDao(), roomDao());
            PollLookups.recordExit(request, pollId, pollDao());
            return "";
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_200_7D5770.
     */
    public static String submitAnswer(int socketIndex, PollAnswerSubmission answer) {
        try {
            PollLookups.RoomRequest request = PollLookups.roomRequest(socketIndex, userDao(), roomDao());
            PollLookups.submitAnswer(request, answer, pollDao());
            return "";
        } catch (Exception ignored) {
            return "";
        }
    }

    /**
     * Original function: Proc_6_201_7D5AC0.
     */
    public static String sendLivePoll(int socketIndex, long pollId) {
        try {
            PollLookups.RoomRequest request = PollLookups.roomRequest(socketIndex, userDao(), roomDao());
            String payload = PollLookups.livePollPayload(request, pollId, pollDao());
            if (!payload.isEmpty()) {
                SocketDelivery.sendToSocket(socketIndex, payload);
            }
            return payload;
        } catch (Exception ignored) {
            return "";
        }
    }

    private static UserDao userDao() {
        return DaoProvider.userDao();
    }

    private static RoomDao roomDao() {
        return DaoProvider.roomDao();
    }

    private static PollDao pollDao() {
        return DaoProvider.pollDao();
    }
}
