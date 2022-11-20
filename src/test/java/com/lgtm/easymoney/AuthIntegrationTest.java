package com.lgtm.easymoney;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lgtm.easymoney.configs.UserTestConfig;
import com.lgtm.easymoney.exceptions.handlers.ControllerExceptionHandler;
import com.lgtm.easymoney.payload.req.LoginReq;
import com.lgtm.easymoney.payload.req.RegisterReq;
import com.lgtm.easymoney.repositories.UserRepository;
import com.lgtm.easymoney.security.JwtTokenProvider;
import com.lgtm.easymoney.security.UserPrincipal;
import com.lgtm.easymoney.services.AuthService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

/** Integration tests of API invocations with/without auth. */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AuthIntegrationTest {
  @Autowired
  private MockMvc mvc;

  @Autowired
  private JwtTokenProvider jwtTokenProvider;

  @Autowired
  private AuthService authService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ControllerExceptionHandler controllerExceptionHandler;

  @Value("${app.jwt.header}")
  private String tokenRequestHeader;

  private final UserPrincipal person1 = UserTestConfig.PERSON1_PRINCIPAL;

  private static String person1Token;
  private static boolean setupDone = false;

  /** Prepare user and its token. */
  @Before
  public void setup() {
    if (setupDone) {
      return;
    }
    person1Token = jwtTokenProvider.generateToken(person1.getUsername());

    register(person1);
    userRepository.flush();

    setupDone = true;
  }

  // We need the runtime passwordEncoder, so can't directly call userRepository.save()
  private void register(UserPrincipal principal) {
    var req = new RegisterReq();
    req.setEmail(principal.getUsername());
    req.setPassword(principal.getPassword());
    var u = principal.get();
    req.setUserType(u.getType().name());
    req.setBizPromotionText(u.getBizPromotionText());
    var a = u.getAccount();
    req.setAccountName(a.getAccountName());
    req.setAccountNumber(a.getAccountNumber());
    req.setRoutingNumber(a.getRoutingNumber());
    authService.register(req);
  }

  @Test
  public void shouldGenerateJwt() throws Exception {
    var req = new LoginReq();
    req.setEmail(person1.getUsername());
    req.setPassword(person1.getPassword());

    var resultActions = mvc.perform(post("/auth/login")
        .content(asJsonString(req))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON));

    resultActions.andExpect(status().isOk()).andExpect(content().contentType("application/json"));

    String token = resultActions.andReturn().getResponse().getContentAsString();
    assertNotNull(token);
    assertTrue(token.length() > 100);

    String username = jwtTokenProvider.getUsernameFromJwt(token);
    assertEquals(person1.getUsername(), username);
  }

  @Test
  public void shouldNotGenerateJwtGivenWrongCredentials() throws Exception {
    var req = new LoginReq();
    req.setEmail(person1.getUsername());
    req.setPassword("wrong pwd");

    mvc.perform(post("/auth/login")
        .content(asJsonString(req))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized())
        .andExpect(content().string(""));
  }

  @Test
  public void testFeedApi() throws Exception {
    // Without valid token
    mvc.perform(get("/feed")).andExpect(status().isUnauthorized());
    mvc.perform(get("/feed").header(tokenRequestHeader, "bad token"))
        .andExpect(status().isUnauthorized());

    // With valid token
    mvc.perform(get("/feed").header(tokenRequestHeader, person1Token))
        .andExpect(status().isOk());
  }

  @Test
  public void testFriendApi() throws Exception {
    // Without token
    mvc.perform(post("/friend/add")).andExpect(status().isUnauthorized());
    mvc.perform(put("/friend/accept")).andExpect(status().isUnauthorized());
    mvc.perform(delete("/friend/2")).andExpect(status().isUnauthorized());
    mvc.perform(get("/friend")).andExpect(status().isUnauthorized());
    mvc.perform(get("/friend/pending")).andExpect(status().isUnauthorized());

    // With token
    mvc.perform(post("/friend/add").header(tokenRequestHeader, person1Token))
        .andExpect(status().isBadRequest());
    mvc.perform(put("/friend/accept").header(tokenRequestHeader, person1Token))
        .andExpect(status().isBadRequest());
    mvc.perform(delete("/friend/2").header(tokenRequestHeader, person1Token))
        .andExpect(status().isNotFound());
    mvc.perform(get("/friend").header(tokenRequestHeader, person1Token))
        .andExpect(status().isOk());
    mvc.perform(get("/friend/pending").header(tokenRequestHeader, person1Token))
        .andExpect(status().isOk());
  }

  @Test
  public void testGroupApi() throws Exception {
    // Without token
    mvc.perform(post("/group/create")).andExpect(status().isUnauthorized());
    mvc.perform(put("/group/invite")).andExpect(status().isUnauthorized());
    mvc.perform(put("/group/leave")).andExpect(status().isUnauthorized());
    mvc.perform(get("/group/1")).andExpect(status().isUnauthorized());
    mvc.perform(get("/group/1/ads")).andExpect(status().isUnauthorized());

    // With token
    mvc.perform(post("/group/create").header(tokenRequestHeader, person1Token))
        .andExpect(status().isBadRequest());
    mvc.perform(put("/group/invite").header(tokenRequestHeader, person1Token))
        .andExpect(status().isBadRequest());
    mvc.perform(put("/group/leave").header(tokenRequestHeader, person1Token))
        .andExpect(status().isBadRequest());
    mvc.perform(get("/group/1").header(tokenRequestHeader, person1Token))
        .andExpect(status().isNotFound());
    mvc.perform(get("/group/1/ads").header(tokenRequestHeader, person1Token))
        .andExpect(status().isNotFound());
  }

  @Test
  public void testLoanApi() throws Exception {
    // Without token
    mvc.perform(post("/loan/request")).andExpect(status().isUnauthorized());
    mvc.perform(get("/loan")).andExpect(status().isUnauthorized());
    mvc.perform(put("/loan/approve")).andExpect(status().isUnauthorized());
    mvc.perform(put("/loan/decline")).andExpect(status().isUnauthorized());

    // With token
    mvc.perform(post("/loan/request").header(tokenRequestHeader, person1Token))
        .andExpect(status().isBadRequest());
    mvc.perform(get("/loan").header(tokenRequestHeader, person1Token))
        .andExpect(status().isOk());
    mvc.perform(put("/loan/approve").header(tokenRequestHeader, person1Token))
        .andExpect(status().isBadRequest());
    mvc.perform(put("/loan/decline").header(tokenRequestHeader, person1Token))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void testRequestApi() throws Exception {
    // Without token
    mvc.perform(post("/request/create")).andExpect(status().isUnauthorized());
    mvc.perform(get("/request")).andExpect(status().isUnauthorized());
    mvc.perform(put("/request/accept")).andExpect(status().isUnauthorized());
    mvc.perform(put("/request/decline")).andExpect(status().isUnauthorized());

    // With token
    mvc.perform(post("/request/create").header(tokenRequestHeader, person1Token))
        .andExpect(status().isBadRequest());
    mvc.perform(get("/request").header(tokenRequestHeader, person1Token))
        .andExpect(status().isOk());
    mvc.perform(put("/request/accept").header(tokenRequestHeader, person1Token))
        .andExpect(status().isBadRequest());
    mvc.perform(put("/request/decline").header(tokenRequestHeader, person1Token))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void testTransferApi() throws Exception {
    // Without token
    mvc.perform(post("/transfer/create")).andExpect(status().isUnauthorized());
    mvc.perform(get("/transfer")).andExpect(status().isUnauthorized());

    // With token
    mvc.perform(post("/transfer/create").header(tokenRequestHeader, person1Token))
        .andExpect(status().isBadRequest());
    mvc.perform(get("/transfer").header(tokenRequestHeader, person1Token))
        .andExpect(status().isOk());
  }

  @Test
  public void testUserApi() throws Exception {
    // Without token
    mvc.perform(get("/user/me")).andExpect(status().isUnauthorized());
    mvc.perform(put("/user/deposit")).andExpect(status().isUnauthorized());
    mvc.perform(put("/user/withdraw")).andExpect(status().isUnauthorized());
    mvc.perform(put("/user/biz")).andExpect(status().isUnauthorized());

    // With token
    mvc.perform(put("/user/deposit").header(tokenRequestHeader, person1Token))
        .andExpect(status().isBadRequest());
    mvc.perform(put("/user/withdraw").header(tokenRequestHeader, person1Token))
        .andExpect(status().isBadRequest());
    mvc.perform(put("/user/biz").header(tokenRequestHeader, person1Token))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void getCurrentUserWithToken() throws Exception {
    var u = person1.get();
    var a = u.getAccount();
    mvc.perform(get("/user/me").header(tokenRequestHeader, person1Token))
        .andExpectAll(
            status().isOk(),
            jsonPath("$.id").value(u.getId()),
            jsonPath("$.email").value(u.getEmail()),
            jsonPath("$.phone").value(u.getPhone()),
            jsonPath("$.address").value(u.getAddress()),
            jsonPath("$.type").value(u.getType().name()),
            jsonPath("$.balance").value(u.getBalance().doubleValue()),
            jsonPath("$.account.accountName").value(a.getAccountName()),
            jsonPath("$.account.accountNumber").value(a.getAccountNumber()),
            jsonPath("$.account.routingNumber").value(a.getRoutingNumber())
        );
  }

  private static String asJsonString(final Object obj) throws JsonProcessingException {
    return new ObjectMapper().writeValueAsString(obj);
  }
}
