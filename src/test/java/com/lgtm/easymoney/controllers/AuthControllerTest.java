package com.lgtm.easymoney.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.req.RegisterReq;
import com.lgtm.easymoney.services.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

/**
 * Unit tests for auth controller.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(AuthController.class)
public class AuthControllerTest {
  @Autowired
  private MockMvc mvc;

  @MockBean
  private UserService userService;

  private static RegisterReq req;

  /** Establish a test request payload. */
  @Before
  public void setup() {
    req = new RegisterReq();
    req.setEmail("a@a.com");
    req.setPassword("a");
    req.setUserType("PERSONAL");
    req.setAccountName("a");
    req.setAccountNumber("123");
    req.setRoutingNumber("123456789");

    // Assume userService.saveUser() always succeeds
    Mockito.when(userService.saveUser(Mockito.any(User.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
  }

  @Test
  public void registerSuccessful() throws Exception {
    postRegister(req).andExpect(status().isCreated()); // when field values are all valid
  }

  @Test
  public void registerFailedByInvalidEmail() throws Exception {
    // email is null
    req.setEmail(null);
    postRegister(req).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("email"));

    // email is empty
    req.setEmail("");
    postRegister(req).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("email"));

    // email doesn't contain '@'
    req.setEmail("a");
    postRegister(req).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("email"));

    // email contains more than 1 '@'
    req.setEmail("a@b.com@.com");
    postRegister(req).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("email"));
  }

  @Test
  public void registerFailedByInvalidPassword() throws Exception {
    // password is null
    req.setPassword(null);
    postRegister(req).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("password"));

    // password is empty
    req.setPassword("");
    postRegister(req).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("password"));
  }

  @Test
  public void registerFailedByInvalidUserType() throws Exception {
    // user type is null
    req.setUserType(null);
    postRegister(req).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("userType"));

    // user type is empty
    req.setUserType("");
    postRegister(req).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("userType"));

    // user type is not in enum
    req.setUserType("abc");
    postRegister(req).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("userType"));
  }

  @Test
  public void registerFailedByInvalidPhone() throws Exception {
    // phone is not null but empty
    req.setPhone("");
    postRegister(req).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("phone"));

    // phone contains non-numeric characters
    req.setPhone("abc");
    postRegister(req).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("phone"));

    // phone is too short
    req.setPhone("000");
    postRegister(req).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("phone"));

    // phone is too long
    req.setPhone("00000000000");
    postRegister(req).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("phone"));
  }

  @Test
  public void registerFailedByInvalidAccountName() throws Exception {
    // account name is null
    req.setAccountName(null);
    postRegister(req).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("accountName"));

    // account name is empty
    req.setAccountName("");
    postRegister(req).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("accountName"));
  }

  @Test
  public void registerFailedByInvalidAccountNumber() throws Exception {
    // account number is null
    req.setAccountNumber(null);
    postRegister(req).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("accountNumber"));

    // account number is empty
    req.setAccountNumber("");
    postRegister(req).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("accountNumber"));

    // account number contains non-numeric characters
    req.setAccountNumber("abc");
    postRegister(req).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("accountNumber"));

    // account number is too long
    req.setAccountNumber("12321371892473218947122");
    postRegister(req).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("accountNumber"));
  }

  @Test
  public void registerFailedByInvalidRoutingNumber() throws Exception {
    // routing number is null
    req.setRoutingNumber(null);
    postRegister(req).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("routingNumber"));

    // routing number is empty
    req.setRoutingNumber("");
    postRegister(req).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("routingNumber"));

    // routing number contains non-numeric characters
    req.setRoutingNumber("abc");
    postRegister(req).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("routingNumber"));

    // routing number is too short
    req.setRoutingNumber("12321");
    postRegister(req).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("routingNumber"));

    // routing number is too long
    req.setRoutingNumber("12321371892473218947122");
    postRegister(req).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("routingNumber"));
  }

  private ResultActions postRegister(RegisterReq req) throws Exception {
    return mvc.perform(post("/auth/register")
        .content(asJsonString(req))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON));
  }

  private static String asJsonString(final Object obj) throws JsonProcessingException {
    return new ObjectMapper().writeValueAsString(obj);
  }
}
