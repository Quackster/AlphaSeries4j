package com.alphaseries.game.user;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.Locale;

public final class UserValidation {
    private UserValidation() {
    }

    /**
     * Original function: Proc_6_16_6E2320.
     * Original function: Proc_6_17_6E48D0.
     */
    public static boolean isValidWardrobeFigure(String figureText, String genderText) {
        return isValidWardrobeFigure(figureText, genderText, "");
    }

    /**
     * Original function: Proc_6_16_6E2320.
     * Original function: Proc_6_17_6E48D0.
     */
    public static boolean isValidWardrobeFigure(String figureText, String genderText, String figureData) {
        String figure = StringUtils.text(figureText);
        String gender = StringUtils.text(genderText).toUpperCase(Locale.ROOT);
        if (figure.isEmpty() || figure.length() > 255 || figure.indexOf('\'') >= 0 || figure.indexOf('"') >= 0) {
            return false;
        }

        String allowedTypes = ";lg;ha;wa;hr;ch;sh;cc;ea;he;ca;hd;fa;cp;";
        for (String part : StringUtils.delimitedFields(figure, '.')) {
            if (!part.isEmpty()) {
                StringUtils.IndexedFields fields = StringUtils.indexedFields(part, '-');
                String figureType = fields.text(0).toLowerCase(Locale.ROOT);
                String setId = fields.text(1);
                if (figureType.isEmpty() || setId.isEmpty()) {
                    return false;
                }
                if (!allowedTypes.contains(";" + figureType + ";") || NumberUtils.parseLong(setId) <= 0L) {
                    return false;
                }

                if (!StringUtils.text(figureData).isEmpty()) {
                    String lowerFigureData = figureData.toLowerCase(Locale.ROOT);
                    String setTypeMarker = "<settype type=\"" + figureType + "\"";
                    int setTypeStart = lowerFigureData.indexOf(setTypeMarker.toLowerCase(Locale.ROOT));
                    if (setTypeStart < 0) {
                        return false;
                    }
                    int setTypeEnd = lowerFigureData.indexOf("</settype>", setTypeStart);
                    if (setTypeEnd < 0) {
                        return false;
                    }
                    String setTypeXml = figureData.substring(setTypeStart, setTypeEnd);
                    String setMarker = "<set id=\"" + NumberUtils.parseLong(setId) + "\"";
                    if (!setTypeXml.toLowerCase(Locale.ROOT).contains(setMarker.toLowerCase(Locale.ROOT))) {
                        return false;
                    }
                    if (!figureSetAllowsGender(setTypeXml, setMarker, gender)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Original function: Proc_6_16_6E2320.
     * Original function: Proc_6_17_6E48D0.
     */
    public static boolean figureSetAllowsGender(String setTypeXml, String setMarker, String genderText) {
        String setType = StringUtils.text(setTypeXml);
        String marker = StringUtils.text(setMarker);
        int setStart = setType.toLowerCase(Locale.ROOT).indexOf(marker.toLowerCase(Locale.ROOT));
        if (setStart < 0) {
            return false;
        }
        String lowerSetType = setType.toLowerCase(Locale.ROOT);
        int setEnd = lowerSetType.indexOf("</set>", setStart);
        if (setEnd < 0) {
            setEnd = lowerSetType.indexOf("/>", setStart);
        }
        if (setEnd < 0) {
            setEnd = setType.length();
        }
        String setXml = setType.substring(setStart, setEnd);
        int genderStart = setXml.toLowerCase(Locale.ROOT).indexOf("gender=\"");
        if (genderStart < 0) {
            return true;
        }
        if (genderStart + 8 >= setXml.length()) {
            return false;
        }
        String genderValue = setXml.substring(genderStart + 8, genderStart + 9).toUpperCase(Locale.ROOT);
        String gender = StringUtils.text(genderText).toUpperCase(Locale.ROOT);
        return "U".equals(genderValue) || genderValue.equals(gender);
    }

    /**
     * Original function: Proc_6_36_711480.
     * Original function: Proc_6_37_7134D0.
     */
    public static long avatarNameValidationCode(String candidateName, String currentName, long existingCount) {
        String candidate = StringUtils.text(candidateName).trim();
        if (candidate.length() < 3) {
            return 2L;
        }
        if (candidate.length() > 14) {
            return 1L;
        }
        String upper = candidate.toUpperCase(Locale.ROOT);
        if (upper.startsWith("MOD-") || upper.startsWith("VIP-")) {
            return 2L;
        }
        for (int index = 0; index < candidate.length(); index++) {
            char ch = candidate.charAt(index);
            boolean allowed = (ch >= 'A' && ch <= 'Z')
                || (ch >= 'a' && ch <= 'z')
                || (ch >= '0' && ch <= '9')
                || ch == '-'
                || ch == '_';
            if (!allowed) {
                return 2L;
            }
        }
        if (candidate.equalsIgnoreCase(StringUtils.text(currentName))) {
            return 0L;
        }
        return existingCount > 0L ? 3L : 0L;
    }
}
