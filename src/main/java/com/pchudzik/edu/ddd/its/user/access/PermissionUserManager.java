package com.pchudzik.edu.ddd.its.user.access;

class PermissionUserManager implements Permission {
    @Override
    public boolean evaluate(EvaluationContext evaluationContext) {
        return true;
    }

    @Override
    public boolean isApplicable(PermissionType permissionType) {
        return permissionType == PermissionType.USER_MANAGER;
    }

    @Override
    public PermissionSnapshot getSnapshot() {
        return new PermissionSnapshot(PermissionType.USER_MANAGER);
    }
}
