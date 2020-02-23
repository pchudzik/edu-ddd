package com.pchudzik.edu.ddd.its.issue.read;

import com.pchudzik.edu.ddd.its.issue.id.IssueId;
import com.pchudzik.edu.ddd.its.issue.read.IssueRead.IssueDto;

import java.util.Optional;

interface IssueReadRepository {
    Optional<IssueDto> findOne(IssueId issueId);
}
