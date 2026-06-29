package com.alphaseries.config;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

public final class PermissionMatrix {
    private final Object permissions;

    private PermissionMatrix(Object permissions) {
        this.permissions = permissions == null ? "" : permissions;
    }

    public static PermissionMatrix fromLegacy(Object permissions) {
        return new PermissionMatrix(permissions);
    }

    public boolean allows(Object rankValue, Object basePermissions, Object permissionName, Object hcLevelValue) {
        int rankIndex = clamp(NumberUtils.parseInt(rankValue), 0, 20);
        int hcLevel = clamp(NumberUtils.parseInt(hcLevelValue), 0, 2);
        String permission = StringUtils.text(permissionName);
        String permissionList = "\2" + StringUtils.text(basePermissions) + "\2";

        if (permissions instanceof String[][]) {
            String[][] permissionRows = (String[][]) permissions;
            if (rankIndex < permissionRows.length && permissionRows[rankIndex] != null && hcLevel < permissionRows[rankIndex].length) {
                permissionList += StringUtils.text(permissionRows[rankIndex][hcLevel]);
            }
        } else if (permissions instanceof String[]) {
            String[] permissionRows = (String[]) permissions;
            if (rankIndex < permissionRows.length) {
                permissionList += StringUtils.text(permissionRows[rankIndex]);
            }
        }

        return permissionList.contains("\2" + permission + "\2");
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
