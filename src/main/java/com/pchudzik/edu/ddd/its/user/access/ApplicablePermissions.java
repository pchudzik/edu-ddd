package com.pchudzik.edu.ddd.its.user.access;

import com.pchudzik.edu.ddd.its.user.access.Permission.EvaluationContext;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toSet;

class ApplicablePermissions {
    private final Set<Permission> permissions;

    public ApplicablePermissions(Collection<Permission> permissions) {
        this.permissions = new HashSet<>(permissions);
    }

    public static ApplicablePermissions empty() {
        return new ApplicablePermissions(emptySet());
    }

    public ApplicablePermissions and(ApplicablePermissions other) {
        return new ApplicablePermissions(Stream
                .concat(
                        this.permissions.stream(),
                        other.permissions.stream())
                .collect(toSet()));
    }

    boolean checkPermission(EvaluationContext context, PermissionType projectManager) {
        return permissions.stream()
                .filter(p -> p.isApplicable(projectManager))
                .anyMatch(p -> p.evaluate(context));
    }
}
