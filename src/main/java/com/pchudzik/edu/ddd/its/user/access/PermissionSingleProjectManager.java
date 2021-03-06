package com.pchudzik.edu.ddd.its.user.access;

import com.pchudzik.edu.ddd.its.project.ProjectId;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@EqualsAndHashCode
@RequiredArgsConstructor
class PermissionSingleProjectManager implements Permission {
    private final ProjectId projectId;

    @Override
    public boolean evaluate(EvaluationContext evaluationContext) {
        var otherProject = evaluationContext
                .getProjectId()
                .orElseThrow(() -> new IllegalArgumentException("Project id for evaluating permission is missing"));
        return Objects.equals(projectId, otherProject);
    }

    @Override
    public boolean isApplicable(PermissionType permissionType) {
        return permissionType == PermissionType.SINGLE_PROJECT_MANAGER;
    }

    @Override
    public PermissionSnapshot getSnapshot() {
        return new PermissionSnapshot(PermissionType.SINGLE_PROJECT_MANAGER, projectId);
    }
}
