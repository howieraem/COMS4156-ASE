package com.lgtm.easymoney.controllers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lgtm.easymoney.configs.UserTestConfig;
import com.lgtm.easymoney.configs.WebSecurityConfig;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.req.FriendshipReq;
import com.lgtm.easymoney.payload.rsp.ProfileRsp;
import com.lgtm.easymoney.payload.rsp.ProfilesRsp;
import com.lgtm.easymoney.security.JwtAuthenticationEntryPoint;
import com.lgtm.easymoney.security.JwtTokenProvider;
import com.lgtm.easymoney.services.FriendService;
import com.lgtm.easymoney.services.impl.UserServiceImpl;
import java.util.ArrayList;
import java.util.List;
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
 * Unit test for FriendController.
 * {@link com.lgtm.easymoney.controllers.FriendController}
 * */

@RunWith(SpringRunner.class)
@WebMvcTest(FriendController.class)
@Import({WebSecurityConfig.class})
public class FriendControllerTest {
  @Autowired
  private MockMvc mvc;

  @MockBean
  private FriendService friendService;

  // We test jwt functionalities in integration tests instead
  @MockBean
  private UserServiceImpl userService;
  @MockBean
  private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
  @MockBean
  private JwtTokenProvider jwtTokenProvider;

  private FriendshipReq friendshipReq;
  private ProfilesRsp profilesRsp;
  private final User user1 = UserTestConfig.PERSON1;
  private final User user2 = UserTestConfig.PERSON2;
  private final Long uid1 = user1.getId();
  private final Long uid2 = user2.getId();

  /**
   * Set up reusable test fixtures.
   * */
  @Before
  public void setUp() {
    // friendshipreq
    friendshipReq = new FriendshipReq();
    friendshipReq.setUid(uid2);
    Mockito.when(userService.getUserById(uid2)).thenReturn(user2);
    ProfileRsp profileRsp = new ProfileRsp(user2);

    // profilesrsp
    List<ProfileRsp> res = new ArrayList<>();
    res.add(profileRsp);
    profilesRsp = new ProfilesRsp(res);
  }

  @Test
  public void addFriendSuccess() throws Exception {
    // Act
    ResultActions returnedResponse = person1PostAddFriend(friendshipReq);

    // Assert
    returnedResponse.andExpectAll(status().isCreated());
  }

  @Test
  public void addFriendFailWithNull() throws Exception {
    friendshipReq.setUid(null);
    // Act
    ResultActions returnedResponse = person1PostAddFriend(friendshipReq);

    // Assert
    returnedResponse.andExpectAll(
            status().isBadRequest(),
            jsonPath("$.errorFields").value("uid"));
  }

  @Test
  public void acceptFriendSuccess() throws Exception {
    // Arrange
    friendshipReq.setUid(uid1);

    // Act
    ResultActions returnedResponse =
        person2PutAcceptFriend(friendshipReq);

    // Assert
    returnedResponse.andExpectAll(
            status().isOk());
  }

  @Test
  public void deleteFriendSuccess() throws Exception {
    // Act
    ResultActions returnedResponse = person1DeleteFriend(uid2);

    // Assert
    returnedResponse.andExpectAll(
            status().isOk());
  }

  @Test
  public void getFriendsSuccess() throws Exception {
    // Arrange
    Mockito.when(friendService.getFriendProfiles(user1)).thenReturn(profilesRsp);

    // Act
    ResultActions returnedResponse = mvc.perform(
        get("/friend").with(user(UserTestConfig.PERSON1_PRINCIPAL)));

    // Assert
    returnedResponse.andExpectAll(
            status().isOk(),
            jsonPath("$.userProfiles[0].uid").value(uid2));
  }

  @Test
  public void getFriendsPendingSuccess() throws Exception {
    // Arrange
    Mockito.when(friendService.getFriendProfilesPending(user1)).thenReturn(profilesRsp);

    // Act
    ResultActions returnedResponse = mvc.perform(
        get("/friend/pending").with(user(UserTestConfig.PERSON1_PRINCIPAL)));

    // Assert
    returnedResponse.andExpectAll(
            status().isOk(),
            jsonPath("$.userProfiles[0].uid").value(uid2));
  }

  private ResultActions person1PostAddFriend(FriendshipReq req) throws Exception {
    return mvc.perform(post("/friend/add")
        .with(user(UserTestConfig.PERSON1_PRINCIPAL))
        .content(asJsonString(req))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON));
  }

  private ResultActions person2PutAcceptFriend(FriendshipReq req)
      throws Exception {
    return mvc.perform(put("/friend/accept")
        .with(user(UserTestConfig.PERSON2_PRINCIPAL))
        .content(asJsonString(req))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON));
  }

  private ResultActions person1DeleteFriend(Long friendUid)
      throws Exception {
    return mvc.perform(delete("/friend/{uid}", String.valueOf(friendUid))
        .with(user(UserTestConfig.PERSON1_PRINCIPAL)));
  }

  private String asJsonString(final Object obj) throws JsonProcessingException {
    return new ObjectMapper().writeValueAsString(obj);
  }
}
