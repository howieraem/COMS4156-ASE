package com.lgtm.easymoney.services.impl;

import com.lgtm.easymoney.exceptions.ResourceNotFoundException;
import com.lgtm.easymoney.models.Group;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.CreateGroupReq;
import com.lgtm.easymoney.payload.GroupRsp;
import com.lgtm.easymoney.payload.InviteToGroupReq;
import com.lgtm.easymoney.payload.LeaveGroupReq;
import com.lgtm.easymoney.payload.ResourceCreatedRsp;
import com.lgtm.easymoney.payload.SimpApiRsp;
import com.lgtm.easymoney.repositories.GroupRepository;
import com.lgtm.easymoney.services.GroupService;
import com.lgtm.easymoney.services.UserService;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
  public ResponseEntity<GroupRsp> getGroup(Long gid) {
    Group g = getGroupById(gid);
    GroupRsp r = new GroupRsp();
    r.setGid(g.getId());
    r.setName(g.getName());
    r.setDescription(g.getDescription());
    r.setUids(g.getGroupUsers().stream().map(User::getId).collect(Collectors.toList()));
    return ResponseEntity.status(HttpStatus.OK).body(r);
  }

  @Override
  public ResponseEntity<ResourceCreatedRsp> createGroup(CreateGroupReq createGroupReq) {
    Set<User> users = new HashSet<>();
    for (Long uid : createGroupReq.getUids()) {
      users.add(userService.getUserById(uid));
    }
    Group group = new Group();
    group.setGroupUsers(users);
    group.setName(createGroupReq.getName());
    group.setDescription(createGroupReq.getDescription());
    group = groupRepository.save(group);
    return ResponseEntity.status(HttpStatus.CREATED).body(new ResourceCreatedRsp(group.getId()));
  }

  @Override
  public ResponseEntity<SimpApiRsp> inviteToGroup(InviteToGroupReq inviteToGroupReq) {
    Group g = getGroupById(inviteToGroupReq.getGid());
    User inviter = userService.getUserById(inviteToGroupReq.getInviterId());
    User invitee = userService.getUserById(inviteToGroupReq.getInviteeId());
    SimpApiRsp r = new SimpApiRsp();
    if (!isInGroup(g, inviter)) {
      r.setSuccess(Boolean.FALSE);
    } else {
      r.setSuccess(joinGroup(g, invitee));
    }
    // TODO may refactor this
    return ResponseEntity.status(r.getSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(r);
  }

  @Override
  public ResponseEntity<SimpApiRsp> leaveGroup(LeaveGroupReq leaveGroupReq) {
    Group g = getGroupById(leaveGroupReq.getGid());
    User u = userService.getUserById(leaveGroupReq.getUid());
    SimpApiRsp r = new SimpApiRsp();
    r.setSuccess(leaveGroup(g, u));
    // TODO may refactor this
    return ResponseEntity.status(r.getSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(r);
  }

  @Override
  public Boolean leaveGroup(Group group, User user) {
    if (!isInGroup(group, user)) {
      return Boolean.FALSE;
    }
    group.getGroupUsers().remove(user);
    groupRepository.save(group);
    return Boolean.TRUE;
  }

  @Override
  public Boolean joinGroup(Group group, User user) {
    if (isInGroup(group, user)) {
      return Boolean.FALSE;
    }
    group.getGroupUsers().add(user);
    groupRepository.save(group);
    return Boolean.TRUE;
  }



  @Override
  public boolean isInGroup(Group group, User user) {
    return group.getGroupUsers().contains(user);
  }
}
