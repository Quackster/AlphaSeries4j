package com.alphaseries.game.poll;

import com.alphaseries.protocol.WireEncoding;
import com.alphaseries.protocol.WireReader;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

public final class PollWire {
    private PollWire() {
    }

    public static long idFromWire(String packetPayload, String prefix) {
        String requestPayload = requestPayload(packetPayload, prefix);
        WireReader.Offset offset = new WireReader.Offset(1);
        long value = WireReader.readLong(requestPayload, offset);
        if (value <= 0L) {
            value = NumberUtils.parseLong(WireEncoding.readVl64LengthString(requestPayload));
        }
        return value;
    }

    public static PollAnswerSubmission answerFromWire(String packetPayload, String prefix) {
        String requestPayload = requestPayload(packetPayload, prefix);
        WireReader.Offset offset = new WireReader.Offset(1);
        long pollId = WireReader.readLong(requestPayload, offset);
        long questionId = WireReader.readLong(requestPayload, offset);
        long answerValue = WireReader.readLong(requestPayload, offset);
        String answerText = StringUtils.singleLineText(WireReader.readString(requestPayload, offset));
        if (pollId <= 0L) {
            pollId = NumberUtils.parseLong(WireEncoding.readVl64LengthString(requestPayload));
        }
        if (answerText.isEmpty() && answerValue > 0L) {
            answerText = String.valueOf(answerValue);
        }
        return new PollAnswerSubmission(pollId, questionId, answerValue, answerText, pollId > 0L && questionId > 0L);
    }

    private static String requestPayload(String packetPayload, String prefix) {
        String requestPayload = StringUtils.text(packetPayload);
        if (!StringUtils.text(prefix).isEmpty() && requestPayload.startsWith(prefix)) {
            requestPayload = requestPayload.substring(prefix.length());
        }
        return requestPayload;
    }

}
