package com.lgtm.easymoney.integrations;

import com.lgtm.easymoney.configs.DbConsts;
import com.lgtm.easymoney.controllers.AuthController;
import com.lgtm.easymoney.controllers.GroupController;
import com.lgtm.easymoney.exceptions.handlers.ControllerExceptionHandler;
import com.lgtm.easymoney.models.Account;
import com.lgtm.easymoney.models.Group;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.CreateGroupReq;
import com.lgtm.easymoney.payload.RegisterReq;
import com.lgtm.easymoney.repositories.GroupRepository;
import com.lgtm.easymoney.repositories.UserRepository;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DataIntegrityTest {
  @Autowired
  private ControllerExceptionHandler controllerExceptionHandler;

  @Autowired
  private AuthController authController;

  @Autowired
  private GroupController groupController;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private GroupRepository groupRepository;

  private static User user;
  private static Group group;
  private static RegisterReq registerReq;
  private static CreateGroupReq createGroupReq;

  private static boolean setupDone = false;

  @Before
  public void setup() {
    if (setupDone) {
      // run setup() once only
      return;
    }
    user = new User();
    user.setEmail("a@a.com");
    user.setPassword("a");
    user.setTypeByStr("PERSONAL");
    Account a1 = new Account();
    a1.setAccountName("a");
    a1.setAccountNumber("123");
    a1.setRoutingNumber("123456789");
    user.setAccount(a1);
    user = userRepository.saveAndFlush(user);

    Set<User> users = new HashSet<>();
    users.add(user);

    group = new Group();
    group.setName("g1");
    group.setGroupUsers(users);
    group = groupRepository.saveAndFlush(group);

    registerReq = new RegisterReq();
    registerReq.setEmail("b@b.com");
    registerReq.setPassword("b");
    registerReq.setUserType("business");
    registerReq.setAccountName("b");
    registerReq.setAccountNumber("456");
    registerReq.setRoutingNumber(a1.getRoutingNumber());

    createGroupReq = new CreateGroupReq();
    createGroupReq.setName("g2");
    createGroupReq.setUids(List.of(user.getId()));

    setupDone = true;
  }

  @Test
  public void shouldThrowExceptionWhenSavingUserWithExistingEmail() {
    var constraint = DbConsts.USER_EMAIL_CONSTRAINT;
    registerReq.setEmail(user.getEmail());

    var ex = assertThrows(DataIntegrityViolationException.class, () -> {
      authController.register(registerReq);
    });

    var rsp = controllerExceptionHandler.handle(ex);
    assertEquals(rsp.getStatusCode(), HttpStatus.BAD_REQUEST);
    var body = rsp.getBody();
    assertNotNull(body);
    assertEquals(body.getErrorFields(), Arrays.asList(DbConsts.CONSTRAINTS_FIELDS.get(constraint)));
    assertEquals(body.getMessage(), DbConsts.CONSTRAINTS_ERR_MSGS.get(constraint));
  }

  @Test
  public void shouldThrowExceptionWhenSavingUserWithExistingAccount() {
    var constraint = DbConsts.ACCOUNT_NUMBERS_CONSTRAINT;
    registerReq.setAccountNumber(user.getAccount().getAccountNumber());

    var ex = assertThrows(DataIntegrityViolationException.class, () -> {
      authController.register(registerReq);
    });

    var rsp = controllerExceptionHandler.handle(ex);
    assertEquals(rsp.getStatusCode(), HttpStatus.BAD_REQUEST);
    var body = rsp.getBody();
    assertNotNull(body);
    assertEquals(body.getErrorFields(), Arrays.asList(DbConsts.CONSTRAINTS_FIELDS.get(constraint)));
    assertEquals(body.getMessage(), DbConsts.CONSTRAINTS_ERR_MSGS.get(constraint));
  }

  @Test
  public void shouldThrowExceptionWhenSavingGroupWithExistingName() {
    var constraint = DbConsts.GROUP_NAME_CONSTRAINT;
    createGroupReq.setName(group.getName());

    var ex = assertThrows(DataIntegrityViolationException.class, () -> {
      groupController.createGroup(createGroupReq);
    });

    var rsp = controllerExceptionHandler.handle(ex);
    assertEquals(rsp.getStatusCode(), HttpStatus.BAD_REQUEST);
    var body = rsp.getBody();
    assertNotNull(body);
    assertEquals(body.getErrorFields(), Arrays.asList(DbConsts.CONSTRAINTS_FIELDS.get(constraint)));
    assertEquals(body.getMessage(), DbConsts.CONSTRAINTS_ERR_MSGS.get(constraint));
  }
}
