package com.lgtm.easymoney.services.impl;

import com.lgtm.easymoney.enums.UserType;
import com.lgtm.easymoney.exceptions.InvalidUpdateException;
import com.lgtm.easymoney.exceptions.ResourceNotFoundException;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.BalanceReq;
import com.lgtm.easymoney.payload.RegisterReq;
import com.lgtm.easymoney.repositories.UserRepository;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.BDDMockito;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
public class UserServiceImplTest {
  @InjectMocks
  private UserServiceImpl userService;

  @Mock
  private UserRepository userRepository;

  private Long uid1 = 1L;
  private String email1 = "a@a.com";
  private String pwd1 = "a";
  private Long uid2 = 2L;
  private String email2 = "b@b.com";
  private String pwd2 = "b";

  private Long nonExistId = 3L;

  @Before
  public void setUp() {
    User user1 = new User();
    user1.setId(uid1);
    user1.setEmail(email1);
    user1.setPassword(pwd1);

    User user2 = new User();
    user2.setId(uid2);
    user2.setEmail(email2);
    user2.setPassword(pwd2);
    user2.setBalance(new BigDecimal(100));

    Mockito.when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
    Mockito.when(userRepository.existsById(user1.getId())).thenReturn(true);
    Mockito.when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));
    Mockito.when(userRepository.existsById(user2.getId())).thenReturn(true);
    Mockito.when(userRepository.existsById(nonExistId)).thenReturn(false);

    List<User> users = new ArrayList<>();
    users.add(user1);
    users.add(user2);
    Mockito.when(userRepository.findAll()).thenReturn(users);
  }

  @Test
  public void testUserExists() {
    assertTrue(userService.existsById(uid1));
    assertFalse(userService.existsById(nonExistId));
  }

  @Test
  public void shouldGetUserById() {
    User user = userService.getUserById(uid1);
    assertEquals(user.getId(), uid1);
    assertEquals(user.getEmail(), email1);
    assertEquals(user.getPassword(), pwd1);
    assertEquals(user.getBalance(), BigDecimal.ZERO);
  }

  @Test
  public void shouldThrowExceptionWhenUserNotFound() {
    Long id = 3L;
    Mockito.when(userRepository.findById(id)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(id));
  }

  @Test
  public void shouldCreateUser() {
    // Register req payload passed to UserService has been validated
    RegisterReq req = new RegisterReq();
    req.setEmail("c@c.com");
    req.setPassword("c");
    req.setUserType("BUSINESS");
    req.setPhone("1234567890");
    req.setAddress("2960 Broadway");
    req.setAccountName("c");
    req.setAccountNumber("789");
    req.setRoutingNumber("123456789");
    req.setBizPromotionText("hello");

    var user = userService.buildUser(req);
    assertEquals(user.getEmail(), req.getEmail());
    assertEquals(user.getPassword(), req.getPassword());
    assertEquals(user.getType(), UserType.valueOf(req.getUserType()));
    assertEquals(user.getPhone(), req.getPhone());
    assertEquals(user.getAddress(), req.getAddress());
    var account = user.getAccount();
    assertEquals(account.getAccountName(), req.getAccountName());
    assertEquals(account.getAccountNumber(), req.getAccountNumber());
    assertEquals(account.getRoutingNumber(), req.getRoutingNumber());
    var bisProfile = user.getBizProfile();
    assertEquals(bisProfile.getPromotionText(), req.getBizPromotionText());

    Long expectedId = 3L;

    // Assume userRepository.save() always succeeds
    Mockito.doAnswer(invocation -> {
      ReflectionTestUtils.setField((User) invocation.getArgument(0), "id", expectedId);
      return invocation.getArgument(0);
    }).when(userRepository).save(Mockito.any(User.class));

    var rsp = userService.createUser(req);
    assertEquals(rsp.getId(), expectedId);
  }

  @Test
  public void shouldSaveUser() {
    User user = new User();
    user.setEmail("c@c.com");
    user.setPassword("c");
    Long expectedId = 3L;

    // Assume userRepository.save() always succeeds
    Mockito.doAnswer(invocation -> {
      ReflectionTestUtils.setField((User) invocation.getArgument(0), "id", expectedId);
      return invocation.getArgument(0);
    }).when(userRepository).save(Mockito.any(User.class));
    User saved = userService.saveUser(user);

    assertNotNull(saved);
    assertEquals(saved.getId(), expectedId);
    assertEquals(saved.getEmail(), user.getEmail());
    assertEquals(saved.getPassword(), user.getPassword());
  }

  @Test
  public void shouldGetAllUsers() {
    List<User> users = userService.getAllUsers();
    assertEquals(users.size(), 2);
  }

  @Test
  public void shouldDepositSuccessfully() {
    BalanceReq req = new BalanceReq();
    req.setUid(1L);
    req.setAmount(new BigDecimal(100));
    var rsp = userService.makeDeposit(req);
    assertNotNull(rsp);
    assertEquals(rsp.getCurrBalance(), req.getAmount());
  }

  @Test
  public void shouldWithdrawSuccessfully() {
    BalanceReq req = new BalanceReq();
    req.setUid(2L);
    req.setAmount(new BigDecimal(100));
    var rsp = userService.makeWithdraw(req);
    assertNotNull(rsp);
    assertEquals(rsp.getCurrBalance(), BigDecimal.ZERO);
  }

  @Test
  public void shouldNotWithdrawIfAmountExceedsBalance() {
    BalanceReq req = new BalanceReq();
    req.setUid(1L);
    req.setAmount(new BigDecimal(100));
    assertThrows(InvalidUpdateException.class, () -> userService.makeWithdraw(req));
  }
}
