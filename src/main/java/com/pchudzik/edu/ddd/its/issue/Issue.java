package com.pchudzik.edu.ddd.its.issue;

import com.pchudzik.edu.ddd.its.issue.id.IssueId;

class Issue {
    private final IssueId id;

    private final String title;

    Issue(IssueId id, String title) {
        this.id = id;
        this.title = title;
    }
}
