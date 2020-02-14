package com.pchudzik.edu.ddd.its.field;

import com.pchudzik.edu.ddd.its.field.defaults.assignment.FieldDefinitions;
import com.pchudzik.edu.ddd.its.infrastructure.db.TransactionManager;
import com.pchudzik.edu.ddd.its.infrastructure.queue.MessageQueue;
import com.pchudzik.edu.ddd.its.issue.id.IssueId;
import com.pchudzik.edu.ddd.its.project.ProjectId;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Collections.singleton;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
class FieldAssignmentImpl implements FieldAssignment {
    private final TransactionManager txManager;

    private final StringFieldRepository stringFieldRepository;
    private final LabelFieldRepository labelFieldRepository;
    private final FieldValueRepository fieldValueRepository;
    private final MessageQueue messageQueue;
    private final FieldDefinitions fieldDefinitions;

    @Override
    public void assignToField(ProjectId projectId, Collection<FieldAssignmentCommand> assignments) {
        var strategyFactory = new AssignmentStrategyFactory();
        txManager.useTransaction(() -> {
            assignments
                    .forEach(cmd -> {
                        Collection<MessageQueue.Message> msgs = strategyFactory
                                .getAssignmentStrategy(AssignmentType.PROJECT, cmd.getFieldType())
                                .assign(projectId, cmd);
                        msgs.forEach(messageQueue::publish);
                    });
        });

    }

    @Override
    public void assignToField(IssueId issueId, Collection<FieldAssignmentCommand> assignments) {
        var strategyFactory = new AssignmentStrategyFactory();
        txManager.useTransaction(() -> {
            checkRequiredFieldsProvided(issueId, assignments);

            assignments
                    .forEach(cmd -> {
                        Collection<MessageQueue.Message> msgs = strategyFactory
                                .getAssignmentStrategy(AssignmentType.ISSUE, cmd.getFieldType())
                                .assign(issueId, cmd);
                        msgs.forEach(messageQueue::publish);
                    });
        });
    }

    private void checkRequiredFieldsProvided(IssueId issueId, Collection<FieldAssignmentCommand> assignments) {
        var missingRequiredFields = fieldDefinitions.findMissingRequiredFields(
                issueId.getProject(),
                assignments.stream().map(FieldAssignmentCommand::getFieldId).collect(Collectors.toList()));
        if(!missingRequiredFields.isEmpty()) {
            throw new RequiredFieldsMissingException(missingRequiredFields);
        }
    }

    private interface AssignFieldStrategy<V, A> {
        Collection<MessageQueue.Message> assign(A assignee, FieldAssignmentCommand<V> cmd);
    }

    private class AssignmentStrategyFactory {
        private Map<AssignmentKey, Supplier<AssignFieldStrategy>> strategies = Map.of(
                new AssignmentKey(AssignmentType.ISSUE, FieldType.STRING_FIELD), AssignStringFieldToIssue::new,
                new AssignmentKey(AssignmentType.ISSUE, FieldType.LABEL_FIELD), AssignLabelFieldToIssue::new,
                new AssignmentKey(AssignmentType.PROJECT, FieldType.STRING_FIELD), AssignStringFieldToProject::new,
                new AssignmentKey(AssignmentType.PROJECT, FieldType.LABEL_FIELD), AssignLabelFieldToProject::new
        );

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

    private class AssignStringFieldToProject implements AssignFieldStrategy<String, ProjectId> {
        @Override
        public Collection<MessageQueue.Message> assign(ProjectId projectId, FieldAssignmentCommand<String> cmd) {
            StringField field = stringFieldRepository.findStringField(cmd.getFieldId());
            FieldValue<String> value = field
                    .value(cmd.getValue())
                    .getOrElseThrow(validationResult -> new IllegalStateException("TODO"));
            fieldValueRepository.removeOldValues(cmd.getFieldId(), projectId);
            var fieldValueId = fieldValueRepository.saveStringValue(projectId, value);
            return singleton(new FieldAssignedMessage(singleton(fieldValueId)));
        }
    }

    private class AssignStringFieldToIssue implements AssignFieldStrategy<String, IssueId> {
        public Collection<MessageQueue.Message> assign(IssueId issueId, FieldAssignmentCommand<String> cmd) {
            StringField field = stringFieldRepository.findStringField(cmd.getFieldId());
            FieldValue<String> value = field
                    .value(cmd.getValue())
                    .getOrElseThrow(validationResult -> new IllegalStateException("TODO"));
            fieldValueRepository.removeOldValues(cmd.getFieldId(), issueId);
            var fieldValueId = fieldValueRepository.saveStringValue(issueId, value);
            return singleton(new FieldAssignedMessage(singleton(fieldValueId)));
        }
    }

    private class AssignLabelFieldToProject implements AssignFieldStrategy<LabelValues, ProjectId> {
        @Override
        public Collection<MessageQueue.Message> assign(ProjectId projectId, FieldAssignmentCommand<LabelValues> cmd) {
            LabelField field = labelFieldRepository.findLabelField(cmd.getFieldId());
            FieldValue<LabelValues> value = field
                    .value(cmd.getValue())
                    .getOrElseThrow(validationResult -> new IllegalStateException("TODO"));
            fieldValueRepository.removeOldValues(cmd.getFieldId(), projectId);
            var valueIds = fieldValueRepository.saveLabelValue(projectId, value);
            return singleton(new FieldAssignedMessage(valueIds));
        }
    }

    private class AssignLabelFieldToIssue implements AssignFieldStrategy<LabelValues, IssueId> {
        public Collection<MessageQueue.Message> assign(IssueId issueId, FieldAssignmentCommand<LabelValues> cmd) {
            LabelField field = labelFieldRepository.findLabelField(cmd.getFieldId());
            FieldValue<LabelValues> value = field
                    .value(cmd.getValue())
                    .getOrElseThrow(validationResult -> new IllegalStateException("TODO"));
            fieldValueRepository.removeOldValues(cmd.getFieldId(), issueId);
            var valueIds = fieldValueRepository.saveLabelValue(issueId, value);
            return singleton(new FieldAssignedMessage(valueIds));
        }
    }

    private enum AssignmentType {
        ISSUE,
        PROJECT
    }
}
