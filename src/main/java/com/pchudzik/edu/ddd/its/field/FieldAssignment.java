package com.pchudzik.edu.ddd.its.field;

import com.pchudzik.edu.ddd.its.infrastructure.domain.Command;
import com.pchudzik.edu.ddd.its.issue.id.IssueId;
import com.pchudzik.edu.ddd.its.project.ProjectId;
import lombok.Getter;

import java.util.Collection;

public interface FieldAssignment {
    void assignFieldValues(ProjectId projectId, Collection<FieldAssignmentCommand> assignments);
    void assignFieldValues(IssueId issue, Collection<FieldAssignmentCommand> assignments);

    interface FieldAssignmentCommand<V> extends Command {
        FieldId getFieldId();

        V getValue();

        FieldType getFieldType();
    }

    abstract class AbstractFieldAssignmentCommand<V> implements FieldAssignmentCommand<V> {
        @Getter
        private final FieldType fieldType;

        @Getter
        private final FieldId fieldId;

        protected AbstractFieldAssignmentCommand(FieldType fieldType, FieldId fieldId) {
            this.fieldType = fieldType;
            this.fieldId = fieldId;
        }
    }

    class StringFieldAssignmentCommand extends AbstractFieldAssignmentCommand<String> {
        @Getter
        private final String value;

        public StringFieldAssignmentCommand(FieldId fieldId, String value) {
            super(FieldType.STRING_FIELD, fieldId);
            this.value = value;
        }
    }

    class LabelFieldAssignmentCommand extends AbstractFieldAssignmentCommand<LabelValues> {
        @Getter
        private final LabelValues value;

        public LabelFieldAssignmentCommand(FieldId fieldId, LabelValues value) {
            super(FieldType.LABEL_FIELD, fieldId);
            this.value = value;
        }
    }
}
