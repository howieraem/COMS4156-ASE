package com.lgtm.easymoney.exceptions;

import java.io.Serial;
import lombok.Getter;

/**
 * invalid update of resource exeception.
 */
@Getter
public class DatabaseFailureException extends RuntimeException {
  @Serial
  private static final long serialVersionUID = 1L;

  private final String resourceName;
  private final String operation;

  /**
   * handling database failures.
   *
   * @param resourceName resource name, i.e.user
   */
  public DatabaseFailureException(String resourceName, String operation) {
    super(String.format("Database Failure occured when performing %s on '%s'",
            resourceName, operation));
    this.resourceName = resourceName;
    this.operation = operation;
  }
}
