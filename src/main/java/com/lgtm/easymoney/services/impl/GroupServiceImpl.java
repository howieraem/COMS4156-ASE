package com.lgtm.easymoney.services.impl;

import com.lgtm.easymoney.enums.UserType;
import com.lgtm.easymoney.exceptions.InvalidUpdateException;
import com.lgtm.easymoney.exceptions.ResourceNotFoundException;
import com.lgtm.easymoney.models.Group;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.CreateGroupReq;
import com.lgtm.easymoney.payload.GroupAdsRsp;
import com.lgtm.easymoney.payload.GroupRsp;
import com.lgtm.easymoney.payload.InviteToGroupReq;
import com.lgtm.easymoney.payload.LeaveGroupReq;
import com.lgtm.easymoney.payload.ResourceCreatedRsp;
import com.lgtm.easymoney.repositories.GroupRepository;
import com.lgtm.easymoney.services.GroupService;
import com.lgtm.easymoney.services.UserService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * group service implementation for accessing groups DB.
 */
@Service
public class GroupServiceImpl implements GroupService {
  private final GroupRepository groupRepository;
  private final UserService userService;

  @Autowired
  public GroupServiceImpl(GroupRepository groupRepository, UserService userService) {
    this.groupRepository = groupRepository;
    this.userService = userService;
  }

  @Override
  public Group getGroupById(Long gid) {
    var groupWrapper = groupRepository.findById(gid);
    if (groupWrapper.isEmpty()) {
      throw new ResourceNotFoundException("Group", "id", gid);
    }
    return groupWrapper.get();
  }

  @Override
  public GroupRsp getGroupProfile(Long gid) {
    Group g = getGroupById(gid);
    GroupRsp r = new GroupRsp();
    r.setGid(g.getId());
    r.setName(g.getName());
    r.setDescription(g.getDescription());
    r.setUids(g.getGroupUsers().stream()
            .map(User::getId)
            .collect(Collectors.toList()));
    return r;
  }

  @Override
  public GroupAdsRsp getGroupAds(Long gid) {
    Group g = getGroupById(gid);
    Set<User> users = g.getGroupUsers();
    List<String> ads = new ArrayList<>();
    for (User u : users) {
      if (u.getType().equals(UserType.BUSINESS)) {
        ads.add(u.getBizProfile().getPromotionText());
      }
    }
    return new GroupAdsRsp(ads);
  }

  @Override
  public ResourceCreatedRsp createGroup(CreateGroupReq createGroupReq) {
    Set<User> users = new HashSet<>();
    for (Long uid : createGroupReq.getUids()) {
      users.add(userService.getUserById(uid));
    }
    Group group = new Group();
    group.setGroupUsers(users);
    group.setName(createGroupReq.getName());
    group.setDescription(createGroupReq.getDescription());
    group = groupRepository.save(group);
    return new ResourceCreatedRsp(group.getId());
  }

  @Override
  public void inviteToGroup(InviteToGroupReq inviteToGroupReq) {
    Group g = getGroupById(inviteToGroupReq.getGid());
    User inviter = userService.getUserById(inviteToGroupReq.getInviterId());
    User invitee = userService.getUserById(inviteToGroupReq.getInviteeId());
    if (!isInGroup(g, inviter)) {
      throw new InvalidUpdateException("Group", g.getId(), "inviterId", inviter.getId());
    }
    joinGroup(g, invitee);
  }

  @Override
  public void leaveGroup(LeaveGroupReq leaveGroupReq) {
    Group g = getGroupById(leaveGroupReq.getGid());
    User u = userService.getUserById(leaveGroupReq.getUid());
    leaveGroup(g, u);
  }

  @Override
  public void leaveGroup(Group group, User user) {
    if (!isInGroup(group, user)) {
      throw new InvalidUpdateException("Group", group.getId(), "uid", user.getId());
    }
    group.getGroupUsers().remove(user);
    groupRepository.save(group);
  }

  @Override
  public void joinGroup(Group group, User user) {
    if (isInGroup(group, user)) {
      throw new InvalidUpdateException("Group", group.getId(), "inviteeId", user.getId());
    }
    group.getGroupUsers().add(user);
    groupRepository.save(group);
  }

  @Override
  public boolean isInGroup(Group group, User user) {
    return group.getGroupUsers().contains(user);
  }
}
