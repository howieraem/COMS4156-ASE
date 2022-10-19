package com.lgtm.easymoney.services.impl;

import com.lgtm.easymoney.exceptions.InvalidUpdateException;
import com.lgtm.easymoney.exceptions.ResourceNotFoundException;
import com.lgtm.easymoney.models.Group;
import com.lgtm.easymoney.models.User;
import com.lgtm.easymoney.payload.*;
import com.lgtm.easymoney.repositories.GroupRepository;
import com.lgtm.easymoney.services.GroupService;
import com.lgtm.easymoney.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        r.setUids(g.getGroupUsers().stream()
                .map(User::getId)
                .collect(Collectors.toList()));
        return ResponseEntity.status(HttpStatus.OK).body(r);
    }

    @Override
    public ResponseEntity<ResourceCreatedRsp> createAGroup(CreateGroupReq createGroupReq) {
        Set<User> users = new HashSet<>();
        for (Long uid : createGroupReq.getUids()) {
            users.add(userService.getUserByID(uid));
        }
        Group group = new Group();
        group.setGroupUsers(users);
        group.setName(createGroupReq.getName());
        group.setDescription(createGroupReq.getDescription());
        group = groupRepository.save(group);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResourceCreatedRsp(group.getId()));
    }

    @Override
    public ResponseEntity<SimpApiRsp> inviteToAGroup(InviteToGroupReq inviteToGroupReq) {
        Group g = getGroupById(inviteToGroupReq.getGid());
        User inviter = userService.getUserByID(inviteToGroupReq.getInviterId());
        User invitee = userService.getUserByID(inviteToGroupReq.getInviteeId());
        SimpApiRsp r = new SimpApiRsp();
        if (!isInGroup(g, inviter)) {
            throw new InvalidUpdateException("Group", g.getId(), "uids", inviter.getId());
        } else {
            r.setSuccess(joinAGroup(g, invitee));
        }

        // TODO may refactor this
        return ResponseEntity.status(r.getSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(r);
    }

    @Override
    public ResponseEntity<SimpApiRsp> leaveAGroup(LeaveGroupReq leaveGroupReq) {
        Group g = getGroupById(leaveGroupReq.getGid());
        User u = userService.getUserByID(leaveGroupReq.getUid());
        SimpApiRsp r = new SimpApiRsp();
        r.setSuccess(leaveAGroup(g, u));
        // TODO may refactor this
        return ResponseEntity.status(r.getSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(r);
    }

    @Override
    public Boolean joinAGroup(Group group, User user) {
        if (isInGroup(group, user)) {
            throw new InvalidUpdateException("Group", group.getId(), "uids", user.getId());
        }
        group.getGroupUsers().add(user);
        groupRepository.save(group);
        return Boolean.TRUE;
    }

    @Override
    public Boolean leaveAGroup(Group group, User user) {
        if (!isInGroup(group, user)) {
            throw new InvalidUpdateException("Group", group.getId(), "uids", user.getId());
        }
        group.getGroupUsers().remove(user);
        groupRepository.save(group);
        return Boolean.TRUE;
    }

    @Override
    public boolean isInGroup(Group group, User user) {
        return group.getGroupUsers().contains(user);
    }
}
