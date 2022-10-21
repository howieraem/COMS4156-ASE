package com.lgtm.easymoney.exceptions;

import java.io.Serial;
import lombok.Getter;

/**
 * invalid update of resource exeception.
 */
@Getter
public class InvalidUpdateException extends RuntimeException {
  @Serial
  private static final long serialVersionUID = 1L;

  private final String resourceName;
  private final Object resourceId;
  private final String fieldName;
  private final Object fieldValue;

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
    super(String.format("Invalid change to %s ID %s with %s: '%s' provided",
            resourceName, resourceId, fieldName, fieldValue));
    this.resourceName = resourceName;
    this.resourceId = resourceId;
    this.fieldName = fieldName;
    this.fieldValue = fieldValue;
  }
}
