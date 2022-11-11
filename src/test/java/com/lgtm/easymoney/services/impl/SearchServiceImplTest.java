package com.lgtm.easymoney.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import com.lgtm.easymoney.exceptions.ResourceNotFoundException;
import com.lgtm.easymoney.models.Account;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.rsp.ProfileRsp;
import com.lgtm.easymoney.payload.rsp.ProfilesRsp;
import com.lgtm.easymoney.repositories.AccountRepository;
import com.lgtm.easymoney.repositories.UserRepository;
import com.lgtm.easymoney.services.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Search Service Test.
 */
@RunWith(SpringRunner.class)
public class SearchServiceImplTest {
  @InjectMocks
  private SearchServiceImpl searchService;

  @Mock
  private UserService userService;
  @Mock
  private UserRepository userRepository;
  @Mock
  private AccountRepository accountRepository;


  private User user1;
  private User user2;
  private Long id1 = 1L;
  private Long id2 = 2L;
  private Account account1;
  private Account account2;

  /**
   * Setup.
   */
  @Before
  public void setUp() {
    user1 = new User();
    account1 = new Account();
    user1.setId(id1);
    user1.setEmail("a@a.com");
    user1.setPhone("1234562211");
    user1.setPassword("a");
    account1.setId(id1);
    account1.setAccountUser(user1);
    account1.setAccountName("abc");
    user1.setAccount(account1);

    user2 = new User();
    account2 = new Account();
    user2.setId(id2);
    user2.setEmail("b@b.com");
    user1.setPhone("1234562210");
    user2.setPassword("b");
    account2.setId(id2);
    account2.setAccountUser(user2);
    account2.setAccountName("acd");
    user2.setAccount(account2);

    Mockito.when(userService.getUserById(id1)).thenReturn(user1);
    Mockito.when(userService.getUserById(id2)).thenReturn(user2);

    Mockito.when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
    Mockito.when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));
    Mockito.when(userRepository.existsById(user1.getId())).thenReturn(true);
    Mockito.when(userRepository.existsById(user2.getId())).thenReturn(true);
    List<User> users = new ArrayList<>();
    users.add(user1);
    users.add(user2);
    Mockito.when(userRepository.findAll()).thenReturn(users);

    Mockito.when(accountRepository.findById(id1)).thenReturn(Optional.of(account1));
    Mockito.when(accountRepository.findById(id2)).thenReturn(Optional.of(account2));
  }

  @Test
  public void getUserByIdSuccess() {
    // Act
    User user = searchService.getUserById(id1);

    // Assert
    assertEquals(user1, user);
  }

  @Test
  public void getUserByNameSuccess() {
    // Arrange
    Mockito.when(accountRepository.findByAccountNameContainingIgnoreCase(
            account1.getAccountName())).thenReturn(List.of(account1));

    // Act
    User user = searchService.getUserByName(account1.getAccountName()).get(0);

    // Assert
    assertEquals(user1, user);
  }

  @Test
  public void getUserByNameSuccessWithMultipleMatch() {
    // Arrange
    String searchStr = "a";
    Mockito.when(accountRepository.findByAccountNameContainingIgnoreCase(
            searchStr)).thenReturn(List.of(account1, account2));

    // Act
    List<User> userList = searchService.getUserByName("a");

    // Assert
    assertEquals(2, userList.size());
  }

  @Test
  public void getUserByEmailOrPhoneSuccess() {
    // Arrange
    Mockito.when(userRepository.findByEmailContainingIgnoreCaseOrPhoneContaining(
            any(), any())).thenReturn(List.of(user1));

    // Act
    User user = searchService.getUserByEmailOrPhone(user1.getEmail(), user1.getPhone()).get(0);

    // Assert
    assertEquals(user1, user);
  }

  @Test
  public void getUserByIdFailedWithNonExistingId() {
    // Act && Assert
    assertThrows(ResourceNotFoundException.class, () -> searchService.getUserById(3L));
  }

  @Test
  public void getUserByNameFailedWithNonExistingName() {
    // Arrange
    Account fakeAccount = new Account();
    fakeAccount.setAccountName("123");

    // Act
    List<User> userList = searchService.getUserByName(fakeAccount.getAccountName());

    // Assert
    assertEquals(0, userList.size());
  }

  @Test
  public void getUserByEmailOrPhoneFailedWithNonExistingEmailOrPhone() {
    // Arrange
    User user = new User();
    user.setEmail("foo@foo.com");

    // Act
    List<User> returnedUserList = searchService.getUserByEmailOrPhone(
            user.getEmail(), user.getPhone());

    // Assert
    assertEquals(0, returnedUserList.size());
  }

  @Test
  public void searchByIdSuccess() {
    ProfileRsp rsp = searchService.searchById(id1);
    assertEquals(id1, rsp.getUid());
    assertEquals(user1.getEmail(),
            rsp.getEmail());
    assertEquals(user1.getAccount().getAccountName(),
            rsp.getAccountName());
  }

  @Test
  public void searchByInfoSuccess() {
    List<User> userList = new ArrayList<>();
    userList.add(user1);
    Mockito.when(userRepository.findByEmailContainingIgnoreCaseOrPhoneContaining(
            user1.getEmail(), user1.getEmail())).thenReturn(userList);
    ProfilesRsp rsp = searchService.searchByInfo(user1.getEmail());
    assertEquals(id1,
            rsp.getUserProfiles().get(0).getUid());
    assertEquals(user1.getEmail(),
            rsp.getUserProfiles().get(0).getEmail());
    assertEquals(user1.getAccount().getAccountName(),
            rsp.getUserProfiles().get(0).getAccountName());
    assertEquals(user1.getPhone(),
            rsp.getUserProfiles().get(0).getPhone());
  }

  @Test
  public void searchByInfoSuccessMultipleMatch() {
    List<User> userList = new ArrayList<>();
    userList.add(user1);
    userList.add(user2);
    String strString = "a";
    Mockito.when(userRepository.findByEmailContainingIgnoreCaseOrPhoneContaining(
            strString, strString)).thenReturn(userList);
    Mockito.when(accountRepository.findByAccountNameContainingIgnoreCase(
            strString)).thenReturn(List.of(account1, account2));

    ProfilesRsp rsp = searchService.searchByInfo("a");
    assertEquals(2, rsp.getUserProfiles().size());
  }

}
