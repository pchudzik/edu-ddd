package com.pchudzik.edu.ddd.its.project;

import com.pchudzik.edu.ddd.its.infrastructure.domain.ValidationException;
import org.apache.commons.lang3.StringUtils;

public class ProjectId {
    private final String id;

    public ProjectId(String id) {
        this.id = checkProjectName(id);
    }

    public String getValue() {
        return id;
    }

    private static String checkProjectName(String id) {
        if (StringUtils.isBlank(id)) {
            throw new ValidationException.EmptyValueValidationException("id can not be empty");
        }
        if (id.length() < 3 || id.length() > 5) {
            throw new TooLongProjectIdValidationException("Id has invalid length. Id '" + id + "' must be 3 >= " + id.length() + " >= 5");
        }

        if (!StringUtils.isAlphanumeric(id)) {
            throw new InvalidCharactersValidationException("Id contains invalid characters. Id '" + id + "' can contain only A-Z letters and digits");
        }

        return id.toUpperCase();
    }

    private static class TooLongProjectIdValidationException extends ValidationException {
        public TooLongProjectIdValidationException(String msg) {
            super(msg);
        }
    }

    private static class InvalidCharactersValidationException extends ValidationException {
        public InvalidCharactersValidationException(String msg) {
            super(msg);
        }
    }
}
