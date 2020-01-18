package com.pchudzik.edu.ddd.its.issue.id;

import com.pchudzik.edu.ddd.its.project.ProjectId;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class IssueId {
    private final ProjectId project;
    private final int issue;

    @Override
    public String toString() {
        return project.getValue() + "-" + issue;
    }
}
