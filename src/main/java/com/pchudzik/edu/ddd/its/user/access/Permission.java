package com.pchudzik.edu.ddd.its.user.access;

import com.pchudzik.edu.ddd.its.project.ProjectId;
import com.pchudzik.edu.ddd.its.user.UserId;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

interface Permission {
    boolean evaluate(EvaluationContext evaluationContext);

    boolean isApplicable(PermissionType permissionType);

    PermissionSnapshot getSnapshot();

    @RequiredArgsConstructor
    class PermissionSnapshot {
        @Getter
        private final PermissionType permissionType;
        private final ProjectId projectId;

        public PermissionSnapshot(PermissionType permissionType) {
            this(permissionType, null);
        }

        Optional<ProjectId> getProjectId() {
            return Optional.ofNullable(projectId);
        }
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

        public EvaluationContext withActiveUserId(UserId userId) {
            arguments.put(Keys.activeUserId, userId);
            return this;
        }

        public EvaluationContext withOtherUserId(UserId userId) {
            arguments.put(Keys.otherUserId, userId);
            return this;
        }

        public Optional<UserId> getActiveUserId() {
            return Optional.ofNullable((UserId) arguments.get(Keys.activeUserId));
        }

        public Optional<UserId> getOtherUserId() {
            return Optional.ofNullable((UserId) arguments.get(Keys.otherUserId));
        }

        private enum Keys {
            activeUserId, otherUserId, projectId
        }
    }
}
