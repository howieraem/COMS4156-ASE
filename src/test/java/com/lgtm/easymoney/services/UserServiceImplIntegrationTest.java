package com.lgtm.easymoney.services;

import com.lgtm.easymoney.models.Account;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceImplIntegrationTest {
    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    private static boolean setupDone = false;

    @Before
    public void setup() {
        if (setupDone) {
            // run setup() once only
            return;
        }
        User user1 = new User();
        user1.setEmail("a@a.com");
        user1.setPassword("a");
        user1.setTypeByStr("personal");
        Account a1 = new Account();
        a1.setAccountName("a");
        a1.setAccountNumber("123");
        a1.setRoutingNumber("123456789");
        user1.setAccount(a1);
        userRepository.saveAndFlush(user1);
        setupDone = true;
    }

    @Test
    public void shouldThrowExceptionWhenSavingUserWithExistingEmail() {
        User user = new User();
        user.setEmail("a@a.com");
        user.setPassword("c");
        user.setTypeByStr("personal");
        Account a = new Account();
        a.setAccountName("a");
        a.setAccountNumber("123456");
        a.setRoutingNumber("123456789");
        user.setAccount(a);

        assertThrows(DataIntegrityViolationException.class, () -> {
            userService.saveUser(user);
        });
    }

    @Test
    public void shouldThrowExceptionWhenSavingUserWithExistingAccount() {
        User user = new User();
        user.setEmail("c@c.com");
        user.setPassword("c");
        user.setTypeByStr("personal");
        Account a = new Account();
        a.setAccountName("a");
        a.setAccountNumber("123");
        a.setRoutingNumber("123456789");
        user.setAccount(a);

        assertThrows(DataIntegrityViolationException.class, () -> {
            userService.saveUser(user);
        });
    }
}
