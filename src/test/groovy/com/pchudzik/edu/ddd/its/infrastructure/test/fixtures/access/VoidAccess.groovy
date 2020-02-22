package com.pchudzik.edu.ddd.its.infrastructure.test.fixtures.access

import com.pchudzik.edu.ddd.its.user.access.Access

class VoidAccess implements Access {
    @Override
    <T> T ifCanCreateIssue(Principal principal, SecuredAction<T> action) {
        return action.apply()
    }

    @Override
    void ifCanUpdateIssue(Principal principal, SecuredOperation action) {
        action.execute()
    }
}
