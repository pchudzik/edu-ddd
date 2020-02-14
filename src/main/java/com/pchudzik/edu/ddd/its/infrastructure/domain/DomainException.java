package com.pchudzik.edu.ddd.its.infrastructure.domain;

public abstract class DomainException extends RuntimeException {
    public DomainException() {
    }

    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }

    public DomainException(Throwable cause) {
        super(cause);
    }
}
