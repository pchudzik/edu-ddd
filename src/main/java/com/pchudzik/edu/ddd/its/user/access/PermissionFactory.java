package com.pchudzik.edu.ddd.its.user.access;

import com.pchudzik.edu.ddd.its.project.ProjectId;

import javax.annotation.Nullable;
import java.util.Optional;

class PermissionFactory {
    private static final PermissionAdministrator administrator = new PermissionAdministrator();
    private static final PermissionProjectCreator newProjectCreator = new PermissionProjectCreator();
    private static final PermissionUserManager permissionUserManager = new PermissionUserManager();
    private static final PermissionRolesManager rolesManager = new PermissionRolesManager();

    public static Permission administrator() {
        return administrator;
    }

    public static Permission accessIssue(ProjectId projectId) {
        return new PermissionIssueViewer(projectId);
    }

    public static Permission issueCreatorWithinProject(ProjectId projectId) {
        return new PermissionIssueCreatorWithinProject(projectId);
    }

    public static Permission newProjectCreator() {
        return newProjectCreator;
    }

    public static Permission singleProjectManager(ProjectId projectId) {
        return new PermissionSingleProjectManager(projectId);
    }

    public static Permission userManager() {
        return permissionUserManager;
    }

    public static Permission accessProject(ProjectId projectId) {
        return new PermissionAccessProject(projectId);
    }

    public static Permission rolesManager() {
        return rolesManager;
    }

    public static Permission createPermission(PermissionType permissionType, @Nullable ProjectId projectId) {
        switch (permissionType) {
            case USER_MANAGER:
                return userManager();
            case SINGLE_PROJECT_MANAGER:
                return singleProjectManager(requiredProjectId(projectId));
            case CREATE_PROJECT:
                return newProjectCreator();
            case ACCESS_ISSUE:
                return accessIssue(requiredProjectId(projectId));
            case ISSUE_MANAGER:
                return issueCreatorWithinProject(requiredProjectId(projectId));
            case ACCESS_PROJECT:
                return accessProject(requiredProjectId(projectId));
            default:
                throw new IllegalArgumentException("Unknown permission type " + permissionType);
        }
    }

    private static ProjectId requiredProjectId(@Nullable ProjectId projectId) {
        return Optional
                .ofNullable(projectId)
                .orElseThrow(() -> new IllegalArgumentException("ProjectId not provided"));
    }
}
