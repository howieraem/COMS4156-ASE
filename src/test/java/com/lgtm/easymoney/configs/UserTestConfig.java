package com.lgtm.easymoney.configs;

import com.lgtm.easymoney.models.Account;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.security.UserPrincipal;
import org.springframework.boot.test.context.TestConfiguration;

/** Configs of clients for tests. */
@TestConfiguration
public class UserTestConfig {
  public static final User PERSON1 = User.ofTest(
      1L, "1@a.com", "1", "PERSONAL", "",
      Account.ofTest("a", "1"));
  public static final User PERSON2 = User.ofTest(
      2L, "2@a.com", "2", "PERSONAL", "",
      Account.ofTest("b", "2"));
  public static final User BIZ_USR = User.ofTest(
      3L, "3@b.com", "3", "BUSINESS", "free goods!",
      Account.ofTest("c", "3"));
  public static final User FIN_USR = User.ofTest(
      4L, "4@c.com", "4", "FINANCIAL", "low loan interest rate",
      Account.ofTest("d", "4"));

  public static final UserPrincipal PERSON1_PRINCIPAL = new UserPrincipal(PERSON1);
  public static final UserPrincipal PERSON2_PRINCIPAL = new UserPrincipal(PERSON2);
  public static final UserPrincipal BIZ_PRINCIPAL = new UserPrincipal(BIZ_USR);
  public static final UserPrincipal FIN_PRINCIPAL = new UserPrincipal(FIN_USR);
}
