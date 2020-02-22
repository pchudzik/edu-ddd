package com.pchudzik.edu.ddd.its.user.access;

import com.pchudzik.edu.ddd.its.project.ProjectId;

class PermissionFactory {
    private static final PermissionAdministrator administrator = new PermissionAdministrator();
    private static final PermissionNewProjectCreator newProjectCreator = new PermissionNewProjectCreator();
    private static final PermissionUserCreator permissionUserCreator = new PermissionUserCreator();
    private static final PermissionUserDataManager userDataManager = new PermissionUserDataManager();

    public static Permission administrator() {
        return administrator;
    }

    public static Permission accessIssue(ProjectId projectId) {
        return new PermissionAccessIssue(projectId);
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

    public static Permission userCreator() {
        return permissionUserCreator;
    }

    public static Permission userDataManager() {
        return userDataManager;
    }
}
