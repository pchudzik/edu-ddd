package com.pchudzik.edu.ddd.its.user;

import com.pchudzik.edu.ddd.its.project.ProjectId;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

interface Permission {
    boolean evaluate(EvaluationContext evaluationContext);

    boolean isApplicable(PermissionType permissionType);

    enum PermissionType {
        USER_MANAGER_PERMISSION,
        PROJECT_MANAGER,
        CREATE_ISSUE
    }

    class EvaluationContext {
        private final Map<Keys, Object> arguments = new HashMap<>();

        public static EvaluationContext empty() {
            return new EvaluationContext();
        }

        public EvaluationContext withProjectId(ProjectId projectId) {
            arguments.put(Keys.projectId, projectId);
            return this;
        }

        public Optional<ProjectId> getProjectId() {
            return Optional.ofNullable((ProjectId) arguments.get(Keys.projectId));
        }

        private enum Keys {
            projectId
        }
    }
}
