package com.alphaseries.config;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

public final class PermissionMatrix {
    private final String[][] permissions;

    private PermissionMatrix(String[][] permissions) {
        this.permissions = copy(permissions);
    }

    public static PermissionMatrix fromLegacy(Object permissions) {
        if (permissions instanceof PermissionMatrix permissionMatrix) {
            return permissionMatrix;
        }
        if (permissions instanceof String[][] matrix) {
            return new PermissionMatrix(matrix);
        }
        if (permissions instanceof String[] rows) {
            String[][] matrix = new String[rows.length][3];
            for (int index = 0; index < rows.length; index++) {
                String row = StringUtils.text(rows[index]);
                matrix[index][0] = row;
                matrix[index][1] = row;
                matrix[index][2] = row;
            }
            return new PermissionMatrix(matrix);
        }
        return new PermissionMatrix(new String[0][0]);
    }

    public static PermissionMatrix fromRows(String[][] permissions) {
        return new PermissionMatrix(permissions);
    }

    public boolean allows(Object rankValue, Object basePermissions, Object permissionName, Object hcLevelValue) {
        int rankIndex = clamp(NumberUtils.parseInt(rankValue), 0, 20);
        int hcLevel = clamp(NumberUtils.parseInt(hcLevelValue), 0, 2);
        String permission = StringUtils.text(permissionName);
        String permissionList = "\2" + StringUtils.text(basePermissions) + "\2";

        if (rankIndex < permissions.length && permissions[rankIndex] != null && hcLevel < permissions[rankIndex].length) {
            permissionList += StringUtils.text(permissions[rankIndex][hcLevel]);
        }

        return permissionList.contains("\2" + permission + "\2");
    }

    public String[][] rows() {
        return copy(permissions);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static String[][] copy(String[][] permissions) {
        if (permissions == null) {
            return new String[0][0];
        }
        String[][] result = new String[permissions.length][];
        for (int rowIndex = 0; rowIndex < permissions.length; rowIndex++) {
            if (permissions[rowIndex] == null) {
                result[rowIndex] = new String[0];
                continue;
            }
            result[rowIndex] = new String[permissions[rowIndex].length];
            for (int columnIndex = 0; columnIndex < permissions[rowIndex].length; columnIndex++) {
                result[rowIndex][columnIndex] = StringUtils.text(permissions[rowIndex][columnIndex]);
            }
        }
        return result;
    }
}
