package com.pchudzik.edu.ddd.its.user;

public class PermissionUserCreator implements Permission {
    @Override
    public boolean evaluate(EvaluationContext evaluationContext) {
        return true;
    }

    @Override
    public boolean isApplicable(PermissionType permissionType) {
        return permissionType == PermissionType.USER_MANAGER_PERMISSION
                || permissionType == PermissionType.UPDATE_USER;
    }
}
