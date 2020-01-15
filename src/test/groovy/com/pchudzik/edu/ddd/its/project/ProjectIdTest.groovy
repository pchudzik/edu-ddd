package com.pchudzik.edu.ddd.its.project

import com.pchudzik.edu.ddd.its.infrastructure.domain.ValidationException
import spock.lang.Specification

class ProjectIdTest extends Specification {
    def "ValidationException is raised when id is empty"() {
        when:
        new ProjectId(id)

        then:
        thrown(ValidationException)

        where:
        id << ["", "  ", null]
    }

    def "ValidationException is raised when id is on invalid length"() {
        when:
        new ProjectId(id)

        then:
        thrown(ValidationException)

        where:
        id << ["AB", "ABCDEF"]
    }

    def "ValidationException is raised when id is not alphanumeric"() {
        when:
        new ProjectId(id)

        then:
        thrown(ValidationException)

        where:
        id << ["ABC.", "ABC_+"]
    }

    def "id is converted to upper case"() {
        when:
        def id = new ProjectId("abc")

        then:
        id.value == "ABC"
    }

}
