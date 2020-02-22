package com.pchudzik.edu.ddd.its.user.access;

import com.pchudzik.edu.ddd.its.project.ProjectId;
import com.pchudzik.edu.ddd.its.user.UserId;

public interface Access {

    <T> T ifCanCreateIssue(Principal principal, ProjectId projectId, SecuredAction<T> action);

    void ifCanUpdateIssue(Principal principal, ProjectId projectId, SecuredOperation action);

    interface SecuredAction<T> {
        T apply();
    }

    interface SecuredOperation {
        void execute();
    }

    interface Principal {
        UserId getUserId();

        static Principal of(UserId user) {
            return () -> user;
        }
    }
}
