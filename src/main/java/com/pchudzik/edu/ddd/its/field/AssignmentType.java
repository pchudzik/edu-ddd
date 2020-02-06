package com.pchudzik.edu.ddd.its.field;

import com.pchudzik.edu.ddd.its.issue.id.IssueId;
import com.pchudzik.edu.ddd.its.project.ProjectId;

public enum AssignmentType {
    ISSUE,
    PROJECT;

    public static AssignmentType typeFor(Class clazz) {
        if (IssueId.class.equals(clazz)) {
            return ISSUE;
        }

        if (ProjectId.class.equals(clazz)) {
            return PROJECT;
        }

        throw new IllegalArgumentException("Unsupported assignee type " + clazz);
    }
}
