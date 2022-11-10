package com.lgtm.easymoney.exceptions;

public class InvalidTokenRequestException extends RuntimeException {
  private final String token;
  private final String message;

  public InvalidTokenRequestException(String message, String token) {
    super(String.format("%s: %s", message, token));
    this.token = token;
    this.message = message;
  }
}
