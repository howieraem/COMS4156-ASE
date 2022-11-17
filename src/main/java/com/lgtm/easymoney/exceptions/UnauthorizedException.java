package com.lgtm.easymoney.exceptions;

import lombok.Getter;

/**
 * Exception thrown when a user tried to perform unauthorized operations.
 */
@Getter
public class UnauthorizedException extends RuntimeException {
  public UnauthorizedException(Long uid, String resource, Object resourceId) {
    super(String.format(
        "User with ID %s is not authorized to access %s with ID %s", uid, resource, resourceId));
  }
}
