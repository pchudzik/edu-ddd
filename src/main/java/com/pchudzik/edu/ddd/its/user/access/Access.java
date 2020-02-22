package com.pchudzik.edu.ddd.its.user.access;

import com.pchudzik.edu.ddd.its.user.UserId;

public interface Access {

    <T> T ifCanCreateIssue(Principal principal, SecuredAction<T> action);

    void ifCanUpdateIssue(Principal principal, SecuredOperation action);

    interface SecuredAction<T> {
        T apply();
    }

    interface SecuredOperation {
        void execute();
    }

    interface Principal {
        UserId getUserId();
    }
}
