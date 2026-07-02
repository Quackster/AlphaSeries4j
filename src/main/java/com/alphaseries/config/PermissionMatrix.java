package com.alphaseries.config;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class PermissionMatrix {
    private final List<PermissionPayload> permissions;

    private PermissionMatrix(List<PermissionPayload> permissions) {
        this.permissions = copy(permissions);
    }

    public static PermissionMatrix empty() {
        return fromPayloadRows(List.of());
    }

    public static PermissionMatrix fromPayloadRows(List<PermissionPayload> permissions) {
        return new PermissionMatrix(permissions);
    }

    public boolean allows(long rankValue, String basePermissions, String permissionName, long hcLevelValue) {
        int rankIndex = clamp(NumberUtils.parseInt(rankValue), 0, 20);
        int hcLevel = clamp(NumberUtils.parseInt(hcLevelValue), 0, 2);
        String permission = StringUtils.text(permissionName);
        String permissionList = "\2" + StringUtils.text(basePermissions) + "\2";

        for (PermissionPayload payload : permissions) {
            if (payload.rankIndex() == rankIndex && payload.hcLevel() == hcLevel) {
                permissionList += payload.payload();
                break;
            }
        }

        return permissionList.contains("\2" + permission + "\2");
    }

    public record PermissionPayload(long rankIndex, long hcLevel, String payload) {
        public PermissionPayload {
            rankIndex = clamp(NumberUtils.parseInt(rankIndex), 0, 20);
            hcLevel = clamp(NumberUtils.parseInt(hcLevel), 0, 2);
            payload = StringUtils.text(payload);
        }
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static List<PermissionPayload> copy(List<PermissionPayload> permissions) {
        if (permissions == null) {
            return List.of();
        }
        List<PermissionPayload> result = new ArrayList<>();
        for (PermissionPayload payload : permissions) {
            if (payload == null) {
                continue;
            }
            result.add(payload);
        }
        return List.copyOf(result);
    }
}
