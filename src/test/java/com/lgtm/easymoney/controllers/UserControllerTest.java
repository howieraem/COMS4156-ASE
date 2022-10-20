package com.lgtm.easymoney.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lgtm.easymoney.exceptions.InvalidUpdateException;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.BalanceReq;
import com.lgtm.easymoney.payload.BalanceRsp;
import com.lgtm.easymoney.payload.RegisterReq;
import com.lgtm.easymoney.services.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    private static User user;
    private static BalanceReq req;

    @Before
    public void setup() {
        Long uid = 1L;

        user = new User();
        user.setId(uid);
        user.setBalance(new BigDecimal(100));
        Mockito.when(userService.getUserByID(uid)).thenReturn(user);

        req = new BalanceReq();
        req.setUid(uid);
    }

    @Test
    public void depositSuccess() throws Exception {
        req.setAmount(new BigDecimal(100));
        putDeposit(req).andExpect(status().isOk());

        req.setAmount(new BigDecimal("0.1"));
        putDeposit(req).andExpect(status().isOk());

        req.setAmount(new BigDecimal("0.01"));
        putDeposit(req).andExpect(status().isOk());
    }

    @Test
    public void depositFailedByInvalidAmount() throws Exception {
        req.setAmount(new BigDecimal(-100));
        putDeposit(req).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields").value("amount"));

        req.setAmount(BigDecimal.ZERO);
        putDeposit(req).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields").value("amount"));

        req.setAmount(new BigDecimal("0.001"));
        putDeposit(req).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields").value("amount"));
    }

    @Test
    public void withdrawSuccess() throws Exception {
        req.setAmount(new BigDecimal(99));
        putWithdraw(req).andExpect(status().isOk());

        req.setAmount(new BigDecimal("0.1"));
        putWithdraw(req).andExpect(status().isOk());

        req.setAmount(new BigDecimal("0.01"));
        putWithdraw(req).andExpect(status().isOk());
    }

    @Test
    public void withdrawFailedByInvalidAmount() throws Exception {
        req.setAmount(new BigDecimal(-100));
        putWithdraw(req).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields").value("amount"));

        req.setAmount(BigDecimal.ZERO);
        putWithdraw(req).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields").value("amount"));

        req.setAmount(new BigDecimal("0.001"));
        putWithdraw(req).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields").value("amount"));

        req.setAmount(new BigDecimal(2000));
        Mockito.when(userService.makeAWithdraw(req)).thenThrow(new InvalidUpdateException("User", user.getId(), "amount", req.getAmount()));
        putWithdraw(req).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorFields").value("amount"));
    }

    public ResultActions putDeposit(BalanceReq req) throws Exception {
        return mvc.perform(put("/user/deposit")
                .content(asJsonString(req))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
    }

    public ResultActions putWithdraw(BalanceReq req) throws Exception {
        return mvc.perform(put("/user/withdraw")
                .content(asJsonString(req))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
    }

    public static String asJsonString(final Object obj) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(obj);
    }
}
