package com.pchudzik.edu.ddd.its.user.access;

class PermissionRolesManager implements Permission{
    @Override
    public boolean evaluate(EvaluationContext evaluationContext) {
        return true;
    }

    @Override
    public boolean isApplicable(PermissionType permissionType) {
        return PermissionType.ROLES_MANAGER == permissionType;
    }

    @Override
    public PermissionSnapshot getSnapshot() {
        return new PermissionSnapshot(PermissionType.ROLES_MANAGER);
    }
}
