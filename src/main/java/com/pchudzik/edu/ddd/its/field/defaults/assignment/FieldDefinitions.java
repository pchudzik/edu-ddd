package com.pchudzik.edu.ddd.its.field.defaults.assignment;

import com.pchudzik.edu.ddd.its.field.FieldId;
import com.pchudzik.edu.ddd.its.field.read.AvailableFields;
import com.pchudzik.edu.ddd.its.project.ProjectId;

import java.util.Collection;

public interface FieldDefinitions {
    void assignDefaultFields(Collection<FieldId> fieldIds);
    void assignDefaultFields(ProjectId projectId, Collection<FieldId> fieldIds);

    void removeDefaultFields(FieldId fieldId);
    void removeDefaultFields(ProjectId projectId, FieldId fieldId);

    Collection<AvailableFields.FieldDto> findDefaultFields();
    Collection<AvailableFields.FieldDto> findDefaultFields(ProjectId projectId);

    AssignmentValidator checkAssignments(Collection<FieldId> toAssignFieldIds);
    AssignmentValidator checkAssignments(ProjectId projectId, Collection<FieldId> toAssignFieldIds);

    interface AssignmentValidator {
        Collection<FieldId> missingRequiredFields();
        Collection<FieldId> notAvailableFields();

        default boolean allRequiredFieldsProvided() {
            return missingRequiredFields().isEmpty();
        }

        default boolean onlyAvailableFieldsProvided() {
            return notAvailableFields().isEmpty();
        }
    }
}
