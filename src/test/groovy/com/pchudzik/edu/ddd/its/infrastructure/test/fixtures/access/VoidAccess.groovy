package com.pchudzik.edu.ddd.its.infrastructure.test.fixtures.access

import com.pchudzik.edu.ddd.its.project.ProjectId
import com.pchudzik.edu.ddd.its.user.access.Access

class VoidAccess implements Access {
    @Override
    <T> T ifCanCreateIssue(Principal principal, ProjectId projectId, SecuredAction<T> action) {
        return action.apply()
    }

    @Override
    void ifCanUpdateIssue(Principal principal, ProjectId projectId, SecuredOperation action) {
        action.execute()
    }

    @Override
    <T> T ifCanViewIssue(Principal principal, ProjectId project, SecuredAction<T> action) {
        return action.apply()
    }
}
