package com.pchudzik.edu.ddd.its.field;

import com.pchudzik.edu.ddd.its.infrastructure.db.TransactionManager;
import com.pchudzik.edu.ddd.its.infrastructure.queue.MessageQueue;
import com.pchudzik.edu.ddd.its.issue.id.IssueId;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
class FieldAssignmentImpl implements FieldAssignment {
    private final TransactionManager txManager;

    private final StringFieldRepository stringFieldRepository;
    private final LabelFieldRepository labelFieldRepository;
    private final FieldValueRepository fieldValueRepository;
    private final MessageQueue messageQueue;

    @Override
    public void assignToField(Collection<FieldAssignmentCommand> assignments) {
        var strategyFactory = new AssignmentStrategyFactory();
        txManager.useTransaction(() -> assignments
                .forEach(cmd -> {
                    Collection<MessageQueue.Message> msgs = strategyFactory
                            .getAssignmentStrategy(cmd.getAssignmentType(), cmd.getFieldType())
                            .assign(cmd);
                    msgs.forEach(messageQueue::publish);
                }));
    }

    private interface AssignFieldStrategy<V, A> {
        Collection<MessageQueue.Message> assign(FieldAssignmentCommand<V, A> cmd);
    }

    private class AssignmentStrategyFactory {
        private Map<AssignmentKey, Supplier<AssignFieldStrategy>> strategies = Map.of(
                new AssignmentKey(AssignmentType.ISSUE, FieldType.STRING_FIELD), AssignStringFieldToIssue::new,
                new AssignmentKey(AssignmentType.ISSUE, FieldType.LABEL_FIELD), AssignLabelFieldToIssue::new);

        <V, A> AssignFieldStrategy<V, A> getAssignmentStrategy(AssignmentType assignmentType, FieldType fieldType) {
            return Optional
                    .ofNullable(strategies.get(new AssignmentKey(assignmentType, fieldType)))
                    .map(Supplier::get)
                    .orElseThrow(() -> new IllegalArgumentException("No strategy for assigning " + assignmentType + " to " + fieldType));
        }

        @EqualsAndHashCode
        @RequiredArgsConstructor
        private class AssignmentKey {
            private final AssignmentType assignmentType;
            private final FieldType fieldType;
        }
    }

    private class AssignStringFieldToIssue implements AssignFieldStrategy<String, IssueId> {
        public Collection<MessageQueue.Message> assign(FieldAssignmentCommand<String, IssueId> cmd) {
            StringField field = stringFieldRepository.findStringField(cmd.getFieldId());
            FieldValue<String> value = field
                    .value(cmd.getValue())
                    .getOrElseThrow(validationResult -> new IllegalStateException("TODO"));
            fieldValueRepository.removeOldValues(cmd.getFieldId(), cmd.getAssigneeId(IssueId.class));
            var fieldValueId = fieldValueRepository.saveStringValue(cmd.getAssigneeId(IssueId.class), value);
            return Collections.singleton(new FieldAssignedMessage(Collections.singleton(fieldValueId)));
        }
    }

    private class AssignLabelFieldToIssue implements AssignFieldStrategy<LabelValues, IssueId> {
        public Collection<MessageQueue.Message> assign(FieldAssignmentCommand<LabelValues, IssueId> cmd) {
            LabelField field = labelFieldRepository.findLabelField(cmd.getFieldId());
            FieldValue<LabelValues> value = field
                    .value(cmd.getValue())
                    .getOrElseThrow(validationResult -> new IllegalStateException("TODO"));
            fieldValueRepository.removeOldValues(cmd.getFieldId(), cmd.getAssigneeId(IssueId.class));
            var valueIds = fieldValueRepository.saveLabelValue(cmd.getAssigneeId(IssueId.class), value);
            return Collections.singleton(new FieldAssignedMessage(valueIds));
        }
    }
}
