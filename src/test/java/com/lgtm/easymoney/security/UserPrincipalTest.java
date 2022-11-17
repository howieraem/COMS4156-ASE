package com.lgtm.easymoney.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.lgtm.easymoney.configs.UserTestConfig;
import com.lgtm.easymoney.models.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

/** Test the user principal. */
@RunWith(SpringRunner.class)
public class UserPrincipalTest {
  @Test
  public void testEqual() {
    User u = new User();
    u.setId(UserTestConfig.PERSON1_PRINCIPAL.getId());
    UserPrincipal p = new UserPrincipal(u);

    assertEquals(UserTestConfig.PERSON1_PRINCIPAL, UserTestConfig.PERSON1_PRINCIPAL);
    assertEquals(UserTestConfig.PERSON1_PRINCIPAL, p);
  }

  @Test
  public void testNotEqual() {
    assertNotEquals(UserTestConfig.PERSON1_PRINCIPAL, null);
  }
}
