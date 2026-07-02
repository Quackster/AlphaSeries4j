package com.alphaseries.game.poll;

import com.alphaseries.dao.mysql.PollDao;
import com.alphaseries.dao.mysql.RoomDao;
import com.alphaseries.dao.mysql.UserDao;
import com.alphaseries.game.session.SessionState;
import com.alphaseries.messages.outgoing.PollPayloads;

public final class PollLookups {
    private PollLookups() {
    }

    public record RoomRequest(long userId, long roomId) {
        public boolean valid() {
            return userId > 0L && roomId > 0L;
        }
    }

    public static RoomRequest roomRequest(int socketIndex, UserDao users, RoomDao rooms) {
        if (socketIndex <= 0) {
            return new RoomRequest(0L, 0L);
        }
        long userId = SessionState.instance().sessionUserIdBySocket(socketIndex);
        if (userId <= 0L && users != null) {
            try {
                userId = users.userIdBySocket(socketIndex);
            } catch (Exception ignored) {
                userId = 0L;
            }
        }
        long roomId = SessionState.instance().sessionCacheLong(socketIndex, 1);
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
        return new RoomRequest(userId, roomId);
    }

    public static String promptPayload(long userId, long roomId, PollDao polls) {
        if (userId <= 0L || roomId <= 0L || polls == null) {
            return "";
        }
        try {
            PollPrompt pollPrompt = polls.activePrompt(roomId).orElse(null);
            if (pollPrompt == null || pollPrompt.id() <= 0L) {
                return "";
            }
            if (polls.hasExited(userId, pollPrompt.id())) {
                return "";
            }
            if (polls.hasAnswered(userId, pollPrompt.id())) {
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
    public static boolean recordExit(RoomRequest request, long pollId, PollDao polls) {
        if (request == null || !request.valid()) {
            return false;
        }
        try {
            long userId = request.userId();
            long roomId = request.roomId();
            if (pollId <= 0L || userId <= 0L || roomId <= 0L || polls == null
                || polls.pollHeader(pollId, roomId).isEmpty()) {
                return false;
            }
            polls.recordPollExit(userId, pollId);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    /**
     * Original function: Proc_6_200_7D5770.
     */
    public static boolean submitAnswer(RoomRequest request, PollAnswerSubmission submission, PollDao polls) {
        if (request == null || !request.valid()) {
            return false;
        }
        try {
            long userId = request.userId();
            long roomId = request.roomId();
            if (!submission.valid() || userId <= 0L || roomId <= 0L || polls == null
                || polls.pollHeader(submission.pollId(), roomId).isEmpty()) {
                return false;
            }
            polls.recordPollAnswer(submission.pollId(), submission.questionId(), submission.answerText(), userId);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    /**
     * Original function: Proc_6_201_7D5AC0.
     */
    public static String livePollPayload(RoomRequest request, long pollId, PollDao polls) {
        if (request == null || !request.valid()) {
            return "";
        }
        try {
            if (pollId <= 0L || request.userId() <= 0L || request.roomId() <= 0L || polls == null) {
                return "";
            }
            PollDefinition poll = polls.pollDefinition(pollId, request.roomId()).orElse(null);
            return PollPayloads.poll(poll);
        } catch (Exception ignored) {
            return "";
        }
    }
}
