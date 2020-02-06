package com.pchudzik.edu.ddd.its.field;

import lombok.Getter;

import java.util.Collection;

public interface FieldAssignment {
    void assignToField(Collection<FieldAssignmentCommand> assignments);

    interface FieldAssignmentCommand<V, A> {
        FieldId getFieldId();

        A getAssigneeId(Class<A> clazz);

        V getValue();

        AssignmentType getAssignmentType();

        FieldType getFieldType();
    }

    abstract class AbstractFieldAssignmentCommand<V, A> implements FieldAssignmentCommand<V, A> {
        @Getter
        private final FieldType fieldType;

        @Getter
        private final AssignmentType assignmentType;

        @Getter
        private final FieldId fieldId;

        private final A assigneeId;

        protected AbstractFieldAssignmentCommand(FieldType fieldType, FieldId fieldId, A assigneeId) {
            this.fieldType = fieldType;
            this.assignmentType = AssignmentType.typeFor(assigneeId.getClass());
            this.fieldId = fieldId;
            this.assigneeId = assigneeId;
        }

        @Override
        public A getAssigneeId(Class<A> clazz) {
            if (!clazz.isAssignableFrom(assigneeId.getClass())) {
                throw new IllegalArgumentException("Assignee is not of type " + clazz);
            }
            return assigneeId;
        }
    }

    class StringFieldAssignmentCommand<A> extends AbstractFieldAssignmentCommand<String, A> {
        @Getter
        private final String value;

        public StringFieldAssignmentCommand(FieldId fieldId, A assigneeId, String value) {
            super(FieldType.STRING_FIELD, fieldId, assigneeId);
            this.value = value;
        }
    }

    class LabelFieldAssignmentCommand<A> extends AbstractFieldAssignmentCommand<LabelValues, A> {
        @Getter
        private final LabelValues value;

        public LabelFieldAssignmentCommand(FieldId fieldId, A assigneeId, LabelValues value) {
            super(FieldType.LABEL_FIELD, fieldId, assigneeId);
            this.value = value;
        }
    }
}
