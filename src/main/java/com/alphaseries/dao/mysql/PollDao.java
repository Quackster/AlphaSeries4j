package com.alphaseries.dao.mysql;

import com.alphaseries.db.Database;
import com.alphaseries.game.poll.PollAnswerRow;
import com.alphaseries.game.poll.PollDefinition;
import com.alphaseries.game.poll.PollHeader;
import com.alphaseries.game.poll.PollPrompt;
import com.alphaseries.game.poll.PollQuestionRow;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class PollDao {
    private final Database database;

    public PollDao(Database database) {
        this.database = database;
    }

    public Optional<PollHeader> pollHeader(long pollId, long roomId) throws SQLException {
        return database.queryOne(
            "SELECT id,description_title,description_thanks FROM poll WHERE id=? AND id_room=? LIMIT 1",
            resultSet -> new PollHeader(resultSet.getLong(1), resultSet.getString(2), resultSet.getString(3)),
            pollId,
            roomId);
    }

    public Optional<PollDefinition> pollDefinition(long pollId, long roomId) throws SQLException {
        Optional<PollHeader> pollHeader = pollHeader(pollId, roomId);
        if (pollHeader.isEmpty()) {
            return Optional.empty();
        }
        List<PollQuestionRow> questions = new ArrayList<>();
        for (PollQuestionRow question : pollQuestions(pollId)) {
            questions.add(new PollQuestionRow(
                question.id(),
                question.question(),
                question.type(),
                pollAnswers(question.id())));
        }
        return Optional.of(new PollDefinition(pollHeader.get(), questions));
    }

    public List<PollQuestionRow> pollQuestions(long pollId) throws SQLException {
        return database.query(
            "SELECT id,description_question,id_type FROM poll_questions WHERE id_poll=? LIMIT 50",
            resultSet -> new PollQuestionRow(resultSet.getLong(1), resultSet.getString(2), resultSet.getLong(3), List.of()),
            pollId);
    }

    public List<PollAnswerRow> pollAnswers(long questionId) throws SQLException {
        return database.query(
            "SELECT id,id_question,caption FROM poll_answers WHERE id_question=? LIMIT 5",
            resultSet -> new PollAnswerRow(resultSet.getLong(1), resultSet.getLong(2), resultSet.getString(3)),
            questionId);
    }

    public int recordPollExit(long userId, long pollId) throws SQLException {
        return database.execute("INSERT INTO poll_exit(id_user,id_poll) VALUES(?,?)", userId, pollId);
    }

    public int recordPollAnswer(long pollId, long questionId, String answerText, long userId) throws SQLException {
        return database.execute(
            "INSERT INTO poll_results(id_poll,id_question,message_answer,id_user,timestamp) VALUES(?,?,?,?,UNIX_TIMESTAMP())",
            pollId,
            questionId,
            answerText,
            userId);
    }

    public Optional<PollPrompt> activePrompt(long roomId) throws SQLException {
        return database.queryOne(
            "SELECT id,description_title FROM poll WHERE id_room=? AND timestamp_hide>UNIX_TIMESTAMP() LIMIT 1",
            resultSet -> new PollPrompt(resultSet.getLong(1), resultSet.getString(2)),
            roomId);
    }

    public boolean hasExited(long userId, long pollId) throws SQLException {
        return database.queryOne(
            "SELECT id_user FROM poll_exit WHERE id_user=? AND id_poll=? LIMIT 1",
            resultSet -> resultSet.getLong(1),
            userId,
            pollId)
            .isPresent();
    }

    public boolean hasAnswered(long userId, long pollId) throws SQLException {
        return database.queryOne(
            "SELECT id FROM poll_results WHERE id_user=? AND id_poll=? LIMIT 1",
            resultSet -> resultSet.getLong(1),
            userId,
            pollId)
            .isPresent();
    }
}
