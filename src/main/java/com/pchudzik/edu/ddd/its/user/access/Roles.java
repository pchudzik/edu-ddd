package com.pchudzik.edu.ddd.its.user.access;

import com.pchudzik.edu.ddd.its.project.ProjectId;
import com.pchudzik.edu.ddd.its.user.UserId;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Optional;

public interface Roles {
    RoleId createRole(RoleCreationCommand cmd);

    void updateRole(RoleUpdateCommand cmd);

    void assignUserToRole(RoleAssignmentCommand assignmentCommand);

    @Getter
    @RequiredArgsConstructor
    class RoleAssignmentCommand implements AuthorizedCommand {
        private final UserId userId;
        private final RoleId roleId;
        private final Principal principal;
    }

    @Getter
    @RequiredArgsConstructor
    class RoleCreationCommand implements AuthorizedCommand {
        private final Principal principal;
        private final String name;
        private final Collection<PermissionAssignment> permissions;
    }

    @Getter
    @RequiredArgsConstructor
    class RoleUpdateCommand implements AuthorizedCommand {
        private final RoleId roleId;
        private final Principal principal;
        private final String name;
        private final Collection<PermissionAssignment> permissions;
    }

    @RequiredArgsConstructor
    class PermissionAssignment {
        @Getter
        private final PermissionType permissionType;
        private final ProjectId projectId;

        public PermissionAssignment(PermissionType permissionType) {
            this(permissionType, null);
        }

        public Optional<ProjectId> getProjectId() {
            return Optional.ofNullable(projectId);
        }
    }
}
