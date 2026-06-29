package com.alphaseries.game.poll;

import java.util.List;

public record PollDefinition(PollHeader header, List<PollQuestionRow> questions) {
    public PollDefinition {
        questions = questions == null ? List.of() : List.copyOf(questions);
    }
}
