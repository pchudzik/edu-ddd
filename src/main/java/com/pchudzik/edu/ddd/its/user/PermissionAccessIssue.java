package com.pchudzik.edu.ddd.its.user;

import com.pchudzik.edu.ddd.its.project.ProjectId;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
class PermissionAccessIssue implements Permission {
    private final ProjectId projectId;

    @Override
    public boolean evaluate(EvaluationContext evaluationContext) {
        var project = evaluationContext
                .getProjectId()
                .orElseThrow(() -> new IllegalArgumentException("Project id not provided"));
        return Objects.equals(projectId, project);
    }

    @Override
    public boolean isApplicable(PermissionType permissionType) {
        return PermissionType.ACCESS_ISSUE == permissionType;
    }
}
