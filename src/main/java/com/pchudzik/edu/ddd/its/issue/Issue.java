package com.pchudzik.edu.ddd.its.issue;

import com.pchudzik.edu.ddd.its.issue.id.IssueId;
import lombok.AccessLevel;
import lombok.Getter;

@Getter(AccessLevel.PACKAGE)
class Issue {
    private final IssueId id;

    private String title;

    Issue(IssueId id, String title) {
        this.id = id;
        this.title = title;
    }

    public void title(String title) {
        this.title = title;
    }
}
