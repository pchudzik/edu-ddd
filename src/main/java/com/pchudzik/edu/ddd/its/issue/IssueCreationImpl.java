package com.pchudzik.edu.ddd.its.issue;

import com.pchudzik.edu.ddd.its.field.FieldAssignment;
import com.pchudzik.edu.ddd.its.field.FieldAssignmentCommandFactory;
import com.pchudzik.edu.ddd.its.issue.id.IssueId;
import com.pchudzik.edu.ddd.its.issue.id.IssueIdGenerator;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import java.util.stream.Collectors;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
class IssueCreationImpl implements IssueCreation {
    private final IssueIdGenerator idGenerator;
    private final IssueRepository issueRepository;
    private final FieldAssignment fieldAssignment;


    @Override
    public IssueId createIssue(IssueCreationCommand cmd) {

        IssueId issueId = idGenerator.next(cmd.getProjectId());
        Issue issue = new Issue(issueId, cmd.getTitle());
        issueRepository.saveIssue(issue);

        fieldAssignment
                .assignFieldValues(
                        issueId,
                        cmd
                                .getFieldAssignments().stream()
                                .map(FieldAssignmentCommandFactory::buildAssignmentCommand)
                                .collect(Collectors.toList()));

        return issueId;
    }

    @Override
    public void updateIssue(IssueId issueId, IssueUpdateCommand cmd) {
        Issue issue = issueRepository.findIssue(issueId);
        issue.title(cmd.getTitle());
        issueRepository.saveIssue(issue);

        fieldAssignment
                .assignFieldValues(
                        issueId,
                        cmd
                                .getFieldAssignments().stream()
                                .map(FieldAssignmentCommandFactory::buildAssignmentCommand)
                                .collect(Collectors.toList()));
    }
}
