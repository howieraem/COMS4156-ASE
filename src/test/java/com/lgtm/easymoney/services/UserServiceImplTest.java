package com.lgtm.easymoney.services;

import com.lgtm.easymoney.exceptions.InvalidUpdateException;
import com.lgtm.easymoney.exceptions.ResourceNotFoundException;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.BalanceReq;
import com.lgtm.easymoney.repositories.UserRepository;
import static org.junit.Assert.*;
import com.lgtm.easymoney.services.impl.UserServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.BDDMockito;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

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

    @Before
    public void setUp() {
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("a@a.com");
        user1.setPassword("a");

        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("b@b.com");
        user2.setPassword("b");
        user2.setBalance(new BigDecimal(100));

        Mockito.when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        Mockito.when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));

        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        Mockito.when(userRepository.findAll()).thenReturn(users);
    }

    @Test
    public void shouldGetUserByID() {
        Long id = 1L;
        User user = userService.getUserByID(id);
        assertEquals(user.getId(), id);
    }

    @Test
    public void shouldThrowExceptionWhenUserNotFound() {
        Long id = 3L;
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserByID(id);
        });
    }

    // Perhaps this is better done in the integration test?
    @Test
    public void shouldSaveUser() {
        User user = new User();
        user.setId(3L);
        user.setEmail("c@c.com");
        user.setPassword("c");

        // Assume userRepository.save() always succeeds
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        User saved = userService.saveUser(user);

        assertNotNull(saved);
        assertEquals(saved.getId(), user.getId());
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
        var rsp = userService.makeADeposit(req);
        assertNotNull(rsp);
        assertTrue(rsp.getSuccess());
        assertEquals(rsp.getCurrBalance(), req.getAmount());
    }

    @Test
    public void shouldWithdrawSuccessfully() {
        BalanceReq req = new BalanceReq();
        req.setUid(2L);
        req.setAmount(new BigDecimal(100));
        var rsp = userService.makeAWithdraw(req);
        assertNotNull(rsp);
        assertTrue(rsp.getSuccess());
        assertEquals(rsp.getCurrBalance(), BigDecimal.ZERO);
    }

    @Test
    public void shouldNotWithdrawIfAmountExceedsBalance() {
        BalanceReq req = new BalanceReq();
        req.setUid(1L);
        req.setAmount(new BigDecimal(100));
        assertThrows(InvalidUpdateException.class, () -> {
            userService.makeAWithdraw(req);
        });
    }
}
