package com.pchudzik.edu.ddd.its.project

import com.pchudzik.edu.ddd.its.infrastructure.domain.ValidationException
import spock.lang.Specification

class ProjectTest extends Specification {
    def "ValidationException is raised when project name is empty"() {
        when:
        new Project().projectName(name)

        then:
        thrown(ValidationException)

        where:
        name << ["", "  ", null]
    }
}
