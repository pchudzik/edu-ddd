package com.pchudzik.edu.ddd.its.field.read;

import com.pchudzik.edu.ddd.its.field.FieldId;
import com.pchudzik.edu.ddd.its.field.FieldType;
import com.pchudzik.edu.ddd.its.issue.id.IssueId;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public interface FieldValues {
    List<FieldValueDto> findFieldsAssignedToIssue(IssueId issueId);

    @Builder
    @Getter
    class FieldValueDto {
        private final FieldId fieldId;
        private final IssueId issueId;
        private final FieldType fieldType;
        private final Value value;
    }

    @RequiredArgsConstructor
    class Value {
        private final String value;

        public <V> V getValue(Class<V> clazz) {
            if(String.class.equals(clazz)) {
                return (V) value;
            }

            throw new UnsupportedOperationException("Ex");
        }
    }

    @EqualsAndHashCode
    @RequiredArgsConstructor
    class LabelValues {
        private final List<LabelValue> values;

        public List<LabelValue> getValues() {
            return Collections.unmodifiableList(values);
        }
    }

    @EqualsAndHashCode
    @RequiredArgsConstructor
    class LabelValue {
        private final UUID id;
        private final String value;
    }
}
