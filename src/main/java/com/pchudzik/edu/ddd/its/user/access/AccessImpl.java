package com.pchudzik.edu.ddd.its.user.access;

import com.pchudzik.edu.ddd.its.project.ProjectId;
import com.pchudzik.edu.ddd.its.user.access.AccessException.ForbiddenOperationException;
import com.pchudzik.edu.ddd.its.user.access.AccessException.NoSuchUserException;
import com.pchudzik.edu.ddd.its.user.access.Permission.PermissionType;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;

@RequiredArgsConstructor(onConstructor_ = @Inject)
class AccessImpl implements Access {
    private final PermissionsRepository permissionsRepository;

    @Override
    public <T> T ifCanCreateIssue(Principal principal, ProjectId projectId, SecuredAction<T> action) {
        if (!findPermissionsForUser(principal).canCreateIssue(projectId)) {
            throw new ForbiddenOperationException(principal, projectId, PermissionType.CREATE_ISSUE);
        }

        return action.apply();
    }

    @Override
    public void ifCanUpdateIssue(Principal principal, ProjectId projectId, SecuredOperation action) {
        if (!findPermissionsForUser(principal).canUpdateIssue(projectId)) {
            throw new ForbiddenOperationException(principal, projectId, PermissionType.UPDATE_ISSUE);
        }

        action.execute();
    }

    @Override
    public <T> T ifCanViewIssue(Principal principal, ProjectId project, SecuredAction<T> action) {
        if (!findPermissionsForUser(principal).canAccessIssue(project)) {
            throw new ForbiddenOperationException(principal, project, PermissionType.ACCESS_ISSUE);
        }

        return action.apply();
    }

    @Override
    public <T> T ifCanCreateProject(Principal principal, SecuredAction<T> action) {
        if (!findPermissionsForUser(principal).canCreateProject()) {
            throw new ForbiddenOperationException(principal, null, PermissionType.CREATE_PROJECT);
        }

        return action.apply();
    }

    @Override
    public void ifCanUpdateProject(Principal principal, ProjectId projectId, SecuredOperation action) {
        if (!findPermissionsForUser(principal).canManageProject(projectId)) {
            throw new ForbiddenOperationException(principal, projectId, PermissionType.PROJECT_MANAGER);
        }

        action.execute();
    }

    @Override
    public <T> T ifCanViewProject(Principal principal, ProjectId projectId, SecuredAction<T> action) {
        if (!findPermissionsForUser(principal).canAccessProject(projectId)) {
            throw new ForbiddenOperationException(principal, null, PermissionType.CREATE_PROJECT);
        }

        return action.apply();
    }

    private UserPermissions findPermissionsForUser(Principal principal) {
        return permissionsRepository
                .findOne(principal.getUserId())
                .orElseThrow(() -> new NoSuchUserException(principal));
    }
}
