package com.pchudzik.edu.ddd.its.user;

import java.util.Objects;

public class PermissionUserDataManager implements Permission {

    @Override
    public boolean evaluate(EvaluationContext evaluationContext) {
        var activeUserId = evaluationContext
                .getActiveUserId()
                .orElseThrow(() -> new IllegalArgumentException("Active user id not provided"));
        var userId = evaluationContext
                .getOtherUserId()
                .orElseThrow(() -> new IllegalArgumentException("User id not provided"));

        return Objects.equals(activeUserId, userId);
    }

    @Override
    public boolean isApplicable(PermissionType permissionType) {
        return PermissionType.UPDATE_USER == permissionType;
    }
}
