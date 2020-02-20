package com.pchudzik.edu.ddd.its.user;

import com.pchudzik.edu.ddd.its.project.ProjectId;
import com.pchudzik.edu.ddd.its.user.Permission.EvaluationContext;
import com.pchudzik.edu.ddd.its.user.Permission.PermissionType;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static com.pchudzik.edu.ddd.its.user.Permission.PermissionType.CREATE_ISSUE;
import static com.pchudzik.edu.ddd.its.user.Permission.PermissionType.PROJECT_MANAGER;

class UserPermissions {
    @Getter(AccessLevel.PACKAGE)
    private final UserId userId;
    private final Set<Permission> permissions;

    public UserPermissions(UserId userId, Collection<Permission> permissions) {
        this.userId = userId;
        this.permissions = new HashSet<>(permissions);
    }

    public boolean canAddUser() {
        return checkPermission(
                EvaluationContext.empty(),
                PermissionType.USER_MANAGER_PERMISSION);
    }

    public boolean canManageProject(ProjectId projectId) {
        return checkPermission(
                new EvaluationContext().withProjectId(projectId),
                PROJECT_MANAGER);
    }

    private boolean checkPermission(EvaluationContext context, PermissionType projectManager) {
        return permissions.stream()
                .filter(p -> p.isApplicable(projectManager))
                .anyMatch(p -> p.evaluate(context));
    }

    public boolean canCreateIssue(ProjectId projectId) {
        return checkPermission(
                new EvaluationContext().withProjectId(projectId),
                CREATE_ISSUE);
    }

    public boolean canCreateProject() {
        return checkPermission(
                EvaluationContext.empty(),
                PermissionType.PROJECT_CREATOR);
    }

    public boolean canEditUser(UserId userId) {
        if(Objects.equals(this.userId, userId)) {
            return true;
        }

        return checkPermission(
                new EvaluationContext()
                        .withActiveUserId(this.userId)
                        .withOtherUserId(userId),
                PermissionType.UPDATE_USER);
    }

    public boolean canAccessIssue(ProjectId projectId) {
        return checkPermission(
                new EvaluationContext().withProjectId(projectId),
                PermissionType.ACCESS_ISSUE);
    }
}
