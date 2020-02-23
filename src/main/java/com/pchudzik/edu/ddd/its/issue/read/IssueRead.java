package com.pchudzik.edu.ddd.its.issue.read;

import com.pchudzik.edu.ddd.its.issue.id.IssueId;
import com.pchudzik.edu.ddd.its.user.access.Access.Principal;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public interface IssueRead {
    IssueDto findIssue(IssueLookupCommand lookupCommand);

    @Getter
    @RequiredArgsConstructor
    class IssueLookupCommand {
        private final IssueId issueId;
        private final Principal principal;
    }

    @Getter
    @RequiredArgsConstructor
    class IssueDto {
        private final IssueId id;
        private final String title;
    }
}
