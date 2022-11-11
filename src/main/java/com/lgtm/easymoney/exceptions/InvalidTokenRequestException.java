package com.lgtm.easymoney.exceptions;

import java.io.Serial;

/** Exception thrown when client sends an invalid jwt. */
public class InvalidTokenRequestException extends RuntimeException {
  @Serial
  private static final long serialVersionUID = 1L;

  /** Constructor. */
  public InvalidTokenRequestException(String message, String token) {
    super(String.format("%s: %s", message, token));
  }
}
