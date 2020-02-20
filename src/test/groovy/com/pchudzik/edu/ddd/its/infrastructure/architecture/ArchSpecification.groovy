package com.pchudzik.edu.ddd.its.infrastructure.architecture

import com.tngtech.archunit.core.importer.ClassFileImporter
import spock.lang.Specification

class ArchSpecification extends Specification {
    protected def package_ = new ClassFileImporter().importPackages("com.pchudzik.edu.ddd.its")
}
