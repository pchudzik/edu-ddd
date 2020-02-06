package com.pchudzik.edu.ddd.its.field.read;

import com.pchudzik.edu.ddd.its.field.FieldId;
import com.pchudzik.edu.ddd.its.field.FieldType;
import com.pchudzik.edu.ddd.its.issue.id.IssueId;
import com.pchudzik.edu.ddd.its.project.ProjectId;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

public interface FieldValues {
    List<FieldValueDto<?, IssueId>> findFieldsAssignedToIssue(IssueId issueId);

    List<FieldValueDto<?, ProjectId>> findFieldsAssignedToProject(ProjectId projectId);


    @Builder
    class FieldValueDto<V, A> {
        @Getter
        private final FieldId fieldId;

        private final A assigneeId;

        @Getter
        private final FieldType fieldType;

        private final V value;

        public A getAssignee(Class<A> clazz) {
            return (A) assigneeId;
        }

        public V getValue(Class<V> clazz) {
            return (V) value;
        }
    }

    @Builder
    class ProjectFieldValueDto<V> {
        @Getter
        private final FieldId fieldId;

        @Getter
        private final ProjectId projectId;

        @Getter
        private final FieldType fieldType;

        private final V value;

        V getValue(Class<V> clazz) {
            return (V) value;
        }
    }

    @Builder
    class IssueFieldValueDto<V> {
        @Getter
        private final FieldId fieldId;

        @Getter
        private final IssueId issueId;

        @Getter
        private final FieldType fieldType;

        private final V value;

        V getValue(Class<V> clazz) {
            return (V) value;
        }
    }


    @Getter
    @EqualsAndHashCode
    @RequiredArgsConstructor
    class StringValue {
        private final UUID id;
        private final String value;
    }

    @EqualsAndHashCode
    @RequiredArgsConstructor
    class LabelValues {
        @Getter
        private final List<LabelValue> labels;
    }

    @Getter
    @EqualsAndHashCode
    @RequiredArgsConstructor
    class LabelValue {
        private final UUID id;
        private final String value;
    }
}
