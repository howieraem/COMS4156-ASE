package com.lgtm.easymoney.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lgtm.easymoney.models.Account;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.FriendshipReq;
import com.lgtm.easymoney.payload.ProfileRsp;
import com.lgtm.easymoney.payload.ProfilesRsp;
import com.lgtm.easymoney.services.FriendService;
import com.lgtm.easymoney.services.UserService;
import java.util.ArrayList;
import java.util.List;
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
 * Unit test for FriendController.
 * {@link com.lgtm.easymoney.controllers.FriendController}
 * */

@RunWith(SpringRunner.class)
@WebMvcTest(FriendController.class)
public class FriendControllerTest {
  @Autowired
  private MockMvc mvc;
  @MockBean
  private FriendService friendService;
  @MockBean
  private UserService userService;
  private FriendshipReq friendshipReq;
  private ProfileRsp profileRsp;
  private ProfilesRsp profilesRsp;
  private Long uid1 = 1L;
  private Long uid2 = 2L;
  private User friend = new User();
  private Account friendAccount = new Account();

  /**
   * Set up reusable test fixtures.
   * */
  @Before
  public void setUp() {
    // friendshipreq
    friendshipReq = new FriendshipReq();
    friendshipReq.setUid1(uid1);
    friendshipReq.setUid2(uid2);
    // profilersp
    friend.setId(uid2);
    friendAccount.setAccountName("friendAccount");
    friend.setAccount(friendAccount);
    Mockito.when(userService.getUserById(uid2)).thenReturn(friend);
    friendAccount.setAccountUser(friend);
    profileRsp = new ProfileRsp(friend);
    // profilesrsp
    List<ProfileRsp> res = new ArrayList<>();
    res.add(profileRsp);
    profilesRsp = new ProfilesRsp(Boolean.TRUE, res);
  }

  @Test
  public void addFriendSuccess() throws Exception {
    // Act
    ResultActions returnedResponse = postAddFriend(friendshipReq);

    // Assert
    returnedResponse.andExpectAll(
            status().isCreated());
  }

  @Test
  public void addFriendFailWithNull() throws Exception {
    friendshipReq.setUid1(null);
    // Act
    ResultActions returnedResponse = postAddFriend(friendshipReq);

    // Assert
    returnedResponse.andExpectAll(
            status().isBadRequest(),
            jsonPath("$.errorFields").value("uid1"));
  }

  @Test
  public void acceptFriendSuccess() throws Exception {
    // Act
    ResultActions returnedResponse = putAcceptFriend(friendshipReq);

    // Assert
    returnedResponse.andExpectAll(
            status().isOk());
  }

  @Test
  public void deleteFriendSuccess() throws Exception {
    // Act
    ResultActions returnedResponse = deleteDelFriend(friendshipReq);

    // Assert
    returnedResponse.andExpectAll(
            status().isOk());
  }

  @Test
  public void getFriendsSuccess() throws Exception {
    // Arrange
    Mockito.when(friendService.getFriends(uid1)).thenReturn(profilesRsp);

    // Act
    ResultActions returnedResponse =
            mvc.perform(get("/friend/{uid}", String.valueOf(uid1)));

    // Assert
    returnedResponse.andExpectAll(
            status().isOk(),
            jsonPath("$.success").value(true),
            jsonPath("$.userProfiles[0].uid").value(uid2));
  }

  @Test
  public void getFriendsFailedWithEmptyInput() throws Exception {
    // Act
    ResultActions returnedResponse =
            mvc.perform(get("/friend/{uid}", String.valueOf("")));

    // Assert
    returnedResponse.andExpectAll(
            status().isNotFound());
  }

  @Test
  public void getFriendsPendingSuccess() throws Exception {
    // Arrange
    Mockito.when(friendService.getFriendsPending(uid1)).thenReturn(profilesRsp);

    // Act
    ResultActions returnedResponse =
            mvc.perform(get("/friend/{uid}/pending", String.valueOf(uid1)));

    // Assert
    returnedResponse.andExpectAll(
            status().isOk(),
            jsonPath("$.success").value(true),
            jsonPath("$.userProfiles[0].uid").value(uid2));
  }

  private ResultActions postAddFriend(FriendshipReq req) throws Exception {
    return mvc.perform(post("/friend/add")
            .content(asJsonString(req))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));
  }

  private ResultActions putAcceptFriend(FriendshipReq req) throws Exception {
    return mvc.perform(put("/friend/accept")
            .content(asJsonString(req))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));
  }

  private ResultActions deleteDelFriend(FriendshipReq req) throws Exception {
    return mvc.perform(delete("/friend/delete")
            .content(asJsonString(req))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON));
  }

  private String asJsonString(final Object obj) throws JsonProcessingException {
    return new ObjectMapper().writeValueAsString(obj);
  }
}
