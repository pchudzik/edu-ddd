package com.pchudzik.edu.ddd.its.user.access;

import com.pchudzik.edu.ddd.its.project.ProjectId;
import com.pchudzik.edu.ddd.its.user.UserId;
import com.pchudzik.edu.ddd.its.user.access.Permission.EvaluationContext;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.Objects;

import static com.pchudzik.edu.ddd.its.user.access.PermissionType.CREATE_ISSUE;
import static com.pchudzik.edu.ddd.its.user.access.PermissionType.PROJECT_MANAGER;

class UserPermissions {
    @Getter(AccessLevel.PACKAGE)
    private final UserId userId;
    private final ApplicablePermissions permissions;

    public UserPermissions(UserId userId, ApplicablePermissions userPermissions, ApplicablePermissions rolePermissions) {
        this.userId = userId;
        this.permissions = userPermissions.and(rolePermissions);
    }

    public boolean canAddUser() {
        return permissions.checkPermission(
                EvaluationContext.empty(),
                PermissionType.USER_MANAGER_PERMISSION);
    }

    public boolean canManageProject(ProjectId projectId) {
        return permissions.checkPermission(
                new EvaluationContext().withProjectId(projectId),
                PROJECT_MANAGER);
    }

    public boolean canCreateIssue(ProjectId projectId) {
        return permissions.checkPermission(
                new EvaluationContext().withProjectId(projectId),
                CREATE_ISSUE);
    }

    public boolean canCreateProject() {
        return permissions.checkPermission(
                EvaluationContext.empty(),
                PermissionType.CREATE_PROJECT);
    }

    public boolean canEditUser(UserId userId) {
        if (Objects.equals(this.userId, userId)) {
            return true;
        }

        return permissions.checkPermission(new EvaluationContext()
                .withActiveUserId(this.userId)
                .withOtherUserId(userId), PermissionType.UPDATE_USER);
    }

    public boolean canAccessIssue(ProjectId projectId) {
        return permissions.checkPermission(
                new EvaluationContext().withProjectId(projectId),
                PermissionType.ACCESS_ISSUE);
    }

    public boolean canUpdateIssue(ProjectId projectId) {
        return permissions.checkPermission(
                new EvaluationContext().withProjectId(projectId),
                PermissionType.UPDATE_ISSUE);
    }

    public boolean canAccessProject(ProjectId projectId) {
        return permissions.checkPermission(
                new EvaluationContext().withProjectId(projectId),
                PermissionType.ACCESS_PROJECT);
    }

}
