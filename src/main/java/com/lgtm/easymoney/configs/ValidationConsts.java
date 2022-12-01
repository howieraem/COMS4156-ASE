package com.lgtm.easymoney.configs;

/** Constants for request data validation.
 * <a href="https://qr.ae/pvJXyd">Account number length constraints</a>
 */
public class ValidationConsts {
  private ValidationConsts() {}

  public static final int MIN_ACCOUNT_NUMBER_LEN = 1;
  public static final int MAX_ACCOUNT_NUMBER_LEN = 17;
  public static final int ROUTING_NUMBER_LEN = 9;
  public static final int PHONE_LEN = 10;

  private static final String DIGITS_REGEX = "[\\d]{";

  public static final String ACCOUNT_NUMBER_REGEX =
      DIGITS_REGEX + MIN_ACCOUNT_NUMBER_LEN + "," + MAX_ACCOUNT_NUMBER_LEN + "}";
  public static final String ROUTING_NUMBER_REGEX = DIGITS_REGEX + ROUTING_NUMBER_LEN + "}";
  public static final String PHONE_NUMBER_REGEX = DIGITS_REGEX + PHONE_LEN + "}";
  public static final int MAX_USER_TYPE_LEN = 9;

  public static final String ACCOUNT_NAME_REGEX = "^[A-Za-z\\s]+$";
}
