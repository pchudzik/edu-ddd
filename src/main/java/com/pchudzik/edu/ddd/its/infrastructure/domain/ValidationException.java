package com.pchudzik.edu.ddd.its.infrastructure.domain;

public class ValidationException extends DomainException {
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public static class EmptyValueValidationException extends ValidationException {
        public EmptyValueValidationException(String message) {
            super(message);
        }
    }
}
