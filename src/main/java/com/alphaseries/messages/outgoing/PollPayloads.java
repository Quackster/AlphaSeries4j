package com.alphaseries.messages.outgoing;

import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.protocol.WireEncoding;

import java.util.Map;

public final class PollPayloads {
    private PollPayloads() {
    }

    public static String poll(String pollRow, String questionRows, Map<Long, String> answerRowsByQuestionId) {
        String[] pollFields = text(pollRow).split("\t", -1);
        if (pollFields.length < 3) {
            return "";
        }
        long pollId = number(pollFields[0]);
        PacketBuilder questionPayload = PacketBuilder.create();
        long questionCount = 0L;
        for (String questionRow : text(questionRows).split("\r", -1)) {
            if (!questionRow.isEmpty()) {
                String[] questionFields = questionRow.split("\t", -1);
                if (questionFields.length >= 3) {
                    long questionId = number(questionFields[0]);
                    String questionText = questionFields[1];
                    long questionType = number(questionFields[2]);
                    PacketBuilder answerPayload = PacketBuilder.create();
                    long answerCount = 0L;
                    String answerRows = answerRowsByQuestionId != null ? answerRowsByQuestionId.get(questionId) : null;
                    for (String answerRow : text(answerRows).split("\r", -1)) {
                        if (!answerRow.isEmpty()) {
                            String[] answerFields = answerRow.split("\t", -1);
                            if (answerFields.length >= 3) {
                                answerPayload.appendString(answerFields[2]);
                                answerCount++;
                            }
                        }
                    }
                    questionCount++;
                    questionPayload
                        .appendInt(questionId)
                        .appendInt(questionCount)
                        .appendInt(questionType)
                        .appendString(questionText)
                        .appendInt(answerCount)
                        .appendInt(0L)
                        .appendInt(answerCount)
                        .appendRaw(answerPayload);
                }
            }
        }
        return PacketBuilder.message("D}")
            .appendInt(pollId)
            .appendString(pollFields[1])
            .appendString(pollFields[2])
            .appendInt(questionCount)
            .appendRaw(questionPayload)
            .build();
    }

    private static long number(Object value) {
        return WireEncoding.parseLeadingLong(value);
    }

    private static String text(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}
