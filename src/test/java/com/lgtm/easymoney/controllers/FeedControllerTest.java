package com.lgtm.easymoney.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.jayway.jsonpath.JsonPath;
import com.lgtm.easymoney.configs.UserTestConfig;
import com.lgtm.easymoney.configs.WebSecurityConfig;
import com.lgtm.easymoney.enums.Category;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.rsp.FeedActivityRsp;
import com.lgtm.easymoney.payload.rsp.FeedRsp;
import com.lgtm.easymoney.security.JwtAuthenticationEntryPoint;
import com.lgtm.easymoney.security.JwtTokenProvider;
import com.lgtm.easymoney.services.FeedService;
import com.lgtm.easymoney.services.impl.UserServiceImpl;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
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
 * Unit tests for feed controller.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(FeedController.class)
@Import({WebSecurityConfig.class})
public class FeedControllerTest {
  @Autowired
  private MockMvc mvc;

  @MockBean
  private FeedService feedService;

  // We test jwt functionalities in integration tests instead
  @MockBean
  private UserServiceImpl userService;
  @MockBean
  private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
  @MockBean
  private JwtTokenProvider jwtTokenProvider;

  private Date now;
  private FeedActivityRsp feedActivityRsp;
  private FeedRsp feedRsp;
  private static final User fromUsr = UserTestConfig.PERSON1;
  private static final User toUsr = UserTestConfig.PERSON2;

  /** Feed fixtures. */
  @Before
  public void setUp() {
    now = new Date();

    feedActivityRsp = new FeedActivityRsp(
        fromUsr.getId(),
        toUsr.getId(),
        fromUsr.getType(),
        toUsr.getType(),
        Category.PARTY,
        new BigDecimal(1),
        null,
        now,
        null
    );
    feedRsp = new FeedRsp(List.of(feedActivityRsp));
  }

  @Test
  public void testGetFeed() throws Exception {
    Mockito.when(feedService.getFeed(fromUsr)).thenReturn(feedRsp);

    var resultActions = mvc.perform(get("/feed").with(user(UserTestConfig.PERSON1_PRINCIPAL)));

    resultActions.andExpectAll(
        status().isOk(),
        jsonPath("$.activities[0].fromUid").value(feedActivityRsp.getFromUid()),
        jsonPath("$.activities[0].toUid").value(feedActivityRsp.getToUid()),
        jsonPath("$.activities[0].fromType").value(feedActivityRsp.getFromType().name()),
        jsonPath("$.activities[0].toType").value(feedActivityRsp.getToType().name()),
        jsonPath("$.activities[0].category").value(feedActivityRsp.getCategory().name()),
        jsonPath("$.activities[0].amount").value(feedActivityRsp.getAmount()),
        jsonPath("$.activities[0].promoText").value(feedActivityRsp.getPromoText())
    );

    var dateString = JsonPath.read(resultActions.andReturn().getResponse().getContentAsString(),
        "$.activities[0].lastUpdateTime").toString();
    var sdf = new StdDateFormat();
    assertEquals(sdf.parse(dateString), now);
  }
}
