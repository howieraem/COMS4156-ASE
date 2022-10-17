package com.lgtm.easymoney.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lgtm.easymoney.controllers.AuthController;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.RegisterReq;
import com.lgtm.easymoney.services.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
//@AutoConfigureMockMvc
@WebMvcTest(AuthController.class)
public class AuthControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    private static RegisterReq req;

    @Before
    public void setup() {
        req = new RegisterReq();
        req.setEmail("a@a.com");
        req.setPassword("a");
        req.setUserType("personal");
        req.setAccountName("a");
        req.setAccountNumber("123");
        req.setRoutingNumber("123456789");

        // Assume userService.saveUser() always succeeds
        Mockito.when(userService.saveUser(Mockito.any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    public void registerSuccessful() throws Exception {
        postRegister(req).andExpect(status().isCreated());
//                .andExpect(jsonPath("$.uid").value(1L));
    }

    @Test
    public void registerFailedByInvalidEmail() throws Exception {
        req.setEmail(null);
        postRegister(req).andExpect(status().isBadRequest());

        req.setEmail("");
        postRegister(req).andExpect(status().isBadRequest());

        req.setEmail("a");
        postRegister(req).andExpect(status().isBadRequest());
    }

    @Test
    public void registerFailedByInvalidPassword() throws Exception {
        req.setPassword(null);
        postRegister(req).andExpect(status().isBadRequest());

        req.setPassword("");
        postRegister(req).andExpect(status().isBadRequest());
    }

    @Test
    public void registerFailedByInvalidUserType() throws Exception {
        req.setUserType(null);
        postRegister(req).andExpect(status().isBadRequest());

        req.setUserType("");
        postRegister(req).andExpect(status().isBadRequest());
    }

    @Test
    public void registerFailedByInvalidPhone() throws Exception {
        req.setPhone("");
        postRegister(req).andExpect(status().isBadRequest());

        req.setPhone("abc");
        postRegister(req).andExpect(status().isBadRequest());

        req.setPhone("000");
        postRegister(req).andExpect(status().isBadRequest());

        req.setPhone("00000000000");
        postRegister(req).andExpect(status().isBadRequest());
    }

    @Test
    public void registerFailedByInvalidAccountName() throws Exception {
        req.setAccountName(null);
        postRegister(req).andExpect(status().isBadRequest());

        req.setAccountName("");
        postRegister(req).andExpect(status().isBadRequest());
    }

    @Test
    public void registerFailedByInvalidAccountNumber() throws Exception {
        req.setAccountNumber(null);
        postRegister(req).andExpect(status().isBadRequest());

        req.setAccountNumber("");
        postRegister(req).andExpect(status().isBadRequest());

        req.setAccountNumber("abc");
        postRegister(req).andExpect(status().isBadRequest());

        req.setAccountNumber("12321371892473218947122");
        postRegister(req).andExpect(status().isBadRequest());
    }

    @Test
    public void registerFailedByInvalidRoutingNumber() throws Exception {
        req.setRoutingNumber(null);
        postRegister(req).andExpect(status().isBadRequest());

        req.setRoutingNumber("");
        postRegister(req).andExpect(status().isBadRequest());

        req.setRoutingNumber("abc");
        postRegister(req).andExpect(status().isBadRequest());

        req.setRoutingNumber("12321");
        postRegister(req).andExpect(status().isBadRequest());

        req.setRoutingNumber("12321371892473218947122");
        postRegister(req).andExpect(status().isBadRequest());
    }

    public ResultActions postRegister(RegisterReq req) throws Exception {
        return mvc.perform(post("/user/register")
                .content(asJsonString(req))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
    }

    public static String asJsonString(final Object obj) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(obj);
    }
}
