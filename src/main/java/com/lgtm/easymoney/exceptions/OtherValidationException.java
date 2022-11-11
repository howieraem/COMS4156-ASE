package com.lgtm.easymoney.exceptions;

import java.io.Serial;
import java.util.List;
import lombok.Getter;

/**
 * Exception for violating advanced validation rules (e.g., cross-parameters)
 * that are hard to implement with Spring validation.
 */
@Getter
public class OtherValidationException extends RuntimeException {
  @Serial
  private static final long serialVersionUID = 1L;

  private final List<String> fieldNames;

  public OtherValidationException(String message, List<String> fieldNames) {
    super(message);
    this.fieldNames = fieldNames;
  }
}
