package com.pchudzik.edu.ddd.its.user.access;

import com.pchudzik.edu.ddd.its.infrastructure.db.TransactionManager;
import com.pchudzik.edu.ddd.its.infrastructure.domain.DomainService;

import javax.inject.Inject;

public class RolesAppService implements Roles {
    private final Access access;
    private final TransactionManager transactionManager;
    private final Roles roles;

    @Inject
    public RolesAppService(Access access, TransactionManager transactionManager, @DomainService Roles roles) {
        this.access = access;
        this.transactionManager = transactionManager;
        this.roles = roles;
    }

    @Override
    public RoleId createRole(RoleCreationCommand cmd) {
        return transactionManager.inTransaction(() -> access.ifCanManageRoles(
                cmd.getPrincipal(),
                () -> roles.createRole(cmd)));
    }

    @Override
    public void updateRole(RoleUpdateCommand cmd) {

    }

    @Override
    public void assignUserToRole(RoleAssignmentCommand assignmentCommand) {

    }
}
