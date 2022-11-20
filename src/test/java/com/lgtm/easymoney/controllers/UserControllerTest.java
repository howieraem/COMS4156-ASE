package com.lgtm.easymoney.controllers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lgtm.easymoney.configs.UserTestConfig;
import com.lgtm.easymoney.configs.WebSecurityConfig;
import com.lgtm.easymoney.exceptions.InapplicableOperationException;
import com.lgtm.easymoney.exceptions.InvalidUpdateException;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.req.BalanceReq;
import com.lgtm.easymoney.payload.req.BizProfileReq;
import com.lgtm.easymoney.security.JwtAuthenticationEntryPoint;
import com.lgtm.easymoney.security.JwtTokenProvider;
import com.lgtm.easymoney.security.UserPrincipal;
import com.lgtm.easymoney.services.impl.UserServiceImpl;
import java.math.BigDecimal;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;


/**
 * Unit tests for user controller.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
@Import({WebSecurityConfig.class})
public class UserControllerTest {
  @Autowired
  private MockMvc mvc;

  // We test jwt functionalities in integration tests instead
  @MockBean
  private UserServiceImpl userService;
  @MockBean
  private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
  @MockBean
  private JwtTokenProvider jwtTokenProvider;

  private static final User person = UserTestConfig.PERSON1;
  private static BalanceReq balanceReq;
  private static BizProfileReq bizProfileReq;

  /** Establish user and request payload for further testing. */
  @Before
  public void setup() {
    Mockito.when(userService.getUserById(person.getId())).thenReturn(person);

    balanceReq = new BalanceReq();

    bizProfileReq = new BizProfileReq();
    bizProfileReq.setPromotionText("abc");
  }

  @Test
  public void testPrincipalGetDetails() throws Exception {
    var u = UserTestConfig.PERSON1;
    var a = u.getAccount();
    var resultActions = mvc.perform(get("/user/me").with(user(UserTestConfig.PERSON1_PRINCIPAL)));

    resultActions.andExpectAll(
        status().isOk(),
        jsonPath("$.id").value(u.getId()),
        jsonPath("$.email").value(u.getEmail()),
        jsonPath("$.phone").value(u.getPhone()),
        jsonPath("$.address").value(u.getAddress()),
        jsonPath("$.type").value(u.getType().name()),
        jsonPath("$.balance").value(u.getBalance().doubleValue()),
        jsonPath("$.account.accountName").value(a.getAccountName()),
        jsonPath("$.account.accountNumber").value(a.getAccountNumber()),
        jsonPath("$.account.routingNumber").value(a.getRoutingNumber()));
  }

  @Test
  public void depositSuccess() throws Exception {
    balanceReq.setAmount(new BigDecimal(100));
    putDeposit(balanceReq).andExpect(status().isOk());

    balanceReq.setAmount(new BigDecimal("0.1"));
    putDeposit(balanceReq).andExpect(status().isOk());

    balanceReq.setAmount(new BigDecimal("0.01"));
    putDeposit(balanceReq).andExpect(status().isOk());
  }

  @Test
  public void depositFailedByInvalidAmount() throws Exception {
    balanceReq.setAmount(new BigDecimal(-100));
    putDeposit(balanceReq).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("amount"));

    balanceReq.setAmount(BigDecimal.ZERO);
    putDeposit(balanceReq).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("amount"));

    balanceReq.setAmount(new BigDecimal("0.001"));
    putDeposit(balanceReq).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("amount"));
  }

  @Test
  public void withdrawSuccess() throws Exception {
    balanceReq.setAmount(new BigDecimal(99));
    putWithdraw(balanceReq).andExpect(status().isOk());

    balanceReq.setAmount(new BigDecimal("0.1"));
    putWithdraw(balanceReq).andExpect(status().isOk());

    balanceReq.setAmount(new BigDecimal("0.01"));
    putWithdraw(balanceReq).andExpect(status().isOk());
  }

  @Test
  public void withdrawFailedByInvalidAmount() throws Exception {
    balanceReq.setAmount(new BigDecimal(-100));
    putWithdraw(balanceReq).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("amount"));

    balanceReq.setAmount(BigDecimal.ZERO);
    putWithdraw(balanceReq).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("amount"));

    balanceReq.setAmount(new BigDecimal("0.001"));
    putWithdraw(balanceReq).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("amount"));

    balanceReq.setAmount(new BigDecimal(2000));
    Mockito.when(userService.makeWithdraw(person, balanceReq.getAmount()))
        .thenThrow(
            new InvalidUpdateException("User", person.getId(), "amount", balanceReq.getAmount()));
    putWithdraw(balanceReq).andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("amount"));
  }

  @Test
  public void testUpdateBizProfile() throws Exception {
    putUpdateBizProfile(UserTestConfig.BIZ_PRINCIPAL, bizProfileReq).andExpect(status().isOk());
    Mockito.verify(userService, Mockito.times(1))
        .updateBizProfile(UserTestConfig.BIZ_USR, bizProfileReq);
  }

  @Test
  public void updateBizProfileFailedByPersonalUser() throws Exception {
    Mockito.doThrow(new InapplicableOperationException(
        "user", person.getId(), "Authorization", "updateBizProfile"))
        .when(userService).updateBizProfile(person, bizProfileReq);

    putUpdateBizProfile(UserTestConfig.PERSON1_PRINCIPAL, bizProfileReq)
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorFields").value("Authorization"));
  }

  private ResultActions putDeposit(BalanceReq req) throws Exception {
    return mvc.perform(put("/user/deposit")
        .with(user(UserTestConfig.PERSON1_PRINCIPAL))
        .content(asJsonString(req))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON));
  }

  private ResultActions putWithdraw(BalanceReq req) throws Exception {
    return mvc.perform(put("/user/withdraw")
        .with(user(UserTestConfig.PERSON1_PRINCIPAL))
        .content(asJsonString(req))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON));
  }

  private ResultActions putUpdateBizProfile(UserPrincipal principal, BizProfileReq req)
      throws Exception {
    return mvc.perform(put("/user/biz")
        .with(user(principal))
        .content(asJsonString(req))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON));
  }

  public static String asJsonString(final Object obj) throws JsonProcessingException {
    return new ObjectMapper().writeValueAsString(obj);
  }
}
