package com.lgtm.easymoney.exceptions;

import java.io.Serial;

/**
 * resource not found exception wrapper.
 */
public class ResourceNotFoundException extends RuntimeException {
  @Serial
  private static final long serialVersionUID = 1L;

  private final String resourceName;
  private final String fieldName;
  private final Object fieldValue;

  /**
   * constructor.
   *
   * @param resourceName name of the resource, i.e. user, account, group, etc.
   * @param fieldName property name, e.g. user's email
   * @param fieldValue prop value, e.g. kenXiongZuiBiuBi@amazonian.com
   */
  public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
    super(String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue));
    this.resourceName = resourceName;
    this.fieldName = fieldName;
    this.fieldValue = fieldValue;
  }

  public String getResourceName() {
    return resourceName;
  }

  public String getFieldName() {
    return fieldName;
  }

  public Object getFieldValue() {
    return fieldValue;
  }
}
