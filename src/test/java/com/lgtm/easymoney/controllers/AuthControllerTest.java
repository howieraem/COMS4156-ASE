package com.lgtm.easymoney.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lgtm.easymoney.configs.UserTestConfig;
import com.lgtm.easymoney.configs.WebSecurityConfig;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.req.LoginReq;
import com.lgtm.easymoney.payload.req.RegisterReq;
import com.lgtm.easymoney.security.JwtAuthenticationEntryPoint;
import com.lgtm.easymoney.security.JwtTokenProvider;
import com.lgtm.easymoney.services.AuthService;
import com.lgtm.easymoney.services.impl.UserServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

/**
 * Unit tests for auth controller.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(AuthController.class)
@Import({WebSecurityConfig.class})
public class AuthControllerTest {
  @Autowired
  private MockMvc mvc;

  @MockBean
  private AuthService authService;

  // We test jwt functionalities in integration tests instead
  @MockBean
  private UserServiceImpl userService;
  @MockBean
  private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
  @MockBean
  private JwtTokenProvider jwtTokenProvider;

  private static RegisterReq registerReq;
  private static LoginReq loginReq;

  /** Establish a test request payload. */
  @Before
  public void setup() {
    registerReq = new RegisterReq();
    registerReq.setEmail("a@a.com");
    registerReq.setPassword("a");
    registerReq.setUserType("PERSONAL");
    registerReq.setAccountName("a");
    registerReq.setAccountNumber("123");
    registerReq.setRoutingNumber("123456789");

    loginReq = new LoginReq();
    loginReq.setEmail(UserTestConfig.PERSON1.getEmail());
    loginReq.setPassword(UserTestConfig.PERSON1.getPassword());

    // Assume userService.saveUser() always succeeds
    Mockito.when(userService.saveUser(Mockito.any(User.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
  }

  @Test
  public void registerSuccessful() throws Exception {
    postRegister(registerReq).andExpect(status().isCreated()); // when field values are all valid
  }

  @Test
  public void registerFailedByInvalidEmail() throws Exception {
    // email is null
    registerReq.setEmail(null);
    postRegister(registerReq).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("email"));

    // email is empty
    registerReq.setEmail("");
    postRegister(registerReq).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("email"));

    // email doesn't contain '@'
    registerReq.setEmail("a");
    postRegister(registerReq).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("email"));

    // email contains more than 1 '@'
    registerReq.setEmail("a@b.com@.com");
    postRegister(registerReq).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("email"));
  }

  @Test
  public void registerFailedByInvalidPassword() throws Exception {
    // password is null
    registerReq.setPassword(null);
    postRegister(registerReq).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("password"));

    // password is empty
    registerReq.setPassword("");
    postRegister(registerReq).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("password"));
  }

  @Test
  public void registerFailedByInvalidUserType() throws Exception {
    // user type is null
    registerReq.setUserType(null);
    postRegister(registerReq).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("userType"));

    // user type is empty
    registerReq.setUserType("");
    postRegister(registerReq).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("userType"));

    // user type is not in enum
    registerReq.setUserType("abc");
    postRegister(registerReq).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("userType"));
  }

  @Test
  public void registerFailedByInvalidPhone() throws Exception {
    // phone is not null but empty
    registerReq.setPhone("");
    postRegister(registerReq).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("phone"));

    // phone contains non-numeric characters
    registerReq.setPhone("abc");
    postRegister(registerReq).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("phone"));

    // phone is too short
    registerReq.setPhone("000");
    postRegister(registerReq).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("phone"));

    // phone is too long
    registerReq.setPhone("00000000000");
    postRegister(registerReq).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("phone"));
  }

  @Test
  public void registerFailedByInvalidAccountName() throws Exception {
    // account name is null
    registerReq.setAccountName(null);
    postRegister(registerReq).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("accountName"));

    // account name is empty
    registerReq.setAccountName("");
    postRegister(registerReq).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("accountName"));

    // account name contains digits
    registerReq.setAccountName("test123");
    postRegister(registerReq).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("accountName"));

    // account name contains special characters
    registerReq.setAccountName("test?test");
    postRegister(registerReq).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("accountName"));
  }

  @Test
  public void registerFailedByInvalidAccountNumber() throws Exception {
    // account number is null
    registerReq.setAccountNumber(null);
    postRegister(registerReq).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("accountNumber"));

    // account number is empty
    registerReq.setAccountNumber("");
    postRegister(registerReq).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("accountNumber"));

    // account number contains non-numeric characters
    registerReq.setAccountNumber("abc");
    postRegister(registerReq).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("accountNumber"));

    // account number is too long
    registerReq.setAccountNumber("12321371892473218947122");
    postRegister(registerReq).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("accountNumber"));
  }

  @Test
  public void registerFailedByInvalidRoutingNumber() throws Exception {
    // routing number is null
    registerReq.setRoutingNumber(null);
    postRegister(registerReq).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("routingNumber"));

    // routing number is empty
    registerReq.setRoutingNumber("");
    postRegister(registerReq).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("routingNumber"));

    // routing number contains non-numeric characters
    registerReq.setRoutingNumber("abc");
    postRegister(registerReq).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("routingNumber"));

    // routing number is too short
    registerReq.setRoutingNumber("12321");
    postRegister(registerReq).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("routingNumber"));

    // routing number is too long
    registerReq.setRoutingNumber("12321371892473218947122");
    postRegister(registerReq).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("routingNumber"));
  }

  @Test
  public void testLoginSuccess() throws Exception {
    String fakeToken = "Bearer";
    Mockito.when(authService.login(loginReq)).thenReturn(fakeToken);

    postLogin(loginReq).andExpect(status().isOk()).andExpect(content().string(fakeToken));

    // Note: login failure should be tested in integration tests
  }

  private ResultActions postRegister(RegisterReq req) throws Exception {
    return mvc.perform(post("/auth/register")
        .content(asJsonString(req))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON));
  }

  private ResultActions postLogin(LoginReq req) throws Exception {
    return mvc.perform(post("/auth/login")
        .content(asJsonString(req))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON));
  }

  private static String asJsonString(final Object obj) throws JsonProcessingException {
    return new ObjectMapper().writeValueAsString(obj);
  }
}
