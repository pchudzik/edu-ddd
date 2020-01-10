package com.pchudzik.edu.ddd.its.infrastructure;

public class ValidationException extends RuntimeException {
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
