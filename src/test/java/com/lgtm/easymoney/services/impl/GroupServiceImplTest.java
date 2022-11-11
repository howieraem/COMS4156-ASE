package com.lgtm.easymoney.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import com.lgtm.easymoney.exceptions.InvalidUpdateException;
import com.lgtm.easymoney.exceptions.ResourceNotFoundException;
import com.lgtm.easymoney.models.BizProfile;
import com.lgtm.easymoney.models.Group;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.req.CreateGroupReq;
import com.lgtm.easymoney.payload.req.InviteToGroupReq;
import com.lgtm.easymoney.payload.req.LeaveGroupReq;
import com.lgtm.easymoney.payload.rsp.GroupRsp;
import com.lgtm.easymoney.payload.rsp.ResourceCreatedRsp;
import com.lgtm.easymoney.repositories.GroupRepository;
import com.lgtm.easymoney.services.UserService;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Unit tests for group service implementation.
 */
@RunWith(SpringRunner.class)
public class GroupServiceImplTest {
  @InjectMocks
  private GroupServiceImpl groupService;

  @Mock
  private GroupRepository groupRepository;

  @Mock
  private UserService userService;

  private CreateGroupReq createGroupReq;
  private InviteToGroupReq inviteToGroupReq;
  private LeaveGroupReq leaveGroupReq;
  private ResourceCreatedRsp createdRsp;
  private GroupRsp groupRsp;

  private String groupName = "test";
  private String groupDescription = "test";
  private Long expectedGid = 1L;
  private Long nonExistGid = 2L;
  private Long uid1 = 1L;
  private List<Long> uids = List.of(uid1);
  private Long uid2 = 2L;
  private Group group;
  private User user1;
  private User user2;
  private Set<User> users;

  /** Establish users, group and request payloads for further testing. */
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

    user1 = new User();
    user1.setId(uid1);
    user1.setTypeByStr("business");
    var bizProfile = new BizProfile();
    bizProfile.setBizUser(user1);
    bizProfile.setPromotionText("abc");
    user1.setBizProfile(bizProfile);

    users = new HashSet<>();
    users.add(user1);
    user2 = new User();
    user2.setId(uid2);
    Mockito.when(userService.getUserById(uid1)).thenReturn(user1);
    Mockito.when(userService.getUserById(uid2)).thenReturn(user2);

    group = new Group();
    group.setId(expectedGid);
    group.setName(groupName);
    group.setDescription(groupDescription);
    group.setGroupUsers(users);
    Mockito.when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
  }

  @Test
  public void testFindGroup() {
    var g = groupService.getGroupById(expectedGid);

    assertEquals(g.getId(), expectedGid);
    assertEquals(g.getGroupUsers(), users);
    assertEquals(g.getName(), groupName);
    assertEquals(g.getDescription(), groupDescription);
  }

  @Test
  public void testFindNonExistGroup() {
    Mockito.when(groupRepository.findById(nonExistGid)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> groupService.getGroupById(nonExistGid));
  }

  @Test
  public void testCreateGroup() {
    Mockito.doAnswer(invocation -> {
      ReflectionTestUtils.setField((Group) invocation.getArgument(0), "id", expectedGid);
      return invocation.getArgument(0);
    }).when(groupRepository).save(Mockito.any(Group.class));

    var rsp = groupService.createGroup(user1, createGroupReq);

    assertEquals(rsp, createdRsp);
  }

  @Test
  public void testInvite() {
    groupService.inviteToGroup(inviteToGroupReq);

    Mockito.verify(groupRepository, Mockito.times(1)).save(Mockito.any(Group.class));
  }

  @Test
  public void inviteFailedByNonMemberInviter() {
    inviteToGroupReq.setInviterId(uid2);

    assertThrows(InvalidUpdateException.class, () -> groupService.inviteToGroup(inviteToGroupReq));
  }

  @Test
  public void inviteFailedByMemberInvitee() {
    inviteToGroupReq.setInviteeId(uid1);

    assertThrows(InvalidUpdateException.class, () -> groupService.inviteToGroup(inviteToGroupReq));
  }

  @Test
  public void testLeave() {
    groupService.leaveGroup(leaveGroupReq);

    Mockito.verify(groupRepository, Mockito.times(1)).save(Mockito.any(Group.class));
  }

  @Test
  public void leaveFailedByNonMember() {
    leaveGroupReq.setUid(uid2);

    assertThrows(InvalidUpdateException.class, () -> groupService.leaveGroup(leaveGroupReq));
  }

  @Test
  public void testGetGroup() {
    var rsp = groupService.getGroupProfile(expectedGid);

    assertEquals(rsp, groupRsp);
  }

  @Test
  public void testGetGroupAds() {
    var rsp = groupService.getGroupAds(expectedGid);

    assertEquals(rsp.getAds(), List.of(user1.getBizProfile().getPromotionText()));
  }
}
