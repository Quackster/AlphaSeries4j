package com.alphaseries.game.poll;

import com.alphaseries.util.StringUtils;

public record PollAnswerSubmission(
    long pollId,
    long questionId,
    long answerValue,
    String answerText,
    boolean valid
) {
    public PollAnswerSubmission {
        answerText = StringUtils.text(answerText);
    }
}
