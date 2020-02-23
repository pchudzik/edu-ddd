package com.pchudzik.edu.ddd.its.infrastructure.domain;

public class NoSuchObjectException extends DomainException {
    private final Id id;

    public NoSuchObjectException(Id id) {
        super("No object with id " + id);
        this.id = id;
    }
}
