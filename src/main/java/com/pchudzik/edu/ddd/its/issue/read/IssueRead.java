package com.pchudzik.edu.ddd.its.issue.read;

import com.pchudzik.edu.ddd.its.issue.id.IssueId;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public interface IssueRead {
    IssueDto findIssue(IssueId issueId);

    @Getter
    @RequiredArgsConstructor
    class IssueDto {
        private final IssueId id;
        private final String title;
    }
}
