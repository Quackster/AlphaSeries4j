package com.alphaseries.messages.outgoing;

import com.alphaseries.game.poll.PollAnswerRow;
import com.alphaseries.game.poll.PollDefinition;
import com.alphaseries.game.poll.PollHeader;
import com.alphaseries.game.poll.PollQuestionRow;
import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public static String poll(String pollRow, String questionRows, Map<Long, String> answerRowsByQuestionId) {
        String[] pollFields = StringUtils.text(pollRow).split("\t", -1);
        if (pollFields.length < 3) {
            return "";
        }
        long pollId = NumberUtils.parseLong(pollFields[0]);
        List<PollQuestionRow> questions = new ArrayList<>();
        for (String questionRow : StringUtils.text(questionRows).split("\r", -1)) {
            if (!questionRow.isEmpty()) {
                String[] questionFields = questionRow.split("\t", -1);
                if (questionFields.length >= 3) {
                    long questionId = NumberUtils.parseLong(questionFields[0]);
                    String questionText = questionFields[1];
                    long questionType = NumberUtils.parseLong(questionFields[2]);
                    List<PollAnswerRow> answers = new ArrayList<>();
                    String answerRows = answerRowsByQuestionId != null ? answerRowsByQuestionId.get(questionId) : null;
                    for (String answerRow : StringUtils.text(answerRows).split("\r", -1)) {
                        if (!answerRow.isEmpty()) {
                            String[] answerFields = answerRow.split("\t", -1);
                            if (answerFields.length >= 3) {
                                answers.add(new PollAnswerRow(
                                    NumberUtils.parseLong(answerFields[0]),
                                    NumberUtils.parseLong(answerFields[1]),
                                    answerFields[2]));
                            }
                        }
                    }
                    questions.add(new PollQuestionRow(questionId, questionText, questionType, answers));
                }
            }
        }
        return poll(new PollDefinition(new PollHeader(pollId, pollFields[1], pollFields[2]), questions));
    }

}
