package com.pchudzik.edu.ddd.its.user.access;

import com.pchudzik.edu.ddd.its.project.ProjectId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class PermissionAccessProject implements Permission {
    private final ProjectId projectId;

    @Override
    public boolean evaluate(EvaluationContext evaluationContext) {
        return evaluationContext
                .getProjectId()
                .orElseThrow(() -> new IllegalArgumentException("Project id for evaluating permission is missing"))
                .equals(projectId);
    }

    @Override
    public boolean isApplicable(PermissionType permissionType) {
        return PermissionType.ACCESS_PROJECT == permissionType;
    }
}
