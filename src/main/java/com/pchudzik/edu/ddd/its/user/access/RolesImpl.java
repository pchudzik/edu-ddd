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
        var permissions = repository.findOne(cmd.getRoleId());

        permissions.updateName(cmd.getName());
        permissions.updatePermissions(cmd.getPermissions().stream()
                .map(assignment -> PermissionFactory.createPermission(assignment.getPermissionType(), assignment.getProjectId().orElse(null)))
                .collect(Collectors.toList()));

        repository.save(permissions.getSnapshot());
    }

    @Override
    public void assignUserToRole(RoleAssignmentCommand assignmentCommand) {

    }

}
