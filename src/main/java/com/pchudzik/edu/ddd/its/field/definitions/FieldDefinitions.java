package com.pchudzik.edu.ddd.its.field.definitions;

import com.pchudzik.edu.ddd.its.field.FieldId;
import com.pchudzik.edu.ddd.its.field.read.AvailableFields;
import com.pchudzik.edu.ddd.its.issue.id.IssueId;
import com.pchudzik.edu.ddd.its.project.ProjectId;

import java.util.Collection;

public interface FieldDefinitions {
    void assignDefaultFields(ProjectId projectId, Collection<FieldId> fieldIds);

    void assignDefaultFields(IssueId issueId, Collection<FieldId> fieldIds);

    void removeDefaultFields(ProjectId projectId, FieldId fieldId);

    void removeDefaultFields(IssueId projectId, FieldId fieldId);

    Collection<AvailableFields.FieldDto> findDefaultFields(ProjectId projectId);

    Collection<AvailableFields.FieldDto> findDefaultFields(IssueId issueId);
}
