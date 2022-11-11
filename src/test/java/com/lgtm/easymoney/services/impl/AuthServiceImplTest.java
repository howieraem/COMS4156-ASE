package com.lgtm.easymoney.services.impl;

import static org.junit.Assert.assertEquals;

import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.req.RegisterReq;
import com.lgtm.easymoney.services.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;


/**
 * Unit tests for auth service implementation.
 */
@RunWith(SpringRunner.class)
public class AuthServiceImplTest {
  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private UserService userService;

  @InjectMocks
  private AuthServiceImpl authService;

  @Test
  public void shouldRegisterUser() {
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

    Long expectedId = 1L;

    // Assume userRepository.save() always succeeds
    Mockito.doAnswer(invocation -> {
      ReflectionTestUtils.setField((User) invocation.getArgument(0), "id", expectedId);
      return invocation.getArgument(0);
    }).when(userService).saveUser(Mockito.any(User.class));

    var rsp = authService.register(req);
    assertEquals(rsp.getId(), expectedId);
  }
}
