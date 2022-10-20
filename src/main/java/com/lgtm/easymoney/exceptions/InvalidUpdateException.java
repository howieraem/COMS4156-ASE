package com.lgtm.easymoney.exceptions;

import lombok.Getter;

import java.io.Serial;

@Getter
public class InvalidUpdateException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String resourceName;
    private final Object resourceId;
    private final String fieldName;
    private final Object fieldValue;

    public InvalidUpdateException(String resourceName, Object resourceId, String fieldName, Object fieldValue) {
        super(String.format("Invalid change to %s with ID %s with %s update: '%s'", resourceName, resourceId, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.resourceId = resourceId;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
}
