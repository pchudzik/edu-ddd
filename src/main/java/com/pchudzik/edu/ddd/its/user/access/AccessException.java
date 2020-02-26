package com.pchudzik.edu.ddd.its.user.access;

import com.pchudzik.edu.ddd.its.infrastructure.domain.DomainException;
import com.pchudzik.edu.ddd.its.project.ProjectId;

import javax.annotation.Nullable;

public class AccessException extends DomainException {
    public AccessException(String message) {
        super(message);
    }

    static class ForbiddenOperationException extends AccessException {
        private final Principal principal;
        @Nullable
        private final ProjectId projectId;
        private final PermissionType permissionType;

        public ForbiddenOperationException(Principal principal, PermissionType permissionType) {
            this(principal, null, permissionType);
        }

        public ForbiddenOperationException(Principal principal, ProjectId projectId, PermissionType permissionType) {
            super("User " + principal.getUserId() + " doesn't have permission " + permissionType);
            this.principal = principal;
            this.projectId = projectId;
            this.permissionType = permissionType;
        }
    }

    static class NoSuchUserException extends AccessException {
        private final Principal principal;

        public NoSuchUserException(Principal principal) {
            super("No user " + principal.getUserId());
            this.principal = principal;
        }
    }
}
