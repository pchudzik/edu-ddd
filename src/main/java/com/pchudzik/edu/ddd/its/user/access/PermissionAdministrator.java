package com.pchudzik.edu.ddd.its.user.access;

class PermissionAdministrator implements Permission {
    @Override
    public boolean evaluate(EvaluationContext evaluationContext) {
        return true;
    }

    @Override
    public boolean isApplicable(PermissionType permissionType) {
        return true;
    }
}
