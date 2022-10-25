package com.lgtm.easymoney.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lgtm.easymoney.exceptions.InvalidUpdateException;
import com.lgtm.easymoney.exceptions.ResourceNotFoundException;
import com.lgtm.easymoney.payload.CreateGroupReq;
import com.lgtm.easymoney.payload.GroupAdsRsp;
import com.lgtm.easymoney.payload.GroupRsp;
import com.lgtm.easymoney.payload.InviteToGroupReq;
import com.lgtm.easymoney.payload.LeaveGroupReq;
import com.lgtm.easymoney.payload.ResourceCreatedRsp;
import com.lgtm.easymoney.services.GroupService;
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
 * Unit tests for group controller.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(GroupController.class)
public class GroupControllerTest {
  @Autowired
  private MockMvc mvc;

  @MockBean
  private GroupService groupService;

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

  /** Establish request payloads for further testing. */
  @Before
  public void setUp() {
    createGroupReq = new CreateGroupReq();
    createGroupReq.setName(groupName);
    createGroupReq.setDescription(groupDescription);
    createGroupReq.setUids(uids);

    createdRsp = new ResourceCreatedRsp(expectedGid);

    inviteToGroupReq = new InviteToGroupReq();
    inviteToGroupReq.setGid(expectedGid);
    inviteToGroupReq.setInviterId(uid1);
    inviteToGroupReq.setInviteeId(uid2);

    leaveGroupReq = new LeaveGroupReq();
    leaveGroupReq.setGid(expectedGid);
    leaveGroupReq.setUid(uid1);

    groupRsp = new GroupRsp();
    groupRsp.setGid(expectedGid);
    groupRsp.setName(groupName);
    groupRsp.setDescription(groupDescription);
    groupRsp.setUids(uids);
  }

  @Test
  public void createGroupSuccess() throws Exception {
    Mockito.when(groupService.createGroup(createGroupReq))
        .thenReturn(createdRsp);

    var resultActions = postCreate(createGroupReq);

    resultActions.andExpectAll(
        status().isCreated(),
        jsonPath("$.id").value(expectedGid));
  }

  @Test
  public void createGroupFailedByEmptyUids() throws Exception {
    createGroupReq.setUids(new ArrayList<>());

    var resultActions = postCreate(createGroupReq);

    resultActions.andExpectAll(
        status().isBadRequest(),
        jsonPath("$.errorFields").value("uids"));
  }

  @Test
  public void createGroupFailedByUidsContainingNull() throws Exception {
    // `createGroupReq.setUids(List.of(uid1, null))` throws exception immediately
    // and can't simulate user input, so raw JSON is used here.
    var resultActions = mvc.perform(post("/group/create")
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
    postCreate(createGroupReq).andExpectAll(
        status().isBadRequest(),
        jsonPath("$.errorFields").value("name"));

    createGroupReq.setName("");
    postCreate(createGroupReq).andExpectAll(
        status().isBadRequest(),
        jsonPath("$.errorFields").value("name"));
  }

  @Test
  public void inviteSuccess() throws Exception {
    putInvite(inviteToGroupReq).andExpect(status().isOk());
    Mockito.verify(groupService, Mockito.times(1)).inviteToGroup(inviteToGroupReq);
  }

  @Test
  public void inviteFailedByNonMember() throws Exception {
    inviteToGroupReq.setInviterId(uid2);  // inviter not in group
    inviteToGroupReq.setInviteeId(3L);
    Mockito.doThrow(new InvalidUpdateException("Group", expectedGid, "inviterId", uid2))
        .when(groupService)
        .inviteToGroup(inviteToGroupReq);

    var resultActions = putInvite(inviteToGroupReq);

    resultActions.andExpectAll(
        status().isBadRequest(),
        jsonPath("$.errorFields").value("inviterId")
    );
  }

  @Test
  public void inviteFailedByExistingMember() throws Exception {
    inviteToGroupReq.setInviterId(uid1);
    inviteToGroupReq.setInviteeId(uid1);  // invitee already in group
    Mockito.doThrow(new InvalidUpdateException("Group", expectedGid, "inviteeId", uid2))
        .when(groupService)
        .inviteToGroup(inviteToGroupReq);

    var resultActions = putInvite(inviteToGroupReq);

    resultActions.andExpectAll(
        status().isBadRequest(),
        jsonPath("$.errorFields").value("inviteeId")
    );
  }

  @Test
  public void leaveSuccess() throws Exception {
    putLeave(leaveGroupReq).andExpect(status().isOk());
    Mockito.verify(groupService, Mockito.times(1)).leaveGroup(leaveGroupReq);
  }

  @Test
  public void leaveFailedByNonMember() throws Exception {
    leaveGroupReq.setUid(uid2);  // user not in group can't leave
    Mockito.doThrow(new InvalidUpdateException("Group", expectedGid, "uid", uid2))
        .when(groupService)
        .leaveGroup(leaveGroupReq);

    var resultActions = putLeave(leaveGroupReq);

    resultActions.andExpectAll(
        status().isBadRequest(),
        jsonPath("$.errorFields").value("uid")
    );
  }

  @Test
  public void getGroupSuccess() throws Exception {
    Mockito.when(groupService.getGroupProfile(expectedGid))
        .thenReturn(groupRsp);

    var resultActions = getGroup(expectedGid);

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
    Mockito.when(groupService.getGroupProfile(someId))
        .thenThrow(new ResourceNotFoundException("Group", "id", someId));

    var resultActions = getGroup(someId);

    resultActions.andExpectAll(
        status().isNotFound(),
        jsonPath("$.errorFields").value("id")
    );
  }

  @Test
  public void getGroupFailedByInvalidPathVar() throws Exception {
    getGroup(null).andExpectAll(
        status().isBadRequest(),
        jsonPath("$.errorFields").value("id")
    );

    getGroup("abc").andExpectAll(
        status().isBadRequest(),
        jsonPath("$.errorFields").value("id")
    );
  }

  @Test
  public void getGroupAdsSuccess() throws Exception {
    var ads = "abc";
    var groupAdsRsp = new GroupAdsRsp(List.of(ads));
    Mockito.when(groupService.getGroupAds(expectedGid))
        .thenReturn(groupAdsRsp);

    var resultActions = getGroupAds(expectedGid);

    resultActions.andExpectAll(
        status().isOk(),
        jsonPath("$.ads").value(ads));
  }

  private ResultActions postCreate(CreateGroupReq req) throws Exception {
    return mvc.perform(post("/group/create")
        .content(asJsonString(req))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON));
  }

  private ResultActions putInvite(InviteToGroupReq req) throws Exception {
    return mvc.perform(put("/group/invite")
        .content(asJsonString(req))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON));
  }

  private ResultActions putLeave(LeaveGroupReq req) throws Exception {
    return mvc.perform(put("/group/leave")
        .content(asJsonString(req))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON));
  }

  private ResultActions getGroup(Object id) throws Exception {
    return mvc.perform(get("/group/" + id));
  }

  private ResultActions getGroupAds(Object id) throws Exception {
    return mvc.perform(get("/group/" + id + "/business"));
  }

  private String asJsonString(final Object obj) throws JsonProcessingException {
    return new ObjectMapper().writeValueAsString(obj);
  }
}
