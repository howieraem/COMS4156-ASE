package com.lgtm.easymoney.controllers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lgtm.easymoney.configs.UserTestConfig;
import com.lgtm.easymoney.configs.WebSecurityConfig;
import com.lgtm.easymoney.exceptions.InvalidUpdateException;
import com.lgtm.easymoney.exceptions.ResourceNotFoundException;
import com.lgtm.easymoney.exceptions.UnauthorizedException;
import com.lgtm.easymoney.payload.req.CreateGroupReq;
import com.lgtm.easymoney.payload.req.InviteToGroupReq;
import com.lgtm.easymoney.payload.req.LeaveGroupReq;
import com.lgtm.easymoney.payload.rsp.GroupAdsRsp;
import com.lgtm.easymoney.payload.rsp.GroupRsp;
import com.lgtm.easymoney.payload.rsp.ResourceCreatedRsp;
import com.lgtm.easymoney.security.JwtAuthenticationEntryPoint;
import com.lgtm.easymoney.security.JwtTokenProvider;
import com.lgtm.easymoney.security.UserPrincipal;
import com.lgtm.easymoney.services.GroupService;
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
 * Unit tests for group controller.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(GroupController.class)
@Import({WebSecurityConfig.class})
public class GroupControllerTest {
  @Autowired
  private MockMvc mvc;

  @MockBean
  private GroupService groupService;

  // We test jwt functionalities in integration tests instead
  @MockBean
  private UserServiceImpl userService;
  @MockBean
  private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
  @MockBean
  private JwtTokenProvider jwtTokenProvider;

  private CreateGroupReq createGroupReq;

  private InviteToGroupReq inviteToGroupReq;

  private LeaveGroupReq leaveGroupReq;

  private ResourceCreatedRsp createdRsp;

  private GroupRsp groupRsp;

  private String groupName = "test";

  private String groupDescription = "test";

  private Long expectedGid = 1L;

  private Long uid1 = 1L;

  private List<Long> uids = List.of(uid1);

  private Long uid2 = 2L;

  /** Establish payloads for further testing. */
  @Before
  public void setUp() {
    createGroupReq = new CreateGroupReq();
    createGroupReq.setName(groupName);
    createGroupReq.setDescription(groupDescription);
    createGroupReq.setUids(uids);

    createdRsp = new ResourceCreatedRsp(expectedGid);

    inviteToGroupReq = new InviteToGroupReq();
    inviteToGroupReq.setGid(expectedGid);
    inviteToGroupReq.setInviteeId(uid2);

    leaveGroupReq = new LeaveGroupReq();
    leaveGroupReq.setGid(expectedGid);

    groupRsp = new GroupRsp();
    groupRsp.setGid(expectedGid);
    groupRsp.setName(groupName);
    groupRsp.setDescription(groupDescription);
    groupRsp.setUids(uids);
  }

  @Test
  public void createGroupSuccess() throws Exception {
    Mockito.when(groupService.createGroup(UserTestConfig.PERSON1, createGroupReq))
        .thenReturn(createdRsp);

    var resultActions = person1PostCreate(createGroupReq);

    resultActions.andExpectAll(
        status().isCreated(),
        jsonPath("$.id").value(expectedGid));
  }

  @Test
  public void createGroupFailedByEmptyUids() throws Exception {
    createGroupReq.setUids(new ArrayList<>());

    var resultActions = person1PostCreate(createGroupReq);

    resultActions.andExpectAll(
        status().isBadRequest(),
        jsonPath("$.errorFields").value("uids"));
  }

  @Test
  public void createGroupFailedByUidsContainingNull() throws Exception {
    // `createGroupReq.setUids(List.of(uid1, null))` throws exception immediately
    // and can't simulate user input, so raw JSON is used here.
    var resultActions = mvc.perform(post("/group/create")
        .with(user(UserTestConfig.PERSON1_PRINCIPAL))
        .content(String.format("{\"name\": \"%s\", \"uids\": [%d, null]", groupName, uid1))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON));

    resultActions.andExpectAll(
        status().isBadRequest(),
        jsonPath("$.errorFields").value("uids"));
  }

  @Test
  public void createGroupFailedByNullOrEmptyName() throws Exception {
    createGroupReq.setName(null);
    person1PostCreate(createGroupReq).andExpectAll(
        status().isBadRequest(),
        jsonPath("$.errorFields").value("name"));

    createGroupReq.setName("");
    person1PostCreate(createGroupReq).andExpectAll(
        status().isBadRequest(),
        jsonPath("$.errorFields").value("name"));
  }

  @Test
  public void inviteSuccess() throws Exception {
    person1PutInvite(inviteToGroupReq).andExpect(status().isOk());
    Mockito.verify(groupService, Mockito.times(1))
        .inviteToGroup(UserTestConfig.PERSON1, inviteToGroupReq);
  }

  @Test
  public void inviteFailedByNonMember() throws Exception {
    inviteToGroupReq.setInviteeId(3L);
    Mockito.doThrow(new InvalidUpdateException("Group", expectedGid, "inviterId", uid2))
        .when(groupService)
        .inviteToGroup(UserTestConfig.PERSON2, inviteToGroupReq);

    var resultActions = mvc.perform(put("/group/invite")
        .with(user(UserTestConfig.PERSON2_PRINCIPAL))
        .content(asJsonString(inviteToGroupReq))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON));

    resultActions.andExpectAll(
        status().isBadRequest(),
        jsonPath("$.errorFields").value("inviterId")
    );
  }

  @Test
  public void inviteFailedByExistingMember() throws Exception {
    inviteToGroupReq.setInviteeId(uid1);  // invitee already in group
    Mockito.doThrow(new InvalidUpdateException("Group", expectedGid, "inviteeId", uid1))
        .when(groupService)
        .inviteToGroup(UserTestConfig.PERSON1, inviteToGroupReq);

    var resultActions = person1PutInvite(inviteToGroupReq);

    resultActions.andExpectAll(
        status().isBadRequest(),
        jsonPath("$.errorFields").value("inviteeId")
    );
  }

  @Test
  public void leaveSuccess() throws Exception {
    putLeave(leaveGroupReq, UserTestConfig.PERSON1_PRINCIPAL).andExpect(status().isOk());
    Mockito.verify(groupService, Mockito.times(1))
        .leaveGroup(UserTestConfig.PERSON1, leaveGroupReq);
  }

  @Test
  public void leaveFailedByNonMember() throws Exception {
    Mockito.doThrow(new InvalidUpdateException("Group", expectedGid, "uid", uid2))
        .when(groupService)
        .leaveGroup(UserTestConfig.PERSON2, leaveGroupReq);

    var resultActions = putLeave(leaveGroupReq, UserTestConfig.PERSON2_PRINCIPAL);

    resultActions.andExpectAll(
        status().isBadRequest(),
        jsonPath("$.errorFields").value("uid")
    );
  }

  @Test
  public void getGroupSuccess() throws Exception {
    Mockito.when(groupService.getGroupProfile(UserTestConfig.PERSON1, expectedGid))
        .thenReturn(groupRsp);

    var resultActions = person1GetGroup(expectedGid);

    resultActions.andExpectAll(
        status().isOk(),
        jsonPath("$.name").value(groupName),
        jsonPath("$.description").value(groupDescription),
        jsonPath("$.uids[0]").value(uid1)  // `jsonPath("$.uids").value(uids)` doesn't work
    );
  }

  @Test
  public void getNonExistentGroupFail() throws Exception {
    Long someId = 2L;
    Mockito.when(groupService.getGroupProfile(UserTestConfig.PERSON1, someId))
        .thenThrow(new ResourceNotFoundException("Group", "id", someId));

    var resultActions = person1GetGroup(someId);

    resultActions.andExpectAll(
        status().isNotFound(),
        jsonPath("$.errorFields").value("id")
    );
  }

  @Test
  public void getGroupFailedByInvalidPathVar() throws Exception {
    person1GetGroup(null).andExpectAll(
        status().isBadRequest(),
        jsonPath("$.errorFields").value("id")
    );

    person1GetGroup("abc").andExpectAll(
        status().isBadRequest(),
        jsonPath("$.errorFields").value("id")
    );
  }

  @Test
  public void getGroupFailedByNonMember() throws Exception {
    Mockito.when(groupService.getGroupProfile(UserTestConfig.PERSON2, expectedGid))
        .thenThrow(new UnauthorizedException(UserTestConfig.PERSON2.getId(), "Group", expectedGid));

    var resultActions = mvc.perform(get("/group/" + expectedGid)
        .with(user(UserTestConfig.PERSON2_PRINCIPAL)));

    resultActions.andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.errorFields").value("Authorization"));
  }

  @Test
  public void getGroupAdsSuccess() throws Exception {
    var ads = "abc";
    var groupAdsRsp = new GroupAdsRsp(List.of(ads));
    Mockito.when(groupService.getGroupAds(UserTestConfig.PERSON1, expectedGid))
        .thenReturn(groupAdsRsp);

    var resultActions = getGroupAds(UserTestConfig.PERSON1_PRINCIPAL, expectedGid);

    resultActions.andExpectAll(
        status().isOk(),
        jsonPath("$.ads").value(ads));
  }

  @Test
  public void getGroupAdsFailedByNonMember() throws Exception {
    Mockito.when(groupService.getGroupAds(UserTestConfig.PERSON2, expectedGid))
        .thenThrow(new UnauthorizedException(UserTestConfig.PERSON2.getId(), "Group", expectedGid));

    var resultActions = getGroupAds(UserTestConfig.PERSON2_PRINCIPAL, expectedGid);

    resultActions.andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.errorFields").value("Authorization"));
  }

  private ResultActions person1PostCreate(CreateGroupReq req) throws Exception {
    return mvc.perform(post("/group/create")
        .with(user(UserTestConfig.PERSON1_PRINCIPAL))
        .content(asJsonString(req))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON));
  }

  private ResultActions person1PutInvite(InviteToGroupReq req) throws Exception {
    return mvc.perform(put("/group/invite")
        .with(user(UserTestConfig.PERSON1_PRINCIPAL))
        .content(asJsonString(req))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON));
  }

  private ResultActions putLeave(LeaveGroupReq req, UserPrincipal principal) throws Exception {
    return mvc.perform(put("/group/leave")
        .with(user(principal))
        .content(asJsonString(req))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON));
  }

  private ResultActions person1GetGroup(Object id) throws Exception {
    return mvc.perform(get("/group/" + id).with(user(UserTestConfig.PERSON1_PRINCIPAL)));
  }

  private ResultActions getGroupAds(UserPrincipal principal, Object id) throws Exception {
    return mvc.perform(get("/group/" + id + "/business")
        .with(user(principal)));
  }

  private String asJsonString(final Object obj) throws JsonProcessingException {
    return new ObjectMapper().writeValueAsString(obj);
  }
}
