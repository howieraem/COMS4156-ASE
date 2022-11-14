package com.lgtm.easymoney.exceptions;

import java.io.Serial;
import lombok.Getter;

/**
 * Inapplicable operation exception.
 * Use case example: friendship functionalities not available for business/financial clients.
 */
@Getter
public class InapplicableOperationException extends RuntimeException {
  @Serial
  private static final long serialVersionUID = 1L;

  private final String fieldName;

  /**
   * Constructor of inapplicable operation exception.
   *
   * @param resourceName resource name, i.e. user
   * @param resourceId resource id, i.e. uid
   * @param fieldName field name (of request)
   * @param operationName operation/method name
   */
  public InapplicableOperationException(
      String resourceName, Object resourceId,
      String fieldName, String operationName) {
    super(String.format("Operation '%s' is inapplicable to %s with ID %s.",
        operationName, resourceName, resourceId));
    this.fieldName = fieldName;
  }
}
