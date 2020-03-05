package com.pchudzik.edu.ddd.its.user.access;

import com.pchudzik.edu.ddd.its.project.ProjectId;
import com.pchudzik.edu.ddd.its.user.UserId;
import com.pchudzik.edu.ddd.its.user.access.Permission.EvaluationContext;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.Objects;

import static com.pchudzik.edu.ddd.its.user.access.PermissionType.ISSUE_MANAGER;
import static com.pchudzik.edu.ddd.its.user.access.PermissionType.SINGLE_PROJECT_MANAGER;

class UserPermissions {
    @Getter(AccessLevel.PACKAGE)
    private final UserId userId;
    private final ApplicablePermissions permissions;

    public UserPermissions(UserId userId, ApplicablePermissions permissions){
        this.userId = userId;
        this.permissions = permissions;
    }

    public boolean canManageUsers() {
        return permissions.checkPermission(
                EvaluationContext.empty(),
                PermissionType.USER_MANAGER);
    }

    public boolean canManageProject(ProjectId projectId) {
        return permissions.checkPermission(
                new EvaluationContext().withProjectId(projectId),
                SINGLE_PROJECT_MANAGER);
    }

    public boolean canCreateIssue(ProjectId projectId) {
        return permissions.checkPermission(
                new EvaluationContext().withProjectId(projectId),
                ISSUE_MANAGER);
    }

    public boolean canCreateProject() {
        return permissions.checkPermission(
                EvaluationContext.empty(),
                PermissionType.CREATE_PROJECT);
    }

    public boolean canManageUser(UserId userId) {
        if (Objects.equals(this.userId, userId)) {
            return true;
        }

        return permissions.checkPermission(new EvaluationContext()
                .withActiveUserId(this.userId)
                .withOtherUserId(userId), PermissionType.USER_MANAGER);
    }

    public boolean canAccessIssue(ProjectId projectId) {
        return permissions.checkPermission(
                new EvaluationContext().withProjectId(projectId),
                PermissionType.ACCESS_ISSUE);
    }

    public boolean canAccessProject(ProjectId projectId) {
        return permissions.checkPermission(
                new EvaluationContext().withProjectId(projectId),
                PermissionType.ACCESS_PROJECT);
    }

    public boolean canCreateRoles() {
        return permissions.checkPermission(
                EvaluationContext.empty(),
                PermissionType.ROLES_MANAGER);
    }
}
