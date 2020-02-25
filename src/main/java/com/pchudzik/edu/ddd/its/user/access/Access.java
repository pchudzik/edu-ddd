package com.pchudzik.edu.ddd.its.user.access;

import com.pchudzik.edu.ddd.its.project.ProjectId;
import com.pchudzik.edu.ddd.its.user.UserId;

import java.util.function.Predicate;

public interface Access {

    <T> T ifCanCreateIssue(Principal principal, ProjectId projectId, SecuredAction<T> action);

    void ifCanUpdateIssue(Principal principal, ProjectId projectId, SecuredOperation action);

    <T> T ifCanViewIssue(Principal principal, ProjectId project, SecuredAction<T> action);

    <T> T ifCanCreateProject(Principal principal, SecuredAction<T> action);

    void ifCanUpdateProject(Principal principal, ProjectId projectId, SecuredOperation action);

    Predicate<ProjectId> canViewProject(Principal principal);

    interface SecuredAction<T> {
        T apply();
    }

    interface SecuredOperation {
        void execute();
    }

    interface Principal {
        static Principal of(UserId user) {
            return () -> user;
        }

        UserId getUserId();
    }
}
