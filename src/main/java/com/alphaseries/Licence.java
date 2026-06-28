package com.alphaseries;

import com.alphaseries.vb.Vb;

import java.time.LocalDateTime;

public final class Licence {
    public static Object global_008292BC = "";
    public static Object global_008292C0 = "";
    public static long global_008292C8 = 0L;
    public static Object global_008292CC = "";
    public static Object global_008292D0 = "";
    public static String global_008292D4 = "";
    public static Object global_008292D8 = "";
    public static String global_008291E4 = "";
    public static Object global_008291E8 = "";
    public static Object global_0082927C = "";
    public static String global_0082912C = "";
    public static Object global_0082911C = "";
    public static long global_00829128 = 0L;
    public static Object global_00829140 = "";
    public static Object global_0082915C = "";
    public static long global_00829168 = 0L;
    public static String global_00829178 = "";
    public static String global_0082917C = "";
    public static String global_00829258 = "";
    public static long global_0082916C = 0L;
    public static String global_008291EC = "";
    public static String global_008291F8 = "";
    public static String global_008291FC = "";
    public static String global_00829078 = "";
    public static String global_0082907C = "";
    public static String global_00829084 = "";
    public static String global_00829094 = "";
    public static String global_008290A0 = "";
    public static long global_008290A4 = 0L;
    public static long global_008290A8 = 0L;
    public static String global_0082925C = "";
    public static String global_00829260 = "";
    public static String global_00829268 = "";
    public static String global_00829204 = "";
    public static String global_00829208 = "";
    public static Object global_0082920C = "";
    public static Object global_00829210 = "";
    public static String global_00829290 = "";
    public static String global_00829294 = "";
    public static Object global_00829224 = "";
    public static String global_00829230 = "";
    public static Object global_00829244 = "";
    public static Object global_008292F4 = "";
    public static Object global_00829308 = "";
    public static Object global_008291D4 = "";
    public static long global_008291D8 = 0L;
    public static long global_0082919C = 0L;
    public static String global_008291A0 = "";
    public static boolean global_00829190 = false;
    public static long global_0082904C = 0L;
    public static String global_00829038 = "";
    public static long global_0082903C = 0L;
    public static boolean global_00829034 = false;
    public static long global_008290AC = 0L;
    public static String global_00829040 = "";
    public static String global_00829044 = "";
    public static String global_00829048 = "";
    public static String global_00829080 = "";
    public static String global_0082908C = "";
    public static LocalDateTime global_00829090 = null;
    public static String global_00829098 = "";
    public static String global_0082909C = "";
    public static Object global_0082934C = "";
    public static String global_00829310 = "";
    public static String global_0082930C = "";
    public static String global_00829350 = "";
    public static String global_00829354 = "";
    public static String global_00829358 = "";

    private Licence() {
    }

    public static long Proc_9_0_806F70(Object... args) {
        if (args == null || args.length < 2) {
            return 0L;
        }
        return Vb.val(getTableCell(global_008292BC, Vb.val(args[0]), Vb.val(args[1])));
    }

    public static String Proc_9_1_8072B0(Object... args) {
        if (args == null || args.length < 2) {
            return "";
        }
        return getTableCell(global_008292C0, Vb.val(args[0]), Vb.val(args[1]));
    }

    public static long Proc_9_2_8075F0(Object... args) {
        if (args == null || args.length < 2) {
            return 0L;
        }
        return Vb.val(getTableCell(global_008292C0, Vb.val(args[0]), Vb.val(args[1])));
    }

    public static String Proc_9_3_807930(Object... args) {
        if (args == null || args.length == 0) {
            return "";
        }
        return getTableRow(global_008292BC, Vb.val(args[0]));
    }

    public static String Proc_9_4_807B90(Object... args) {
        if (args == null || args.length == 0) {
            return "";
        }
        return getTableRow(global_008292C0, Vb.val(args[0]));
    }

    public static String Proc_9_5_807DF0(Object... args) {
        if (args == null || args.length == 0) {
            return "";
        }
        return getDelimitedRow(global_00829258, Vb.val(args[0]));
    }

    public static String Proc_9_6_808080(Object... args) {
        if (args == null || args.length == 0) {
            return "";
        }
        return getSessionRecordField("0:", Vb.cStr(args[0]), optionalColumnIndex(args, 1, 0));
    }

    public static long Proc_9_7_808320(Object... args) {
        if (args == null || args.length == 0) {
            return 0L;
        }
        return Vb.val(getSessionRecordField("1:", Vb.cStr(args[0]), optionalColumnIndex(args, 1, 1)));
    }

    public static long Proc_9_8_8086A0(Object... args) {
        if (args == null || args.length == 0) {
            return 0L;
        }
        return Vb.val(getSessionLinkedValue(Vb.cStr(args[0]), true));
    }

    public static long Proc_9_9_808AC0(Object... args) {
        if (args == null || args.length == 0) {
            return 0L;
        }
        return Vb.val(getSessionLinkedValue(Vb.cStr(args[0]), false));
    }

    public static long Proc_9_10_808F30(Object... args) {
        if (args == null || args.length == 0) {
            return 0L;
        }
        return Vb.val(getSessionCacheField(Vb.cStr(args[0]), optionalColumnIndex(args, 1, 0)));
    }

    public static long Proc_9_11_809220(Object... args) {
        return Proc_9_10_808F30(args);
    }

    public static int optionalColumnIndex(Object[] args, int argumentIndex, int defaultValue) {
        if (args != null && argumentIndex >= 0 && argumentIndex < args.length && !Vb.cStr(args[argumentIndex]).isEmpty()) {
            return (int) Vb.val(args[argumentIndex]);
        }
        return defaultValue;
    }

    public static String getSessionCacheField(String keyName, long columnIndex) {
        if (keyName == null || keyName.isEmpty() || global_00829268.isEmpty()) {
            return "";
        }
        String lowerCache = global_00829268.toLowerCase();
        String marker = "[" + keyName.toLowerCase() + '\1';
        int markerAt = lowerCache.indexOf(marker);
        if (markerAt < 0) {
            return "";
        }
        String valueText = global_00829268.substring(markerAt + marker.length());
        String[] fields = valueText.split("\2", -1);
        if (columnIndex < 0 || columnIndex >= fields.length) {
            return "";
        }
        return fields[(int) columnIndex];
    }

    public static String getSessionRecordField(String recordPrefix, String recordId, long columnIndex) {
        String payload = getSessionRecordPayload(recordPrefix, recordId);
        if (payload.isEmpty()) {
            return "";
        }
        String[] fields = payload.split("\2", -1);
        if (columnIndex < 0 || columnIndex >= fields.length) {
            return "";
        }
        String[] valueParts = fields[(int) columnIndex].split("\\]", -1);
        return valueParts.length == 0 ? "" : valueParts[0];
    }

    public static String getSessionRecordPayload(String recordPrefix, String recordId) {
        if (global_00829268.isEmpty()) {
            return "";
        }
        String marker = "[" + recordPrefix + recordId + '\1';
        int markerAt = global_00829268.toLowerCase().indexOf(marker.toLowerCase());
        if (markerAt < 0) {
            return "";
        }
        int payloadStart = markerAt + marker.length();
        int payloadEnd = global_00829268.indexOf(']', payloadStart);
        if (payloadEnd < 0) {
            payloadEnd = global_00829268.length();
        }
        return global_00829268.substring(payloadStart, payloadEnd);
    }

    public static String getSessionLinkedValue(String recordId, boolean useBracketCount) {
        if (global_00829268.isEmpty()) {
            return "";
        }
        String marker = "\2" + recordId + "]";
        String[] parts = global_00829268.split(java.util.regex.Pattern.quote(marker), -1);
        if (parts.length < 2) {
            return "";
        }
        String sectionText = parts[parts.length - 1];
        String[] bracketParts = sectionText.split("\\[", -1);
        int targetIndex = bracketParts.length - 1;
        String[] valueParts = useBracketCount ? sectionText.split("\1", -1) : sectionText.split("\0", -1);
        if (targetIndex < 0 || targetIndex >= valueParts.length) {
            return "";
        }
        return valueParts[targetIndex];
    }

    public static String getTableCell(Object tableCache, long rowId, long columnIndex) {
        String rowValue = getTableRow(tableCache, rowId);
        if (rowValue.isEmpty()) {
            return "";
        }
        String[] columns = rowValue.split("\t", -1);
        if (columnIndex < 0 || columnIndex >= columns.length) {
            return "";
        }
        return columns[(int) columnIndex];
    }

    public static String getTableRow(Object tableCache, long rowId) {
        if (rowId < 0) {
            return "";
        }
        if (tableCache instanceof String[]) {
            String[] rows = (String[]) tableCache;
            return rowId < rows.length ? Vb.cStr(rows[(int) rowId]) : "";
        }
        return getDelimitedRow(Vb.cStr(tableCache), rowId);
    }

    public static String getDelimitedRow(String tableText, long rowId) {
        if (tableText == null || tableText.isEmpty()) {
            return "";
        }
        String[] rows = ("\r" + tableText + "\r").split("\r", -1);
        for (String rowText : rows) {
            if (!rowText.isEmpty()) {
                String[] columns = rowText.split("\t", -1);
                if (columns.length > 0 && Vb.val(columns[0]) == rowId) {
                    return rowText;
                }
            }
        }
        return "";
    }
}
