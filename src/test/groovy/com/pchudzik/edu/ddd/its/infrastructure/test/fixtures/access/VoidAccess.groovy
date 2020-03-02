package com.pchudzik.edu.ddd.its.infrastructure.test.fixtures.access

import com.pchudzik.edu.ddd.its.project.ProjectId
import com.pchudzik.edu.ddd.its.user.UserId
import com.pchudzik.edu.ddd.its.user.access.Access
import com.pchudzik.edu.ddd.its.user.access.Principal

import java.util.function.Predicate

class VoidAccess implements Access {
    @Override
    <T> T ifCanCreateIssue(Principal principal, ProjectId projectId, SecuredAction<T> action) {
        action.apply()
    }

    @Override
    void ifCanUpdateIssue(Principal principal, ProjectId projectId, SecuredOperation action) {
        action.execute()
    }

    @Override
    <T> T ifCanViewIssue(Principal principal, ProjectId project, SecuredAction<T> action) {
        action.apply()
    }

    @Override
    <T> T ifCanCreateProject(Principal principal, SecuredAction<T> action) {
        action.apply()
    }

    @Override
    void ifCanUpdateProject(Principal principal, ProjectId projectId, SecuredOperation action) {
        action.execute()
    }

    @Override
    Predicate<ProjectId> canViewProject(Principal principal) {
        { p -> true } as Predicate
    }

    @Override
    <T> T ifCanManageRoles(Principal principal, SecuredAction<T> action) {
        action.apply()
    }

    @Override
    <T> T ifCanManageUsers(Principal principal, SecuredAction<T> action) {
        action.apply()
    }

    @Override
    void ifCanManageUser(Principal principal, UserId userId, SecuredOperation operation) {
        operation.execute()
    }
}
