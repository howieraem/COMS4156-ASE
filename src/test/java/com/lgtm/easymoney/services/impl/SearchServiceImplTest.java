package com.lgtm.easymoney.services.impl;

import com.lgtm.easymoney.exceptions.ResourceNotFoundException;
import com.lgtm.easymoney.models.Account;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.repositories.AccountRepository;
import com.lgtm.easymoney.repositories.UserRepository;
import com.lgtm.easymoney.services.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;

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

    @Before
    public void setUp() {
        user1 = new User();
        account1 = new Account();
        user1.setId(id1);
        user1.setEmail("a@a.com");
        user1.setPassword("a");
        account1.setId(id1);
        account1.setAccountUser(user1);
        account1.setAccountName("abc");
        user1.setAccount(account1);

        user2 = new User();
        account2 = new Account();
        user2.setId(id2);
        user2.setEmail("b@b.com");
        user2.setPassword("b");
        account2.setId(id2);
        account2.setAccountUser(user2);
        account2.setAccountName("acd");
        user2.setAccount(account2);

        Mockito.when(userService.getUserById(id1)).thenReturn(user1);
        Mockito.when(userService.getUserById(id2)).thenReturn(user2);

        Mockito.when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        Mockito.when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));

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
        Mockito.when(accountRepository.findByAccountNameContainingIgnoreCase(account1.getAccountName())).thenReturn(List.of(account1));

        // Act
        User user = searchService.getUserByName(account1.getAccountName()).get(0);

        // Assert
        assertEquals(user1,user);
    }

    @Test
    public void getUserByNameSuccessWithMultipleMatch() {
        // Arrange
        String searchStr = "a";
        Mockito.when(accountRepository.findByAccountNameContainingIgnoreCase(searchStr)).thenReturn(List.of(account1,account2));

        // Act
        List<User> userList = searchService.getUserByName("a");

        // Assert
        assertEquals(2,userList.size());
    }

    @Test
    public void getUserByEmailOrPhoneSuccess() {
        // Arrange
        Mockito.when(userRepository.findByEmailContainingIgnoreCaseOrPhoneContaining(any(),any())).thenReturn(List.of(user1));

        // Act
        User user = searchService.getUserByEmailOrPhone(user1.getEmail(), user1.getPhone()).get(0);

        // Assert
        assertEquals(user1,user);
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
        assertEquals(0,userList.size());
    }

    @Test
    public void getUserByEmailOrPhoneFailedWithNonExistingEmailOrPhone() {
        // Arrange
        User user = new User();
        user.setEmail("foo@foo.com");

        // Act
        List<User> returnedUserList = searchService.getUserByEmailOrPhone(user.getEmail(), user.getPhone());

        // Assert
        assertEquals(0,returnedUserList.size());
    }



}
