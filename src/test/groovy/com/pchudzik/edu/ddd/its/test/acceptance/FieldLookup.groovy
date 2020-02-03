package com.pchudzik.edu.ddd.its.test.acceptance

import com.pchudzik.edu.ddd.its.field.FieldId
import com.pchudzik.edu.ddd.its.infrastructure.InjectorFactory
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.mapper.RowMapper

class FieldLookup {

    static List<FieldId> findAllFieldIds() {
        InjectorFactory
                .injector()
                .getInstance(Jdbi)
                .withHandle({ h ->
                    h
                            .select("select id, version from field")
                            .map({ rs, ctx ->
                                new FieldId(
                                        UUID.fromString(rs.getString("id")),
                                        rs.getInt("version")
                                )
                            } as RowMapper<FieldId>)
                            .list()
                })
    }

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
