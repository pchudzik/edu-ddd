package com.pchudzik.edu.ddd.its.user.access;

import com.pchudzik.edu.ddd.its.project.ProjectId;

import java.util.function.Predicate;

public interface Access {

    <T> T ifCanCreateIssue(Principal principal, ProjectId projectId, SecuredAction<T> action);

    void ifCanUpdateIssue(Principal principal, ProjectId projectId, SecuredOperation action);

    <T> T ifCanViewIssue(Principal principal, ProjectId project, SecuredAction<T> action);

    <T> T ifCanCreateProject(Principal principal, SecuredAction<T> action);

    void ifCanUpdateProject(Principal principal, ProjectId projectId, SecuredOperation action);

    Predicate<ProjectId> canViewProject(Principal principal);

    <T> T ifCanManageRoles(Principal principal, SecuredAction<T> action);

    interface SecuredAction<T> {
        T apply();
    }

    interface SecuredOperation {
        void execute();
    }

}
