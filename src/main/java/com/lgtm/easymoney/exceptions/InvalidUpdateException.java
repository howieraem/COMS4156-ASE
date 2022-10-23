package com.lgtm.easymoney.exceptions;

import java.io.Serial;
import lombok.Getter;

/**
 * invalid update of resource exception.
 */
@Getter
public class InvalidUpdateException extends RuntimeException {
  @Serial
  private static final long serialVersionUID = 1L;
  private final String fieldName;

  /**
   * contrudctor of invalid update ex.
   *
   * @param resourceName resource name, i.e.user
   * @param resourceId resource id, i.e uid
   * @param fieldName field name,
   * @param fieldValue field val
   */
  public InvalidUpdateException(
          String resourceName, Object resourceId,
          String fieldName, Object fieldValue) {
    super(String.format("Invalid change to %s with ID %s given %s: '%s'",
            resourceName, resourceId, fieldName, fieldValue));
    this.fieldName = fieldName;
  }
}
