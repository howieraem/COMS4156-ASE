package com.lgtm.easymoney.exceptions;

/** Exception thrown when client sends an invalid jwt. */
public class InvalidTokenRequestException extends RuntimeException {
  private final String token;
  private final String message;

  /** Constructor. */
  public InvalidTokenRequestException(String message, String token) {
    super(String.format("%s: %s", message, token));
    this.token = token;
    this.message = message;
  }
}
