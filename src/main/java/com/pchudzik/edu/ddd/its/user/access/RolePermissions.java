package com.pchudzik.edu.ddd.its.user.access;

import com.pchudzik.edu.ddd.its.user.access.Permission.PermissionSnapshot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Singular;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
class RolePermissions {
    @Getter
    private final RoleId id;
    private String roleName;
    private ApplicablePermissions permissions;

    public RolePermissions(RoleId roleId, String name, Collection<Permission> permissions) {
        this(roleId, name, new ApplicablePermissions(permissions));
    }

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

    public void updateName(String name) {
        this.roleName = name;
    }

    public void updatePermissions(List<Permission> permissions) {
        this.permissions = new ApplicablePermissions(permissions);
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
