package com.pchudzik.edu.ddd.its.user.access;

import com.pchudzik.edu.ddd.its.project.ProjectId;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;

public interface Roles {
    void createRole(RoleCreationCommand cmd);

    void updateRole(RoleUpdateCommand cmd);

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

    @Getter
    @RequiredArgsConstructor
    class PermissionAssignment {
        private final PermissionType permissionType;
        private final ProjectId projectId;

        public PermissionAssignment(PermissionType permissionType) {
            this(permissionType, null);
        }
    }
}
