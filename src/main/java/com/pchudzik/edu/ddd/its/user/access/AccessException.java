package com.pchudzik.edu.ddd.its.user.access;

import com.pchudzik.edu.ddd.its.infrastructure.domain.DomainException;
import com.pchudzik.edu.ddd.its.project.ProjectId;

public class AccessException extends DomainException {
    public AccessException(String message) {
        super(message);
    }

    static class ForbiddenOperationException extends AccessException {
        private final Access.Principal principal;
        private final ProjectId projectId;
        private final Permission.PermissionType permissionType;

        public ForbiddenOperationException(Access.Principal principal, ProjectId projectId, Permission.PermissionType permissionType) {
            super("User " + principal.getUserId() + " doesn't have permission " + permissionType);
            this.principal = principal;
            this.projectId = projectId;
            this.permissionType = permissionType;
        }
    }

    static class NoSuchUserException extends AccessException {
        private final Access.Principal principal;

        public NoSuchUserException(Access.Principal principal) {
            super("No user " + principal.getUserId());
            this.principal = principal;
        }
    }
}
