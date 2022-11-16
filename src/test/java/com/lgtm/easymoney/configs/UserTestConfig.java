package com.lgtm.easymoney.configs;

import com.lgtm.easymoney.models.Account;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.security.UserPrincipal;
import java.util.List;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/** Configs of clients for tests. */
@TestConfiguration
public class UserTestConfig {
  private static final Account PERSON1_ACC = Account.ofTest("a");
  private static final Account PERSON2_ACC = Account.ofTest("b");
  private static final Account BIZ_ACC = Account.ofTest("c");
  private static final Account FIN_ACC = Account.ofTest("d");
  public static final String PERSON1_EMAIL = "1@a.com";
  public static final String PERSON2_EMAIL = "2@a.com";
  public static final String BIZ_EMAIL = "3@b.com";
  public static final String FIN_EMAIL = "4@c.com";

  public static final User PERSON1 = User.ofTest(
      1L, PERSON1_EMAIL, "1", "PERSONAL", "", PERSON1_ACC);
  public static final User PERSON2 = User.ofTest(
      2L, PERSON2_EMAIL, "2", "PERSONAL", "", PERSON2_ACC);
  public static final User BIZ_USR = User.ofTest(
      3L, BIZ_EMAIL, "3", "BUSINESS", "free goods!", BIZ_ACC);
  public static final User FIN_USR = User.ofTest(
      4L, FIN_EMAIL, "4", "FINANCIAL", "", FIN_ACC);

  public static final UserPrincipal PERSON_PRINCIPAL1 = new UserPrincipal(PERSON1);
  public static final UserPrincipal PERSON_PRINCIPAL2 = new UserPrincipal(PERSON2);
  public static final UserPrincipal BIZ_PRINCIPAL = new UserPrincipal(BIZ_USR);
  public static final UserPrincipal FIN_PRINCIPAL = new UserPrincipal(FIN_USR);
}
