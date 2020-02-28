package com.pchudzik.edu.ddd.its.user.access;

import com.pchudzik.edu.ddd.its.user.access.Permission.PermissionSnapshot;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Singular;

import java.util.Collection;

@RequiredArgsConstructor
class RolePermissions {
    @Getter
    private final RoleId id;
    private final String roleName;
    private final ApplicablePermissions permissions;

    public RolePermissions(String name, Collection<Permission> permissions) {
        this(
                new RoleId(),
                name,
                new ApplicablePermissions(permissions));
    }

    public RolePermissionsSnapshot getSnapshot() {
        return new RolePermissionsSnapshot(
                id,
                roleName,
                permissions.getSnapshot());
    }

    @Getter
    @RequiredArgsConstructor
    static class RolePermissionsSnapshot {
        private final RoleId id;
        private final String name;
        @Singular
        private final Collection<PermissionSnapshot> permissions;
    }
}
