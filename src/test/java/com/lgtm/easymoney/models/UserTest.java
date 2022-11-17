package com.lgtm.easymoney.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.lgtm.easymoney.configs.UserTestConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

/** Test the user model. */
@RunWith(SpringRunner.class)
public class UserTest {
  @Test
  public void testUserEqual() {
    User u1 = UserTestConfig.PERSON1;
    User u1c = UserTestConfig.PERSON1_PRINCIPAL.get();
    u1c.setPassword("new password");
    assertEquals(u1, u1c);
  }

  @Test
  public void testUserNotEqual() {
    User u = UserTestConfig.PERSON1;

    assertNotEquals(null, u);
    assertNotEquals(u, new Group());
  }
}
