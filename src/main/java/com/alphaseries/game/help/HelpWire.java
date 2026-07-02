package com.alphaseries.game.help;

import com.alphaseries.protocol.WireEncoding;
import com.alphaseries.protocol.WireReader;
import com.alphaseries.protocol.WireRequests;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

public final class HelpWire {
    private HelpWire() {
    }

    public record FaqIdRequest(long faqId) {
    }

    public record CategoryFaqRequest(long categoryId) {
    }

    public record FaqSearchRequest(String searchText) {
    }

    /**
     * Original function: Proc_6_35_70F630.
     */
    public static CategoryFaqRequest categoryFaqRequest(String packetPayload, String prefix) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, prefix);
        long categoryId = requestPayload.isEmpty() ? 0L : WireReader.readLong(requestPayload, new WireReader.Offset(1));
        return new CategoryFaqRequest(categoryId);
    }

    /**
     * Original function: Proc_6_36_70F7B0.
     */
    public static FaqSearchRequest faqSearchRequest(String packetPayload, String prefix) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, prefix);
        String searchText = requestPayload.isEmpty()
            ? ""
            : StringUtils.sqlEscapedText(WireEncoding.readBase64LengthString(requestPayload));
        return new FaqSearchRequest(searchText);
    }

    /**
     * Original function: Proc_6_37_70FC20.
     */
    public static FaqIdRequest faqIdRequest(String packetPayload, String prefix) {
        String requestPayload = WireRequests.stripPrefix(packetPayload, prefix);
        long faqId = NumberUtils.parseLong(WireEncoding.readVl64LengthString(requestPayload));
        if (faqId <= 0L) {
            faqId = WireReader.readLong(requestPayload, new WireReader.Offset(1));
        }
        return new FaqIdRequest(faqId);
    }
}
