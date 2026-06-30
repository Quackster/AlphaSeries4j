package com.alphaseries.messages.outgoing;

import com.alphaseries.game.poll.PollAnswerRow;
import com.alphaseries.game.poll.PollDefinition;
import com.alphaseries.game.poll.PollHeader;
import com.alphaseries.game.poll.PollQuestionRow;
import com.alphaseries.protocol.PacketBuilder;

public final class PollPayloads {
    private PollPayloads() {
    }

    public static String poll(PollDefinition poll) {
        if (poll == null || poll.header() == null) {
            return "";
        }
        PollHeader header = poll.header();
        PacketBuilder questionPayload = PacketBuilder.create();
        long questionCount = 0L;
        for (PollQuestionRow question : poll.questions()) {
            if (question != null) {
                PacketBuilder answerPayload = PacketBuilder.create();
                long answerCount = 0L;
                for (PollAnswerRow answer : question.answers()) {
                    if (answer != null) {
                        answerPayload.appendString(answer.caption());
                        answerCount++;
                    }
                }
                questionCount++;
                questionPayload
                    .appendInt(question.id())
                    .appendInt(questionCount)
                    .appendInt(question.type())
                    .appendString(question.question())
                    .appendInt(answerCount)
                    .appendInt(0L)
                    .appendInt(answerCount)
                    .appendRaw(answerPayload);
            }
        }
        return PacketBuilder.message("D}")
            .appendInt(header.id())
            .appendString(header.title())
            .appendString(header.thanks())
            .appendInt(questionCount)
            .appendRaw(questionPayload)
            .build();
    }

}
