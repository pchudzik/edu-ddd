package com.pchudzik.edu.ddd.its.user;

import com.pchudzik.edu.ddd.its.project.ProjectId;
import com.pchudzik.edu.ddd.its.user.Permission.EvaluationContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.pchudzik.edu.ddd.its.user.Permission.PermissionType.CREATE_ISSUE;
import static com.pchudzik.edu.ddd.its.user.Permission.PermissionType.PROJECT_MANAGER;

class UserPermissions {
    private final UserId userId;
    private final List<Permission> permissions;

    public UserPermissions(UserId userId, Collection<Permission> permissions) {
        this.userId = userId;
        this.permissions = new ArrayList<>(permissions);
    }

    public boolean canAddUser() {
        return checkPermission(
                EvaluationContext.empty(),
                Permission.PermissionType.USER_MANAGER_PERMISSION);
    }

    public boolean canManageProject(ProjectId projectId) {
        return checkPermission(
                new EvaluationContext().withProjectId(projectId),
                PROJECT_MANAGER);
    }

    private boolean checkPermission(EvaluationContext context, Permission.PermissionType projectManager) {
        return permissions.stream()
                .filter(p -> p.isApplicable(projectManager))
                .anyMatch(p -> p.evaluate(context));
    }

    public boolean canCreateIssue(ProjectId projectId) {
        return checkPermission(
                new EvaluationContext().withProjectId(projectId),
                CREATE_ISSUE);
    }
}
