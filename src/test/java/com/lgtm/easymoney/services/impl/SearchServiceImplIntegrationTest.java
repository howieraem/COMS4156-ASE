//package com.lgtm.easymoney.services.impl;
//
//import com.lgtm.easymoney.exceptions.ResourceNotFoundException;
//import com.lgtm.easymoney.models.Account;
//import com.lgtm.easymoney.models.User;
//import com.lgtm.easymoney.repositories.UserRepository;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import static org.junit.Assert.assertThrows;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class SearchServiceImplIntegrationTest {
//    @Autowired
//    private SearchServiceImpl searchService;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    private static boolean setupDone = false;
//
//    @Before
//    public void setup() {
//        if (setupDone) {
//            // run setup() once only
//            return;
//        }
//        User user1 = new User();
//        user1.setEmail("email1@a.com");
//        user1.setPassword("a");
//        user1.setTypeByStr("PERSONAL");
//        Account a1 = new Account();
//        a1.setAccountName("foo1");
//        a1.setAccountNumber("123");
//        a1.setRoutingNumber("123456781");
//        user1.setAccount(a1);
//
//        User user2 = new User();
//        user2.setEmail("email2@a.com");
//        user2.setPassword("a");
//        user2.setTypeByStr("PERSONAL");
//        Account a2 = new Account();
//        a2.setAccountName("foo2");
//        a2.setAccountNumber("345");
//        a2.setRoutingNumber("123456782");
//        user2.setAccount(a2);
//
//        User user3 = new User();
//        user3.setEmail("email3@a.com");
//        user3.setPassword("a");
//        user3.setTypeByStr("PERSONAL");
//        Account a3 = new Account();
//        a3.setAccountName("foo3");
//        a3.setAccountNumber("678");
//        a3.setRoutingNumber("123456783");
//        user3.setAccount(a3);
//        userRepository.saveAndFlush(user1);
//        userRepository.saveAndFlush(user2);
//        userRepository.saveAndFlush(user3);
//        setupDone = true;
//    }
//
//    @Test
//    public void shouldThrowExceptionWhenSavingUserWithExistingEmail() {
//        assertThrows(ResourceNotFoundException.class, () -> {
//            searchService.searchByInfo("boo");
//        });
//    }
//
//
//}
