package com.pchudzik.edu.ddd.its.user.access;

import lombok.RequiredArgsConstructor;

import java.util.Collection;

@RequiredArgsConstructor
class RolePermissions {
    private final RoleId roleId;
    private final String roleName;
    private final ApplicablePermissions permissions;

    public RolePermissions(String name, Collection<Permission> permissions) {
        this(
                new RoleId(),
                name,
                new ApplicablePermissions(permissions));
    }
}
