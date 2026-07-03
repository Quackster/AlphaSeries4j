package com.alphaseries.config;

import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class PermissionMatrix {
    private final List<PermissionPayload> permissions;

    private PermissionMatrix(Iterable<PermissionPayload> permissions) {
        this.permissions = copy(permissions);
    }

    public static PermissionMatrix empty() {
        return fromPayloadRows(List.of());
    }

    public static PermissionMatrix fromPayloadRows(Iterable<PermissionPayload> permissions) {
        return new PermissionMatrix(permissions);
    }

    public boolean allows(long rankValue, String basePermissions, String permissionName, long hcLevelValue) {
        int rankIndex = clamp(NumberUtils.parseInt(rankValue), 0, 20);
        int hcLevel = clamp(NumberUtils.parseInt(hcLevelValue), 0, 2);
        String permission = StringUtils.text(permissionName);
        String permissionList = "\2" + StringUtils.text(basePermissions) + "\2";
        if (permissionList.contains("\2" + permission + "\2")) {
            return true;
        }

        for (PermissionPayload payload : permissions) {
            if (payload.rankIndex() == rankIndex && payload.hcLevel() == hcLevel) {
                return payload.allows(permission);
            }
        }

        return false;
    }

    public static final class PermissionPayload {
        private final long rankIndex;
        private final long hcLevel;
        private final List<String> permissions;

        private PermissionPayload(long rankIndex, long hcLevel, Iterable<String> permissions) {
            this.rankIndex = clamp(NumberUtils.parseInt(rankIndex), 0, 20);
            this.hcLevel = clamp(NumberUtils.parseInt(hcLevel), 0, 2);
            this.permissions = copyPermissions(permissions);
        }

        public static PermissionPayload ofPermissions(long rankIndex, long hcLevel, String... permissions) {
            return ofPermissions(rankIndex, hcLevel, permissions == null ? List.of() : Arrays.asList(permissions));
        }

        public static PermissionPayload ofPermissions(long rankIndex, long hcLevel, Iterable<String> permissions) {
            return new PermissionPayload(rankIndex, hcLevel, permissions);
        }

        public long rankIndex() {
            return rankIndex;
        }

        public long hcLevel() {
            return hcLevel;
        }

        private boolean allows(String permission) {
            return permissions.contains(StringUtils.text(permission));
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof PermissionPayload payload)) {
                return false;
            }
            return rankIndex == payload.rankIndex
                && hcLevel == payload.hcLevel
                && permissions.equals(payload.permissions);
        }

        @Override
        public int hashCode() {
            return Objects.hash(rankIndex, hcLevel, permissions);
        }

        @Override
        public String toString() {
            return "PermissionPayload[rankIndex=" + rankIndex
                + ", hcLevel=" + hcLevel
                + ", permissions=" + permissions + "]";
        }
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static List<PermissionPayload> copy(Iterable<PermissionPayload> permissions) {
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

    private static List<String> copyPermissions(Iterable<String> permissions) {
        if (permissions == null) {
            return List.of();
        }
        List<String> result = new ArrayList<>();
        for (String permission : permissions) {
            String value = StringUtils.text(permission);
            if (!value.isEmpty()) {
                result.add(value);
            }
        }
        return List.copyOf(result);
    }
}
