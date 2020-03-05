package com.pchudzik.edu.ddd.its.user.access;

import com.pchudzik.edu.ddd.its.user.access.Permission.EvaluationContext;
import com.pchudzik.edu.ddd.its.user.access.Permission.PermissionSnapshot;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.emptySet;

class ApplicablePermissions {
    private final Set<Permission> permissions;

    public ApplicablePermissions(Collection<Permission> permissions) {
        this.permissions = new HashSet<>(permissions);
    }

    public static ApplicablePermissions empty() {
        return new ApplicablePermissions(emptySet());
    }

    boolean checkPermission(EvaluationContext context, PermissionType projectManager) {
        return permissions.stream()
                .filter(p -> p.isApplicable(projectManager))
                .anyMatch(p -> p.evaluate(context));
    }

    public Collection<PermissionSnapshot> getSnapshot() {
        return permissions.stream()
                .map(Permission::getSnapshot)
                .collect(Collectors.toList());
    }
}
