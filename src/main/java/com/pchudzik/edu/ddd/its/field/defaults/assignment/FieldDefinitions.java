package com.pchudzik.edu.ddd.its.field.defaults.assignment;

import com.pchudzik.edu.ddd.its.field.FieldId;
import com.pchudzik.edu.ddd.its.field.read.AvailableFields;
import com.pchudzik.edu.ddd.its.project.ProjectId;

import java.util.Collection;
import java.util.List;

public interface FieldDefinitions {
    void assignDefaultFields(Collection<FieldId> fieldIds);
    void assignDefaultFields(ProjectId projectId, Collection<FieldId> fieldIds);

    void removeDefaultFields(FieldId fieldId);
    void removeDefaultFields(ProjectId projectId, FieldId fieldId);

    Collection<AvailableFields.FieldDto> findDefaultFields();
    Collection<AvailableFields.FieldDto> findDefaultFields(ProjectId projectId);

    List<FieldId> findMissingRequiredFields(Collection<FieldId> fields);
    List<FieldId> findMissingRequiredFields(ProjectId projectId, Collection<FieldId> fields);
}
