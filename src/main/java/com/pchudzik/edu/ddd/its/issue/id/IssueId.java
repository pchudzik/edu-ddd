package com.pchudzik.edu.ddd.its.issue.id;

import com.pchudzik.edu.ddd.its.infrastructure.domain.Id;
import com.pchudzik.edu.ddd.its.project.ProjectId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode
@RequiredArgsConstructor
public class IssueId implements Id {
    @Getter
    private final ProjectId project;
    @Getter
    private final int issue;

    @Override
    public String toString() {
        return project.getValue() + "-" + issue;
    }
}
