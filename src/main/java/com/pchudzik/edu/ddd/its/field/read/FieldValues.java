package com.pchudzik.edu.ddd.its.field.read;

import com.pchudzik.edu.ddd.its.field.FieldId;
import com.pchudzik.edu.ddd.its.field.FieldType;
import com.pchudzik.edu.ddd.its.issue.id.IssueId;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

public interface FieldValues {
    List<FieldValueDto<?>> findFieldsAssignedToIssue(IssueId issueId);

    @Builder
    class FieldValueDto<V> {
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
