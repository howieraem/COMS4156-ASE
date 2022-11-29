package com.lgtm.easymoney.controllers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.lgtm.easymoney.configs.UserTestConfig;
import com.lgtm.easymoney.configs.WebSecurityConfig;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.rsp.AnalyticRsp;
import com.lgtm.easymoney.security.JwtAuthenticationEntryPoint;
import com.lgtm.easymoney.security.JwtTokenProvider;
import com.lgtm.easymoney.services.AnalyticService;
import com.lgtm.easymoney.services.impl.UserServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Unit tests for analytic controller.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(AnalyticController.class)
@Import({WebSecurityConfig.class})
public class AnalyticControllerTest {
  @Autowired
  private MockMvc mvc;

  @MockBean
  private AnalyticService analyticService;

  // We test jwt functionalities in integration tests instead
  @MockBean
  private UserServiceImpl userService;
  @MockBean
  private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
  @MockBean
  private JwtTokenProvider jwtTokenProvider;

  private AnalyticRsp analyticRsp;
  private static final User u = UserTestConfig.PERSON1;

  /** Analytic fixtures. */
  @Before
  public void setUp() {
    analyticRsp = new AnalyticRsp(u);
  }

  @Test
  public void testGetAnalytic() throws Exception {
    Mockito.when(analyticService.getAnalytic(u)).thenReturn(analyticRsp);

    var resultActions = mvc.perform(get("/analytic").with(user(UserTestConfig.PERSON1_PRINCIPAL)));

    resultActions.andExpectAll(
            status().isOk(),
            jsonPath("$.uid").value(analyticRsp.getUid()),
            jsonPath("$.accountName").value(analyticRsp.getAccountName()),
            jsonPath("$.currBalance").value(analyticRsp.getCurrBalance())
    );
  }
}
