package com.lgtm.easymoney.services.impl;

import com.lgtm.easymoney.enums.UserType;
import com.lgtm.easymoney.exceptions.InvalidUpdateException;
import com.lgtm.easymoney.exceptions.ResourceNotFoundException;
import com.lgtm.easymoney.exceptions.UnauthorizedException;
import com.lgtm.easymoney.models.Group;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.req.CreateGroupReq;
import com.lgtm.easymoney.payload.req.InviteToGroupReq;
import com.lgtm.easymoney.payload.req.LeaveGroupReq;
import com.lgtm.easymoney.payload.rsp.GroupAdsRsp;
import com.lgtm.easymoney.payload.rsp.GroupRsp;
import com.lgtm.easymoney.payload.rsp.ResourceCreatedRsp;
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
 * Group service implementation for CRUD of groups.
 */
@Service
public class GroupServiceImpl implements GroupService {
  private final GroupRepository groupRepository;
  private final UserService userService;

  private static final String RESOURCE = "Group";

  /**
   * Constructor of group service.
   *
   * @param groupRepository JPA repository to perform CRUD in group table
   * @param userService User service to perform user CRUD
   */
  @Autowired
  public GroupServiceImpl(GroupRepository groupRepository, UserService userService) {
    this.groupRepository = groupRepository;
    this.userService = userService;
  }

  /**
   * Retrieve the group from group table given the group's id if exists,
   * or throw ResourceNotFound exception if not found.
   */
  @Override
  public Group getGroupById(Long gid) {
    var groupWrapper = groupRepository.findById(gid);
    if (groupWrapper.isEmpty()) {
      throw new ResourceNotFoundException(RESOURCE, "id", gid);
    }
    return groupWrapper.get();
  }

  /** Retrieve the group's name, description and member user ids given the group's id. */
  @Override
  public GroupRsp getGroupProfile(User user, Long gid) {
    Group g = getGroupById(gid);
    if (!user.getGroups().contains(g)) {
      throw new UnauthorizedException(user.getId(), RESOURCE, gid);
    }
    GroupRsp r = new GroupRsp();
    r.setGid(g.getId());
    r.setName(g.getName());
    r.setDescription(g.getDescription());
    r.setUids(g.getGroupUsers().stream()
            .map(User::getId)
            .collect(Collectors.toList()));
    return r;
  }

  /** Get a list of ads from the business users in a group given the group's id. */
  @Override
  public GroupAdsRsp getGroupAds(User user, Long gid) {
    Group g = getGroupById(gid);
    if (!user.getGroups().contains(g)) {
      throw new UnauthorizedException(user.getId(), RESOURCE, gid);
    }
    Set<User> users = g.getGroupUsers();
    List<String> ads = new ArrayList<>();
    for (User u : users) {
      if (u.getType().equals(UserType.BUSINESS)) {
        ads.add(u.getBizPromotionText());
      }
    }
    return new GroupAdsRsp(ads);
  }

  /** Create a group of users (maybe of different types) given info in the request payload. */
  @Override
  public ResourceCreatedRsp createGroup(User creator, CreateGroupReq createGroupReq) {
    Set<User> users = new HashSet<>();
    for (Long uid : createGroupReq.getUids()) {
      users.add(userService.getUserById(uid));
    }
    // In case creator's ID not in req uids which doesn't make sense.
    users.add(creator);

    Group group = new Group();
    group.setGroupUsers(users);
    group.setName(createGroupReq.getName());
    group.setDescription(createGroupReq.getDescription());
    group = groupRepository.save(group);
    return new ResourceCreatedRsp(group.getId());
  }

  /**
   * Invite a non-member user to an existing group by a member user,
   * given info in the request payload. Throw InvalidUpdateException
   * if inviter is not a member of the group.
   */
  @Override
  public void inviteToGroup(User inviter, InviteToGroupReq inviteToGroupReq) {
    Group g = getGroupById(inviteToGroupReq.getGid());
    User invitee = userService.getUserById(inviteToGroupReq.getInviteeId());
    if (!isInGroup(g, inviter)) {
      throw new InvalidUpdateException(RESOURCE, g.getId(), "inviterId", inviter.getId());
    }
    joinGroup(g, invitee);
  }

  /**
   * A member user of a group leaves that group,
   * given info in the request payload.
   */
  @Override
  public void leaveGroup(User user, LeaveGroupReq leaveGroupReq) {
    Group g = getGroupById(leaveGroupReq.getGid());
    leaveGroup(g, user);
  }

  /** A user leaves a group. Throw InvalidUpdateException if the user is not a group member. */
  private void leaveGroup(Group group, User user) {
    if (!isInGroup(group, user)) {
      throw new InvalidUpdateException(RESOURCE, group.getId(), "uid", user.getId());
    }
    group.getGroupUsers().remove(user);
    groupRepository.save(group);
  }

  /** Add a user to a group. Throw InvalidUpdateException if the user is already a group member. */
  private void joinGroup(Group group, User user) {
    if (isInGroup(group, user)) {
      throw new InvalidUpdateException(RESOURCE, group.getId(), "inviteeId", user.getId());
    }
    group.getGroupUsers().add(user);
    groupRepository.save(group);
  }

  /** Return whether a user is a member of a group. */
  @Override
  public boolean isInGroup(Group group, User user) {
    return group.getGroupUsers().contains(user);
  }
}
