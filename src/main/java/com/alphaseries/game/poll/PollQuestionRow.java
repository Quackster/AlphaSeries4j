package com.alphaseries.game.poll;

import java.util.List;

public record PollQuestionRow(long id, String question, long type, List<PollAnswerRow> answers) {
    public PollQuestionRow {
        answers = answers == null ? List.of() : List.copyOf(answers);
    }
}
