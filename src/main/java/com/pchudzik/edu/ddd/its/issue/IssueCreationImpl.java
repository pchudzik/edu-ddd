package com.pchudzik.edu.ddd.its.issue;

import com.pchudzik.edu.ddd.its.field.FieldAssignment;
import com.pchudzik.edu.ddd.its.field.LabelValues;
import com.pchudzik.edu.ddd.its.field.defaults.assignment.FieldDefinitions;
import com.pchudzik.edu.ddd.its.infrastructure.db.TransactionManager;
import com.pchudzik.edu.ddd.its.issue.id.IssueId;
import com.pchudzik.edu.ddd.its.issue.id.IssueIdGenerator;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import java.util.Collection;
import java.util.stream.Collectors;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
class IssueCreationImpl implements IssueCreation {
    private final TransactionManager txManager;
    private final IssueRepository issueRepository;
    private final IssueIdGenerator idGenerator;

    private final FieldAssignment fieldAssignment;
    private final FieldDefinitions fieldDefinitions;

    @Override
    public IssueId createIssue(IssueCreationCommand cmd) {
        return txManager.inTransaction(() -> {
            IssueId issueId = idGenerator.next(cmd.getProjectId());
            issueRepository.saveIssue(issueId, cmd.getTitle());

            fieldAssignment
                    .assignToField(
                            issueId,
                            cmd
                                    .getFieldAssignments().stream()
                                    .map(this::buildAssignmentCommand)
                                    .collect(Collectors.toList()));

            return issueId;
        });
    }

    private FieldAssignment.FieldAssignmentCommand<?> buildAssignmentCommand(FieldToIssueAssignmentCommand assignment) {
        switch (assignment.getFieldType()) {
            case STRING_FIELD:
                return new FieldAssignment.StringFieldAssignmentCommand(
                        assignment.getFieldId(),
                        (String) assignment.getValue());
            case LABEL_FIELD:
                return new FieldAssignment.LabelFieldAssignmentCommand(
                        assignment.getFieldId(),
                        convertRawValues((Collection<String>) assignment.getValue()));
            default:
                throw new IllegalArgumentException("Unsupported field type " + assignment.getFieldType());
        }
    }

    private LabelValues convertRawValues(Collection<String> values) {
        return LabelValues.of(values.stream().map(LabelValues.LabelValue::of).collect(Collectors.toList()));
    }
}
