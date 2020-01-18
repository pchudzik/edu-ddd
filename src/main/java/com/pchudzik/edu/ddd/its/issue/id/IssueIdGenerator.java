package com.pchudzik.edu.ddd.its.issue.id;

import com.pchudzik.edu.ddd.its.project.ProjectId;

public interface IssueIdGenerator {
    IssueId next(ProjectId projectId);
}
