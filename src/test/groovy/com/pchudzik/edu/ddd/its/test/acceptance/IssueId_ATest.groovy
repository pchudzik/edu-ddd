package com.pchudzik.edu.ddd.its.test.acceptance

import com.pchudzik.edu.ddd.its.infrastructure.db.DbSpecification
import com.pchudzik.edu.ddd.its.issue.id.IssueIdGenerator

class IssueId_ATest extends DbSpecification {
    private def issueIdGenerator = injector.getInstance(IssueIdGenerator)

    def "can create issue ids when new project is created"() {
        given:
        def projectId = Fixtures.projectFixture().creator()
                    .id("ABC")
                    .create()

        when:
        def id1 = issueIdGenerator.next(projectId)
        def id2 = issueIdGenerator.next(projectId)

        then:
        id1.toString() == "ABC-1"
        id2.toString() == "ABC-2"
    }
}
