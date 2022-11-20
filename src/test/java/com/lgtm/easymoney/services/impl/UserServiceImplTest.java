package com.lgtm.easymoney.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import com.lgtm.easymoney.exceptions.InapplicableOperationException;
import com.lgtm.easymoney.exceptions.InvalidUpdateException;
import com.lgtm.easymoney.exceptions.ResourceNotFoundException;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.req.BizProfileReq;
import com.lgtm.easymoney.repositories.UserRepository;
import com.lgtm.easymoney.security.UserPrincipal;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;


/**
 * Unit tests for user service implementation.
 */
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

  private User user1;

  private UserPrincipal principal1;

  private User user2;

  /** Establish users for further testing. */
  @Before
  public void setUp() {
    user1 = new User();
    user1.setId(uid1);
    user1.setEmail(email1);
    user1.setPassword(pwd1);
    user1.setTypeByStr("BUSINESS");
    principal1 = new UserPrincipal(user1);

    user2 = new User();
    user2.setId(uid2);
    user2.setEmail(email2);
    user2.setPassword(pwd2);
    user2.setBalance(new BigDecimal(100));

    Mockito.when(userRepository.findById(uid1)).thenReturn(Optional.of(user1));
    Mockito.when(userRepository.findByEmail(email1)).thenReturn(user1);
    Mockito.when(userRepository.existsById(uid1)).thenReturn(true);
    Mockito.when(userRepository.findById(uid2)).thenReturn(Optional.of(user2));
    Mockito.when(userRepository.findByEmail(email2)).thenReturn(user2);
    Mockito.when(userRepository.existsById(uid2)).thenReturn(true);
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
    assertEquals(uid1, user.getId());
    assertEquals(email1, user.getEmail());
    assertEquals(pwd1, user.getPassword());
    assertEquals(BigDecimal.ZERO, user.getBalance());
  }

  @Test
  public void shouldThrowExceptionWhenUserNotFound() {
    Long id = 3L;
    Mockito.when(userRepository.findById(id)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(id));
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
    assertEquals(2, users.size());
  }

  @Test
  public void shouldDepositSuccessfully() {
    var amount = new BigDecimal(100);
    var rsp = userService.makeDeposit(user1, amount);
    assertNotNull(rsp);
    assertEquals(rsp.getCurrBalance(), amount);
  }

  @Test
  public void shouldWithdrawSuccessfully() {
    var amount = new BigDecimal(100);
    user1.setBalance(amount);
    var rsp = userService.makeWithdraw(user1, amount);
    assertNotNull(rsp);
    assertEquals(BigDecimal.ZERO, rsp.getCurrBalance());
  }

  @Test
  public void shouldNotWithdrawIfAmountExceedsBalance() {
    var amount = new BigDecimal(100);
    assertThrows(InvalidUpdateException.class, () -> userService.makeWithdraw(user1, amount));
  }

  @Test
  public void testLoadPrincipal() {
    // Act
    var p = userService.loadUserByUsername(email1);

    // Assert
    assertEquals(p, principal1);
  }

  @Test
  public void loadPrincipalFailedWithNonexistentEmail() {
    // Arrange
    var email = "c@c.com";
    Mockito.when(userRepository.findByEmail(email)).thenReturn(null);

    // Act & Assert
    assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(email));
  }

  @Test
  public void testUpdateBizPromotion() {
    // Arrange
    BizProfileReq req = new BizProfileReq();
    req.setPromotionText("abc");

    // Act
    userService.updateBizProfile(user1, req);

    // Assert
    Mockito.verify(userRepository, Mockito.times(1)).save(user1);
  }

  @Test
  public void updateBizPromotionFailedByUserType() {
    // Arrange
    user1.setTypeByStr("PERSONAL");
    BizProfileReq req = new BizProfileReq();
    req.setPromotionText("abc");

    // Act & Assert
    assertThrows(InapplicableOperationException.class,
        () -> userService.updateBizProfile(user1, req));
  }
}
