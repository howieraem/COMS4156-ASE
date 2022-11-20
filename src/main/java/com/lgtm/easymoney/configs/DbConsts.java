package com.lgtm.easymoney.configs;

import java.util.Map;

/** Constants for database schema. */
public class DbConsts {
  private DbConsts() {}

  public static final String USER_EMAIL_CONSTRAINT = "user_email_uniq_idx";
  public static final String ACCOUNT_NUMBERS_CONSTRAINT = "account_numbers_uniq_idx";
  public static final String GROUP_NAME_CONSTRAINT = "group_name_uniq_idx";
  public static final String FRIENDSHIP_PRIMARY_CONSTRAINT = "PRIMARY";

  public static final Map<String, String[]> CONSTRAINTS_FIELDS = Map.of(
      USER_EMAIL_CONSTRAINT, new String[]{"email"},
      ACCOUNT_NUMBERS_CONSTRAINT, new String[]{"accountNumber", "routingNumber"},
      GROUP_NAME_CONSTRAINT, new String[]{"name"},
      FRIENDSHIP_PRIMARY_CONSTRAINT, new String[]{"uid"}
  );

  public static final Map<String, String> CONSTRAINTS_ERR_MSGS = Map.of(
      USER_EMAIL_CONSTRAINT, "Email already registered!",
      ACCOUNT_NUMBERS_CONSTRAINT, "Bank account already registered!",
      GROUP_NAME_CONSTRAINT, "A group with that name already created!",
      FRIENDSHIP_PRIMARY_CONSTRAINT, "Friendship already requested/accepted!"
  );
}
