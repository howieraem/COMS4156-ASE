package com.lgtm.easymoney.enums.validator;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * validate enum.
 */
public class ValueOfEnumValidator implements ConstraintValidator<ValueOfEnum, String> {
  private Set<String> acceptedValues;

  @Override
  public void initialize(ValueOfEnum annotation) {
    acceptedValues = Stream.of(annotation.enumClass().getEnumConstants())
        .map(Enum::name)
        .collect(Collectors.toCollection(HashSet::new));
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    return value != null && acceptedValues.contains(value.toUpperCase());
  }
}
