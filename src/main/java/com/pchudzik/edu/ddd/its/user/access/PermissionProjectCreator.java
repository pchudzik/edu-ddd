package com.pchudzik.edu.ddd.its.user.access;

class PermissionProjectCreator implements Permission {
    @Override
    public boolean evaluate(EvaluationContext evaluationContext) {
        return true;
    }

    @Override
    public boolean isApplicable(PermissionType permissionType) {
        return PermissionType.CREATE_PROJECT == permissionType || PermissionType.SINGLE_PROJECT_MANAGER == permissionType;
    }

    @Override
    public PermissionSnapshot getSnapshot() {
        return new PermissionSnapshot(PermissionType.CREATE_PROJECT);
    }
}
