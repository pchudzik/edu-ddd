package com.pchudzik.edu.ddd.its.user.access;

import com.pchudzik.edu.ddd.its.project.ProjectId;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

@EqualsAndHashCode
@RequiredArgsConstructor
class PermissionIssueCreatorWithinProject implements Permission {
    private static final Set<PermissionType> applicablePermissions = EnumSet.of(PermissionType.CREATE_ISSUE, PermissionType.UPDATE_ISSUE);

    private final ProjectId projectId;

    @Override
    public boolean evaluate(EvaluationContext evaluationContext) {
        var projectId = evaluationContext
                .getProjectId()
                .orElseThrow(() -> new IllegalArgumentException("Project id for evaluating permission is missing"));
        return Objects.equals(this.projectId, projectId);
    }

    @Override
    public boolean isApplicable(PermissionType permissionType) {
        return applicablePermissions.contains(permissionType);
    }
}
