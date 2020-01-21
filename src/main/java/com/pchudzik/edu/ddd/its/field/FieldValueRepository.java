package com.pchudzik.edu.ddd.its.field;

import com.pchudzik.edu.ddd.its.issue.id.IssueId;

interface FieldValueRepository {
    <T, A> void save(IssueId issueId, FieldValue<A, T> value);
}
