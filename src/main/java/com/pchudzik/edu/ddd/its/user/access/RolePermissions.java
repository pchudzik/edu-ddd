package com.pchudzik.edu.ddd.its.user.access;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

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

    public RolePermissionsSnapshot snapshot() {
        return null;
    }

    static class RolePermissionsSnapshot {

    }
}
