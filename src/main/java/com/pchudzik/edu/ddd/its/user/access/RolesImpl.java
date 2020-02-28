package com.pchudzik.edu.ddd.its.user.access;

import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import java.util.stream.Collectors;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class RolesImpl implements Roles {
    private final RolePermissionsRepository repository;

    @Override
    public RoleId createRole(RoleCreationCommand cmd) {
        var rolePermissions = new RolePermissions(
                cmd.getName(),
                cmd.getPermissions().stream()
                        .map(permissionAssignment -> PermissionFactory.createPermission(
                                permissionAssignment.getPermissionType(),
                                permissionAssignment.getProjectId().orElse(null)))
                        .collect(Collectors.toList()));
        repository.save(rolePermissions.getSnapshot());
        return rolePermissions.getId();
    }

    @Override
    public void updateRole(RoleUpdateCommand cmd) {

    }

    @Override
    public void assignUserToRole(RoleAssignmentCommand assignmentCommand) {

    }

}
