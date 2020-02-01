package com.pchudzik.edu.ddd.its.test.acceptance

import com.pchudzik.edu.ddd.its.field.FieldId
import com.pchudzik.edu.ddd.its.infrastructure.InjectorFactory
import org.jdbi.v3.core.Jdbi

class FieldLookup {
    static int findNumberOfFieldsInAnyVersion(FieldId fieldId) {
        InjectorFactory
                .injector()
                .getInstance(Jdbi)
                .withHandle({ h ->
                    h
                            .select("select * from field where id = :id")
                            .bind("id", fieldId.value)
                            .mapToMap()
                            .list()
                            .size()
                })
    }
}
