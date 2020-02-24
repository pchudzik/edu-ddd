package com.pchudzik.edu.ddd.its.user.access;

class PermissionNewProjectCreator implements Permission {
    @Override
    public boolean evaluate(EvaluationContext evaluationContext) {
        return true;
    }

    @Override
    public boolean isApplicable(PermissionType permissionType) {
        return PermissionType.CREATE_PROJECT == permissionType || PermissionType.PROJECT_MANAGER == permissionType;
    }
}
