package com.alphaseries.game.poll;

import com.alphaseries.dao.mysql.PollDao;
import com.alphaseries.dao.mysql.RoomDao;
import com.alphaseries.dao.mysql.UserDao;
import com.alphaseries.game.session.SessionState;
import com.alphaseries.messages.outgoing.PollPayloads;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

public final class PollLookups {
    private PollLookups() {
    }

    public record RoomRequest(String userId, long roomId) {
        public boolean valid() {
            return NumberUtils.parseLong(userId) > 0L && roomId > 0L;
        }
    }

    public static RoomRequest roomRequest(int socketIndex, UserDao users, RoomDao rooms) {
        if (socketIndex <= 0) {
            return new RoomRequest("", 0L);
        }
        long userId = SessionState.instance().sessionUserIdBySocket(socketIndex);
        if (userId <= 0L && users != null) {
            try {
                userId = users.userIdBySocket(socketIndex);
            } catch (Exception ignored) {
                userId = 0L;
            }
        }
        long roomId = SessionState.instance().sessionCacheLong(String.valueOf(socketIndex), 1);
        if (roomId <= 0L && userId > 0L && rooms != null) {
            try {
                roomId = rooms.currentRoomIdByUser(userId);
            } catch (Exception ignored) {
                roomId = 0L;
            }
        }
        if (roomId <= 0L && rooms != null) {
            try {
                roomId = rooms.roomIdBySlot(socketIndex);
            } catch (Exception ignored) {
                roomId = 0L;
            }
        }
        return new RoomRequest(userId > 0L ? String.valueOf(userId) : "", roomId);
    }

    public static String promptPayload(String userId, long roomId, PollDao polls) {
        if (StringUtils.text(userId).isEmpty() || roomId <= 0L || polls == null) {
            return "";
        }
        long userIdValue = NumberUtils.parseLong(userId);
        if (userIdValue <= 0L) {
            return "";
        }
        try {
            PollPrompt pollPrompt = polls.activePrompt(roomId).orElse(null);
            if (pollPrompt == null || pollPrompt.id() <= 0L) {
                return "";
            }
            if (polls.hasExited(userIdValue, pollPrompt.id())) {
                return "";
            }
            if (polls.hasAnswered(userIdValue, pollPrompt.id())) {
                return "";
            }
            return PollPayloads.prompt(pollPrompt);
        } catch (Exception ignored) {
            // VB6 source suppresses handler failures.
            return "";
        }
    }

    /**
     * Original function: Proc_6_199_7D54E0.
     */
    public static boolean recordExit(String userId, long roomId, long pollId, PollDao polls) {
        try {
            long userIdValue = NumberUtils.parseLong(userId);
            if (pollId <= 0L || userIdValue <= 0L || roomId <= 0L || polls == null
                || polls.pollHeader(pollId, roomId).isEmpty()) {
                return false;
            }
            polls.recordPollExit(userIdValue, pollId);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static boolean recordExit(RoomRequest request, long pollId, PollDao polls) {
        if (request == null || !request.valid()) {
            return false;
        }
        return recordExit(request.userId(), request.roomId(), pollId, polls);
    }

    /**
     * Original function: Proc_6_200_7D5770.
     */
    public static boolean submitAnswer(String userId, long roomId, PollAnswerSubmission submission, PollDao polls) {
        try {
            long userIdValue = NumberUtils.parseLong(userId);
            if (!submission.valid() || userIdValue <= 0L || roomId <= 0L || polls == null
                || polls.pollHeader(submission.pollId(), roomId).isEmpty()) {
                return false;
            }
            polls.recordPollAnswer(submission.pollId(), submission.questionId(), submission.answerText(), userIdValue);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static boolean submitAnswer(RoomRequest request, PollAnswerSubmission submission, PollDao polls) {
        if (request == null || !request.valid()) {
            return false;
        }
        return submitAnswer(request.userId(), request.roomId(), submission, polls);
    }

    /**
     * Original function: Proc_6_201_7D5AC0.
     */
    public static String livePollPayload(String userId, long roomId, long pollId, PollDao polls) {
        try {
            if (pollId <= 0L || NumberUtils.parseLong(userId) <= 0L || roomId <= 0L || polls == null) {
                return "";
            }
            PollDefinition poll = polls.pollDefinition(pollId, roomId).orElse(null);
            return PollPayloads.poll(poll);
        } catch (Exception ignored) {
            return "";
        }
    }

    public static String livePollPayload(RoomRequest request, long pollId, PollDao polls) {
        if (request == null || !request.valid()) {
            return "";
        }
        return livePollPayload(request.userId(), request.roomId(), pollId, polls);
    }
}
