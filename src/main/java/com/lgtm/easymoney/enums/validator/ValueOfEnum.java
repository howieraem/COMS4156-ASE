package com.lgtm.easymoney.enums.validator;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;


/**
 * value of enum.
 */
@Target(FIELD)
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = ValueOfEnumValidator.class)
public @interface ValueOfEnum {
  /**
   * enum class.
   *
   * @return null
   */
  Class<? extends Enum<?>> enumClass();
  /**
   * err msg.
   *
   * @return String of message error
   */
  String message() default "Value is not in enum {enumClass}!";
  /**
   * groups class.
   *
   * @return null
   */
  Class<?>[] groups() default {};
  /**
   * payload class.
   *
   * @return null
   */
  Class<? extends Payload>[] payload() default {};
}
